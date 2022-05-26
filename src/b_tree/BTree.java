package b_tree;

import org.w3c.dom.Node;

import java.sql.Array;
import java.util.*;

public class BTree <K extends Comparable<K>, V> implements IBTree<K, V>{
    private BTreeNode<K, V> root;
    private final int order; //// order is max no. of children within a node

    public BTree(int order) {
        this.order = order;
        this.root = new BTreeNode<>(order, null);
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
        Item<K, V> newItem = new Item<>(key, value);
        BTreeNode<K, V> targetNode = searchNode(key, root);
        List<Item<K, V>> listItems = targetNode.getItems();
        listItems.add(newItem);
        if (targetNode.violatesMaxNoOfKeys())
            splitNodeAndInsertMedianInTheParent(targetNode);
    }
    
    private void splitNodeAndInsertMedianInTheParent(BTreeNode<K, V> targetNode) {

        BTreeNode<K, V> parentNode = targetNode.getParent();
        List<IBTreeNode<K, V>> children = targetNode.getChildren();
        boolean hasChildren = !targetNode.isLeaf();
        List<Item<K, V>> items = targetNode.getItems();

        int indexMedian = (items.size() - 1) / 2;
        Item<K, V> medianItem = items.get(indexMedian);
        if (parentNode == null) {
            parentNode = new BTreeNode<>(order, null);
            List<IBTreeNode<K, V>> child = new ArrayList<>();
            child.add(targetNode);
            parentNode.setChildren(child);
            targetNode.setParent(parentNode);
            root = parentNode;
        }
        List<Item<K, V>> parentItems = parentNode.getItems();
        parentItems.add(medianItem);
        parentItems.sort(Comparator.comparing(Item::getKey));

        List<Item<K, V>> newItemsList1 = new ArrayList<>(items.subList(0, indexMedian));
        List<Item<K, V>> newItemsList2 = new ArrayList<>(items.subList(indexMedian + 1, items.size()));

        BTreeNode<K, V> leftNode;
        BTreeNode<K, V> rightNode;

        if (hasChildren) {
            List<IBTreeNode<K, V>> children1 = new ArrayList<>(children.subList(0, indexMedian + 1));
            List<IBTreeNode<K, V>> children2 = new ArrayList<>(children.subList(indexMedian + 1, children.size()));
            leftNode = new BTreeNode<>(order, parentNode, newItemsList1, children1);
            rightNode = new BTreeNode<>(order, parentNode, newItemsList2, children2);
            for (IBTreeNode<K, V> child : children1)
                ((BTreeNode<K, V>)child).setParent(leftNode);
            for (IBTreeNode<K, V> child : children2) {
                ((BTreeNode<K, V>)child).setParent(rightNode);
            }
        }
        else {
            leftNode = new BTreeNode<>(order, parentNode, newItemsList1);
            rightNode = new BTreeNode<>(order, parentNode, newItemsList2);
        }

        int childIndexToBeRemoved = targetNode.getIndexWithinParent();
        parentNode.getChildren().set(childIndexToBeRemoved, rightNode);
        parentNode.getChildren().add(childIndexToBeRemoved, leftNode);

        if (parentItems.size() == order)
            splitNodeAndInsertMedianInTheParent(parentNode);
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
        if(keysOfNode.isEmpty()) return node;
        K keyOfNode;
        int compRes = 0, index = 0;
        while (index < order){
            if (index == keysOfNode.size()) {
                if (node.isLeaf())
                    return node;
                else
                    return searchNode(key, (BTreeNode<K, V>) node.getChildren().get(index));
            }
            keyOfNode = keysOfNode.get(index);
            if(keyOfNode != null) compRes = key.compareTo(keyOfNode);
            if(keyOfNode == null || compRes < 0) {
                if (!node.isLeaf())
                    return searchNode(key, (BTreeNode<K, V>) node.getChildren().get(index));
                else return node;
            }
            else if(compRes > 0 && index < order - 1) index++;
            else if(compRes > 0 && index == order - 1) {
                if (!node.isLeaf())
                    return searchNode(key, (BTreeNode<K, V>) node.getChildren().get(index+1));
                else 
                    return node;
            }
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
    public boolean delete(K key){
        BTreeNode<K, V> gotNode = searchNode(key, root);
        int indexOfKey = binarySearch(0, gotNode.getKeys().size() - 1, key, gotNode.getKeys());
        if (indexOfKey < 0) return false; ///not found or already deleted
        int indexWithinParent = 0;
        if(gotNode != root) indexWithinParent = gotNode.getIndexWithinParent();
        //1st: solving key-less branches after deletion
        if (!gotNode.isLeaf()) ///try to replace the key with its predecessor or successor
            return replaceWithPredece_ssorOrSucce_ssor(gotNode, key, indexOfKey);
        else if(gotNode.isLeaf()) {
            boolean done = borrowFromAsibling(gotNode, key, indexWithinParent);
            if(!done) done = mergeWithSibling(gotNode, key, indexWithinParent);
            return done;
        }
        if(this.root.getItems().size() == 0 && this.root.isLeaf()) this.root = null;
        return true;
    }
    private boolean replaceWithPredece_ssorOrSucce_ssor(BTreeNode<K, V> node, K key, int index){
        ///index of key within node
        BTreeNode<K, V> successor = node.getSuccessor(index);
        BTreeNode<K, V> predecessor = node.getPredecessor(index);
        BTreeNode<K, V> _cessor ;
        int i;
        if(!successor.hasMinNoOfKeys()){
            _cessor = successor;
            i = 0;
        }
        else{
            _cessor = predecessor;
            i = predecessor.getItems().size() - 1;
        }
        //attempt to replace with successor or predecessor key
        List<K> keys = node.getKeys();
        List<V> values = node.getValues();
        K key1 = _cessor.getKeys().get(i);
        V val1 = _cessor.getValues().get(i);
        delete(key1);
        node = searchNode(key, root);
        keys = node.getKeys();
        values = node.getValues();
        index = binarySearch(0, keys.size() - 1, key, keys);
        keys.set(index, key1);
        values.set(index, val1);
        node.setKeys(keys); node.setValues(values);
        return true; /// deleted properly
    }

    private boolean borrowFromAsibling(BTreeNode<K, V> node, K key, int indexWithinParent){
        //// merging left and right children....
        if(node == root) {
            List<K> keys = node.getKeys(); List<V> values = node.getValues();
            keys.remove(key); values.remove(node.getKeys().indexOf(key) + 1 - 1);
            node.setKeys(keys); node.setValues(values);
            return true;
        }
        BTreeNode<K, V> leftSibling = node.getLeftSibling(), rightSibling = node.getRightSibling(),
        sibling = leftSibling;
        int indexWithinSib = 0, parentKeyInd = indexWithinParent - 1, indexWithinNode = 0;
        if(leftSibling != null) indexWithinSib = leftSibling.getKeys().size() - 1;

        if(leftSibling == null || leftSibling.hasMinNoOfKeys()) {
            sibling = rightSibling;
            indexWithinSib = 0;
            indexWithinNode = node.getKeys().size() - 1;
            if(indexWithinNode < 0) indexWithinNode = 0;
            parentKeyInd = indexWithinParent;
        }
        if(rightSibling == null || rightSibling.hasMinNoOfKeys())
            return false; ///// need to merge with one of them
        List<K> sibKeys = sibling.getKeys() ;
        List<V> sibVals = sibling.getValues();
        List<IBTreeNode<K, V>> sibChildren = sibling.getChildren();

        List<K> parentKeys = node.getParent().getKeys();
        List<V> parentVals = node.getParent().getValues();

        List<IBTreeNode<K, V>> children = node.getChildren();
        List<K> keys = node.getKeys();
        List<V> values = node.getValues();

        if(key != null){
            boolean noViolation = !node.hasMinNoOfKeys();
            values.remove(node.getKeys().indexOf(key));
            keys.remove(key);
            node.setKeys(keys);
            node.setValues(values);
            if(noViolation) return true;
        }

        keys.add(indexWithinNode, parentKeys.get(parentKeyInd));
        values.add(indexWithinNode, parentVals.get(parentKeyInd));
        parentKeys.set(parentKeyInd, sibKeys.get(indexWithinSib));
        parentVals.set(parentKeyInd, sibVals.get(indexWithinSib));
        sibKeys.remove(indexWithinSib + 1 - 1);
        sibVals.remove(indexWithinSib + 1 - 1);

        if(indexWithinNode == node.getKeys().size() - 1) indexWithinNode++;

        if(!children.isEmpty()){
            if (sibling == rightSibling)
                children.add(sibChildren.get(indexWithinSib));
            else
                children.add(0, sibChildren.get(indexWithinSib));
            ((BTreeNode<K, V>)sibChildren.get(indexWithinSib)).setParent(node);
            sibChildren.remove(indexWithinSib + 1 - 1);
        }

        node.setKeys(keys); node.setValues(values); node.setChildren(children);
        node.getParent().setKeys(parentKeys); node.getParent().setValues(parentVals);
        sibling.setKeys(sibKeys); sibling.setValues(sibVals); sibling.setChildren(sibChildren);
        return true;
    }
    private boolean mergeWithSibling(BTreeNode<K, V> node, K key, int indexWithinParent){
        if(node == root) {
            List<K> keys = node.getKeys(); List<V> values = node.getValues();
            keys.remove(key); values.remove(node.getKeys().indexOf(key) + 1 - 1);
            node.setKeys(keys); node.setValues(values);
            return true;
        }
        BTreeNode<K, V> leftSibling = node.getLeftSibling(), rightSibling = node.getRightSibling(), sibling;
        sibling = leftSibling;
        if(leftSibling == null)
            sibling = rightSibling;
        List<K> sibKeys = sibling.getKeys(); List<V> sibVals = sibling.getValues();
        List<K> parentKeys = node.getParent().getKeys(); List<V> parentVals = node.getParent().getValues();
        List<K> keys = node.getKeys(); List<V> values = node.getValues();
        List<IBTreeNode<K, V>> children = node.getChildren();
        List<IBTreeNode<K, V>> sibChildren = sibling.getChildren();

        if(keys.contains(key)) { //////////// needs to be common
            boolean noViolation = !node.hasMinNoOfKeys();
            values.remove(keys.indexOf(key) - 1 + 1); keys.remove(key);
            node.setKeys(keys); node.setValues(values);
            if (noViolation) return true;
        }
        boolean fixParent = node.getParent().hasMinNoOfKeys();
        if(sibling == leftSibling){
            sibKeys.add(parentKeys.get(indexWithinParent - 1));
            sibVals.add(parentVals.get(indexWithinParent - 1));
            parentKeys.remove(indexWithinParent - 1);
            parentVals.remove(indexWithinParent - 1);
            sibKeys.addAll(keys); sibVals.addAll(values);
            node.getParent().getChildren().remove(node);
            if(!sibling.isLeaf()){
                for(IBTreeNode<K,V> child: children)
                    ((BTreeNode<K, V>)child).setParent(sibling);
                sibChildren.addAll(children);
            }
        }else{
            keys.add(parentKeys.get(indexWithinParent));
            values.add(parentVals.get(indexWithinParent));
            parentKeys.remove(indexWithinParent - 1 + 1);
            parentVals.remove(indexWithinParent - 1 + 1);
            keys.addAll(sibKeys); values.addAll(sibVals);
            node.getParent().getChildren().remove(rightSibling);
            if(!sibling.isLeaf()){
                for(IBTreeNode<K,V> child: sibChildren)
                    ((BTreeNode<K, V>)child).setParent(node);
                children.addAll(sibChildren);
            }
        }

        node.setKeys(keys); node.setValues(values);
        node.getParent().setKeys(parentKeys); node.getParent().setValues(parentVals);
        sibling.setKeys(sibKeys); sibling.setValues(sibVals);

        if(fixParent){
            if(sibling == rightSibling) sibling = node;
            if(node.getParent().isRoot()) {
                root = sibling;
                sibling.setParent(null);
            } else{
                int parentIndex = node.getParent().getIndexWithinParent();
                boolean done = borrowFromAsibling(node.getParent(), null, parentIndex);
                if(!done) mergeWithSibling(node.getParent(), null, parentIndex);
            }
        }
        return true;
    }
}


