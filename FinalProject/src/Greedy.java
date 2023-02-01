import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Greedy {
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

    //Takes in the list of items, sorts items by weight value ratio, greedily takes top one.
    private static List<Item> compute(List<Item> items, int capacity) {
        List<Item> itemsCopy = new ArrayList<>(items);
        //Sort the items by value/weight ratio.
        Comparator<Item> valueWeightComparator = Comparator.comparing(item -> -((double)item.value)/item.weight);
        itemsCopy.sort(valueWeightComparator);

        List<Item> result = new ArrayList<>();
        int currentWeight = 0;
        for(Item item : itemsCopy){
            if(currentWeight + item.getWeight() > capacity) break;
            currentWeight += item.getWeight();
            result.add(item);
        }

        //Resort to be in ascending order of id's
        result.sort(Comparator.comparing(Item::getId));
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
        System.out.println("Greedy solution (not necessarily optimal): Value " + tValue +
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
