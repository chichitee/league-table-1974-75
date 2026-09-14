# About this folder

This is the project-level Claude session configuration requested by the
assignment ("include the `.claude/` folder from your project root").

`settings.json` declares the permissions this project needs when worked on
with Claude Code (or Claude in Cowork, as this project actually was): running
Maven and the built jar, running git, and reading/writing files inside the
repository — plus a deliberate `deny` rule against `rm -rf`, since a
CLI-driven coding assistant should not be able to recursively delete files
in this project without an explicit, separate confirmation.

This project was built with Claude running as **Claude (Cowork)** in a
hosted cloud sandbox rather than the Claude Code CLI in a local terminal, so
there is no local hook or MCP-server configuration specific to a developer's
own machine to include here — see `../CLAUDE.md` for the actual instructions
that guided the work, and `../ai/` for the full exported conversation that
produced this repository.
