import com.sun.source.tree.BreakTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FullEnum {
    private static List<Item> allItems;
    //Gets all file names from cmd line, runs them.
    public static void main(String[] args) {
        if(args.length > 0){
            for(int run = 0; run < args.length; run++){
                runAssist(args[run]);
            }
        }
    }

    //For any file, gets all attributes and runs then computes a solution
    private static void runAssist(String filename){
        List<Item> items = readFile(filename);
        int capacity = items.get(items.size()-1).getId();
        items.remove(items.size()-1);
        List<Item> result = compute(items, capacity);
        generateOutput(result, filename);
    }

    //Takes in the list of items, computes runs function to compute every combination, returns the best found as list of items.
    private static List<Item> compute(List<Item> items, int capacity) {
        allItems = new ArrayList<>(items);
        List<Item> result = new ArrayList<>();
        String[] binaryCombination = fullEnum(allItems.size(), capacity);

        for(int index = 0; index < binaryCombination.length; index++){
            if(Integer.parseInt(binaryCombination[index]) == 1){
                result.add(allItems.get(index));
            }
        }
        return result;
    }

    //Iteratively compares every combination, utilizing the properties of binary strings.
    private static String[] fullEnum(int n, int capacity){
        int bestValue = 0;
        String[] bestCombination = {};
        for(int combination = 0; combination < Math.pow(2, n); combination++){
            //Converts iteration to a unique combination, adds necessary padding zeros.
            String binaryCombination = String.format("%0" + (n) + "d", new BigInteger(Integer.toBinaryString(combination)));
            String[] indices = binaryCombination.split("");

            //Calculates combination values.
            int combinationValue = 0, combinationWeight = 0;
            for (int binaryIndex = 0; binaryIndex < indices.length; binaryIndex++) {
                if(Integer.parseInt(indices[binaryIndex]) == 1){
                    combinationValue += allItems.get(binaryIndex).getValue();
                    combinationWeight += allItems.get(binaryIndex).getWeight();
                }
            }

            //If current combination is better than previous best, set.
            if(combinationWeight <= capacity &&  bestValue < combinationValue){
                bestValue = combinationValue;
                bestCombination = indices;
            }
        }

        return bestCombination;
    }

    //Generates a formatted console display.
    private static void generateOutput(List<Item> result, String filename){
        int tValue = 0;
        int tWeight = 0;
        for(Item item : result){
            tValue += item.value;
            tWeight += item.weight;
        }
        System.out.println("Result for File: " + filename);
        System.out.println("Using Brute force the best feasible solution found: Value " + tValue +
                ", Weight " + tWeight);

        for(Item item : result){
            System.out.print(item.id + " ");
        }
        System.out.print("\n\n");
    }

    //Reads in the input file.
    public static List<Item> readFile(String filename){
        FileInputStream in = null;
        try {
            in = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(in);

        List<Item> items = new ArrayList<>();
        sc.nextLine();

        while(sc.hasNext()){
            String[] fileLine = sc.nextLine().trim().split("\\s+");
            if(fileLine.length != 1){
                items.add(new Item(Integer.parseInt(fileLine[0]), Integer.parseInt(fileLine[1]), Integer.parseInt(fileLine[2])));
            }else{
                items.add(new Item(Integer.parseInt(fileLine[0]), -1, -1));
            }
        }
        return items;
    }

    //Item class to store item attributes.
    private static class Item {
        int id;
        int weight;
        int value;

        Item(int id, int value, int weight){
            this.id = id;
            this.weight = weight;
            this.value = value;
        }

        public int getId() { return this.id; }

        public int getWeight() { return this.weight; }

        public int getValue() { return this.value; }

        public String toString(){
            return "Id: " + id + ", Weight: " + weight + ", Value: " + value;
        }
    }
}
