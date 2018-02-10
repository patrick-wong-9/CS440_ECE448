package FlowFree;

import java.util.ArrayList;

public class State {
	
	private FFcell[][] assignment; 
	private ArrayList<Position> variables; // positions on the board left
	private ArrayList<Character> domain; //colors remaining
	private ArrayList<Position> sources; //same for all instances of State hence static
	private int rows;
	private int cols; 
	
	public State (FFcell[][] assignment, ArrayList<Position> variables, ArrayList<Position> sources, ArrayList<Character> domain, int rows, int cols){
		this.assignment = assignment; 
		this.variables = variables;
		this.domain = domain;
		this.rows= rows;
		this.cols =cols; 
		this.sources = sources;
	}

	public FFcell[][] getAssignment() {
		return assignment;
	}

	public void setAssignment(FFcell[][] assignment) {
		this.assignment = assignment;
	}

	public ArrayList<Position> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<Position> variables) {
		this.variables = variables;
	}

	public ArrayList<Character> getDomain() {
		return domain;
	}

	public void setDomain(ArrayList<Character> domain) {
		this.domain = domain;
	}

	public ArrayList<Position> getSources() {
		return sources;
	}

	public void setSources(ArrayList<Position> sources) {
		this.sources = sources;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}
	
	

}
