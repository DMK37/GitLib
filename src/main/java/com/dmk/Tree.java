package com.dmk;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;



class Tree extends GitObject{
    private final List<BlobPair> blobs;
    private final List<TreePair> trees;

    private String hash;

    Tree(List<BlobPair> blobs, List<TreePair> trees) {
        this.blobs = blobs;
        this.trees = trees;
        setHash();
    }

    private void setHash() {
        StringBuilder builder = new StringBuilder();
        if(blobs != null) {
            for (BlobPair blobPair : blobs) {
                builder.append(blobPair.blob().getHash());
                builder.append(' ');
            }
        }
        if(trees != null) {
            for (TreePair treePair : trees) {
                builder.append(treePair.tree().getHash());
                builder.append(' ');
            }
        }

        if(!builder.isEmpty())
            builder.deleteCharAt(builder.length() - 1);

        hash = DigestUtils.sha1Hex("tree " + builder.length() + "\0" + builder);
    }

    @Override
    String getHash() {
        return hash;
    }

    List<TreePair> getTrees() {
        return new ArrayList<>(trees);
    }

    List<BlobPair> getBlobs() {
        return new ArrayList<>(blobs);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(blobs != null) {
            for (BlobPair blob : blobs) {
                builder.append(blob);
                builder.append('\n');
            }
        }
        if(trees != null) {
            for (TreePair tree : trees) {
                builder.append(tree);
                builder.append('\n');
            }
        }
        if(!builder.isEmpty())
            builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
