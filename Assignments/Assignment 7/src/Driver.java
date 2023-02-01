import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
        do{
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Enter a test filename, \"standard\" to run preset tests, or quit: ");
            String input = keyboard.nextLine();
            if(input.equalsIgnoreCase("quit")) break;
            System.out.println("Display alignment?: ");
            String option = keyboard.nextLine();
            if(option.equalsIgnoreCase("quit")) break;
            boolean displayAlignment = Boolean.parseBoolean(option);

            if(input.equalsIgnoreCase("standard")){
                runStandardTests(displayAlignment);
                System.out.println();
            }else {
                try {
                    EditDistance.findEditDistance(input, displayAlignment);
                    System.out.println();
                } catch (FileNotFoundException e) {
                    System.out.println("File not found.");
                }
            }
        }while(true);
    }

    private static void runStandardTests(boolean displayAlignment){
        String[] testFileNames = { "projtest1.txt", "projtest2.txt", "projtest3.txt", "projtest4.txt" };
        for(String testFile : testFileNames){
            try {
                EditDistance.findEditDistance(testFile, displayAlignment);
            } catch (FileNotFoundException e) {
                System.out.println("File " + testFile +" Missing.");
            }
        }
    }
}
