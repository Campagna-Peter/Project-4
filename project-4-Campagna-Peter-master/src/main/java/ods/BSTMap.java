package ods;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

public class BSTMap<K extends Comparable<K>, V> implements Map<K, V> {
    private Node root;
    private Comparator<K> comp;
    private int size;

    public BSTMap() {
        this(Comparator.<K>naturalOrder());
    }

    public BSTMap(Comparator<K> c) {
        clear();
        comp = c;
    }

    @Override
    public V put(K key, V value) {
        Node newNode = new Node(key, value);
        if (root == null) {
            root = newNode;
            size++;
            return null;
        } else {
            V ret = put(newNode, root);
            if (ret == null)
                size++;
            return ret;
        }
    }

    private V put(Node newNode, Node cur) {
        int c = comp.compare(newNode.key, cur.key);
        if (c == 0) {
            V val = cur.value;
            cur.value = newNode.value;
            return val;
        } else if (c < 0) {
            if (cur.left == null) {
                cur.left = newNode;
                newNode.parent = cur;
                return null;
            } else {
                return put(newNode, cur.left);
                // we inserted in the left. See if we need to rebalance.
            }
        } else {
            if (cur.right == null) {
                cur.right = newNode;
                newNode.parent = cur;
                return null;
            } else {
                return put(newNode, cur.right);
            }
        }
    }

    @Override
    public V remove(K key) {
        return remove(key, root);
    }

    // Rewritten to trim the method down, using a separate method to avoid duplicate
    // code.
    // The method I called "removeTwig" is essentially the same as the book's
    // splice().
    private V remove(K key, Node cur) {
        if (cur == null) {
            return null;
        }

        int c = comp.compare(key, cur.key);
        if (c < 0) {
            return remove(key, cur.left);
        } else if (c > 0) {
            return remove(key, cur.right);
        } else {
            // we found it! get it outta here.
            var val = cur.value;

            // do we have 2 children?
            if (cur.left != null && cur.right != null) {
                // find the replacement
                var least = cur.right;
                while (least.left != null) {
                    least = least.left;
                }
                // move payload into this spot
                cur.key = least.key;
                cur.value = least.value;

                // remove the stale node
                removeTwig(least);
            } else {
                // at most one child
                removeTwig(cur);
            }
            size--;
            return val;
        }
    }

    @Override
    public V get(K key) {
        Node cur = root;
        while (cur != null) {
            int c = comp.compare(key, cur.key);
            if (c == 0) {
                return cur.value;
            } else if (c < 0) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return null;
    }

    private void removeTwig(Node twig) {
        // for our purposes, a twig is a node with 0 or 1 child(ren)
        // don't pass a full node to this method!

        // our parent. Might be null if we're root
        var p = twig.parent;

        // the child of twig we are going to promote. Will be null if
        // twig is a leaf. Saves some if-else branching.
        var promoteMe = twig.left != null ? twig.left : twig.right;

        if (p == null) {
            // twig is the root
            root = promoteMe;
        } else if (p.left == twig) {
            p.left = promoteMe;
        } else {
            p.right = promoteMe;
        }

        if (promoteMe != null) {
            promoteMe.parent = p;
        }
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    // Re-compute the height of n, assuming that the heights of n's
    // children are correct.
    private void fixHeight(Node n) {
        if (n == null)
            return;

        int l = n.left == null ? -1 : n.left.height;
        int r = n.right == null ? -1 : n.right.height;

        n.height = Math.max(l, r) + 1;
    }

    public void printInOrder() {
        Node cur = root;
        while (cur.left != null) {
            cur = cur.left;
        }

        while (cur != null) {
            System.out.println(cur.key + ": " + cur.value);
            // find cur's successor
            if (cur.right != null) {
                cur = cur.right;
                while (cur.left != null) {
                    cur = cur.left;
                }
            }
            else {
                while (cur.parent != null && cur.parent.right == cur) {
                    cur = cur.parent;
                }
                cur = cur.parent;
            }
        }
    }

    // Use the GraphViz library of tools to produce a picture of this graph. We
    // do this by generating a .dot file that describes the tree, and then
    // executes the dot command to convert that file into a .png file.
    public void makePic(String name) {
        String dotFileName = name + ".dot";
        String picName = name + ".png";
        PrintWriter dotFile = null;
        try {
            dotFile = new PrintWriter(new BufferedWriter(new FileWriter(dotFileName)));
        } catch (IOException e) {
            return;
        }

        // boilerplate header for the
        dotFile.println("digraph tree {");
        dotFile.println("\tratio=0.5;");
        dotFile.println("\tsplines=false;");

        // recursively handle all the nodes in the tree
        dotNode(root, dotFile);

        dotFile.println("}");
        dotFile.close();

        try {
            String cmd = "dot -Tpng -o" + picName + " " + dotFileName;
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dotNode(Node cur, PrintWriter file) {
        if (cur == null)
            return;

        if (cur.left == null && cur.right == null)
            return;

        // at least one child if we get here
        if (cur.left != null) {
            file.printf("\t\"%s: %s\":sw -> \"%s: %s\"%n", cur.key.toString(), cur.value.toString(),
                    cur.left.key.toString(), cur.left.value.toString());
        }
        if (cur.right != null) {
            file.printf("\t\"%s: %s\":se -> \"%s: %s\"%n", cur.key.toString(), cur.value.toString(),
                    cur.right.key.toString(), cur.right.value.toString());
        }

        dotNode(cur.left, file);
        dotNode(cur.right, file);
    }

    private class Node {
        K key;
        V value;
        int height;

        Node parent;
        Node left;
        Node right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            height = -1;
            parent = left = right = null;
        }
    }
}
