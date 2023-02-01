import java.util.Arrays;

public class Inversions {
    public static int totalInversions = 0;
    public static int invCounter(int[] array){
        //This function simply makes a call to the modified merge sort.
        divide(array);

        //Reset the static variable.
        int total = totalInversions;
        totalInversions = 0;
        return total;
    }

    //These two functions are a modified merge sort.

    //This function makes the recursive calls to split the array.
    static int[] divide(int[] array){
        //Base case
        if(array.length == 1)
            return array;
        //Find split and initialize
        int mid = array.length/2;
        int[] halfOne, halfTwo;

        //Create copies
        halfOne = Arrays.copyOfRange(array, 0, mid);
        halfTwo = Arrays.copyOfRange(array, mid, array.length);

        //Reccur
        halfOne = divide(halfOne);
        halfTwo = divide(halfTwo);
        return countAndCombine(halfOne, halfTwo);
    }

    //This function counts the inversions and combines the two arrays.
    static int[] countAndCombine(int[] array1, int[] array2){
        int i=0, j=0, k=0;
        int[] result = new int[array1.length+array2.length];

        //Appends elements to the end of the resulting array until one array is void of elements
        while(i < array1.length && j < array2.length){
            //If the element in the first array greater than the element in the second array, then because both are sorted, every remaining element in the first array
                // is greater than that specific element in the second array, meaning there is one inversion for every remaining element in the first array.
            if(array1[i] > array2[j]) {
                result[k] = array2[j];
                j++;
                //Add the size of the remaining first array to total
                totalInversions += array1.length-i;
            }else{
                //If the initial statement is false, then there is no inversion to count.
                result[k] = array1[i];
                i++;
            }
            k++;
        }

        //Append whichever array has remaining elements to the end of the result.
        if(i == array1.length)
            for(; k < result.length; k++){
                result[k] = array2[j];
                j++;
            }
        else
            for(; k < result.length; k++){
                result[k] = array1[i];
                i++;
            }

        return result;
    }

}
