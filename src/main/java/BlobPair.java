record BlobPair(Blob blob, String filename) {

    @Override
    public String toString() {
        return "blob " + blob.getHash() + ' ' + filename;
    }
}
