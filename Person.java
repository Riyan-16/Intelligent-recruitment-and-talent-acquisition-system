public abstract class Person {
    private final String name;
    private final String email;

    protected Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName()  { return name; }
    public String getEmail() { return email; }

    // every kind of person describes itself differently
    public abstract String describe();
}
