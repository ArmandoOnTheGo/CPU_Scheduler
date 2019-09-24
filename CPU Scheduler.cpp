#include <iostream>
#include <vector> 

using namespace std;

int PID_COUNTER = 0; 
long long TIME = 0;
long long WAITTIME = 0;
long long TURNAROUND = 0;
//keeps track of if the PTable is full
bool ISFULL = false;

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
void createProcess(vector<Process> &PTable) 
{
	int arrival = PID_COUNTER * 6;
	int PID = PID_COUNTER++;
	int burst = rand() % 200;
	string state = "ready";
	int remaining = burst;
	
	Process p(PID, arrival, burst, state, remaining);
	//PTable.append(p);
	PTable.push_back(p);
	
	//tells us if the Ptable is full
	if(PTable.size() >= 10000)
		ISFULL = true;
}

//returns the index of the process with the least remaining time from a process table
int getShortestP(vector<Process> &PTable) {
	int index = 0;
	for(int i = 0; i < PTable.size(); i++) {
		if(PTable[i].getState() == "ready" and PTable[i].getRemaining() < PTable[index].getRemaining())
			index = i;
	}
	return index;
}

//assume all the elements of PTable are sorted by arrival time so go through the array linearly
void FCFS(vector<Process> &PTable)
{
	//initialize them again so we can use them again, one function after the other
	PID_COUNTER = 0; 
	TIME = 0;
	WAITTIME = 0;
	TURNAROUND = 0;
	//var to keep track of when to create a new process
	long createP = 0;
	
	//fills the table with some elements so the table wont be empty to begin with
	for(int i = 0; i < 100; i++) 
		createProcess(PTable);
	
	for(int i = 0; i < 10000; i++) {
		createP += PTable[i].getBurst();
		
		//creates a process every 6 seconds
		while(createP >= 6 and !ISFULL) {
			createProcess(PTable);
			createP -= 6;
		}
		
		WAITTIME = TIME - PTable[i].getArrival() - PTable[i].getBurst();
		TURNAROUND = WAITTIME + PTable[i].getBurst();
		TIME += PTable[i].getBurst();		
	}
}


void SRT(vector<Process> &PTable, int q) 
{
	//initialize them again so we can use them again, one function after the other
	PID_COUNTER = 0; 
	TIME = 0;
	WAITTIME = 0;
	TURNAROUND = 0;
	
	//fills the table with some elements so the table wont be empty to begin with
	for(int i = 0; i < 100; i++) 
		createProcess(PTable);
	
	//var used to keep track of the index of the process in the CPU;
	int index = 0;
	
	//var to keep track of when to create a new process
	int createP = 0;
	
	//main loop, repeats until 10000 processes are done
	for(int i = 0; i < 10000; i++) {
		index = getShortestP(PTable);
		
		
		if(PTable[index].getRemaining() <= q) {
			TIME += PTable[index].getRemaining();
			WAITTIME += TIME - PTable[index].getArrival() - PTable[index].getRemaining();
			TURNAROUND += WAITTIME + PTable[index].getRemaining();
			
			createP += PTable[index].getRemaining();
			PTable[index].setRemaining(0);
			PTable[index].setState("terminated");	
		}
		
		else {
			TIME += q;
			TURNAROUND += q;
			//subtracts q from the reamining time
			PTable[index].setRemaining(PTable[index].getRemaining() - q);
			createP += q;
		}
		
		//creates a process every 6 seconds
		while(createP >= 6 and !ISFULL) {
			createProcess(PTable);
			createP -= 6;
		}
	}
}

void RR(vector<Process> &PTable, int q) {
	
	//initialize them again so we can use them again, one function after the other
	PID_COUNTER = 0; 
	TIME = 0;
	WAITTIME = 0;
	TURNAROUND = 0;
	
	//var used to keep track of the index of the process in the CPU
	int index = 0;
	int createP = 0;
	//lets us know if all elements of the array are done in RR
	bool isDone = false;
	
	//fills the table with some elements so the table wont be empty to begin with
	for(int i = 0; i < 100; i++) 
		createProcess(PTable);
	
	//loops over the array until there are no more elements that need to access the CPU
	while(!isDone) {
		if(PTable[index].getRemaining() <= q) {
			WAITTIME += TIME - PTable[index].getArrival() - PTable[index].getRemaining();
			TURNAROUND += WAITTIME + PTable[index].getRemaining();
			TIME += PTable[index].getRemaining();
			createP += PTable[index].getRemaining();
			PTable[index].setRemaining(0);
			PTable[index].setState("terminated");	
		}
		
		else {
			TIME += q;
			TURNAROUND += q;
			//subtracts q from the reamining time
			PTable[index].setRemaining(PTable[index].getRemaining() - q);
			createP += q;
		}
		
		//creates a process every 6 seconds
		while(createP >= 6 and !ISFULL) {
			createProcess(PTable);
			createP -= 6;
		}
		
		//loop to make sure that the array is not done
		for(int i = 0; i < PTable.size(); i++) {
			isDone = true;
			if(PTable[i].getState() == "ready") {
				isDone = false;
				break;
			}	
		}
		//incr index while maki)ng sure it loops back to the start
		index = (index + 1) % 10000;
	}
}

int main()
{
	vector<Process> PTable1;
	vector<Process> PTable2;
	vector<Process> PTable3;
	vector<Process> PTable4;
	/*
	FCFS(PTable1);
	cout << "For FCFS, the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
	*/
	RR(PTable3, 10);	
	cout << "For RR 10, the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
	
	RR(PTable4, 200);
	cout << "For RR 200, the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
	
	SRT(PTable2, 105);
	cout << "For STF, the total is: " << TIME << ", Turnaround time is: " << TURNAROUND << ", Waiting time is: " << WAITTIME << endl;
	
	
	
	
	
	/*createProcess(PTable1);
	//Process p(1, 10, 20, "ready", 25);
	//PTable1.push_back(p);
	cout << "PID: " << PTable1[0].getPID() << ", Arrival: " << PTable1[0].getArrival() << ", Burst: " << PTable1[0].getBurst() << ", State:" << PTable1[0].getState() << ", Remaining: " << PTable1[0].getRemaining() << endl;*/
}
