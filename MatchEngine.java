import java.util.*;

public class MatchEngine {
    public record Result(Candidate candidate, int percent) {}

    private final Map<Scorer, Double> scorers = new LinkedHashMap<>();

    public MatchEngine() {
        scorers.put(new SkillScorer(), 0.7);
        scorers.put(new ExperienceScorer(), 0.3);
    }

    public int rate(Candidate c, Job job) {
        double total = 0;
        for (Map.Entry<Scorer, Double> e : scorers.entrySet())
            total += e.getKey().score(c, job) * e.getValue();
        return (int) Math.round(total);
    }

    public List<Result> rank(List<Candidate> list, Job job) {
        return list.stream()
                .map(c -> new Result(c, rate(c, job)))
                .sorted(Comparator.comparingInt(Result::percent).reversed())
                .toList();
    }
}
