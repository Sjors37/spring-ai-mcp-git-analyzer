package com.sjors37.git_analyzer_server.model;

import java.time.Instant;

public record CommitInfo(
        String shortHash,
        String authorName,
        String message,
        Instant commitTime
) {}