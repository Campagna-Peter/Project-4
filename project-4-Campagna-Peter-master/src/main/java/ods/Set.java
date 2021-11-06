package ods;
public interface Set<T extends Comparable<T>> {

    void add(T val);

    T find(T val);

    T remove(T val);

    int size();

    boolean isEmpty();
}
