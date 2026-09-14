# Football League Table Calculator

A command-line application that computes a football (soccer) league standings
table from a CSV file of match results. Built for a SPAN Digital backend
coding test, and used here to calculate the English Football League First
Division table after the 10th round of matches of the 1974/75 season.

## The rules implemented

The 1974/75 season predates several rule changes still associated with
"the football table" today, so this implementation deliberately does **not**
use the modern defaults:

| Rule | 1974/75 (implemented here) | Modern football |
|---|---|---|
| Points for a win | **2** | 3 |
| Points for a draw | 1 | 1 |
| Points for a loss | 0 | 0 |
| Tie-break | **Goal average** (goals scored ÷ goals conceded) | Goal difference (goals scored − goals conceded) |
| Second tie-break | Goals scored | Goals scored |

Goal difference was not adopted by the English Football League until the
1976/77 season — using it for 1974/75 would be historically wrong, and would
occasionally produce a different table order than goal average does. The
2-points-for-a-win rule held in England until 1981/82. Both rules are sourced
from the [1974–75 Football League First Division](https://en.wikipedia.org/wiki/1974%E2%80%9375_Football_League_First_Division)
Wikipedia article, which states the season's own classification order as
"1) Points; 2) Goal average; 3) Goals scored".

The scoring rule is pluggable (`ScoringRules` interface) — a
`threePointsForWin()` variant is included and unit-tested — but the CLI
defaults to the 1974/75 rules, since that's the brief.

**Undocumented tie-break beyond "goals scored":** the historical rules don't
say what breaks a tie that survives all three levels above (in practice this
essentially never happened over a full season). This implementation falls
back to alphabetical order by team name, purely so the output is
deterministic and reproducible. This is a documented assumption, not a
historical fact.

## Building and running

Requires only a JDK, **Java 11 or newer** — the Maven Wrapper is included
(`mvnw` / `mvnw.cmd`), so a separate Maven installation is not needed. The
only runtime dependency is JUnit 5, used for tests only and not bundled into
the jar. Code deliberately avoids any Java 12+ syntax or API (no text blocks,
no `String.formatted()`, no pattern-matching `instanceof`) so it builds
cleanly even on an older JDK 11 — `pom.xml` pins `maven.compiler.release` to
11 so this is enforced, not just assumed.

```bash
./mvnw test               # run the test suite (mvnw.cmd on Windows)
<img width="960" height="726" alt="image" src="https://github.com/user-attachments/assets/d6cb298e-731e-4b16-8a9c-d7d15e5a5aed" />

./mvnw package             # build target/league-table.jar
```

(If you already have Maven installed and prefer it, plain `mvn test` /
`mvn package` work identically — the wrapper just removes that prerequisite.)

The application reads a CSV of match results and writes a CSV standings
table. It supports three invocation styles:

```bash
# 1. Filenames on the command line
java -jar target/league-table.jar data/1974-75-first-division-week10.csv standings.csv

# 2. Input file, output to stdout
java -jar target/league-table.jar data/1974-75-first-division-week10.csv

# 3. stdin / stdout
java -jar target/league-table.jar < data/1974-75-first-division-week10.csv > standings.csv
```

Exit codes: `0` success, `1` usage error (bad arguments, unreadable input
file), `2` the CSV could not be parsed (with a message on stderr naming the
line number and the problem).

## Input format

One match per line, oldest-first or in any order, with a mandatory header:

```csv
Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals
1974-08-17,Birmingham City,Middlesbrough,0,3
1974-08-17,Stoke City,Leeds United,3,0
```

- `Date` is ISO-8601 (`yyyy-MM-dd`).
- Team names may be wrapped in double quotes if they need to contain a comma
  (`""` escapes a literal quote), per the usual CSV convention.
- Blank lines are ignored.
- A team does not need to have played the same number of matches as every
  other team — see "the game-in-hand wrinkle" below.

## Output format

The conventional English league-table column order:

```csv
Position,Team,Played,Won,Drawn,Lost,GoalsFor,GoalsAgainst,GoalAverage,Points
1,Ipswich Town,10,8,0,2,18,6,3.000,16
...
```

`GoalAverage` is formatted to three decimal places. A team that has conceded
no goals at all is printed as `Inf` (mathematically undefined/infinite ratio)
rather than crashing or silently showing `0.000` — this never happens in the
real 1974/75 data used here, but it's exercised by a unit test.

## The 1974/75 First Division, week 10: data and verification

`data/1974-75-first-division-week10.csv` contains the 107 real match results
that make up the first 10 rounds of the 1974/75 First Division season.

**Sourcing.** The primary source was worldfootball.net's round-by-round
schedule for the competition. Because this is a historical data set with no
single authoritative machine-readable source, every club's run of results was
cross-checked against at least one independent source — the corresponding
Wikipedia "1974–75 &lt;Club&gt; F.C. season" article, or, for Newcastle United, a
dedicated fan history site (nufc-history.co.uk) — before being trusted.

**A genuine data error, found and fixed.** That cross-checking caught a real
mistake: the first automated fetch of "Matchday 9" produced 11 fixtures, but
three of them — Arsenal v Newcastle United, Leeds United v Tottenham Hotspur,
and Middlesbrough v Leicester City — turned out to be fabricated. Comparing
against Arsenal's, Newcastle's, Leicester's, Middlesbrough's and Leeds'
individual season logs showed none of those five clubs had *any* match on
24–25 September 1974 at all (the Arsenal–Newcastle score, in particular, was
lifted from their *reverse* fixture played on 23 April 1975). The likely
cause: when re-queried and told "this round should have 11 matches, don't
skip any", the fetching tool filled the gap with plausible-looking but wrong
results rather than reporting that the round genuinely only had 8 matches.
This is written up in more detail, with what it changed about how the data
was verified afterwards, in `AI_REFLECTION.md`.

**The real explanation, once found:** those three fixtures were postponed.
Arsenal, Newcastle United, Leeds United, Tottenham Hotspur, Leicester City
and Middlesbrough simply did not play in round 9 (24–25 September 1974); each
went on to play their round-10 fixture as scheduled on 28 September. So, as
of this cut-off date, those six clubs have a genuine **game in hand** and
show `Played = 9` rather than `10` in the output table — this is not a bug,
it's an authentic feature of the historical data (and of English football
tables generally, which have never required every club to have played the
same number of matches). `EnglishFirstDivision1974_75Test` asserts this
explicitly (6 clubs on 9 games, 16 on 10).

**Ipswich Town finish top after 10 games**, a fact independently checkable
against the final [1974–75 First Division](https://en.wikipedia.org/wiki/1974%E2%80%9375_Football_League_First_Division)
Wikipedia article's narrative of that (eventually very close, four-way)
title race.

The generated standings for this data set are checked in at
`data/1974-75-first-division-week10-standings.csv` and are regenerated with:

```bash
java -jar target/league-table.jar data/1974-75-first-division-week10.csv data/1974-75-first-division-week10-standings.csv
```

## Tests

```bash
./mvnw test
```

- `MatchTest`, `TeamStandingTest` — domain model validation and accumulation.
- `StandingsCalculatorTest` — points, and every level of the tie-break chain,
  including a test that specifically demonstrates goal *average* and goal
  *difference* would rank two teams differently, to guard against someone
  "helpfully" simplifying the tie-break to goal difference later.
- `CsvMatchReaderTest`, `CsvStandingsWriterTest` — parsing/formatting,
  quoting, and every validation error path.
- `AppTest` — the CLI end-to-end, all three invocation styles, and the
  usage/data error exit codes.
- `EnglishFirstDivision1974_75Test` — the actual deliverable: loads the real
  107-match data set and asserts the full, exact 22-row output table
  (position, played, won, drawn, lost, for, against, points) against a table
  that was independently computed in Python from the same sourced results
  before the Java implementation was written, so the test is a genuine
  external check rather than "assert whatever the code currently outputs".

## Project layout

```
├── pom.xml
├── src/main/java/leaguetable/
│   ├── App.java                    # CLI entry point
│   ├── model/                      # Match, TeamStanding
│   ├── service/                    # ScoringRules, StandingsCalculator
│   └── io/                         # CSV reading/writing
├── src/test/java/...               # mirrors the above, plus AppTest and
│                                    # EnglishFirstDivision1974_75Test
├── src/test/resources/             # copy of the real match-results CSV, so
│                                    # tests don't depend on the working directory
├── data/
│   ├── 1974-75-first-division-week10.csv            # input: the deliverable
│   └── 1974-75-first-division-week10-standings.csv  # output: the deliverable
├── CLAUDE.md                       # instructions given to the AI assistant
├── AI_REFLECTION.md                # reflection on the AI collaboration
├── .claude/                        # Claude Code session configuration
└── ai/                             # exported conversation history
```

## Assumptions log

1. **CSV schema** is my own design (`Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals`
   in, `Position,Team,Played,Won,Drawn,Lost,GoalsFor,GoalsAgainst,GoalAverage,Points`
   out) — the brief specifies CSV in/out but not a schema.
2. **"Week 10"** is interpreted as "after the 10th round of the fixture
   list", not "the 10th calendar week of the season" — the two coincide
   closely here (all 10 rounds fall between 17 August and 28 September 1974,
   because the schedule included several midweek rounds), but are not
   identical concepts. Rounds are matched to real calendar dates.
3. Where a round spanned more than one date (e.g. a Tuesday/Wednesday split
   because of fixture congestion), all of that round's matches are dated to
   a single representative date from primary sources, rather than
   researching the exact day for every individual fixture — the standings
   calculation itself is unaffected either way, since it doesn't depend on
   exact dates, only on which 107 results fall within the first 10 rounds.
4. **Tie-break beyond "goals scored"** falls back to alphabetical order, as
   noted above — undocumented by the historical rules and included only for
   determinism.
5. A team with 0 goals for and 0 goals against has goal average defined as
   `0.0` (not `Inf`, not `NaN`) — an edge case the real data set never hits,
   covered by a unit test.
