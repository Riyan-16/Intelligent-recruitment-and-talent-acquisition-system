public class ExperienceScorer implements Scorer {
    @Override
    public double score(Candidate c, Job job) {
        if (job.getMinExp() == 0) return 100;
        return Math.min((double) c.getExperienceYears() / job.getMinExp(), 1.0) * 100;
    }
}
