package Perceptrons;
import java.awt.BorderLayout;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;



public class Perceptron {
	private static double epoch;
	private static double alpha;
	private static double b; 
	private static boolean bias;
	private static boolean randomOrder; 
	private static boolean randomWeights;
	private static int row;
	private static int col; 
	private static int numOfClasses; 
	private static int numTrainingEx;
	private static int numTestEx;



	public Perceptron(){

	}

	public static ArrayOfWeight trainOVERLAP (String trainfile, String trainlabel, ArrayOfWeight W, int r, int c) throws FileNotFoundException{
		ArrayList<Integer> Labels = readLabel(trainlabel);
		ArrayList<Double[]> binImageVec = binaryImageVec(trainfile);
		ArrayList<Weight> weights = W.getW(); 

		double numCorrect = 0; 
		double[] digit = new double[numOfClasses];
		double[] numOfTrainingExamples = new double[numOfClasses]; 
		Double[] currImage;
		int currLabel;
		//int vectorIndex = 0;
		double[] perceptron = new double[numOfClasses]; 

		for(int i = 0; i < 5000; i++){
			currLabel = Labels.get(i);
			currImage = binImageVec.get(i); 
			numOfTrainingExamples[currLabel] += 1; //total appearances of a digit

			for(int a = 0; a < numOfClasses; a++){
				perceptron[a] = b; //initialize the perceptrons each training example
			}
			for(int dig = 0; dig < numOfClasses; dig++){
				int vectorIndex = -1; 
				for(int rr = 0; rr < row; rr++){
					for(int cc = 0; cc < col; cc++){
						if((rr+r-1 <28) && (cc%28 + c-1) < 28) {
							vectorIndex++; 
							ArrayList<Double> tuple = new ArrayList<Double>();
							for(int x = rr; x < rr+r; x++){
								for(int y = cc; y < cc+c; y++){
									tuple.add((double)currImage[x*28+y]);
								}
							}
							int val = binaryToIndex(tuple);
							perceptron[dig] += weights.get(dig).getVector()[vectorIndex] * val;
						}
					}
				}
			}

			double max = perceptron[0];
			int predicted = 0;
			for(int a = 1; a < numOfClasses; a++){
				if(perceptron[a]> max){
					max = perceptron[a];
					predicted = a; 
				}
			}
			if(predicted == currLabel){
				digit[predicted]+= 1;
				numCorrect += 1; 
			}
			else{
				int index = -1; 
				for(int rr = 0; rr < row; rr++){
					for(int cc = 0; cc < col; cc++){
						ArrayList<Double> tuple = new ArrayList<Double>();
						if((rr%r) ==0 && (cc%c) ==0){
							index++; 
							for(int x = rr; x < rr+r; x++){
								for(int y = cc; y < cc+c; y++){
									tuple.add((double)currImage[x*28+y]);
								}
							}
						}
						int val = binaryToIndex(tuple);
						weights.get(currLabel).getVector()[index] += alpha * val;
						weights.get(predicted).getVector()[index] -= alpha * val;
					}
				}
				if(bias){
					weights.get(currLabel).getVector()[index] += alpha * b;
					weights.get(predicted).getVector()[index] -= alpha * b;
				}
			}
		}
		numCorrect = numCorrect/5000;
		System.out.println("Training Accuracy: " + numCorrect);	
		W.setW(weights);
		W.getTrainingCurve().add(numCorrect);
		return W; 	
	}


	public static ArrayOfWeight trainDISJOINT (String trainfile, String trainlabel, ArrayOfWeight W, int r, int c) throws FileNotFoundException{
		ArrayList<Integer> Labels = readLabel(trainlabel);
		ArrayList<Double[]> binImageVec = binaryImageVec(trainfile);
		ArrayList<Weight> weights = W.getW(); 

		double numCorrect = 0; 
		double[] digit = new double[numOfClasses];
		double[] numOfTrainingExamples = new double[numOfClasses]; 
		Double[] currImage;
		int currLabel;
		//int vectorIndex = 0;
		double[] perceptron = new double[numOfClasses]; 

		for(int i = 0; i < 5000; i++){
			currLabel = Labels.get(i);
			currImage = binImageVec.get(i); 
			numOfTrainingExamples[currLabel] += 1; //total appearances of a digit

			for(int a = 0; a < numOfClasses; a++){
				perceptron[a] = b; //initialize the perceptrons each training example
			}
			for(int dig = 0; dig < numOfClasses; dig++){
				int vectorIndex = -1; 
				for(int rr = 0; rr < row; rr++){
					for(int cc = 0; cc < col; cc++){

						ArrayList<Double> tuple = new ArrayList<Double>();
						if((rr%r) ==0 && (cc%c) ==0){
							vectorIndex++; 
							for(int x = rr; x < rr+r; x++){
								for(int y = cc; y < cc+c; y++){
									tuple.add((double)currImage[x*28+y]);
								}
							}
							int val = binaryToIndex(tuple);
							//vectorIndex = rr*row/r + cc/c;
							//System.out.println(vectorIndex);
							perceptron[dig] += weights.get(dig).getVector()[vectorIndex] * val;
						}
					}
				}
			}

			double max = perceptron[0];
			int predicted = 0;
			for(int a = 1; a < numOfClasses; a++){
				if(perceptron[a]> max){
					max = perceptron[a];
					predicted = a; 
				}
			}
			if(predicted == currLabel){
				digit[predicted]+= 1;
				numCorrect += 1; 
			}
			else{
				int index = -1; 
				for(int rr = 0; rr < row; rr++){
					for(int cc = 0; cc < col; cc++){
						ArrayList<Double> tuple = new ArrayList<Double>();
						if((rr%r) ==0 && (cc%c) ==0){
							index++; 
							for(int x = rr; x < rr+r; x++){
								for(int y = cc; y < cc+c; y++){
									tuple.add((double)currImage[rr*28+cc]);
								}
							}
						}
						int val = binaryToIndex(tuple);
						//index = rr*row + cc; 
						weights.get(currLabel).getVector()[index] += alpha * val;
						weights.get(predicted).getVector()[index] -= alpha * val;
					}
				}
				if(bias){
					weights.get(currLabel).getVector()[index] += alpha * b;
					weights.get(predicted).getVector()[index] -= alpha * b;
				}
			}
		}
		numCorrect = numCorrect/5000;
		System.out.println("Training Accuracy: " + numCorrect);	
		W.setW(weights);
		W.getTrainingCurve().add(numCorrect);
		return W; 	
	}

	private static int binaryToIndex (ArrayList<Double> binary){
		int index = 0; 
		for(int t = 0; t < binary.size(); t++){
			index = index +(int) (binary.get(t)*(int)Math.pow(2.0, t));
		}
		return index; 
	}



	/**
	 * @param values
	 * @return 
	 * @throws FileNotFoundException
	 */
	public static ArrayOfWeight train (String trainfile, String trainlabel, ArrayOfWeight W) throws FileNotFoundException{
		ArrayList<Integer> Labels = readLabel(trainlabel);
		ArrayList<Double[]> binImageVec = binaryImageVec(trainfile);
		ArrayList<Weight> weights = W.getW(); 

		double accuracy = 0; 
		double[] digit = new double[numOfClasses]; // number of right digit
		double[] numOfTrainingExamples = new double[numOfClasses]; //tot number of digits in training


		ArrayList<Integer> trainExIndex = new ArrayList<Integer>();
		for(int i =0; i < numTrainingEx; i++){
			trainExIndex.add(i);
		}
		if(randomOrder) {
			Collections.shuffle(trainExIndex);
		}
		Double[] currImage; 
		int currLabel; 
		int vectorIndex = 0; 
		double[] perceptron = new double[numOfClasses];

		for(Integer i: trainExIndex){
			currLabel = Labels.get(i);
			currImage = binImageVec.get(i); 
			numOfTrainingExamples[currLabel] += 1; //total appearances of a digit

			for(int a = 0; a < numOfClasses; a++){
				perceptron[a] = b; //initialize the perceptrons each training example
			}
			for(int dig = 0; dig < numOfClasses; dig++){
				for(int r = 0; r < row; r++){
					for(int c = 0; c < col; c++){
						vectorIndex = r*row + c; 
						perceptron[dig] += weights.get(dig).getVector()[vectorIndex] * currImage[vectorIndex];				
					}
				}
			}
			double max = perceptron[0];
			int predicted = 0;
			for(int a = 1; a < numOfClasses; a++){
				if(perceptron[a]> max){
					max = perceptron[a];
					predicted = a; 
				}
			}
			if(predicted == currLabel){
				digit[predicted]+= 1;
				accuracy += 1; 
			}
			else{
				int index = 0; 
				for(int r = 0; r < row; r++){
					for(int c = 0; c < col; c++){
						index = r*row + c; 
						weights.get(currLabel).getVector()[index] += alpha * currImage[index];
						weights.get(predicted).getVector()[index] -= alpha * currImage[index];
					}
				}
				if(bias){
					weights.get(currLabel).getVector()[index] += alpha * b;
					weights.get(predicted).getVector()[index] -= alpha * b;
				}
			}
		}

		accuracy = accuracy/5000;

		//System.out.println("Training Accuracy: " + accuracy);	
		W.setW(weights);
		W.getTrainingCurve().add(accuracy);
		return W; 	
	}


	public static double testOVERLAP (String testfile, String testlabel, ArrayOfWeight W, int r, int c) throws FileNotFoundException{
		ArrayList<Integer> Labels = readLabel(testlabel);
		ArrayList<Double[]> binImageVec = binaryImageVec(testfile);
		ArrayList<Weight> weights = W.getW(); 

		double numCorrect = 0; 
		double[] digit = new double[numOfClasses];
		double[] numOfTestExamples = new double[numOfClasses]; 
		Double[] currImage;
		int currLabel;
		//int vectorIndex = 0;
		double[][] confusionMatrix = new double[10][10];
		double[] perceptron = new double[numOfClasses]; 

		for(int i = 0; i < 1000; i++){
			currLabel = Labels.get(i);
			currImage = binImageVec.get(i); 
			numOfTestExamples[currLabel] += 1; //total appearances of a digit

			for(int a = 0; a < numOfClasses; a++){
				perceptron[a] = b; //initialize the perceptrons each training example
			}
			for(int dig = 0; dig < numOfClasses; dig++){
				int vectorIndex = -1; 
				for(int rr = 0; rr < row; rr++){
					for(int cc = 0; cc < col; cc++){				
						if((rr+r-1 <28) && (cc%28 + c-1) < 28) {
							vectorIndex++; 
							ArrayList<Double> tuple = new ArrayList<Double>();
							for(int x = rr; x < rr+r; x++){
								for(int y = cc; y < cc+c; y++){
									tuple.add((double)currImage[x*28+y]);
								}
							}
							int val = binaryToIndex(tuple);
							//vectorIndex = rr*row/r + cc/c;
							//System.out.println(vectorIndex);
							perceptron[dig] += weights.get(dig).getVector()[vectorIndex] * val;
						}
					}
				}
			}

			double max = perceptron[0];
			int predicted = 0;
			for(int a = 1; a < numOfClasses; a++){
				if(perceptron[a]> max){
					max = perceptron[a];
					predicted = a; 
				}
			}
			if(predicted == currLabel){
				digit[predicted]+= 1;
				numCorrect += 1; 
			}
			confusionMatrix[currLabel][predicted]+=1; 


		}
		System.out.println("Confusion Matrix Overlaping Group");
		for(int ii = 0; ii < confusionMatrix.length;ii++){
			for(int jj = 0; jj < confusionMatrix[0].length;jj++){
				confusionMatrix[ii][jj] = (100*confusionMatrix[ii][jj]/numOfTestExamples[ii]);
				System.out.format("%10.2f", confusionMatrix[ii][jj]);
				System.out.print("%");
			}
			System.out.println();
		}


		numCorrect = numCorrect/1000;
		System.out.println("Test Accuracy: " + numCorrect);	

		return numCorrect; 
	}


	public static double testingDisjoint (String testfile, String testlabel, ArrayOfWeight W, int r, int c) throws FileNotFoundException{
		ArrayList<Integer> Labels = readLabel(testlabel);
		ArrayList<Double[]> binImageVec = binaryImageVec(testfile);
		ArrayList<Weight> weights = W.getW(); 

		double accuracy = 0; 
		double[] digit = new double[numOfClasses];
		double[] numOfTestExamples = new double[numOfClasses]; 
		Double[] currImage;
		double[][] confusionMatrix= new double[10][10];
		int currLabel;
		//int vectorIndex = 0;
		double[] perceptron = new double[numOfClasses]; 

		for(int i = 0; i < 1000; i++){
			currLabel = Labels.get(i);
			currImage = binImageVec.get(i); 
			numOfTestExamples[currLabel] += 1; //total appearances of a digit

			for(int a = 0; a < numOfClasses; a++){
				perceptron[a] = b; //initialize the perceptrons each training example
			}
			for(int dig = 0; dig < numOfClasses; dig++){
				int vectorIndex = -1; 
				for(int rr = 0; rr < row; rr++){
					for(int cc = 0; cc < col; cc++){

						ArrayList<Double> tuple = new ArrayList<Double>();
						if((rr%r) ==0 && (cc%c) ==0){
							vectorIndex++; 
							for(int x = rr; x < rr+r; x++){
								for(int y = cc; y < cc+c; y++){
									tuple.add((double)currImage[rr*28+cc]);
								}
							}
							int val = binaryToIndex(tuple);
							//vectorIndex = rr*row/r + cc/c;
							//System.out.println(vectorIndex);
							perceptron[dig] += weights.get(dig).getVector()[vectorIndex] * val;
						}
					}
				}
			}

			double max = perceptron[0];
			int predicted = 0;
			for(int a = 1; a < numOfClasses; a++){
				if(perceptron[a]> max){
					max = perceptron[a];
					predicted = a; 
				}
			}
			if(predicted == currLabel){
				digit[predicted]+= 1;
				accuracy += 1; 
			}
			confusionMatrix[currLabel][predicted]+=1; 
		}
		System.out.println("Confusion Matrix Disjoint Groups: ");
		for(int ii = 0; ii < confusionMatrix.length;ii++){
			for(int jj = 0; jj < confusionMatrix[0].length;jj++){
				confusionMatrix[ii][jj] = (100*confusionMatrix[ii][jj]/numOfTestExamples[ii]);
				System.out.format("%10.2f", confusionMatrix[ii][jj]);
				System.out.print("%");
			}
			System.out.println();
		}
		accuracy = accuracy/1000;
		System.out.println("Testing Accuracy: " + accuracy);	
		return accuracy; 	
	}	




	public static double testing (String testlabel, String testfile, ArrayList<Weight> weights) throws FileNotFoundException{
		ArrayList<Integer> Labels = readLabel(testlabel);
		ArrayList<Double[]> binImageVec = binaryImageVec(testfile);
		double[] digit = new double[numOfClasses]; // number of right digit
		double[] numOfTestExamples = new double[numOfClasses]; //tot number of digits in training
		double accuracy = 0; 

		double[][] confusionMatrix = new double[numOfClasses][numOfClasses];

		Double[] currImage = new Double[row*col]; //28 x 28
		int currLabel; 
		int vectorIndex = 0; 
		double[] perceptron = new double[numOfClasses];

		for(int i = 0; i < numTestEx; i++){
			currLabel = Labels.get(i);
			currImage = binImageVec.get(i); 
			numOfTestExamples[currLabel] += 1; //total app of a digit

			for(int a = 0; a < numOfClasses; a++){
				perceptron[a] = 0; 
			}
			for(int dig = 0; dig < numOfClasses; dig++){
				for(int r = 0; r < row; r++){
					for(int c = 0; c < col; c++){
						vectorIndex = r*row + c; 
						perceptron[dig] += weights.get(dig).getVector()[vectorIndex] * currImage[vectorIndex];				
					}
				}
			}
			double max = perceptron[0];
			int predicted = 0;
			for(int a = 1; a < numOfClasses; a++){
				if(perceptron[a]> max){
					max = perceptron[a];
					predicted = a; 
				}
			}
			confusionMatrix[currLabel][predicted]+=1; 

			if(predicted == currLabel){
				digit[predicted]+= 1;
				accuracy += 1; 
			}
		}
		System.out.println("Confusion Matrix single pixels");
		for(int ii = 0; ii < confusionMatrix.length;ii++){
			for(int jj = 0; jj < confusionMatrix[0].length;jj++){
				confusionMatrix[ii][jj] = (100*confusionMatrix[ii][jj]/numOfTestExamples[ii]);
				System.out.format("%10.2f", confusionMatrix[ii][jj]);
				System.out.print("%");
			}
			System.out.println();
		}

		accuracy = accuracy/numTestEx;
		//System.out.println("Test Accuracy: " + accuracy);	

		return accuracy;
	}

	public static double KNN (String trainfile, String trainlabel, String testfile, String testlabel, int k) throws FileNotFoundException{
		//for each test image, cycle through ALL training images and calculate 
		//how to store k highest similarity scores WHILE keeping their label? easy sort by similarity score
		// create ADT that stores score and label. 	
		double accuracy = 0; 
		PriorityQueue<Distance> PQ = new PriorityQueue<Distance>(new Comparator<Object>() {
			// ascending sort
			public int compare(Object o1, Object o2) {
				Distance d1 = (Distance) o1; 
				Distance d2 = (Distance) o2;
				if (d1.getDistance() > d2.getDistance()) return -1;
				else if (d1.getDistance() < d2.getDistance()) return 1;
				return 0;
			}
		});

		ArrayList<Integer> trainlabels = readLabel(trainlabel);
		ArrayList<Double[]> binaryTrainImage = binaryImageVec(trainfile);
		ArrayList<Integer> testlabels = readLabel(testlabel);
		ArrayList<Double[]> binaryTestImage = binaryImageVec(testfile);

		for(int i = 0; i < numTestEx; i++){
			for(int j = 0; j < numTrainingEx; j++){
				double distance = calcDistance(binaryTrainImage.get(j), binaryTestImage.get(i));

				if(PQ.size() < k){ //
					PQ.add(new Distance(distance, trainlabels.get(j)));
				}
				else {
					if(PQ.peek().getDistance() > distance) {
						PQ.poll(); //removes the lowest distance in PQ and replaces with larger dist
						PQ.add(new Distance(distance,trainlabels.get(j)));
					}
				}
			}
			int[] digitCount = new int[numOfClasses];
			int mostFREQ = 0;
			int labelFREQ = 0; 
			for(int a = 0; a < PQ.size(); a++){
				Distance temp = PQ.poll();
				digitCount[temp.getDigit()]++; 
			}

			for(int dig = 0; dig < numOfClasses; dig++){
				if(digitCount[dig] > labelFREQ){
					mostFREQ = dig; 
					labelFREQ = digitCount[dig];
				}
			}
			if(testlabels.get(i) == mostFREQ){
				accuracy++; 
			}

		}
		accuracy = accuracy/numTestEx;

		return accuracy; 
	}

	public static double calcDistance(Double[] Image1, Double[] Image2){
		double distance = 0; 
		for(int i = 0; i < Image1.length; i++){
			distance += Math.pow(Image1[i]-Image2[i], 2); 
		}
		return Math.sqrt(distance);	
	}


	public static ArrayOfWeight trainSoftMax (String trainfile, String trainlabel, ArrayOfWeight W) throws FileNotFoundException{
		ArrayList<Integer> Labels = readLabel(trainlabel);
		ArrayList<Double[]> binImageVec = binaryImageVec(trainfile);
		ArrayList<Weight> weights = W.getW(); 

		double accuracy = 0; 
		double[] digit = new double[numOfClasses]; // number of right digit
		double[] numOfTrainingExamples = new double[numOfClasses]; //tot number of digits in training

		ArrayList<Integer> trainExIndex = new ArrayList<Integer>();
		for(int i =0; i < numTrainingEx; i++){
			trainExIndex.add(i);
		}
		if(randomOrder) {
			Collections.shuffle(trainExIndex);
		}
		Double[] currImage; 
		int currLabel; 
		int vectorIndex = 0; 
		double[] perceptron = new double[numOfClasses];

		for(Integer i: trainExIndex){
			currLabel = Labels.get(i);
			currImage = binImageVec.get(i); 
			numOfTrainingExamples[currLabel] += 1; //total appearances of a digit

			for(int a = 0; a < numOfClasses; a++){
				perceptron[a] = b; //initialize the perceptrons each training example
			}
			for(int dig = 0; dig < numOfClasses; dig++){
				for(int r = 0; r < row; r++){
					for(int c = 0; c < col; c++){
						vectorIndex = r*row + c; 
						perceptron[dig] += weights.get(dig).getVector()[vectorIndex] * currImage[vectorIndex];				
					}
				}
			}
			// implement SOFT MAX STUFF HERE:
			double max = perceptron[0];
			int predicted = 0;
			double[] softMax = perceptron.clone();

			for(int a = 0; a < numOfClasses; a++){
				System.out.println(a + " | "+ perceptron[a]);
				if(perceptron[a]> max){
					max = perceptron[a];
					predicted = a; 
				}
			}

			//System.out.println(predicted+ " | " + currLabel);

			for(int d = 0; d < numOfClasses; d++) {
				//System.out.println(perceptron[d]);
				softMax[d] -= max; //shifting so largest value is 0	
				//System.out.println(perceptron[d]);
			}
			double sum = 0;
			for(int d = 0; d < numOfClasses; d++) sum = sum+ Math.exp(softMax[d]); //summing exp of all shifted values
			for(int d = 0; d < numOfClasses; d++){
				softMax[d] = Math.exp(softMax[d])/sum; // softmax VECTOR
			}

			//			double max2 = -Math.log(perceptron[0]);
			//			//System.out.println("Max 2: " + max2);
			//			for(int a = 1; a < numOfClasses; a++){
			//				//System.out.println("Digit: " + a +" " + -Math.log(perceptron[a]));
			//				if(-Math.log(perceptron[a]) > max2){
			//					max2 = -Math.log(perceptron[a]);	
			//				}
			//			}
			//System.out.println(predicted);

			if(predicted == currLabel){
				digit[predicted]+= 1;
				accuracy += 1; 
			}
			else{
				int index = 0; 

				for(int r = 0; r < row; r++){
					for(int c = 0; c < col; c++){
						index = r*row + c; 
						for(int dig = 0; dig < 10; dig++){
							if(dig == predicted){
								//	System.out.println(softMax[dig]);
								weights.get(dig).getVector()[index] -= alpha*currImage[index]*(-1 + softMax[dig]);
							}
							else{
								System.out.println(softMax[dig]);
								weights.get(dig).getVector()[index] -= alpha*currImage[index]*softMax[dig]; 
							}
						}
					}
				}
				//				if(bias){
				//					weights.get(currLabel).getVector()[index] += alpha * b;
				//					weights.get(predicted).getVector()[index] -= alpha * b;
				//				}
			}
		}

		accuracy = accuracy/5000;
		System.out.println("Training Accuracy: " + accuracy);	
		W.setW(weights);
		W.getTrainingCurve().add(accuracy);
		return W; 	
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

	//

	public static ArrayList<Double[]> binaryImageVec(String fileName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(fileName));
		List<String> lines = new ArrayList<>();
		while(sc.hasNext()) {
			String currLine = sc.nextLine();
			lines.add(currLine);
		}
		sc.close();

		int totRows = lines.size();
		int totCols = lines.get(0).length();

		int r = 0; //current row of a training example
		int labelIndex = -1; 
		int vecIndex = 0; 
		Double[] image = new Double[row*col]; 
		Double[] temp = new Double[row*col];
		ArrayList<Double[]> binImage = new ArrayList<Double[]>();

		for(int i = 0; i < totRows; i++) {
			r = i%28;
			if(r == 0){
				labelIndex = labelIndex + 1;
				temp = image.clone();
			}
			for(int j = 0; j < totCols; j++) {
				vecIndex = r*28 + j; 
				Character currChar = lines.get(i).charAt(j);
				switch (currChar) {
				case ' ': // empty space
					temp[vecIndex] = (double) 0;
					break;
				case '!': //empty space
					temp[vecIndex] = (double) 0;
					break;
				default: // foreground
					temp[vecIndex] = (double) 1;
					break;
				}
				if(r == 27 && j == 27){
					binImage.add(temp);
					vecIndex = 0;
				}
			}
		}	
		return binImage;
	}

	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException{
		epoch = 200; 
		alpha = 1000/(1000+epoch);
		bias = false; 
		randomOrder = false; 
		randomWeights = false;
		row = 28;
		col = 28; 
		numOfClasses = 10; 
		numTrainingEx = 5000;
		numTestEx = 1000; 
		if(bias) b = .5; //-1 + Math.random()*2;
		else b = 0;
	
		ArrayList<Weight> overlapWeight = new ArrayList<Weight>(); 
		ArrayList<Weight> disjointWeight = new ArrayList<Weight>(); 
		ArrayList<Weight> weights = new ArrayList<Weight>(); 
		ArrayList<Double> trainingCurve = new ArrayList<Double>(); 

		ArrayOfWeight W = new ArrayOfWeight(weights, trainingCurve); 
		ArrayOfWeight W2 = new ArrayOfWeight(disjointWeight, trainingCurve); 
		ArrayOfWeight W3 = new ArrayOfWeight(overlapWeight,trainingCurve);

		int r = 2; 
		int c = 2; 

		double[] overlapVector = new double[(row-r+1)*(col-c+1)];
		for(int i = 0; i < (row-r+1)*(col-c+1); i++) overlapVector[i] = -1+Math.random()*2.0;

		double[] disjointVector = new double[(row/r)*(col/c)];
		for(int i = 0; i <row*col/(r*c); i++) disjointVector[i] = 0; 

		double[]emptyVector = new double[row*col+1];
		emptyVector[row*col] = 1; 
		for(int i = 0; i < row*col; i++){
			if(randomWeights){
				emptyVector[i] = (-1 + 2*Math.random())/100;
			}
			else {
				emptyVector[i] = 0; 
			}
		}	

		for(int i =0; i<numOfClasses; i++){
			double[] copy = emptyVector.clone();
			double[] copy2 = disjointVector.clone();
			double[] copy3 = overlapVector.clone();
			weights.add(new Weight(copy,i)); 
			disjointWeight.add(new Weight(copy2,i));
			overlapWeight.add(new Weight(copy3,i));
		}

		int fullAcc = 0;
		for(int i = 0; i < epoch; i++){
			//W = trainSoftMax("trainingimages", "traininglabels",W);
			W = train("trainingimages", "traininglabels", W);
			//W2 = trainDISJOINT("trainingimages", "traininglabels", W2, r, c);
			//W3 = trainOVERLAP("trainingimages", "traininglabels", W3, r,c);
			if (W.getTrainingCurve().get(i) == 1.0) {	
				fullAcc = i+1;
				System.out.println("Epoch: " + fullAcc);
				break;
			}
		}

		PrintWriter writer = new PrintWriter("Training_Curve_300", "UTF-8");
		for(int i = 0; i < fullAcc; i++){
			writer.println(W.getTrainingCurve().get(i));
			//writer.println(W.getTrainingCurve().get(i));
		}
		writer.close();

		//System.out.println(W.getTrainingCurve().get((int)full-1));
		System.out.println("Epoch: " + epoch);
		System.out.println("Alpha: " + alpha);
		System.out.println("b term: " + b);
		if(randomWeights) System.out.println("Random WEIGHTS.");
		else System.out.println("Weights = 0.");
		if(randomOrder) System.out.println("Random Order.");
		else System.out.println("Not Random Order.");

		//double overlapAccuracy = testOVERLAP("testimages", "testlabels", W3, r, c);
		//System.out.println(overlapAccuracy);
		
		//double disjointAccuracy = testingDisjoint("testimages", "testlabels", W2,r,c);
		//System.out.println("Test Accuracy: " + disjointAccuracy);
		
		double accuracy = testing("testlabels", "testimages",weights);
		System.out.println(accuracy);

		//	ArrayList<Double> KNNaccuracyList = new ArrayList<Double>();
		//ArrayList<Long> runTime = new ArrayList<Long>();

		//		int k;
		//		for(k = 1; k < 100; k++){
		//			long startTime = System.currentTimeMillis();
		//			double KNNaccuracy = KNN("trainingimages", "traininglabels", "testimages", "testlabels", k);
		//			long endTime = System.currentTimeMillis();
		//			long totalTime = endTime - startTime; 
		//			runTime.add(totalTime);
		//			KNNaccuracyList.add(KNNaccuracy);

		//			System.out.println(KNNaccuracy);
		//		}
		//		PrintWriter writer2 = new PrintWriter("KNN run times", "UTF-8");
		//		for(int i = 0; i < fullAcc; i++){
		//			writer2.println(runTime.get(i));
		//		}
		//		writer2.close();

	}
}
