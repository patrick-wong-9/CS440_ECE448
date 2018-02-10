package NBClassification;

import java.util.ArrayList;

public class PosteriorGroup {

	private ArrayList<GroupLikelihood> L; 
	private double[] priors; 
	
	public PosteriorGroup (ArrayList<GroupLikelihood> L, double[] priors){
		this.L = L; 
		this.priors = priors; 
	}

	public ArrayList<GroupLikelihood> getL() {
		return L;
	}

	public void setL(ArrayList<GroupLikelihood> l) {
		L = l;
	}

	public double[] getPriors() {
		return priors;
	}

	public void setPriors(double[] priors) {
		this.priors = priors;
	}
	
	
}
