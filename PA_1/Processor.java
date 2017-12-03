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
public class Processor {
    public int timeRunning;//keeps track of how long the processor has been running
    public int jobsRun;
    public int currentJob;
    
    public Processor(){
        jobsRun = 0;
        currentJob = 0;
    }
    //Keeps track of how many jobs have run on the processor
    public void setJobsRun(){
        jobsRun++;
    }
    
}
