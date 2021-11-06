package lvc.cds;

import java.io.File;
import java.util.Random;

import ods.AVLMap;
import ods.BSTMap;
import ods.ChainedHashMap;
import ods.LinearProbedHashMap;
import ods.SkipListSet;

/**
 * Hello world!
 */
public final class App {
    private static Random r = new Random();

    /**
     * Run a method and return the number of MS it takes to execute.
     * 
     * @param rble The Runnable you want me to run.
     * @return double The time in MS.
     */
    public static double timeInMS(Runnable rble) {
        final double convert = 1_000_000.0;
        var start = System.nanoTime();
        rble.run();
        return (System.nanoTime() - start) / convert;
    }

    public static void timings() {
        for (int s = 100; s <= 100_000_000; s *= 2) {
            final int size = s;
            SkipListSet<Integer> sls = new SkipListSet<>();

            var elapsed = timeInMS(() -> {
                for (int i = 0; i < size; ++i) {
                    int v = r.nextInt(size * 10);
                    sls.add(v);
                }
            });

            final int[] targets = new int[10_000];
            for (int i = 0; i < targets.length; ++i) {
                targets[i] = r.nextInt(size * 10);
            }

            elapsed = timeInMS(() -> {
                for (int i = 0; i < 10_000; i++) {
                    sls.find(targets[i]);
                }
            });

            System.out.printf("%d,%1.3f%n", size, elapsed);
        }
    }

    public static void checkAvgHeight() {
        SkipListSet<Integer> sls = new SkipListSet<>();
        int size = 100_000;

        for (int i = 0; i < size; ++i) {
            int v = r.nextInt(size * 10);
            sls.add(v);
        }

        System.out.println("Avg height = " + sls.averageHeight());

    }

    public static void printList() {
        SkipListSet<Integer> sls = new SkipListSet<>();
        int size = 10;
        int[] data = new int[size];

        for (int i = 0; i < size; ++i) {
            int v = r.nextInt(size * 10);
            data[i] = v;
            sls.add(v);
        }

        sls.print();

        System.out.println("\nRemoving " + data[5]);
        sls.remove(data[5]);
        sls.print();

        System.out.println("\nRemoving " + data[1]);
        sls.remove(data[1]);
        sls.print();

        System.out.println("\nRemoving " + 120);
        sls.remove(120);
        sls.print();

    }

    public static void testChainedHashMap() {
        ChainedHashMap<String, Integer> map = new ChainedHashMap<>(32);

        for (int i = 0; i < 62; ++i) {
            map.put("key" + i, i);
        }

        if (map.containsKey("key21")) {
            System.out.println("map contains key21");
        }

        if (!map.containsKey("key123")) {
            System.out.println("map does not contain key123");
        }

        Integer v = map.get("key42");
        if (v != null) {
            System.out.println("key 42 paired with " + 42);
        }

        map.print();
    }

    public static void testLinearHashMap() {
        // This is a really incomplete test.
        final int SIZE = 100_000;
        LinearProbedHashMap<Integer, String> lphm = new LinearProbedHashMap<>();
        int[] targets = new int[SIZE];

        for (int i = 0; i < SIZE; ++i) {
            int arr = r.nextInt();
            lphm.put(arr, "Boo");
            targets[i] = arr;
        }

        var res = timeInMS(() -> {
            for (int i = 0; i < SIZE; i++) {
                lphm.containsKey(targets[i]);
            }
        });

        System.out.printf("time to search for all entries is %,1.4f%n", res);
        lphm.printStats();
    }

    public static void testBSTMap() {
        new File("pics").mkdir();

        BSTMap<Integer, Integer> bstm = new BSTMap<>();
        for (int i = 0; i < 20; ++i) {
            int val = r.nextInt(100);
            bstm.put(val, val);
            bstm.makePic("pics/tree" + i);
        }

    }

    public static void testAVLMap() {
        new File("pics").mkdir();

        // 20 random adds
        AVLMap<Integer, Integer> avlm = new AVLMap<>();
        for (int i = 0; i < 20; ++i) {
        int val = r.nextInt(100);
        avlm.put(val, val);
        avlm.makePic("pics/tree" + i);
        }

        // combination of adds and removes
        AVLMap<Integer, Integer> avlm2 = new AVLMap<>();
        avlm2.put(20, 20);
        avlm2.put(10, 10);
        avlm2.makePic("pics/avlm0");
        avlm2.put(5, 5);
        avlm2.makePic("pics/avlm1");

        avlm2.put(7, 7);
        avlm2.makePic("pics/avlm2");

        avlm2.put(50, 50);
        avlm2.makePic("pics/avlm3");

        avlm2.put(500, 500);
        avlm2.makePic("pics/avlm4");
        avlm2.put(30, 30);
        avlm2.printInOrder();
        avlm2.makePic("pics/avlm5");
        System.out.println("add 25");
        avlm2.put(25, 25);
        avlm2.makePic("pics/avlm6");
        avlm2.printInOrder();

        avlm2.put(28, 28);
        avlm2.put(22, 22);
        avlm2.remove(28);
        avlm2.put(33, 33);
        avlm2.put(31, 31);
        avlm2.remove(31);
        avlm2.put(32, 32);
        avlm2.put(38, 38);
        System.out.println("put 38");
        avlm2.printInOrder();
        avlm2.makePic("pics/avlm7");

        System.out.println("about to remove");
        avlm2.remove(30);

        System.out.println("Did remove");
        avlm2.makePic("pics/avlm8");

    }

    public static double CONVERT = 1_000_000_000.0;

    public static void test(AVLMap<Integer, Integer> avlm, int size) {
        for (int i = 0; i < size; ++i) {
            int val = r.nextInt(100_000_000);
            avlm.put(val, val);
        }
    }

    public static void AVLTimings() {
        final int MAX = 1_000_000;
        for (int size = 10; size < MAX; size *= 5) {
            var count = 0.0;
            for (int i = 0; i < 10; i++) {
                AVLMap<Integer, Integer> avlm = new AVLMap<>();
                var start = System.nanoTime();
                test(avlm, size);
                var elapsed = System.nanoTime() - start;
                count += elapsed;
            }
            var avg = count / 10;
            System.out.println(size + " add's took, on average, " + avg / CONVERT + " secs");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        testAVLMap();
        AVLTimings();

        // The complexity of a sequence of n adds to an empty tree would be O(nlogn) becuase we are doing n puts
        // which each take logn time. I can see this in my timings in that I graphed the sizes and corresponding
        // times in Excel and saw a graph that was logarithmic in nature.
    }
}
