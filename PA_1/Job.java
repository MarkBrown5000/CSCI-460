/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os1;

/**
 *
 * @author MarkyMark5000
 */
public class Job {
    public int arrivalTime;
    public int runTime;
    public int finishTime;
    public double TT;
    public boolean hasRun;
    
    public Job(){
        hasRun = false;
    }
    //Calculates the turnaround time of the job
    public void turnaroundTime(){
        TT = finishTime - arrivalTime;
    }
    //sets the run time of the job
    public void setRunTime(){
        runTime = (int) (Math.random() * 500) + 1;
    }
    //sets the arrival time of the job
    public void setArrivalTime(int at){
        arrivalTime = at;
    }
    //sets whether or not the job has been run.
    public void run(){
        hasRun = true;
    }
}
