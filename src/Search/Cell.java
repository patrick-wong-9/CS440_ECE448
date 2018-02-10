package Search;

import java.util.ArrayList;

//data structure for each location in the array
public class Cell {
	
	private boolean isVisited;
	private Cell parent;
	private Character val;
	private int x;
	private int y;
	private int manDist;
	private float h_n; // heuristic
	private float g_n; // for A*
	private ArrayList<Cell> childrenCell;
    
    public Cell(Character val, int x, int y) {
    	this.isVisited = false;
    	this.val = val;
    	this.parent = null;	
    	this.x = x;
    	this.y = y;
    	this.childrenCell = new ArrayList<Cell>();
    }
    
    public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	
    public void setParent(Cell parent) {
		this.parent = parent;
	}
    
	public Cell getParent() {
		return parent;
	}
	
	public Character getVal() {
		return val;
	}

	public void setVal(Character val) {
		this.val = val;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	} 

	public float getH_n() {
		return h_n;
	}

	public void setH_n(float h_n) {
		this.h_n = h_n;
	}

	public float getG_n() {
		return g_n;
	}

	public void setG_n(float g_n) {
		this.g_n = g_n;
	}

	public float getF_n() {
		return h_n + g_n;
	}

	public void setY(int y) {
		this.y = y;
	}
		
	public int getManDist(){
		return manDist;
	}
	
	public void setManDist(int manDist){
		this.manDist = manDist;
	}

	public ArrayList<Cell> getChildrenCell() {
		return childrenCell;
	}

	public void setChildrenCell(ArrayList<Cell> childrenCell) {
		this.childrenCell = childrenCell;
	}
	

}

