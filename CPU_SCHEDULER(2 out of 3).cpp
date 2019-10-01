#include <iostream>
#include <vector> 

using namespace std;

int PID_COUNTER = 0; 
long long TIME = 0;
long long WAITTIME = 0;
long long TURNAROUND = 0;
bool ISFULL = false; //keeps track of if the PTable is full
int TOTAL = 0;
int TOTALPID = 0;
int BURST = 0;
int ARRIVAL = 0;
int REMAINING = 0;
int INDEX = 0;
int CREATEP = 0;
int A_COUNTER = 0;

//lets us know if all elements of the array are done in RR
bool isDone = false;

class Process {
	private:
		int PID;
		int arrival;
		int burst;
		string state;
		int remaining;
	
	public:
		Process(int PID, int arrival, int burst, string state, int remaining) {
			this->PID = PID;
			this->arrival = arrival;
			this->burst = burst;
			this->state = state;
			this->remaining = remaining;
		}
		
		int getPID() { return PID;}
		int getArrival() { return arrival;}
		int getBurst() { return burst;}
		string getState() { return state;}
		int getRemaining() { return remaining;}
		
		void setRemaining(int remaining) { this->remaining = remaining;}
		void setState(string state) { this->state = state;}
};

//creates and adds a process to a given process Table
void createProcess(vector<Process> &PTable, int lambda) 
{
	for(int i = 0; i < lambda; i++) {
		
		//makes sure that the table doesnt exceed 10000
		if (PID_COUNTER >= 10000){
			ISFULL = true;
			return;
		}
		
		int arrival = A_COUNTER * 6;
		int PID = PID_COUNTER++;
		int burst = rand() % 200;
		string state = "ready";
		int remaining = burst;
		
		//global variables used to check if our calculations are correct
		TOTAL += burst;
		TOTALPID++;
		
		Process p(PID, arrival, burst, state, remaining);
	
		PTable.push_back(p);
	}
	A_COUNTER++;
}

//returns the index of the process with the least remaining time from a process table
int getShortestP(vector<Process> &PTable) {
	int index = 0;
	for(int i = 1; i < PTable.size(); i++) {
		if(PTable[i].getRemaining() <= PTable[index].getRemaining() and PTable[i].getState() == "ready") {
			index = i;
		}
	}
	return index;
}

//function to reset global variables so that we can reuse them
void reset() 
{
	PID_COUNTER = 0; 
	TIME = 0;
	WAITTIME = 0;
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
}

void SRT(vector<Process> &PTable, int q) 
{
	//reset the global variables
	reset();
	
	//fills the table with some elements so the table wont be empty to begin with
	for(int i = 0; i < 10; i++) 
		createProcess(PTable, 1);
	
	//main loop, repeats until 10000 processes are done
	while(PTable.size() != 0) {
		//returns the element with the shortest remaining time
		INDEX = getShortestP(PTable);
		
		//this is just for clarity in the code
		ARRIVAL = PTable[INDEX].getArrival();
		REMAINING = PTable[INDEX].getRemaining();
		
		if(REMAINING <= q) {
			//adds to TIME the amount of time the process was in the CPU
			TIME += REMAINING;
			WAITTIME += TIME - ARRIVAL - REMAINING;
			TURNAROUND += WAITTIME + REMAINING;
			
			CREATEP += REMAINING;
			PTable[INDEX].setRemaining(0);
			PTable[INDEX].setState("terminated");
			
			//removes element index from the table
			PTable.erase(PTable.begin() + INDEX);
		}
		
		else if (REMAINING > q) {
			TIME += q;
			TURNAROUND += q;
			
			//subtracts q from the reamining time of the element
			PTable[INDEX].setRemaining(REMAINING - q);
			CREATEP += q;
		}
		
		//creates a process every 6 seconds
		while(CREATEP >= 6 and !ISFULL) {
			createProcess(PTable, 1);
			CREATEP -= 6;
		}
	}
}

//assume all the elements of PTable are sorted by arrival time so go through the array linearly
void FCFS(vector<Process> &PTable, int lambda)
{
	reset();
	
	//fills the table with some elements so the table wont be empty to begin with
	for(int i = 0; i < 10; i++) 
		createProcess(PTable, 1);
	
	for(int i = 0; i < 10000; i++) {
		//this is just for clarity in the code
		ARRIVAL = PTable[i].getArrival();
		BURST = PTable[i].getBurst();
		
		CREATEP += BURST;

		//creates a process every 6 seconds
		while(CREATEP >= 6 and !ISFULL) {
			createProcess(PTable, lambda);
			CREATEP -= 6;
		}
		
		WAITTIME = TIME - ARRIVAL - BURST;
		TURNAROUND = WAITTIME + BURST;
		TIME += BURST;		
	}
}

void RR(vector<Process> &PTable, int q, int lambda) 
{
	//resets global variables so we can run the functions one after the other
	reset();
	
	//lets us know if all elements of the array are done in RR
	bool isDone = false;
	
	//fills the table with some elements so the table wont be empty to begin with
	for(int i = 0; i < 10; i++) 
		createProcess(PTable, 10);
	
	//loops over the array until there are no more elements that need to access the CPU
	while(!isDone) {
		
		//this is just for clarity in the code
		ARRIVAL = PTable[INDEX].getArrival();
		REMAINING = PTable[INDEX].getRemaining();
		
		if(REMAINING <= q) {
			WAITTIME += TIME - ARRIVAL - REMAINING;
			TURNAROUND += WAITTIME + REMAINING;
			TIME += REMAINING;
			CREATEP += REMAINING;
			PTable[INDEX].setRemaining(0);
			PTable[INDEX].setState("terminated");	
		}
		
		else {
			TIME += q;
			TURNAROUND += q;
			
			//subtracts q from the reamining time
			PTable[INDEX].setRemaining(REMAINING - q);
			CREATEP += q;
		}
		
		//creates a process every 6 seconds
		while(CREATEP >= 6 and !ISFULL) {
			createProcess(PTable, lambda);
			CREATEP -= 6;
		}
		
		isDone = true;
		
		//loop to make sure that the array is not done
		for(int i = 1; i < PTable.size(); i++) {
			if(PTable[i - 1].getState() == "ready") {
				//cout << "element " << i << " state: " << PTable[i].getState() << endl;
				isDone = false;
				break;
			}
			//cout <<	isDone << endl;
		}
		//incr index while making sure it loops back to the start
		INDEX = (INDEX + 1) % 10000;
	}
}

int main()
{
	vector<Process> PTable1;
	vector<Process> PTable2;
	vector<Process> PTable3;
	vector<Process> PTable4;
	
	/*
	for(int i = 1; i < 31; i++)
		createProcess(PTable1, i);
	*/
	
	for(int i = 1; i < 31; i++) {
		FCFS(PTable1, i);
		cout << "For FCFS" << i << ", the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
		cout << "The actual total time is: " << TOTAL << ", and PID is: " << TOTALPID << endl << endl;
	}	
	/*
	for(int i = 1; i < 31; i++) {
		RR(PTable3, 10, i);	
		cout << "For RR 10, #" << i << " the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
		cout << "The actual total time is: " << TOTAL << ", and PID is: " << TOTALPID << endl << endl;
	}
	*//*
	for(int i = 1; i < 31; i++) {
		RR(PTable4, 200, i);
		cout << "For RR 200, #" << i << " the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
		cout << "The actual total time is: " << TOTAL << ", and PID is: " << TOTALPID << endl << endl;
	}
	*/
	for(int i = 1; i < 31; i++) {
		SRT(PTable2, 10);
		cout << "For STF, the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
		cout << "The actual total time is: " << TOTAL << ", and PID is: " << TOTALPID << endl;
	}
}
