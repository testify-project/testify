/*
 * Copyright 2016-2017 Sharmarke Aden.
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
package org.testify.core.analyzer;

import static java.lang.Class.forName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static java.security.AccessController.doPrivileged;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Stream.of;
import org.testify.FieldDescriptor;
import org.testify.annotation.CollaboratorProvider;
import org.testify.annotation.ConfigHandler;
import org.testify.annotation.Cut;
import org.testify.annotation.TestGroup;
import org.testify.asm.AnnotationVisitor;
import org.testify.asm.ClassVisitor;
import org.testify.asm.FieldVisitor;
import org.testify.asm.MethodVisitor;
import static org.testify.asm.Opcodes.ASM5;
import org.testify.asm.Type;
import static org.testify.asm.Type.getMethodType;
import static org.testify.asm.Type.getType;
import org.testify.core.util.ServiceLocatorUtil;
import static org.testify.guava.common.base.Preconditions.checkState;

/**
 * A class visitor implementation that performs analysis on the test class.
 *
 * @author saden
 */
public class TestClassAnalyzer extends ClassVisitor {

    public static final String CONSTRUCTOR_NAME = "<init>";
    public static final String STATIC_NAME = "<cinit>";

    private int cutCount = 0;
    private final Class<?> testClass;
    private final TestDescriptorBuilder testDescriptorBuilder;

    public TestClassAnalyzer(Class<?> testClass, TestDescriptorBuilder testDescriptorBuilder) {
        super(ASM5);
        this.testClass = testClass;
        this.testDescriptorBuilder = testDescriptorBuilder;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return doPrivileged((PrivilegedAction<AnnotationVisitor>) () -> {
            try {
                Type type = getType(desc);
                Class annotationClass = getClass(type.getClassName());
                List<TestAnnotationInspector> inspectors = ServiceLocatorUtil.INSTANCE.getAll(TestAnnotationInspector.class);

                //if the annotation class is annotated with TestGroup meta annotation
                //then inspect anotations on the annotation class
                if (annotationClass.isAnnotationPresent(TestGroup.class)) {
                    Annotation[] annotations = annotationClass.getDeclaredAnnotations();

                    for (Annotation annotation : annotations) {
                        for (TestAnnotationInspector inspector : inspectors) {
                            if (inspector.handles(annotation.annotationType())) {
                                inspector.inspect(testDescriptorBuilder, annotationClass, annotation);
                            }

                        }
                    }
                } else {
                    for (TestAnnotationInspector inspector : inspectors) {
                        if (inspector.handles(annotationClass)) {
                            Annotation annotation = testClass.getDeclaredAnnotation(annotationClass);
                            inspector.inspect(testDescriptorBuilder, testClass, annotation);
                        }
                    }
                }
            } catch (Exception e) {
                checkState(false,
                        "Could not analyze annotations in test class '%s'.\n%s",
                        testClass.getName(), e.getMessage());
            }

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

                //remove final modifier from the field
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                Cut cut = field.getDeclaredAnnotation(Cut.class);
                if (cut != null) {
                    testDescriptorBuilder.cutField(field);
                    cutCount++;
                } else {
                    FieldDescriptor fieldDescriptor = new DefaultFieldDescriptor(field);
                    testDescriptorBuilder.addFieldDescriptor(fieldDescriptor);
                }

            } catch (SecurityException
                    | NoSuchFieldException
                    | IllegalAccessException
                    | IllegalArgumentException e) {
                checkState(false,
                        "Could not alter modifiers of field '%s' in test class '%s'.\n%s",
                        name, testClass.getName(), e.getMessage());
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
            try {

                Method method = testClass.getDeclaredMethod(name, parameterTypes);
                method.setAccessible(true);

                if (method.getDeclaredAnnotation(ConfigHandler.class) != null) {
                    DefaultMethodDescriptor methodDescriptor = new DefaultMethodDescriptor(method);
                    DefaultInvokableDescriptor invokableDescriptor = new DefaultInvokableDescriptor(methodDescriptor);

                    testDescriptorBuilder.addConfigHandler(invokableDescriptor);
                } else if (method.getDeclaredAnnotation(CollaboratorProvider.class) != null) {
                    DefaultMethodDescriptor methodDescriptor = new DefaultMethodDescriptor(method);
                    DefaultInvokableDescriptor invokableDescriptor = new DefaultInvokableDescriptor(methodDescriptor);

                    testDescriptorBuilder.collaboratorMethod(invokableDescriptor);
                }

            } catch (NoSuchMethodException | SecurityException e) {
                checkState(false,
                        "Method with '%s' parameters not accessible in '%s' class.",
                        Arrays.toString(parameterTypes), testClass.getName());
            }

            return null;
        });
    }

    @Override
    public void visitEnd() {
        checkState(cutCount <= 1,
                "Found more than one class under test in test class %s. Please insure "
                + "that the test class has only one field annotated with @Cut.",
                testClass.getName());
    }

    private Class<?> getClass(String className) {
        try {
            return forName(className);
        } catch (ClassNotFoundException e) {
            checkState(false, "Class '%s' not found in the classpath.", className);
            //not reachable;
            throw new IllegalStateException(e);
        }
    }

}
