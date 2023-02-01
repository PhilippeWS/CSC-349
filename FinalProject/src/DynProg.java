import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class DynProg {
    private static List<Item> allItems;
    private static int[][] table;
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

    //Takes in the list of items, computes fills table with one funciton, computes traceback on table with anoter.
    private static List<Item> compute(List<Item> items, int capacity) {
        allItems = new ArrayList<>(items);
        allItems.add(0, new Item(0,0,0));
        table = new int[allItems.size()+1][capacity+1];

        fillTable(allItems.size(), capacity);
        List<Item> result = traceback(allItems.size(), capacity);
        Collections.reverse(result);
        return result;
    }

    //Initializes and fills the table based on the recurrence relation.
    private static void fillTable(int n, int capacity){
        for(int i = 0; i < n+1; i++){
            table[i][0] = 0;
        }
        for(int j = 0; j < capacity+1; j++){
            table[0][j] = 0;
        }

        for(int i = 1; i < n+1; i++){
            for(int j = 1; j < capacity+1; j++){
                int weightStep =  j-allItems.get(i-1).getWeight();

                int abstain = table[i-1][j];
                int take = table[i-1][Math.max(weightStep, 0)] + allItems.get(weightStep < 0 ? 0 : i-1).getValue();
                table[i][j] = Math.max(abstain, take);
            }
        }
    }

    //Performs traceback on the table to find the solution.
    private static List<Item> traceback(int n, int capacity){
        List<Item> result = new ArrayList<>();
        int j = capacity;
        for(int i = n; i > 0; i--){
            if(table[i][j] != table[i-1][j]){
                Item itemTaken = allItems.get(i-1);
                j -= itemTaken.getWeight();
                if(j < 0) break;
                result.add(itemTaken);
            }
        }
        return result;
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
        System.out.println("Dynamic Programming solution: Value " + tValue +
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
