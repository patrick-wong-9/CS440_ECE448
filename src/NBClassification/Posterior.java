package NBClassification;

import java.util.ArrayList;

public class Posterior {
	private ArrayList<Likelihood> L1; 
	private double[] prior;
	
	public Posterior(ArrayList<Likelihood> L1, double[] prior){
		this.L1 = L1;
		this.prior = prior; 
	}

	public ArrayList<Likelihood> getL1() {
		return L1;
	}

	public void setL1(ArrayList<Likelihood> l1) {
		L1 = l1;
	}

	public double[] getPrior() {
		return prior;
	}

	public void setPrior(double[] prior) {
		this.prior = prior;
	}


	

}
