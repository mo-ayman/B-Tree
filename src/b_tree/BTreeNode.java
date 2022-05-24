package b_tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BTreeNode <K extends Comparable<K>, V> implements IBTreeNode<K, V>{
    private Item<K, V>[] items; /// key-value pair stored at each slot
    private BTreeNode<K, V>[] children;   ///list of children pointed to

//  e.g
//               items : ( , ), ( , ), ( , );
//    indices of itmes :    0 ,   1  ,  2  ;
//            children :  0 ,  1 ,  2,  3 ;
//            ___________________________________________________________________________________
//            | child   |      item     |  child |      item     | child |      item      | child |
//            |    0    |       0       |    1   |        1      |    2  |        2       |   3   |
//            ------------------------------------------------------------------------------------
//


    public BTreeNode(int degree) {
        this.items = new Item[degree - 1];
        this.children = new BTreeNode[degree];
    }

    @Override
    public int getNumOfKeys() {
        return this.items.length;
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {
        Item<K, V>[] newItems  = new Item[numOfKeys];
        BTreeNode<K, V>[] newChildren = new BTreeNode[numOfKeys + 1];
        System.arraycopy(items, 0, newItems, 0, items.length);
        if (items.length + 1 >= 0) System.arraycopy(children, 0, newChildren, 0, items.length + 1);
        this.items = newItems;
        this.children = newChildren;
    }

    @Override
    public boolean isLeaf() {
        for(int i = 0; i < this.children.length; i++)
            if(this.children[i] != null) return false;
        return true;
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
        for(Item<K, V> item : items) keys.add(item.getKey());
        return keys;
    }

    @Override
    public void setKeys(List<K> keys) {
        try {
            for (int i = 0; i < keys.size(); i++)
                items[i].setKey(keys.get(i));
        }catch (Exception e){
            System.out.println("different sizes");
            System.out.println(e.getMessage());
        }
    }
    @Override
    public List<V> getValues() {
        if(items.length == 0) return null;
        List<V> vals = new ArrayList<>();
        for(Item<K, V> item : items) vals.add(item.getValue());
        return vals;
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
        List<IBTreeNode<K,V>> childrenList = new ArrayList<>();
        childrenList.addAll(Arrays.asList(this.children));
        return childrenList;
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
}
