package NBClassification;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.math.*;

public class ParseOptimizedK {

	private static int totRows;
	private static int totCols; 
	private static ArrayList<Integer> Labels;

	public ParseOptimizedK(){

	}


	public static double argmax (Posterior P, String testfile, String testlabel) throws FileNotFoundException{
		Integer[][] testValues = readImage(testfile);
		double[] priors = P.getPrior();
		ArrayList<Integer> testLabels = readLabel(testlabel); // only use for accuracy of classification
		int[] classification = new int[1000]; // compared with testLabels for accuracy. 
		double[] MAParray = new double[10];
		int imageNum = 0;

		for(int i = 0; i < testValues.length; i++){
			if(i!= 0 && i%28 ==0) imageNum += 1; 

			for(int j = 0; j < testValues[0].length; j++){
				//System.out.print(testValues[i][j]);
				for(int digit = 0; digit < 10; digit++){
					if(i%28 == 0 && j == 0){
						MAParray[digit] = priors[digit]; // resets MAP for each new test image 
					}

					if(testValues[i][j] == 1){
						MAParray[digit] = MAParray[digit] + Math.log10(P.getL1().get(digit).getMatrix()[i%28][j]);
					}
					else {
						MAParray[digit] = MAParray[digit] + Math.log10(1.0 - P.getL1().get(digit).getMatrix()[i%28][j]);
					}
				}
				if(i%28 == 27 && j ==27){
					int maxD = 0;
					double max = MAParray[0];
					for(int dig=1; dig <10;dig++){
						if (MAParray[dig]> max){
							//System.out.print(x + "|");
							max = MAParray[dig];
							maxD = dig; 
						}
					}
					classification[imageNum] = maxD; 
				}				
			} 
		}
		double accurate = 0; 
		for(int i = 0; i < testLabels.size(); i++){
			//System.out.println(classification[i]);
			if(testLabels.get(i) == classification[i]) accurate++; 
		}

		return (double)accurate/(double)testLabels.size();
	}

	/**
	 * @param values
	 * @throws FileNotFoundException
	 */
	public static Posterior likelihood (String trainfile) throws FileNotFoundException{
		Integer[][] values = readImage(trainfile); //training images converted to 0s and 1s
		ArrayList<Integer> Labels = readLabel("traininglabels");

		double[] priors = new double[10];
		double k = 1;
		double[][] emptyMatrix = new double[28][28];

		for(int i = 0; i < 28; i++){
			for(int j = 0; j<28; j++){
				emptyMatrix[i][j] = 0;
			}
		}
		double[] numOfTrainingEx = new double[10];

		ArrayList<Likelihood> L = new ArrayList<Likelihood>();//initializing array of 2d matrices --784 features
		ArrayList<Likelihood> L0 = new ArrayList<Likelihood>();
		for(int i =0; i<10; i++){
			double[][] deepcopy = deepCopy(emptyMatrix);
			double[][] deepcopy0 = deepCopy(emptyMatrix);
			L.add(new Likelihood(deepcopy,i,1)); 
			L0.add(new Likelihood(deepcopy0, i,1));
		}

		int i = 0, digit; // i is current label index, 0 to 4999 or 0 to 999

		for(int r = 0; r <totRows; r++){
			digit = Labels.get(i);			
			if(r == 0){
				//System.out.println(i+ "$" + digit); 
				numOfTrainingEx[digit] = numOfTrainingEx[digit] + 1;
			}

			if(r!=0 && r%28 == 0 && (i) < Labels.size()) {
				i = i+1; 
				digit = Labels.get(i);
				//System.out.println(i + "$"+ digit);
				numOfTrainingEx[digit] = numOfTrainingEx[digit] + 1; // keeps track of occurrences of each digit/class
			}

			for(int c = 0; c < totCols; c++){		
				L.get(digit).getMatrix()[r%28][c] = L.get(digit).getMatrix()[r%28][c] + (double)values[r][c];		
			}	

			//System.out.println("(" + r+ "|" + r%28+")" + "digit: " + digit + "|"); //shows % is working
		}	

		for(int dig = 0; dig< 10; dig++){
			// System.out.println("Digit: " + dig);
			priors[dig] = ((double)numOfTrainingEx[dig])/((double)Labels.size());
			//System.out.println("Digit: " + dig + " " + priors[dig]);
			for(int r = 0; r < 28; r++){
				for(int c = 0; c <28; c++){
					//LAPLACE SMOOTHING:
					L.get(dig).getMatrix()[r][c] = (L.get(dig).getMatrix()[r][c] + k)/((numOfTrainingEx[dig]+ k*2.0)); 
					//		System.out.print("(" +L.get(dig).getMatrix()[r][c]+ ")"); 
				}
				//System.out.println();
			}
		}
		Posterior P = new Posterior(L, priors);
		return P; 
		//			
		//			//System.out.println(numOfTrainingEx[dig]+ " digit: " + dig);
		//			//479 + 563 + 488 + 493+535 + 434 +501 + 550 +462 + 495
		//		
	}

	/**
	 * @param fileName
	 * @return values 2d array of data converted to 1 and 0s
	 * @throws FileNotFoundException
	 */
	public static Integer[][] readImage(String fileName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(fileName));
		List<String> lines = new ArrayList<>();
		while(sc.hasNext()) {
			String currLine = sc.nextLine();
			lines.add(currLine);
		}
		sc.close();

		totRows = lines.size();
		totCols = lines.get(0).length();
		//fills up values array with 1 foreground and 0 background

		Integer[][] values = new Integer[totRows][totCols];
		for(int i = 0; i < totRows; i++) {
			for(int j = 0; j < totCols; j++) {
				Character currChar = lines.get(i).charAt(j);
				switch (currChar) {
				case ' ': // empty space
					values[i][j] = 0; 
					break;

				case '!': //empty space
					values[i][j] = 0;
					break;
				default: // foreground
					values[i][j] = 1; 
					break;
				}
				//System.out.print(values[i][j]);
			}
			//System.out.println();	
		}
		//System.out.println(totRows + "#" + totCols);	
		return values;
	}

	/**
	 * @param fileName -- reads in the training/test labels
	 * @return ArrayList of the labels (size 5000)
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Integer> readLabel(String fileName) throws FileNotFoundException {
		ArrayList<Integer> Labels = new ArrayList<Integer>();
		Scanner sc = new Scanner(new File(fileName));
		List<String> lines = new ArrayList<>();
		while(sc.hasNext()) {
			String currLine = sc.nextLine();
			lines.add(currLine);
		}
		sc.close();

		int numRows = lines.size();	
		for(int i = 0; i < numRows; i++){
			Character currLabel = lines.get(i).charAt(0);
			Labels.add(i, Character.getNumericValue(currLabel));
		}
		return Labels;
	}

	private static double[][] deepCopy (double[][] original){
		if(original == null) return null;
		double[][] result = new double[original.length][];
		for(int r =0; r <original.length; r++){
			result[r] = original[r].clone();
		}
		return result;	

	}


	public static int getTotRows() {
		return totRows;
	}


	public static void setTotRows(int totRows) {
		ParseOptimizedK.totRows = totRows;
	}


	public static int getTotCols() {
		return totCols;
	}


	public static void setTotCols(int totCols) {
		ParseOptimizedK.totCols = totCols;
	}


	public static ArrayList<Integer> getLabels() {
		return Labels;
	}


	public static void setLabels(ArrayList<Integer> labels) {
		Labels = labels;
	}

}



