package com.sjors37.git_analyzer_server.tools;

import com.sjors37.git_analyzer_server.git.GitRepositoryReader;
import com.sjors37.git_analyzer_server.model.RepoStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitStatsToolTest {

    @Mock
    private GitRepositoryReader gitRepositoryReader;

    @InjectMocks
    private GitStatsTool gitStatsTool;

    @Test
    void getRepoStats_returnsFormattedStats() throws Exception {
        Map<String, Integer> topFiles = new LinkedHashMap<>();
        topFiles.put("app.txt", 2);
        topFiles.put("readme.md", 1);

        when(gitRepositoryReader.getRepoStats("/repo"))
                .thenReturn(new RepoStats(4, 2, topFiles));

        String result = gitStatsTool.getRepoStats("/repo");

        assertThat(result)
                .contains("4 total commits")
                .contains("2 contributors")
                .contains("app.txt (2 changes)")
                .contains("readme.md (1 changes)");
    }

    @Test
    void getRepoStats_handlesEmptyTopFiles() throws Exception {
        when(gitRepositoryReader.getRepoStats("/empty-repo"))
                .thenReturn(new RepoStats(0, 0, Map.of()));

        String result = gitStatsTool.getRepoStats("/empty-repo");

        assertThat(result).contains("Most changed files: none");
    }

    @Test
    void getRepoStats_returnsErrorMessage_whenReaderThrowsException() throws Exception {
        when(gitRepositoryReader.getRepoStats("/invalid-path"))
                .thenThrow(new RuntimeException("Not a git repository"));

        String result = gitStatsTool.getRepoStats("/invalid-path");

        assertThat(result)
                .contains("Failed to read stats from '/invalid-path'")
                .contains("Not a git repository");
    }
}