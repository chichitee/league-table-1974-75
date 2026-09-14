# AI conversation history

This folder is the conversation-history export requested by the assignment
("copy the contents of `~/.claude/projects/<project-path-and-name>/` into an
`./ai/` folder").

A couple of things worth noting about how this maps onto the assignment's
description, since this project was built with Claude running as **Claude
(Cowork)** in a hosted cloud sandbox, rather than the Claude Code CLI running
directly in a local terminal against this checked-out repository:

- The session's working directory started at `/home/claude` (before the
  project directory `league-table-1974-75` itself was created inside it),
  which is why the exported path is `projects/-home-claude/...` rather than
  a path containing this repo's own name — Claude Code/Cowork encodes a
  session's project path by replacing `/` with `-`.
- `180585c4-e91e-5f3e-9f36-33e0ddb40a37.jsonl` is the transcript of the
  session that produced this repository: every user message, assistant
  message, tool call and tool result, in Claude Code's own JSONL session
  format. It includes the research into the 1974/75 season data (including
  the mistake described in `AI_REFLECTION.md` and how it was found), the
  implementation of the Java application, and the writing of the tests and
  documentation. One redaction has been applied to this copy: the name of
  the candidate's current employer (unrelated to this coding test) has been
  replaced with `[Organization]`/`leaguetable`, since this repository is
  public — nothing else has been changed.
- The `180585c4-e91e-5f3e-9f36-33e0ddb40a37/` subfolder is copied as-is from
  the same session directory for completeness; it does not contain
  additional conversation turns.

To read it back, each line is one JSON event; message events have a
`"type": "user"` or `"type": "assistant"` field and a `message` object
whose shape matches the Claude API's message format.
