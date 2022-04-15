import java.util.*;

public class MID_LAB {
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

    static void Q1_Part1(int[] data) {
        Arrays.sort(data);
        ArrayList<Integer> filteredData = new ArrayList<Integer>();
        System.out.println("!! Initial Data (sorted) !!");
        for (int x :
                data) {
            if (x == -1) {
                System.out.print("_ ");
            } else {
                System.out.print(x + " ");
            }
        }
        for (int x :
                data) {
            if (x != -1) {
                filteredData.add(x);
            }
        }
        System.out.println();
        Double percentileIndex = (75 * (filteredData.size() + 1)) / 100.00;
        int percentile75th = 0;
        if (Math.ceil(percentileIndex) != percentileIndex) {
            int low = (int) Math.floor(percentileIndex);
            int high = (int) Math.ceil(percentileIndex);
            percentile75th = (int) ((filteredData.get(low - 1) + filteredData.get(high - 1)) / 2.00);
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] == -1) {
                data[i] = percentile75th;
            }
        }
        Arrays.sort(data);
        System.out.println("!! Final Data - After replacing missing values (sorted) !!");
        for (int x :
                data) {
            System.out.print(x + " ");
        }
        System.out.println("\n[MODE] of data : " + percentile75th);
    }

    public static int[][] Binning_Helper(int[] data, int k) {
        int binFreq = k;
        int indx = 0;
        int[][] bins = new int[data.length / binFreq][binFreq];
        System.out.println("Reduced Data in bins with frequency (9) :");
        for (int i = 0; i < data.length / binFreq; i++) {
            System.out.print("Bin " + (int) (i + 1) + " : ");
            for (int j = 0; j < binFreq; j++) {
                bins[i][j] = data[indx + j];
                System.out.print(data[indx + j] + " ");
            }
            indx += binFreq;
            System.out.println();
        }
        return bins;
    }

    public static void variance(int[][] bins) {
        //var stores variance
        System.out.println("[Variance of each bin]");
        double[] var = new double[bins.length];
        for (int i = 0; i < bins.length; i++) {
            int mean = 0;
            for (int j = 0; j < bins[i].length; j++) {
                mean += bins[i][j];
            }
            mean /= bins[i].length;

            double v = 0;
            for (int k = 0; k < bins[i].length; k++) {
                double temp = (bins[i][k] - mean);
                temp *= temp;
                v += temp;
            }
            var[i] = (double) Math.sqrt(v);
            System.out.print("[Variance : Bin " + (i+1) + "] : " + v);
            System.out.println();
        }
    }

    static void Q1_Part2(int[] data) {
        int freq = 9;
        int[][] bins;
        bins = Binning_Helper(data, freq);
        double[] var;
        variance(bins);
    }

    static void Q2_Part1(double minSupport, HashSet<String> items) {
        String[] temp = new String[items.size()];
        items.toArray(temp);
        HashMap<String, Integer> itemSets = new HashMap<String, Integer>();
        for (int i = 0; i < temp.length; i++) {
            for(int j = i+1; j < temp.length; j++) {
                String set = temp[i] + " " + temp[j];
                itemSets.put(set,0);
            }
        }
        for (Map.Entry itemSet: itemSets.entrySet()) {
            Integer itemSetCount = 0;
            String[] checkItems = itemSet.getKey().toString().split(" ");
            for (String[] transaction: database) {
                int cnt = 0;
                for (String itm:
                     transaction) {
                    if(itm.compareTo(checkItems[cnt]) == 0) {
                        cnt++;
                    }
                    if(cnt >= checkItems.length)
                        break;
                }
                if(cnt >= checkItems.length) {
                    itemSetCount++;
                }
            }
            itemSets.put(itemSet.getKey().toString(),itemSetCount);
        }
        ArrayList<ArrayList<String>> hash = new ArrayList<ArrayList<String>>();
        for (Map.Entry itm:
             itemSets.entrySet()) {
            System.out.println(itm.getKey() + " : " + itm.getValue());
        }
    }

    static void Q2() {
        double minSupport = 3.00;
        HashSet<String> items = new HashSet<String>();
        for (int i = 0; i < database.length; i++) {
            for (String s :
                    database[i]) {
                items.add(s);
            }
        }
        System.out.println("******* Frequent ItemSets - Assignment_2 Static DB *******");
        System.out.println("******* Hash Function - (x*10+y)%7 and (x*10-y*5+z)%7 *******");
        Q2_Part1(minSupport, items);
    }

    public static void main(String[] args) {
        // 2,__,12,__,2,6,24,16,13,34,5,13,__,17,19,__,24,23,13,6,16,27,17,__,14,13,12
        int[] data = {
                2, -1, 12, -1, 2, 6, 24, 16, 13, 34, 5, 13, -1, 17, 19, -1, 24, 23, 13, 6, 16, 27, 17, -1, 14, 13, 12
        };
        /**
         * Question 1
         * Approach :
         * [Part 1] - Making seperate array for non missing values, then finding the 75th percentile among them
         * using formula idx = (75 * (data.size() + 1)) / 100, which will give us index of the 75th percentile element.
         * If the index is in floating value then take the average of elements the floor(idx) and ceil(idx). Then replace
         * the missing elements with the 75th percentile. Since no. of missing values are 5 that ensure the mode of the
         * given data will be 75th percentile of the remaining values
         * [Part 2] - I am using binning technique to reducing/cleaning above data. As the number of elements in data is
         * 27, frequency i am using for binning is factor of 27 i.e. 9 (in my case). In binning sorted data is divided into
         * bins of equal size of freq(9). Finally calculating the variance of each bin seperatly.
         */
        System.out.println("\t\t\t\t QUESTION 1\n");
        System.out.println("[PART : 1]");
        Q1_Part1(data);
        System.out.println("\n[PART : 2] (Using Binning)");
        Q1_Part2(data);
        System.out.println("----------------------------------------\n");
        /**
         * [OUTPUT]:
         * QUESTION 1
         *
         * [PART : 1]
         * !! Initial Data (sorted) !!
         * _ _ _ _ _ 2 2 5 6 6 12 12 13 13 13 13 14 16 16 17 17 19 23 24 24 27 34
         * !! Final Data - After replacing missing values (sorted) !!
         * 2 2 5 6 6 12 12 13 13 13 13 14 16 16 17 17 19 21 21 21 21 21 23 24 24 27 34
         * [MODE] of data : 21
         *
         * [PART : 2] (Using Binning)
         * Reduced Data in bins with frequency (9) :
         * Bin 1 : 2 2 5 6 6 12 12 13 13
         * Bin 2 : 13 13 14 16 16 17 17 19 21
         * Bin 3 : 21 21 21 21 23 24 24 27 34
         * [Variance of each bin]
         * [Variance : Bin 1] : 178.0
         * [Variance : Bin 2] : 58.0
         * [Variance : Bin 3] : 146.0
         */

        /**
         * Question 2
         * Approach: It is just like Apriori Algorithm but little bit of modification, We first make the itemset of
         * size 2 then find the support for each of the item sets, also corresponding value hash function
         * for each item set itemSet, if support value of item set is 0 we dont calculate its hash function value.
         * We use this hash value to put each item set into their designated hash bucket.
         *
         * We will do similar steps for item set of size 3 but with different hash function as given in the question
         *
         * Now we iterate though each hashed bucket and check the number of items in them, if the number of items in
         * hash bucket is less than the minimum support we ignore that bucket (i.e exclude it from frequent item set consideration)
         * and visa versa.
         *
         * Using this hashing technique we are minimizing the number of scans needed to be done for large database for each itemsets
         * at each step.
         */
        System.out.println("\t\t\t\t QUESTION 2\n");
        Q2();
        /**\
         * OUTPUT
         * QUESTION 2
         *
         * ******* Frequent ItemSets - Assignment_2 Static DB *******
         * ******* Hash Function - (x*10+y)%7 and (x*10-y*5+z)%7 *******
         * I1 I4 : 1
         * I2 I5 : 2
         * I1 I3 : 4
         * I2 I4 : 2
         * I1 I2 : 4
         * I2 I3 : 4
         * I3 I4 : 0
         * I4 I5 : 0
         * I3 I5 : 1
         * I1 I5 : 2
         */
    }
}
