public class SkillScorer implements Scorer {
    @Override
    public double score(Candidate c, Job job) {
        if (job.getSkills().isEmpty()) return 100;
        long hit = job.getSkills().stream().filter(c.getSkills()::contains).count();
        return (hit * 100.0) / job.getSkills().size();
    }
}
