package ods;

import java.util.ArrayList;

public class ChainedHashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_CAPACITY = 256;
    private static final double DEFAULT_LOADFACTOR = 1.0;

    private ArrayList<Entry>[] table;
    private double loadFactor;
    private int size;

    // This is typical in Java when you have several constructor parameters
    // with defaults. Other lanugages handle this differently...
    public ChainedHashMap() {
        this(DEFAULT_CAPACITY);
    }

    // The actual constructor
    public ChainedHashMap(int initialCapacity) {
        clear(initialCapacity);
        loadFactor = DEFAULT_LOADFACTOR;
    }


    /**
     * @param key
     * @param value
     * @return V
     */
    @Override
    public V put(K key, V value) {
        var bucket = table[hash(key)];
        // search the bucket for this key
        for (var e : bucket) {
            if (e.key.equals(key)) {
                var v = e.value;
                e.value = value;
                // no size change; return the old value
                return v;
            }
        }
        // we didn't find this key. append new entry to the bucket
        bucket.add(new Entry(key, value));
        size++;

        // check if we need to resize
        if (table.length * loadFactor < size) {
            rehash(table.length * 2);
        }
        return null;
    }


    /**
     * @param key
     * @return V
     */
    @Override
    public V remove(K key) {
        var bucket = table[hash(key)];
        int bLast = bucket.size() - 1;
        for (int i = 0; i < bucket.size(); ++i) {
            var e = bucket.get(i);
            if (e.key.equals(key)) {
                // swap last element into this spot to make
                // removal cheaper
                if (i < bLast) {
                    bucket.set(i, bucket.get(bLast));
                }
                // now remove the last entry in the bucket. Should be cheap.
                bucket.remove(bLast);
                return e.value;
            }
        }
        // the key was not found. Return null
        return null;
    }


    /**
     * @param key
     * @return V
     */
    @Override
    public V get(K key) {
        var bucket = table[hash(key)];
        for (var e: bucket) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }
        return null;
    }


    /**
     * @param key
     * @return boolean
     */
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        clear(DEFAULT_CAPACITY);
    }


    /**
     * @return boolean
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * @return int
     */
    @Override
    public int size() {
        return size;
    }

    public void print() {
        for (int i = 0; i < table.length; ++i) {
            System.out.printf("bucket %d: ", i);
            for (Entry e : table[i]) {
                System.out.print("(" + e.key + ", " + e.value + ") ");
            }
            System.out.println();
        }
    }

    /**
     * @param key
     * @return int
     */
    private int hash(K key) {
        return Math.abs(key.hashCode() % table.length);
    }

    /**
     * @param cap
     */
    @SuppressWarnings("unchecked")
    private void clear(int cap) {
        table = new ArrayList[cap];
        for (int i = 0; i < table.length; ++i) {
            table[i] = new ArrayList<>();
        }
        size = 0;
    }

    /**
     * @param newCap
     */
    private void rehash(int newCap) {
        var oldTable = table;
        clear(newCap);
        // move each entry from oldTable into the new one
        for (var bucket: oldTable) {
            for (var e: bucket) {
                this.put(e.key, e.value);
            }
        }
    }

    private class Entry {
        K key;
        V value;

        Entry(K k, V v) {
            this.key = k;
            this.value = v;
        }
    }
}
