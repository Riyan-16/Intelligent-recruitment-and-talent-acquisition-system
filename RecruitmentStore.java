import java.time.LocalDateTime;
import java.util.*;

public class RecruitmentStore {
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Job> jobs = new ArrayList<>();
    private final List<Interview> interviews = new ArrayList<>();
    private final MatchEngine engine = new MatchEngine();

    public List<Candidate> getCandidates() { return Collections.unmodifiableList(candidates); }
    public List<Job> getJobs() { return Collections.unmodifiableList(jobs); }
    public List<Interview> getInterviews() { return Collections.unmodifiableList(interviews); }

    public void addCandidate(Candidate c) { candidates.add(c); }
    public void addJob(Job j) { jobs.add(j); }

    public void removeCandidate(int id) {
        candidates.removeIf(c -> c.getId() == id);
        interviews.removeIf(i -> i.candidate().getId() == id);
    }

    public void removeJob(int id) { jobs.removeIf(j -> j.getId() == id); }

    public Optional<Candidate> findCandidate(int id) {
        return candidates.stream().filter(c -> c.getId() == id).findFirst();
    }

    public Optional<Job> findJob(int id) {
        return jobs.stream().filter(j -> j.getId() == id).findFirst();
    }

    public void schedule(int candId, LocalDateTime when, String mode) {
        Candidate c = findCandidate(candId)
                .orElseThrow(() -> new IllegalArgumentException("No such candidate"));
        interviews.add(new Interview(c, when, mode));
        interviews.sort(Comparator.comparing(Interview::when));
        if (c.getStage() == Stage.APPLIED || c.getStage() == Stage.SCREENING)
            c.moveTo(Stage.INTERVIEW);
    }

    public List<MatchEngine.Result> rank(int jobId) {
        Job j = findJob(jobId).orElseThrow(() -> new IllegalArgumentException("No such job"));
        return engine.rank(candidates, j);
    }
}
