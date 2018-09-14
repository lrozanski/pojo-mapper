package name.lech.pojomapper;

public class TestClassA {

    private Long id;
    private String name;
    private boolean active;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @MapToString
    public boolean isActive() {
        return active;
    }
}
