package com.sjors37.git_analyzer_server.git;

import com.sjors37.git_analyzer_server.model.CommitInfo;
import com.sjors37.git_analyzer_server.model.RepoStats;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GitRepositoryReader {

    public List<CommitInfo> getRecentCommits(String repoPath, int maxCount) throws IOException, org.eclipse.jgit.api.errors.GitAPIException {
        try (Git git = Git.open(new File(repoPath))) {
            List<CommitInfo> commits = new ArrayList<>();
            for (RevCommit commit : git.log().setMaxCount(maxCount).call()) {
                commits.add(new CommitInfo(
                        commit.getId().abbreviate(7).name(),
                        commit.getAuthorIdent().getName(),
                        commit.getShortMessage(),
                        Instant.ofEpochSecond(commit.getCommitTime())
                ));
            }
            return commits;
        }
    }

    public RepoStats getRepoStats(String repoPath) throws IOException, org.eclipse.jgit.api.errors.GitAPIException {
        try (Git git = Git.open(new File(repoPath))) {
            Repository repository = git.getRepository();
            int totalCommits = 0;
            Set<String> contributors = new HashSet<>();
            Map<String, Integer> fileChangeCounts = new HashMap<>();

            try (RevWalk revWalk = new RevWalk(repository)) {
                for (RevCommit commit : git.log().call()) {
                    totalCommits++;
                    contributors.add(commit.getAuthorIdent().getName());

                    if (commit.getParentCount() > 0) {
                        RevCommit parent = revWalk.parseCommit(commit.getParent(0).getId());
                        for (DiffEntry diff : diffBetween(repository, parent, commit)) {
                            fileChangeCounts.merge(diff.getNewPath(), 1, Integer::sum);
                        }
                    }
                }
            }

            Map<String, Integer> topFiles = fileChangeCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

            return new RepoStats(totalCommits, contributors.size(), topFiles);
        }
    }

    private List<DiffEntry> diffBetween(Repository repository, RevCommit oldCommit, RevCommit newCommit) throws IOException {
        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);
            return diffFormatter.scan(oldCommit.getTree(), newCommit.getTree());
        }
    }
}