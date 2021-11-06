package ods;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Random;

public class SkipListSet<T extends Comparable<T>> implements Set<T> {
    private static final int MAX_HEIGHT = 32;
    private static final Random rand = new Random();

    private Node sentinel;
    private int height;
    private int size;
    private Comparator<T> comp;
    
    public SkipListSet(Comparator<T> c) {
        sentinel = new Node(null, MAX_HEIGHT);
        height = 0;
        size = 0;
        comp = c;
    }

    public SkipListSet() {
        this(Comparator.<T>naturalOrder());
    }

    /**
     * @param val
     * @return Node
     */
    private Node findPredNode(T val) {
        Node cur = sentinel;
        int level = height;
        // keep searching until we've reached level 0
        while (level >= 0) {
            // move as far to the right as we can
            // Note that cur is always the predecessor of the node we're
            // looking at
            while (cur.next[level] != null && comp.compare(cur.next[level].data, val) < 0) {
                cur = cur.next[level];
            }
            // we can't go right. Go down to the next level
            level--;
        }
        // return the node that contains the value that most directly preceeds val.
        return cur;
    }

    /**
     * @param val
     * @return Node[]
     */
    // like findPredNode, but returns an arraylist of predecessors
    @SuppressWarnings("unchecked")
    private Node[] findPredNodes(T val) {
        var preds = (Node[]) Array.newInstance(Node.class, height + 1);

        Node cur = sentinel;
        int level = height;
        // keep searching until we've reached level 0
        while (level >= 0) {
            // move as far to the right as we can
            // Note that cur is always the predecessor of the node we're
            // looking at
            while (cur.next[level] != null && comp.compare(cur.next[level].data, val) < 0) {
                cur = cur.next[level];
            }
            // we can't go right. Go down to the next level This is a node for our
            // predecessor list
            preds[level] = cur;
            level--;
        }
        // return the node that contains the value that most directly preceeds val.
        return preds;
    }

    /**
     * @return int
     */
    private int pickHeight() {
        // treat the bits of this variable as 32 random coinflips
        int coinFlips = rand.nextInt();

        int h = 0;
        int spot = 1;
        // count the number of 0's that occur on the right of the binary
        // representation of coinflips.
        while ((coinFlips & spot) == 0) { // checks if rightmost bit is 0
            h++;
            spot = spot << 1; // shift spot over 1
        }
        return h;
    }

    /**
     * @param val
     */
    @Override
    public void add(T val) {
        var preds = findPredNodes(val);
        // don't insert duplicates.
        var target = preds[0].next[0];
        if (target != null && target.data.equals(val)) {
            return;
        }

        // make a new node, with coinflipped height
        var newNode = new Node(val, pickHeight());

        // This is a little tricky. The new node might be shorter or taller than the
        // existing
        // skiplist. So, I break insertion up into two stages: As many of the found
        // predecessors
        // as we can use, followed by the sentinel in case we're tall.
        int predHeight = Math.min(newNode.next.length, preds.length);
        for (int i = 0; i < predHeight; ++i) {
            var curPred = preds[i];
            newNode.next[i] = curPred.next[i];
            curPred.next[i] = newNode;
        }
        // if newNode is tall, pad the rest of its preds with the sentinel. Note that
        // this loop
        // doesn't run at all if the new node is not taller than the skiplist.
        for (int i = predHeight; i < newNode.next.length; ++i) {
            newNode.next[i] = sentinel.next[i];
            sentinel.next[i] = newNode;
        }

        // Check if our skiplist got taller.
        if (newNode.height() > height) {
            height = newNode.height();
        }

        // The simplest bit!
        size++;
    }

    /**
     * @param val
     * @return T
     */
    @Override
    public T find(T val) {
        var pred = findPredNode(val);
        if (pred.next[0] == null || !pred.next[0].data.equals(val)) {
            return null;
        } else {
            return pred.next[0].data;
        }
    }

    /**
     * @param val
     * @return T
     */
    @Override
    public T remove(T val) {
        Node cur = sentinel;
        Node ret = null;
        int level = height;

        while (level >= 0) {
            // move as far right as we can in list number "level"
            while (cur.next[level] != null && comp.compare(cur.next[level].data, val) < 0) {
                cur = cur.next[level];
            }
            if (cur.next[level] != null && comp.compare(cur.next[level].data, val) == 0) {
                // we have something to return
                ret = cur.next[level];
                cur.next[level] = cur.next[level].next[level];
                // did we just reduce the height? If so, we made
                // this list empty
                if (cur == sentinel && cur.next[level] == null) {
                    height--;
                }
            }
            level--;
        }
        if (ret != null) {
            size--;
            return ret.data;
        } else {
            return null;
        }
    }

    /**
     * @return int
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * @return boolean
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public void print() {
        for (int level = height; level >= 0; --level) {
            System.out.printf("level %d: sentinel -> ", level);
            var cur = sentinel;
            while (cur.next[level] != null) {
                System.out.print(cur.next[level].data + " -> ");
                cur = cur.next[level];
            }
            System.out.println("null");
        }
    }

    /**
     * @return double
     */
    public double averageHeight() {
        double total = 0.0;
        Node cur = sentinel;
        while (cur.next[0] != null) {
            total += cur.next[0].height();
            cur = cur.next[0];
        }
        return total / size;
    }

    private class Node {
        Node[] next;
        T data;

        @SuppressWarnings("unchecked")
        Node(T d, int height) {
            this.data = d;
            next = (Node[]) Array.newInstance(Node.class, height + 1);
        }

        int height() {
            return next.length - 1;
        }
    }
}
