package com.dmk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class GitLib {
    private final TreeBuilder mainTree;
    private final List<Commit> commitList;
    private final HashMap<String, GitObject> objects;
    private final String path;

    public GitLib(String path) {
        mainTree = new TreeBuilder();
        commitList = new ArrayList<>();
        objects = new HashMap<>();
        this.path = path;
    }

    List<GitObject> getObjects() {
        return new ArrayList<>(objects.values());
    }

    public void add(String filename) {
        if("*".equals(filename)) {
            ///

            try {
                Files.walk(Paths.get(path))
                        .filter(Files::isRegularFile)
                        .filter(Files::isReadable)
                        .filter(filePath -> !filePath.getFileName().toString().startsWith("."))
                        //.filter(filePath -> !filePath.toFile().toString().contains("."))
                        .forEach(filePath -> {
                            String fname = filePath.getFileName().toString();
                            String data = null;
                            try {
                                data = readFileAsString(filePath.toString());
                                String pth = filePath.toString().substring(path.length() + 1);
                                Blob blob = new Blob(data);
                                if(objects.containsKey(blob.getHash())) {
                                    mainTree.addBlob((Blob)objects.get(blob.getHash()), pth);
                                } else {
                                    mainTree.addBlob(blob, pth);
                                }
                            } catch (IOException e) {
                                System.out.println("Error reading file");
                            }
                        });
            } catch (IOException e) {
                System.out.println("Error reading files");
            }
        }
        else {
           String projectPath = path;
            String data = null;
            try {
                data = readFileAsString(projectPath + '/' + filename);
                Blob blob = new Blob(data);
                if(objects.containsKey(blob.getHash())) {
                    // add existing object to treeBuilder
                    // we are adding it because it can have different path form existing blob
                    mainTree.addBlob((Blob)objects.get(blob.getHash()), filename);
                } else {
                    // add new blob to treeBuilder
                    mainTree.addBlob(blob, filename);
                }
            } catch (IOException e) {
                System.out.println("Error reading file");
            }
        }
    }

    private static String readFileAsString(String fileName) throws IOException {
        String data = "";
        data = new String(
                Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    public void commit(String author, String message) {
        // build tree
        Tree t = mainTree.build(objects);
        // create commit
        if (commitList.isEmpty()) {
            commitList.add(new Commit(author, message, t, null));
        } else {
            commitList.add(new Commit(author, message, t, commitList.get(commitList.size() - 1)));
        }
    }

    public List<Commit> listCommits() {
        for(int i = 0; i < commitList.size(); i++) {
            System.out.printf(commitList.get(i).toString() + "\n");
        }
        return new ArrayList<>(commitList);
    }

    public Commit findCommitByHash(String hash) {
        for (Commit commit : commitList) {
            if (Objects.equals(commit.getHash(), hash)) {
                System.out.printf(commit.toString() + "\n");
                return commit;
            }
        }
        return null;
    }

    public Commit findCommitByMetadata(String author, String message, LocalDateTime time, String parentHash, String treeHash) {
        for (Commit commit : commitList) {
            Commit parentCommit = commit.getParentCommit();
            if (Objects.equals(commit.getAuthor(), author) &&
                    Objects.equals(commit.getMessage(), message) &&
                    Objects.equals(commit.getTime(), time) &&
                    Objects.equals(parentCommit == null ? null : parentCommit.getHash(), parentHash) &&
                    Objects.equals(commit.getTree().getHash(), treeHash)) {
                System.out.printf(commit.toString() + "\n");
                return commit;
            }
        }
        return null;
    }

    public void showObjectByHash(String hash) {
        if(objects.containsKey(hash)) {
            System.out.printf(objects.get(hash).toString() + "\n");
        } else {
            System.out.println("Object not found");
        }
    }
}
