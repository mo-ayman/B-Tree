package b_tree;

import org.w3c.dom.Node;

import java.util.List;

public class BTree <K extends Comparable<K>, V> implements IBTree<K, V>{
    private BTreeNode<K, V> root;
    private int order; //// order is max no. of children within a node

    public BTree(int order) {
        this.order = order;
    }

    @Override
    public int getMinimumDegree() {
        return (int) Math.ceil( order / 2.0);
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return this.root;
    }

    @Override
    public void insert(K key, V value) {

    }

    @Override
    public V search(K key) {
        BTreeNode<K, V> gotNode = searchNode(key, this.root);
        List<K> keys = gotNode.getKeys();
        int index = binarySearch(0, keys.size() - 1, key, keys);
        if(index < 0) return null;
        return gotNode.getValues().get(index);
    }

    private BTreeNode<K, V> searchNode(K key, BTreeNode<K, V> node){ /// all the tree can use this
        List<K> keysOfNode = node.getKeys();
        K keyOfNode;
        int compRes = 0, index = 0;
        while (index < keysOfNode.size()){
            keyOfNode = keysOfNode.get(index);
            if(keyOfNode != null) compRes = key.compareTo(keyOfNode);
            if(key == null || (compRes < 0 && !node.isLeaf()))
                return searchNode(key, (BTreeNode<K, V>) node.getChildren().get(index));
            else if(key == null || (compRes < 0 && node.isLeaf())) return node;
            else if(compRes > 0 && index < keysOfNode.size() - 1) index++;
            else if(compRes > 0 && index == keysOfNode.size() - 1 && !node.isLeaf())
                return searchNode(key, (BTreeNode<K, V>) node.getChildren().get(index+1));
            else if(compRes > 0 && index == keysOfNode.size() - 1 && node.isLeaf())
                return node;
            else return node;
        }
        return null;
    }
    private int binarySearch(int low, int high, K key, List<K> keys){
        if(low > high) return -1;
        int mid = low + (high - low) / 2;
        int res = key.compareTo(keys.get(mid));
        if(res > 0) return binarySearch(mid+1, high, key, keys);
        else if(res < 0) return binarySearch(low, mid - 1, key, keys);
        else return mid;
    }

    @Override
    public boolean delete(K key) {
        return false;
    }
}
