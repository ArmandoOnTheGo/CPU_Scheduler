package cpu;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	

	int PID_COUNTER = 0; 
	long CPU_TIME = 0;
	long WAITCPU_TIME = 0;
	long TURNAROUND = 0;
	boolean ISFULL = false; //keeps track of if the PTable is full
	int TOTAL = 0;
	int TOTALPID = 0;
	int BURST = 0;
	int ARRIVAL = 0;
	int REMAINING = 0;
	int INDEX = 0;
	int CREATEP = 0;
	int A_COUNTER = 0;
	long TIME = 0;
	boolean isDone = false;
	
	public Main(String[] args) {
		
		/*for(int i = 1; i < 31; i++) {
			FCFS(new ArrayList<Process>(), i);
			System.out.println("For FCFS" + i + ", the total is: " + CPU_TIME + ", Turnaround CPU_TIME is: " + TURNAROUND + ", Waiting CPU_TIME is: " + WAITCPU_TIME);
			System.out.println("The actual total CPU_TIME is: " + TOTAL + ", and PID is: " + TOTALPID);
		}*/	
		
		/*
		for(int i = 1; i < 31; i++) {
			RR(new ArrayList<Process>(), 10, i);
			System.out.println("For RR 10, #" + i + " the total is: " + CPU_TIME + ", Turnaround CPU_TIME is: " + TURNAROUND + ", Waiting CPU_TIME is: " + WAITCPU_TIME);
			System.out.println("The actual total CPU_TIME is: " + TOTAL + ", and PID is: " + TOTALPID);
			reset();
		}
		
		for(int i = 1; i < 31; i++) {
			RR(new ArrayList<Process>(), 200, i);
			System.out.println("For RR 200, #" + i + " the total is: " + CPU_TIME + ", Turnaround CPU_TIME is: " + TURNAROUND + ", Waiting CPU_TIME is: " + WAITCPU_TIME);
			System.out.println("The actual total CPU_TIME is: " + TOTAL + ", and PID is: " + TOTALPID);
		}*/
		
		for(int i = 1; i < 31; i++) {
			SRT(new ArrayList<Process>(), 10);
			System.out.println("For STF, the total is: " + TIME + ", Turnaround CPU_TIME is: " + TURNAROUND + ", Waiting CPU_TIME is: " + WAITCPU_TIME);
			System.out.println("The actual total CPU_TIME is: " + TOTAL + ", and PID is: " + TOTALPID);
		}
	
	}

	void createProcess(ArrayList<Process> PTable, int lambda) 
	{
		for(int i = 0; i < lambda; i++) {
			
			//makes sure that the table doesn't exceed 10000
			if (PID_COUNTER >= 10000){
				ISFULL = true;
				return;
			}
			
			int arrival = A_COUNTER * 6;
			int PID = PID_COUNTER++;
			int burst = ThreadLocalRandom.current().nextInt(1, 200 + 1);;
			String state = "ready";
			int remaining = burst;
			
			//global variables used to check if our calculations are correct
			TOTAL += burst;
			TOTALPID++;
		
			PTable.add(new Process(PID, arrival, burst, state, remaining));
		}
		A_COUNTER++;
	}


	//returns the index of the process with the least remaining CPU_TIME from a process table
	int getShortestP(ArrayList<Process> PTable) {
		int index = 0;
		for(int i = 1; i < PTable.size(); i++) {
			if(PTable.get(i).getRemaining() <= PTable.get(i).getRemaining() && PTable.get(i).getState() == "ready") {
				index = i;
			}
		}
		return index;
	}

	//function to reset global variables so that we can reuse them
	void reset() 
	{
		PID_COUNTER = 0; 
		CPU_TIME = 0;
		WAITCPU_TIME = 0;
		TURNAROUND = 0;
		TOTAL = 0;
		TOTALPID = 0;
		ISFULL = false;
		BURST = 0;
		ARRIVAL = 0;
		REMAINING = 0;
		CREATEP = 0;
		INDEX = 0;
		A_COUNTER = 0;
		TIME = 0;
	}

	void FCFS(ArrayList<Process> PTable, int lambda)
	{
		
		reset();
		
		//fills the table with some elements so the table wont be empty to begin with
		for(int i = 0; i < 10; i++) 
			createProcess(PTable, 1);
		
		for(int i = 0; i < 10000; i++) {
			//this is just for clarity in the code
			ARRIVAL = PTable.get(i).getArrival();
			BURST = PTable.get(i).getBurst();
			
			CREATEP += BURST;

			//creates a process every 6 seconds
			while(CREATEP >= 6 && !ISFULL) {
				createProcess(PTable, lambda);
				CREATEP -= 6;
			}
			
			WAITCPU_TIME = CPU_TIME - ARRIVAL - BURST;
			TURNAROUND = WAITCPU_TIME + BURST;
			CPU_TIME += BURST;		
		}
	}
	
	void SRT(ArrayList<Process> PTable, int lambda) 
	{
		//reset the global variables
		reset();
		
		//fills the table with some elements so the table wont be empty to begin with
		//for(int i = 0; i < 10; i++) 
		//createProcess(PTable, 1);
		
		createProcess(PTable, lambda);
		
		//main loop, repeats until 10000 processes are done
		while(PTable.size() != 0) {
			
			if(TIME % 6 == 0)
				createProcess(PTable, lambda);
			
			//returns the element with the shortest remaining CPU_TIME
			INDEX = getShortestP(PTable);
			
			//this is just for clarity in the code
			ARRIVAL = PTable.get(INDEX).getArrival();
			REMAINING = PTable.get(INDEX).getRemaining();
			BURST = PTable.get(INDEX).getBurst();
			
			if(REMAINING == 1) {
				//adds to CPU_TIME the amount of CPU_TIME the process was in the CPU
				CPU_TIME += BURST;
				WAITCPU_TIME += TIME - ARRIVAL - BURST;
				TURNAROUND += TIME - ARRIVAL;
				
				PTable.get(INDEX).setRemaining(0);
				PTable.get(INDEX).setState("terminated");
				
				//removes element index from the table
				PTable.remove(INDEX);
			}
			
			else if (REMAINING > 1) 
				//subtracts q from the remaining CPU_TIME of the element
				PTable.get(INDEX).setRemaining(--REMAINING);
	
			//Each iteration of the loop last 1 second.
			TIME++;
		}
	}
	
	void RR(ArrayList<Process> PTable, int q, int lambda) 
	{
		//resets global variables so we can run the functions one after the other
		reset();
		
		//lets us know if all elements of the array are done in RR
		boolean isDone = false;
		int terminated = 0;
		
		//fills the table with some elements so the table wont be empty to begin with
		for(int i = 0; i < 10; i++) 
			createProcess(PTable, 10);
		
		//loops over the array until there are no more elements that need to access the CPU
		while(!isDone) {
			
			//this is just for clarity in the code
			ARRIVAL = PTable.get(INDEX).getArrival();
			REMAINING = PTable.get(INDEX).getRemaining();
			
			if(REMAINING <= q) {
				WAITCPU_TIME += CPU_TIME - ARRIVAL - REMAINING;
				TURNAROUND += WAITCPU_TIME + REMAINING;
				CPU_TIME += REMAINING;
				CREATEP += REMAINING;
				PTable.get(INDEX).setRemaining(0);
				PTable.get(INDEX).setState("terminated");	
				
				terminated++;
			}
			
			else {
				CPU_TIME += q;
				TURNAROUND += q;
				
				//subtracts q from the remaining CPU_TIME
				PTable.get(INDEX).setRemaining(REMAINING - q);
				CREATEP += q;
			}
			
			
			
			if(terminated == 10000)
				break;
			
			//creates a process every 6 seconds
			while(CREATEP >= 6 && !ISFULL) {
				createProcess(PTable, lambda);
				CREATEP -= 6;
			}
			
			//loop to make sure that the array is not done
			for(int i = 1; i < PTable.size(); i++) {
				if(PTable.get(i).getState() == "ready") {
					isDone = false;
					break;
				}
			}
			//incr index while making sure it loops back to the start
			INDEX = (INDEX + 1) % PTable.size();
		}
	}
}
