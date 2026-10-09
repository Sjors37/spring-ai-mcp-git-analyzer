package com.sjors37.git_analyzer_server.tools;

import com.sjors37.git_analyzer_server.git.GitRepositoryReader;
import com.sjors37.git_analyzer_server.model.CommitInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitCommitTool {

    private final GitRepositoryReader gitRepositoryReader;

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
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.warn("Failed to read commits from '{}'", repoPath, e);
            return "Failed to read commits from '" + repoPath + "': " + e.getMessage();
        }
    }
}