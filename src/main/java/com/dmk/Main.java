package com.dmk;

public class Main {
    public static void main(String[] args) {
        GitLib git = new GitLib(System.getProperty("user.dir") );
        //git.add("*");
        git.add("pom.xml");
        git.add("src/main/java/com/dmk/Blob.java");
        git.add("src/main/java/com/dmk/BlobPair.java");
        git.commit("dmk", "first commit");
        //git.add("pom.xml");
        git.add("*");
        git.commit("dmk", "second  commit");
        var ls = git.listCommits();
        Commit commit = ls.get(1);
        git.findCommitByHash(commit.getHash());
        git.findCommitByMetadata(commit.getAuthor(), commit.getMessage(), commit.getTime(), commit.getParentCommit() == null ? null : commit.getParentCommit().getHash(), commit.getTreeHash());
    }
}
