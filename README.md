# Spring AI MCP Git Analyzer

An MCP (Model Context Protocol) server and client built with **Spring Boot 4** and **Spring AI 2.0** that expose Git repository analysis as reusable, protocol-standard tools — and demonstrate an AI model actually discovering and using them.

This is the second project in a series exploring agentic AI patterns in the Java/Spring ecosystem. See [`ops-assistant-agent`](https://github.com/Sjors37/ops-assistant-agent) for the first (tool-calling agent with Spring AI, no MCP).

## Why this project

Most agentic AI tools are hardcoded directly into a single application, which means they can't be reused elsewhere without copy-pasting code. **MCP** solves this: it's a standardized protocol (now a Linux Foundation project) that lets a tool be built once, as a standalone server, and used by any MCP-compatible client — Claude Desktop, VS Code, custom applications, and so on.

This repo contains two independent Spring Boot applications:
- **`git-analyzer-server`** — an MCP server exposing Git analysis tools
- **`git-analyzer-client`** — an MCP client that connects to the server over HTTP, discovers its tools, and lets Claude use them during a conversation

They run as **separate processes**, communicating over the actual MCP protocol (Streamable HTTP) — the same way a completely separate client (like Claude Desktop) would talk to the server. This isn't a multi-module Maven build; each is a standalone project with its own `pom.xml`, kept in the same repo for convenience.

## What it does

- **`listRecentCommits`** — returns the N most recent commits of a Git repository (author, message, date)
- **`getRepoStats`** — returns commit count, contributor count, and most frequently changed files

Example: *"What are the last 5 commits in this repo, and who's the most active contributor?"*

## Tech stack

- **Java 21**
- **Spring Boot 4.0**
- **Spring AI 2.0** — MCP server (`spring-ai-starter-mcp-server-webmvc`) and MCP client (`spring-ai-starter-mcp-client`) starters, Anthropic model integration on the client
- **JGit** (native Java Git operations, no CLI dependency)
- **Maven**

## Getting started

### Prerequisites
- Java 21+
- Maven
- An Anthropic API key
- A local Git repository to analyze (this repo itself works fine)

### Run the server first

```bash
cd git-analyzer-server
./mvnw spring-boot:run
```

Runs on port `8081`. Wait until it logs `Started GitAnalyzerServerApplication` before starting the client.

### Then run the client

In a separate terminal:

```bash
cd git-analyzer-client
export ANTHROPIC_API_KEY=your-key-here
./mvnw spring-boot:run
```

Runs on port `8080` and connects to the server at `http://localhost:8081`.

### Try it out

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "List the last 5 commits in /absolute/path/to/a/repo and give me the repo stats"}'
```

## Running tests

Each module has its own test suite:

```bash
cd git-analyzer-server && ./mvnw test
cd git-analyzer-client && ./mvnw test
```

The server's tests cover the JGit-based repository reader (against a real, temporary Git repo) and both tools (with the reader mocked). The client's test suite mirrors the controller test approach from `ops-assistant-agent`.

## Known limitations

- **No conversation memory** — same limitation as `ops-assistant-agent`; each `/chat` request is stateless.
- **Client and server must both be running locally** — there's no service discovery; the client is configured with a fixed URL to the server.
- **Full commit history is walked for `getRepoStats`** — fine for small-to-medium repos, would need pagination/limits for very large ones.

