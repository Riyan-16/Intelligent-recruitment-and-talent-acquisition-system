import java.time.LocalDateTime;

public record Interview(Candidate candidate, LocalDateTime when, String mode) {}
