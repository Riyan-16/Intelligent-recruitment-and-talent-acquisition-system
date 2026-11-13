import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    static final RecruitmentStore store = new RecruitmentStore();

    public static void main(String[] args) throws IOException {
        seed();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/", Server::api);
        server.createContext("/", Server::files);
        server.start();
        System.out.println("HireTrack running at http://localhost:8080");
    }

    static void seed() {
        store.addCandidate(new Experienced("Riya Sharma", "riya@mail.com", Util.skills("java,spring,sql"), 4));
        store.addCandidate(new Fresher("Arjun Mehta", "arjun@mail.com", Util.skills("java,html,css"), "B.Tech CSE"));
        store.addCandidate(new Experienced("Neha Kapoor", "neha@mail.com", Util.skills("javascript,react,css"), 2));
        store.addJob(new Job("Java Developer", Util.skills("java,sql,spring"), 2));
        store.addJob(new Job("Frontend Intern", Util.skills("html,css,javascript"), 0));
    }

    // ---------- static files ----------
    static void files(HttpExchange ex) throws IOException {
        String p = ex.getRequestURI().getPath();
        if (p.equals("/")) p = "/index.html";
        Path root = Paths.get("web").toAbsolutePath().normalize();
        Path f = root.resolve(p.substring(1)).normalize();
        if (!f.startsWith(root) || !Files.isRegularFile(f)) {
            send(ex, 404, "text/plain", "Not found".getBytes());
            return;
        }
        String type = p.endsWith(".css") ? "text/css"
                    : p.endsWith(".js") ? "application/javascript" : "text/html";
        send(ex, 200, type + "; charset=utf-8", Files.readAllBytes(f));
    }

    // ---------- API ----------
    static void api(HttpExchange ex) throws IOException {
        try {
            Map<String, String> q = params(ex);
            switch (ex.getRequestURI().getPath()) {
                case "/api/state" -> json(ex, state());
                case "/api/match" -> json(ex, match(Integer.parseInt(q.get("job"))));
                case "/api/candidate" -> {
                    List<String> skills = Util.skills(q.get("skills"));
                    Candidate c = "exp".equals(q.get("type"))
                        ? new Experienced(q.get("name"), q.get("email"), skills, Util.toInt(q.get("extra")))
                        : new Fresher(q.get("name"), q.get("email"), skills, q.get("extra"));
                    store.addCandidate(c);
                    json(ex, state());
                }
                case "/api/candidate/delete" -> {
                    store.removeCandidate(Integer.parseInt(q.get("id")));
                    json(ex, state());
                }
                case "/api/job" -> {
                    store.addJob(new Job(q.get("title"), Util.skills(q.get("skills")), Util.toInt(q.get("minExp"))));
                    json(ex, state());
                }
                case "/api/job/delete" -> {
                    store.removeJob(Integer.parseInt(q.get("id")));
                    json(ex, state());
                }
                case "/api/stage" -> {
                    Candidate c = store.findCandidate(Integer.parseInt(q.get("id")))
                            .orElseThrow(() -> new IllegalArgumentException("No such candidate"));
                    c.moveTo(Stage.fromLabel(q.get("stage")));
                    json(ex, state());
                }
                case "/api/interview" -> {
                    store.schedule(Integer.parseInt(q.get("cand")),
                            LocalDateTime.parse(q.get("when")), q.get("mode"));
                    json(ex, state());
                }
                default -> send(ex, 404, "application/json", "{\"error\":\"Not found\"}".getBytes());
            }
        } catch (Exception e) {
            String msg = "{\"error\":" + Util.str(String.valueOf(e.getMessage())) + "}";
            send(ex, 400, "application/json; charset=utf-8", msg.getBytes(StandardCharsets.UTF_8));
        }
    }

    static String state() {
        String cands = store.getCandidates().stream().map(c ->
            "{\"id\":" + c.getId() + ",\"name\":" + Util.str(c.getName())
            + ",\"email\":" + Util.str(c.getEmail())
            + ",\"desc\":" + Util.str(c.describe())          // polymorphic call
            + ",\"skills\":" + Util.arr(c.getSkills())
            + ",\"stage\":" + Util.str(c.getStage().getLabel()) + "}"
        ).collect(Collectors.joining(","));

        String jobs = store.getJobs().stream().map(j ->
            "{\"id\":" + j.getId() + ",\"title\":" + Util.str(j.getTitle())
            + ",\"minExp\":" + j.getMinExp()
            + ",\"skills\":" + Util.arr(j.getSkills()) + "}"
        ).collect(Collectors.joining(","));

        String ints = store.getInterviews().stream().map(i ->
            "{\"name\":" + Util.str(i.candidate().getName())
            + ",\"when\":" + Util.str(i.when().toString())
            + ",\"mode\":" + Util.str(i.mode()) + "}"
        ).collect(Collectors.joining(","));

        return "{\"candidates\":[" + cands + "],\"jobs\":[" + jobs + "],\"interviews\":[" + ints + "]}";
    }

    static String match(int jobId) {
        return store.rank(jobId).stream().map(r ->
            "{\"name\":" + Util.str(r.candidate().getName())
            + ",\"desc\":" + Util.str(r.candidate().describe())
            + ",\"pct\":" + r.percent() + "}"
        ).collect(Collectors.joining(",", "[", "]"));
    }

    // ---------- helpers ----------
    static Map<String, String> params(HttpExchange ex) throws IOException {
        String query = ex.getRequestURI().getRawQuery();
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> m = new HashMap<>();
        for (String part : ((query == null ? "" : query) + "&" + body).split("&")) {
            int i = part.indexOf('=');
            if (i < 1) continue;
            m.put(URLDecoder.decode(part.substring(0, i), StandardCharsets.UTF_8),
                  URLDecoder.decode(part.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    static void json(HttpExchange ex, String s) throws IOException {
        send(ex, 200, "application/json; charset=utf-8", s.getBytes(StandardCharsets.UTF_8));
    }

    static void send(HttpExchange ex, int code, String type, byte[] data) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, data.length);
        ex.getResponseBody().write(data);
        ex.close();
    }
                                                   }
