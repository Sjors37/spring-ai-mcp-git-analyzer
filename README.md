# Spring AI MCP Git Analyzer

An MCP (Model Context Protocol) server and client built with **Spring Boot 4** and **Spring AI 2.0** that exposes Git repository analysis as reusable, protocol-standard tools — and a client that shows an AI model actually discovering and using them.

## Why this project

Most agentic AI tools are hardcoded directly into a single application, which means they can't be reused elsewhere without copy-pasting code. **MCP** solves this: it's a standardized protocol that lets a tool be built once, as a standalone server, and used by any MCP-compatible client — Claude Desktop, VS Code, custom applications, and so on.

This project builds:
- An **MCP server** exposing two Git analysis tools
- An **MCP client** that connects to that server, discovers its tools, and lets Claude use them during a conversation

Both run in the same application for simplicity, but communicate over the actual MCP protocol (HTTP/SSE) — not direct Java method calls — the same way a completely separate client (like Claude Desktop) would talk to it.

## What it does

- **`listRecentCommits`** — returns the N most recent commits of a Git repository (author, message, date)
- **`getRepoStats`** — returns commit count, contributor count, and most frequently changed files

Example: *"What are the last 5 commits in this repo, and who's the most active contributor?"*

## Tech stack

- **Java 21**
- **Spring Boot 4.0**
- **Spring AI 2.0** (Anthropic model integration, MCP server + client starters)
- **JGit** (native Java Git operations, no CLI dependency)
- **Maven**

## Getting started

*(Setup instructions will be filled in once the server and client are implemented.)*

### Prerequisites
- Java 21+
- Maven
- An Anthropic API key
- A local Git repository to analyze (can be this repo itself)
