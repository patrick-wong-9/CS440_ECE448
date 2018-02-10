package Breakthrough;

//BLACK always on top
//WHITE always on bottom
public class State {
	
	public Pawn[][] gameBoard;
	public boolean gameOver;
	public Player currPlayer;
	public Player opponent; 
	public static int rows;
	public static int cols;

	public State(Pawn[][] gameBoard, Player currPlayer, Player opponent){
		this.gameBoard = gameBoard;
		this.gameOver = false; 
		this.currPlayer = currPlayer;
		this.opponent = opponent; 
	}

	public Pawn[][] getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(Pawn[][] gameBoard) {
		this.gameBoard = gameBoard;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public Player getCurrPlayer() {
		return currPlayer;
	}

	public void setCurrPlayer(Player currPlayer) {
		this.currPlayer = currPlayer;
	}

	public Player getOpponent() {
		return opponent;
	}

	public void setOpponent(Player opponent) {
		this.opponent = opponent;
	}

	public static int getRows() {
		return rows;
	}

	public static void setRows(int rows) {
		State.rows = rows;
	}

	public static int getCols() {
		return cols;
	}

	public static void setCols(int cols) {
		State.cols = cols;
	}

}
