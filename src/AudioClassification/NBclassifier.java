package AudioClassification;

import java.io.FileNotFoundException;
import java.io.IOException;



// run this program
public class NBclassifier {
	// build a NB classifier using bernoulli model -> every spectrogram is described by 250 binary variables
	// high energy = blank
	// low energy = %
	// how to estimate likelihoods
	public static void main(String[] args) throws IOException{

		for(double k = .1; k < 10.0; k= k + .1){
		 
			Posterior P = ParseSpectogram.Posterior("no_train.txt", "yes_train.txt", k);
			double accuracy = ParseSpectogram.argmax(P, "no_test.txt", "yes_test.txt");
			System.out.println(accuracy + " k value: " + k);
		}
		//Integer[][] values = ParseSpectogram.readImage("no_train.txt");
		//ParseImage.readImage("trainingimages");

		//ParseSpectogram.readFile("yes_train");
	}


}
