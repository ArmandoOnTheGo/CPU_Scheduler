package cpu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/*import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;*/
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class Main {
	//Initializes some global variables to keep track
	double PID_COUNTER = 0; 
	double CPU_TIME = 0;
	double WAITCPU_TIME = 0;
	double TURNAROUND = 0;
	double BURST = 0;
	double ARRIVAL = 0;
	double REMAINING = 0;
	int INDEX = 0;
	double TIME = 0;
	int AVG_QUEUE = 0;

	ArrayList<DataCollected> FCFSData = new ArrayList<DataCollected>();
	ArrayList<DataCollected> SRTData = new ArrayList<DataCollected>();
	ArrayList<DataCollected> RR10Data = new ArrayList<DataCollected>();
	ArrayList<DataCollected> RR200Data = new ArrayList<DataCollected>();

	Random rand = new Random();

	//code to write data to an excel file
/*	public void writeDataToExcel(ArrayList<DataCollected> data, String filename, String schedulerName)
	{

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet(schedulerName);
			Iterator<DataCollected> iterator = data.iterator();

			int rowIndex = 0;
			Row titlerow = sheet.createRow(rowIndex++);
			Cell cell0 = titlerow.createCell(0);
			cell0.setCellValue("AVG_QUEUE_SIZE");
			Cell cell1 = titlerow.createCell(1);
			cell1.setCellValue("CPU_TIME");
			Cell cell2 = titlerow.createCell(2);
			cell2.setCellValue("TURNAROUND");
			Cell cell3 = titlerow.createCell(3);
			cell3.setCellValue("WAITCPU_TIME");
			Cell cell4 = titlerow.createCell(4);
			cell4.setCellValue("TIME");

			while(iterator.hasNext())
			{
				DataCollected schedulerData = iterator.next();
				Row row = sheet.createRow(rowIndex++);
				Cell cell5 = row.createCell(0);
				cell5.setCellValue(schedulerData.AVG_QUEUE_SIZE);
				Cell cell6 = row.createCell(1);
				cell6.setCellValue(schedulerData.CPU_TIME);
				Cell cell7 = row.createCell(2);
				cell7.setCellValue(schedulerData.TURNAROUND);
				Cell cell8 = row.createCell(3);
				cell8.setCellValue(schedulerData.WAITCPU_TIME);
				Cell cell9 = row.createCell(4);
				cell9.setCellValue(schedulerData.TIME);
			}
			try
			{
				FileOutputStream fos = new FileOutputStream(filename);
				workbook.write(fos);
				fos.close();
				System.out.println(filename + " written sucessfully");
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
			}
	}*/

	public Main(String[] args)
	{
		//runs the FCFS scheduling algorithm for lambda ranging from 1 to 31
		for(int i = 1; i < 31; i++) {
			FCFS(new ArrayList<Process>(), i);
			FCFSData.add(new DataCollected("FCFS " + i, CPU_TIME, WAITCPU_TIME, TURNAROUND, TIME, AVG_QUEUE));
		}
//		writeDataToExcel(FCFSData, "FCFS.xlsx", "FIRST COME FIRST SERVE");

		//runs the SRT scheduling algorithm for lambda ranging from 1 to 31
		for(int i = 1; i < 31; i++)
		{
			SRT(new ArrayList<Process>(), i);
			SRTData.add(new DataCollected("SRT " + i, CPU_TIME, WAITCPU_TIME, TURNAROUND, TIME, AVG_QUEUE));
		}
//		writeDataToExcel(SRTData, "SRT.xlsx", "SHORTEST REMAINING TIME");

		//runs the RR 0.01 scheduling algorithm for lambda ranging from 1 to 31
		for(int i = 1; i < 31; i++)
		{
			RR(new ArrayList<Process>(), .01, i);
			RR10Data.add(new DataCollected("RR10 #" + i, CPU_TIME, WAITCPU_TIME, TURNAROUND, TIME, AVG_QUEUE));
			reset();
		}
//		writeDataToExcel(RR10Data, "round_robin10.xlsx", "Round Robin 10");

		//runs the RR 0.2 scheduling algorithm for lambda ranging from 1 to 31
		for(int i = 1; i < 31; i++)
		{
			RR(new ArrayList<Process>(), .2, i);
			RR200Data.add(new DataCollected("RR200 #" + i, CPU_TIME, WAITCPU_TIME, TURNAROUND, TIME, AVG_QUEUE));
		}
//		writeDataToExcel(RR200Data, "round_robin200.xlsx", "Round Robin 200");
	}

	//function to create processes and add them to a Process table that is passed
	void createProcess(ArrayList<Process> PTable, int lambda) 
	{
		for(int i = 0; i < lambda; i++) {
			
			double arrival = TIME + .06;
			double PID = PID_COUNTER++;
			double random = new Random().nextDouble();
			double burst = .05 + (random * (.07 - .05));
			String state = "ready";
			double remaining = burst;
			TIME += arrival - TIME;
			PTable.add(new Process(PID, arrival, burst, state, remaining));
		}
	}

	int sortByArrival(ArrayList<Process> PTable) {
		int index = 0;
		for(int i = 0; i < PTable.size(); i++) {
			if(PTable.get(i).getArrival() < PTable.get(index).getArrival() && PTable.get(i).getState() == "ready")
				index = i;
		}
		
		return index;
	}

	//returns the index of the process with the least remaining CPU_TIME from a process table
	int getShortestP(ArrayList<Process> PTable) {
		int index = 0;
		for(int i = 0; i < PTable.size(); i++) {
			if(PTable.get(i).getRemaining() <= PTable.get(index).getRemaining() && PTable.get(i).getState() == "ready") {
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
		BURST = 0;
		ARRIVAL = 0;
		REMAINING = 0;
		INDEX = 0;
		TIME = 0;
		AVG_QUEUE = 0;
	}

	void FCFS(ArrayList<Process> PTable, int lambda)
	{
		
		reset();
		createProcess(PTable, lambda);
		int terminated = 0;

		//loops for 10000 process
		while (terminated < 10000) {
			int arrivalIndex = sortByArrival(PTable);
			//this is just for clarity in the code
			ARRIVAL = PTable.get(arrivalIndex).getArrival();
			BURST = PTable.get(arrivalIndex).getBurst();

			TIME+=BURST;
			CPU_TIME += BURST;
			WAITCPU_TIME += TIME - ARRIVAL - BURST;
			TURNAROUND += TIME - ARRIVAL + BURST;

			PTable.remove(arrivalIndex);
			terminated++;

			//keeps track of the queue size
			if(terminated % 100 == 0)
				AVG_QUEUE += PTable.size();

			createProcess(PTable, lambda);
		}
		AVG_QUEUE /= 100;
	}
    
	void SRT(ArrayList<Process> PTable, int lambda) 
	{
		//reset the global variables
		reset();
		
		createProcess(PTable, lambda);
		
		int terminated = 0;
		
		//main loop, repeats until 10000 processes are done
		while(terminated < 10000)
		{
			
			//returns the element with the shortest remaining CPU_TIME
			INDEX = getShortestP(PTable);

			//this is just for clarity in the code
			ARRIVAL = PTable.get(INDEX).getArrival();
			REMAINING = PTable.get(INDEX).getRemaining();
			BURST = PTable.get(INDEX).getBurst();

			if(REMAINING < 0.1) {

				TIME += BURST;
				CPU_TIME += BURST;
				WAITCPU_TIME += TIME - ARRIVAL - BURST;
				TURNAROUND += TIME - ARRIVAL + BURST;
				
				PTable.get(INDEX).setRemaining(0);
				terminated++;
				if(terminated % 100 == 0)
					AVG_QUEUE += PTable.size();
					
				//removes element index from the table
				PTable.remove(INDEX);
			}

			else {
				PTable.get(INDEX).setRemaining(REMAINING-.1);
				CPU_TIME += 0.1;
			}
			createProcess(PTable, lambda);

			//TIME++;
		}
		AVG_QUEUE /= 100;
	}
	
	void RR(ArrayList<Process> PTable, double q, int lambda) 
	{
		//resets global variables so we can run the functions one after the other
		reset();
		
		//lets us know if all elements of the array are done in RR
		int terminated = 0;
		
		//fills the table with some elements so the table wont be empty to begin with
		for(int i = 0; i < 10; i++)
			createProcess(PTable, 10);
		
		//loops over the array until there are no more elements that need to access the CPU
		while(terminated < 10000)//was !isDone
		{
			//this is just for clarity in the code
			ARRIVAL = PTable.get(INDEX).getArrival();
			REMAINING = PTable.get(INDEX).getRemaining();
			
			if(REMAINING <= q) 
			{
				TIME += REMAINING;
				CPU_TIME += REMAINING;
				WAITCPU_TIME += TIME - ARRIVAL - REMAINING;
				TURNAROUND += WAITCPU_TIME + BURST;
				PTable.remove(INDEX);
				terminated++;

				//takes the average # of elements in the queue
				if(terminated % 100 == 0)
					AVG_QUEUE += PTable.size();
			}
			
			else {
				TIME += q;
				CPU_TIME += q;
				
				//subtracts q from the remaining CPU_TIME
				PTable.get(INDEX).setRemaining(REMAINING - q);

			}

				createProcess(PTable, lambda);

			//incr index while making sure it loops back to the start
			if(PTable.size() < 10000)
				INDEX = (INDEX + 1) % PTable.size();
			else
				INDEX = (INDEX + 1) % 10000;
		}
		//gets the average size of the queue
		AVG_QUEUE /= 100;
	}
}

