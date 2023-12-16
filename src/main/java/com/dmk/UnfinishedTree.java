package com.dmk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

class UnfinishedTree {
    private List<BlobPair> blobs;
    private List<TreePair> trees;
    private List<UnfinishedTree> unfinishedTrees;
    private String filename;
    UnfinishedTree(String filename) {
        blobs = new ArrayList<>();
        trees = new ArrayList<>();
        unfinishedTrees = new ArrayList<>();
        this.filename = filename;
    }

    UnfinishedTree(List<BlobPair> blobs, List<TreePair> trees, String filename) {
        if(blobs == null) blobs = new ArrayList<>();
        if(trees == null) trees = new ArrayList<>();
        this.blobs = blobs;
        //this.trees = trees;
        this.filename = filename;
    }
    UnfinishedTree(List<BlobPair> blobs, String filename) {
        this.blobs = blobs;
        this.trees = new ArrayList<>();
        this.filename = filename;
    }

    public void addBlob(Blob blob, String filename) {
        BlobPair blobPair = new BlobPair(blob, filename);
        for (int i = 0; i < blobs.size(); i++) {
            if(Objects.equals(blobs.get(i).filename(), blobPair.filename())) {
                blobs.set(i, blobPair);
                return;
            }
        }
            blobs.add(new BlobPair(blob, filename));
    }

    public void addUnfinishedTree(UnfinishedTree unfinishedTree) {
        for(int i = 0; i < unfinishedTrees.size(); i++) {
            if(Objects.equals(unfinishedTree.getFilename(), unfinishedTrees.get(i).getFilename())) {
                unfinishedTrees.set(i, unfinishedTree);
                return;
            }
        }
        unfinishedTrees.add(unfinishedTree);
    }

    public String getFilename() {
        return filename;
    }

    public Tree build(HashMap<String, GitObject> objects) {
        for (BlobPair blobPair : blobs) {
            if (!objects.containsKey(blobPair.blob().getHash())) {
                Blob blob = blobPair.blob();
                objects.put(blob.getHash(), blob);
            }
        }
        for (int i = 0; i < unfinishedTrees.size(); i++) {
            trees.add(new TreePair(unfinishedTrees.get(i).build(objects), unfinishedTrees.get(i).getFilename()));
        }
        Tree tree =  new Tree(blobs, trees);
        if(objects.containsKey(tree.getHash()))
        {
            tree = (Tree) objects.get(tree.getHash());
        }
        else {
            objects.put(tree.getHash(), tree);
        }
        blobs = new ArrayList<>(blobs);
        trees = new ArrayList<>();
        return tree;
    }
}
