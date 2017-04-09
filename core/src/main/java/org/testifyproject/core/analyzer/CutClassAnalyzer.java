/*
 * Copyright 2016-2017 Testify Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.testifyproject.core.analyzer;

import static java.lang.Class.forName;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.security.AccessController;
import static java.security.AccessController.doPrivileged;
import java.security.PrivilegedAction;
import java.util.Arrays;
import static java.util.stream.Stream.of;
import org.testifyproject.CutDescriptor;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.ParameterDescriptor;
import org.testifyproject.asm.ClassVisitor;
import org.testifyproject.asm.FieldVisitor;
import org.testifyproject.asm.MethodVisitor;
import static org.testifyproject.asm.Opcodes.ASM5;
import static org.testifyproject.asm.Type.getMethodType;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;

/**
 * A class visitor implementation that performs analysis on the class under test.
 *
 * @author saden
 */
public class CutClassAnalyzer extends ClassVisitor {

    public static final String CONSTRUCTOR_NAME = "<init>";
    private final Field cutField;
    private final CutDescriptor cutDescriptor;

    public CutClassAnalyzer(Field cutField, CutDescriptor cutDescriptor) {
        super(ASM5);
        this.cutField = cutField;
        this.cutDescriptor = cutDescriptor;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return doPrivileged((PrivilegedAction<FieldVisitor>) () -> {
            try {
                Class<?> cutType = cutField.getType();
                Field field = cutType.getDeclaredField(name);
                field.setAccessible(true);

                ReflectionUtil.INSTANCE.removeFinalModifier(field);
                saveField(field);

                return null;
            } catch (NoSuchFieldException | SecurityException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could not get  field '{}' in class under test '{}'.",
                        e, name, cutField.getDeclaringClass().getSimpleName());
            }
        });
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return AccessController.doPrivileged((PrivilegedAction<MethodVisitor>) () -> {
            if (CONSTRUCTOR_NAME.equals(name)) {
                org.testifyproject.asm.Type type = getMethodType(desc);
                Class[] parameterTypes = of(type.getArgumentTypes()).map(this::getClass).toArray(Class[]::new);

                try {
                    Constructor<?> constructor = cutField.getType().getDeclaredConstructor(parameterTypes);
                    cutDescriptor.addProperty(CutDescriptorProperties.CONSTRUCTOR, constructor);

                    Parameter[] parameters = constructor.getParameters();
                    for (int index = 0; index < parameters.length; index++) {
                        saveParamter(parameters[index], index);
                    }
                } catch (NoSuchMethodException | SecurityException e) {
                    throw ExceptionUtil.INSTANCE.propagate(
                            "Constructor with '{}' parameters not accessible in '{}' class.",
                            e, Arrays.toString(parameterTypes), cutField.getDeclaringClass().getTypeName());
                }
            }

            return null;
        });
    }

    void saveField(Field field) {
        FieldDescriptor fieldDescriptor = DefaultFieldDescriptor.of(field);
        Type fieldType = fieldDescriptor.getGenericType();
        String fieldName = fieldDescriptor.getName();

        DescriptorKey typeKey = DescriptorKey.of(fieldType);
        DescriptorKey typeAndNameKey = DescriptorKey.of(fieldType, fieldName);

        cutDescriptor.addMapEntry(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, typeKey, fieldDescriptor);
        cutDescriptor.addMapEntry(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, typeAndNameKey, fieldDescriptor);
        cutDescriptor.addListElement(CutDescriptorProperties.FIELD_DESCRIPTORS, fieldDescriptor);
    }

    void saveParamter(Parameter parameter, int index) {
        ParameterDescriptor paramterDescriptor = DefaultParameterDescriptor.of(parameter, index);

        Type paramterType = paramterDescriptor.getGenericType();
        String paramterName = paramterDescriptor.getName();

        DescriptorKey typeKey = DescriptorKey.of(paramterType);
        DescriptorKey typeAndNameKey = DescriptorKey.of(paramterType, paramterName);

        cutDescriptor.addMapEntry(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, typeKey, paramterDescriptor);
        cutDescriptor.addMapEntry(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, typeAndNameKey, paramterDescriptor);
        cutDescriptor.addListElement(CutDescriptorProperties.PARAMETER_DESCRIPTORS, paramterDescriptor);
    }

    Class<?> getClass(org.testifyproject.asm.Type type) {
        try {
            return forName(type.getInternalName().replace('/', '.'));
        } catch (ClassNotFoundException e) {
            throw ExceptionUtil.INSTANCE.propagate("Class '{}' not found in the classpath.", e, type.getClassName());
        }
    }

}
