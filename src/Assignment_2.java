import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.util.*;

public class Assignment_2 {

    static final String[] staticDataset = {
            "T1 a b c e",
            "T2 b d f",
            "T3 a c d f",
            "T4 d f",
            "T5 c d e"
    };

    static String[] randomDataset = null;

    static String generateTransactions(int range) {
        HashSet<Character> transactions = new HashSet<Character>();
        Random r = new Random();
        for (int i = 0; i < range; i++) {
            int itemNo = r.nextInt(26);
            if(transactions.contains((char)(97 + itemNo))) {
                range++;
            }else {
                transactions.add((char)(97 + itemNo));
            }
        }
        String transactionsString = "";
        for (Character c:
             transactions) {
            transactionsString += (c + " ");
        }
        return transactionsString;
    }

    static void databaseCreation(Integer range) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("database.txt");

            Scanner sc = new Scanner(System.in);
            Random r = new Random();
            int n = r.nextInt(range) + 1;
            randomDataset = new String[n];

            for(int i = 1; i <= n; i++) {
                int noOfTransactions = r.nextInt(range) + 1;
                String transaction = "T" + i + " " + generateTransactions(noOfTransactions);
                writer.write(transaction + '\n');
                randomDataset[i-1] = transaction;
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void readTransactions(File fileObject) {
        Scanner reader = null;

        try {
            reader = new Scanner(fileObject);
            System.out.println("");
            System.out.println("******** TRANSACTIONS ********");
            while(reader.hasNextLine()) {
                String transaction = reader.nextLine();
                System.out.println(transaction);
            }
            System.out.println("******************************");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reader.close();
    }

    static void findCountOfItems(File fileObject) {
        Scanner reader = null;
        File outputObj = null;
        FileWriter writer = null;
        try {
            reader = new Scanner(fileObject);
            outputObj = new File("output_3.txt");;
            outputObj.createNewFile();
            writer = new FileWriter("output_3.txt");

            HashMap<String, Integer> map = new HashMap<String, Integer>();

            while(reader.hasNextLine()) {
                String transaction = reader.nextLine();
                String[] transactionArray = transaction.split(" ");
                for(int i = 1; i < transactionArray.length; i++) {
                    String item = transactionArray[i];
                    if(map.containsKey(item)) {
                        map.put(item, map.get(item) + 1);
                    }else {
                        map.put(item, 1);
                    }
                }
            }

            System.out.println("******* Count of Items *******");
            for(Map.Entry<String,Integer> entry : map.entrySet()) {
                String output = entry.getKey() + " " + entry.getValue();
                System.out.println(output);
                writer.write(output + '\n');
            }
            System.out.println("******************************");

            writer.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void extractList(File fileObject) {
        Scanner reader = null;
        File outputObj = null;
        FileWriter writer = null;

        try {
            reader = new Scanner(fileObject);
            outputObj = new File("output_4.txt");;
            outputObj.createNewFile();
            writer = new FileWriter("output_4.txt");

            HashMap<String, List<String>> map = new HashMap<String, List<String>>();

            while(reader.hasNextLine()) {
                String transaction = reader.nextLine();
                String[] transactionArray = transaction.split(" ");
                String key = transactionArray[0];
                for(int i = 1; i < transactionArray.length; i++) {
                    String item = transactionArray[i];
                    if(map.containsKey(item)) {
                        map.get(item).add(key);
                    }else {
                        List<String> initialList = new ArrayList<>();
                        initialList.add(key);
                        map.put(item, initialList);
                    }
                }
            }

            System.out.println("******* Item-Transaction List *******");
            for(Map.Entry<String,List<String>> entry : map.entrySet()) {
                String output = entry.getKey() + " " + entry.getValue();
                System.out.println(output);
                writer.write(output + '\n');
            }
            System.out.println("******************************");

            writer.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void frequentItemSetHelper(double minSupport,HashSet<String> items, String database) {
        HashSet<String> itemSets = new HashSet<String>();
        String[] temp = new String[items.size()];
        items.toArray(temp);
        int n = temp.length;

        for(int i = 0; i < 1 << n; i++) {
            String str = "";
            for(int j = 0; j < n; j++) {
                if( ( i & ( 1 << j )) > 0) {
                    str += temp[j] + " ";
                }
            }
            if (str.length() > 0)
                itemSets.add(str);
        }
        String[] db = null;
        if(database == "a1") {
            db = staticDataset;
        }else {
            db = randomDataset;
        }

        HashMap<String,Integer> frequentItemSets = new HashMap<String,Integer>();
        for (String itemSet: itemSets) {
            Integer itemSetCount = 0;
            String[] checkItems = itemSet.split(" ");
            for (String transaction: db) {
                String[] transactionTokens = transaction.split(" ");
                HashSet<String> checkItemHash = new HashSet<String>();
                if(transactionTokens.length >= checkItems.length) {
                    for (String str: checkItems) {
                        checkItemHash.add(str);
                    }
                    for (int i = 1; i < transactionTokens.length; i++) {
                        if(checkItemHash.contains(transactionTokens[i])) {
                            checkItemHash.remove(transactionTokens[i]);
                        }
                    }
                    if(checkItemHash.isEmpty()) {
                        itemSetCount++;
                    }
                }
            }
            double support = itemSetCount;
            if(support >= minSupport) {
                frequentItemSets.put(itemSet,itemSetCount);
            }
        }

        FileWriter writer = null;
        try {
            if(database == "a1") {
                writer = new FileWriter("output_5_a1.txt");
            }else if (database == "a2") {
                writer = new FileWriter("output_5_a2.txt");
            }
            System.out.println("[ItemCount] [Item]");
            for(Map.Entry mapElement : frequentItemSets.entrySet()) {
                String frequentItem = (String) mapElement.getKey();
                Integer frequentItemCount = (Integer) mapElement.getValue();
                writer.write(frequentItemCount + " : " + frequentItem + '\n');
                System.out.println(frequentItemCount + " " + frequentItem);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void getFrequentItemSets() {
        double minSupport = 2;
        HashSet<String> items = new HashSet<String>();
        System.out.println("******* Frequent ItemSets - Assignment_1 DB *******");
        for (String str: staticDataset) {
            String[] tokens = str.split(" ");
            for(int i = 1; i < tokens.length; i++) {
                items.add(tokens[i]);
            }
        }
        frequentItemSetHelper(minSupport,items,"a1");
        System.out.println("******************************");

        items.clear();
        minSupport = (double) (randomDataset.length * 30.00)/100.00;
        System.out.println("******* Frequent ItemSets - Assignment_2 Random DB *******");
        for (String str: randomDataset) {
            String[] tokens = str.split(" ");
            for(int i = 1; i < tokens.length; i++) {
                items.add(tokens[i]);
            }
        }
        frequentItemSetHelper(minSupport,items,"a2");
        System.out.println("******************************");
    }

    public static void main(String args[]) {
        try {
            File fileObject = new File("database.txt");
            fileObject.createNewFile();

            /*
             * Question : 1
             * Took input of transactions from user and stored it into
             * the database.
             * */
            databaseCreation(9);

            /*
             * Question : 2
             * Reading the file created and displaying it in console
             * */
            readTransactions(fileObject);

            /*
             * Question : 3
             * Finding support/count of items given in all the transactions in DB
             * Output displayed in console
             * Output is also saved into output_3.txt
             * */
            findCountOfItems(fileObject);

            /*
             * Question : 4
             * Extracting item-transaction list
             * Output displayed in console
             * Output is also saved into output_4.txt
             * */
            extractList(fileObject);

            /*
             * Question : 5
             * Extracting frequent item-sets from transaction DB
             * For Assignment - 1 static DB and Assignment - 2 Random DB
             * minSupport = 2 : Assignment 1
             * minSupport = 30% of all transactions : Assignment 2
             * Output is also saved into output_5.txt
             * */
            getFrequentItemSets();
        }catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}
