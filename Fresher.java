import java.util.List;

public class Fresher extends Candidate {
    private final String degree;

    public Fresher(String name, String email, List<String> skills, String degree) {
        super(name, email, skills);
        this.degree = (degree == null || degree.isBlank()) ? "Not stated" : degree;
    }

    @Override
    public String describe() { return "Fresher, " + degree; }
}
