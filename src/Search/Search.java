package Search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Search {

	private int numExpandedNodes;
	Cell[][] maze;
	Cell start;
	ArrayList<Cell> goals;

	/**
	 * Constructor
	 * 
	 * @param maze
	 */
	Search(Cell start, ArrayList<Cell> goals, Cell[][] maze) {
		this.maze = maze;
		this.numExpandedNodes = 0;
		this.start = start;
		this.goals = goals;

	}
	/**
	 * Performs depth first search on a maze state.
	 * 
	 * @return the goal state node once found, null if never found 
	 * Can read the path used to find the goal by using node.getParent()
	 */
	public Cell DepthFirstSearch() {
		Stack<Cell> s = new Stack<>();
		s.push(start);
		VisitNode(start);
		
		while (!s.isEmpty()) {
			Cell curr = (Cell) s.peek();
			Cell child = findChildNode(curr);
			if (child != null) {
				VisitNode(child);
				child.setParent(curr);
				if (isGoal(child)) {
					goals.add(child);
					return child;
				}
				s.push(child);
			} else {
				s.pop(); //remove elem at end of stack
				numExpandedNodes++;
			}
		}
		return null;
	}
	
	public Cell BreadthFirstSearch() {
		Queue<Cell> frontier = new LinkedList<Cell>();
		frontier.add(this.start);
		VisitNode(this.start);
		ArrayList<Cell> eaten = new ArrayList<Cell>();
		while (frontier.size()!=0) {
			Cell curr = (Cell) frontier.remove();
			numExpandedNodes++;
			Cell child = null;
			while ((child = findChildNode(curr)) != null) {
				VisitNode(child);
				if (child.getParent() == null) {
					child.setParent(curr);
				}
				if (isGoal(child)) {
					
					if (goals.size() == 1) return child; 
					else
						eaten.add(child);
						if (eaten.size() == goals.size()){
							return eaten.get(eaten.size()-1);
						}
					
				}
				frontier.add(child);
			}
		}
		return null;
	}


	public Cell GreedyBFS() {
		// need to update children of ALL nodes and feed in
		updateChildren();

		ArrayList<Cell> unchecked = new ArrayList<Cell>();
		ArrayList<Cell> checked = new ArrayList<Cell>();

		maze[this.start.getX()][this.start.getY()].setG_n(0);
		maze[this.start.getX()][this.start.getY()].setH_n(manDistance(maze[this.start.getX()][this.start.getY()], maze[this.goals.get(0).getX()][this.goals.get(0).getY()]));

		unchecked.add(maze[this.start.getX()][this.start.getY()]);
		//unchecked.sort(Comparator.comparing(Cell::getH_n)); //sorts by H
		VisitNode(maze[this.start.getX()][this.start.getY()]);

		Cell curr; 
		ArrayList<Cell> children;

		while(!unchecked.isEmpty()){
			curr = unchecked.get(0);

			// checks whether or not curr is the smallest in the unchecked arraylist
			for(int i = 1; i<unchecked.size();i++){
				if(unchecked.get(i).getH_n() < curr.getH_n()) {
					curr = unchecked.get(i);
				}
				else if (unchecked.get(i).getH_n() == curr.getH_n()){
					if(unchecked.get(i).getF_n() < curr.getF_n())
						curr = unchecked.get(i); // makes sure that curr is the lowest h value
				}
			}
			unchecked.remove(curr);
			checked.add(curr);
			numExpandedNodes++;
			if (isGoal(curr)) return curr;
			children = curr.getChildrenCell(); //returns an array list of all the children of curr 

			if(children.size() != 0 && children != null){
				for(Cell c : children){
					if(checked.contains(c)){
					}
					else if (!unchecked.contains(c)){ //if the child is not in the unchecked array list
						c.setG_n(curr.getG_n() + 1); //increase g(n) by 1
						c.setH_n(manDistance(c,goals.get(0))); 
						c.setParent(curr);
						unchecked.add(c);
						VisitNode(c);	
					}
					else if (c.getH_n() > (curr.getH_n() + 1)){ //if the child's manDist is greater than parent + 1, don't want to visit.
						unchecked.remove(c); //remove from the unchecked array list if manD at least 2 greater than parent cell
						c.setG_n(curr.getG_n() + 1);
						c.setH_n(manDistance(c, goals.get(0)));
						c.setParent(curr);
						unchecked.add(c); // add to the end
					}
				}
			}
		}
		return null;
	}

	// use the sum of H_n and G_n
	public Cell Astar(){
		updateChildren();

		ArrayList<Cell> unchecked = new ArrayList<Cell>();
		ArrayList<Cell> checked = new ArrayList<Cell>();

		maze[this.start.getX()][this.start.getY()].setG_n(0);
		maze[this.start.getX()][this.start.getY()].setH_n(manDistance(maze[this.start.getX()][this.start.getY()], maze[this.goals.get(0).getX()][this.goals.get(0).getY()]));

		unchecked.add(maze[this.start.getX()][this.start.getY()]);
		//unchecked.sort(Comparator.comparing(Cell::getH_n)); //sorts by H
		VisitNode(maze[this.start.getX()][this.start.getY()]);

		Cell curr; 
		ArrayList<Cell> children;

		while(!unchecked.isEmpty()){
			curr = unchecked.get(0);

			// checks whether or not curr is the smallest in the unchecked arraylist
			for(int i = 1; i<unchecked.size();i++){
				if(unchecked.get(i).getF_n() < curr.getF_n()) {
					curr = unchecked.get(i);
				}
				else if (unchecked.get(i).getF_n() == curr.getF_n()){
					if(unchecked.get(i).getH_n() < curr.getH_n())
						curr = unchecked.get(i); // makes sure that curr is the lowest h value
				}
			}
			unchecked.remove(curr);
			checked.add(curr);
			numExpandedNodes++;
			if (isGoal(curr)) return curr;
			children = curr.getChildrenCell(); //returns an array list of all the children of curr 

			if(children.size() != 0 && children != null){
				for(Cell c : children){
					if(checked.contains(c)){
					}
					else if (!unchecked.contains(c)){ //if the child is not in the unchecked array list
						c.setG_n(curr.getG_n() + 1); //increase g(n) by 1
						c.setH_n(manDistance(c,goals.get(0))); 
						c.setParent(curr);
						unchecked.add(c);
						VisitNode(c);	
					}
					else if (c.getG_n() > (curr.getG_n() + 1)){ // if child's path dist at least 2 greater than parents, remove and update
						unchecked.remove(c); //remove from the unchecked array list if manD at least 2 greater than parent cell
						c.setG_n(curr.getG_n() + 1);
						c.setH_n(manDistance(c, goals.get(0)));
						c.setParent(curr);
						unchecked.add(c); // adds to the end of the unchecked array list
					}
				}
			}
		}
		return null;
	}

	public void VisitNode(Cell cell) {
		cell.setVisited(true);
		//this.numExpandedNodes++;
	}

	public boolean isNotWall(Cell cell) {
		if(cell.getVal() != '%') {
			return true;
		}else {
			return false;
		}	
	}

	public boolean isGoal(Cell curr) {
		if(curr.getVal() == '.') {
			return true;
		}else {
			return false;
		}
	}

	// finding children for BFS algorithm
	public Cell findChildNode(Cell curr) {
		if(isNotWall(curr)) {
			if(curr.getX() > 0 && maze[curr.getX() - 1][curr.getY()].isVisited() == false ) {
				return maze[curr.getX() - 1][curr.getY()];
			}
			if(curr.getX() < maze.length - 1 && maze[curr.getX() + 1][curr.getY()].isVisited() == false ) {
				return maze[curr.getX() + 1][curr.getY()];
			}
			if(curr.getY() > 0 && maze[curr.getX()][curr.getY() - 1].isVisited() == false ) {
				return maze[curr.getX()][curr.getY() - 1];
			}
			if(curr.getY() < maze[0].length - 1 && maze[curr.getX()][curr.getY() + 1].isVisited() == false ) {
				return maze[curr.getX()][curr.getY() + 1]; 
			}
		}
		return null;
	}
	// updates the children for Greedy and A*
	public void updateChildren (){
		int rows = maze.length;
		int cols = maze[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				for (int ii = i - 1; ii <= i + 1; ii++) {
					for (int jj = j - 1; jj <= j + 1; jj++) {
						if (ii < 0 || jj < 0 || ii >= rows || jj >= cols) {
						}
						else{ // if not out of bounds)
							if ((ii - i) == 0 && (jj-j) == 1 ) {
								if (isNotWall(maze[ii][jj])) {
									maze[i][j].getChildrenCell().add(maze[ii][jj]);
								}
							}
							if ((ii - i) == 0 && (jj-j) == -1 ) {
								if (isNotWall(maze[ii][jj])) {
									maze[i][j].getChildrenCell().add(maze[ii][jj]);
								}
							}
							if ((ii - i) == 1 && (jj-j) == 0) {
								if (isNotWall(maze[ii][jj])) {
									maze[i][j].getChildrenCell().add(maze[ii][jj]);
								}
							}
							if ((ii - i) == -1 && (jj-j) == 0 ) {
								if (isNotWall(maze[ii][jj])) {
									maze[i][j].getChildrenCell().add(maze[ii][jj]);
								}
							}
						}
					}
				}
			}
		}	
	}


	public int manDistance (Cell node, Cell goal){
		int dx = Math.abs(node.getX() - goal.getX()); 
		int dy = Math.abs(node.getY() - goal.getY());
		return (dx + dy);
	}
	public int getNumExpandedNodes() {
		return numExpandedNodes;
	}
	public void setNumExpandedNodes(int numExpandedNodes) {
		this.numExpandedNodes = numExpandedNodes;
	}
	public Cell[][] getMaze() {
		return maze;
	}
	public void setMaze(Cell[][] maze) {
		this.maze = maze;
	}
	public Cell getStart() {
		return start;
	}
	public void setStart(Cell start) {
		this.start = start;
	}
	public ArrayList<Cell> getGoals() {
		return goals;
	}
	public void setGoals(ArrayList<Cell> goals) {
		this.goals = goals;
	}
}
