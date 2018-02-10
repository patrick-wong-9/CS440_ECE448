package Breakthrough;

import java.util.ArrayList;

public class Update {
	private ArrayList<Pawn> Pawns;
	private Player currPlayer;
	private Player opponent;
	private boolean goalTest;
	private boolean gameOver;
	
	public Update(ArrayList<Pawn> Pawns, Player currPlayer, Player opponent){
		this.Pawns = Pawns; 
		this.currPlayer = currPlayer;
		this.opponent = opponent; 
		this.goalTest = false;
		this.gameOver = false; 
	}

	public ArrayList<Pawn> getPawns() {
		return Pawns;
	}

	public void setPawns(ArrayList<Pawn> pawns) {
		Pawns = pawns;
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

	public boolean isGoalTest() {
		return goalTest;
	}

	public void setGoalTest(boolean goalTest) {
		this.goalTest = goalTest;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	
}
