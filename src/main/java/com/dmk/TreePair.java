package com.dmk;

record TreePair(Tree tree, String filename) {

    @Override
    public String toString() {
        return "tree " + tree.getHash() + ' ' + filename;
    }
}
