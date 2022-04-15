/*
 * Copyright (c) 2022.
 * Divyanshu Tyagi
 * 207919, MCA
 */

import java.io.*;
import java.util.*;

/**
 * 1. Predicting computer_buys class, that's why calculating info gain for that
 * 2. Select the column(attribute) having maximum gain at each iteration of DTreeNode
 * 3. Also, Update DB everytime we calculate Information Gain and Entropy while calculating gain at each level.
 * 4. Using Queue<Pair<DTreeNode,UpdatedDB>> to keep track of updated tables at each node, also doing preorder traversal of tree to build it
 * 5. Stop when there is only one class attribute to predict, that will be our leaf nodes
 * 6. Code is written to handel any type of dataset, with any number of columns and rows. Also, Each attribute can have any number of different types of outcomes.
 *    Also, predicted class can have any number of different types of predictions i.e. code can handel non-binary (more than 2) predicted class labels.
 * 7. All commented code is for debug purpose, you can uncomment it all and see each and every step clearly in a readable format with proper indentation.
 *
 *
 * [NOTE] : Database is stored in database_A9.txt with following structuring rules
 * A. First line of file should have names of each column in correct order, separated by single space
 * B. Following lines should have each tuple of table where each cell entry is separated by single space
 * C. Predicted class entries should always be in last column
 *
 * Example DB :
 * age income student credit_rating buys_computer
 * youth high no fair no
 * youth high no excellent no
 * middle_aged high no fair yes
 * senior medium no fair yes
 * senior low yes fair yes
 * senior low yes excellent no
 * middle_aged low yes excellent yes
 * youth medium no fair no
 * youth low yes fair yes
 * senior medium yes fair yes
 * youth medium yes excellent yes
 * middle_aged medium no excellent yes
 * middle_aged high yes fair yes
 * senior medium no excellent no
 *
 * [Note] : Meaning of symbols in output are as follows:
 * [-] : TreeNode
 * [+] : Branch Name
 * [=] : Leaf Node - Prediction
 * OUTPUT :
 * - [age] ->
 * 	+ (senior)
 * 		- [credit_rating] ->
 * 			+ (excellent)
 * 				= buys_computer : no
 * 			+ (fair)
 * 				= buys_computer : yes
 * 	+ (middle_aged)
 * 		= buys_computer : yes
 * 	+ (youth)
 * 		- [student] ->
 * 			+ (no)
 * 				= buys_computer : no
 * 			+ (yes)
 * 				= buys_computer : yes
 *
 */

class DTreeNode {
    public String label = null;
    public HashMap<String, DTreeNode> branch = null;

    public DTreeNode(String label) {
        this.label = label;
        this.branch = new HashMap<>();
    }

    public DTreeNode(String label, HashMap<String,DTreeNode> branch) {
        this.label = label;
        this.branch = branch;
    }
};

class Pair {
    public DTreeNode node = null;
    public ArrayList<String> DB = null;

    public Pair(DTreeNode node, ArrayList<String> DB) {
        this.node = node;
        this.DB = DB;
    }
};

public class Assignment_9 {
    // Question : Part [1] - DB, finding InfoGain class cell
    static ArrayList<String> database = null;
    static HashMap<Integer, String> class_labels = null;
    static ArrayList<ArrayList<String>> possible_outcome_per_class = null;

    // Question : Part [2] - Finding entropy of each cell
    static HashMap<String, Double> gain_of_each_column = null;

    static void solve() {
        getDatabase();
        DTreeNode root = makeDecisionTree();
        printDecisionTree(root, 0);
    }

    static void getDatabase() {
        File file = null;
        database = new ArrayList<>();
        class_labels = new HashMap<>();
        possible_outcome_per_class = new ArrayList<>(class_labels.size());
        HashMap<Integer, HashSet<String>> options_per_class_holder = new HashMap<>();
        try {
            file = new File("database_A9.txt");
            Scanner reader = new Scanner(file);
            int lineNo = 1;
            while (reader.hasNextLine()) {
                if (lineNo == 1) {
                    String[] labels = reader.nextLine().split(" ");
                    for (int i = 0; i < labels.length; i++)
                        class_labels.put(i, labels[i]);
                    lineNo++;
                } else {
                    String tuple = reader.nextLine();
                    database.add(tuple);
                    String[] tokens = tuple.split(" ");
                    HashSet<String> options = new HashSet<>();
                    for (int i = 0; i < tokens.length; i++) {
                        if (options_per_class_holder.containsKey(i)) {
                            HashSet<String> value = options_per_class_holder.get(i);
                            value.add(tokens[i]);
                            options_per_class_holder.put(i, value);
                        } else {
                            HashSet<String> tmp = new HashSet<String>();
                            tmp.add(tokens[i]);
                            options_per_class_holder.put(i, tmp);
                        }
                    }
                }
            }

            for (Map.Entry<Integer, HashSet<String>> it : options_per_class_holder.entrySet()) {
                int index = it.getKey();
                ArrayList<String> tmp = new ArrayList<>();
                for (String option : it.getValue()) {
                    tmp.add(option);
                }
                possible_outcome_per_class.add(tmp);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * @debug
         */
        // System.out.println("---- Class Labels ----");
        // for (Map.Entry<Integer,String> cell : class_labels.entrySet()) {
        //     System.out.println(cell.getKey() + " : " + cell.getValue());
        // }
        // System.out.println();
        // System.out.println("--- DB ---");
        // for (String tuple : database) {
        //     System.out.println(tuple);
        // }
        // System.out.println();
    }

    static Double calculateInfoGain(ArrayList<String> DB) {
        HashMap<String, Integer> possible_outcomes_count = new HashMap<>();
        for (String tuple : DB) {
            String cell = tuple.split(" ")[class_labels.size() - 1];
            if (possible_outcomes_count.containsKey(cell)) {
                possible_outcomes_count.put(cell, possible_outcomes_count.get(cell) + 1);
            } else {
                possible_outcomes_count.put(cell, 1);
            }
        }
        Integer total_tuples = database.size();
        Double total_info_gain = 0.0;
        for (Map.Entry<String, Integer> mp : possible_outcomes_count.entrySet()) {
            Double x = calculateInformationGain(mp.getValue(), total_tuples);
            total_info_gain += x;
        }
        return total_info_gain;
    }

    static HashMap<String, HashMap<String, HashMap<String, Integer>>> calculateExpectedEntropy(ArrayList<String> DB, Double total_information_gain) {
        gain_of_each_column = new HashMap<>();

        // Very awesome DS
        HashMap<String, HashMap<String, HashMap<String, Integer>>> counter_per_set = new HashMap<>();

        for (String db : DB) {
            String[] cells = db.split(" ");
            for (int i = 0; i < cells.length - 1; i++) {

                String columnLabel = cells[cells.length - 1];
                String rowLabel = cells[i];
                String className = class_labels.get(i);

                if (counter_per_set.containsKey(className)) {
                    HashMap<String, HashMap<String, Integer>> rowColumnMapWithCell = counter_per_set.get(className);
                    if (rowColumnMapWithCell.containsKey(rowLabel)) {
                        HashMap<String, Integer> columnMapWithCell = rowColumnMapWithCell.get(rowLabel);
                        if (columnMapWithCell.containsKey(columnLabel)) {
                            columnMapWithCell.put(columnLabel, columnMapWithCell.get(columnLabel) + 1);
                            rowColumnMapWithCell.put(rowLabel, columnMapWithCell);
                            counter_per_set.put(className, rowColumnMapWithCell);
                        } else {
                            columnMapWithCell.put(columnLabel, 1);
                            rowColumnMapWithCell.put(rowLabel, columnMapWithCell);
                            counter_per_set.put(className, rowColumnMapWithCell);
                        }
                    } else {
                        HashMap<String, Integer> colMapWithCell = new HashMap<>();
                        colMapWithCell.put(columnLabel, 1);
                        rowColumnMapWithCell.put(rowLabel, colMapWithCell);
                        counter_per_set.put(className, rowColumnMapWithCell);
                    }
                } else {
                    HashMap<String, Integer> colMapWithCell = new HashMap<>();
                    colMapWithCell.put(columnLabel, 1);
                    HashMap<String, HashMap<String, Integer>> rowMapWithCell = new HashMap<>();
                    rowMapWithCell.put(rowLabel, colMapWithCell);
                    counter_per_set.put(className, rowMapWithCell);
                }
            }
        }

//        System.out.println("----------- Tables for Entropy --------------");
        for (Map.Entry<String, HashMap<String, HashMap<String, Integer>>> mp : counter_per_set.entrySet()) {
//            System.out.println("[Class] : " + mp.getKey());
            Double info_per_class = 0.0;
            for (Map.Entry<String, HashMap<String, Integer>> row : mp.getValue().entrySet()) {
//                System.out.println("\t[Row] : " + row.getKey());
                Integer total_values = 0;
                for (Map.Entry<String, Integer> col : row.getValue().entrySet()) {
//                    System.out.println("\t\t[Col] : " + col.getKey() + ", [Value] : " + col.getValue());
                    total_values += col.getValue();
                }
                Double temp_sum = 0.0;
                for (Map.Entry<String, Integer> col : row.getValue().entrySet()) {
                    temp_sum += calculateInformationGain(col.getValue(), total_values);
                }
                info_per_class += ((double) total_values / database.size()) * temp_sum;
            }
            Double gain = total_information_gain - info_per_class;
            gain_of_each_column.put(mp.getKey(), gain);
//            System.out.println("\t[Info Gain] : " + info_per_class);
//            System.out.println("\t[Gain] : " + gain);
//            System.out.println();
        }
//        System.out.println();
        return counter_per_set;
    }

    static DTreeNode makeDecisionTree() {
        Double total_information_gain = calculateInfoGain(database);
//        System.out.println("[Total DB Information Gain] " + total_information_gain);
//        System.out.println("-----------------------------------------------------");

        HashMap<String, HashMap<String, HashMap<String, Integer>>> counter_per_set = calculateExpectedEntropy(database, total_information_gain);
        String rootVal = Collections.max(gain_of_each_column.entrySet(), Map.Entry.comparingByValue()).getKey();
        DTreeNode root = new DTreeNode(rootVal);
        Queue<Pair> queue = new LinkedList<>();
        queue.add(new Pair(root, database));

        while (!queue.isEmpty()) {
            Pair front = queue.poll();
            HashMap<String, DTreeNode> children = front.node.branch;
            DTreeNode curr_node = front.node;
            ArrayList<String> curr_DB = front.DB;
            for (Map.Entry<String, HashMap<String, Integer>> possible_options : counter_per_set.get(curr_node.label).entrySet()) {
                String curr_option = possible_options.getKey();
                ArrayList<String> newDB = updateDB(curr_DB, curr_node.label, curr_option);
/*
                System.out.println("+++++++++++++++++++++++++++++++++++++");
                System.out.println("[" + curr_node.label + " : " + curr_option + "]");
                for (String tuple : newDB) {
                    System.out.println("\t" + tuple);
                }
*/
                counter_per_set = calculateExpectedEntropy(newDB, calculateInfoGain(newDB));
                DTreeNode newNode = null;
                if (counter_per_set.get(curr_node.label).get(curr_option).size() > 1) {
                    String newNodeLabel = Collections.max(gain_of_each_column.entrySet(), Map.Entry.comparingByValue()).getKey();
                    newNode = new DTreeNode(newNodeLabel);
//                    System.out.println("[newNodeLabel] " + " --> " + newNodeLabel);
                    queue.add(new Pair(newNode, newDB));
                } else {
                    String result = "";
                    for (Map.Entry<String, Integer> mp : counter_per_set.get(curr_node.label).get(curr_option).entrySet()) {
                        result = mp.getKey();
                    }
                    newNode = new DTreeNode(class_labels.get(class_labels.size() - 1) +  " : " + result, null);
                }
//                System.out.println("+++++++++++++++++++++++++++++++++++++");
//                System.out.println();
                children.put(curr_option, newNode);
            }
        }
        return root;
    }

    static void printDecisionTree(DTreeNode root, int gap) {
        if (root.branch == null) {
            for (int i = 0; i < gap; i++) System.out.print('\t');
            System.out.print("= " + root.label);
            return;
        }
        for (int i = 0; i < gap; i++) System.out.print('\t');
        System.out.println("- [" + root.label + "] -> ");
        for (Map.Entry<String,DTreeNode> children : root.branch.entrySet()) {
            for (int i = 0; i < gap+1; i++) System.out.print('\t');
            System.out.println("+ (" + children.getKey() +  ")");
            printDecisionTree(children.getValue(), gap+2);
            System.out.println();
        }
    }

    static ArrayList<String> updateDB(ArrayList<String> curr_DB, String classLabel, String curr_option) {
        ArrayList<String> newDB = new ArrayList<>();
        for (String tuple : curr_DB) {
            String[] tokens = tuple.split(" ");
            Integer class_label_idx = 0;
            for (Map.Entry<Integer, String> label : class_labels.entrySet()) {
                if (label.getValue() == classLabel) {
                    class_label_idx = label.getKey();
                    break;
                }
            }
//            System.out.println(curr_option + " -- " + class_label_idx + " :: " + tokens[class_label_idx]);
            if (tokens[class_label_idx].equals(curr_option)) {
                newDB.add(tuple);
            }
        }
        return newDB;
    }

    static Double calculateInformationGain(Integer cnt, Integer n) {
        return -1 * ((double) cnt / n * (Math.log((double) cnt / n) / Math.log(2)));
    }

    public static void main(String[] args) {
        solve();
    }
}
