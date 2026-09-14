# Project instructions for Claude

This file guides AI-assisted work on this repository: a Java command-line
football league table calculator, built for a take-home backend coding test.
The test brief explicitly expects and evaluates AI collaboration, so treat
this file as you would for any real project — read it before making changes,
and keep it up to date if the project's conventions change.

## What this project is

A CLI that reads football match results from a CSV file and writes a league
standings table to CSV. It is used to calculate the English Football League
First Division table after the 10th round of matches of the 1974/75 season —
see `README.md` for the full brief, the historical rules, and the data
sourcing/verification notes.

## Ground rules

1. **Language: Java only**, per the assignment. Target **Java 11** — the
   reviewer's own machine may not have anything newer, and the assignment
   says to keep the platform assumptions minimal. Concretely, that rules out
   text blocks, `String.formatted()`, pattern-matching `instanceof`, records,
   and switch expressions (all Java 12+). `pom.xml`'s `maven.compiler.release`
   enforces this at build time rather than relying on nobody using them.
2. **No new runtime dependencies without a good reason.** JUnit 5 for tests
   is the only dependency this project should need. Don't reach for Guava,
   Apache Commons CSV, Lombok, etc. — hand-rolling the small amount of CSV
   parsing this needs keeps the submission dependency-free and easy to
   review, per the assignment's own guidance not to commit installed
   packages.
3. **Historical correctness over "obvious" modern defaults.** This is the
   easiest way to get this assignment subtly wrong: it is tempting to default
   to 3 points for a win and goal difference, because that's "how football
   works" today. For the 1974/75 season, neither is true. Before assuming
   any football rule, check whether it was actually true in the specific
   season being computed, and cite a source in `README.md`.
4. **Don't trust a single source for historical data, and don't trust a
   summarized fetch of a large page without spot-checking it.** When
   re-querying a source with a stronger claim about what it "should" contain
   (e.g. "this round has 11 matches, list all of them"), be alert to the
   tool filling gaps with plausible-but-wrong content rather than reporting
   that the premise was false. Cross-check any researched match data against
   at least one independent source before treating it as ground truth. See
   `AI_REFLECTION.md` for a case where this went wrong and how it was caught.
5. **Tests are mandatory and must include a real, external check**, not just
   tests that assert whatever the code currently does. The end-to-end test
   against the real 1974/75 data (`EnglishFirstDivision1974_75Test`) is
   deliberately asserted against a table that was computed independently
   (in Python, from the same sourced CSV) before the Java implementation was
   written, so it can actually catch a wrong implementation rather than just
   confirming one.
6. **Document assumptions rather than silently picking one.** Where the
   brief or the historical rules are ambiguous (the CSV schema, tie-breaks
   beyond "goals scored", how to handle a team with 0 goals against), write
   the assumption down in `README.md`'s "Assumptions log" rather than
   picking silently.
7. **Fail loudly, not silently, on bad input.** The CLI should exit non-zero
   with a clear message (including line numbers, for CSV parse errors)
   rather than producing a wrong table from malformed input.

## Working conventions

- Package structure: `model` (domain objects: `Match`, `TeamStanding`),
  `service` (`ScoringRules`, `StandingsCalculator`), `io` (CSV read/write),
  and `App` (CLI entry point) at the root.
- Keep domain objects immutable (`Match`, `TeamStanding`); `TeamStanding`
  accumulates via `withResult(...)` returning a new instance, not mutation.
- `StandingsCalculator`'s sort order is the single most important piece of
  business logic in this codebase — any change to it needs a new test in
  `StandingsCalculatorTest` demonstrating the specific behaviour, not just an
  update to the existing assertions.
- Run `./mvnw test` before considering any change done. Run
  `./mvnw package && java -jar target/league-table.jar data/1974-75-first-division-week10.csv data/1974-75-first-division-week10-standings.csv`
  and check the committed output CSV is still correct after any change that
  could affect the real data set's table.
- Target Java **11**, not whatever JDK happens to be on the machine doing the
  work — `pom.xml` pins `maven.compiler.release`. This bit a real review
  session: the code was originally written against Java 17 (text blocks,
  `String.formatted()`, pattern-matching `instanceof`) and failed to build on
  the reviewer's actual Java 11 environment. Don't reintroduce those.

## AI collaboration artefacts

This repository intentionally includes `.claude/` and `ai/` alongside this
file and `AI_REFLECTION.md`, per the assignment's requirements — do not
`.gitignore` them.
