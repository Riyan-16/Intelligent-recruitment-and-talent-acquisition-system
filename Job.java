import java.util.*;

public class Job {
    private static int counter = 0;

    private final int id;
    private final String title;
    private final List<String> skills;
    private final int minExp;

    public Job(String title, List<String> skills, int minExp) {
        this.id = ++counter;
        this.title = title;
        this.skills = new ArrayList<>(skills);
        this.minExp = Math.max(0, minExp);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getMinExp() { return minExp; }
    public List<String> getSkills() { return Collections.unmodifiableList(skills); }
}
