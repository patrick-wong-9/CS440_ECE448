package FlowFree;

public class Position {
	private int row;
	private int col; 
	private boolean selected;
	
	public Position(int row, int col){
		this.row = row;
		this.col = col; 
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	

}
