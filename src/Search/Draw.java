package Search;
import java.util.HashSet;
import java.util.Set;

public class Draw {
    
	public static void drawSolution(Cell[][] maze, Cell goal) {
		Set<Cell> path = new HashSet<>();
		Cell curr = goal;
		while(!isStart(curr)) {
			path.add(curr);
			curr = curr.getParent();
		}
		System.out.println(path.size());
		int numRows = maze.length;
		int numCols = maze[0].length;
		for(int i = 0; i < numRows; i++) {
 		   for(int j = 0; j < numCols; j++) {
 			   if(path.contains(maze[i][j])) {
 				   System.out.print('.');
 			   }else {
 				   System.out.print(maze[i][j].getVal()); 
 			   }
 		   }
 		  System.out.print('\n');  
 		}
	}
	
	private static boolean isStart(Cell cell) {
		if(cell.getVal() == 'P') {
			return true;
		}else{
			return false;
		}
	}
}
