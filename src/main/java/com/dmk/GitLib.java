package com.dmk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**
 * GitLib class is main class of this project, which user will use to interact with git clone
 */
public class GitLib {
    private final TreeBuilder mainTree;
    private final List<Commit> commitList;
    private final HashMap<String, GitObject> objects;

    private final HashMap<String, Commit> commitMap;
    private final String path;

    private String currentBranch = "master";

    private HashMap<String, Branch> branches = new HashMap<>();

    /**
     * Creates new GitLib object, similar to git init
     *
     * @param path path to project root
     */
    public GitLib(String path) {
        mainTree = new TreeBuilder();
        commitList = new ArrayList<>();
        objects = new HashMap<>();
        this.path = path;
        commitMap = new HashMap<>();
        branches.put(currentBranch, new Branch(currentBranch, null));
    }

    List<GitObject> getObjects() {
        return new ArrayList<>(objects.values());
    }

    /**
     * Adds file to the stage
     *
     * @param filename file to add, path is relative to project root
     */
    public void add(String filename) {
        if ("*".equals(filename)) {
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
                                if (objects.containsKey(blob.getHash())) {
                                    mainTree.addBlob((Blob) objects.get(blob.getHash()), pth);
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
        } else {
            String projectPath = path;
            String data = null;
            try {
                data = readFileAsString(projectPath + '/' + filename);
                Blob blob = new Blob(data);
                if (objects.containsKey(blob.getHash())) {
                    // add existing object to treeBuilder
                    // we are adding it because it can have different path form existing blob
                    mainTree.addBlob((Blob) objects.get(blob.getHash()), filename);
                } else {
                    // add new blob to treeBuilder
                    mainTree.addBlob(blob, filename);
                }
            } catch (IOException e) {
                System.out.println("Error reading file");
            }
        }
    }

    /**
     * reads file
     *
     * @param fileName path to file
     * @return file content as string
     * @throws IOException
     */
    private static String readFileAsString(String fileName) throws IOException {
        String data = "";
        data = new String(
                Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    /**
     * Creates commit
     *
     * @param author  author of commit
     * @param message message of commit
     */
    public void commit(String author, String message) {
        // build tree
        Tree t = mainTree.build(objects);
        // create commit
        Commit commit = branches.get(currentBranch).addCommit(author, message, t);
        commitList.add(commit);
        commitMap.put(commit.getHash(), commit);
    }

    /**
     * Lists all commits
     *
     * @return list of commits as new arraylist
     */
    public List<Commit> listCommits() {
        return branches.get(currentBranch).listCommits();
    }

    /**
     * find commit by hash
     *
     * @return commit or null if not found
     */
    public Commit findCommitByHash(String hash) {
        if (commitMap.containsKey(hash)) {
            System.out.println(commitMap.get(hash).toString());
            return commitMap.get(hash);
        }
        return null;
    }

    /**
     * find commit by metadata
     *
     * @return commit or null if not found
     */
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

    /**
     * print tree or blob by hash
     */
    public void showObjectByHash(String hash) {
        if (objects.containsKey(hash)) {
            System.out.printf(objects.get(hash).toString() + "\n");
        } else {
            System.out.println("Object not found");
        }
    }

    public void addBranch(String branchName) {
        branches.put(branchName, new Branch(branchName, branches.get(currentBranch)));
    }

    public void switchBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            currentBranch = branchName;
        } else {
            System.out.println("Branch not found");
        }
    }

    Branch getCurrentBranch() {
        return branches.get(currentBranch);
    }
}
