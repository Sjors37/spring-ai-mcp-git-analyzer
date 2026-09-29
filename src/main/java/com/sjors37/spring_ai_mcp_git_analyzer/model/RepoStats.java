package com.sjors37.spring_ai_mcp_git_analyzer.model;

import java.util.Map;

public record RepoStats(
        int totalCommits,
        int contributorCount,
        Map<String, Integer> topChangedFiles
) {}