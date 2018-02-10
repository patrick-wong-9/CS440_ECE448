package Breakthrough;

public class Pawn {
	public Position curr;
	public Position next; 
	public char c; 
	
	public Pawn(int row, int col, char c, int nextRow, int nextCol){
		this.curr = new Position(row,col);
		this.next = new Position(nextRow,nextCol);
		this.c = c;
	}

	public char getC() {
		return c;
	}

	public void setC(char c) {
		this.c = c;
	}

	public Position getCurr() {
		return curr;
	}

	public void setCurr(Position curr) {
		this.curr = curr;
	}

	public Position getNext() {
		return next;
	}

	public void setNext(Position next) {
		this.next = next;
	}
	

}
