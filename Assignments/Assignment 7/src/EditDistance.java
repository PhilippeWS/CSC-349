import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

class EditDistance {
    private static String[] sequences;
    private static int[][] resultTable;
    private static ArrayList<Character> resultSequence1;
    private static ArrayList<Character> resultSequence2;


    //One function to manage all necessary sub functions, for testing ease.
    static void findEditDistance(String filename, boolean displayAlignment) throws FileNotFoundException {
        //Reads in the file and delegates the sequences
        readfile(filename);
        //Initializes and fills result table
        fillTable();
        //Runs traceback and displays desired output to console.
        displayResult(displayAlignment);
        //Resets the static class variables to allow program to be run again.
        resetData();
    }

    static void resetData(){
        sequences = null;
        resultTable = null;
        resultSequence1 = null;
        resultSequence2 = null;
    }

    //Reads in the source file, puts the larger string as string 1, smaller string as string 2.
    private static void readfile(String filename) throws FileNotFoundException {
        FileInputStream in = new FileInputStream(filename);
        Scanner sc = new Scanner(in);
        String[] strings = new String[2];

        strings[0] = sc.next();
        strings[1] = sc.next();

        //Compare the two, if the fist ones bigger, swap.
        if (strings[0].length() < strings[1].length()) {
            String temp = strings[0];
            strings[0] = strings[1];
            strings[1] = temp;
        }
        sequences = strings;
    }

    //Initialize the resultTable
    private static void initializeResultTable(){
        //If there are no read in sequences, there is nothing to do.
        if(sequences != null){
            //The table has first string.length+1 columns, second string.length+1 rows.
            resultTable = new int[(sequences[0].length()+1)][(sequences[1].length()+1)];

            //As to initialize the first row, there are gaps always after the first comparison
            //since a string of length 1 is being compared to string of length i.
            for(int i = 0; i <= sequences[0].length();i++)
                resultTable[i][0] = i*2;

            //As to initialize the first col, there are gaps always after the first comparison
            //since a string of length 1 is being compared to string of length j.
            for(int j = 0; j <= sequences[1].length();j++)
                resultTable[0][j] = j*2;

        }
    }

    private static void fillTable(){
        //Init table
        initializeResultTable();

        //Iterate over ever row and column.
        for(int i = 1; i <= sequences[0].length(); i++){
            for(int j = 1; j <= sequences[1].length(); j++){
                //Two Scenarios, either the two match characters match in teh string, and 0 is added
                // to the edit distance...
                if (sequences[0].charAt(i - 1) == sequences[1].charAt(j - 1))
                    resultTable[i][j] = resultTable[i - 1][j - 1];
                //Or the two characters do not match, which then we must decide either leaving as a mismatch
                //or adding a gap to either of the strings.
                else {
                    resultTable[i][j] = Math.min(Math.min(resultTable[i - 1][j] + 2, resultTable[i][j - 1] + 2), resultTable[i - 1][j - 1] + 1);
                }
            }
        }
    }

    private static void traceback(){
        ArrayList<Character> string1 = new ArrayList<>();
        ArrayList<Character> string2 = new ArrayList<>();

        int i = sequences[0].length(), j = sequences[1].length();
        while(i > 0 && j > 0){
            //Characters are equal, so just go back diagonally
            if(sequences[0].charAt(i-1) == sequences[1].charAt(j-1)){
                string1.add(sequences[0].charAt(--i));
                string2.add(sequences[1].charAt(--j));
            }
            //Otherwise, the characters do not match, so the move was either a gap or a mismatch
            else{
                int min = Math.min(resultTable[i-1][j], resultTable[i][j-1]);
                //Added a gap to get to next result
                if(min == (resultTable[i][j]-2)) {
                    //Went up in the table to get the minimum, thus added a gap to string 2.
                    if(resultTable[i-1][j] == min){
                        string1.add(sequences[0].charAt(--i));
                        string2.add('-');
                    }
                    //Went left in the table to get the minimum, thus added a gap to string 1.
                    else{
                        string1.add('-');
                        string2.add(sequences[1].charAt(--j));
                    }
                }
                //Left as a mismatch, so decrement once.
                else{
                    string1.add(sequences[0].charAt(--i));
                    string2.add(sequences[1].charAt(--j));
                }
            }
        }

        //One string may finish first, and since we always put the longer one first,
        //We must also finish its additions. Since there are no more letters in string2,
        //We just add gaps.
        while(i-- > 0) {
            string1.add(sequences[0].charAt(i));
            string2.add('-');
        }

        //Reverse the results to get the right ordering.
        Collections.reverse(string1);
        Collections.reverse(string2);
        //Set them to their respective results.
        resultSequence1 = string1;
        resultSequence2 = string2;
    }

    private static void displayResult(boolean displayAlignment){
        //Compute Traceback.
        traceback();

        //Get edit Distance
        int editDistance = resultTable[sequences[0].length()][sequences[1].length()];

        //Display Edit Distance
        System.out.println("Edit Distance = " + editDistance);

        //Option to display alignment or not. If true, do.
        if(displayAlignment) {
            for (int i = 0; i < resultSequence1.size(); i++) {
                int scoreLabel = resultSequence1.get(i) == resultSequence2.get(i) ? 0 : ((resultSequence1.get(i) == '-' || resultSequence2.get(i) == '-') ? 2 : 1);
                System.out.print(resultSequence1.get(i) + " " + resultSequence2.get(i) + " " + scoreLabel + "\n");
            }
        }
    }
}
