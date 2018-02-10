package Perceptrons;


/**
 * @author Patrick Wong
 *
 */
public class Weight {
	private double[] vector; // (i, j) = 28*i + j
	// index/28 = row, index - row = col 
	private int digit; 
	
	
	public Weight(double[] vector, int digit){
		this.vector = vector;
		this.digit = digit; 
	}

	public double[] getVector() {
		return vector;
	}

	public void setVector(double[] vector) {
		this.vector = vector;
	}

	public int getDigit() {
		return digit;
	}

	public void setDigit(int digit) {
		this.digit = digit;
	}

	
	

}
