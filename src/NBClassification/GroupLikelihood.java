package NBClassification;

import java.util.ArrayList;

public class GroupLikelihood {
	private GroupFeature[][] matrix; 
	//private int numOfEx; 
	//private static ArrayList<Double> order;
	
	public GroupLikelihood(GroupFeature[][] matrix){
		this.matrix = matrix;
		//this.numOfEx = numOfEx; 
	}

	public GroupFeature[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(GroupFeature[][] matrix) {
		this.matrix = matrix;
	}

}
