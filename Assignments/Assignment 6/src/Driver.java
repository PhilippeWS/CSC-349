import java.util.Arrays;

public class Driver {
    public static void main(String[] args) {
        //Case 1
        int[] sTimes1 = {4,3,2,10,7};
        int[] fTimes1 = {7,10,6,13,9};
        int[] weights1 = {6,6,5,2,8};

        int[] res = WgtIntScheduler.getOptSet(sTimes1, fTimes1, weights1);

        System.out.println(Arrays.toString(res));

        //Case 2
        int[] sTimes2 = {3,3,1,10,8};
        int[] fTimes2 = {7,10,4,13,11};
        int[] weights2 = {6,9,5,8,10};

        res = WgtIntScheduler.getOptSet(sTimes2, fTimes2, weights2);

        System.out.println(Arrays.toString(res));

        //Case 2
        int[] sTimes3 = {0, 1, 3, 5, 5, 7};
        int[] fTimes3 = {6, 4, 5, 7, 9, 8};
        int[] weights3 = {60, 30, 10, 30 ,50, 10};

        res = WgtIntScheduler.getOptSet(sTimes3, fTimes3, weights3);

        System.out.println(Arrays.toString(res));
    }
}
