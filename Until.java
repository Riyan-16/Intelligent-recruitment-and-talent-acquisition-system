import java.util.*;
import java.util.stream.Collectors;

final class Util {
    private Util() {}

    static List<String> skills(String csv) {
        if (csv == null) return List.of();
        return Arrays.stream(csv.split(","))
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    static int toInt(String s) {
        try { return Math.max(0, Integer.parseInt(s.trim())); }
        catch (Exception e) { return 0; }
    }

    static String str(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "").replace("\t", " ") + "\"";
    }

    static String arr(List<String> list) {
        return list.stream().map(Util::str).collect(Collectors.joining(",", "[", "]"));
    }
}
