package b_tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BTreeNode <K extends Comparable<K>, V> implements IBTreeNode<K, V>{
    private BTreeNode<K, V> parent;
    private List<Item<K, V>> items; /// key-value pair stored at each slot
    private List<IBTreeNode<K, V>> children;   ///list of children pointed to
    private final int order;

//  e.g
//               items : ( , ), ( , ), ( , );
//    indices of itmes :    0 ,   1  ,  2  ;
//            children :  0 ,  1 ,  2,  3 ;
//            ___________________________________________________________________________________
//            | child   |      item     |  child |      item     | child |      item      | child |
//            |    0    |       0       |    1   |        1      |    2  |        2       |   3   |
//            ------------------------------------------------------------------------------------

    public BTreeNode(int order, BTreeNode<K, V> parent) {
        this.items = new ArrayList<>();
        this.children = new ArrayList<>();
        this.parent = parent;
        this.order = order;
    }


    public BTreeNode(int order, BTreeNode<K, V> parent, List<Item<K, V>> items) {
        this.items = items;
        this.children = new ArrayList<>();
        this.parent = parent;
        this.order = order;
    }

    public BTreeNode(int order, BTreeNode<K, V> parent, List<Item<K, V>> items, List<IBTreeNode<K, V>> children) {
        this.items = items;
        this.children = children;
        this.parent = parent;
        this.order = order;
    }

    public BTreeNode<K, V> getParent() {
        return parent;
    }

    public void setParent(BTreeNode<K, V> parent) {
        this.parent = parent;
    }
    public BTreeNode<K, V> getSuccessor(int index){
        BTreeNode<K, V> temp = (BTreeNode<K, V>) this.getChildren().get(index+1);
        while (!temp.isLeaf()) temp = (BTreeNode<K, V>) temp.getChildren().get(0);
        return temp;
    }
    public BTreeNode<K, V> getPredecessor(int index){
        BTreeNode<K, V> temp = (BTreeNode<K, V>) this.getChildren().get(index);
        while (!temp.isLeaf()) temp = (BTreeNode<K, V>) temp.getChildren().get(temp.getNumOfKeys());
        return temp;
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
        return children.indexOf(this);
    }


    @Override
    public int getNumOfKeys() {
        return items.size();
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {
        List<Item<K, V>> newItems  = new ArrayList<>();
        List<IBTreeNode<K, V>> newChildren = new ArrayList<>();
        System.arraycopy(items, 0, newItems, 0, items.size());
        if (items.size() + 1 >= 0) System.arraycopy(children, 0, newChildren, 0, items.size() + 1);
        this.items = newItems;
        this.children = newChildren;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public void setLeaf(boolean isLeaf) {
        children = null;
    }

    @Override
    public List<K> getKeys() {
        List<K> keys = new ArrayList<>();
        if(items.isEmpty()) return keys;
        for(Item<K, V> item : items)
            keys.add(item.getKey());
        return keys;
    }

    @Override
    public void setKeys(List<K> keys) {
        items.clear();
        for (int i = 0; i < keys.size(); i++){
            items.add(new Item<K, V>());
            items.get(i).setKey(keys.get(i));
        }
    }
    @Override
    public List<V> getValues() {
        List<V> values = new ArrayList<>();
        if(items.isEmpty()) return values;
        for(Item<K, V> item : items) values.add(item.getValue());
        return values;
    }

    @Override
    public void setValues(List<V> values) {
        for (int i = 0; i < values.size(); i++)
            items.get(i).setValue(values.get(i));
    }

    @Override
    public List<IBTreeNode<K, V>> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<IBTreeNode<K, V>> children) {
        this.children = children;
    }

    public void setItems(List<Item<K, V>> items) {
        this.items = items;
    }

    public List<Item<K, V>> getItems() {
        return items;
    }

    public boolean isRoot() {
        return parent == null;
    }
    public boolean hasMinNoOfKeys() {
        return (items.size() == Math.ceil(order / 2.0) - 1) || (isRoot() && items.size() == 1);
    }

    public boolean violatesMaxNoOfKeys() {
        return items.size() == order;
    }

}