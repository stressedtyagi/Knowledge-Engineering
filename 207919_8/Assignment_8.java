import java.io.File;
import java.io.IOException;
import java.util.*;

public class Assignment_8 {
    static final Integer K = 3;
    static ArrayList<String> database = null;
    static ArrayList<ArrayList<Integer>> centers = null;
    static HashMap<Integer,ArrayList<String>> clusters = null;

    static void solve() {
        getDatabase();
        generateInitialClusters();
        KMEANS_Algorithm();
    }

    static void getDatabase() {
        File file = null;
        database = new ArrayList<>();
        try {
            file = new File("database_A8.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                database.add(reader.nextLine());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void generateInitialClusters() {
        centers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < K; i++) {
            Integer randomIndex = random.nextInt(database.size());
            String[] coordinates = database.get(randomIndex).split(" ");
            Integer x = Integer.parseInt(coordinates[0]);
            Integer y = Integer.parseInt(coordinates[1]);
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(x);
            tmp.add(y);
            centers.add(tmp);
        }

        updateClusters();

        System.out.println("-------------- Initial Clusters --------------");
        printClusters();
        System.out.println();
    }

    static void KMEANS_Algorithm() {
        boolean change = true;
        Integer iterationNo = 1;
        while (change) {
            change = false;
            // Updating centers with K means
            for (Map.Entry<Integer,ArrayList<String>> mp : clusters.entrySet()) {
                Integer newX = 0;
                Integer newY = 0;
                for (String coordinate : mp.getValue()) {
                    Integer x = Integer.parseInt(coordinate.split(" ")[0]);
                    Integer y = Integer.parseInt(coordinate.split(" ")[1]);
                    newX += x;
                    newY += y;
                }
                newX /= mp.getValue().size();
                newY /= mp.getValue().size();
                if (centers.get(mp.getKey()).get(0) != newX || centers.get(mp.getKey()).get(1) != newY) {
                    centers.set(mp.getKey(), new ArrayList<>(Arrays.asList(newX,newY)));
                    change = true;
                }
            }

            // If there is any change in centers then update the clusters
            if (change) {
                updateClusters();
                System.out.println("++++++++++++++ Iteration " + iterationNo + " ++++++++++++++");
                printClusters();
                System.out.println();
            }
            iterationNo++;
        }
    }

    static void updateClusters() {
        clusters = new HashMap<>();
        for (String coordinate : database) {
            Integer x = Integer.parseInt(coordinate.split(" ")[0]);
            Integer y = Integer.parseInt(coordinate.split(" ")[1]);
            Integer assignedClusterIndex = 0;
            Integer mn = Integer.MAX_VALUE;
            for (int i = 0; i < centers.size(); i++) {
                ArrayList<Integer> center = centers.get(i);
                Integer cX = center.get(0);
                Integer cY = center.get(1);
                Integer dist = calDistance(x,y,cX,cY);
                if (mn > dist) {
                    assignedClusterIndex = i;
                    mn = dist;
                }
            }
            if (clusters.containsKey(assignedClusterIndex)) {
                ArrayList<String> tmp = clusters.get(assignedClusterIndex);
                tmp.add(coordinate);
                clusters.put(assignedClusterIndex,tmp);
            } else {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(coordinate);
                clusters.put(assignedClusterIndex, tmp);
            }
        }
    }

    static void printClusters() {
        for (Map.Entry<Integer,ArrayList<String>> mp : clusters.entrySet()) {
            Integer cX = centers.get(mp.getKey()).get(0);
            Integer cY = centers.get(mp.getKey()).get(1);
            System.out.println("[Center] : (" + cX + "," + cY + ")");
            for (String knn : mp.getValue()) {
                System.out.println("\t\t(" + knn.split(" ")[0] + "," + knn.split(" ")[1] + ")");
            }
        }
    }

    static Integer calDistance(Integer x1, Integer y1, Integer x2, Integer y2) {
        return (int)Math.floor(Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2,2)));
    }

    public static void main(String[] args) {
        solve();
    }
}
