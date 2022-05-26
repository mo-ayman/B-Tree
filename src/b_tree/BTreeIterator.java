package b_tree;

import java.util.Iterator;
import java.util.Stack;

public class BTreeIterator<K extends Comparable<K>, V> implements Iterator<V> {
    private BTreeNode<K, V> current;
    private int index;
    Stack<BTreeNode<K, V>> stack;

    public BTreeIterator(BTree<K, V> vs) {
        stack = new Stack<>();

        current = (BTreeNode)vs.getRoot();
        stack.push(current);
        index = current.getNumOfKeys();

    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public V next() {
        if(index == current.getNumOfKeys()) {
            index = 0;
            current = stack.pop();
            if (!current.isLeaf()) {
                for (IBTreeNode<K, V> i : current.getChildren()) {
                    stack.push((BTreeNode<K, V>) i);
                }
            }
        }
        return current.getItems().get(index++).getValue();
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }
}
