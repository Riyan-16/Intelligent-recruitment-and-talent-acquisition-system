const STAGES = ["Applied", "Screening", "Interview", "Offer", "Hired", "Rejected"];
let state = { candidates: [], jobs: [], interviews: [] };

const $ = id => document.getElementById(id);
const esc = s => String(s).replace(/[&<>"']/g, ch => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[ch]));
const tags = arr => arr.map(s => `<span class="tag">${esc(s)}</span>`).join("");
const countByStage = s => state.candidates.filter(c => c.stage === s).length;

// talk to the Java server
async function call(url, data) {
  const opt = data ? {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams(data)
  } : {};
  const res = await fetch(url, opt);
  const out = await res.json();
  if (!res.ok) { alert(out.error || "Something went wrong"); return null; }
  return out;
}
async function act(url, data) {
  const s = await call(url, data);
  if (s) { state = s; render(); }
}

function render() {
  $("stats").innerHTML = [
    [state.candidates.length, "Candidates"],
    [state.jobs.length, "Open jobs"],
    [state.interviews.length, "Interviews"],
    [countByStage("Hired"), "Hired"]
  ].map(([n, l]) => `<div class="stat"><strong>${n}</strong><span>${l}</span></div>`).join("");

  const max = Math.max(1, ...STAGES.map(countByStage));
  $("stageBars").innerHTML = STAGES.map(s => {
    const n = countByStage(s);
    return `<div class="bar"><em>${s}</em><i style="width:${(n / max) * 60}%"></i>${n}</div>`;
  }).join("");

  $("candList").innerHTML = state.candidates.map(c => `
    <div class="item">
      <div><b>${esc(c.name)}</b><small>${esc(c.email)} · ${esc(c.desc)}</small><br>${tags(c.skills)}</div>
      <button class="del" onclick="delCand(${c.id})">Remove</button>
    </div>`).join("") || "<small>No candidates yet.</small>";

  $("jobList").innerHTML = state.jobs.map(j => `
    <div class="item">
      <div><b>${esc(j.title)}</b><small>Min experience: ${j.minExp} yr</small><br>${tags(j.skills)}</div>
      <button class="del" onclick="delJob(${j.id})">Close</button>
    </div>`).join("") || "<small>No jobs posted.</small>";

  const keep = $("matchJob").value;
  $("matchJob").innerHTML = state.jobs.map(j => `<option value="${j.id}">${esc(j.title)}</option>`).join("");
  if (keep) $("matchJob").value = keep;
  renderMatch();

  $("board").innerHTML = STAGES.map(s => `
    <div class="col"><h4>${s} (${countByStage(s)})</h4>
    ${state.candidates.filter(c => c.stage === s).map(c => `
      <div class="pc">${esc(c.name)}
        <select onchange="moveStage(${c.id}, this.value)">
          ${STAGES.map(x => `<option ${x === c.stage ? "selected" : ""}>${x}</option>`).join("")}
        </select>
      </div>`).join("")}
    </div>`).join("");

  $("iCand").innerHTML = state.candidates.map(c => `<option value="${c.id}">${esc(c.name)}</option>`).join("");
  $("intList").innerHTML = state.interviews.map(i => `
    <div class="item">
      <div><b>${esc(i.name)}</b><small>${new Date(i.when).toLocaleString()} · ${esc(i.mode)}</small></div>
    </div>`).join("") || "<small>Nothing scheduled.</small>";
}

async function renderMatch() {
  const id = $("matchJob").value;
  if (!id) { $("matchList").innerHTML = "<small>Post a job first.</small>"; return; }
  const list = await call("/api/match?job=" + id);
  if (!list) return;
  $("matchList").innerHTML = list.map(r => `
    <div class="item">
      <div><b>${esc(r.name)}</b><small>${esc(r.desc)}</small></div>
      <div class="score"><div><i style="width:${r.pct}%"></i></div><small>${r.pct}% match</small></div>
    </div>`).join("");
}

// actions
window.delCand = id => act("/api/candidate/delete", { id });
window.delJob = id => act("/api/job/delete", { id });
window.moveStage = (id, stage) => act("/api/stage", { id, stage });

// forms
$("cType").onchange = e => {
  $("cExtra").placeholder = e.target.value === "exp" ? "Years of experience" : "Degree";
  $("cExtra").type = e.target.value === "exp" ? "number" : "text";
};
$("candForm").onsubmit = e => {
  e.preventDefault();
  act("/api/candidate", {
    name: $("cName").value, email: $("cMail").value, skills: $("cSkills").value,
    type: $("cType").value, extra: $("cExtra").value
  });
  e.target.reset(); $("cType").onchange({ target: $("cType") });
};
$("jobForm").onsubmit = e => {
  e.preventDefault();
  act("/api/job", { title: $("jTitle").value, skills: $("jSkills").value, minExp: $("jExp").value });
  e.target.reset();
};
$("intForm").onsubmit = e => {
  e.preventDefault();
  act("/api/interview", { cand: $("iCand").value, when: $("iDate").value, mode: $("iMode").value });
  e.target.reset();
};
$("matchJob").onchange = renderMatch;

// tabs
document.querySelectorAll("nav button").forEach(b => {
  b.onclick = () => {
    document.querySelectorAll("nav button").forEach(x => x.classList.remove("on"));
    document.querySelectorAll(".tab").forEach(x => x.classList.remove("show"));
    b.classList.add("on");
    $(b.dataset.tab).classList.add("show");
  };
});

act("/api/state");
