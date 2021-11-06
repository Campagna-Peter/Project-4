package ods;

import java.lang.reflect.Array;

/**
 * A linear-probed hash table, using the strategies from the reading.
 * This code has not been tested well. You may find bugs! Report them,
 * please.
 */
public class BubbaHashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_CAPACITY = 8;
    private static final double DEFAULT_LOADFACTOR = 0.5;

    private Entry[] table;
    private int size;
    private double loadFactor;

    public BubbaHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOADFACTOR);
    }

    public BubbaHashMap(int initCap) {
        this(initCap, DEFAULT_LOADFACTOR);
    }

    public BubbaHashMap(double lf) {
        this(DEFAULT_CAPACITY, lf);
    }

    public BubbaHashMap(int initCap, double lf) {
        clear(initCap);
        loadFactor = lf;
    }

    /**
     * TODO: Rewrite to use Bubba's ideas
     */
    @Override
    public V put(K key, V value) {
        int delPos = -1;
        int idx = hash(key);
        while (table[idx] != null) {
            if (table[idx].key == null && delPos == -1) {
                // this is where we'll put the new entry,
                // if we decide to
                delPos = idx;
            } else if (table[idx].key.equals(key)) {
                // found a dup. Update the value and return the original
                V oldVal = table[idx].value;
                table[idx].value = value;
                return oldVal;
            }
            idx = increment(idx);
        }
        // if we get here, we didn't find a duplicate. We either insert at the
        // first deleted position we found, or at the null that stopped our search.
        if (delPos != -1) {
            table[delPos].key = key;
            table[delPos].value = value;
        }
        else {
            table[idx] = new Entry(key, value);
        }
        size++;

        // If we exceed the loadfactor, double the size.
        if (size >= loadFactor * table.length) {
            rehash(table.length * 2);
        }

        return null;
    }

    /**
     * TODO: Bubba rewrite
     */
    @Override
    public V remove(K key) {
        int idx = hash(key);
        while (table[idx] != null) {
            if (table[idx].key != null && table[idx].key.equals(key)) {
                // we found this key. Return the associated value, null out
                // the key and value so the entry becomes a "tombstone", resize,
                // rehash as necessary.
                V val = table[idx].value;
                table[idx].key = null;
                table[idx].value = null;
                size--;

                // check to see if we can save some space.
                if (size < loadFactor * table.length / 4.0) {
                    rehash(table.length / 2);
                }
                return val;
            }
            idx = increment(idx);
        }
        return null;
    }

    /**
     * TODO: Bubba knows best
     */
    @Override
    public V get(K key) {
        int idx = hash(key);
        while (table[idx] != null) {
            if (table[idx].key != null && table[idx].key.equals(key)) {
                return table[idx].value;
            }
            idx = increment(idx);
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        clear(DEFAULT_CAPACITY);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void clear(int cap) {
        table = (Entry[]) Array.newInstance(Entry.class, cap);
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode() % table.length);
    }

    private int increment(int idx) {
        idx++;
        return idx == table.length ? 0 : idx;
    }

    /**
     * TODO: Does this need a rewrite?
     * @param newCap
     */
    private void rehash(int newCap) {
        // make a new table of the new size, then iterate over the old
        // table and reinsert each entry.
        var oldTable = table;
        clear(newCap);
        for (var e : oldTable) {
            // skip nulls and tombstones.
            if (e != null && e.key != null) {
                this.put(e.key, e.value);
            }
        }
    }

    public void printStats() {
        System.out.println("Size: " + size);
        System.out.println("Capacity: " + table.length);

    }


    /**
     * An entry in our table. Note that we'll use an entry with key==null to
     * indicate a deleted entry (what the text called DEL; also called tombstones).
     *
     * TODO: What adjustments do Bubba's ideas suggest here?
     */
    private class Entry {
        K key;
        V value;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }
    }
}
