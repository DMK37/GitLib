package com.dmk;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;

public class Commit {
    private final String author;
    private final String message;
    private final LocalDateTime time;

    private final Tree tree;

    private final Commit parentCommit;
    private String hash;

    public String getAuthor() {
        return author;
    }

    public String getTreeHash() {
        return tree.getHash();
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    Tree getTree() {
        return tree;
    }

    public Commit getParentCommit() {
        return parentCommit;
    }

    public String getHash() {
        return hash;
    }


    Commit(String author, String message, Tree tree, Commit parentCommit) {
        this.author = author;
        this.message = message;
        this.tree = tree;
        this.parentCommit = parentCommit;
        time = LocalDateTime.now();
        setHash();
    }

    private void setHash() {
        StringBuilder builder = new StringBuilder();
        builder.append(tree.getHash());
        builder.append(' ');
        if (parentCommit != null) {
            builder.append(parentCommit.getHash());
            builder.append(' ');
        }

        builder.append(author);
        builder.append(' ');
        builder.append(time.toString());
        builder.append(' ');
        builder.append(message);
        hash = DigestUtils.sha1Hex("commit " + builder.length() + '\0' + builder);
    }

    @Override
    public String toString() {
        String s2 = "null";
        if (parentCommit != null) {
            s2 = parentCommit.getHash();
        }
        return "commit " +
                hash +
                '\n' +
                "tree " +
                tree.getHash() +
                '\n' +
                "parent " +
                s2 + '\n' +
                "author " +
                author +
                '\n' +
                "time " +
                time.toString() +
                '\n' +
                message;
    }
}
