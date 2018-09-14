package name.lech.pojomapper;

import org.junit.Test;

import java.io.IOException;

public class PojoMapperTest {

    @Test
    public void testClassAToClassB() throws IOException {
        new PojoMapper<TestClassA, TestClassB>()
                .generateForClasses(TestClassA.class, TestClassB.class, "name.lech.pojomapper3");
    }
}
