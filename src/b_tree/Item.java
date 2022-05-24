package b_tree;

public class Item <K extends Comparable<K>, V>{
    private K key;
    private V value;

    public Item() {
        this.key = null;
        this.value = null;
    }

    public Item(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
