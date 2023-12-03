import org.apache.commons.codec.digest.DigestUtils;

class Blob {
    private final String data;

    private String hash;

    public Blob(String data) {
        this.data = data;
        setHash();
    }

    private void setHash() {
        hash = DigestUtils.sha1Hex("blob " + data.length() + "\0" + data);
    }

    public String getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return data;
    }
}
