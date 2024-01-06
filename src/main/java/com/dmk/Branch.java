package com.dmk;

import java.util.ArrayList;
import java.util.List;

class Branch {
    private final String name;
    private final List<Commit> commits;

    private final Commit parentCommit;

    private final Branch parentBranch;

    public Branch(String name, Branch parentBranch) {
        this.name = name;
        this.commits = new ArrayList<>();
        this.parentBranch = parentBranch;
        if (parentBranch == null) {
            this.parentCommit = null;
        } else {
            this.parentCommit = parentBranch.getLastCommit();
        }
    }

    public String getName() {
        return name;
    }

    public Commit getLastCommit() {
        if (commits.isEmpty()) return null;
        return commits.get(commits.size() - 1);
    }

    public Commit addCommit(String author, String message, Tree tree) {
        Commit commit;
        if (commits.isEmpty()) {
            commit = new Commit(author, message, tree, parentCommit);
        } else {
            commit = new Commit(author, message, tree, commits.get(commits.size() - 1));
        }
        commits.add(commit);
        return commit;
    }

    public List<Commit> listCommits() {
        for (Commit commit : commits) {
            System.out.printf(commit.toString() + "\n");
        }
        return new ArrayList<>(commits);
    }

    public String getParentBranchName() {
        if (parentBranch == null) return null;
        return parentBranch.getName();
    }

    public Commit getParentCommit() {
        return parentCommit;
    }


}
