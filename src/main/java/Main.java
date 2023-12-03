import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Blob blob = new Blob("hello world");
        Blob blob2 = new Blob("shs");
        List<BlobPair> ls = new ArrayList<>();
        ls.add(new BlobPair(blob, "hello"));
        List<BlobPair> ls2 = new ArrayList<>();
        ls2.add(new BlobPair(blob2, "sec"));
        Tree tree2 = new Tree(ls2, null);
        List<TreePair> ls3 = new ArrayList<>();
        ls3.add(new TreePair(tree2, "my tree"));
        Tree tree = new Tree(ls, ls3);
        Commit commit = new Commit("dmk", "first commit", tree, null);
        System.out.println(tree);
        System.out.println(tree2);
        System.out.println(commit);
        System.out.println(System.getProperty("user.dir"));
    }
}
