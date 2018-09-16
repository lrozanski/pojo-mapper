package name.lech.pojomapper.generation;

import name.lech.pojomapper.base.CreateMappersForClasses;
import name.lech.pojomapper.base.MapToString;

@CreateMappersForClasses(targetClasses = {
        TestClassA.class,
        TestClassB.class
})
public class TestClassA {

    private Long id;
    private String name;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @MapToString(targetClasses = TestClassB.class)
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
