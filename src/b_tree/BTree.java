package b_tree;

import org.w3c.dom.Node;

import java.util.ArrayList;
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
        Item<K, V> newItem = new Item<>(key, value);
        BTreeNode<K, V> targetNode = searchNode(key, root);
        List<Item<K, V>> listItems = targetNode.getItems();
        listItems.add(newItem);
        if (targetNode.hasMaxNoOfKeys()) 
            
            
    }
    
    private void splitNodeAndInsertMedianInTheParent(BTreeNode<K, V> targetNode) {
        BTreeNode<K, V> parentNode = targetNode.getParent();
        List<IBTreeNode<K, V>> children = targetNode.getChildren();
        boolean hasChildren = !targetNode.isLeaf();
        List<Item<K, V>> items = targetNode.getItems();
        int indexMedian = (items.size() - 1) / 2;
        Item<K, V> medianItem = items.get(indexMedian);
        List<Item<K, V>> newList1 = items.subList(0, indexMedian);
        List<Item<K, V>> newList2 = items.subList(indexMedian + 1, items.size());
        items = null;
        BTreeNode<K, V> leftNode = new BTreeNode<>();
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
            if(keyOfNode == null || compRes < 0) {
                if (!node.isLeaf())
                    return searchNode(key, (BTreeNode<K, V>) node.getChildren().get(index));
                else return node;
            }
            else if(compRes > 0 && index < keysOfNode.size() - 1) index++;
            else if(compRes > 0 && index == keysOfNode.size() - 1) {
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
    public boolean delete(K key) {
        BTreeNode<K, V> gotNode = searchNode(key, this.root);
        assert gotNode != null;
        List<K> keys = gotNode.getKeys();
        List<V> values = gotNode.getValues();

        int index = binarySearch(0, keys.size() - 1, key, keys);///index of key within node
        if (index < 0) return false;
        boolean violatesMin =  gotNode.hasMinNoOfKeys();
        //1st: solving key-less branches after deletion
        if (!gotNode.isLeaf()) { 
            Item<K, V> successorKey = gotNode.getSuccessor(index);
            Item<K, V> predecessorKey = gotNode.getPredecessor(index);
            if (successorKey != null) { //attempt to replace with successor key
                keys.set(index, successorKey.getKey());
                values.set(index, successorKey.getValue());
                gotNode.setKeys(keys);
                gotNode.setValues(values);
                delete(successorKey.getKey());
            } else if (predecessorKey != null) { //attempt to replace with predecessor key
                keys.set(index, predecessorKey.getKey());
                values.set(index, predecessorKey.getValue());
                gotNode.setKeys(keys);
                gotNode.setValues(values);
                delete(predecessorKey.getKey());
            } else { //// merging left and right children....
                BTreeNode<K, V> leftChild = gotNode.getLeftChild(index);
                BTreeNode<K, V> rightChild = gotNode.getRightChild(index);
                List<K> mergedKeys = leftChild.getKeys(); 
                List<V> mergedVals = leftChild.getValues();
                mergedKeys.addAll(rightChild.getKeys());
                mergedVals.addAll(rightChild.getValues());

                leftChild.setKeys(mergedKeys);
                leftChild.setValues(mergedVals);
                rightChild.setItems(null);
                
                keys.remove(key);
                values.remove(gotNode.getValues().get(index));
                gotNode.setKeys(keys);
                gotNode.setValues(values);
            }
        }
        if(violatesMin){
            if(gotNode.isLeaf()){
                keys.remove(key);
                values.remove(gotNode.getValues().get(index));
            }
            BTreeNode<K, V> leftSibling = gotNode.getLeftSibling();
            BTreeNode<K, V> rightSibling = gotNode.getRightSibling();
            if(leftSibling != null || rightSibling != null){
                BTreeNode<K, V> sibling = null;
                List<K> parentKeys = gotNode.getParent().getKeys();
                List<V> parentVals = gotNode.getParent().getValues();
                List<IBTreeNode<K, V>> children = gotNode.getChildren();
                List<K> mergedKeys;
                List<V> mergedVals;
                List<IBTreeNode<K, V>> mergedChildren;
                int ind = gotNode.getIndexWithinParent();

                if(leftSibling != null && !leftSibling.hasMinNoOfKeys())
                    sibling = leftSibling;
                else if(rightSibling != null && !rightSibling.hasMinNoOfKeys()) 
                    sibling = rightSibling;
                else if(leftSibling != null && leftSibling.hasMinNoOfKeys()){
                    mergedKeys = leftSibling.getKeys();
                    mergedVals = leftSibling.getValues();
                    //mergedChildren = leftSibling.getChildren();
                    mergedKeys.add(parentKeys.get(ind - 1));
                    mergedVals.add(parentVals.get(ind - 1));
                    parentKeys.remove(ind - 1);
                    parentVals.remove(ind - 1);
                    mergedKeys.addAll(gotNode.getKeys());
                    mergedVals.addAll(gotNode.getValues());
                    leftSibling.setKeys(mergedKeys);
                    leftSibling.setValues(mergedVals);
                    gotNode.getParent().setKeys(parentKeys);
                    gotNode.getParent().setValues(parentVals);

                    return true;
                }
                else if(rightSibling != null && rightSibling.hasMinNoOfKeys()){
                    mergedKeys = gotNode.getKeys();
                    mergedVals = gotNode.getValues();
                    mergedKeys.add(parentKeys.get(ind));
                    mergedVals.add(parentVals.get(ind));
                    parentKeys.remove(ind -1 +1);
                    parentVals.remove(ind -1 +1);
                    mergedKeys.addAll(rightSibling.getKeys());
                    mergedVals.addAll(rightSibling.getValues());
                    gotNode.setKeys(mergedKeys);
                    gotNode.setValues(mergedVals);
                    gotNode.getParent().setKeys(parentKeys);
                    gotNode.getParent().setValues(parentVals);
                    return true;
                }

                List<K> sibKeys ;
                List<V> sibVals ;
                List<IBTreeNode<K, V>> sibChildren;
                if(sibling == leftSibling){
                    sibKeys = leftSibling.getKeys();
                    sibVals = leftSibling.getValues();
                    sibChildren = leftSibling.getChildren();
                } else {
                    sibKeys = rightSibling.getKeys();
                    sibVals = rightSibling.getValues();
                    sibChildren = rightSibling.getChildren();
                }
                
                keys.set(index, parentKeys.get(ind - 1));
                values.set(index, parentVals.get(ind - 1));
                if(sibling == leftSibling) {
                    parentKeys.set(ind - 1, sibKeys.get(sibling.getNumOfKeys() - 1));
                    parentVals.set(ind - 1, sibVals.get(sibling.getNumOfKeys() - 1));
                    sibKeys.remove(sibling.getNumOfKeys() - 1);
                    sibVals.remove(sibling.getNumOfKeys() - 1);
                    children.set(0, sibChildren.get(sibling.getNumOfKeys())) ;
                    sibChildren.remove(sibling.getNumOfKeys() -1 + 1);
                }else{
                    parentKeys.set(ind, sibKeys.get(0));
                    parentVals.set(ind, sibVals.get(0));
                    sibKeys.remove(0);
                    sibVals.remove(0);
                    children.set(0, sibChildren.get(0)) ;
                    sibChildren.remove(0);
                }
                
                gotNode.setChildren(children);
                gotNode.setKeys(keys);
                gotNode.setValues(values);
                sibling.setChildren(sibChildren);
                sibling.setKeys(sibKeys);
                sibling.setValues(sibVals);
            }
        }
        return true;
    }
}


