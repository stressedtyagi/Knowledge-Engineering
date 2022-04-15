import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
DB:
    T1: a(a1) b(b1) c(c1) d(d1) e(e1)
    T2: a f(b2) c g(d2) e
    T3: a f c d h(e2)
    T4: i(a2) b c d h
    T5: i b c d j(e3)
*/
public class Assignment_5 {
    static String[] staticDataset = {
            "T1 a b c d e",
            "T2 a f c g e",
            "T3 a f c d h",
            "T4 i b c d h",
            "T5 i b c d j"
    };

    public static void main(String[] args) {
        ArrayList<String> items;
        int r = 3;
        items = token(staticDataset);
        String[] raw_data = new String[staticDataset.length];
        for (int i = 0; i < staticDataset.length; i++) {
            String temp = staticDataset[i].substring(3);
            String[] trans_arr = temp.split(" ");
            String concat = String.join("", trans_arr);
            raw_data[i] = concat;
        }


        HashMap<String, Integer> frequency_list = new HashMap<>();

        System.out.println("Finally Selected itemSets");
        HashSet<String> combinations_set = new HashSet<>();
        String[] data = new String[r];
        HashSet<String> hs = new HashSet<>(items);
        int minsupport = 2;
        int k = 2;
        while (hs.size() != 0) {
            hs = frequency(hs, raw_data, frequency_list, k, minsupport);
            k++;
        }
        System.out.println("[ITEM]\t[SUPPORT]");
        for (HashMap.Entry<String, Integer> entry : frequency_list.entrySet()) {
            if(entry.getKey().length() >= 4) {
                System.out.println(entry.getKey() + "\t" + entry.getValue());
            } else {
                System.out.println(entry.getKey() + "\t\t" + entry.getValue());
            }

        }
        File file = new File("output.txt");
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));

            for (HashMap.Entry<String, Integer> entry :
                    frequency_list.entrySet()) {
                bf.write(entry.getKey() + ":"
                        + entry.getValue());
                bf.newLine();
            }

            bf.flush();
            bf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<String> token(String[] items) {
        Set<String> tokenised = new HashSet<String>();
        for (int i = 0; i < items.length; i++) {
            String[] trans_arr = items[i].split(" ");
            for (int j = 1; j < trans_arr.length; j++) {
                if (!(tokenised.contains(trans_arr[j]))) {
                    tokenised.add(trans_arr[j]);
                }

            }
        }
        ArrayList<String> s = new ArrayList<>(tokenised);
        return s;
    }

    public static void combination(HashSet<String> s, ArrayList<String> arr, String[] data, int start, int end, int index, int r) {
        if (index == r) {
            String temp = "";
            for (int i = 0; i < data.length; i++) {
                temp += data[i];
            }

            System.out.print(temp + " ");
            s.add(temp);
            System.out.println();
            return;
        }
        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = arr.get(i);
            combination(s, arr, data, i + 1, end, index + 1, r);
        }
    }

    public static HashSet<String> frequency(HashSet<String> combinations_set, String[] raw_data, HashMap<String, Integer> frequency_list, int k, int minsupport) {

        HashMap<String, Integer> curr_freq_list = new HashMap<>();
        for (int i = 0; i < raw_data.length; i++) {
            for (String s : combinations_set) {
                String temp = "";
                if (s.length() <= raw_data[i].length()) {
                    for (int j = 0; j < s.length(); j++) {
                        Character c = s.charAt(j);
                        if (raw_data[i].indexOf(c) != -1) {
                            temp += c;
                        }
                    }

                    if (s.equals(temp)) {
                        if (curr_freq_list.containsKey(s)) {
                            int x = curr_freq_list.get(s);
                            x += 1;
                            curr_freq_list.put(s, x);
                        } else {
                            curr_freq_list.put(s, 1);
                        }
                    }
                }
            }
        }

        HashSet<String> curr_items = new HashSet<>();
        for (String name : curr_freq_list.keySet()) {
            String key = name.toString();
            int value = curr_freq_list.get(name);
            if (value >= minsupport) {
                curr_items.add(key);
                frequency_list.put(key, value);
            } else {
                //add to global map//
            }
        }

        //join function
        HashSet<String> next_item_list = new HashSet<>();
        ArrayList<String> arr = new ArrayList<>();
        for (String s : curr_items) {
            arr.add(s);
        }
        if (k == 1) {
            String[] data = new String[2];
            combination(next_item_list, arr, data, 0, arr.size(), 0, 2);
        } else {
            for (int i = 0; i < arr.size(); i++) {
                for (int j = i + 1; j < arr.size(); j++) {
                    HashSet<Character> similar = new HashSet<>();
                    HashSet<Character> different = new HashSet<>();
                    for (int m = 0; m < arr.get(j).length(); m++) {
                        Boolean flag = false;
                        char c = arr.get(j).charAt(m);
                        for (int n = 0; n < arr.get(i).length(); n++) {
                            char d = arr.get(i).charAt(n);
                            if (c == d) {
                                flag = true;
                                similar.add(d);
                                break;
                            }
                        }
                        if (!flag) {
                            different.add(c);
                        }
                    }
                    if (similar.size() >= k - 2) {
                        for (Character c : different) {
                            String temp = arr.get(i) + c;
                            next_item_list.add(temp);
                        }
                    }
                }
            }
        }
        return next_item_list;
    }

}