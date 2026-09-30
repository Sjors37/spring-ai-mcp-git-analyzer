package com.sjors37.git_analyzer_server.tools;

import com.sjors37.git_analyzer_server.git.GitRepositoryReader;
import com.sjors37.git_analyzer_server.model.RepoStats;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class GitStatsTool {

    private final GitRepositoryReader gitRepositoryReader;

    public GitStatsTool(GitRepositoryReader gitRepositoryReader) {
        this.gitRepositoryReader = gitRepositoryReader;
    }

    @McpTool(description = "Returns statistics for a Git repository: total commit count, number of distinct " +
            "contributors, and the most frequently changed files. Use this to understand overall repository activity.")
    public String getRepoStats(
            @McpToolParam(description = "Absolute path to the local Git repository", required = true) String repoPath
    ) {
        try {
            RepoStats stats = gitRepositoryReader.getRepoStats(repoPath);

            String topFiles = stats.topChangedFiles().entrySet().stream()
                    .map(e -> "%s (%d changes)".formatted(e.getKey(), e.getValue()))
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none");

            return "Repository '%s': %d total commits, %d contributors. Most changed files: %s"
                    .formatted(repoPath, stats.totalCommits(), stats.contributorCount(), topFiles);
        } catch (Exception e) {
            return "Failed to read stats from '" + repoPath + "': " + e.getMessage();
        }
    }
}