package AudioClassification;

/**
 * @author Patrick Wong
 *
 */
public class Likelihood {
	private double[][] matrix;
	private int numOfEx; 
	
	
	public Likelihood(double[][] matrix, int numOfEx){
		this.matrix = matrix;
		this.numOfEx = numOfEx; 
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}

	public int getNumOfEx() {
		return numOfEx;
	}

	public void setNumOfEx(int numOfEx) {
		this.numOfEx = numOfEx;
	}




}
