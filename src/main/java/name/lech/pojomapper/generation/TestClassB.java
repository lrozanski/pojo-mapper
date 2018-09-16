package name.lech.pojomapper.generation;

import name.lech.pojomapper.base.MapToBoolean;

public class TestClassB {

    private Long id;
    private String name;
    private String active;

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

    @MapToBoolean(targetClasses = TestClassA.class)
    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
