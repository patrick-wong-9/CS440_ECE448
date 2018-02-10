package Breakthrough;

/**
 * @author Patrick Wong
 *
 */
public class Player {
	// created to keep track of pieces for both sides of the board at each state
	public char color;
	public int distance;//distance to goal
	public int Pawns; // start with 16
	public int GoalPawns; // starts at 0
	public int Walls;  // diagonally connected pawns past the initial rows
	public int AtkPawns;
	public int NodesExp;
	public int dir;
	public String Heuristic;

	public Player(char color, String strat, int rows, int cols){
		this.color = color;
		this.Pawns = 2*cols; // updated through getPawns
		this.distance = rows - 1; // updated through getPawns
		this.Walls = 0; // updated through getPawns
		this.AtkPawns = 0; // updated through getPawns
		this.GoalPawns = 0; // updated through getPawns
		this.NodesExp = 0; 
		if (color == 'B') this.dir = 1;
		if (color == 'W') this.dir = -1; 
		this.Heuristic = strat; 
	}

	public char getColor() {
		return color;
	}

	public void setColor(char color) {
		this.color = color;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getPawns() {
		return Pawns;
	}

	public void setPawns(int pawns) {
		Pawns = pawns;
	}

	public int getGoalPawns() {
		return GoalPawns;
	}

	public void setGoalPawns(int goalPawns) {
		GoalPawns = goalPawns;
	}

	public int getWalls() {
		return Walls;
	}

	public void setWalls(int walls) {
		Walls = walls;
	}

	public int getAtkPawns() {
		return AtkPawns;
	}

	public void setAtkPawns(int atkPawns) {
		AtkPawns = atkPawns;
	}

	public int getNodesExp() {
		return NodesExp;
	}

	public void setNodesExp(int nodesExp) {
		NodesExp = nodesExp;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public String getHeuristic() {
		return Heuristic;
	}

	public void setHeuristic(String heuristic) {
		Heuristic = heuristic;
	}
	
}
