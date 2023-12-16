package com.dmk;

import org.apache.commons.codec.digest.DigestUtils;

class Blob extends GitObject{
    private final String data;

    private String hash;

    Blob(String data) {
        this.data = data;
        setHash();
    }

    private void setHash() {
        hash = DigestUtils.sha1Hex("blob " + data.length() + "\0" + data);
    }

    String getData() {
        return data;
    }

    @Override
    String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return data;
    }
}
