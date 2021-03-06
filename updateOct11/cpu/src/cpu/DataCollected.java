package cpu;

public class DataCollected {
	String name = "";
	double CPU_TIME = 0;
	double WAITCPU_TIME = 0;
	double TURNAROUND = 0;
	double TIME = 0;
	int AVG_QUEUE_SIZE = 0;
	
	public DataCollected(String name, double CPU_TIME, double WAITCPU_TIME,
			double TURNAROUND, double TIME, int AVG_QUEUE_SIZE) {
		this.name = name;
		this.CPU_TIME = CPU_TIME;
		this.WAITCPU_TIME = WAITCPU_TIME;
		this.TURNAROUND = TURNAROUND;
		this.TIME = TIME;
		this.AVG_QUEUE_SIZE = AVG_QUEUE_SIZE;
	}
	
	public void display() {
		System.out.println(name + " CPU_TIME = " + CPU_TIME + " WAITCPU_TIME = " + WAITCPU_TIME + " TURNAROUND = " + TURNAROUND + " AVG_QUEUE_SIZE = " + AVG_QUEUE_SIZE);
	}
}
