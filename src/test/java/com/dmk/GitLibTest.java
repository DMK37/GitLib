package com.dmk;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GitLibTest {

    private static String readFileAsString(String fileName) throws IOException {
        String data = "";
        data = new String(
                Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    @Test
    void numberOfBlobsShouldBeTwo() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources");
        gitLib.add("files/file1.txt");
        gitLib.add("files/file2.txt");
        gitLib.add("files/file1.txt");
        gitLib.commit("dmk", "first commit");
        var list = gitLib.getObjects();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Blob) {
                count++;
            }
        }
        assertEquals(2, count);
    }

    @Test
    void hashForBlobWithSameDataAlwaysOne() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources");
        gitLib.add("files/file1.txt");
        gitLib.commit("dmk", "first commit");
        String data = null;
        try {
            data = readFileAsString(System.getProperty("user.dir") + "/src/test/resources/files/file1.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Blob blob = new Blob(data);
        var list = gitLib.getObjects();
        for (GitObject gitObject : list) {
            if (gitObject instanceof Blob) {
                assertEquals(blob.getHash(), gitObject.getHash());
                return;
            }
        }
    }

    @Test
    void file1OnlyCreatedOnceButUsedInDifferentFolders() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources");
        gitLib.add("files/file1.txt");
        gitLib.add("files/subdir/file1.txt");
        gitLib.commit("dmk", "first commit");
        var list = gitLib.getObjects();
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Blob) {
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    void mainTreeCreatedCorrectly() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources");
        gitLib.add("*");
        gitLib.commit("dmk", "first commit");
        Commit commit = gitLib.listCommits().get(0);
        String data = null;
        try {
            data =
                    readFileAsString(System.getProperty("user.dir") + "/src/test/resources/files/subdir/file1.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Blob file1 = new Blob(data);
        try {
            data =
                    readFileAsString(System.getProperty("user.dir") + "/src/test/resources/files/file2.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Blob file2 = new Blob(data);
        List<BlobPair> subdirBlobs = new ArrayList<>();
        subdirBlobs.add(new BlobPair(file1, "file1.txt"));
        Tree subdir = new Tree(subdirBlobs, null);

        List<BlobPair> filesBlobs = new ArrayList<>();
        filesBlobs.add(new BlobPair(file2, "file2.txt"));
        filesBlobs.add(new BlobPair(file1, "file1.txt"));
        filesBlobs.add(new BlobPair(file1, "file3.txt"));
        List<TreePair> filesTrees = new ArrayList<>();
        filesTrees.add(new TreePair(subdir, "subdir"));
        Tree files = new Tree(filesBlobs, filesTrees);

        List<TreePair> resourcesTrees = new ArrayList<>();
        resourcesTrees.add(new TreePair(files, "files"));
        Tree resourcesTree = new Tree(null, resourcesTrees);

        assertEquals(resourcesTree.getHash(), commit.getTreeHash());
    }

    @Test
    void findCommitByHashShouldReturnCorrectCommit() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources");
        gitLib.add("*");
        gitLib.commit("dmk", "first commit");
        Commit commit = gitLib.listCommits().get(0);
        assertEquals(commit, gitLib.findCommitByHash(commit.getHash()));
    }

    @Test
    void filesWithSameDataButDifferentNamesShouldHaveSameHash() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources/files");
        gitLib.add("file1.txt");
        gitLib.add("file3.txt");
        gitLib.commit("dmk", "first commit");
        Commit commit = gitLib.listCommits().get(0);
        var list = commit.getTree().getBlobs();

        assertEquals(list.get(0).blob().getHash(), list.get(1).blob().getHash());
    }

    @Test
    void findCommitByMetadataShouldReturnCorrectCommit() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources");
        gitLib.add("*");
        gitLib.commit("dmk", "first commit");
        Commit commit = gitLib.listCommits().get(0);
        assertEquals(commit, gitLib.findCommitByMetadata(commit.getAuthor(), commit.getMessage(), commit.getTime(),
                commit.getParentCommit() == null ? null : commit.getParentCommit().getHash(), commit.getTreeHash()));
    }

    @Test
    void newBlobCreatedAfterFileDataChanged() {
        GitLib gitLib = new GitLib(System.getProperty("user.dir") + "/src/test/resources");
        gitLib.add("files/file1.txt");
        gitLib.commit("dmk", "first commit");

        String filePath = System.getProperty("user.dir") + "/src/test/resources/files/file1.txt";
        String newContent = "This is the new content of the file.";

        try {
            Files.writeString(Path.of(filePath), newContent, StandardOpenOption.TRUNCATE_EXISTING);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gitLib.add("files/file1.txt");
        gitLib.commit("dmk", "second commit");
        var list = gitLib.listCommits();
        Blob b1 = list.get(0).getTree().getTrees().get(0).tree().getBlobs().get(0).blob();
        Blob b2 = list.get(1).getTree().getTrees().get(0).tree().getBlobs().get(0).blob();
        try {
            Files.writeString(Path.of(filePath), "Hello World!", StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertNotEquals(b1.getData(), b2.getData());
    }
}