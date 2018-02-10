package FlowFree;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;


import java.awt.Color;
import java.awt.Graphics;


public class FFBacktracking extends JPanel{
	private State state; 
	private FFcell[][] board; 
	private int rows;
	private int cols; 
	private ArrayList<Character> allColors;
	private int assignments; 
	private ArrayList<Position> sources;

	public FFBacktracking(State state){
		this.state = state;
		this.board = state.getAssignment();
		this.rows = state.getRows();
		this.cols = state.getCols(); 
		this.allColors = state.getDomain();
		this.assignments = 0; 
		this.sources = state.getSources();
	}

	public FFcell[][] DUMB(int index, ArrayList<Position> randomOrder){
		if(isComplete(board)){
			return board; 
		}
		FFcell var = new FFcell('_', state.getVariables().get(index), false, allColors);
		for(Character c: var.getColors()){
			FFcell newColor = new FFcell(c, var.getLoc(), false, var.getColors());
			if(cellConstraint(newColor)){
				assignments += 1; 
				board[var.getLoc().getRow()][var.getLoc().getCol()] = newColor; 
				//DrawBoard(board);
				FFcell[][] result = DUMB(index+1, randomOrder); 
				if(result != null) return result;	
			}
			board[var.getLoc().getRow()][var.getLoc().getCol()] = var; 
		}
		return null; 
	}


	public FFcell[][] SMART(int index, ArrayList<Position> variables){
		if(isComplete(board)){
			return board; 
		}
//		if(assignments == 0){
//			variables = initialLegalValues(variables);
//		}
		FFcell var = new FFcell('_', variables.get(index), false, allColors);
		for(Character c: var.getColors()){
			FFcell newColor = new FFcell(c, var.getLoc(), false, var.getColors());
			if(cellConstraint(newColor)){
				assignments += 1; 
				board[var.getLoc().getRow()][var.getLoc().getCol()] = newColor; 
				//DrawBoard(board);
				FFcell[][] result = SMART(index+1, variables); 
				if(result != null) return result;	
			}
			board[var.getLoc().getRow()][var.getLoc().getCol()] = var; 
		}

		return null; 

	}

	public FFcell[][] SMARTER(){
		if(isComplete(board)){
			return board; 
		}
		FFcell var = MCV_MCA(); 
		if(var == null) return null;
		for(Character c: var.getColors()){
			FFcell newColor = new FFcell(c, var.getLoc(), false, var.getColors());
			if(cellConstraint(newColor)){
				assignments += 1; 
				board[var.getLoc().getRow()][var.getLoc().getCol()] = newColor; 
				FFcell[][] result = SMARTER(); 
				if(result != null) return result;	
			}
			//	var = new FFcell('_', var.getLoc(), false, var.getColors());
			board[var.getLoc().getRow()][var.getLoc().getCol()] = var; 
		}
		return null; 
	}

	/**
	 * @return and empty cell from the board
	 */
	public FFcell getUnassignedVar(){
		for(int i = 0 ; i < rows; i++){
			for(int j = 0; j < cols; j++){
				if(!isFilled(board[i][j])){
					return(board[i][j]);
				}
			}
		}
		return null;			
	}
	
	private static ArrayList<FFcell> posToFFcell (ArrayList<Position> vars, FFcell[][] board){
		ArrayList<FFcell> FFcellVars = new ArrayList<FFcell>();
		for(Position p: vars){
			FFcellVars.add(board[p.getRow()][p.getCol()]);
		}
		return FFcellVars;
	}
	
	/** looks at all the sources and neighbors. if a source only has one empty neighbor, that neighbor
	 * must be the same color as the source. 
	 * @param board
	 * @return
	 */
	public ArrayList<FFcell> initialLegalValues(ArrayList<FFcell> variables){
		for(Position p: sources){ //looking at all the sources
			int x = p.getRow();
			int y = p.getCol();
			ArrayList<FFcell> neighOfSource = new ArrayList<FFcell>();
			neighOfSource = getChildren(board[x][y]);
			FFcell emptyCell = null; 
			int empty = 0;

			for(FFcell n: neighOfSource){ //looking at neighbors of a source cell
				if(!isFilled(n)){ //looks at empty neighbors of a source cell
					ArrayList<FFcell> neighOfNeigh = getChildren(n);
					ArrayList<Character> tempColors = new ArrayList<Character>();
					int unassigned = 0;
					for(FFcell n2: neighOfNeigh){
						if(n2.getColors().size() < allColors.size()){ 
							for(Character c: n2.getColors()){
								if (!tempColors.contains(c)) tempColors.add(c);//if source
							}			
						}
						else unassigned++; 
					}
					if(unassigned == 1) board[n.getLoc().getRow()][n.getLoc().getCol()].setColors(tempColors);
					emptyCell = n; //saves the position of the source's empty neighbor
					empty++;					
				}
			}
			if (empty == 1){ // updates the legal value for the one empty neighbor
				ArrayList<Character> temp = new ArrayList<Character>(); 
				temp.add(board[x][y].getValue()); //add color of source
				board[emptyCell.getLoc().getRow()][emptyCell.getLoc().getCol()].setColors(temp);
			}		
		} 
		ArrayList<FFcell> updatedVars = new ArrayList<FFcell>();
		for(FFcell v: variables){
			updatedVars.add(board[v.getLoc().getRow()][v.getLoc().getCol()]);
			//System.out.println(v.getLoc().getRow() + "##" + v.getLoc().getCol()+v.getColors());
		}
		return updatedVars; 
	}

	/**
	 * @return best FFcell based on MCV and MCA for SMARTEST
	 */
	public FFcell MCV_MCA (){
		int numOfColors = 20;
		FFcell best = null;
		for(int i = 0 ; i < rows; i++){
			for(int j = 0; j < cols; j++){
				if(!isFilled(board[i][j])){
					ArrayList<Character> color = new ArrayList<Character>();
					for(Character c: allColors){
						FFcell temp = new FFcell(c,new Position(i,j), false, null);
						if(cellConstraint(temp)){
							color.add(c);
						}
					}
					board[i][j].setColors(color);
					if(board[i][j].getColors().size() == 0) return null;
					if(board[i][j].getColors().size() < numOfColors){
						numOfColors = board[i][j].getColors().size(); 
						best = board[i][j];
					}
					if(board[i][j].getColors().size() == numOfColors){
						if(emptyNeighbors(board[i][j]) < emptyNeighbors(best)){
							best = board[i][j]; 
							numOfColors = board[i][j].getColors().size(); 
						}
					}
				}
			}
		}
		return best; 
	}

	/**
	 * @param cell
	 * @return neighbors of a given cell
	 */
	public int emptyNeighbors(FFcell cell){
		ArrayList<FFcell> neigh = getChildren(cell);
		int empty = 0;
		for(FFcell n : neigh){
			if(!isFilled(n)) empty++;
		}
		return empty; 
	}

	/**generates the neighbors for a given FFcell
	 * @param curr
	 * @return arrayList of FFcell neighbors
	 */
	private ArrayList<FFcell> getChildren(FFcell curr){
		ArrayList<FFcell> neigh = new ArrayList<FFcell>();
		int x = curr.getLoc().getRow();
		int y = curr.getLoc().getCol();
		for (int ii = x - 1; ii <= x + 1; ii++) {
			for (int jj = y - 1; jj <= y + 1; jj++) {
				if (ii < 0 || jj < 0 || ii >= rows || jj >= cols) {
				}	//do nothing, out of bounds of 2D FFcell matrix
				else{ // if not out of bounds)
					if(Math.abs((ii-x) + (jj-y)) == 1){
						neigh.add(board[ii][jj]);
					}
				}
			}
		}
		return neigh; 
	}

	/**
	 * calls upon a helper method to check current cell's neighbor's constraints
	 * @param curr - check constraint for current cell 
	 * @return
	 */
	private boolean cellConstraint(FFcell curr){
		board[curr.getLoc().getRow()][curr.getLoc().getCol()] = curr; 
		ArrayList<FFcell> neigh = getChildren(board[curr.getLoc().getRow()][curr.getLoc().getCol()]);
		int same = 0;
		int filled = 0;
		for (FFcell n: neigh){
			if(isFilled(n) && !n.isSource()){
				boolean test = neighborConstraints(n, board);
				//if(n.getLoc().getRow() == 1 && n.getLoc().getCol()==0) System.out.println(test);
				//System.out.print("("+ n.getLoc().getRow() + "," + n.getLoc().getCol()+ ")"+test);
				if(test==false) {
					board[curr.getLoc().getRow()][curr.getLoc().getCol()] = new FFcell('_', curr.getLoc(), false, allColors);
					return false;
				}
			}
			if (n.isSource()){				
				boolean result = sourceConstraint(n);
				//System.out.println("("+ n.getLoc().getRow() + "," + n.getLoc().getCol()+ ")"+ result);
				if (result == false){
					board[curr.getLoc().getRow()][curr.getLoc().getCol()] = new FFcell('_', curr.getLoc(), false, allColors);
					return false; 
				}
			}
			if(isFilled(n)) filled++;
			if(sameColor(curr,n)) same++; 
		}
		if(same >2 || (neigh.size() - filled) == 1 && same == 0 || (neigh.size()-filled)==0 && same <= 1){
			board[curr.getLoc().getRow()][curr.getLoc().getCol()] = new FFcell('_', curr.getLoc(), false, allColors);
			return false;		
		}
		board[curr.getLoc().getRow()][curr.getLoc().getCol()] = new FFcell('_', curr.getLoc(), false, allColors);
		return true;
	}

	/**
	 * @param test
	 * @param currState
	 * @return if a source cell passes the constraint (while filling up)
	 */
	private boolean sourceConstraint(FFcell test){
		ArrayList<FFcell> neigh = new ArrayList<FFcell>();
		neigh = getChildren(test);
		int filled = 0;
		int same = 0;
		//counts how many neighbors are filled and counts #repeated colors around source

		for (FFcell n:neigh){
			if(isFilled(n)) filled++; 
			if(sameColor(test, n)) same++;
		}
		if (same > 1 || same == 0 && filled == neigh.size() ) return false; //if more than 1color
		//OR none are the same and all neighbors are filled, return false
		else return true;
	}

	/** helper function for cell constraints. check's a curr cell's neighbor's constraints
	 * @param n
	 * @param currState
	 * @return
	 */
	private boolean neighborConstraints(FFcell n, FFcell[][] currState){
		//currState[n.getLoc().getRow()][n.getLoc().getCol()] = n; 
		ArrayList<FFcell> neigh = getChildren(board[n.getLoc().getRow()][n.getLoc().getCol()]);
		int same = 0;
		int filled = 0; 

		for (FFcell n2: neigh){
			if(isFilled(n2)) filled++;
			if(sameColor(n2,n)) same++; 
		}
		if(n.isSource()){
			if (same > 1 || same == 0 && filled == neigh.size() ) return false;
			else return true; 
		}
		else {
			if(same >2 || (neigh.size() - filled) == 1 && same == 0 || (neigh.size()-filled)==0 && same <= 1) return false;
			else return true; 
		} 
	}

	private boolean isFilled(FFcell curr){
		return (curr.getValue() != '_');
	}

	private boolean sameColor (FFcell sourceCell, FFcell neigh){
		Character color1 = sourceCell.getValue();
		Character color2 = neigh.getValue();
		return (color1==color2);
	}

	/** for non source cells when the cell has all neighbors filled
	 * @param curr
	 * @param currState
	 * @return if the non source cell has 2 same colored neighbors
	 */
	private boolean cellCheck(FFcell curr, FFcell[][] currState){
		currState[curr.getLoc().getRow()][curr.getLoc().getCol()] = curr; 
		ArrayList<FFcell> neigh = getChildren(currState[curr.getLoc().getRow()][curr.getLoc().getCol()]);
		int same = 0;
		for (FFcell n: neigh){
			if(sameColor(curr,n)) same++; 
		}
		if(same ==2) return true;		
		else return false;
	}

	/** this source check is for when the the current cell has all neighbors filled
	 * @param curr
	 * @param currState
	 * @return if the source cell has 1 same colored neighbor
	 */
	private boolean sourceCheck(FFcell curr, FFcell[][] currState){
		ArrayList<FFcell> neigh = new ArrayList<FFcell>();
		neigh = getChildren(curr);
		int same = 0;
		for (FFcell n:neigh){ 
			if(sameColor(curr, n)) same++;
		}
		if (same == 1) return true; 
		else return false;
	}

	/** 
	 * @param curr
	 * @param currState
	 * @return whether or not the current cell has completely filled neighbors or not
	 */
	private boolean isFull(FFcell curr){
		int filled = 0;	
		ArrayList<FFcell> neighbors = getChildren(curr);
		for(FFcell n: neighbors){
			if(isFilled(n)) filled++;
		}		
		return(filled == neighbors.size());	//returns full or not;
	}

	/**
	 * @param assignment
	 * @return whether or not the board is complete (non source cells have 2 neighbors of 
	 * the same color, source cells have only 1 neighbor of the same color. Then checks to see if the 
	 * board is full, if it is, return true. 
	 */
	private boolean isComplete (FFcell[][] assignment){
		int fullCount = 0; 
		int boardSize = assignment.length*assignment[0].length;		

		for(int i = 0; i<rows; i++){
			for(int j=0; j<cols; j++){
				FFcell curr = assignment[i][j];
				boolean early = isFull(curr);
				if(early == false) return false; //there is a cell that has empty neighbors

				if(isFull(curr)){
					fullCount = fullCount +1; 
					if(curr.isSource()){
						boolean result = sourceCheck(curr, assignment);
						if (result == false) return false;
					}
					if(!curr.isSource()){
						boolean result = cellCheck(curr, assignment);
						if (result == false) return false;
					}
				}
			}
		}
		if(fullCount == boardSize) return true;
		else return false;
	}

	private void DrawBoard(FFcell[][] draw) {
		System.out.println();
		int numRows = draw.length;
		int numCols = draw[0].length;
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				if (!draw[i][j].isSource()){
					Character c = draw[i][j].getValue();
					c = Character.toLowerCase(c);
					System.out.print(c);
				}
				else System.out.print(draw[i][j].getValue()); 
			}	
			System.out.print("\n");
		}
		System.out.println("****************");
	}
	
	public Color charToColor(Character c){
		Color color = null;
		if (c == '_') color = Color.BLACK;
		else if(c == 'G') color = Color.GREEN;
		else if (c == 'B') color = Color.BLUE; 
		else if (c == 'R') color = Color.RED;
		else if (c == 'O') color = Color.ORANGE;
		else if (c == 'Y') color = Color.YELLOW; 
		else if (c == 'P') color = Color.PINK;
		else if (c == 'Q') color = Color.MAGENTA;
		else if (c == 'D') color = new Color(0,100,0); //DARK GREEN
		else if (c == 'K') color = new Color(128, 0, 128); //PURPLE
		else if (c == 'T') color = new Color(0,128,128); //TEAL
		else if (c == 'W') color = Color.WHITE;
		else if (c == 'A') color = Color.CYAN; // AQUA
		else if (c == 'F') color = new Color(178,34,34);//FIREBRICK RED
		else if (c == 'H') color = new Color(0, 255, 0); //LIME GREEN
		else if (c == 'S') color = new Color(139,69,19); //SADDLE BROWN
		else if (c == 'V') color = new Color(238,130,238); //VIOLET
		else if (c == 'N') color = new Color(0,0,128); //NAVY
		return color;
	}

	public void paint (Graphics g){
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				g.setColor(Color.WHITE);
				g.fillRect(50*j, 50*i, 50, 50);
				g.setColor(Color.BLACK);
				g.fillRect(50*j+1, 50*i+1, 48, 48); // sets black border		
				g.setColor(charToColor(board[i][j].getValue()));
				g.fillOval(50*j+8, 50*i+8, 34, 34);
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException{
		Scanner sc = new Scanner(System.in);
		System.out.println("Which Solver? DUMB, SMART, or SMARTER? \nEnter 1, 2, or 3 respectively: ");
		int solver = sc.nextInt();
		System.out.println("Enter the board text file using the following key:\n 1 = 5x5, \n 2 = 7x7, \n 3 = 8x8, \n 4 = 9x9, \n 5 = 10x10_1, \n 6 = 10x10_2, \n 7 = 12x12, \n 8 = 12x14, \n 9 = 14x14 \n");
		int txtFile = sc.nextInt();
		String file = "";
		if(txtFile == 1) file = "input55.txt";
		if(txtFile == 2) file = "input77.txt";
		if(txtFile == 3) file = "input88.txt";
		if(txtFile == 4) file = "input991.txt";
		if(txtFile == 5) file = "input10101.txt";
		if(txtFile == 6) file = "input10102.txt";
		if(txtFile == 7) file = "input1212.txt";
		if(txtFile == 8) file = "input1214.txt";
		if(txtFile == 9) file = "input1414.txt";
		sc.close();
		try{
			State state = ImportInput.readFile(file);	
			FFBacktracking complete = new FFBacktracking(state); 
			FFcell[][] result = null;	

			long startTime = System.currentTimeMillis();
			if(solver == 1) {
				ArrayList<Position> vars = complete.state.getVariables();
				Collections.shuffle(vars);
				result = complete.DUMB(0, vars); 
			}
			else if(solver == 2){
			//	ArrayList<FFcell> variables = posToFFcell(complete.state.getVariables(), complete.board);
				result = complete.SMART(0, complete.state.getVariables());
			}
			else if(solver == 3) result = complete.SMARTER();		
			else result = complete.board;
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Completed Board: ");
			complete.DrawBoard(result);
			System.out.println("Run Time: "+ totalTime + " ms");
			System.out.println("Assignments: " + complete.assignments);		
			JFrame frame = new JFrame();
			frame.setSize(800, 800);
			frame.getContentPane().add(complete);
			frame.setLocationRelativeTo(null);
			frame.setBackground(Color.BLACK);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}

