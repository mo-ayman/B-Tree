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
        BTreeNode<K, V> gotNode = searchNode(key, this.root);
        boolean violatesMin =  gotNode.hasMinNoOfKeys();
        boolean nobranchViolation = false;
        //1st: solving key-less branches after deletion
        if (!gotNode.isLeaf()){
            ///try to replace the key with its predecessor or successor
            nobranchViolation = replaceWithPredece_ssorOrSucce_ssor(gotNode, key);
            if(!nobranchViolation) mergeChildren(gotNode, key);
        }
        if(violatesMin){
            boolean done = borrowFromAsibling(gotNode, key, nobranchViolation);
            if(!done) done = mergeWithSibling(gotNode, key, nobranchViolation);
            if(!done) return false;
        }
        if(this.root.getItems().size() == 0 && this.root.isLeaf()) this.root = null;
        return true;
    }
    private boolean replaceWithPredece_ssorOrSucce_ssor(BTreeNode<K, V> node, K key){
        ///index of key within node
        int index = binarySearch(0, node.getKeys().size() - 1, key, node.getKeys());
        if (index < 0) return false;
        Item<K, V> successorKey = node.getSuccessor(index);
        Item<K, V> predecessorKey = node.getPredecessor(index);
        Item<K, V> _cessorKey = null;
        if(successorKey != null) _cessorKey = successorKey;
        else if (predecessorKey != null) _cessorKey = predecessorKey;
        List<K> keys = node.getKeys();
        List<V> values = node.getValues();
        if (_cessorKey == null) return false; //// you will need to merge children
        else{ //attempt to replace with successor key
            keys.set(index, _cessorKey.getKey());
            values.set(index, _cessorKey.getValue());
            node.setKeys(keys);
            node.setValues(values);
            delete(_cessorKey.getKey());
        }
        return true;
    }
    private boolean mergeChildren(BTreeNode<K, V> node, K key){
        //// merging left and right children....
        int index = binarySearch(0, node.getKeys().size() - 1, key, node.getKeys());
        if (index < 0) return false;
        BTreeNode<K, V> leftChild = node.getLeftChild(index);
        BTreeNode<K, V> rightChild = node.getRightChild(index);

        List<K> keys = node.getKeys();
        List<V> values = node.getValues();

        List<K> mergedKeys = leftChild.getKeys();
        List<V> mergedVals = leftChild.getValues();
        mergedKeys.addAll(rightChild.getKeys());
        mergedVals.addAll(rightChild.getValues());
        //// do not know what to do in case of descendants !!!!

        leftChild.setKeys(mergedKeys);
        leftChild.setValues(mergedVals);
        rightChild.setItems(null);
        keys.remove(key);
        values.remove(node.getValues().get(index));
        node.setKeys(keys);
        node.setValues(values);
        return true;
    }
    private boolean borrowFromAsibling(BTreeNode<K, V> node, K key, boolean deleted){
        //// merging left and right children....
        int ind = binarySearch(0, node.getKeys().size() - 1, key, node.getKeys());
        if (ind < 0) return false;
        int index = node.getIndexWithinParent();
        BTreeNode<K, V> leftSibling = node.getLeftSibling();
        BTreeNode<K, V> rightSibling = node.getRightSibling();
        BTreeNode<K, V> sibling = leftSibling;
        if(leftSibling == null || leftSibling.hasMinNoOfKeys())
            sibling = rightSibling;
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
        if (!deleted) {
            keys.remove(key);
            values.remove(node.getValues().get(index));
        }
        if(sibling == leftSibling){
            children.set(children.size() - 1, children.get(children.size() - 2));
            for(int i = keys.size() - 2; i > - 1; i--) {
                keys.set(i+1, keys.get(i));
                values.set(i+1, values.get(i));
                children.set(i+1, children.get(i));
            }
            keys.set(0, parentKeys.get(index - 1));
            values.set(0, parentVals.get(index - 1));
            children.set(0, sibChildren.get(sibling.getNumOfKeys()));
            ((BTreeNode<K, V>)sibChildren.get(sibling.getNumOfKeys())).setParent(node);
            parentKeys.set(index - 1, sibKeys.get(sibling.getNumOfKeys() - 1));
            parentVals.set(index - 1, sibVals.get(sibling.getNumOfKeys() - 1));
            sibKeys.remove(sibKeys.get(sibling.getNumOfKeys() - 1));
            sibVals.remove(sibVals.get(sibling.getNumOfKeys() - 1));
            sibChildren.set(sibling.getNumOfKeys(), null);
            node.getParent().setKeys(parentKeys); node.getParent().setValues(parentVals);
            node.setKeys(keys); node.setValues(values); node.setChildren(children);
            sibling.setKeys(sibKeys); sibling.setValues(sibVals); sibling.setChildren(sibChildren);
        }
        return true;
    }
    private boolean mergeWithSibling(BTreeNode<K, V> node, K key, boolean deleted){
        int ind = binarySearch(0, node.getKeys().size() - 1, key, node.getKeys());
        if (ind < 0) return false;
        int index = node.getIndexWithinParent();
        BTreeNode<K, V> leftSibling = node.getLeftSibling();
        BTreeNode<K, V> rightSibling = node.getRightSibling();
        BTreeNode<K, V> sibling = leftSibling;
        if(leftSibling == null || leftSibling.hasMinNoOfKeys())
            sibling = rightSibling;
        if((rightSibling == null || rightSibling.hasMinNoOfKeys()) && node.isRoot() && node.isLeaf())
            return true;
        List<K> sibKeys = sibling.getKeys() ;
        List<V> sibVals = sibling.getValues();
        List<K> parentKeys = node.getParent().getKeys();
        List<V> parentVals = node.getParent().getValues();

//        do not know what to do with children...
        List<K> keys = node.getKeys();
        List<V> values = node.getValues();
        if (!deleted) {
            keys.remove(key);
            values.remove(node.getValues().get(index));
        }
        if(sibling == leftSibling){
            sibKeys.add(parentKeys.get(index - 1));
            sibVals.add(parentVals.get(index - 1));
            parentKeys.remove(index - 1);
            parentVals.remove(index - 1);
            sibKeys.addAll(node.getKeys());
            sibVals.addAll(node.getValues());
            leftSibling.setKeys(sibKeys);
            leftSibling.setValues(sibVals);
            node.setItems(null);
            node.getParent().setKeys(parentKeys);
            node.getParent().setValues(parentVals);
        }
        else{
            keys.add(parentKeys.get(index));
            values.add(parentVals.get(index));
            parentKeys.remove(index - 1 + 1);
            parentVals.remove(index - 1 + 1);
            keys.addAll(sibKeys);
            values.addAll(sibVals);
            sibling.setItems(null);
            node.setKeys(keys);
            node.setValues(values);
            node.getParent().setKeys(parentKeys);
            node.getParent().setValues(parentVals);
        }
        if(parentKeys.size() == 0){
            node.setParent(node.getParent().getParent());
            node.getParent().getParent().getChildren().set(node.getParent().getIndexWithinParent(), node);
        }
        return true;
    }
}


