package NBClassification;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class NBtester {





	public static void main(String[] args) throws FileNotFoundException{
//
		//for(double k = .1; k < 10; k+= 1){
//			Posterior P = ParseImage.likelihood("trainingimages", "traininglabels", 0.1, 28, 28, 10); // posterior
//			double accuracy = ParseImage.argmax(P, "testimages", "testlabels", 28,28);
//			System.out.println(accuracy + " k value: " + 0.1);
//		//}
		
		
		
		double k = .1;
			int r = 2;
			int c = 2;
			int row = 28;
			int col = 28; 
			int numOfClasses = 10;
			
			long startTime = System.currentTimeMillis();
			
//			
//			PosteriorGroup P0 = ParseImage.DisjointLikelihood("trainingimages","traininglabels", k, r, c, row, col, numOfClasses, 3);
//			double disjointAccuracy = ParseImage.argmaxDisjoint(P0, "testimages", "testlabels", r, c, row, col);
//			System.out.println(disjointAccuracy + " k value:"+k+ " ("+ r + " by "+ c + ")");
			
		
			
			PosteriorGroup P0 = ParseImage.OverlapLikelihood("trainingimages", "traininglabels", k , r, c, row, col, numOfClasses, 3);
			double accuracy = ParseImage.argmaxOverlap(P0, "testimages", "testlabels", r, c, row, col);
			System.out.println(accuracy + " k value: " + k + " (" + r +  " by " + c +")");

		//		for(Integer a : Labels){
		//			System.out.print(a + ",");
		//		}
		//System.out.println(blah.size());
			
			
//			Posterior Pface = ParseImage.likelihood("facedatatrain", "facedatatrainlabels", k, 70, 60, 2);
//			double PfaceAccuracy = ParseImage.argmax(Pface, "facedatatrain", "facedatatrainlabels", 70,60);
//			System.out.println(PfaceAccuracy +" "+ k + " single pixel");
			
//			PosteriorGroup face = ParseImage.DisjointLikelihood("facedatatrain", "facedatatrainlabels", k, r, c, 70, 60, 2);
//			double faceAccuracy = ParseImage.argmaxDisjoint(face, "facedatatest", "facedatatestlabels", r, c, 70, 60);
//			System.out.println(faceAccuracy + " "+ k + " (" + r +  " by " + c +")");
	
//			
//			PosteriorGroup P1 = ParseImage.OverlapLikelihood("facedatatrain", "facedatatrainlabels", k , r, c, 70, 60, 2);
//			double faceAccuracy1 = ParseImage.argmaxOverlap(P1, "facedatatest", "facedatatestlabels", r, c, 70, 60);
//			System.out.println(faceAccuracy1 + " k value: " + k + " (" + r +  " by " + c +")");
			
		//	for(double k = .1; k < 10; k+= 1){
//			PosteriorGroup P0 = ParseImage.ternaryLikelihood("trainingimages","traininglabels", k, row, col, 10, 3);
//			double ternaryAccuracy = ParseImage.argmaxTernary(P0, "testimages", "testlabels", 1, 1, row, col);
//			System.out.println(ternaryAccuracy + " k value: "+k);
			//}
			
			
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(totalTime + " ms");
		}
	
	


}
