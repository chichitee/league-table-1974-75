# AI Reflection

The moment worth describing honestly happened during research, not while
writing code, and it's the kind of failure I think is easy to miss if you're
not specifically watching for it.

I asked Claude to fetch the real First Division results for the first ten
rounds of the 1974/75 season from a football statistics site, as the data
source for the actual deliverable. The first pass came back with only 8
matches for "Matchday 9" instead of the 11 a full round needs, so — reasonably,
I thought — it re-queried the same page and told the fetching tool "each
matchday has exactly 11 matches, list all of them, don't skip any." That
worked, in the sense that it came back with 11 matches. What I didn't catch
immediately was that three of those eleven were wrong: Arsenal v Newcastle
United, Leeds v Tottenham, and Middlesbrough v Leicester had been filled in
to complete the round, and at least one of them (the Arsenal–Newcastle
score) turned out to have been lifted from the *reverse* fixture played
seven months later, in April 1975. The tool didn't flag any of this as
uncertain — it just returned a complete-looking round.

What made me disagree with how this was initially handled wasn't the
mistake itself (fetching tools summarizing a big page are always going to be
a little lossy) — it's that being told "there should be 11" gave the model
license to *manufacture* the missing 3 rather than report back that the
premise might be wrong. That's a subtly dangerous failure mode for exactly
this kind of task: a coding test's whole point is to check the "table" logic
against real numbers, and confidently wrong input data produces a
confidently wrong — but plausible-looking — answer that's much harder to
spot than a crash.

Once I noticed the discrepancy (checking Arsenal's own season page showed no
match at all around that date), the response was the right one: rather than
patch over the three scores, it cross-checked every club's log against an
independent source (Wikipedia's season articles, plus a dedicated Newcastle
history site) and figured out what had actually happened — those six clubs
had that round's fixture postponed, full stop, and simply played nine
games rather than ten by the cut-off date. That turned out to be a more
interesting and more correct outcome than either of us initially assumed:
the "week 10" table for six clubs genuinely does show a game in hand, which
is a real, checkable feature of that season rather than a data-quality
compromise. I asked for that verification approach — cross-checking every
club's own log rather than trusting one site — to be written down in
`CLAUDE.md`, and for `README.md` to say plainly what was wrong and how it was
found, rather than presenting the final data set as if it had been correct
from the start.

The decision I'd call out as shaping the solution, separate from that
incident: I chose goal *average* (goals for ÷ goals against) as the
tie-break rather than goal difference, and 2 points for a win rather than 3,
specifically because those were the actual rules English football used in
1974/75 — goal difference wasn't introduced until 1976/77, and 3 points for
a win didn't arrive until 1981/82. It would have been easy, and would have
looked "obviously right" to anyone skimming the code, to default to today's
rules instead. Making that an explicit, sourced choice (and adding a unit
test that specifically shows a case where goal average and goal difference
would rank two teams differently) felt like the part of this exercise that
actually tested understanding of the problem, rather than just the ability
to write a sort comparator.
