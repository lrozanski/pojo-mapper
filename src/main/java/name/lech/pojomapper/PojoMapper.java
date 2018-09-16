package name.lech.pojomapper;

import name.lech.pojomapper.base.*;
import name.lech.pojomapper.generation.MappingPair;
import name.lech.pojomapper.generation.MappingPairCreator;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Not thread safe.
 *
 * @param <A>
 * @param <B>
 */
@SuppressWarnings("WeakerAccess")
public class PojoMapper<A, B> {

    private MappingPairCreator mappingPairCreator = MappingPairCreator.instance();

    private final Set<Class> usedClasses = new HashSet<>();

    public void generateForClasses(Class<A> fromClass, Class<B> toClass, String packageName) throws IOException {
        resetUsedClasses(fromClass, toClass);

        File outputDir = new File(System.getProperty("user.home") + "/IdeaProjects/pojo-mapper/generated-src/main/java");
        Files.createDirectories(Paths.get(outputDir.getAbsolutePath(), packageName.replace(".", "/")));

        String className = fromClass.getSimpleName() + "To" + toClass.getSimpleName() + "Mapper";

        JavaClassSource javaClassSource = Roaster.create(JavaClassSource.class);
        javaClassSource
                .setPublic()
                .setName(className)
                .setPackage(packageName)
                .setSuperType("Mapper<" + fromClass.getSimpleName() + ", " + toClass.getSimpleName() + ">");

        String returnType = "List<BiConsumer<" + fromClass.getSimpleName() + ", " + toClass.getSimpleName() + ">>";

        List<MappingPair> mappingPairs = mappingPairCreator.createMappingPairs(fromClass, toClass);

        String fieldMappers = mappingPairs.stream()
                .map(pair -> "map("
                        + createSetterString(toClass, pair)
                        + ", "
                        + createGetterString(fromClass, pair)
                        + ")"
                )
                .collect(Collectors.joining(", "));

        // Add required imports
        usedClasses
                .stream()
                .filter(javaClassSource::requiresImport)
                .forEach(javaClassSource::addImport);

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

        usedClasses.clear();
    }

    private void resetUsedClasses(Class<A> fromClass, Class<B> toClass) {
        usedClasses.clear();

        usedClasses.add(List.class);
        usedClasses.add(BiConsumer.class);
        usedClasses.add(Mapper.class);
        usedClasses.add(Arrays.class);
        usedClasses.add(fromClass);
        usedClasses.add(toClass);
    }

    private String createGetterString(Class<A> fromClass, MappingPair pair) {
        if (pair.getGetter().isAnnotationPresent(MapToString.class)) {
            usedClasses.add(Mappers.class);
            usedClasses.add(MapToString.class);

            return "Mappers.mapToString("
                    + fromClass.getSimpleName()
                    + "::"
                    + pair.getGetter().getName()
                    + ")";
        }
        if (pair.getGetter().isAnnotationPresent(MapToLong.class)) {
            usedClasses.add(Mappers.class);
            usedClasses.add(MapToLong.class);

            return "Mappers.mapToLong("
                    + fromClass.getSimpleName()
                    + "::"
                    + pair.getGetter().getName()
                    + ")";
        }
        if (pair.getGetter().isAnnotationPresent(MapToBoolean.class)) {
            usedClasses.add(Mappers.class);
            usedClasses.add(MapToBoolean.class);

            return "Mappers.mapToBoolean("
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
}
