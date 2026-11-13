public interface Scorer {
    double score(Candidate c, Job job);   // returns 0 to 100
}
