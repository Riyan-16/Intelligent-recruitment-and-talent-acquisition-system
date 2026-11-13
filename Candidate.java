import java.util.*;

public abstract class Candidate extends Person {
    private static int counter = 0;

    private final int id;
    private final List<String> skills;
    private Stage stage = Stage.APPLIED;

    protected Candidate(String name, String email, List<String> skills) {
        super(name, email);
        this.id = ++counter;
        this.skills = new ArrayList<>(skills);
    }

    public int getId() { return id; }
    public Stage getStage() { return stage; }
    public List<String> getSkills() { return Collections.unmodifiableList(skills); }

    public void moveTo(Stage newStage) {
        if (newStage == null) throw new IllegalArgumentException("Stage required");
        this.stage = newStage;
    }

    // default; Experienced overrides it
    public int getExperienceYears() { return 0; }
}
