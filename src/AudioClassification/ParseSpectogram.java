package AudioClassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.omg.CORBA.portable.ValueInputStream;

public class ParseSpectogram {
	private static int numRows;
	private static int numCols; 

	public ParseSpectogram(){

	}


	public static double argmax (Posterior P, String noTest, String yesTest) throws FileNotFoundException{
		Integer[][] yes_testVal = readImage(yesTest); //test values converted to binary
		Integer[][] no_testVal = readImage(noTest); // test values converted to binary
		int setAccuracy = 0;
		int[] tempNo = MAP(P, no_testVal, 0);
		int[] tempYes = MAP(P, yes_testVal, 1);
		double[][] confusionMatrix = new double[2][2];

		for(int i = 0; i < tempNo.length; i++){
			if(tempNo[i] == 0) {
				setAccuracy++;	
				confusionMatrix[0][0] = confusionMatrix[0][0]+1.0; 
			}
			else {// (tempNo[i] == 1) 
				confusionMatrix[0][1] = confusionMatrix[0][1]+1.0; 
			}
		}

		for(int i = 0; i < tempYes.length; i++){
			if(tempYes[i]==1){
				setAccuracy++; 
				confusionMatrix[1][1] = confusionMatrix[1][1]+1.0; 
				
			}
			else {// (tempYes[i]==0)
				confusionMatrix[1][0] = confusionMatrix[1][0]+1.0; 
			}
		}
		
		for(int r = 0; r < 2; r++){
			for(int c = 0; c < 2; c++){
				confusionMatrix[r][c] = confusionMatrix[r][c]/50.0;
				System.out.print("(" + confusionMatrix[r][c] + ")"); 
			}
		System.out.println();
		}

			return (double)setAccuracy/ (double)(tempNo.length+tempYes.length); 
	}


	//returns the classified array
	//1 yes
	//0 no
	public static int[] MAP (Posterior P, Integer[][] testValues, int CLASS){
		// class is YES = 1 or NO = 0
		double[] priors = P.getPrior(); 
		double[] MAParray = new double[2];
		int[] classified = new int[50]; //50 yes, 50 no
		int imageNum = 0; 

		for(int i=0; i < testValues.length; i++){
			if(i!=0 && i%28 ==0) imageNum += 1; 	
			for(int j = 0; j < testValues[0].length; j++){
				for(int clss = 0; clss < 2; clss++){
					if(25 <= i%28 && i%28 <= 27){
						//do nothing... skip 3 lines
					}
					else{
						if(i%28 == 0 && j == 0){
							MAParray[clss] = Math.log10(priors[clss]);
						}			
						if(testValues[i][j] == 1){
							MAParray[clss]= MAParray[clss] + Math.log10(P.getL1().get(clss).getMatrix()[i%28][j]);
						}
						if(testValues[i][j] == 0){
							MAParray[clss]= MAParray[clss] + Math.log10(1.0-P.getL1().get(clss).getMatrix()[i%28][j]);
						}
					}
				}	

				if(i%28 ==24 && j ==9){
					double max = MAParray[0];
					int maxC = 0;
					if(MAParray[1] > max){
						max = MAParray[1];
						maxC = 1; 
					}	
					classified[imageNum] = maxC; // 1, yes OR 0, no
				}
			}
		}
		return classified; 
	}

	public static Posterior Posterior (String notrainfile, String yestrainfile, double k) throws FileNotFoundException {
		Integer[][] val_no = readImage(notrainfile); 
		Integer[][] val_yes = readImage(yestrainfile); 
		double[] priors = new double[2]; // num of no's/no+yes and yes/ no+yes
		ArrayList<Likelihood> L = new ArrayList<Likelihood>(); // arraylist holding the
		// likelihoods of yes and no's at pixels i, j

		Likelihood L_NO = LikelihoodForClass(val_no);
		Likelihood L_YES = LikelihoodForClass(val_yes);
		L.add(L_NO);
		L.add(L_YES);
		int totalNumOfEx = L.get(0).getNumOfEx() + L.get(1).getNumOfEx();
		for(int clss = 0; clss < 2; clss++){

			priors[clss] = (double)L.get(clss).getNumOfEx()/(double)totalNumOfEx; 
			for(int i = 0; i < 25; i++){
				for(int j = 0; j < 10; j++){
					L.get(clss).getMatrix()[i][j] = (L.get(clss).getMatrix()[i][j]+k)/((double)L.get(clss).getNumOfEx()+k*2.0); 
					//System.out.print("(" + L.get(clss).getMatrix()[i][j] + ")");
				}
				//System.out.println();
			}	
			//System.out.println();
		}	
		Posterior P = new Posterior(L,priors);
		return P;
	}

	/**
	 * @param values -- data converted to 1s and 0s
	 * @return Likelihood for yes or no class
	 * calculates the likelihoods for yes and no training files...
	 */
	public static Likelihood LikelihoodForClass (Integer[][] values){
		int numOfTrainingEx = 0; 
		double[][] emptyMatrix = new double[25][10];

		for (int i =0; i < emptyMatrix.length; i++){
			for (int j = 0; j < emptyMatrix[0].length; j++){
				emptyMatrix[i][j] = 0; 
			}
		}

		for(int r=0; r < values.length; r++){
			if(25 <= r%28 && r%28 <= 27){
				//do nothing... skip 3 lines
			}
			else {
				if(r%28==0) numOfTrainingEx = numOfTrainingEx + 1; 
				for(int c=0; c < values[0].length; c++){
					emptyMatrix[r%28][c] = emptyMatrix[r%28][c] + (double)values[r][c]; 
					//	System.out.print("(" + emptyMatrix[r%28][c] + ")");
				}
				//System.out.println();
			}
		}
		Likelihood L = new Likelihood(emptyMatrix, numOfTrainingEx);

		return L; 
	}

	/**
	 * @param fileName
	 * @return the data converted to 1s and 0s
	 * @throws FileNotFoundException
	 */
	public static Integer[][] readImage(String fileName) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(fileName));
		List<String> lines = new ArrayList<>();
		while(sc.hasNext()) {
			String currLine = sc.nextLine();
			//System.out.println(currLine);
			lines.add(currLine);
		}
		sc.close();

		int totRows = lines.size();
		int totCols = lines.get(0).length();

		Character[][] test = new Character[totRows][totCols];
		Integer[][] value = new Integer[totRows][totCols];

		for(int i = 0; i < totRows; i++) {
			for(int j = 0; j < lines.get(i).length(); j++) {
				if(!lines.get(i).isEmpty()){
					Character currChar = lines.get(i).charAt(j);
					switch (currChar){
					case ' ': // high energy
						test[i][j] = ' ';
						value[i][j] = 1;
						break;
					default: // low energy
						test[i][j] = '%';
						value[i][j] = 0;
						break;
					}
				}
				//System.out.print(value[i][j]);
			}
			//System.out.println();
		}
		//System.out.println(value[27][0]);
		return value;
	}

	private static double[][] deepCopy (double[][] original){
		if(original == null) return null;
		double[][] result = new double[original.length][];
		for(int r =0; r <original.length; r++){
			result[r] = original[r].clone();
		}
		return result;	
	}












}
