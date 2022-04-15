import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Assignment_3 {
    static int[] textbookData = { 4, 8, 15, 21, 21, 24, 25, 28, 34 };
    static Random r = new Random();


    public static void Q1() {
        // Indexes where value will be missing
        int idx1 = r.nextInt(20);
        int idx2 = r.nextInt(20);
        int[] arr = new int[20];

        // generating random data values for array of size 20
        for (int i = 0; i < arr.length; i++) {
            if (i == idx1 || i == idx2) {
                arr[i] = Integer.MIN_VALUE;
            } else {
                arr[i] = r.nextInt(100);
            }
        }

        System.out.println("!!! FILLING MISSING VALUES !!!");
        System.out.print("Original array: ");
        for (int i : arr) {
            if (i == Integer.MIN_VALUE) {
                System.out.print("_" + " ");
            }else {
                System.out.print(i + " ");
            }
        }
        System.out.println();

        // Technique 1 : Using global constant to represent an empty value
        // Integer.MIN_VALUE represents that no value is present at the index
        System.out.println("[METHOD 1] : Filling missing values with global constant (INT_MIN)");
        System.out.print("Modified Array : ");
        for (int i : arr) {
            if (i == Integer.MIN_VALUE) {
                System.out.print("INT_MIN" + " ");
            }else {
                System.out.print(i + " ");
            }
        }
        System.out.println();

        // Technique 2 : Using Measure of central tendency - mean , median , mode
        // We will use Mean to fill up empty indexes
        System.out.println("[METHOD 2] : Filling missing value with mean : ");
        double mean = 0;
        for (int i : arr) {
            if (i != Integer.MIN_VALUE) {
                mean += i;
            }
        }

        mean /= 18.0;

        System.out.print("Modified Array : ");
        for (int i : arr) {
            if (i == Integer.MIN_VALUE) {
                System.out.print(mean + " ");
            } else {
                System.out.print(i + " ");
            }
        }
        System.out.println();

        // Technique 3 - Using attribute mean to fill missing values
        // [Assumption] Categorizing  idx1 as even missing value and idx2 as odd missing value
        // We will use the mean of even and mean of odd sum to fill up the corresponding values
        System.out.println("[METHOD 3] : Filling missing value with class mean : ");
        int evenMean = 0;
        int evenCount = 0;
        int oddMean = 0;
        int oddCount = 0;
        for (int i = 0; i < 20; i++) {
            if (i != idx1 && i != idx2) {
                if (arr[i] % 2 == 0) {
                    evenMean += arr[i];
                    evenCount++;
                } else {
                    oddMean += arr[i];
                    oddCount++;
                }
            }
        }
        evenMean /= evenCount;
        oddMean /= oddCount;

        System.out.print("Modified Array : ");
        for (int i = 0; i < 20; i++) {
            if (i == idx1) {
                System.out.print(evenMean + " ");
                continue;
            }
            if (i == idx2) {
                System.out.print(oddMean + " ");
                continue;
            }
            System.out.print(arr[i] + " ");
        }
        System.out.println('\n');

    }

    public static void Q2() {
        int[] arr = new int[20];
        r = new Random();

        for (int i = 0; i < arr.length; i++) {
            arr[i] = r.nextInt(100);
        }

        System.out.println("!!! BINNING ON TEXTBOOK DATA !!!\n");
        Binning_Tech(textbookData, 3);
        System.out.println("!!! BINNING ON RANDOMLY GENERATED DATA !!!\n");
        Binning_Tech(arr, 5);
    }

    public static void Binning_Tech(int[] arr, int k) {
        int binFreq = k;
        int indx = 0;
        int[][] bins = new int[arr.length / binFreq][binFreq];
        Arrays.sort(arr);

        /*
         * Technique 1
         * Data is put into bins of equal frequencies (here of size 3)
         */
        System.out.println("Equal Frequency Bins :");
        for (int i = 0; i < arr.length / binFreq; i++) {
            System.out.print("Bin " + (int) (i + 1) + " : ");
            for (int j = 0; j < binFreq; j++) {
                bins[i][j] = arr[indx + j];
                System.out.print(arr[indx + j] + " ");
            }
            indx += binFreq;
            System.out.println();
        }
        System.out.println("\n");

        /*
         * Technique 2
         * Mean of each Bin is found and every element in that bin is replaced by its
         * respective mean
         */
        System.out.println("Smoothening by Bin Means :");
        for (int i = 0; i < arr.length / binFreq; i++) {
            // finding sum of each bin and then calculating its mean
            int s = 0;
            for (int j = 0; j < binFreq; j++) {
                s += bins[i][j];
            }
            s /= binFreq;

            System.out.print("Bin " + (int) (i + 1) + " : ");
            for (int j = 0; j < binFreq; j++) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
        System.out.println("\n");

        /*
         * Technique 3
         * Sort bins. Middle values in bin boundaries move to its closest neighbor value
         * with less distance.
         */
        System.out.println("Smoothening by Bin Boundary :");
        int[][] bins_smoothening = new int[arr.length / binFreq][binFreq];
        for (int i = 0; i < arr.length / binFreq; i++) {
            int maximum = bins[i][binFreq - 1];
            int minimum = bins[i][0];
            for (int j = 0; j < binFreq; j++) {
                if (j == 0 || j == binFreq - 1) {
                    bins_smoothening[i][j] = bins[i][j];
                } else {
                    bins_smoothening[i][j] = (maximum - bins[i][j]) > (bins[i][j] - minimum) ? minimum : maximum;
                }
            }
            System.out.print("Bin " + (int) (i + 1) + " : ");
            for (int j = 0; j < binFreq; j++) {
                System.out.print(bins_smoothening[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("\n");
    }

    public static void Q3() {
        System.out.println("!!! FILLING MISSING VALUES USING CLASS LABELS !!!\n");
        int[][] data = new int[10][2];

        // Two random index's for missing values
        r = new Random();
        int idx1 = r.nextInt(10);
        int idx2 = r.nextInt(10);

        // filling up 10 values 2 labels array with random values
        for (int i = 0; i < 10; i++) {
            if (i == idx1 || i == idx2) {
                data[i][0] = Integer.MIN_VALUE;
                data[i][1] = r.nextInt(2);
            } else {
                data[i][0] = r.nextInt(100);
                data[i][1] = r.nextInt(2);
            }
        }

        System.out.println("*** Generated Data ***");
        System.out.println("Data : \t Label : ");
        for(int i =0;i<10;i++) {
            if(i==idx1 || i==idx2) {
                System.out.println("--"+" \t\t "+data[i][1]);
            }else {
                System.out.println(data[i][0]+" \t\t "+data[i][1]);
            }
        }
        System.out.println();

        int mean_Label_0 = 0;
        int count_Label_0 = 0;
        int mean_Label_1 = 0;
        int count_Label_1 = 0;
        for (int i = 0; i < 10; i++) {
            if (i != idx1 && i != idx2) {
                if (data[i][1] == 0) {
                    mean_Label_0 += data[i][0];
                    count_Label_0++;
                } else {
                    mean_Label_1 += data[i][0];
                    count_Label_1++;
                }
            }
        }
        mean_Label_0 /= count_Label_0;
        mean_Label_1 /= count_Label_1;

        data[idx1][0] = data[idx1][1] == 0? mean_Label_0 : mean_Label_1;
        data[idx2][0] = data[idx2][1] == 0? mean_Label_0 : mean_Label_1;
        System.out.println("Corrected Data");
        System.out.println("Data : \t Label : ");
        for(int i = 0; i < 10; i++) {
            System.out.println(data[i][0]+" \t\t "+data[i][1]);
        }
        System.out.println("\n");
    }

    public static void Q4() {
        System.out.println("!!! NORMALISING RANDOMLY GENERATED DATA !!!\n");
        int[][] data = new int[20][2];
        r = new Random();

        // Filling up random data
        for(int i = 0; i < 20; i++) {
            data[i][0]= r.nextInt(0, 100);
            data[i][1]= r.nextInt(0,2);
        }

        System.out.println("[Generated data]: ");
        for(int i = 0; i < 20; i++) {
            System.out.println(data[i][0]+" \t "+data[i][1]);
        }

        /**
         *  [Method 1] : min max normalisation
         *  new minimum - 0.0
         *  new maximum - 1.0
         *  formula : v_new = ((v-curr_min)/(curr_max - curr_min))*(new_max-new_min)+new_min
         */
        int new_min = 0;
        int new_max = 1;
        int curr_max_0 = Integer.MIN_VALUE;
        int curr_min_0 = Integer.MAX_VALUE;
        int curr_max_1 = Integer.MIN_VALUE;
        int curr_min_1 = Integer.MAX_VALUE;
        for(int i = 0; i < 20; i++) {
            if(data[i][1] == 0) {
                curr_max_0 = Math.max(curr_max_0, data[i][0]);
                curr_min_0 = Math.min(curr_min_0, data[i][0]) ;
            } else {
                curr_max_1 = Math.max(curr_max_1, data[i][0]);
                curr_min_1 = Math.max(curr_min_1, data[i][0]);
            }
        }

        float[][] norm1_data = new float[20][2];
        for(int i = 0; i < 20; i++) {
            if(data[i][1] == 0){
                norm1_data[i][0] = (((data[i][0]-curr_min_0)/(curr_max_0-curr_min_0))*(new_max-new_min))+new_min;
                norm1_data[i][1] = 0;
            } else {
                norm1_data[i][0] = (((data[i][0]-curr_min_1)/(curr_max_1-curr_min_1))*(new_max-new_min))+new_min;
                norm1_data[i][1] = 1;
            }
        }
        System.out.println("\nMin Max Norm:");
        for(int i = 0; i < 20; i++) {
            System.out.println(norm1_data[i][0] + " \t " + (int)norm1_data[i][1]);
        }

        /**
         *  [Method 2] : z-score normalisation
         *  A - Mean of numbers with attribute A
         *  Sigma - std deviation of values with label A
         *  formula - v_new = (v - A)/Sigma
         */
        float mean_0 = 0;
        float count_0 = 0;
        float count_1 = 0;
        float mean_1 = 0;
        float std_dev_0 = 0;
        float std_dev_1 = 0;
        for(int i = 0; i < 20; i++) {
            if(data[i][1] == 0) {
                mean_0 += data[i][0];
                count_0++;
            } else {
                mean_1 += data[i][0];
                count_1++;
            }
        }

        mean_0 /= count_0;
        mean_1 /= count_1;

        for(int i = 0; i < 20; i++) {
            if(data[i][1] == 0) {
                std_dev_0 += ((data[i][0]-mean_0)*(data[i][0]-mean_0));
            } else {
                std_dev_1 += ((data[i][0]-mean_1)*(data[i][0]-mean_1));
            }
        }

        std_dev_0 /= count_0;
        std_dev_0 = (float)Math.sqrt(std_dev_0);
        std_dev_1 /= count_1;
        std_dev_1 = (float)Math.sqrt(std_dev_1);

        double[][] norm2_data = new double[20][2];
        for(int i = 0; i < 20; i++) {
            if(data[i][1] == 0) {
                norm2_data[i][0] = (data[i][0]-mean_0)/std_dev_0;
                norm2_data[i][1] = 0;
            } else {
                norm2_data[i][0] = (data[i][0]-mean_1)/std_dev_1;
                norm2_data[i][1] = 1;
            }
        }

        System.out.println("\nZ-Score Norm:");
        for(int i = 0; i < 20; i++) {
            System.out.println(String.format("%.2f",norm2_data[i][0]) + " \t " + (int)norm2_data[i][1]);
        }

        /**
         *  [Method 3] : Decimal scaling
         *  Find the normalisation factor 10^j for the largest value of particular label
         *  such that v/(10^j) < 0
         */
        Double[][] norm3_data = new Double[20][2];
        curr_max_0 = Integer.MIN_VALUE;
        curr_max_1 = Integer.MIN_VALUE;
        for(int i = 0; i < 20; i++) {
            if(data[i][1] == 0) {
                if(data[i][0] > curr_max_0) {
                    curr_max_0=data[i][0];
                }
            } else {
                if(data[i][0] > curr_max_1) {
                    curr_max_1=data[i][0];
                }
            }
        }

        int normalisation_factor_0 = 1;
        while(curr_max_0 > 0) {
            curr_max_0 /= 10;
            normalisation_factor_0 *= 10;
        }

        int normalisation_factor_1 = 1;
        while(curr_max_1 > 0) {
            curr_max_1 /= 10;
            normalisation_factor_1 *= 10;
        }

        for(int i = 0; i < 20; i++) {
            if(data[i][1] == 0) {
                norm3_data[i][0] = ((double)data[i][0])/normalisation_factor_0;
                norm3_data[i][1] = 0.00;
            } else {
                norm3_data[i][0] = ((double)data[i][0])/normalisation_factor_1;
                norm3_data[i][1] = 1.00;
            }
        }

        System.out.println("\nNormalisation by Decimal scaling :");
        for(int i = 0; i < 20; i++) {
            System.out.println(norm3_data[i][0] +" \t "+ Math.floor(norm3_data[i][1]));
        }
    }


    public static void main(String[] args) {
        /**
         * [Question : 1]
         * Implement any 3 missing values techniques, considering 20 random values with 2 missing position
         */
        Q1();

        /**
         * [Question : 2]
         * Implement Binning techniques,  considering 20 random values and text book data
         */
        Q2();

        /**
         * [Question : 3]
         * Implement missing values using class labels (10 values for column with 2 labels,  2 missing values)
         */
        Q3();

        /**
         * [Question : 4]
         * Normalization Techniques (look into page-113 )
         * Create your own data values for 2 columns and implement
         * all three normalization techniques.
         */
        Q4();
    }

}
