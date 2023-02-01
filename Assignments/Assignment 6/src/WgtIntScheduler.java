import java.util.*;

public class WgtIntScheduler {
    public static int[] getOptSet(int[] stime, int[] ftime, int[] weight){
        ArrayList<Job> jobs = new ArrayList<>();

        jobs.add(new Job(0,0,0, 0));
        //Initialize job objects
        for(int i = 0; i < stime.length; i++){
            jobs.add(new Job(stime[i], ftime[i], weight[i], i+1));
        }

        //Sort jobs by finish time.
        jobs.sort(Comparator.comparingInt((Job job) -> job.ftime));

        //Initialize closestJobIndices table
        int[] closestJobIndices = new int[jobs.size()];
        closestJobIndices[0] = 0;
        //Initialize closest Indices tale
        for(int i = 1; i < jobs.size(); i++){
            for(int j = i-1; j >= 0; j--){
                if(j == 0){
                   closestJobIndices[i] = 0;
                }else if(jobs.get(j).ftime <= jobs.get(i).stime){
                    closestJobIndices[i] = j;
                    break;
                }
            }
        }

        //Initialize max Weight Table
        int[] maxWeightTable = new int[jobs.size()];
        maxWeightTable[0] = jobs.get(0).weight;

        //Fill max weight Table
        for(int i = 1; i < jobs.size(); i++){
            maxWeightTable[i] = Math.max(maxWeightTable[i-1], maxWeightTable[closestJobIndices[i]] + jobs.get(i).weight);;
        }

        return traceBack(jobs, maxWeightTable);
    }

    private static int[] traceBack(ArrayList<Job> jobs, int[] weightTable){
        int totalWeight = weightTable[weightTable.length-1];
        ArrayList<Integer> jobSet = new ArrayList<>();

        //Grab the total weight from the max weight table. Check if the in the weight table if the current element
        //is the source of that max weight and matches the that value. If it is, then subtract from the total value
        //and add that job. Continue until found all jobs.
        for(int i = weightTable.length-1; i > 0; i--){
            if(weightTable[i] > weightTable[i-1] && weightTable[i] == totalWeight) {
                totalWeight -= jobs.get(i).weight;
                jobSet.add(jobs.get(i).id);
            }
        }

        //Sort so that the jobSet is in order as requested.
        jobSet.sort(Comparator.comparingInt((Integer i) -> i));

        //Convert to primitive array as requested
        int[] selectedJobs = new int[jobSet.size()];
        for(int i = 0; i < jobSet.size(); i++)
            selectedJobs[i] = jobSet.get(i);

        return selectedJobs;
    }

    //A Job object to more easily manage the three different fields.
    private static class Job{
        private int stime, ftime, weight, id;

        private Job(int stime, int ftime, int weight, int id){
            this.stime = stime;
            this.ftime = ftime;
            this.weight = weight;
            this.id = id;
        }
    }



}
