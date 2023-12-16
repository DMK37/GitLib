package com.dmk;

import java.util.*;

class TreeBuilder {
    private final HashMap<String, HashMap<String, Blob>> blobsUnderPath;

    private final HashMap<String, TreePair> treesUnderPath;

    private final HashMap<String, UnfinishedTree> unfinishedTrees;

    public TreeBuilder() {
        blobsUnderPath = new HashMap<>();
        treesUnderPath = new HashMap<>();
        unfinishedTrees = new HashMap<>();
    }

    public void addBlob(Blob blob, String filename) {
        int idx = filename.lastIndexOf('/');
        String extrudedFilename;
        String path;
        if (idx == -1) {
            extrudedFilename = filename;
            path = "";
        } else {
            extrudedFilename = filename.substring(idx + 1);
            path = filename.substring(0, idx);
        }
        if(unfinishedTrees.containsKey(path)) {
            unfinishedTrees.get(path).addBlob(blob, extrudedFilename);
        } else {
            UnfinishedTree unfinishedTree = new UnfinishedTree(path.substring(path.lastIndexOf('/') + 1));
            unfinishedTree.addBlob(blob, extrudedFilename);
            unfinishedTrees.put(path, unfinishedTree);
        }

        if (blobsUnderPath.containsKey(path)) {
            if (blobsUnderPath.get(path).containsKey(extrudedFilename)) {
                if (!Objects.equals(blobsUnderPath.get(path).get(extrudedFilename).getHash(), blob.getHash()))
                    blobsUnderPath.get(path).put(extrudedFilename, blob);
            } else
                blobsUnderPath.get(path).put(extrudedFilename, blob);
        } else {
            HashMap<String, Blob> map = new HashMap<>();
            map.put(extrudedFilename, blob);
            blobsUnderPath.put(path, map);
        }
    }

    public Tree build(HashMap<String, GitObject> objects) {
        List<Map.Entry<String, UnfinishedTree>> list = unfinishedTrees.entrySet().stream().sorted((o1, o2) ->
        {
            int a = o1.getKey().split("/").length;
            int b = o2.getKey().split("/").length;
            return Integer.compare(a, b);
        }).toList();

        for(int i = 0 ; i < list.size(); i++) {
            Map.Entry<String, UnfinishedTree> entry = list.get(i);

            if(!unfinishedTrees.containsKey(""))
                unfinishedTrees.put("", new UnfinishedTree(""));
            if(entry.getKey().isEmpty())
                continue;
            if(entry.getKey().lastIndexOf('/') == -1) {
                unfinishedTrees.get("").addUnfinishedTree(entry.getValue());
                continue;
            }
            String parentPath = entry.getKey().substring(0, entry.getKey().lastIndexOf('/'));
            UnfinishedTree prevTree = entry.getValue();
            while(!unfinishedTrees.containsKey(parentPath)) {
                int idx = parentPath.lastIndexOf('/') + 1;
                UnfinishedTree tree = new UnfinishedTree(parentPath.substring(idx));
                tree.addUnfinishedTree(prevTree);
                unfinishedTrees.put(parentPath,tree);
                prevTree = tree;
                if(idx == 0) {
                    parentPath = "";
                } else {
                    parentPath = parentPath.substring(0, parentPath.lastIndexOf('/'));
                }
            }
            unfinishedTrees.get(parentPath).addUnfinishedTree(prevTree);
        }

        return unfinishedTrees.get("").build(objects);
    }
}
