package application;

import java.util.ArrayList;

public class playerNode {
	playerNode parent;
	String playerName;
	int playerCode;
	String hometown = null;
	ArrayList<Integer> teamCodes;
	ArrayList<Integer> years;
	
	String connection;
	
	public playerNode(String playerName,int playerCode,String hometown,int teamCode,int year){
		this.playerName = playerName;
		this.playerCode = playerCode;
		this.hometown = hometown;
		teamCodes = new ArrayList<Integer>();
		teamCodes.add(teamCode);
		years = new ArrayList<Integer>();
		years.add(year);
		this.parent = null;
	}
	public void updatePlayer(String hometown,int teamCode,int year){
		this.hometown = hometown;
		teamCodes.add(teamCode);
		years.add(year);
	}
	public void updatePlayer(int teamCode,int year){
		teamCodes.add(teamCode);
		years.add(year);
	}
}
