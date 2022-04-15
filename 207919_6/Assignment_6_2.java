import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Assignment_6_2 {
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

    public static void main(String[] args) {
        solve(3);
    }

    static void solve(int partitionSize) {
        int minSupport = (int) Math.ceil(35.00 * (database.length) / 100.00);
        int minSupportPerPartition = (int) Math.ceil(35.00 * (partitionSize) / 100.00);

        String[] transformedDatabase = databaseTransformation();
        Map<Integer, ArrayList<Integer>> partitions = generatePartitions(partitionSize);
        HashSet<String> distinctFrequentItemsSetsAllPartitions = new HashSet<>();

//        int i = 0;
        for (Map.Entry itr :
                partitions.entrySet()) {
            ArrayList<String> frequentItemSetsPerPartition = getFrequentItemSets(transformedDatabase, (ArrayList<Integer>) itr.getValue(), minSupportPerPartition);
//            System.out.println("[Partition : " + (i+1) + "]");
            for (String itemSet :
                    frequentItemSetsPerPartition) {
                distinctFrequentItemsSetsAllPartitions.add(itemSet);
//                System.out.println(itemSet);
            }
//            System.out.println("----------------");
//            i++;
        }

        /*Final Step : to do 2nd traversal in DB using the frequent item-sets came from all partitions*/
        ArrayList<String> finalFrequentItemSets = generateFrequentItemSets(transformedDatabase, minSupport, distinctFrequentItemsSetsAllPartitions);
        HashSet<String> finalDistinctFrequentItemSets = new HashSet<>();

        for (String itmSet :
                finalFrequentItemSets) {
            finalDistinctFrequentItemSets.add(itmSet);
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter("output_7_p2.txt");
            System.out.println("[Frequent ItemSets]");
            for (String frequentItemSet :
                    finalDistinctFrequentItemSets) {
                System.out.println(frequentItemSet);
                writer.write(frequentItemSet );
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static Map<Integer, ArrayList<Integer>> generatePartitions(int partitionSize) {
        Map<Integer, ArrayList<Integer>> possiblePartitions = new HashMap<>();
        for (int i = 0; i < database.length; i += partitionSize) {
            ArrayList<Integer> elementsInPartition = new ArrayList<>();
            for (int j = i; j < i + partitionSize; j++) {
                elementsInPartition.add(j);
            }
            possiblePartitions.put(i / partitionSize, elementsInPartition);
        }
        return possiblePartitions;
    }

    static String[] databaseTransformation() {
        ArrayList<ArrayList<String>> dataset = new ArrayList<>();

        for (int i = 0; i < database.length; i++) {
            ArrayList<String> transaction = new ArrayList<String>(List.of(database[i]));
            dataset.add(transaction);
        }

        String[] transformedDataset = new String[database.length];
        int i = 0;
        for (ArrayList<String> itr :
                dataset) {
            String transaction = "T" + (i + 1);
            for (String itm :
                    itr) {
                transaction += " " + itm;
            }
            transformedDataset[i] = transaction;
            i++;
        }
        return transformedDataset;
    }

    static ArrayList<String> getFrequentItemSets(String[] transformedDB, ArrayList<Integer> datasetIndexes, int minSupport) {
        ArrayList<String> frequentItemSets = new ArrayList<>();
        ArrayList<ArrayList<String>> dataset = new ArrayList<>();

        String[] transformedDataset = new String[datasetIndexes.size()];
        for (int i = 0; i < datasetIndexes.size(); i++) {
            transformedDataset[i] = transformedDB[datasetIndexes.get(i)];
        }

        frequentItemSets = generateFrequentItemSets(transformedDataset, minSupport, null);
        return frequentItemSets;
    }

    static ArrayList<String> frequentItemSetHelper(String[] db, double minSupport, HashSet<String> items, HashSet<String> givenItemSets) {
        HashSet<String> itemSets = givenItemSets == null ? new HashSet<String>() : givenItemSets;
        String[] temp = new String[items.size()];
        items.toArray(temp);
        int n = temp.length;

        if (givenItemSets == null) {
            for (int i = 0; i < 1 << n; i++) {
                String str = "";
                for (int j = 0; j < n; j++) {
                    if ((i & (1 << j)) > 0) {
                        str += temp[j] + " ";
                    }
                }
                if (str.length() > 0)
                    itemSets.add(str);
            }
        }

        HashMap<String, Integer> frequentItemSets = new HashMap<String, Integer>();
        for (String itemSet : itemSets) {
            Integer itemSetCount = 0;
            String[] checkItems = itemSet.split(" ");
            for (String transaction : db) {
                String[] transactionTokens = transaction.split(" ");
                HashSet<String> checkItemHash = new HashSet<String>();
                if (transactionTokens.length >= checkItems.length) {
                    for (String str : checkItems) {
                        checkItemHash.add(str);
                    }
                    for (int i = 1; i < transactionTokens.length; i++) {
                        if (checkItemHash.contains(transactionTokens[i])) {
                            checkItemHash.remove(transactionTokens[i]);
                        }
                    }
                    if (checkItemHash.isEmpty()) {
                        itemSetCount++;
                    }
                }
            }
            double support = itemSetCount;
            if (support >= minSupport) {
                frequentItemSets.put(itemSet, itemSetCount);
            }
        }

        ArrayList<String> frequentItems = new ArrayList<>();
        for (Map.Entry mapElement : frequentItemSets.entrySet()) {
            String frequentItem = (String) mapElement.getKey();
            Integer frequentItemCount = (Integer) mapElement.getValue();
            frequentItems.add(frequentItem);
        }
        return frequentItems;
    }

    static ArrayList<String> generateFrequentItemSets(String[] db, int minSupport, HashSet<String> givenItemSet) {
        HashSet<String> items = new HashSet<String>();
        for (String str : db) {
            String[] tokens = str.split(" ");
            for (int i = 1; i < tokens.length; i++) {
                items.add(tokens[i]);
            }
        }
        return frequentItemSetHelper(db, minSupport, items, givenItemSet);
    }
}
