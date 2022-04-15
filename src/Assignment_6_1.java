import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Assignment_6_1 {

    /*Static Database*/
    static String[][] database = {
            {"I1", "I2", "I5"},
            {"I2", "I4"},
            {"I2", "I3"},
            {"I1", "I2", "I4"},
            {"I1", "I3"},
            {"I2", "I3"},
            {"I1", "I3"},
            {"I1", "I2", "I3", "I5"},
            {"I1", "I2", "I3"}
    };

    static Map<String, Integer> order = new HashMap<String, Integer>() {{
        put("I1", 1);
        put("I2", 2);
        put("I3", 3);
        put("I4", 4);
        put("I5", 5);
    }};

    public static void main(String[] args) {
        solve();
    }

    static void solve() {
        Integer minSupport = 3;
        HashSet<String> items = new HashSet<String>();
        for (int i = 0; i < database.length; i++) {
            for (String s :
                    database[i]) {
                items.add(s);
            }
        }
        System.out.println("\n[Frequent ItemSets] - Assignment_2 [Part-1] Static DB \n");
        System.out.println("[Hash Function] - (x*10+y)%7 and (x*10-y*5+z)%7 \n");
        Hashing_2x(minSupport, items, 10);
        Hashing_3x(minSupport, items, 10, 5);
        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||\n");
        System.out.println("\n[CUSTOM Hash Function] - (x+y)%7 and (x-y+z)%7 \n");
        Hashing_2x(minSupport, items, 1);
        Hashing_3x(minSupport, items, 1, 1);
    }

    /*Function doing hashing for item-set of size 2*/
    static void Hashing_2x(Integer minSupport, HashSet<String> items, int a) {
        String[] temp = new String[items.size()];
        items.toArray(temp);

        HashMap<String, Integer> itemSets = new HashMap<String, Integer>();
        for (int i = 0; i < temp.length; i++) {
            for (int j = i + 1; j < temp.length; j++) {
                String set = temp[i] + " " + temp[j];
                itemSets.put(set, 0);
            }
        }

        for (Map.Entry itemSet : itemSets.entrySet()) {
            Integer itemSetCount = 0;
            String[] checkItems = itemSet.getKey().toString().split(" ");
            for (String[] transaction : database) {
                int cnt = 0;
                for (String itm :
                        transaction) {
                    if (itm.compareTo(checkItems[cnt]) == 0) {
                        cnt++;
                    }
                    if (cnt >= checkItems.length)
                        break;
                }
                if (cnt >= checkItems.length) {
                    itemSetCount++;
                }
            }
            itemSets.put(itemSet.getKey().toString(), itemSetCount);
        }
        HashMap<Integer, Integer> hash_bin_freq = new HashMap<Integer, Integer>();
        HashMap<Integer, ArrayList<String>> bucket_content = new HashMap<>();


        for (Map.Entry itm :
                itemSets.entrySet()) {
            Integer supportCount = (int) itm.getValue();
            if (supportCount != 0) {
                Integer hashVal = getHash_2x(itm.getKey().toString(), a);
                if (hash_bin_freq.containsKey(hashVal)) {
                    hash_bin_freq.put(hashVal, (hash_bin_freq.get(hashVal) + (int) itm.getValue()));
                    ArrayList<String> t = bucket_content.get(hashVal);
                    t.add(itm.getKey().toString());
                    bucket_content.put(hashVal, t);
                } else {
                    ArrayList<String> new_Array = new ArrayList<>();
                    new_Array.add(itm.getKey().toString());
                    hash_bin_freq.put(hashVal, (int) itm.getValue());
                    bucket_content.put(hashVal, new_Array);
                }
            }
        }
        System.out.println("[Frequent item-set of size (2)]");
        for (Map.Entry itm : bucket_content.entrySet()) {
            int hashVal = (int) itm.getKey();
            int bucketCount = hash_bin_freq.get(hashVal);
            if (bucketCount >= minSupport) {
                System.out.println("[Bucket hash-val] " + hashVal + " | [Bucket Count] " + bucketCount);
                System.out.println("[Item-Sets] : ");
                for (String i :
                        bucket_content.get(hashVal)) {
                    System.out.print(i + " | ");
                }
                System.out.println("\n+-+-+-+-+-+-+-+-");
            }
        }
    }

    /*Function doing hashing for item-set of size 3*/
    static void Hashing_3x(Integer minSupport, HashSet<String> items, int a, int b) {
        System.out.println();

        String[] temp = new String[items.size()];
        items.toArray(temp);
        HashMap<String, Integer> itemSets = new HashMap<String, Integer>();
        for (int i = 0; i < temp.length; i++) {
            for (int j = i + 1; j < temp.length; j++) {
                for (int k = j + 1; k < temp.length; k++) {
                    String set = temp[i] + " " + temp[j] + " " + temp[k];
                    itemSets.put(set, 0);
                }
            }
        }

        for (Map.Entry itemSet : itemSets.entrySet()) {
            Integer itemSetCount = 0;
            String[] checkItems = itemSet.getKey().toString().split(" ");
            for (String[] transaction : database) {
                int cnt = 0;
                for (String itm :
                        transaction) {
                    if (itm.compareTo(checkItems[cnt]) == 0) {
                        cnt++;
                    }
                    if (cnt >= checkItems.length)
                        break;
                }
                if (cnt >= checkItems.length) {
                    itemSetCount++;
                }
            }
            itemSets.put(itemSet.getKey().toString(), itemSetCount);
        }

        HashMap<Integer, Integer> hash_bin_freq = new HashMap<Integer, Integer>();
        HashMap<Integer, ArrayList<String>> bucket_content = new HashMap<>();


        for (Map.Entry itm :
                itemSets.entrySet()) {
            Integer supportCount = (int) itm.getValue();
            if (supportCount != 0) {
                Integer hashVal = getHash_3x(itm.getKey().toString(), a, b);
                if (hash_bin_freq.containsKey(hashVal)) {
                    hash_bin_freq.put(hashVal, (hash_bin_freq.get(hashVal) + (int) itm.getValue()));
                    ArrayList<String> t = bucket_content.get(hashVal);
                    t.add(itm.getKey().toString());
                    bucket_content.put(hashVal, t);
                } else {
                    ArrayList<String> new_Array = new ArrayList<>();
                    new_Array.add(itm.getKey().toString());
                    hash_bin_freq.put(hashVal, (int) itm.getValue());
                    bucket_content.put(hashVal, new_Array);
                }
            }
        }

        System.out.println("[Frequent item-set of size (3)]");
        for (Map.Entry itm : bucket_content.entrySet()) {
            int hashVal = (int) itm.getKey();
            int bucketCount = hash_bin_freq.get(hashVal);
            if (bucketCount >= minSupport) {
                System.out.println("[Bucket hash-val] " + hashVal + " | [Bucket Count] " + hash_bin_freq.get(hashVal));
                System.out.println("[Item-Sets] : ");
                for (String i :
                        bucket_content.get(hashVal)) {
                    System.out.print(i + " | ");
                }
                System.out.println("\n+-+-+-+-+-+-+-+-");
            }
        }
    }
    /*Function calculating hash for item-set of size 2 with coff a*/
    static int getHash_2x(String itemSet, int a) {
        String[] items = itemSet.split(" ");
        int x = order.get(items[0]);
        int y = order.get(items[1]);
        return (x * a + y) % 7;
    }

    /*Function calculating hash for item-set of size 3 with coff a and b*/
    static int getHash_3x(String itemSet, int a, int b) {
        String[] items = itemSet.split(" ");
        int x = order.get(items[0]);
        int y = order.get(items[1]);
        int z = order.get(items[2]);
        return (x * a + y * b + z) % 7;
    }
}
