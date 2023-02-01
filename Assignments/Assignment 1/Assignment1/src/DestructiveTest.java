import java.lang.Math;
import java.util.ArrayList;

public class DestructiveTest {
    public ArrayList<Integer> findHighestSafeRung(int height, int safest){
        //Initialize the array and default elements.
        ArrayList<Integer> result = new ArrayList<>(9);
        for(int index = 0; index < 9; index++)
            result.add(-1);

        result.set(0, height);
        result.set(1, safest);
        result.set(3, 0);

        //Initialize the variables, k is the number of gapWidths traversed.
        int k, totalDrops, ladderRemainder, gapWidth, currentRung;
        totalDrops = 0;

        //The ladder remained, since every ladder height may not be a perfect root.
        ladderRemainder =  height%(int)Math.sqrt(height);
        //The approach I choose was to use equal gap widths of sqrt(n), where n = height.
        gapWidth = (int)(Math.sqrt(height));
        //The first drop will always be at the 1*gapWidth, since safest cannot = 0;
        result.set(4, gapWidth);

        //Found variable is used for the following loop that traverses linearly.
        boolean found = false;
        //This loop traverses k gaps until either the device breaks, or the highest safe rung is the top rung, in which case it
            //returns
        for(k = 1; k<=height/gapWidth; k++){
            currentRung = gapWidth*k;

            //Since the second and third drop may be variable, use this conditional to set those results.
            totalDrops++;
            if(totalDrops == 2)
                result.set(5, currentRung);
            if(totalDrops == 3)
                result.set(6, currentRung);


            //If the algorithm is at the gap immediately before the top of the ladder,
                //set the current rung to the height to account for remaining rungs.
            if(height-currentRung == ladderRemainder)
                currentRung = height;

            //If at the top rung and the device hasn't broken, we know the top rung is the solution, so return.
            if(currentRung == height && !(currentRung > safest)){
                result.set(2, currentRung);
                found = true;
                break;
            }

            //If the device would break, break it.
            if(currentRung > safest){
                result.set(7, currentRung);
                break;
            }
        }

        //This loop goes linearly starting at the gap before the device broke (k-1)*gapWidth+1,
            //until the device breaks, thus revealing the solution. The plus 1 is because we have
            //previously checked the interval
        for(int i = (k-1)*gapWidth+1; !found; i++){
            currentRung = i;

            //Since the second and third drop may be variable, use this conditional to set those results.
            totalDrops++;
            if(totalDrops == 2)
                result.set(5, currentRung);
            if(totalDrops == 3)
                result.set(6, currentRung);

            //Find when the device breaks, set appropriate results.
            if(currentRung > safest){
                found = true;
                result.set(8, i);
                result.set(2, i-1);
            }
        }
        //Set total drops.
        result.set(3, totalDrops);

        return result;
    }
}
