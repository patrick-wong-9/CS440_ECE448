package Perceptrons;

public class Distance {
	private double distance;
	private int digit; 
	
	public Distance (double distance, int digit){
		this.distance = distance;
		this.digit = digit;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getDigit() {
		return digit;
	}

	public void setDigit(int digit) {
		this.digit = digit;
	}

	
}
