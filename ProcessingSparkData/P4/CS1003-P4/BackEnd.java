
//BackEnd
//15 April 2022
import org.apache.spark.*;
import org.apache.spark.api.java.*;
import java.util.*;

public class BackEnd {

    private String dataFilePath;
    private String searchPhrase;
    private double similarityTarget;

    public BackEnd(String dataFilePath, String searchPhrase, double similarityTarget) {
        this.dataFilePath = dataFilePath;
        this.searchPhrase = searchPhrase;
        this.similarityTarget = similarityTarget;
    }

    public void Search() {

        // Initialise Apprache Spark App 1
        SparkConf sparkConfiguration = new SparkConf()
                .setAppName("Appache Spark App 1")
                .setMaster("local[*]");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConfiguration);

        // Find length of user entered phrase
        int phraseWordCount = this.searchPhrase.split(" ").length;

        // Read txt files into RDD
        String[] files = new String[] { "11-0.txt", "84-0.txt",
                "98-0.txt", "844-0.txt", "1080-0.txt", "1342-0.txt",
                "2542-0.txt", "2701-0.txt", "5200-0.txt", "64317-0.txt" };
        for (String file : files) {
            JavaRDD<String> lines = sparkContext.textFile(dataFilePath
                    + "/" + file);
            for (String line : lines.collect()) {
                List<String> subsequences = ConstructSubsequences(line, phraseWordCount);
                compareSubsequences(subsequences);
            }
        }
    }

    // Generate a list of subsequences
    private List<String> ConstructSubsequences(String text, int n) {

        int noOfSubsequence;
        // Cleaning textual data (Code from spec)
        text = text.replaceAll("[^a-zA-Z0-9]", " ");
        text = text.toLowerCase();
        String[] words = text.split("[ \t\n\r]");
        int wordCount = words.length;

        List<String> subsequences = new ArrayList<>();
        // Formula for amount of subsequences required
        noOfSubsequence = wordCount - (n - 1);

        for (int i = 0; i < noOfSubsequence; i++) {
            String subsequence = "";
            for (int j = 0; j < n; j++) {
                subsequence += words[j + i] + " ";
            }

            subsequences.add(subsequence);
        }

        return subsequences;
    }

    private void compareSubsequences(List<String> subsequences) {
        Set<String> biGramSet1 = CalculateBiGram(this.searchPhrase);

        for (String subsequence : subsequences) {
            Set<String> biGramSetSubsequence = CalculateBiGram(subsequence);

            double similarityCoefficient = JaccardCoefficent(biGramSet1, biGramSetSubsequence);
            if (similarityCoefficient >= this.similarityTarget) {
                System.out.println(subsequence);
            }
        }
    }

    // Create BiGrams
    private static Set<String> CalculateBiGram(String stringToConvert) {

        Set<String> biGramSet = new HashSet<>();

        for (int i = 0; i < stringToConvert.length() - 1; i++) {
            char char1 = stringToConvert.charAt(i);
            char char2 = stringToConvert.charAt(i + 1);

            String biGram = char1 + "" + char2;
            biGramSet.add(biGram);
        }

        return biGramSet;
    }

    // Calculate Jaccard Coefficent
    private static double JaccardCoefficent(Set<String> set1,
            Set<String> set2) {

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return ((double) intersection.size()) / ((double) union.size());
    }
}