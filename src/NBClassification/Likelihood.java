package NBClassification;

/**
 * @author Patrick Wong
 *
 */
public class Likelihood {
	private double[][] matrix;
	private int digit; 
	private int k; //laplace smoothing constant
	
	public Likelihood(double[][] matrix, int digit, int k){
		this.matrix = matrix;
		this.digit = digit; 
		this.k = k; 
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}

	public int getDigit() {
		return digit;
	}

	public void setDigit(int digit) {
		this.digit = digit;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}
	
	
	
	

}
