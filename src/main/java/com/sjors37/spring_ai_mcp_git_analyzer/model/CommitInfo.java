package com.sjors37.spring_ai_mcp_git_analyzer.model;

import java.time.Instant;

public record CommitInfo(
        String shortHash,
        String authorName,
        String message,
        Instant commitTime
) {}