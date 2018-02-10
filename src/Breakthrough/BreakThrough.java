package Breakthrough;

import java.util.ArrayList;
import java.util.Scanner;

public class BreakThrough {
	private static int rows;
	private static int cols;
	public static State state; //initial state
	public static int inf = 999999999; 
	public static int moves; 
	public static Scanner scanner;

	public static int whiteWins;
	public static int blackWins;
	public static double whiteNodesExpanded;
	public static double blackNodesExpanded;
	public static double whiteCaptures;
	public static double blackCaptures; 
	public static double totalMoves;
	public static double time; 


	public BreakThrough(){
	}

	/**
	 * @param state
	 * @param currPlayer
	 * @return Update ADT with pawn information and array of pawns for current player
	 */
	public static Update locPawns(State state, Player currPlayer){
		ArrayList<Pawn> arrayOfPawns = new ArrayList<Pawn>();
		state.currPlayer.distance = rows-1; 
		state.opponent.distance = rows-1;
		state.currPlayer.Pawns = 0; 
		state.opponent.Pawns = 0; 
		state.currPlayer.GoalPawns = 0;
		state.opponent.GoalPawns = 0;
		state.currPlayer.AtkPawns = 0;
		state.opponent.AtkPawns = 0; 
		state.currPlayer.Walls = 0;
		state.opponent.Walls = 0; 

		for(int i = 0; i < rows; i++){
			for (int j = 0; j < cols; j++){
				if(state.gameBoard[i][j].c == state.currPlayer.color){
					if(state.currPlayer.color == 'W' && i < state.currPlayer.distance){
						state.currPlayer.distance = i;
					}
					if(state.currPlayer.color == 'B'&& rows-1-i < state.currPlayer.distance){
						state.currPlayer.distance = rows-1-i;
					}
					if(state.gameBoard[i][j].c == 'W' && i == 0){
						state.currPlayer.GoalPawns += 1; 
					}
					if(state.gameBoard[i][j].c == 'B' && i == rows - 1){
						state.currPlayer.GoalPawns += 1; 
					}
					if(rows == 8){
						if(state.currPlayer.color =='W' && i < 5){
							state.currPlayer.AtkPawns += 1; 	
							if( (j+1) < rows){
								if(state.gameBoard[i][j+1].c == 'W') state.currPlayer.Walls += 1;
							}
						}
						if(state.currPlayer.color == 'B' && i > 2){
							state.currPlayer.AtkPawns += 1; 
							if( (j+1) < rows){
								if(state.gameBoard[i][j+1].c == 'B') state.currPlayer.Walls += 1;
							}
						}
					}
					if(rows == 5){ // no walls for 5x10 board
						if(state.currPlayer.color == 'W' && i < 3) state.currPlayer.AtkPawns += 1;
						if(state.currPlayer.color == 'B' && i > 1) state.currPlayer.AtkPawns += 1; 
					}
					state.currPlayer.Pawns += 1; 
					arrayOfPawns.add(state.gameBoard[i][j]);
				}	
				if(state.gameBoard[i][j].c == state.opponent.color){
					if(state.opponent.color == 'W' && i < state.opponent.distance){	
						state.opponent.distance = i;
					}
					if(state.opponent.color == 'B'&& rows-1-i < state.opponent.distance){
						state.opponent.distance = rows-1-i;
					}
					if(state.gameBoard[i][j].c == 'W' && i == 0){
						state.opponent.GoalPawns += 1;
					}
					if(state.gameBoard[i][j].c == 'B' && i == rows-1){
						state.opponent.GoalPawns += 1;
					}	
					if(rows == 8){
						if(state.opponent.color =='W' && i < 5){
							state.opponent.AtkPawns += 1; 	
							if( (j+1) < rows){
								if(state.gameBoard[i][j+1].c == 'W') state.opponent.Walls += 1;
							}
						}
						if(state.opponent.color == 'B' && i > 2){
							state.opponent.AtkPawns += 1; 	
							if( (j+1) < rows){
								if(state.gameBoard[i][j+1].c == 'B') state.opponent.Walls += 1;
							}
						}
					}
					if(rows == 5){
						if(state.opponent.color == 'W' && i < 3) state.opponent.AtkPawns += 1;
						if(state.opponent.color == 'B' && i > 1) state.opponent.AtkPawns += 1; 
					}
					state.opponent.Pawns += 1;
				}
			}
		}	
		Update update = new Update(arrayOfPawns, state.currPlayer, state.opponent);
		// change to >2 for the 3 pawn to goal scenario
		if(state.currPlayer.GoalPawns > 0 || state.opponent.GoalPawns > 0|| state.opponent.Pawns <= 2 || state.currPlayer.Pawns <= 2) {
			update.setGameOver(true);
			update.setGoalTest(true);
		}
		return update; 
	}

	public static Update getMoves(State state){
		ArrayList<Pawn> nextMoves = new ArrayList<Pawn>(); 
		Update update = locPawns(state, state.currPlayer);
		ArrayList<Pawn> arrayOfPawns = update.getPawns();

		int dir = state.currPlayer.dir; 

		for(Pawn p: arrayOfPawns){
			int newRow =(p.getCurr().getR() + dir);
			if (newRow < rows && newRow >= 0){
				//LEFT
				int left = (p.getCurr().getC()+ dir); //column of left move
				if(left >= 0 && left < cols){
					if(state.gameBoard[newRow][left].getC() != state.currPlayer.color){
						nextMoves.add(new Pawn(p.getCurr().getR(), p.getCurr().getC(), state.currPlayer.color, newRow,left));
					}
				}
				//STRAIGHT
				int straight = p.getCurr().getC(); 
				if(state.gameBoard[newRow][straight].getC()=='~'||state.gameBoard[newRow][straight].getC() == '1'||state.gameBoard[newRow][straight].getC() == '2'){
					nextMoves.add(new Pawn(p.getCurr().getR(), p.getCurr().getC(), state.currPlayer.color, newRow, straight));
				}
				//RIGHT
				int right = (p.getCurr().getC() - dir);//column of right move
				if(right>= 0 && right < cols){
					if(state.gameBoard[newRow][right].getC() != state.currPlayer.color){
						nextMoves.add(new Pawn(p.getCurr().getR(), p.getCurr().getC(), state.currPlayer.color, newRow,right));
					}
				}		
			}
		}
		ArrayList<Pawn> moveOrdering = new ArrayList<Pawn>(); 
		for(Pawn P: nextMoves){
			if(P.next.getR() == 0|| P.next.getR() == rows-1) moveOrdering.add(0, P); // goal moves
			else if(state.gameBoard[P.next.getR()][P.next.getC()].c == state.opponent.color) moveOrdering.add(0,P);//captures
			else moveOrdering.add(P); // any other move
		}
		update.setPawns(moveOrdering);
		return update; 
	}


	/**
	 * @param CURRstate - takes in the current state 
	 * @param nextMove - moves piece to next position
	 * @return state after move - if pawn reaches end of board, convert to * and allow other pawns to move to same spot
	 */
	public static State move(State CURRstate, Pawn nextMove){
		Pawn[][] copyBoard = deepCopy(CURRstate.gameBoard); //copy of old board prior to next move
		copyBoard[nextMove.getCurr().getR()][nextMove.getCurr().getC()].c = '~';
		copyBoard[nextMove.getNext().getR()][nextMove.getNext().getC()].c= (CURRstate.currPlayer.color);
		State nextState = new State(copyBoard, CURRstate.opponent, CURRstate.currPlayer);
		Update update = locPawns(nextState, CURRstate.opponent); 
		nextState.currPlayer = update.getCurrPlayer();
		nextState.opponent = update.getOpponent(); 
		nextState.gameOver = update.isGameOver();  
		return nextState; 
	}	

	//////////////////////////** MINIMAX START **\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

	/**
	 * @param state
	 * @param depth of search tree
	 * @return best move according to minmax algorithm
	 */
	public static Pawn MinimaxMove(State state, int depth){
		moves = 0;
		double maxValue = -inf;
		Pawn bestMove= null;
		Update update = getMoves(state);
		ArrayList<Pawn> arrayOfMoves = update.getPawns();
		for(Pawn m: arrayOfMoves){
			moves+=1; 
			double v = minValue(move(state,m),depth, state.currPlayer.Heuristic);
			if (v > maxValue){
				maxValue = v;
				bestMove = m; 
			}
		}
		state.currPlayer.NodesExp += moves;
		return bestMove;
	} 

	public static double maxValue(State state, int depth, String Heuristic){
		depth = depth - 1; 
		Update update = getMoves(state);
		if(update.isGoalTest()){	
			return -(100000 + (depth*100));	
		}
		if (depth == 0){
			return Strategy(Heuristic, state.currPlayer, state.opponent);
		}
		double v = -inf;
		for(Pawn m : update.getPawns()){
			moves+=1; 
			v = max(v, minValue(move(state,m), depth, Heuristic));
		}
		return v;
	}

	public static double minValue(State state, int depth, String Heuristic){
		depth = depth - 1; 
		Update update = getMoves(state);
		if(update.isGoalTest()){
			return 100000 + (depth*100); //want to value states that are closer to the goal
		}
		if (depth == 0){
			return Strategy(Heuristic, state.opponent, state.currPlayer);
		}
		double v = inf;
		for(Pawn m : getMoves(state).getPawns()){
			moves +=1;
			v = min(v, maxValue(move(state,m), depth, Heuristic));
		}
		return v; 
	}
	/////////////////////////////////////////////////////////////////////////////////
	///////////// ALPHA BETA SEARCH \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\	

	/**
	 * @param state - current state
	 * @param depth of search tree
	 * @return best move according to AlphaBeta Algorithm
	 */
	public static Pawn AlphaBetaMove(State state, int depth){
		moves = 0;
		double maxValue = -inf;
		double a = -inf;
		double b = inf;
		Pawn bestMove= null;
		for(Pawn m: getMoves(state).getPawns()){
			moves+=1; 
			double v = minValueAB(move(state,m), depth, state.currPlayer.Heuristic, a, b);
			if (v > maxValue){
				maxValue = v;
				bestMove = m; 
			}
			a = max(a,v);
		}
		state.currPlayer.NodesExp += moves;
		return bestMove;

	}
	public static double maxValueAB(State state, int depth, String Heuristic, double a, double b){
		depth = depth - 1; 
		Update update = getMoves(state);
		if(update.isGoalTest()){	
			return -(100000 + (depth*100));	
		}
		if (depth == 0){
			return Strategy(Heuristic, state.currPlayer, state.opponent);
		}
		double v = -inf;
		for(Pawn m : update.getPawns()){
			moves+=1; 
			v = max(v, minValueAB(move(state,m), depth, Heuristic, a, b));
			if (v >= b) return v;
			a = max(a,v);
		}
		return v;
	}

	public static double minValueAB(State state, int depth, String Heuristic, double a, double b){
		depth = depth - 1; 
		Update update = getMoves(state);
		if(update.isGoalTest()){
			return 100000 + (depth*100); //want to value states that are closer to the goal
		}
		if (depth == 0) return Strategy(Heuristic, state.opponent, state.currPlayer);
		double v = inf;
		for(Pawn m : update.getPawns()){
			moves +=1;
			v = min(v, maxValueAB(move(state,m), depth, Heuristic, a, b));
			if(v <= a) return v;
			b = min(b,v);
		}
		return v; 
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param state
	 * @param heuristic
	 * @return best move using GREEDY HEURISTIC
	 */
	public static Pawn GreedyMove (State state, String heuristic){
		moves = 0;
		double maxValue = -inf;
		double v;
		Pawn bestMove= null;
		Update update = getMoves(state);
		State newState;
		for(Pawn m: update.getPawns()){
			moves+=1; 
			newState = move(state,m);
			if(newState.gameOver){
				bestMove = m;
				break;
			}
			v = Strategy(heuristic, newState.opponent, newState.currPlayer);
			if (v > maxValue){
				maxValue = v;
				bestMove = m; 
			}
		}
		state.currPlayer.NodesExp += moves;
		return bestMove;
	}

	/**
	 * @param state of the current board
	 * @return move according to user input
	 */
	public static Pawn HumanMove (State state ){
		Pawn nextMove = null;
		Update update = getMoves(state);
		int currR, currC, nextR, nextC; 
		scanner = new Scanner (System.in);
		System.out.println("Enter the row of the Pawn you want to move: ");
		currR = scanner.nextInt();
		System.out.println("Enter the column of the Pawn you want to move: ");
		currC = scanner.nextInt();
		System.out.println("Enter the row of the space you want to move to: ");
		nextR = scanner.nextInt();
		System.out.println("Enter the column of the space you want to move to: ");
		nextC =  scanner.nextInt(); 
		
		boolean legalMove = false; 
		for(Pawn m : update.getPawns()){
			if(currR == m.getCurr().getR() && currC == m.getCurr().getC() && nextR == m.getNext().getR() && nextC == m.getNext().getC()){
				nextMove= m;
				legalMove = true;
				break;
			}
		}
		if(legalMove == false){
			System.out.println("Invalid move, try again!" );
			nextMove = HumanMove(state);
		}
		return nextMove;
	}


	private static Pawn[][] deepCopy (Pawn[][] original){
		if(original == null) return null;
		Pawn[][] result = new Pawn[original.length][original[0].length];
		int currR, currC, nextR, nextC; 
		char c;
		for(int i =0; i <original.length; i++){
			for(int j = 0; j < original[0].length; j++){
				currR = original[i][j].getCurr().getR();
				currC = original[i][j].getCurr().getC();
				nextR = original[i][j].getNext().getR();
				nextC = original[i][j].getNext().getC();
				c = original[i][j].getC(); 
				result[i][j] = new Pawn(currR, currC, c, nextR, nextC);
			}
		}
		return result;	
	}	


	public static void showBoard(Pawn[][] board){
		System.out.println();
		for(int r = 0; r < 2; r++){
			for(int c = 0; c < cols; c++){
				if (r == 1){
					if (c == 0) System.out.print("   ");
					System.out.print(" " + '_' + " ");
				}
				else {
					if (c == 0) System.out.print("   ");
					System.out.print(" " + c + " ");
				}
			}
			System.out.println();
		}
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				if(j == 0) System.out.print(i + " |");
				System.out.print(" " + board[i][j].getC() + " ");
			}
			System.out.println();
		}
		for(int r = 0; r < 2; r++){
			for(int c = 0; c < cols; c++){
				if (r == 0){
					if (c == 0) System.out.print("   ");
					System.out.print(" " + '_' + " ");
				}
				else {
					if (c == 0) System.out.print("   ");
					System.out.print(" " + c + " ");
				}
			}
			System.out.println();
		}
	}

	private static double min(double x, double y) {
		if (x <= y) return x;
		else return y; 
	}

	private static double max(double x, double y) {
		if (x >= y) return x;
		else return y;
	}

	/**
	 * @param heuristic - a strategy for a player
	 * @param currPlayer
	 * @param opponent
	 * @return the value of a strategy from a certain heuristic
	 */
	public static double Strategy (String heuristic, Player currPlayer, Player opponent){
		if (heuristic == "OFF1"){
			return 2*(4*cols-2 - opponent.Pawns) + Math.random(); // 2*(30 - opponent.pawns)
		}
		else if (heuristic == "DEF1"){
			return 2 * currPlayer.Pawns + Math.random(); 
		}	
		else if (heuristic == "OFF2"){ // WORKS for 8x8 and 1 pawn to goal
			return 4*(2*cols-opponent.Pawns) + 2*currPlayer.Pawns +  1*currPlayer.AtkPawns + 2.5*(rows-1-currPlayer.distance)+ 0*(cols-1-opponent.Walls)+ 2.5*currPlayer.GoalPawns + Math.random();
		}
		else if (heuristic == "DEF2"){// changes weight of walls from 2; attack pawns from 1.5; goals from 3/ opponent distance 2
			return 6*currPlayer.Pawns + 4*opponent.distance + 1*(currPlayer.Walls) + 4*(rows-1-currPlayer.distance) + 1*(2*cols-opponent.AtkPawns)+ 5*currPlayer.GoalPawns +Math.random();
		}
		else return 0;
	}


	public static void main(String[] args){
		for(int runs = 0; runs < 1; runs++){
			rows = 8;
			cols = 8;
			Pawn[][] board = new Pawn[rows][cols];

			for(int i = 0; i < rows; i++){
				for(int j = 0; j < cols; j++){
					if(i < 2){
						board[i][j] = new Pawn(i, j, 'B',i,j);
					}
					else if(i > (rows - 3)){
						board[i][j] = new Pawn(i, j, 'W',i,j);
					}
					else board[i][j] = new Pawn(i, j, '~',i,j);
				}
			}
			
			String P1_strat = "DEF2";
			String P2_strat = "OFF1";

			Player P1 = new Player('W', P1_strat, rows, cols);
			Player P2 = new Player('B', P2_strat, rows, cols);
			state = new State(board, P1, P2);
			showBoard(state.gameBoard);
			int i = 1;
			long startTime = System.currentTimeMillis();
			while (state.gameOver == false){
				Pawn move; 
				if (i%2 == 1){
					//PLAYER P1
						//move = GreedyMove(state,state.currPlayer.Heuristic);
					move = AlphaBetaMove(state,5);
				}
				else{
					//PLAYER P2
					move = AlphaBetaMove(state,5);
				
				}

				if(move == null) break;
											System.out.println("----------");
											System.out.println(state.currPlayer.color +" Nodes expanded: " + state.currPlayer.NodesExp);
											System.out.println(state.currPlayer.color + " Goal Pawns: " + state.currPlayer.GoalPawns);
											System.out.println("# of Pawns " + state.currPlayer.Pawns);
											System.out.println("Min Distance: " + state.currPlayer.distance);
											System.out.println("Attack pawns: " + state.currPlayer.AtkPawns);
											System.out.println("Walls: " + state.currPlayer.Walls);
											System.out.println("----------");
											System.out.println(state.opponent.color + " Nodes exp: " + state.opponent.NodesExp);
											System.out.println(state.opponent.color + " Goal Pawns: " + state.opponent.GoalPawns);
											System.out.println("# of Pawns " + state.opponent.Pawns);
											System.out.println("Min Distance: " + state.opponent.distance);
											System.out.println("Attack Pawns: " + state.opponent.AtkPawns);
											System.out.println("Walls: " + state.opponent.Walls);

				state = move(state,move); //generate next state
				if (state.gameOver == true) {
					break; 
				}
				//System.out.println(i);
				showBoard(state.gameBoard);

				i+=1; 
			}
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime; 
			showBoard(state.gameBoard);

			// NOTE the winner is state.opponent.color because function "move" switches currPlayer and opponent at each round
			System.out.println("The winnner is: " + state.opponent.color + " " + "(" +state.opponent.Heuristic +")"+" vs "+state.currPlayer.color+ " (" +state.currPlayer.Heuristic + ")");

			System.out.println(state.opponent.color + " Nodes expanded: " + state.opponent.NodesExp);
			System.out.println(state.currPlayer.color + " Nodes expanded: " + state.currPlayer.NodesExp);

			if(i%2 == 0){
				System.out.println("Average # of Nodes Expanded per move: " + state.opponent.color + ": " + (double)(state.opponent.NodesExp)/((double)i/2));
				System.out.println("Average # of Nodes Expanded per move: " + state.currPlayer.color + ": " + (double)(state.currPlayer.NodesExp)/((double)i/2));
			}
			else {
				System.out.println("Average # of Nodes Expanded per move: " + state.opponent.color + ": " + (double)(state.opponent.NodesExp)/((double)i/2 + 1));
				System.out.println("Average # of Nodes Expanded per move: " + state.currPlayer.color + ": " + (double)(state.currPlayer.NodesExp)/((double)i/2));
			}
			System.out.println("Average Time Per Move: " + (double)(totalTime/i) + " milliseconds");

			System.out.println(state.opponent.color+ " # of Pawns Captured: " + (2*cols - state.currPlayer.Pawns));
			System.out.println(state.currPlayer.color+ " # of Pawns Captured: " + (2*cols - state.opponent.Pawns));
			System.out.println("Total Moves (Both Players): " + i);

			if(state.opponent.color == 'W'){
				whiteNodesExpanded += state.opponent.NodesExp; 
				whiteCaptures += 2*cols - state.currPlayer.Pawns;
				whiteWins+=1;
			}
			if(state.opponent.color == 'B'){
				blackNodesExpanded += state.opponent.NodesExp; 
				blackCaptures += 2*cols - state.currPlayer.Pawns;
				blackWins +=1;
			}
			if(state.currPlayer.color == 'W'){
				whiteNodesExpanded += state.currPlayer.NodesExp;
				whiteCaptures += 2*cols - state.opponent.Pawns;
			}
			if(state.currPlayer.color == 'B'){
				blackNodesExpanded += state.currPlayer.NodesExp;
				blackCaptures += 2*cols - state.opponent.Pawns;
			}
			time += totalTime; 
			totalMoves += i; 
		}
		//scanner.close();

		System.out.println(whiteWins + "/" +(whiteWins+blackWins));
		System.out.println("Average White Nodes Expanded: " + (double)(whiteNodesExpanded/10));
		System.out.println("Average Black Nodes Expanded: " + (double)(blackNodesExpanded/10));
		System.out.println("Average Time per Move: " + (double)(time/totalMoves));
		System.out.println("White Captures: " + (double)(whiteCaptures/10));
		System.out.println("Black Captures: " + (double)(blackCaptures/10));
		System.out.println("Total Moves per Game: " + (double)totalMoves/10);


	}

}
