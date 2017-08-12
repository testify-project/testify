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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static java.security.AccessController.doPrivileged;
import java.security.PrivilegedAction;
import java.util.List;
import static java.util.stream.Stream.of;
import org.testifyproject.FieldDescriptor;
import org.testifyproject.MethodDescriptor;
import org.testifyproject.TestDescriptor;
import org.testifyproject.annotation.Bundle;
import org.testifyproject.annotation.CollaboratorProvider;
import org.testifyproject.annotation.ConfigHandler;
import org.testifyproject.annotation.Sut;
import org.testifyproject.asm.AnnotationVisitor;
import org.testifyproject.asm.ClassVisitor;
import org.testifyproject.asm.FieldVisitor;
import org.testifyproject.asm.MethodVisitor;
import static org.testifyproject.asm.Opcodes.ASM5;
import org.testifyproject.asm.Type;
import static org.testifyproject.asm.Type.getMethodType;
import static org.testifyproject.asm.Type.getType;
import org.testifyproject.core.util.ExceptionUtil;
import org.testifyproject.core.util.ReflectionUtil;
import org.testifyproject.core.util.ServiceLocatorUtil;
import org.testifyproject.extension.AnnotationInspector;
import org.testifyproject.extension.annotation.Handles;

/**
 * A class visitor implementation that performs analysis on the test class.
 *
 * @author saden
 */
public class TestClassAnalyzer extends ClassVisitor {

    public static final String CONSTRUCTOR_NAME = "<init>";
    public static final String STATIC_NAME = "<cinit>";

    private int sutCount = 0;
    private final Class<?> testClass;
    private final TestDescriptor testDescriptor;

    public TestClassAnalyzer(Class<?> testClass, TestDescriptor testDescriptor) {
        super(ASM5);
        this.testClass = testClass;
        this.testDescriptor = testDescriptor;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Type type = getType(desc);
        Class annotationClass = getClass(type.getClassName());
        List<AnnotationInspector> inspectors = ServiceLocatorUtil.INSTANCE.getAll(AnnotationInspector.class);

        return doPrivileged((PrivilegedAction<AnnotationVisitor>) () -> {
            //if the annotation class is annotated with Bundle meta annotation
            //then inspect anotations on the annotation class
            inspectors.forEach(inspector -> {
                Handles handles = inspector.getClass().getDeclaredAnnotation(Handles.class);
                if (handles != null) {
                    Class<? extends Annotation>[] typesHandled = handles.value();

                    for (Class<? extends Annotation> typeHandled : typesHandled) {

                        if (typeHandled.isAssignableFrom(annotationClass)) {
                            inspector.inspect(testDescriptor, testClass, testClass.getDeclaredAnnotation(annotationClass));
                        } else if (typeHandled.equals(Bundle.class) && annotationClass.isAnnotationPresent(Bundle.class)) {
                            inspector.inspect(testDescriptor, annotationClass, annotationClass.getDeclaredAnnotation(Bundle.class));
                        }
                    }
                }
            });

            return null;
        });
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return doPrivileged((PrivilegedAction<FieldVisitor>) () -> {
            try {
                Field field = testClass.getDeclaredField(name);

                //make the field accessible
                field.setAccessible(true);
                ReflectionUtil.INSTANCE.removeFinalModifier(field);

                Sut sut = field.getDeclaredAnnotation(Sut.class);
                if (sut == null) {
                    saveField(field);
                } else {
                    testDescriptor.addProperty(TestDescriptorProperties.SUT_FIELD, field);
                    sutCount++;
                }
            } catch (NoSuchFieldException | SecurityException e) {
                throw ExceptionUtil.INSTANCE.propagate(
                        "Could not get  field '{}' in test class '{}'.",
                        e, name, testClass.getName());
            }

            return null;
        });
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (CONSTRUCTOR_NAME.equals(name) || STATIC_NAME.equals(name)) {
            return null;
        }
        return doPrivileged((PrivilegedAction<MethodVisitor>) () -> {
            Type type = getMethodType(desc);
            Class[] parameterTypes = of(type.getArgumentTypes())
                    .sequential()
                    .map(Type::getClassName)
                    .map(this::getClass)
                    .toArray(Class[]::new);

            saveMethod(name, parameterTypes);

            return null;
        });
    }

    @Override
    public void visitEnd() {
        ExceptionUtil.INSTANCE.raise(sutCount > 1,
                "Found {} fields annotated with @Sut in test class {}. Please insure "
                + "that the test class has only one field annotated with @Sut.",
                sutCount, testClass.getName());
    }

    void saveField(Field field) {
        FieldDescriptor fieldDescriptor = DefaultFieldDescriptor.of(field);
        java.lang.reflect.Type fieldType = fieldDescriptor.getGenericType();
        String fieldName = fieldDescriptor.getName();
        DescriptorKey typeKey = DescriptorKey.of(fieldType);
        DescriptorKey typeAndNameKey = DescriptorKey.of(fieldType, fieldName);

        testDescriptor.addMapEntry(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE, typeKey, fieldDescriptor);
        testDescriptor.addMapEntry(TestDescriptorProperties.FIELD_DESCRIPTORS_CACHE, typeAndNameKey, fieldDescriptor);
        testDescriptor.addListElement(TestDescriptorProperties.FIELD_DESCRIPTORS, fieldDescriptor);
    }

    void saveMethod(String name, Class[] parameterTypes) {
        try {
            Method method = testClass.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);

            if (method.getDeclaredAnnotation(ConfigHandler.class) != null) {
                MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method);
                testDescriptor.addListElement(TestDescriptorProperties.CONFIG_HANDLERS, methodDescriptor);
                testDescriptor.addProperty(TestDescriptorProperties.CONFIG_HANDLER,
                        method.getDeclaredAnnotation(ConfigHandler.class));
            } else if (method.getDeclaredAnnotation(CollaboratorProvider.class) != null) {
                MethodDescriptor methodDescriptor = DefaultMethodDescriptor.of(method);
                testDescriptor.addListElement(TestDescriptorProperties.COLLABORATOR_PROVIDERS, methodDescriptor);
                testDescriptor.addProperty(TestDescriptorProperties.COLLABORATOR_PROVIDER,
                        method.getDeclaredAnnotation(CollaboratorProvider.class));
            }
        } catch (NoSuchMethodException | SecurityException e) {
            throw ExceptionUtil.INSTANCE.propagate(
                    "Could not analyze '{}' method in test class '{}'.",
                    e, name, testClass.getSimpleName());
        }
    }

    Class<?> getClass(String className) {
        try {
            return forName(className);
        } catch (ClassNotFoundException e) {
            throw ExceptionUtil.INSTANCE.propagate("Class '{}' not found in the classpath.", e, className);
        }
    }

}
