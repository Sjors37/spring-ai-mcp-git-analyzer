package com.sjors37.git_analyzer_server.tools;

import com.sjors37.git_analyzer_server.git.GitRepositoryReader;
import com.sjors37.git_analyzer_server.model.CommitInfo;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GitCommitTool {

    private final GitRepositoryReader gitRepositoryReader;

    public GitCommitTool(GitRepositoryReader gitRepositoryReader) {
        this.gitRepositoryReader = gitRepositoryReader;
    }

    @McpTool(description = "Lists the most recent commits of a Git repository, including author, message, and date. " +
            "Use this to see recent activity in a repository.")
    public String listRecentCommits(
            @McpToolParam(description = "Absolute path to the local Git repository", required = true) String repoPath,
            @McpToolParam(description = "Maximum number of commits to return", required = true) int maxCount
    ) {
        try {
            List<CommitInfo> commits = gitRepositoryReader.getRecentCommits(repoPath, maxCount);

            if (commits.isEmpty()) {
                return "No commits found in repository at '" + repoPath + "'.";
            }

            return commits.stream()
                    .map(c -> "[%s] %s by %s: %s".formatted(c.shortHash(), c.commitTime(), c.authorName(), c.message()))
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");
        } catch (Exception e) {
            return "Failed to read commits from '" + repoPath + "': " + e.getMessage();
        }
    }
}