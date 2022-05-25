package b_tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BTreeNode <K extends Comparable<K>, V> implements IBTreeNode<K, V>{
    private BTreeNode<K, V> parent;
    private Item<K, V>[] items; /// key-value pair stored at each slot
    private BTreeNode<K, V>[] children;   ///list of children pointed to
    private int actualSize;
    private int order;

//  e.g
//               items : ( , ), ( , ), ( , );
//    indices of itmes :    0 ,   1  ,  2  ;
//            children :  0 ,  1 ,  2,  3 ;
//            ___________________________________________________________________________________
//            | child   |      item     |  child |      item     | child |      item      | child |
//            |    0    |       0       |    1   |        1      |    2  |        2       |   3   |
//            ------------------------------------------------------------------------------------

    public BTreeNode(int order, BTreeNode<K, V> parent) {
        this.items = new Item[order - 1];
        this.children = new BTreeNode[order];
        this.parent = parent;
        this.order = order;
        actualSize = 0;
    }

    public BTreeNode(int order, BTreeNode<K, V> parent, List<Item<K, V>> items) {
        this.items = items.toArray(new Item[0]);
        this.children = new BTreeNode[order];
        this.parent = parent;
        this.order = order;
        actualSize = 0;
    }

    public BTreeNode(int order, BTreeNode<K, V> parent, List<Item<K, V>> items, List<IBTreeNode<K, V>> children) {
        this.items = items.toArray(new Item[0]);
        this.children = children.toArray(new BTreeNode[0]);
        this.parent = parent;
        this.order = order;
        actualSize = 0;
    }

    public BTreeNode<K, V> getParent() {
        return parent;
    }

    public void setParent(BTreeNode<K, V> parent) {
        this.parent = parent;
    }
    public Item<K, V> getSuccessor(int index){
        BTreeNode<K, V> temp = (BTreeNode<K, V>) this.getChildren().get(index+1);
        while (!temp.isLeaf()) temp = (BTreeNode<K, V>) temp.getChildren().get(0);
        if(temp.hasMinNoOfKeys()) return null;
        K key = temp.getKeys().get(0);
        V val = temp.getValues().get(0);
        return new Item<>(key, val);
    }
    public Item<K, V> getPredecessor(int index){
        BTreeNode<K, V> temp = (BTreeNode<K, V>) this.getChildren().get(index);
        while (!temp.isLeaf()) temp = (BTreeNode<K, V>) temp.getChildren().get(temp.getNumOfKeys());
        if(temp.hasMinNoOfKeys()) return null;
        K key = temp.getKeys().get(temp.getNumOfKeys() - 1);
        V val = temp.getValues().get(temp.getNumOfKeys() - 1);
        return new Item<>(key, val);
    }

    public BTreeNode<K, V> getRightSibling(){
        if(this.getParent() == null) return null;
        List<IBTreeNode<K, V>> children = this.getParent().getChildren();
        int index = this.getIndexWithinParent();
        if(index == children.size() - 1) return null;
        return (BTreeNode<K, V>) children.get(index + 1);
    }

    public BTreeNode<K, V> getLeftSibling(){
        if(this.getParent() == null) return null;
        List<IBTreeNode<K, V>> children = this.getParent().getChildren();
        int index = this.getIndexWithinParent();
        if(index == 0) return null;
        return (BTreeNode<K, V>) children.get(index - 1);
    }
    public BTreeNode<K, V> getLeftChild(int index){
        return (BTreeNode<K, V>) this.getChildren().get(index);
    }
    public BTreeNode<K, V> getRightChild(int index){
        return (BTreeNode<K, V>) this.getChildren().get(index + 1);
    }
    public int getIndexWithinParent(){
        List<IBTreeNode<K, V>> children = this.getParent().getChildren();
        int index = children.indexOf(this);
        return index;
    }


    @Override
    public int getNumOfKeys() {
        return actualSize;
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {
        Item<K, V>[] newItems  = new Item[numOfKeys];
        BTreeNode<K, V>[] newChildren = new BTreeNode[numOfKeys + 1];
        System.arraycopy(items, 0, newItems, 0, items.length);
        if (items.length + 1 >= 0) System.arraycopy(children, 0, newChildren, 0, items.length + 1);
        this.items = newItems;
        this.children = newChildren;
        actualSize = numOfKeys;
    }

    @Override
    public boolean isLeaf() {
        return children[0] == null;
    }

    @Override
    public void setLeaf(boolean isLeaf) {
        int length = this.children.length;
        this.children = new BTreeNode[length];
    }

    @Override
    public List<K> getKeys() {
        if(items.length == 0) return null;
        List<K> keys = new ArrayList<>();
        for(Item<K, V> item : items) {
            if(item != null) keys.add(item.getKey());
        }
        return keys;
    }

    @Override
    public void setKeys(List<K> keys) {
        try {
            actualSize = keys.size();
            for (int i = 0; i < keys.size(); i++) {
                items[i].setKey(keys.get(i));
            }
        }catch (Exception e){
            System.out.println("different sizes");
            System.out.println(e.getMessage());
        }
    }
    @Override
    public List<V> getValues() {
        if(items.length == 0) return null;
        List<V> values = new ArrayList<>();
        for(Item<K, V> item : items) values.add(item.getValue());
        return values;
    }

    @Override
    public void setValues(List<V> values) {
        try {
            for (int i = 0; i < values.size(); i++)
                items[i].setValue(values.get(i));
        }catch (Exception e){
            System.out.println("different sizes");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<IBTreeNode<K, V>> getChildren() {
        if(children.length == 0) return null;
        return Arrays.asList(children);
    }

    @Override
    public void setChildren(List<IBTreeNode<K, V>> children) {
        try {
            for (int i = 0; i < children.size(); i++)
                this.children[i] = (BTreeNode<K, V>) children.get(i);
        }catch (Exception e){
            System.out.println("different sizes");
            System.out.println(e.getMessage());
        }
    }

    public void setItems(List<Item<K, V>> items) {
        try {
            actualSize = items.size();
            for (int i = 0; i < items.size(); i++)
                this.items[i] = items.get(i);
        }catch (Exception e){
            System.out.println("different sizes");
            System.out.println(e.getMessage());
        }
    }

    public List<Item<K, V>> getItems() {
        if (actualSize == 0) return new ArrayList<>();
        return Arrays.asList(items);
    }

    public boolean isRoot() {
        return parent == null;
    }
    public boolean hasMinNoOfKeys() {
        return (actualSize == Math.ceil(items.length / 2.0) - 1) || (isRoot() && actualSize == 1);
    }

    public boolean hasMaxNoOfKeys() {
        return actualSize == items.length;
    }

}