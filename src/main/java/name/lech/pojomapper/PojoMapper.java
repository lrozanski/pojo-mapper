package name.lech.pojomapper;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class PojoMapper<A, B> {

    public void generateForClasses(Class<A> fromClass, Class<B> toClass, String packageName) throws IOException {
        File outputDir = new File(System.getProperty("user.home") + "/IdeaProjects/pojo-mapper/generated-src/main/java");
        Files.createDirectories(Paths.get(outputDir.getAbsolutePath(), packageName.replace(".", "/")));

        String className = fromClass.getSimpleName() + "To" + toClass.getSimpleName() + "Mapper";

        JavaClassSource javaClassSource = Roaster.create(JavaClassSource.class);
        javaClassSource
                .setPublic()
                .setName(className)
                .setPackage(packageName)
                .setSuperType("Mapper<" + fromClass.getSimpleName() + ", " + toClass.getSimpleName() + ">");

        javaClassSource.addImport(List.class);
        javaClassSource.addImport(BiConsumer.class);

        if (javaClassSource.requiresImport(Mapper.class)) {
            javaClassSource.addImport(Mapper.class);
        }
        if (javaClassSource.requiresImport(fromClass)) {
            javaClassSource.addImport(fromClass);
        }
        if (javaClassSource.requiresImport(toClass)) {
            javaClassSource.addImport(toClass);
        }
        javaClassSource.addImport(Arrays.class);

        String returnType = "List<BiConsumer<" + fromClass.getSimpleName() + ", " + toClass.getSimpleName() + ">>";

        List<MappingPair> mappingPairs = createMappingPairs(fromClass, toClass);
        if (mappingPairs.stream().anyMatch(pair -> pair.getGetter().isAnnotationPresent(MapToString.class))) {
            if (javaClassSource.requiresImport(Mappers.class)) {
                javaClassSource.addImport(Mappers.class);
            }
            if (javaClassSource.requiresImport(MapToString.class)) {
                javaClassSource.addImport(MapToString.class);
            }
        }

        String fieldMappers = mappingPairs.stream()
                .map(pair -> "map("
                        + createSetterString(toClass, pair)
                        + ", "
                        + createGetterString(fromClass, pair)
                        + ")"
                )
                .collect(Collectors.joining(", "));

        javaClassSource
                .addMethod(returnType + " getFieldConsumers() { return Arrays.asList(" + fieldMappers + "); }")
                .setPublic()
                .addAnnotation(Override.class);

        String formattedClass = Roaster.format(javaClassSource.toUnformattedString());

        Path outputPath = Paths.get(outputDir.getPath(), packageName.replace(".", "/"), className + ".java");

        Files.write(
                outputPath,
                formattedClass.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private String createGetterString(Class<A> fromClass, MappingPair pair) {
        if (pair.getGetter().isAnnotationPresent(MapToString.class)) {
            return "Mappers.mapToString("
                    + fromClass.getSimpleName()
                    + "::"
                    + pair.getGetter().getName()
                    + ")";
        }
        return fromClass.getSimpleName()
                + "::"
                + pair.getGetter().getName();
    }

    private String createSetterString(Class<B> toClass, MappingPair pair) {
        return toClass.getSimpleName()
                + "::"
                + pair.getSetter().getName();
    }

    private List<MappingPair> createMappingPairs(Class<A> fromClass, Class<B> toClass) {
        List<Method> getterMethods = Arrays.stream(fromClass.getDeclaredMethods())
                .filter(this::isGetter)
                .collect(Collectors.toList());
        List<Method> setterMethods = Arrays.stream(toClass.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("set"))
                .collect(Collectors.toList());

        return setterMethods
                .stream()
                .map(setter -> toMappingPair(setter, getterMethods))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<MappingPair> toMappingPair(Method setter, Collection<Method> getters) {
        if (setter.getParameterCount() != 1
                || !setter.getReturnType().equals(Void.TYPE)) {
            return Optional.empty();
        }
        Set<String> expectedGetterNames = new HashSet<>();
        expectedGetterNames.add(setter.getName().replaceFirst("set", "get"));
        expectedGetterNames.add(setter.getName().replaceFirst("set", "is"));
        expectedGetterNames.add(setter.getName().replaceFirst("set", "has"));

        Class<?> expectedReturnType = setter.getParameterTypes()[0];

        return getters
                .stream()
                .filter(getter -> isValidGetter(getter, expectedGetterNames, expectedReturnType))
                .findFirst()
                .map(getter -> new MappingPair(getter, setter));
    }

    private boolean isValidGetter(Method getter, Collection<String> expectedGetterNames, Class<?> expectedReturnType) {
        return expectedGetterNames.contains(getter.getName())
                && getter.getParameterCount() == 0
                && hasValidReturnType(getter, expectedReturnType);
    }

    private boolean hasValidReturnType(Method getter, Class<?> expectedReturnType) {
        if (getter.isAnnotationPresent(MapToString.class)) {
            return expectedReturnType.equals(String.class);
        }
        return getter.getReturnType().equals(expectedReturnType);
    }

    private boolean isGetter(Method getter) {
        if (getter.getReturnType().equals(boolean.class) || getter.getReturnType().equals(Boolean.class)) {
            return getter.getName().startsWith("get")
                    || getter.getName().startsWith("is")
                    || getter.getName().startsWith("has");
        }
        return getter.getName().startsWith("get");
    }
}
