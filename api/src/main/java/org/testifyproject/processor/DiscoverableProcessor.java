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
package org.testifyproject.processor;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import org.testifyproject.annotation.Discoverable;

/**
 * An annotation processor implementation that generates "META-INF/services"
 * provider-configuration files for classes annotated with {@link Discoverable} annotation.
 */
@SupportedAnnotationTypes("org.testifyproject.annotation.Discoverable")
public class DiscoverableProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        String version = System.getProperty("java.specification.version");

        switch (version) {
            case "1.8":
                return SourceVersion.RELEASE_8;
            default:
                throw new AssertionError("Version '" + version + "' not supported");
        }

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        Map<String, Set<String>> services = new HashMap<>();
        Elements elements = processingEnv.getElementUtils();

        roundEnv.getElementsAnnotatedWith(Discoverable.class)
                .stream()
                .filter(element -> element.getKind().isClass())
                .forEach((element) -> {
                    Discoverable discoverable = element.getAnnotation(Discoverable.class);

                    if (discoverable != null) {
                        TypeElement type = (TypeElement) element;
                        Collection<TypeElement> contracts = getContracts(type, discoverable);

                        if (!(contracts.isEmpty())) {
                            contracts.stream()
                                    .map((contract) -> elements.getBinaryName(contract)
                                            .toString())
                                    .map((contractName) -> services.computeIfAbsent(
                                            contractName, p -> new TreeSet<>()))
                                    .forEach((contractNames) -> {
                                        contractNames.add(elements.getBinaryName(type)
                                                .toString());
                                    });
                        }
                    }
                });

        Filer filer = processingEnv.getFiler();
        services.entrySet().stream().forEach((entry) -> {
            try {
                String contract = entry.getKey();
                String fileName = "META-INF/services/" + contract;
                FileObject fileObject = filer.getResource(CLASS_OUTPUT, "", fileName);
                try (BufferedReader reader = new BufferedReader(fileObject.openReader(false))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        entry.getValue().add(line);
                    }
                }
            } catch (FileNotFoundException | NoSuchFileException e) {
            } catch (IOException e) {
                error(e);
            }
        });

        services.entrySet().stream().forEach((entry) -> {
            try {
                String contract = entry.getKey();
                String fileName = "META-INF/services/" + contract;
                FileObject fileObject = filer.createResource(CLASS_OUTPUT, "", fileName);

                note("Writing %s to %s", fileName, fileObject.toUri());

                try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                    entry.getValue().stream().forEach((value) -> {
                        writer.println(value);
                    });

                    writer.flush();
                }

            } catch (IOException e) {
                error(e);
            }
        });

        return false;
    }

    Collection<TypeElement> getContracts(TypeElement typeElement,
            Discoverable discoverable) {
        List<TypeElement> typeElementList = new ArrayList<>();

        try {
            discoverable.value();
            throw new AssertionError();
        } catch (MirroredTypesException e) {
            e.getTypeMirrors().stream().forEach((typeMirror) -> {
                if (typeMirror.getKind() == TypeKind.VOID) {
                    boolean hasBaseClass = !isObject(typeElement.getSuperclass())
                            && typeElement.getSuperclass().getKind() != TypeKind.NONE;
                    boolean hasInterfaces = !typeElement.getInterfaces().isEmpty();

                    if (hasBaseClass) {
                        typeElementList.add((TypeElement) ((DeclaredType) typeElement
                                .getSuperclass()).asElement());
                    } else if (hasInterfaces) {
                        typeElement.getInterfaces()
                                .parallelStream()
                                .map(p -> (TypeElement) ((DeclaredType) p).asElement())
                                .forEach(typeElementList::add);
                    } else {
                        error(typeElement, "Contract type could not be inferred.");
                    }
                } else if (typeMirror instanceof DeclaredType) {
                    DeclaredType declaredType = (DeclaredType) typeMirror;
                    typeElementList.add((TypeElement) declaredType.asElement());
                } else {
                    error(typeElement, "Invalid type specified as the contract");
                }
            });
        }

        return typeElementList;
    }

    boolean isObject(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) typeMirror;

            return ((TypeElement) declaredType.asElement())
                    .getQualifiedName()
                    .toString()
                    .equals("java.lang.Object");
        }

        return false;
    }

    Messager getMessanger() {
        return processingEnv.getMessager();
    }

    void note(String message, Object... args) {
        getMessanger().printMessage(Kind.NOTE, String.format(message, args));
    }

    void warning(String message, Object... args) {
        getMessanger().printMessage(Kind.WARNING, String.format(message, args));
    }

    void error(Element source, String message, Object... args) {
        getMessanger().printMessage(Kind.ERROR, String.format(message, args), source);
    }

    void error(Throwable e) {
        getMessanger().printMessage(Kind.ERROR, e.getMessage());
        StringWriter buffer = new StringWriter();
        try (PrintWriter writer = new PrintWriter(buffer)) {
            e.printStackTrace(writer);
        }

        getMessanger().printMessage(Kind.ERROR, buffer.toString());
    }

}
