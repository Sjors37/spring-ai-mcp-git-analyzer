package com.sjors37.git_analyzer_server.tools;

import com.sjors37.git_analyzer_server.git.GitRepositoryReader;
import com.sjors37.git_analyzer_server.model.CommitInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitCommitToolTest {

    @Mock
    private GitRepositoryReader gitRepositoryReader;

    @InjectMocks
    private GitCommitTool gitCommitTool;

    @Test
    void listRecentCommits_returnsFormattedCommits() throws Exception {
        when(gitRepositoryReader.getRecentCommits("/repo", 5)).thenReturn(List.of(
                new CommitInfo("abc1234", "Alice", "Fix bug", Instant.now())
        ));

        String result = gitCommitTool.listRecentCommits("/repo", 5);

        assertThat(result).contains("abc1234").contains("Alice").contains("Fix bug");
    }

    @Test
    void listRecentCommits_returnsHelpfulMessage_whenNoCommitsFound() throws Exception {
        when(gitRepositoryReader.getRecentCommits("/repo", 5)).thenReturn(List.of());

        String result = gitCommitTool.listRecentCommits("/repo", 5);

        assertThat(result).contains("No commits found in repository at '/repo'");
    }

    @Test
    void listRecentCommits_returnsErrorMessage_whenReaderThrowsException() throws Exception {
        when(gitRepositoryReader.getRecentCommits("/invalid-path", 5))
                .thenThrow(new RuntimeException("Not a git repository"));

        String result = gitCommitTool.listRecentCommits("/invalid-path", 5);

        assertThat(result)
                .contains("Failed to read commits from '/invalid-path'")
                .contains("Not a git repository");
    }
}