package ods;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

public class AVLMap<K extends Comparable<K>, V> implements Map<K, V> {

    private Node root;
    private Comparator<K> comp;
    private int size;

    public AVLMap() {
        this(Comparator.<K>naturalOrder());
    }

    public AVLMap(Comparator<K> c) {
        clear();
        comp = c;
    }

    public boolean isBalanced(Node u) {
        int leftHeight = (u.left == null ? -1 : u.left.height);
        int rightHeight = (u.right == null ? -1 : u.right.height);
        if (Math.abs(leftHeight - rightHeight) > 1) {
            return false;
        } else {
            return true;
        }
    }

    public void lrRotation(Node cur) {

        Node tempCur = cur;
        Node heavy = tempCur.left;
        Node heavyBecause = tempCur.left.right;
        Node tempParent = tempCur.parent;

        heavy.right = heavyBecause.left;

        if (heavy.right != null) {
            heavy.right.parent = heavy;
        }

        tempCur.left = heavyBecause.right;

        if (tempCur.left != null) {
            tempCur.left.parent = tempCur;
        }

        heavyBecause.left = heavy;
        heavyBecause.left.parent = heavyBecause;

        heavyBecause.right = tempCur;
        heavyBecause.right.parent = heavyBecause;

        heavyBecause.parent = tempParent;

        if (tempParent != null) {
            if (tempParent.left == tempCur) {
                tempParent.left = heavyBecause;
            } else {
                tempParent.right = heavyBecause;
            }
        } else {
            root = heavyBecause;
        }
        fixHeight(tempCur);
        fixHeight(heavy);
        fixHeight(heavyBecause);
    }

    public void llRotation(Node cur) {

        Node tempCur = cur;
        Node l = tempCur.left;
        Node tempParent = cur.parent;

        tempCur.left = l.right;
        if (tempCur.left != null) {
            tempCur.left.parent = tempCur;
        }
        l.right = tempCur;
        l.right.parent = l;
        l.parent = tempParent;

        if (tempParent != null) {
            if (tempParent.left == tempCur) {
                tempParent.left = l;
            } else {
                tempParent.right = l;
            }
        } else {
            root = l;
        }

        // fix heights
        fixHeight(tempCur);
        fixHeight(l);
    }

    public void rrRotation(Node cur) {

        Node tempCur = cur;
        Node r = tempCur.right;
        Node tempParent = cur.parent;

        tempCur.right = r.left;
        if (tempCur.right != null) {
            tempCur.right.parent = tempCur;
        }
        r.left = tempCur;
        r.left.parent = r;
        r.parent = tempParent;

        if (tempParent != null) {
            if (tempParent.left == tempCur) {
                tempParent.left = r;
            } else {
                tempParent.right = r;
            }
        } else {
            root = r;
        }

        // fix heights
        fixHeight(tempCur);
        fixHeight(r);
    }

    public void rlRotation(Node cur) {
        Node tempCur = cur;
        Node heavy = tempCur.right;
        Node heavyBecause = tempCur.right.left;
        Node tempParent = tempCur.parent;

        heavy.left = heavyBecause.right;

        if (heavy.left != null) {
            heavy.left.parent = heavy;
        }

        tempCur.right = heavyBecause.left;

        if (tempCur.right != null) {
            tempCur.right.parent = tempCur;
        }

        heavyBecause.right = heavy;
        heavyBecause.right.parent = heavyBecause;

        heavyBecause.left = tempCur;
        heavyBecause.left.parent = heavyBecause;

        heavyBecause.parent = tempParent;

        if (tempParent != null) {
            if (tempParent.left == tempCur) {
                tempParent.left = heavyBecause;
            } else {
                tempParent.right = heavyBecause;
            }
        } else {
            root = heavyBecause;
        }
        fixHeight(tempCur);
        fixHeight(heavy);
        fixHeight(heavyBecause);
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
                fixHeight(cur);
                return null;
            } else {
                var retVal = put(newNode, cur.left);
                if (retVal != null) {
                    return retVal;
                }
                // we inserted in the left. See if we need to rebalance.
                if (isBalanced(cur)) {
                    fixHeight(cur);
                    return retVal;
                } else {
                    if (getHeight(cur.left.left) > getHeight(cur.left.right)) {
                        llRotation(cur);
                    } else {
                        lrRotation(cur);
                    }
                    return null;
                }
            }
        } else {
            if (cur.right == null) {
                cur.right = newNode;
                newNode.parent = cur;
                fixHeight(cur);
                return null;
            } else {
                var retVal = put(newNode, cur.right);
                if (retVal != null) {
                    return retVal;
                }
                if (isBalanced(cur)) {
                    fixHeight(cur);
                    return retVal;
                } else {
                    if (getHeight(cur.right.right) > getHeight(cur.right.left)) {
                        rrRotation(cur);
                    } else {
                        rlRotation(cur);
                    }
                    return null;
                }
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
            Node start = null;
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
                
                start = removeTwig(least);
            } else {
                // at most one child
                start = removeTwig(cur);
            }
            while (start != null) {
                System.out.println("rebalance " + start.key.toString());
                rebalance(start);
                start = start.parent;
            }
            size--;
            return val;
        }
    }

    private void rebalance(Node cur) {
        if ((getHeight(cur.left) - getHeight(cur.right)) > 1) {
            if (getHeight(cur.left.left) >= getHeight(cur.left.right)) {
                llRotation(cur);
            } else {
                lrRotation(cur);
            }
        } else if ((getHeight(cur.right) - getHeight(cur.left)) > 1) {
            if (getHeight(cur.right.right) >= getHeight(cur.right.left)) {
                rrRotation(cur);
            } else {
                rlRotation(cur);
            }
        } else {
            fixHeight(cur);
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

    private Node removeTwig(Node twig) {
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
        return p;
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

    private int getHeight(Node n) {
        if (n == null)
            return -1;
        else
            return n.height;
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
            } else {
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
            file.printf("\t\"%s: %d\":sw -> \"%s: %d\"%n", cur.key.toString(), cur.height, cur.left.key.toString(),
                    cur.left.height);
        }
        if (cur.right != null) {
            file.printf("\t\"%s: %d\":se -> \"%s: %d\"%n", cur.key.toString(), cur.height, cur.right.key.toString(),
                    cur.right.height);
        }

        dotNode(cur.left, file);
        dotNode(cur.right, file);
    }

    // public void printStats() {
    // System.out.println("Size: " + size);
    // System.out.println("Capacity: " + table.length);

    // }

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
            height = 0;
            parent = left = right = null;
        }
    }
}
