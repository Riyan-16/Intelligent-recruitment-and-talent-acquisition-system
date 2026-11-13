import java.util.List;

public class Experienced extends Candidate {
    private final int years;

    public Experienced(String name, String email, List<String> skills, int years) {
        super(name, email, skills);
        this.years = Math.max(0, years);
    }

    @Override
    public int getExperienceYears() { return years; }

    @Override
    public String describe() { return years + " yr experience"; }
}
