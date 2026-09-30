package com.sjors37.git_analyzer_server.model;

import java.util.Map;

public record RepoStats(
        int totalCommits,
        int contributorCount,
        Map<String, Integer> topChangedFiles
) {}