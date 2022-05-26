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
        System.out.println("38 ->" + bTree.delete(38));
        System.out.println("19 ->" + bTree.delete(19));
        System.out.println("45 ->" + bTree.delete(45));
        System.out.println("17 ->" + bTree.delete(17));
        System.out.println("2 ->" +bTree.delete(2));
        System.out.println("34 ->" + bTree.delete(34));
        System.out.println("20 ->" + bTree.delete(20));
        System.out.println("100 ->" + bTree.delete(100));
        System.out.println("3 ->" + bTree.delete(3));
        System.out.println("96 ->" +bTree.delete(96));
        System.out.println("4 ->" + bTree.delete(4));
        System.out.println("37 ->" +bTree.delete(37));
        System.out.println("1 ->" +bTree.delete(1));
        System.out.println("10 ->" +bTree.delete(10));
    }
}