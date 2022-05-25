import b_tree.BTree;

public class Main {
    public static void main(String[] args) {
        BTree<Integer, String> bTree = new BTree<>(4);
        bTree.insert(1, "loay");
        bTree.insert(2, "loay1");
        bTree.insert(3, "loay2");
        bTree.insert(4, "loay3");
        bTree.insert(10, "loay4");
        bTree.insert(17, "loay5");
        bTree.insert(19, "loay6");
        bTree.insert(20, "loay7");
        bTree.insert(34, "loay8");
        bTree.insert(37, "loay9");
        bTree.insert(38, "loay10");
        bTree.insert(45, "loay11");
        bTree.insert(96, "loay");
        bTree.insert(100, "loay");
    }
}