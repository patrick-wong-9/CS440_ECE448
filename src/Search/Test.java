package Search;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		try {

			Cell[][] maze = ParseMaze.readFile("bigMaze.txt");
			int n = maze.length;
			int m = maze[0].length;
			for(int i = 0; i < n ; i++) {
				for(int j = 0; j < m; j++) {
					System.out.print(maze[i][j].getVal() + "");
				}
				System.out.print("\n");
			}


			Cell start = maze[ParseMaze.start.getX()][ParseMaze.start.getY()];
			Cell goal = maze[ParseMaze.getGoals().get(0).getX()][ParseMaze.getGoals().get(0).getY()];    
			Search Search = new Search(start, ParseMaze.getGoals(), maze);
			
			Cell test = Search.Astar();


			System.out.println(test.getX()+ "###" + test.getY());
			int x = Search.getNumExpandedNodes();
			System.out.println(x + "exp");

			Draw.drawSolution(maze, goal);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}