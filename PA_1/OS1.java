/*
 * Mark Brown
 * Operating Systems Programming Assignment 1
 * Notes on operation:  
 * - I used netbeans to create this program, so if not using netbeans to run it, 
 *      you must comment out the "package os1".  
 * - Change numJobs (marked with //***** below) variable to 12 to use predetermined 
 *      list of jobs.  Any other number that is greater than 0 will randomly 
 *      generate an array of jobs.
 */
package os1;

public class OS1 {

    private int numProcessors = (3156 % 3) + 2;
    //private int numProcessors = 2;
    private int arrivalTime = 1;
    //*********************************************************************//
    private int numJobs = 100; //Change this number to 12 to use predetermined list of jobs.  
    //Any other number > 0 will randomly generate a list of jobs.
    //********************************************************************//
    private Job[] jobs;
    private Processor[] processors;
    private int currentProcessor = 0;
    private double[] testRuns;
    private int numTestRuns = 100;

    public OS1() {
        //Initialize array of jobs
        jobs = new Job[numJobs];
        for (int i = 0; i < numJobs; i++) {
            jobs[i] = new Job();
        }

        testRuns = new double[numTestRuns];
        for (int i = 0; i < numTestRuns; i++) {
            testRuns[i] = 0;
        }
        processors = new Processor[numProcessors];
        for (int i = 0; i < numProcessors; i++) {
            processors[i] = new Processor();
        }
    }

    public static void main(String[] args) {
        OS1 os = new OS1();
        OS1 os2 = new OS1();
        if (os.numJobs > 12 || os.numJobs < 12) {
            System.out.println(os.numJobs + " Randomly Generated Jobs");
            os.jobGenerator();
            os.roundRobin();
            //os.printPRunTime();
            System.out.println("Average Turnaround Time with Round Robin: " + os.averageTurnaround());
            os2.jobGenerator();
            os2.sjn();
            System.out.println("Average Turnaround Time with SJN: " + os2.averageTurnaround());
            System.out.println();
            os.testRun(0);
            System.out.println();
            os.testRun(1);
            System.out.println();
        } else {
            System.out.println("12 Predetermined Jobs");
            os.preMadeJobList();
            os.roundRobin();
            //os.printPRunTime();
            System.out.println("Average Turnaround Time with Round Robin: " + os.averageTurnaround());
            os2.preMadeJobList();
            os2.sjn();
            System.out.println("Average Turnaround Time with SJN: " + os2.averageTurnaround());
        }
    }

    //This is the round robin method described in the assignment.  As soon as a job arrives, it loads it on to
    //the next processor and runs it immediately.
    public void roundRobin() {
        for (int i = 0; i < numJobs; i++) {
            if (jobs[i].arrivalTime > processors[currentProcessor].timeRunning) {
                processors[currentProcessor].timeRunning = jobs[i].arrivalTime;
            }
            processors[currentProcessor].timeRunning += (jobs[i].runTime + 1);  //Takes 1 ms to put job on processor
            jobs[i].finishTime = processors[currentProcessor].timeRunning;
            currentProcessor += 1;
            if (currentProcessor >= numProcessors) {
                currentProcessor = 0;
            }

        }
    }

    //This method calculates the average turnaround time.
    public double averageTurnaround() {
        double totalTT = 0;
        for (int i = 0; i < numJobs; i++) {
            jobs[i].turnaroundTime();
            totalTT += jobs[i].TT;
        };
        double aTT = totalTT / numJobs;
        return aTT;

    }

    //This method generates a list of jobs with runtimes between 1 and 500.
    //The number of jobs generated is determined by the numJobs instance variable.
    public void jobGenerator() {
        for (int i = 0; i < numJobs; i++) {
            Job job = new Job();
            job.setRunTime();
            job.setArrivalTime(arrivalTime);
            jobs[i] = job;
            arrivalTime += 1;
        }
    }

    //Method that prints out how long a processor has been running.  Used for testing
    public void printPRunTime() {
        for (int i = 0; i < numProcessors; i++) {
            System.out.println(processors[i].timeRunning);
        }
    }

    //This is the method for the last part of the assignment, designed to beat the round robin.
    //It is a shortest job next method.  
    public void sjn() {
        int totalJobsRun = 0;
        //Job shortestJob = null;
        //Sort the array by runTime
        for (int i = 0; i < jobs.length; i++) {
            for (int j = i + 1; j < jobs.length; j++) {
                Job temp = new Job();
                if (jobs[j].runTime < jobs[i].runTime) {
                    temp = jobs[i];
                    jobs[i] = jobs[j];
                    jobs[j] = temp;
                }
            }
        }
//        for (int k = 0; k < jobs.length; k++) {
//            System.out.print("[" + jobs[k].arrivalTime + ", " + jobs[k].runTime + "], ");
//        }
//        System.out.println();

        while (numJobs > totalJobsRun()) {
            boolean jobRan = false;
            for (int i = 0; i < numJobs; i++) {
                Job temp = jobs[i];
                Job shortestJob = jobs[i];
                if (shortestJob.arrivalTime > processors[currentProcessor].timeRunning && shortestJob.hasRun == false && jobRan == false) {
                    for (int j = i + 1; j < numJobs; j++) {
                        if (jobs[j].arrivalTime < shortestJob.arrivalTime && jobs[j].hasRun == false) {
                            shortestJob = jobs[j];
                        }
                    }
                    if (temp.arrivalTime < shortestJob.arrivalTime) {
                        shortestJob = temp;
                        processors[currentProcessor].timeRunning = shortestJob.arrivalTime;
                    }
                    shortestJob.run();
                    jobRan = true;
                    processors[currentProcessor].timeRunning += (shortestJob.runTime + 1);
                    processors[currentProcessor].setJobsRun();
                    shortestJob.finishTime = processors[currentProcessor].timeRunning;
                    currentProcessor += 1;
                    if (currentProcessor >= numProcessors) {
                        currentProcessor = 0;
                    }
                    //System.out.print(processors[currentProcessor].timeRunning + ", [" + shortestJob.arrivalTime + ", " + shortestJob.runTime + "] ");

                } else {
                    if (jobs[i].hasRun == false && jobRan == false) {
                        shortestJob = jobs[i];
                        jobs[i].run();
                        jobRan = true;
                        processors[currentProcessor].timeRunning += (shortestJob.runTime + 1);
                        processors[currentProcessor].setJobsRun();
                        shortestJob.finishTime = processors[currentProcessor].timeRunning;
                        if (currentProcessor >= numProcessors) {
                            currentProcessor = 0;
                        }
                        //System.out.print(processors[currentProcessor].timeRunning + ", [" + shortestJob.arrivalTime + ", " + shortestJob.runTime + "] ");
                    }
                }

            }
        }
        //System.out.println();
    }

    //This method initiates the job list that was provided on the assignment sheet.  
    //When the numJobs instance variable is set to 12, this method is automatically
    //used to build the premade job list.
    public void preMadeJobList() {
        jobs[0].arrivalTime = 4;
        jobs[1].arrivalTime = 15;
        jobs[2].arrivalTime = 18;
        jobs[3].arrivalTime = 20;
        jobs[4].arrivalTime = 26;
        jobs[5].arrivalTime = 29;
        jobs[6].arrivalTime = 35;
        jobs[7].arrivalTime = 45;
        jobs[8].arrivalTime = 57;
        jobs[9].arrivalTime = 83;
        jobs[10].arrivalTime = 88;
        jobs[11].arrivalTime = 95;
        jobs[0].runTime = 9;
        jobs[1].runTime = 2;
        jobs[2].runTime = 16;
        jobs[3].runTime = 3;
        jobs[4].runTime = 29;
        jobs[5].runTime = 198;
        jobs[6].runTime = 7;
        jobs[7].runTime = 170;
        jobs[8].runTime = 180;
        jobs[9].runTime = 178;
        jobs[10].runTime = 73;
        jobs[11].runTime = 8;
    }

    //This method does a tests the job scheduler methods a certain number of times.
    //The number of times it runs the scheduler methods is determined by the 
    //numTestRuns instance variable.
    public void testRun(int choice) {
        //OS1 os3 = new OS1();
        if (choice == 0) {
            System.out.println("-Statistics Using Round Robin on " + numTestRuns + " Test Runs-");
            for (int i = 0; i < numTestRuns; i++) {
                OS1 os3 = new OS1();
                os3.jobGenerator();
                os3.roundRobin();
                testRuns[i] = os3.averageTurnaround();

            }
            System.out.println("Average: " + average() + ", Max: " + max() + ", Min: " + min() + ", Standard Deviation: " + standardDeviation());
//            for (int i = 0; i < numTestRuns; i++) {
//                System.out.print("[" + testRuns[i] + "]");
//            }
        } else if (choice == 1) {
            System.out.println("-Statistics Using Shortest Job Next on " + numTestRuns + " Test Runs-");
            for (int i = 0; i < numTestRuns; i++) {
                OS1 os3 = new OS1();
                os3.jobGenerator();
                os3.sjn();
                testRuns[i] = os3.averageTurnaround();

            }
            System.out.println("Average: " + average() + ", Max: " + max() + ", Min: " + min() + ", Standard Deviation: " + standardDeviation());
//            for (int i = 0; i < numTestRuns; i++) {
//                System.out.print("[" + testRuns[i] + "]");
//            }
        }
    }

    //This method calculates the average of the turnaround times aquired from the testRuns() method
    public double average() {
        double total = 0;
        double average = 0;
        for (int i = 0; i < numTestRuns; i++) {
            total += testRuns[i];
        }
        average = total / numTestRuns;
        return average;
    }

    //This method finds the maximum turnaround time from testRuns()
    public double max() {
        double max = 0;
        for (int i = 0; i < numTestRuns; i++) {
            if (testRuns[i] > max) {
                max = testRuns[i];
            }
        }
        return max;
    }

    //This method finds the minimum turnaround time from testRuns()
    public double min() {
        double min = testRuns[1];
        for (int i = 0; i < numTestRuns; i++) {
            if (testRuns[i] < min) {
                min = testRuns[i];
            }
        }
        return min;
    }

    //This method finds the standardDeviation of the turnaround times aquired from testRuns()
    public double standardDeviation() {
        double sd = 0;
        double sum = 0;
        double average = average();
        for (int i = 0; i < numTestRuns; i++) {
            sum += Math.pow((testRuns[i] - average), 2);
        }
        sd = Math.sqrt(sum);
        return sd;
    }

    //This method is used to keep track of how many jobs have been run on the processors
    public int totalJobsRun() {
        int total = 0;
        for (int i = 0; i < numProcessors; i++) {
            total += processors[i].jobsRun;
        }
        return total;
    }

}
