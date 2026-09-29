package com.sjors37.spring_ai_mcp_git_analyzer.git;

import com.sjors37.spring_ai_mcp_git_analyzer.model.CommitInfo;
import com.sjors37.spring_ai_mcp_git_analyzer.model.RepoStats;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GitRepositoryReaderTest {

    @TempDir
    Path tempDir;

    private GitRepositoryReader gitRepositoryReader;
    private String repoPath;

    @BeforeEach
    void setUp() throws Exception {
        gitRepositoryReader = new GitRepositoryReader();
        repoPath = tempDir.toString();

        try (Git git = Git.init().setDirectory(tempDir.toFile()).call()) {
            commitFile(git, "readme.md", "# Test repo", "Alice", "Initial commit");
            commitFile(git, "app.txt", "app content v1", "Bob", "Add app.txt");
            commitFile(git, "app.txt", "app content v2", "Alice", "Update app.txt");
            commitFile(git, "readme.md", "# Test repo updated", "Alice", "Update readme");
        }
    }

    private void commitFile(Git git, String fileName, String content, String author, String message) throws Exception {
        Path filePath = tempDir.resolve(fileName);
        Files.writeString(filePath, content);
        git.add().addFilepattern(fileName).call();
        git.commit()
                .setMessage(message)
                .setAuthor(author, author.toLowerCase() + "@example.com")
                .call();
    }

    @Test
    void getRecentCommits_returnsCommitsInReverseChronologicalOrder() throws Exception {
        List<CommitInfo> commits = gitRepositoryReader.getRecentCommits(repoPath, 10);

        assertThat(commits).hasSize(4);
        assertThat(commits.get(0).message()).isEqualTo("Update readme");
        assertThat(commits.get(3).message()).isEqualTo("Initial commit");
    }

    @Test
    void getRecentCommits_respectsMaxCount() throws Exception {
        List<CommitInfo> commits = gitRepositoryReader.getRecentCommits(repoPath, 2);

        assertThat(commits).hasSize(2);
        assertThat(commits.get(0).message()).isEqualTo("Update readme");
        assertThat(commits.get(1).message()).isEqualTo("Update app.txt");
    }

    @Test
    void getRecentCommits_includesAuthorName() throws Exception {
        List<CommitInfo> commits = gitRepositoryReader.getRecentCommits(repoPath, 1);

        assertThat(commits.getFirst().authorName()).isEqualTo("Alice");
    }

    @Test
    void getRepoStats_countsTotalCommitsCorrectly() throws Exception {
        RepoStats stats = gitRepositoryReader.getRepoStats(repoPath);

        assertThat(stats.totalCommits()).isEqualTo(4);
    }

    @Test
    void getRepoStats_countsDistinctContributors() throws Exception {
        RepoStats stats = gitRepositoryReader.getRepoStats(repoPath);

        assertThat(stats.contributorCount()).isEqualTo(2);
    }

    @Test
    void getRepoStats_identifiesMostChangedFiles() throws Exception {
        RepoStats stats = gitRepositoryReader.getRepoStats(repoPath);

        assertThat(stats.topChangedFiles()).containsKey("app.txt");
        assertThat(stats.topChangedFiles()).containsKey("readme.md");
        assertThat(stats.topChangedFiles().get("app.txt")).isEqualTo(2);
        assertThat(stats.topChangedFiles().get("readme.md")).isEqualTo(1);
    }
}