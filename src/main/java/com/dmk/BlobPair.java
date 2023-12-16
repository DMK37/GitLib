package com.dmk;

import java.util.Objects;

record BlobPair(Blob blob, String filename) {

    @Override
    public String toString() {
        return "blob " + blob.getHash() + ' ' + filename;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;
        if (object instanceof BlobPair) {
            sameSame = Objects.equals(this.blob.getHash(), ((BlobPair) object).blob.getHash())
                    && Objects.equals(this.filename, ((BlobPair) object).filename);
        }
        return sameSame;
    }
}
