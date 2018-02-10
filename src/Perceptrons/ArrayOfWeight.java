package Perceptrons;
import java.util.ArrayList;


public class ArrayOfWeight {
	private ArrayList<Weight> W;
	private ArrayList<Double> trainingCurve; 
	
	public ArrayOfWeight(ArrayList<Weight> W, ArrayList<Double> trainingCurve){
		this.W = W; 
		this.trainingCurve = trainingCurve; 
	}

	public ArrayList<Weight> getW() {
		return W;
	}

	public void setW(ArrayList<Weight> w) {
		this.W = w;
	}

	public ArrayList<Double> getTrainingCurve() {
		return trainingCurve;
	}

	public void setTrainingCurve(ArrayList<Double> trainingCurve) {
		this.trainingCurve = trainingCurve;
	}

	

}
