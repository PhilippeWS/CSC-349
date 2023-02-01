import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class BandB_Old {
    private static List<Item> itemsSorted;

    public static void main(String[] args) {
        if(args.length > 0){
            for(int run = 0; run < args.length; run++){
                runAssist(args[run]);
            }
        }
    }

    private static void runAssist(String filename){
        List<Item> items = readFile(filename);
        int capacity = items.get(items.size()-1).getId();
        items.remove(items.size()-1);
        final long startTime = System.currentTimeMillis();
        List<Item> result = compute(items, capacity);
        final long endTime = System.currentTimeMillis();
        generateOutput(result, filename);
    }

    private static List<Item> compute(List<Item> items, int capacity) {
        //Set up ordered items
        itemsSorted = new ArrayList<>(items);
        Comparator<Item> itemComparator = Comparator.comparing(item -> (double)item.getValue()/item.getWeight());
        itemComparator.thenComparing(item -> item.weight);
        itemsSorted.sort(itemComparator.reversed());
        //Init result
        List<Item> result = new ArrayList<>();

        Comparator<Node> nodeComparator = (Comparator.comparingDouble(node -> node.upperBoundScore));
        PriorityQueue<Node> nodes = new PriorityQueue<>(nodeComparator.reversed());

        int[] initialSequence = new int[items.size()];
        Arrays.fill(initialSequence, 0);
        nodes.add(new Node(calculateBound(initialSequence, capacity, 0), initialSequence, 0));

        Node best = bestfs(nodes, capacity);

        for(int index = 0; index < best.sequence.length; index++){
            if(best.sequence[index] == 1){
                result.add(itemsSorted.get(index));
            }
        }
        result.sort(Comparator.comparing(item -> item.id));

        return result;
    }

    private static Node bestfs(PriorityQueue<Node> queue, int capacity) {
        Node parent = queue.peek();
        int[] bestAttributes = calculateSequence(parent.sequence);
        Node bestNode = parent;

        while(!queue.isEmpty() && bestAttributes[0] <= parent.upperBoundScore){
            parent = queue.poll();
            if (parent.level < itemsSorted.size()) {
                //Create Abstain Node

                Node abstain = new Node(calculateBound(parent.sequence, capacity, parent.level), parent.sequence, parent.level + 1);

                int[] chooseSequence = Arrays.copyOf(parent.sequence, parent.sequence.length);
                chooseSequence[parent.level] = 1;
                //Create Choose Node
                Node choose = new Node(calculateBound(chooseSequence, capacity, parent.level), chooseSequence, parent.level + 1);

                if (abstain.upperBoundScore > -1) {
                    queue.add(abstain);
                }
                if (choose.upperBoundScore > -1) {
                    queue.add(choose);
                }

            }else {
                int[] suspectSolutionAttributes = calculateSequence(parent.sequence);
                if (suspectSolutionAttributes[0] > bestAttributes[0]) {
                    bestAttributes = suspectSolutionAttributes;
                    bestNode = parent;
                }
            }
        }
        return bestNode;
    }

    private static double calculateBound(int[] sequence, int capacity, int level){
        int[] sequenceAttributes = calculateSequence(sequence);
        return weightValueBound(sequenceAttributes, capacity, level);
    }

    private static double weightValueBound(int[] attributes, int capacity, int level){
        if(attributes[1] > capacity){
            return -1;
        }else{
            return (attributes[0] + (capacity-attributes[1])*((double)itemsSorted.get(level).getValue()/itemsSorted.get(level).getWeight()));
        }
    }

    private static int[] calculateSequence(int[] sequence){
        int[] sequenceAttributes = new int[2];
        for(int i = 0; i < sequence.length; i++){
            if(sequence[i] == 1){
                sequenceAttributes[0] += itemsSorted.get(i).getValue();
                sequenceAttributes[1] += itemsSorted.get(i).getWeight();
            }
        }
        return sequenceAttributes;
    }


    private static void generateOutput(List<Item> result, String filename){
        int tValue = 0;
        int tWeight = 0;
        for(Item item : result){
            tValue += item.value;
            tWeight += item.weight;
        }
        System.out.println("Result for File: " + filename);
        System.out.println("Using Branch and Bound the best feasible solution found: Value " + tValue +
                ", Weight " + tWeight);

        for(Item item : result){
            System.out.print(item.id + " ");
        }
        System.out.print("\n\n");
    }


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

    private static class Node{
        double costScore;
        double upperBoundScore;
        int level;
        int[] sequence;

        Node(double upperBoundScore, int[] sequence, int level){
            this.upperBoundScore = upperBoundScore;
            this.sequence = sequence;
            this.level = level;
        }

        public String toString(){
            return "UB: " + upperBoundScore + " Lvl: " + level + " Seq: " +Arrays.toString(sequence);
        }
    }

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


