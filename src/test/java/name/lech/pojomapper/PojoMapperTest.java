package name.lech.pojomapper;

import name.lech.pojomapper.generation.TestClassA;
import name.lech.pojomapper.generation.TestClassB;
import org.junit.Test;

import java.io.IOException;

public class PojoMapperTest {

    @Test
    public void testClassAToClassB() throws IOException {
        new PojoMapper<TestClassA, TestClassB>()
                .generateForClasses(TestClassA.class, TestClassB.class, "name.lech.pojomapper.test");
    }

    @Test
    public void testClassBToClassA() throws IOException {
        new PojoMapper<TestClassB, TestClassA>()
                .generateForClasses(TestClassB.class, TestClassA.class, "name.lech.pojomapper.test");
    }
}
