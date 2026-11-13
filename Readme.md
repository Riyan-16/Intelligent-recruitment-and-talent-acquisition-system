# HireTrack - Intelligent Recruitment and Talent Acquisition System

HireTrack is a recruitment management application built as a project for the course **Object Oriented Techniques using Java**. It helps a recruiter store candidates and jobs, rank candidates by how well they fit a job, track every applicant through the hiring stages, and schedule interviews, all from one screen.

The backend is written in **Java** using core object-oriented concepts and runs on the built-in `com.sun.net.httpserver` server, so no external framework or database is needed. The frontend is built with **HTML, CSS and JavaScript**.

---

## Features

1. **Dashboard** - live counts of candidates, open jobs, interviews and hires, with a bar chart of candidates in each stage.
2. **Candidate Registry** - add, view and remove candidates. Supports two types: Fresher (with degree) and Experienced (with years of experience).
3. **Job Openings** - post and close jobs with required skills and minimum experience.
4. **Smart Matching** - ranks all candidates for a selected job using a weighted score (70% skills, 30% experience).
5. **Hiring Pipeline** - board with six stages: Applied, Screening, Interview, Offer, Hired and Rejected. Candidates can be moved between stages.
6. **Interview Scheduler** - book interviews (Online, On-site or Phone) with date and time. Interviews are kept in date order, and the candidate is moved to the Interview stage automatically.

---

## Object-Oriented Concepts Used

| Concept | Where it is used |
|---|---|
| **Abstraction** | `Person` (abstract class with abstract `describe()`), `Candidate` (abstract class), `Scorer` (interface) |
| **Inheritance** | `Person` -> `Candidate` -> `Fresher` / `Experienced`; `Scorer` -> `SkillScorer` / `ExperienceScorer` |
| **Encapsulation** | Private fields with getters, unmodifiable lists returned for skills, stage changed only through `moveTo()` with validation |
| **Polymorphism** | Overridden `describe()` and `getExperienceYears()`; `MatchEngine` calls `score()` on any `Scorer` without knowing its type |

Other Java features used: enums (`Stage`), records (`Interview`, `MatchEngine.Result`), collections, streams, lambda expressions, `Optional` and exception handling.

---

## Tech Stack

- **Backend:** Java 17 or higher (standard library only)
- **Frontend:** HTML5, CSS3, JavaScript (Fetch API)
- **Data format:** JSON
- **Storage:** In memory (resets when the server restarts)

---

## Project Structure

```
HireTrack/
 |-- Stage.java               enum of hiring stages
 |-- Util.java                helper methods (skill parsing, JSON text)
 |-- Person.java              abstract base class
 |-- Candidate.java           abstract class, extends Person
 |-- Fresher.java             extends Candidate
 |-- Experienced.java         extends Candidate
 |-- Job.java                 job opening
 |-- Scorer.java              scoring interface
 |-- SkillScorer.java         scores by matching skills
 |-- ExperienceScorer.java    scores by years of experience
 |-- MatchEngine.java         combines scorers and ranks candidates
 |-- Interview.java           interview record
 |-- RecruitmentStore.java    central data store and rules
 |-- Server.java              HTTP server and API
 |-- web/
      |-- index.html          user interface
      |-- style.css           styling
      |-- app.js              frontend logic (calls the Java API)
```

---

## Getting Started

### Requirements

- JDK 17 or newer
- Any modern web browser

Check your Java installation:

```
javac -version
```

### Steps

1. Clone the repository:

```
git clone <your-repository-link>
cd <repository-folder>
```

2. Compile all the Java files from the project folder.

On Windows PowerShell:

```
javac (Get-ChildItem *.java).Name
```

On Linux / macOS / Command Prompt:

```
javac *.java
```

3. Start the server:

```
java Server
```

4. Open your browser and go to:

```
http://localhost:8080
```

> **Note:** Run the commands from the folder that contains the `.java` files, with the `web` folder next to them. The server looks for the `web` folder in the current directory.

---

## API Endpoints

| Endpoint | Purpose |
|---|---|
| `/api/state` | Get all candidates, jobs and interviews |
| `/api/match?job=ID` | Get candidates ranked for a job |
| `/api/candidate` | Add a candidate |
| `/api/candidate/delete` | Remove a candidate |
| `/api/job` | Post a job |
| `/api/job/delete` | Close a job |
| `/api/stage` | Change a candidate's stage |
| `/api/interview` | Schedule an interview |

---

## Matching Logic

The final match percentage is calculated as:

```
match % = (skill score x 0.7) + (experience score x 0.3)
```

- **Skill score:** percentage of the job's required skills that the candidate has.
- **Experience score:** candidate's years of experience compared with the job's minimum (capped at 100%).

New scoring rules can be added by creating a class that implements `Scorer` and registering it in `MatchEngine`.

---

## Design

The interface uses a three-colour palette in an approximate 60-30-10 ratio:

| Colour | Hex | Usage |
|---|---|---|
| Jet Black | `#0b0b0e` | Page and card backgrounds (~60%) |
| Royal Amethyst | `#5a2d91` | Sidebar, stat tiles, tags (~30%) |
| Electric Cyan | `#00e5ff` | Buttons, progress bars, active tab (~10%) |

---

## Limitations

- Data is stored in memory and is lost when the server is stopped.
- No login or user roles.
- Designed for local, single-user use.

## Future Improvements

- Database storage (MySQL or SQLite)
- Login with recruiter and admin roles
- Resume upload and skill extraction
- More scorers (education, location) and adjustable weights
- Email reminders for interviews
- Search and filters for candidates
- Downloadable hiring reports
---

## License

This project was created for academic purposes. You may add a license of your choice (for example, MIT) before making the repository public.
