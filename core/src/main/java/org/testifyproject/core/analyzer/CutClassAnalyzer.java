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
import java.lang.reflect.Modifier;
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
import static org.testifyproject.guava.common.base.Preconditions.checkState;

/**
 * A class visitor implementation that performs analysis on the class under
 * test.
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
                int modifiers = field.getModifiers();

                //remove final modifier from the field
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modifiers & ~Modifier.FINAL);

                FieldDescriptor fieldDescriptor = DefaultFieldDescriptor.of(field);
                Type fieldType = fieldDescriptor.getGenericType();
                String fieldName = fieldDescriptor.getName();
                DescriptorKey typeKey = DescriptorKey.of(fieldType);
                DescriptorKey typeAndNameKey = DescriptorKey.of(fieldType, fieldName);

                cutDescriptor.addMapEntry(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, typeKey, fieldDescriptor);
                cutDescriptor.addMapEntry(CutDescriptorProperties.FIELD_DESCRIPTORS_CACHE, typeAndNameKey, fieldDescriptor);
                cutDescriptor.addListElement(CutDescriptorProperties.FIELD_DESCRIPTORS, fieldDescriptor);
            } catch (SecurityException
                    | NoSuchFieldException
                    | IllegalAccessException
                    | IllegalArgumentException e) {
                checkState(false,
                        "Could not alter modifiers of field '%s' in class under test '%s'.\n%s",
                        name, cutField.getDeclaringClass().getSimpleName(), e.getMessage());
            }

            return null;
        });
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return AccessController.doPrivileged((PrivilegedAction<MethodVisitor>) () -> {
            if (CONSTRUCTOR_NAME.equals(name)) {

                org.testifyproject.asm.Type type = getMethodType(desc);
                Class[] parameterTypes = of(type.getArgumentTypes())
                        .sequential()
                        .map(this::getClass)
                        .toArray(Class[]::new);

                try {
                    Constructor<?> constructor = cutField
                            .getType()
                            .getDeclaredConstructor(parameterTypes);

                    cutDescriptor.addProperty(CutDescriptorProperties.CONSTRUCTOR, constructor);

                    Parameter[] parameters = constructor.getParameters();

                    for (int index = 0; index < parameters.length; index++) {
                        Parameter parameter = parameters[index];
                        ParameterDescriptor paramterDescriptor = DefaultParameterDescriptor.of(parameter, index);

                        Type paramterType = paramterDescriptor.getGenericType();
                        String paramterName = paramterDescriptor.getName();

                        DescriptorKey typeKey = DescriptorKey.of(paramterType);
                        DescriptorKey typeAndNameKey = DescriptorKey.of(paramterType, paramterName);

                        cutDescriptor.addMapEntry(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, typeKey, paramterDescriptor);
                        cutDescriptor.addMapEntry(CutDescriptorProperties.PARAMETER_DESCRIPTORS_CACHE, typeAndNameKey, paramterDescriptor);
                        cutDescriptor.addListElement(CutDescriptorProperties.PARAMETER_DESCRIPTORS, paramterDescriptor);
                    }

                } catch (NoSuchMethodException | SecurityException e) {
                    checkState(false,
                            "Constructor with '%s' parameters not accessible in '%s' class.",
                            Arrays.toString(parameterTypes), cutField.getDeclaringClass().getTypeName());
                }

            }

            return null;
        });
    }

    private Class<?> getClass(org.testifyproject.asm.Type type) {
        try {
            return forName(type.getInternalName().replace('/', '.'));
        } catch (ClassNotFoundException e) {
            checkState(false, "Class '%s' not found in the classpath.", type.getClassName());
            //not reachable;
            throw new IllegalStateException(e);
        }
    }

}
