package application;

public class GameNode {
	GameNode parent;
	
	Long gameCode;
	
	int team1Code;
	int team2Code;
	int team1Points;
	int team2Points;
	
	int year;
	int winner;
	int loser;
	
	int home_team;
	
	public GameNode(Long gameCode,int team1Code,int team1Points, int year){
		this.gameCode = gameCode;
		this.team1Code = team1Code;
		this.team1Points = team1Points;
		this.year = year;
		this.parent = null;
	}
	
	public GameNode(Long gameCode,int team1Code,int team1Points, int year,int home_team_code){
		this.gameCode = gameCode;
		this.team1Code = team1Code;
		this.team1Points = team1Points;
		this.year = year;
		this.parent = null;
		this.home_team = home_team_code;
	}
	
	public void addTeam2(int team2Code,int team2Points){
		this.team2Code = team2Code;
		this.team2Points = team2Points;
		if(this.team1Points > this.team2Points){
			this.winner = team1Code;
			this.loser = team2Code;
		}
		else{
			this.winner = team2Code;
			this.loser = team1Code;
		}
	}
}
