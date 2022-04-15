import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Assignment_1 {
    static void databaseCreation() {
        FileWriter writer = null;
        try {
            writer = new FileWriter("database.txt");

            Scanner sc = new Scanner(System.in);
            System.out.print("Enter number of transactions: ");
            int n = Integer.parseInt(sc.nextLine());

            for(int i = 1; i <= n; i++) {
                String transactionHeader = "T" + i;
                System.out.print(transactionHeader + " : ");
                String transaction = sc.nextLine();
                writer.write(transactionHeader + " " + transaction + '\n');
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

    public static void main(String args[]) {
        try {
            File fileObject = new File("database.txt");
            fileObject.createNewFile();

            /*
            * Question : 1
            * Took input of transactions from user and stored it into
            * the database.
            * */
            databaseCreation();
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
        }catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}
