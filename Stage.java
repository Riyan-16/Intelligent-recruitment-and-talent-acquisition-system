public enum Stage {
    APPLIED("Applied"), SCREENING("Screening"), INTERVIEW("Interview"),
    OFFER("Offer"), HIRED("Hired"), REJECTED("Rejected");

    private final String label;
    Stage(String label) { this.label = label; }
    public String getLabel() { return label; }

    public static Stage fromLabel(String l) {
        for (Stage s : values()) if (s.label.equalsIgnoreCase(l)) return s;
        throw new IllegalArgumentException("Unknown stage: " + l);
    }
}
