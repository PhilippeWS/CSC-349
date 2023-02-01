import java.util.ArrayList;
import java.util.List;

public class HighestRungAlgorithm {
    public static void main(String[] args) {
        DestructiveTest destructiveTest = new DestructiveTest();
        ArrayList<Integer> result;
        for(int i = 100; i < 1000000000; i*=10) {
//            System.out.println("n-3--------------------------------------------------");
//            result = destructiveTest.findHighestSafeRung(i, i-3);
//            printResults(result);
//            System.out.println("--------------------------(n/2)-2--------------------");
//            result = destructiveTest.findHighestSafeRung(i, (i/2)-2);
//            printResults(result);
//            System.out.println("----------------------------------------------------2");
//            result = destructiveTest.findHighestSafeRung(i, 2);
//            printResults(result);
            System.out.println("--------------------Worst Case-----------------------");
            result = destructiveTest.findHighestSafeRung(i, i-1);
            printResults(result);
        }

    }
    private static void printResults(List<Integer> result){
        System.out.println("Ladder Height: " + result.get(0));
        System.out.println("Safe Rung: " + result.get(1));
        System.out.println("Algorithm Result: " + result.get(2));
        System.out.println("Total Drops: " + result.get(3));
//        System.out.println("First Drop: " + result.get(4));
//        System.out.println("Second Drop: " + result.get(5));
//        System.out.println("Third Drop: " + result.get(6));
//        System.out.println("First Break: " + result.get(7));
//        System.out.println("Second Break: " + result.get(8));
    }
}
