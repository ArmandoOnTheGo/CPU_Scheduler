package cpu;

public class Process {
	private	double PID;
	private	double arrival;
	private double burst;
	private String state;
	private double remaining;
	
	public Process(double PID, double arrival, double burst, String state, double remaining) {
			this.PID = PID;
			this.arrival = arrival;
			this.burst = burst;
			this.state = state;
			this.remaining = remaining;
		}
		
		double getPID() { return PID;}
		double getArrival() { return arrival;}
		double getBurst() { return burst;}
		String getState() { return state;}
		double getRemaining() { return remaining;}
		
		void setRemaining(double remaining) { this.remaining = remaining;}
		void setState(String state) { this.state = state;}
};
