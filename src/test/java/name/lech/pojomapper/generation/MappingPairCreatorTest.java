package name.lech.pojomapper.generation;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MappingPairCreatorTest {

    @Mock
    private GetterValidator getterValidator;

    @Mock
    private MethodMatcher methodMatcher;

    @InjectMocks
    private MappingPairCreator testee;

    @Test
    public void instance() {
        // GIVEN

        // WHEN
        MappingPairCreator instance1 = MappingPairCreator.instance();
        MappingPairCreator instance2 = MappingPairCreator.instance();

        // THEN
        assertThat(instance1).isNotNull();
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    public void createMappingPairs() throws NoSuchMethodException {
        // GIVEN
        Method getter = TestClassA.class.getDeclaredMethod("getName");
        Method setter = TestClassB.class.getDeclaredMethod("setId", Long.class);
        MappingPair mappingPair = new MappingPair(getter, setter);

        // WHEN
        when(getterValidator.isGetter(any(Method.class))).thenReturn(true, false);
        when(methodMatcher.match(any(Method.class), anyCollection())).thenReturn(Optional.of(mappingPair));

        List<MappingPair> mappingPairs = testee.createMappingPairs(TestClassA.class, TestClassB.class);

        // THEN
        assertThat(mappingPairs).hasSize(1);
        assertThat(mappingPairs)
                .extracting(
                        pair -> pair.getGetter().getName(),
                        pair -> pair.getSetter().getName()
                )
                .containsExactly(Tuple.tuple(
                        getter.getName(),
                        setter.getName()
                ));
    }

    @Test
    public void createMappingPairs_withNoMatchingGetter() {
        // WHEN
        when(getterValidator.isGetter(any(Method.class))).thenReturn(true, false);
        when(methodMatcher.match(any(Method.class), anyCollection())).thenReturn(Optional.empty());

        List<MappingPair> mappingPairs = testee.createMappingPairs(TestClassA.class, TestClassB.class);

        // THEN
        assertThat(mappingPairs).isEmpty();
    }
}