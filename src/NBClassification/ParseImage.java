package NBClassification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.math.*;
import java.text.DecimalFormat;

/**
 * @author Patrick Wong
 *
 */
/**
 * @author Patrick Wong
 *
 */
public class ParseImage {
	private static int totRows;
	private static int totCols; 
	private static ArrayList<Integer> Labels;

	public ParseImage(){

	}

	public static void printDigit (String file, String label, int row, int col, int index) throws FileNotFoundException{
		ArrayList<Integer> labels = readLabel(label);
		Character[][] image = printImage(file); //character matrix of images
		for(int i = row*index; i < row*(index+1); i++){
			for(int j = 0; j < image[0].length; j++){
				System.out.print(image[i][j]);
			}
			System.out.println();
		}


	}

	/** this is for part 1.1
	 * @param P
	 * @param testfile
	 * @param testlabel
	 * @return
	 * @throws FileNotFoundException
	 */
	public static double argmax (Posterior P, String testfile, String testlabel, int row, int col) throws FileNotFoundException{
		Integer[][] testValues = readImage(testfile);
		double[] priors = P.getPrior();
		int numOfClasses = priors.length;
		ArrayList<Integer> testLabels = readLabel(testlabel); // only use for accuracy of classification

		int[] classification = new int[testLabels.size()]; // predicted digits/classes --> compared with testLabels for accuracy
		double[] MAParray = new double[numOfClasses]; // used to store Likelihoods for each digit FOR every number in testlabel
		int imageNum = 0, index = 0, num; 
		double[] digitTot = new double[numOfClasses];	// holds number of times a digit/class appears in the test data

		// arrays used to classify highest and lowest posterior probs
		double[][] postProb = new double[testLabels.size()][numOfClasses];

		//for most prototypical and least prototypical 
		double[] minPost = new double[numOfClasses];
		double[] minLocations = new double[numOfClasses];	
		double[] maxPost = new double[numOfClasses];
		double[] maxLocations = new double[numOfClasses];

		for(int i = 0; i < testValues.length; i++){
			if(i%row ==0 && index < testLabels.size()){
				if(i > 0) index += 1; 
				num = testLabels.get(index);
				digitTot[num] += 1;
			}

			if(i!= 0 && i%row ==0) imageNum += 1; 

			for(int j = 0; j < testValues[0].length; j++){
				for(int digit = 0; digit < numOfClasses; digit++){
					if(i%row == 0 && j == 0){
						MAParray[digit] = Math.log10(priors[digit]); // resets MAP for each new test image 
					}
					if(testValues[i][j] == 1){
						MAParray[digit] = MAParray[digit] + Math.log10(P.getL1().get(digit).getMatrix()[i%row][j]);
					}
					else {
						MAParray[digit] = MAParray[digit] + Math.log10(1.0 - P.getL1().get(digit).getMatrix()[i%row][j]);
					}
				}

				if(i%row == row-1 && j ==col-1){
					int maxD = 0;
					double max = MAParray[0]; //MAP array is an array of posteriors for each digit class
					for(int dig=1; dig <numOfClasses;dig++){
						if (MAParray[dig]> max){
							max = MAParray[dig];
							maxD = dig; 
						}
					}
					classification[imageNum] = maxD; 

					for(int y =0; y < numOfClasses; y++){
						postProb[index][y] = MAParray[y];
					}

				}				
			} 
		}

		for(int loc = 0; loc < postProb.length; loc++){
			if(classification[loc] == testLabels.get(loc)){
				//initializing the arrays with first correctly classified entry
				if(minPost[classification[loc]] ==0.0){
					minPost[classification[loc]] = postProb[loc][classification[loc]];
					minLocations[classification[loc]] = loc; 
				}
				if(maxPost[classification[loc]] ==0.0){
					maxPost[classification[loc]] = postProb[loc][classification[loc]];
					maxLocations[classification[loc]] = loc; 
				}

				if(postProb[loc][classification[loc]] < minPost[classification[loc]]){
					minPost[classification[loc]] = postProb[loc][classification[loc]];
					minLocations[classification[loc]] = loc; 
				}
				if(postProb[loc][classification[loc]] > maxPost[classification[loc]]){
					maxPost[classification[loc]] = postProb[loc][classification[loc]];
					maxLocations[classification[loc]] = loc; 
				}
			}
		}
		//printDigit(testfile,testlabel,28,28,723);

		//		for(int dig = 0; dig < 10; dig++){
		//			for(int loc = 0; loc < postProb.length; loc++){
		//				//if(classification[loc] == testLabels.get(loc)){
		//					if(loc == 0){//initializes
		//						minPost[dig] = postProb[0][dig];
		//						maxPost[dig] = postProb[0][dig];
		//						minLocations[dig] = 0;
		//						maxLocations[dig] = 0;		
		//					}
		//
		//					if(postProb[loc][dig] < minPost[dig]){
		//						minPost[dig] = postProb[loc][dig];
		//						minLocations[dig] = loc; 
		//					}
		//
		//					if(postProb[loc][dig] > maxPost[dig]){
		//						maxPost[dig] = postProb[loc][dig];
		//						maxLocations[dig] = loc; 
		//					}	
		//			}
		//		}
		//		
		//		for(int a = 0; a < numOfClasses; a++){
		//			System.out.println(" Digit " + a + "| Min Location: " + minLocations[a] +  "| Posterior: " + minPost[a]+ "  | Classified as: " + classification[(int)minLocations[a]]);
		//			System.out.println(" Digit " + a + "| Max Location: " + maxLocations[a] +  "| Posterior: " + maxPost[a]+ "  | Classified as: " + classification[(int)maxLocations[a]]);
		//			System.out.println();
		//		}

		double[][] confusionMatrix = new double[numOfClasses][numOfClasses];
		double setAccuracy = 0; 
		int r =0, c = 0; 
		for(int i = 0; i < testLabels.size(); i++){
			r = testLabels.get(i); //actual
			c = classification[i]; //classified as...
			//System.out.print(c+"|");
			confusionMatrix[r][c] = confusionMatrix[r][c] + 1.0; 
			if(testLabels.get(i) == classification[i]) setAccuracy++; 
		}

		ArrayList<Double> sortedLike = new ArrayList<Double>();
		DecimalFormat df = new DecimalFormat("#.#####");
		df.setRoundingMode(RoundingMode.FLOOR);
		for(int ii = 0; ii < confusionMatrix.length;ii++){
			for(int jj = 0; jj < confusionMatrix[0].length;jj++){
				confusionMatrix[ii][jj] = (confusionMatrix[ii][jj]/digitTot[ii]);
				sortedLike.add(confusionMatrix[ii][jj]);		
				double result = new Double(df.format(confusionMatrix[ii][jj]));
				System.out.print("(" + result + ")");
			}
			System.out.println();
		}
					Collections.sort(sortedLike);
					for(int u = 0; u < sortedLike.size(); u++){
						System.out.println(sortedLike.get(u));
					}

		//featureLike(P,0);
		//	featureLike(P,3);
		oddsRatio(P,0,1);
		return (double)setAccuracy/(double)testLabels.size();
	}

	public static double argmaxDisjoint (PosteriorGroup P, String testfile, String testlabel, int r, int c, int row, int col) throws FileNotFoundException{
		Integer[][] testValues = readImage(testfile);
		double[] priors = P.getPriors(); 
		ArrayList<Integer> testLabels = readLabel(testlabel); 
		int numOfClasses = priors.length;

		int[] classification = new int[testLabels.size()];
		double[] MAParray = new double[numOfClasses];
		double[] digitTot = new double[numOfClasses];
		int imageNum = 0, index = 0, num; //index is current label index

		for(int i = 0; i < testValues.length; i++){
			if(i%row ==0 && index < testLabels.size()){
				if(i > 0) index += 1; 
				num = testLabels.get(index);
				digitTot[num] += 1;
			}			
			if(i!= 0 && i%row ==0) imageNum += 1; 

			for(int j = 0; j < testValues[0].length; j++){
				for(int digit = 0; digit < numOfClasses; digit++){
					if(i%row == 0 && j == 0){
						MAParray[digit] = Math.log10(priors[digit]); // resets MAP for each new test image 
					}
					if((i%r) == 0 && (j%c) == 0){ // ensures we start at top left corner of each feature. 
						ArrayList<Double> tuple = new ArrayList<Double>();
						for(int x = i; x < i+r; x++){
							for(int y = j; y < j+c; y++){
								tuple.add((double)testValues[x][y]);
							}
						}
						int f = binaryToIndex(tuple); 

						MAParray[digit] = MAParray[digit]+Math.log10(P.getL().get(digit).getMatrix()[(i%row)/r][j/c].getAllTuples()[f]);
					}
				}
				if((i%row) == (row-1) && j == (col-1)){
					int maxD = 0;
					double max = MAParray[0]; //MAP array is an array of posteriors for each digit class
					for(int dig=1; dig <numOfClasses;dig++){
						if (MAParray[dig]> max){
							max = MAParray[dig];
							maxD = dig; 
						}
					}
					classification[imageNum] = maxD; 
				}			
			}
		}


		double setAccuracy = 0; 

		for(int a = 0; a < testLabels.size(); a++){
			if(testLabels.get(a) == classification[a]) setAccuracy++; 
		}

		return setAccuracy/(double)testLabels.size(); 
	}

	public static double argmaxOverlap (PosteriorGroup P, String testfile, String testlabel, int r, int c, int row, int col) throws FileNotFoundException{
		Integer[][] testValues = readImage(testfile);
		double[] priors = P.getPriors(); 
		ArrayList<Integer> testLabels = readLabel(testlabel); 

		int numOfClasses = priors.length; 

		int[] classification = new int[testLabels.size()];
		double[] MAParray = new double[numOfClasses];
		double[] digitTot = new double[numOfClasses];
		int imageNum = 0, index = 0, num; //index is current label index

		for(int i = 0; i < testValues.length; i++){
			if(i%row ==0 && index < testLabels.size()){
				if(i > 0) index += 1; 

				num = testLabels.get(index);
				digitTot[num] += 1;
			}			
			if(i!= 0 && i%row ==0) imageNum += 1; 

			for(int j = 0; j < testValues[0].length; j++){
				for(int digit = 0; digit < numOfClasses; digit++){
					if(i%row == 0 && j == 0){
						MAParray[digit] = Math.log10(priors[digit]); // resets MAP for each new test image 
					}
					if( ((i%row)+r-1) <row-1 && (j+c-1) < col-1){
						ArrayList<Double> tuple = new ArrayList<Double>();
						for(int x = i; x < i+r; x++){
							for(int y = j; y < j+c; y++){
								tuple.add((double)testValues[x][y]);
							}
						}
						int f = binaryToIndex(tuple); 

						MAParray[digit] = MAParray[digit]+Math.log10(P.getL().get(digit).getMatrix()[(i%row)][j].getAllTuples()[f]);
					}
				}

				if(i%row == row-1 && j ==col-1){
					int maxD = 0;
					double max = MAParray[0]; //MAP array is an array of posteriors for each digit class
					for(int dig=1; dig <numOfClasses;dig++){
						if (MAParray[dig]> max){
							max = MAParray[dig];
							maxD = dig; 
						}
					}
					classification[imageNum] = maxD; 
				}				
			}
		}

		double setAccuracy = 0; 

		for(int a = 0; a < testLabels.size(); a++){
			if(testLabels.get(a) == classification[a]) setAccuracy++; 
		}

		return setAccuracy/(double)testLabels.size(); 
	}


	/** prints out graph of  for digit c
	 * @param P
	 * @param c, digit of choice for a graph
	 * @return
	 */
	public static Character[][] featureLike (Posterior P, int c){
		double[][] like = P.getL1().get(c).getMatrix();
		Character[][] graph = new Character[like.length][like[0].length];

		System.out.println("Likelihood graph of digit: " + c);
		for(int i = 0; i < like.length; i++){
			for(int j = 0; j < like[0].length; j++){
				if(Math.log10(like[i][j]) >= -0.12) graph[i][j] = '+';
				else if(Math.log10(like[i][j])< -0.12 && Math.log10(like[i][j])> -0.30) graph[i][j] = 'x';
				else if (Math.log10(like[i][j]) <= -0.30) graph[i][j] = '-';
				System.out.print(graph[i][j]);

			}
			System.out.println();
		}
		return graph;
	}

	public static Character[][] oddsRatio (Posterior P, int c1, int c2){
		double[][] like1 = P.getL1().get(c1).getMatrix();
		double[][] like2 = P.getL1().get(c2).getMatrix();
		Character[][] graph = new Character[like2.length][like2[0].length];
		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.FLOOR);
		System.out.println("Odds Ratio graph of digit: " + c1 + "/"+ c2);
		for(int i = 0; i < like1.length; i++){
			for(int j = 0; j < like1[0].length; j++){
				if(Math.log10(like1[i][j]/like2[i][j]) > 0) graph[i][j] = '+';
				if(Math.log10(like1[i][j]/like2[i][j]) < 0) graph[i][j] = '-';
				if(Math.log10(like1[i][j]/like2[i][j]) > -.1 && Math.log10(like1[i][j]/like2[i][j]) < .1) graph[i][j] = ' ';

				//	if(Math.log10(like1[i][j]/like2[i][j]) < -1.3 &&Math.log10(like1[i][j]/like2[i][j]) > -3) graph[i][j] = 'x';
				System.out.print(graph[i][j]);
				//System.out.print("(" + df.format(Math.log10(like1[i][j]/like2[i][j])) + ")");
			}
			System.out.println();
		}
		// + positive log odds, '' close to 1, - negative log

		return null;
	}

	public static PosteriorGroup OverlapLikelihood (String trainfile, String trainlabel, double k, int r, int c, int imgRow, int imgCol, int numOfClasses, int binOrTern) throws FileNotFoundException{
		Integer[][] values = readImage(trainfile); //training imgs converted to binary
		
		if(binOrTern==3){
			values = ternaryReadImage(trainfile);
		}
		ArrayList<Integer> Labels = readLabel(trainlabel);
		double[] priors = new double[numOfClasses];

		ArrayList<GroupLikelihood> L = new ArrayList<GroupLikelihood>();

		double[] features = new double[(int) Math.pow(binOrTern, r*c)];
		for(int i = 0; i < features.length; i++){
			features[i] = 0; 
		}
		double[] numOfTrainingEx= new double[numOfClasses];

		GroupFeature[][] emptyMatrix = new GroupFeature[imgRow-r+1][imgCol-c+1];
		for(int row = 0; row < emptyMatrix.length; row++){
			for(int col = 0; col < emptyMatrix[0].length; col++){
				emptyMatrix[row][col] = new GroupFeature(features.clone());
				//sets all entries (arrays) to be arrays of length 2^(n*m) filled with 0s
			}
		}
		for(int dig = 0; dig < numOfClasses; dig++){	
			GroupFeature[][] copy = groupCopy(emptyMatrix, features);
			L.add(new GroupLikelihood(copy));
		}

		int digit = 0; 
		int curr = 0; //current label index

		for(int i = 0; i < values.length; i++){
			digit = Labels.get(curr);
			if (i == 0) numOfTrainingEx[digit]=numOfTrainingEx[digit]+ 1; 
			if (i != 0 && i%imgRow == 0 && curr < Labels.size()){
				curr = curr + 1; 
				digit = Labels.get(curr);
				numOfTrainingEx[digit] =numOfTrainingEx[digit]+1; //keeps track of occurences of each digit 
			}

			for(int j = 0; j < values[0].length; j++){

				if((i%imgRow)+(r-1) < imgRow && (j%imgCol)+(c-1) < imgCol){ // ensures we start at top left corner of each feature. 
					ArrayList<Double> tuple = new ArrayList<Double>();
					for(int x = i; x < i+r; x++){
						for(int y = j; y < j+c; y++){
							tuple.add((double)values[x][y]);
						}
					}
					int index = binaryToIndex(tuple); 
					if(binOrTern ==3) index = ternaryToIndex(tuple);
					L.get(digit).getMatrix()[(i%imgRow)][j].getAllTuples()[index] =L.get(digit).getMatrix()[(i%imgRow)][j].getAllTuples()[index] + 1; 
				}
			}
		}
		for (int dig = 0; dig < numOfClasses; dig++){
			priors[dig] = (double)numOfTrainingEx[dig]/(double)Labels.size();
			for (int ii=0;ii< (imgRow-r+1); ii++){
				for (int jj = 0; jj < (imgCol-c+1); jj++){
					for(int a = 0; a < Math.pow(2.0, r*c); a++){
						L.get(dig).getMatrix()[ii][jj].getAllTuples()[a] = ((L.get(dig).getMatrix()[ii][jj].getAllTuples()[a] + k)/((double)numOfTrainingEx[dig] + features.length*k));
					}
				}
			}
		}
		PosteriorGroup P = new PosteriorGroup(L, priors);
		return P;
	}


	/**
	 * @param trainfile
	 * @param k
	 * @param r - group feature row size
	 * @param c - group feature col size
	 * @return
	 * @throws FileNotFoundExcpetion
	 */
	public static PosteriorGroup DisjointLikelihood (String trainfile, String trainlabel, double k, int r, int c, int imgRow, int imgCol, int numOfClasses, int binOrTern) throws FileNotFoundException{
		Integer[][] values = ternaryReadImage(trainfile);
		if(binOrTern ==2){
			values = readImage(trainfile); //training imgs converted to binary
		}
		if(binOrTern ==3){
			values = ternaryReadImage(trainfile);
		}
		ArrayList<Integer> Labels = readLabel(trainlabel);
		double[] priors = new double[numOfClasses];

		ArrayList<GroupLikelihood> L = new ArrayList<GroupLikelihood>();

		double[] features = new double[(int) Math.pow((double)binOrTern, r*c)];
		for(int i = 0; i < features.length; i++){
			features[i] = 0; 
		}
		double[] numOfTrainingEx= new double[numOfClasses];

		GroupFeature[][] emptyMatrix = new GroupFeature[imgRow/r][imgCol/c];
		for(int row = 0; row < emptyMatrix.length; row++){
			for(int col = 0; col < emptyMatrix[0].length; col++){
				emptyMatrix[row][col] = new GroupFeature(features.clone());
				//sets all entries (arrays) to be arrays of length 2^(n*m) filled with 0s
			}
		}

		for(int dig = 0; dig < numOfClasses; dig++){	
			GroupFeature[][] copy = groupCopy(emptyMatrix, features);
			L.add(new GroupLikelihood(copy));
		}

		int digit = 0; 
		int curr = 0; //current label index

		for(int i = 0; i < values.length; i++){
			digit = Labels.get(curr);
			if (i == 0) numOfTrainingEx[digit]=numOfTrainingEx[digit]+ 1; 
			if (i != 0 && i%imgRow == 0 && curr < Labels.size()){
				curr = curr + 1; 
				digit = Labels.get(curr);
				numOfTrainingEx[digit] =numOfTrainingEx[digit]+1; //keeps track of occurences of each digit 
			}

			for(int j = 0; j < values[0].length; j++){

				if( (i%r) == 0 && (j%c) == 0){ // ensures we start at top left corner of each feature. 
					ArrayList<Double> tuple = new ArrayList<Double>();
					for(int x = i; x < i+r; x++){
						for(int y = j; y < j+c; y++){
							//System.out.print(values[x][y]);
							tuple.add((double)values[x][y]);
						}
					}
					int index = 0; 
					if(binOrTern == 2){
						index = binaryToIndex(tuple); 
					}
					
					if(binOrTern==3){
						index = ternaryToIndex(tuple);
					}
					L.get(digit).getMatrix()[(i%imgRow)/r][j/c].getAllTuples()[index] =L.get(digit).getMatrix()[(i%imgRow)/r][j/c].getAllTuples()[index] + 1; 
				}
			}
		}
		for (int dig = 0; dig < numOfClasses; dig++){
			priors[dig] = (double)numOfTrainingEx[dig]/(double)Labels.size();
			for (int ii=0;ii< imgRow/r; ii++){
				for (int jj = 0; jj < imgCol/c; jj++){
					for(int a = 0; a < Math.pow(2.0, r*c); a++){
						L.get(dig).getMatrix()[ii][jj].getAllTuples()[a] = ((L.get(dig).getMatrix()[ii][jj].getAllTuples()[a] + k)/((double)numOfTrainingEx[dig] + features.length*k));
					}
				}
			}
		}
		PosteriorGroup P = new PosteriorGroup(L, priors);
		return P;
	}


	public static double argmaxTernary(PosteriorGroup P, String testfile, String testlabel, int r, int c, int row, int col) throws FileNotFoundException{
		Integer[][] testValues = ternaryReadImage(testfile);
		double[] priors = P.getPriors(); 
		ArrayList<Integer> testLabels = readLabel(testlabel); 
		int numOfClasses = priors.length;

		int[] classification = new int[testLabels.size()];
		double[] MAParray = new double[numOfClasses];
		double[] digitTot = new double[numOfClasses];
		int imageNum = 0, index = 0, num; //index is current label index

		for(int i = 0; i < testValues.length; i++){
			if(i%row ==0 && index < testLabels.size()){
				if(i > 0) index += 1; 
				num = testLabels.get(index);
				digitTot[num] += 1;
			}			
			if(i!= 0 && i%row ==0) imageNum += 1; 

			for(int j = 0; j < testValues[0].length; j++){
				for(int digit = 0; digit < numOfClasses; digit++){
					if(i%row == 0 && j == 0){
						MAParray[digit] = Math.log10(priors[digit]); // resets MAP for each new test image 
					}
					int f = testValues[i][j];
					MAParray[digit] = MAParray[digit]+Math.log10(P.getL().get(digit).getMatrix()[(i%row)/r][j/c].getAllTuples()[f]);
				}
				if((i%row) == (row-1) && j == (col-1)){
					int maxD = 0;
					double max = MAParray[0]; //MAP array is an array of posteriors for each digit class
					for(int dig=1; dig <numOfClasses;dig++){
						if (MAParray[dig]> max){
							max = MAParray[dig];
							maxD = dig; 
						}
					}
					classification[imageNum] = maxD; 
				}			
			}
		}


		double setAccuracy = 0; 

		for(int a = 0; a < testLabels.size(); a++){
			if(testLabels.get(a) == classification[a]) setAccuracy++; 
		}

		return setAccuracy/(double)testLabels.size(); 
	}

	public static PosteriorGroup ternaryLikelihood (String trainfile, String trainlabel, double k, int imgRow, int imgCol, int numOfClasses) throws FileNotFoundException{
		Integer[][] values = ternaryReadImage(trainfile); //training imgs converted to ternary
		ArrayList<Integer> Labels = readLabel(trainlabel);
		double[] priors = new double[numOfClasses];
		ArrayList<GroupLikelihood> L = new ArrayList<GroupLikelihood>();
		double[] features = new double[3];
		double[] numOfTrainingEx= new double[numOfClasses];

		GroupFeature[][] emptyMatrix = new GroupFeature[imgRow][imgCol];//28 b 28 
		for(int row = 0; row < emptyMatrix.length; row++){
			for(int col = 0; col < emptyMatrix[0].length; col++){
				emptyMatrix[row][col] = new GroupFeature(features.clone());
				//sets all entries (arrays) to be arrays of length 2^(n*m) filled with 0s
			}
		}

		for(int dig = 0; dig < numOfClasses; dig++){	
			GroupFeature[][] copy = groupCopy(emptyMatrix, features);
			L.add(new GroupLikelihood(copy));
		}

		int digit = 0; 
		int curr = 0; //current label index

		for(int i = 0; i < values.length; i++){
			digit = Labels.get(curr);
			if (i == 0) numOfTrainingEx[digit]=numOfTrainingEx[digit]+ 1; 
			if (i != 0 && i%imgRow == 0 && curr < Labels.size()){
				curr = curr + 1; 
				digit = Labels.get(curr);
				numOfTrainingEx[digit] =numOfTrainingEx[digit]+1; //keeps track of occurences of each digit 
			}

			for(int j = 0; j < values[0].length; j++){
				int index = values[i][j];
				L.get(digit).getMatrix()[(i%imgRow)][j].getAllTuples()[index] =L.get(digit).getMatrix()[(i%imgRow)][j].getAllTuples()[index] + 1; 

			}
		}
		for (int dig = 0; dig < numOfClasses; dig++){
			priors[dig] = (double)numOfTrainingEx[dig]/(double)Labels.size();
			for (int ii=0;ii< imgRow; ii++){
				for (int jj = 0; jj < imgCol; jj++){
					for(int a = 0; a < 3; a++){
						L.get(dig).getMatrix()[ii][jj].getAllTuples()[a] = ((L.get(dig).getMatrix()[ii][jj].getAllTuples()[a] + k)/((double)numOfTrainingEx[dig] + features.length*k));
					}
				}
			}
		}
		PosteriorGroup P = new PosteriorGroup(L, priors);
		return P;
	}

	/**
	 * @param values
	 * @throws FileNotFoundException
	 */
	public static Posterior likelihood (String trainfile, String trainlabel, double k, int row, int col, int numOfClasses) throws FileNotFoundException{
		Integer[][] values = readImage(trainfile); //training images converted to 0s and 1s
		ArrayList<Integer> Labels = readLabel(trainlabel);

		double[] priors = new double[numOfClasses];
		double[][] emptyMatrix = new double[row][col];

		for(int i = 0; i < row; i++){
			for(int j = 0; j<col; j++){
				emptyMatrix[i][j] = 0;
			}
		}
		double[] numOfTrainingEx = new double[numOfClasses];

		ArrayList<Likelihood> L = new ArrayList<Likelihood>();// likelihood of foreground
		//initializing array of 2d matrices --784 features
		for(int i =0; i<numOfClasses; i++){
			double[][] deepcopy = deepCopy(emptyMatrix);
			L.add(new Likelihood(deepcopy,i,1)); 
		}

		int i = 0, digit; // i is current label index, 0 to 4999 or 0 to 999


		for(int r = 0; r <values.length; r++){
			digit = Labels.get(i);			

			if(r == 0) numOfTrainingEx[digit] = numOfTrainingEx[digit] + 1;

			if(r!=0 && r%row == 0 && i < Labels.size()) {
				i = i+1; 
				digit = Labels.get(i);
				numOfTrainingEx[digit] = numOfTrainingEx[digit] + 1; // keeps track of occurrences of each digit/class
			}

			for(int c = 0; c < values[0].length; c++){		
				L.get(digit).getMatrix()[r%row][c] = L.get(digit).getMatrix()[r%row][c] + (double)values[r][c];	
			}	
			//System.out.println("(" + r+ "|" + r%28+")" + "digit: " + digit + "|"); //shows % is working
		}	

		for(int dig = 0; dig< numOfClasses; dig++){
			//System.out.println("Digit: " + dig);
			priors[dig] = ((double)numOfTrainingEx[dig])/((double)Labels.size());
			//System.out.println("Digit: " + dig + " " + priors[dig]);
			for(int r = 0; r < row; r++){
				for(int c = 0; c <col; c++){
					//LAPLACE SMOOTHING:	
					L.get(dig).getMatrix()[r][c] = (L.get(dig).getMatrix()[r][c] + k)/((numOfTrainingEx[dig]+ k*2.0)); 
					//System.out.print("(" +L.get(dig).getMatrix()[r][c]+ ")"); 
				}
				//System.out.println();
			}
		}
		Posterior P = new Posterior(L, priors);
		return P; 	
	}

	public static Character[][] printImage(String fileName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(fileName));
		List<String> lines = new ArrayList<>();
		while(sc.hasNext()) {
			String currLine = sc.nextLine();
			lines.add(currLine);
		}
		sc.close();
		Character[][] image = new Character[lines.size()][lines.get(0).length()]; 
		for(int i = 0; i < totRows; i++) {
			for(int j = 0; j < totCols; j++) {
				image[i][j] = lines.get(i).charAt(j);
			}
		}
		return image; 
	}

	/**
	 * @param fileName
	 * @return values 2d array of data converted to 1 and 0s
	 * @throws FileNotFoundException
	 */
	public static Integer[][] ternaryReadImage(String fileName) throws FileNotFoundException {
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
				case '#': 
					values[i][j] = 2; 
					break;
				case '+':
					values[i][j] = 1; 
					break;
				default: // foreground + 
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
				//	System.out.print(values[i][j]);
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

	private static GroupFeature[][] groupCopy (GroupFeature[][] original, double[] emptyArray){
		if (original == null) return null; 
		GroupFeature[][] result = new GroupFeature[original.length][original[0].length];
		for(int i = 0; i < original.length; i++){
			for(int j = 0; j < original[0].length; j++){
				result[i][j] = new GroupFeature(emptyArray.clone());
			}
		}
		return result; 
	}

	private static int ternaryToIndex (ArrayList<Double> ternary){
		int index = 0; 
		for(int t = 0; t < ternary.size(); t++){
			index = index +(int) (ternary.get(t)*(int)Math.pow(3.0, t));
		}
		return index; 
	}

	private static int binaryToIndex (ArrayList<Double> binary){

		int index = 0; 
		for(int t = 0; t < binary.size(); t++){
			index = index +(int) (binary.get(t)*(int)Math.pow(2.0, t));
		}
		return index; 
	}


	public static int getTotRows() {
		return totRows;
	}

	public static void setTotRows(int totRows) {
		ParseImage.totRows = totRows;
	}

	public static int getTotCols() {
		return totCols;
	}

	public static void setTotCols(int totCols) {
		ParseImage.totCols = totCols;
	}

	public static ArrayList<Integer> getLabels() {
		return Labels;
	}

	public static void setLabels(ArrayList<Integer> labels) {
		Labels = labels;
	}



}
