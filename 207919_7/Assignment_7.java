import com.sun.source.tree.Tree;

import java.util.*;
import java.io.*;

class Node {
    public String itm;
    public int itmCount;

    Node(String item, int count) {
        this.itm = item;
        this.itmCount = count;
    }
};

class TreeNode {
    public ArrayList<TreeNode> children = null;
    public String val;
    public int cnt;

    TreeNode(String nodeVal, int nodeCount) {
        this.val = nodeVal;
        this.cnt = nodeCount;
        children = new ArrayList<>();
    }
}

public class Assignment_7 {
    static ArrayList<String> database = null;
    static ArrayList<Node> sortedItemsList = null;
    static ArrayList<String> modifiedDB = null;
    static TreeNode root = null;
    static HashMap<String, ArrayList<TreeNode>> headerTable = null;
    static HashMap<String, HashMap<String, Integer>> conditionalPatternBase = null;
    static HashMap<String, HashSet<String>> conditionalFPTreeNodePatterns = null;
    static Integer currentHeaderTableIndex = 0;

    static void getDatabase() {
        File file = null;
        database = new ArrayList<>();
        try {
            file = new File("database.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                database.add(reader.nextLine());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void solve() {
        final Integer minSupport = 2;
        getDatabase();
        HashMap<String, Integer> oneItemSet = new HashMap<>();
        for (String str : database) {
            String[] tokens = str.split(" ");
            for (int i = 1; i < tokens.length; i++) {
                String itr = tokens[i];
                if (oneItemSet.containsKey(itr)) {
                    oneItemSet.put(itr, oneItemSet.get(itr) + 1);
                } else {
                    oneItemSet.put(itr, 1);
                }
            }
        }

        sortItems(oneItemSet);
        sortDB(oneItemSet);
        root = makeFPTree(modifiedDB, false);
        lvlOrderTraversal(root);
        makeHeaderTable(oneItemSet);
        generateConditionalPatternBase(sortedItemsList);
        generateConditionalFPTree(minSupport);
        generateFrequentItemSetsFromConditionalFPTree();
    }

    static void sortItems(HashMap<String, Integer> oneItemSet) {
        // Sorting and storing the one length item-set as per their frequency
        sortedItemsList = new ArrayList<>();
        HashMap<String,Integer> tmp = new HashMap<>(oneItemSet);
        System.out.println();
        System.out.println("---------------------- SORTED ITEM LIST ----------------------");
        for (int i = 0; i < tmp.size(); i++) {
            Integer mx = Integer.MIN_VALUE;
            String maxKey = "";
            for (Map.Entry<String, Integer> j : tmp.entrySet()) {
                if (mx < (int) j.getValue()) {
                    mx = Math.max(mx, (int) j.getValue());
                    maxKey = (String) j.getKey();
                }
            }
            // if (mx > minSupport) {
            System.out.print(maxKey + "(" + mx + ")" + " ");
            sortedItemsList.add(new Node((String) maxKey, (int) mx));
            // }
            tmp.put(maxKey, -1);
        }
        System.out.println();
        System.out.println();
    }

    static void sortDB(HashMap<String, Integer> sortedItemsMap) {
        modifiedDB = new ArrayList<>();
        for (String str : database) {
            String[] tokens = str.split(" ");
            for (int i = 1; i < tokens.length; i++) {
                for (int j = i + 1; j < tokens.length; j++) {
                    String x = tokens[i];
                    String y = tokens[j];
                    if (sortedItemsMap.get(x) < sortedItemsMap.get(y)) {
                        String tmp = x;
                        tokens[i] = y;
                        tokens[j] = tmp;
                    }
                }
            }
            String out = "";
            for (String tmp : tokens) {
                out += tmp + " ";
            }
            modifiedDB.add(out);
        }

        System.out.println("---------------------- SORTED DB ----------------------");
        for (String entry:
             modifiedDB) {
            System.out.println(entry);
        }
        System.out.println();
    }

    static TreeNode makeFPTree(ArrayList<String> DB, boolean initValFlag) {
        TreeNode newRoot = new TreeNode("NULL", -1);
        for (String str : DB) {
//            System.out.println(str);
            String[] tokens = str.split(" ");
            TreeNode curr = newRoot;
            for (int i = 1; i < tokens.length; i++) {
                curr = insertTree(curr, tokens[i], initValFlag ? Integer.parseInt(tokens[0]) : 1);
            }
            // lvlOrderTraversal(root);
        }
        return newRoot;
    }

    static TreeNode insertTree(TreeNode node, String val, Integer initValue) {
        for (TreeNode child : node.children) {
            // System.out.println(node.val + " - " + child.val + " || " + val);
            if (child.val.equals(val)) {
                child.cnt += initValue;
                return child;
            }
        }
        TreeNode newNode = new TreeNode(val, (initValue > 1 ? initValue : 1));
        node.children.add(newNode);
        return newNode;
    }

    static void makeHeaderTable(Map<String, Integer> oneItemSet) {
        headerTable = new HashMap<>();
        for (Map.Entry<String, Integer> item : oneItemSet.entrySet()) {
            headerTable.put(item.getKey(), new ArrayList<TreeNode>());
        }
        preorderTraversal(root, headerTable);
        System.out.println("\n------------- Header Table -------------");
        for (Map.Entry<String, ArrayList<TreeNode>> itm : headerTable.entrySet()) {
            System.out.println("[" + itm.getKey() + "]");
            System.out.print('\t');
            for (TreeNode node : itm.getValue()) {
                System.out.print(node.val + "(" + node.cnt + ") ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static void generateConditionalPatternBase(ArrayList<Node> oneItemSet) {
        conditionalPatternBase = new HashMap<>();
        for (Node node : oneItemSet) {
            ArrayList<TreeNode> headerTableEntries = headerTable.get(node.itm);
            HashMap<String, Integer> possiblePatterns = new HashMap<>();
            String currentPattern = "";
            currentHeaderTableIndex = 0;
            // System.out.println(node.itm);
            preorder_pattern_record(root, currentPattern, possiblePatterns, headerTableEntries, node);
            // System.out.println("-------------");
            conditionalPatternBase.put(node.itm, possiblePatterns);
        }

        // [DEBUG]
        System.out.println("----------- CONDITIONAL PATTERN BASE -------------");
        for (Map.Entry<String, HashMap<String, Integer>> patternBase : conditionalPatternBase.entrySet()) {
            System.out.println("[" + patternBase.getKey() + "]");
            for (Map.Entry<String, Integer> pattern : patternBase.getValue().entrySet()) {
                System.out.println("\t" + pattern.getKey() + " (" + pattern.getValue() + ")");
            }
            System.out.println();
        }
        System.out.println();
    }

    static void generateConditionalFPTree(Integer minSupport) {
        conditionalFPTreeNodePatterns = new HashMap<>();
        System.out.println("----------- CONDITIONAL FP TREE ----------- ");
        for (String key : conditionalPatternBase.keySet()) {
//            System.out.println("[" + key + "]");
            ArrayList<String> DB = new ArrayList<>();
            Integer transNo = 1;
            for (String itemSet : conditionalPatternBase.get(key).keySet()) {
                Integer transactionCount = conditionalPatternBase.get(key).get(itemSet);
                String transaction = transactionCount.toString() + " " + itemSet;
                DB.add(transaction);
            }
//            for (String dbEntry : DB) {
//                System.out.println("\t" + dbEntry);
//            }

            if (DB.size() > 0) {
                TreeNode currNode = makeFPTree(DB, true);
                pruneLeafNodes(currNode, minSupport);
                HashSet<String> allPossiblePatterns = new HashSet<>();
                generateAllConditionalFPTreeBranchTraversals(currNode,"",allPossiblePatterns);
                conditionalFPTreeNodePatterns.put(key,allPossiblePatterns);
            }
//            System.out.println(":::::::::::::::::::::::::::::::::");
//            System.out.println();
        }
        for (String key : conditionalFPTreeNodePatterns.keySet()) {
            System.out.println("[" + key + "]");
            for (String itmSet : conditionalFPTreeNodePatterns.get(key)) {
                System.out.println("\t" + itmSet);
            }
            System.out.println();
        }
        System.out.println();
    }

    static void generateFrequentItemSetsFromConditionalFPTree() {
        System.out.println("------------- FREQUENT ITEM SETS --------------");
        for (String key : conditionalFPTreeNodePatterns.keySet()) {

            HashSet<String> items = new HashSet<String>();
            for (String str : conditionalFPTreeNodePatterns.get(key)) {
                String[] tokens = str.split(" ");
                for(int i = 0; i < tokens.length; i++) {
                    items.add(tokens[i]);
                }
            }

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
                    itemSets.add(str + " " + key);
            }

            System.out.println("[" + key + "]");
            System.out.print("\t");
            for (String set: itemSets) {
                System.out.print("{ " + set + " } ");
            }
            System.out.println();
            System.out.println();
            System.out.println();
        }
        System.out.println();
    }

    static void generateAllConditionalFPTreeBranchTraversals(TreeNode root, String currentPattern, HashSet<String> allPossiblePatters) {
        if (root == null)   return;
        if (!root.val.equals("NULL"))   currentPattern += root.val + " ";

        if (root.children.size() == 0) {
            allPossiblePatters.add(currentPattern);
            return;
        }

        for (TreeNode child : root.children)
            generateAllConditionalFPTreeBranchTraversals(child,currentPattern,allPossiblePatters);
    }

    static void pruneLeafNodes(TreeNode node, Integer minSupport) {
        if (node == null) return;
        for (int i = 0; i < node.children.size(); i++) {
            TreeNode child = node.children.get(i);

            if (child.cnt < minSupport) {
                node.children.remove(i);
                i--;
            } else {
                pruneLeafNodes(child, minSupport);
            }
        }
    }

    static void preorder_pattern_record(TreeNode root, String currentPattern, HashMap<String, Integer> possiblePattern, ArrayList<TreeNode> headerTableEntries, Node currNode) {
        if (root == null) {
            return;
        } else if (currentHeaderTableIndex >= headerTableEntries.size()) {
            return;
        } else if (headerTableEntries.get(currentHeaderTableIndex).val.equals(root.val) && headerTableEntries.get(currentHeaderTableIndex).cnt == root.cnt) {
            // System.out.print(headerTableEntries.get(currentHeaderTableIndex).val + " -- " + root.val + " : " + currentPattern);
            // System.out.println();
            if (currentPattern.length() > 0) {
                currentPattern = currentPattern.substring(0, currentPattern.length() - 1);
                possiblePattern.put(currentPattern, root.cnt);
            }
            currentHeaderTableIndex += 1;
            return;
        }
        if (root.cnt != -1) {
            currentPattern += root.val + " ";
        }
        for (TreeNode child : root.children) {
            preorder_pattern_record(child, currentPattern, possiblePattern, headerTableEntries, currNode);
        }
    }

    static void preorderTraversal(TreeNode root, HashMap<String, ArrayList<TreeNode>> headerTable) {
        if (root == null) {
            return;
        }
        if (root.cnt != -1) {
            ArrayList<TreeNode> tmp = headerTable.get(root.val);
            tmp.add(root);
            headerTable.put(root.val, tmp);
        }
        for (TreeNode child : root.children) {
            preorderTraversal(child, headerTable);
        }
    }

    static void lvlOrderTraversal(TreeNode root) {
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        System.out.println("----------- Level Order Traversal -----------");
        int i = 0;
        while (!q.isEmpty()) {
            int n = q.size();
            System.out.println("Level : " + i);
            System.out.print('\t');
            while (n > 0) {
                TreeNode front = q.poll();
                System.out.print(front.val + "(" + front.cnt + ") ");
                for (TreeNode child : front.children) {
                    q.add(child);
                }
                n -= 1;
            }
            i += 1;
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        solve();
    }
};
