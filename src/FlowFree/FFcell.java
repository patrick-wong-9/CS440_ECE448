package FlowFree;
import java.util.ArrayList;

/**
 * @author Patrick Wong
 * Each position in the 2D array holds a FFcell with the following information
 */
public class FFcell{
	
	private ArrayList<Character> colors;
	private int numOfneigh; 
	private char value;
	private boolean source; 
	private boolean visited;
	private Position loc; 
	private int index;
	private int manD;
	
	public FFcell(char value, Position loc, boolean source, ArrayList<Character> colors){
		this.value = value;
		this.source = source; 
		this.loc = loc; 
		this.colors = colors;
		this.visited= false; 
		//this.colorsRemaining = colors.size(); 
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	public boolean isSource() {
		return source;
	}

	public void setSource(boolean source) {
		this.source = source;
	}

	public Position getLoc() {
		return loc;
	}

	public void setLoc(Position loc) {
		this.loc = loc;
	}

	public ArrayList<Character> getColors() {
		return colors;
	}

	public void setColors(ArrayList<Character> colors) {
		this.colors = colors;
	}

	public int getNumOfneigh() {
		return numOfneigh;
	}

	public void setNumOfneigh(int numOfneigh) {
		this.numOfneigh = numOfneigh;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}



	public int getIndex() {
		return index;
	}



	public void setIndex(int index) {
		this.index = index;
	}



	public int getManD() {
		return manD;
	}



	public void setManD(int manD) {
		this.manD = manD;
	}
}

