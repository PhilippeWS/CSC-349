import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.*;

public class BandB {
    private static List<Item> itemsSorted;

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

    //Takes in the list of items, performs all necessary setup.
    private static List<Item> compute(List<Item> items, int capacity) {
        //Orders the items by value/weight ratio
        itemsSorted = new ArrayList<>(items);
        Comparator<Item> itemComparator = Comparator.comparing(item -> (double)item.getValue()/item.getWeight());
        itemComparator.thenComparing(item -> item.weight);
        itemsSorted.sort(itemComparator.reversed());
        //Initialize the result
        List<Item> result = new ArrayList<>();
        //Initialize the priority queue.
        Comparator<Node> nodeComparator = (Comparator.comparingDouble(node -> node.costScore));
        nodeComparator.thenComparingDouble(node -> node.upperBoundScore);
        PriorityQueue<Node> nodes = new PriorityQueue<>(nodeComparator.reversed());
        //Initialize the starting node.
        String initialSequence = String.format("%0" + (items.size()) + "d", new BigInteger(Integer.toBinaryString(0)));
        double[] costBound = calculateBounds(binaryStringToIntArray(initialSequence), capacity, 0);
        nodes.add(new Node(costBound[0], costBound[1], initialSequence, 0));
        //Perform Best-First Search to find our best node, max run time as X minutes.
        Node best = bestfs(nodes, capacity,2);
        //Take the best sequence, turn it into parsable array, and return result as items ordered in ascending id's
        int[] bestSequence = binaryStringToIntArray(best.sequence);
        for(int index = 0; index < bestSequence.length; index++){
            if(bestSequence[index] == 1){
                result.add(itemsSorted.get(index));
            }
        }
        result.sort(Comparator.comparing(item -> item.id));

        return result;
    }

    //Performs best-firstSearch on the SST, algorithm will halt if it runs past Max Run Time,
    // returns node containing the best solution found so far.
    private static Node bestfs(PriorityQueue<Node> queue, int capacity, long maxRunTime) {
        Node currentNode = queue.peek();
        double bestUB = currentNode.upperBoundScore;
        Node bestNode = currentNode;
        final long startTime = System.currentTimeMillis();
        while(!queue.isEmpty() && (System.currentTimeMillis()-startTime) < getMinutesInMilliseconds(maxRunTime)){
            currentNode = queue.poll();
            if (currentNode.level < itemsSorted.size()) {
                if(bestUB <= currentNode.costScore) {
                    //Generate Both child nodes, one to choose the next item, one to abstain from choosing the next item
                    Node[] children = generateChildren(currentNode.sequence, capacity, currentNode.level);
                    Node abstain = children[0];
                    Node choose = children[1];

                    //If either child is better than the best, and a valid possibility, set the best upperbound, track the best node.
                    if (calculateSequenceAttributes(binaryStringToIntArray(abstain.sequence), capacity)[1] != -1) {
                        queue.add(abstain);
                        if (bestUB <= abstain.costScore) {
                            bestUB = abstain.upperBoundScore;
                            bestNode = abstain;
                        }
                    }
                    if (calculateSequenceAttributes(binaryStringToIntArray(choose.sequence), capacity)[1] != -1) {
                        queue.add(choose);
                        if (bestUB <= choose.costScore) {
                            bestUB = choose.upperBoundScore;
                            bestNode = choose;
                        }
                    }
                }
            }else{
                //If the node is a leaf node, see if it is currently an optimal solution, if so set it.
                int[] valueWeight = calculateSequenceAttributes(binaryStringToIntArray(currentNode.sequence), capacity);
                if(valueWeight[1] != -1){
                    if(bestUB < valueWeight[0]){
                        bestUB = valueWeight[0];
                        bestNode = currentNode;
                    }
                }
            }
        }
        return bestNode;
    }

    //Given the current sequence, level, and overall capacity, generate the two next children.
    private static Node[] generateChildren(String sequence, int capacity, int level){
        //Sequence does not change, only level
        double[] abstainBoundCost = calculateBounds(binaryStringToIntArray(sequence), capacity, level + 1);
        Node abstain = new Node(abstainBoundCost[0], abstainBoundCost[1], sequence, level + 1);

        //Change the sequence to choose the next item.
        int[] chooseSequence = Arrays.copyOf(binaryStringToIntArray(sequence), itemsSorted.size());
        chooseSequence[level] = 1;
        double[] chooseBoundCost = calculateBounds(chooseSequence, capacity, level + 1);
        Node choose = new Node(chooseBoundCost[0], chooseBoundCost[1], sequenceToBinaryString(chooseSequence), level + 1);

        return new Node[]{abstain, choose};
    }

    //Calculates the two bounding scores, upper-bound and cost, and returns them in an integer array.
    private static double[] calculateBounds(int[] sequence, int capacity, int level){
        double[] boundCost = {0, 0};
        int remainingCapacity = capacity;
        int item = level;
        //First calculate the value of the current subset of items for both the bound and cost
        //Keep track of remaining capacity for the next greedy portion
        for (int i = 0; i < level; i++) {
            if (sequence[i] == 1) {
                boundCost[0] += itemsSorted.get(i).getValue();
                boundCost[1] += itemsSorted.get(i).getValue();
                remainingCapacity -= itemsSorted.get(i).getWeight();
            }
        }
        //Now, Greedily take items that have not yet been considered, until the knapsack is full.
        while (item < itemsSorted.size()) {
            if (remainingCapacity - itemsSorted.get(item).getWeight() < 0) break;
            remainingCapacity -= itemsSorted.get(item).getWeight();
            boundCost[0] += itemsSorted.get(item).getValue();
            boundCost[1] += itemsSorted.get(item).getValue();
            item++;
        }
        //With the last remaining capacity, fractionally add the value of the next item to the cost score.
        if(remainingCapacity > 0 && item < itemsSorted.size()){
            boundCost[1] += (double) itemsSorted.get(item).getValue() / itemsSorted.get(item).getWeight() * (double) remainingCapacity;
        }
        return boundCost;
    }

    //For any int[] array representing the binary string of a combination, calculate its weight and value,
    //and return that in a 2 length int array. Weight is -1 if > capacity.
    private static int[] calculateSequenceAttributes(int[] sequence, int capacity){
        int[] sequenceAttributes = new int[2];
        for(int i = 0; i < sequence.length; i++) {
            if (sequence[i] == 1) {
                sequenceAttributes[0] += itemsSorted.get(i).getValue();
                sequenceAttributes[1] += itemsSorted.get(i).getWeight();
            }
        }
        if(sequenceAttributes[1] > capacity) sequenceAttributes[1] = -1;
        return sequenceAttributes;
    }

//Utilizing Binary strings to store combinations in nodes to conserve memory. Originally used int[], but ran out of memory too quickly
    //Convert a binaryString to sequence
    private static int[] binaryStringToIntArray(String sequence){
        char[] numbers = sequence.toCharArray();
        int[] result = new int[numbers.length];
        for(int index = 0; index < numbers.length; index++){
            result[index] = Integer.parseInt(String.valueOf(numbers[index]));
        }
        return result;
    }

    //Convert sequence to binary String
    private static String sequenceToBinaryString(int[] sequence){
        StringBuilder stringSequence = new StringBuilder();
        for(int binary : sequence)
            stringSequence.append(binary);
        return stringSequence.toString();
    }
//---

    //Allows for easy setting of minutes allowed to run for best-fs
    private static long getMinutesInMilliseconds(long minutes){
        return 1000*60*minutes;
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
        System.out.println("Using Branch and Bound the best feasible solution found: Value " + tValue +
                ", Weight " + tWeight);

        for(Item item : result){
            System.out.print(item.id + " ");
        }
        System.out.print("\n\n");
    }

    //Reads in the input file.
    private static List<Item> readFile(String filename){
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

    //Node class used to build the SST, holds the both bounding scores, level, and current sequence.
    private static class Node{
        double costScore;
        double upperBoundScore;
        int level;
        String sequence;

        Node(double upperBoundScore, double costScore, String sequence, int level){
            this.upperBoundScore = upperBoundScore;
            this.costScore = costScore;
            this.sequence = sequence;
            this.level = level;
        }

        public String toString(){
            return "UB: " + upperBoundScore + " Cost: " + costScore + " Lvl: " + level + " Seq: " + sequence;
        }
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
    }}
