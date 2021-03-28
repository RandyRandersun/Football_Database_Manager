package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class P2Controller implements Initializable{
	
	@FXML
	public Button homeButton;
	
	@FXML
	public Button P1Button;
	
	@FXML
	public Button P3Button;
	
	@FXML
	public Button P4Button;
	
	@FXML
	public TextField player1;
	
	@FXML
	public TextField player2;
	
	@FXML
	public ContextMenu player1Context;
	
	@FXML
	public ContextMenu player2Context;
	
	@FXML
	public TextFlow outBox;
	
	@FXML
	public Rectangle buttonActive;
	
	@FXML
	public Rectangle buttonClicked;
	
	@FXML
	public Button queryButton;
	
	@FXML
	public ImageView loadingIcon;
	
	ArrayList<GameNode> games = new ArrayList<GameNode>();
	ArrayList<playerNode> players = new ArrayList<playerNode>();
	
	ArrayList<Integer> teamNum = new ArrayList<Integer>();
	ArrayList<ArrayList<playerNode>> teamPlayers = new ArrayList<ArrayList<playerNode>>();
	
	int player1Code = -1;
	int player2Code = -1;
	
	ArrayList<playerNode> printPath = new ArrayList<playerNode>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	@FXML
	public void handleQueryButton(ActionEvent e){
		
		buttonActive.setVisible(false);
		buttonClicked.setVisible(true);
		queryButton.setDisable(true);
		//loading icon idea from https://codepen.io/scootman/pen/NMZRNa
		loadingIcon.setVisible(true);
		outBox.getChildren().clear();
		
		games.clear();
		players.clear();
		teamNum.clear();
		teamPlayers.clear();
		printPath.clear();
		player1Code = -1;
		player2Code = -1;
		
		String player1Name = player1.getText();
		String player2Name = player2.getText();
		
		int player1Index = Main.playerName.indexOf(player1Name);
		int player2Index = Main.playerName.indexOf(player2Name);
		
		if(player1Index == -1 || player2Index == -1){
			outBox.getChildren().clear();
			printMessage("Incorrect Player Input");
			return;
		}
		
		player1Code = Main.playerCode.get(player1Index);
		player2Code = Main.playerCode.get(player2Index);
		
		System.out.println(player1Code);
		System.out.println(player2Code);

		
		Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
            	executeProblem2();
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> displayResults());
        new Thread(task).start();

	}
	
	public void displayResults(){
		outBox.getChildren().clear();
		buttonActive.setVisible(true);
		buttonClicked.setVisible(false);
		queryButton.setDisable(false);
		loadingIcon.setVisible(false);
		
		if(printPath.get(printPath.size()-1).playerCode != player2Code){
			printMessage("Could Not Find Victory Chain");
		}
		else{
			for(int i=0;i<printPath.size();++i){
				printMessage(printPath.get(i).connection + printPath.get(i).playerName +"\n" );
			}
		}
	}
	
	public void executeProblem2(){
		String teamCodeQuery = "SELECT DISTINCT team_code FROM player ORDER BY team_code;";
		ResultSet queryResult = queryDatabase(teamCodeQuery);
		try {
			while(queryResult.next()){
				teamNum.add(Integer.parseInt(queryResult.getObject(1).toString()));
				teamPlayers.add(new ArrayList<playerNode>());
			}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		//get games
		String homeWinQuery = "SELECT a.game_code,a.team_code,a.points,b.year FROM team_game_stats a, game b WHERE a.game_code = b.game_code ORDER BY game_code;";
		queryResult = queryDatabase(homeWinQuery);
		try {
				while(queryResult.next()){
					boolean found = false;
					for(int i=0;i<games.size();i++){
						if(games.get(i).gameCode == Long.parseLong(queryResult.getObject(1).toString())){
							games.get(i).addTeam2(Integer.parseInt(queryResult.getObject(2).toString()), Integer.parseInt(queryResult.getObject(3).toString()));
							found = true;
						}
					}
					if(!found){
						games.add(new GameNode(Long.parseLong(queryResult.getObject(1).toString()),Integer.parseInt(queryResult.getObject(2).toString()),Integer.parseInt(queryResult.getObject(3).toString()),Integer.parseInt(queryResult.getObject(4).toString())));
					}
				}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}

		//get players
		String getPlayers = "SELECT first_name,last_name,player_code,team_code,hometown,year FROM player ORDER BY player_code;";
		System.out.println("Started query players");
		queryResult = queryDatabase(getPlayers);
		System.out.println("Ended query players");
		try {
			int currentPlayerCode = -1;
			playerNode currPlayerNode = null;
			while(queryResult.next()){
				if(currentPlayerCode != Integer.parseInt(queryResult.getObject(3).toString())){
					if(queryResult.getObject(1) != null){
						if(queryResult.getObject(5) != null){
							currPlayerNode = new playerNode(queryResult.getObject(1).toString()+" "+ queryResult.getObject(2).toString() ,Integer.parseInt(queryResult.getObject(3).toString()) , queryResult.getObject(5).toString() , Integer.parseInt(queryResult.getObject(4).toString()), Integer.parseInt(queryResult.getObject(6).toString()));
							players.add(currPlayerNode);
						}
						else{
							currPlayerNode = new playerNode(queryResult.getObject(1).toString()+" "+ queryResult.getObject(2).toString() ,Integer.parseInt(queryResult.getObject(3).toString()) , null , Integer.parseInt(queryResult.getObject(4).toString()), Integer.parseInt(queryResult.getObject(6).toString()));
							players.add(currPlayerNode);
						}
					}
					else{
						if(queryResult.getObject(5) != null){
							currPlayerNode = new playerNode(queryResult.getObject(2).toString() ,Integer.parseInt(queryResult.getObject(3).toString()) , queryResult.getObject(5).toString() , Integer.parseInt(queryResult.getObject(4).toString()), Integer.parseInt(queryResult.getObject(6).toString()));
							players.add(currPlayerNode);
						}
						else{
							currPlayerNode =new playerNode(queryResult.getObject(2).toString() ,Integer.parseInt(queryResult.getObject(3).toString()) , null , Integer.parseInt(queryResult.getObject(4).toString()), Integer.parseInt(queryResult.getObject(6).toString())) ;
							players.add(currPlayerNode);
						}
					}
					currentPlayerCode = Integer.parseInt(queryResult.getObject(3).toString());
				}
				else{
					if(queryResult.getObject(5) != null){
						currPlayerNode.updatePlayer(queryResult.getObject(5).toString(),Integer.parseInt(queryResult.getObject(4).toString()),Integer.parseInt(queryResult.getObject(6).toString()));
					}
					else{
						currPlayerNode.updatePlayer(Integer.parseInt(queryResult.getObject(4).toString()),Integer.parseInt(queryResult.getObject(6).toString()));
					}
				}
			}
		}
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		System.out.println("Ended creation of player vector");

		for(int i=0;i<players.size();++i){
			for(int j=0;j<teamNum.size();++j){
				if(players.get(i).teamCodes.contains(teamNum.get(j))){
					teamPlayers.get(j).add(players.get(i));
				}
			}
		}

		System.out.println("Game Size:"+games.size());
		System.out.println("Player Size:"+players.size());
		
		ArrayList<playerNode> pathToWin = new ArrayList<playerNode>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		LinkedList<playerNode> queue = new LinkedList<playerNode>(); 
		
		playerNode temp = null;
		for(int i=0;i<players.size();++i){
			if(players.get(i).playerCode == player1Code){
				temp = players.get(i);
				break;
			}
		}
		queue.add(temp); 
		
        while (queue.size() != 0){ 
            playerNode sNode = queue.poll();
            int s = sNode.playerCode;
            pathToWin.add(sNode);
            if(s == player2Code){
            	System.out.println("-----------------Broke---------------------");
            	break;
            }

            for(int i=0;i<players.size();++i){//good test is  alex allen fred holmes
            	if(players.get(i).playerCode == player1Code)continue;
            	//check hometown
            	if(players.get(i).hometown != null){
            		if(players.get(i).hometown.equals(sNode.hometown)){
                		int n = players.get(i).playerCode;
                		if(!visited.contains(n)){
                			visited.add(n);
                            players.get(i).parent = sNode;
                            players.get(i).connection = sNode.playerName + " has the same hometown as ";
                            queue.add(players.get(i));
                		}
                	}
            	}
            }

            for(int i=0;i<sNode.teamCodes.size();++i){
            	ArrayList<playerNode> temporary = teamPlayers.get(teamNum.indexOf(sNode.teamCodes.get(i)));
            	for(int j = 0;j<temporary.size();++j){
            		for(int k=0;k<temporary.get(j).teamCodes.size();++k){
            			if(sNode.years.get(i).equals(temporary.get(j).years.get(k)) && temporary.get(j).playerCode != player1Code){
            				int n = temporary.get(j).playerCode;
                    		if(!visited.contains(n)){
                    			visited.add(n);
                    			temporary.get(j).parent = sNode;
                    			temporary.get(j).connection = sNode.playerName + " was on the same team in "+sNode.years.get(i)+" as ";
                                queue.add(temporary.get(j));
                    		}
            			}
            		}
            	}
            }

            for(int i=0;i<games.size();++i){
            	if(sNode.teamCodes.contains(games.get(i).team1Code) && sNode.years.get(sNode.teamCodes.indexOf(games.get(i).team1Code)).equals(games.get(i).year)){
            		ArrayList<playerNode> tempVec = teamPlayers.get(teamNum.indexOf(games.get(i).team2Code));
            		for(int j=0;j<tempVec.size();++j){
            			int n = players.get(j).playerCode;
                		if(!visited.contains(n)){
                			visited.add(n);
                            players.get(j).parent = sNode;
                            players.get(j).connection = sNode.playerName + " was in the same game in "+games.get(i).year+" as ";
                            queue.add(players.get(j));
                		}
            		}
            	}
            	if(sNode.teamCodes.contains(games.get(i).team2Code) && sNode.years.get(sNode.teamCodes.indexOf(games.get(i).team2Code)).equals(games.get(i).year)){
            		ArrayList<playerNode> tempVec = teamPlayers.get(teamNum.indexOf(games.get(i).team1Code));
            		for(int j=0;j<tempVec.size();++j){
            			int n = players.get(j).playerCode;
                		if(!visited.contains(n)){
                			visited.add(n);
                            players.get(j).parent = sNode;
                            players.get(j).connection = sNode.playerName + " was in the same game in "+games.get(i).year+" as ";
                            queue.add(players.get(j));
                		}
            		}
            	}
            }
        }
		
		System.out.println("Path to the win: ");
		System.out.println("--------------------------------------------------------");
		System.out.println(pathToWin.size());
		
		ArrayList<playerNode> shortPath = new ArrayList<playerNode>();
		playerNode backProp = pathToWin.get(pathToWin.size()-1);
		
		while( backProp.parent != null){
			shortPath.add(backProp);
			backProp = backProp.parent;
		}
		
		Collections.reverse(shortPath);
		printPath = shortPath;
		System.out.println("Visited size="+visited.size());
	}
	
	@FXML
	public void updateAutoCorrext(KeyEvent e){
		TextField current;
		ContextMenu entriesPopup;
		ArrayList<CustomMenuItem> correctBox = new ArrayList<CustomMenuItem>();
		if(e.getSource() == player1){
			current = player1;
			entriesPopup = player1Context;
		}
		else{
			current = player2;
			entriesPopup = player1Context;
		}
		String currentString = current.getText();
		currentString = currentString +e.getCharacter();

		if(currentString.length() == 0){
			entriesPopup.hide();
		}
		else{
			ArrayList<String> searchResult = new ArrayList<String>();
			for(int i=0;i<Main.playerName.size();++i){
				if(Main.playerName.get(i).toLowerCase().contains(currentString.toLowerCase()) && !searchResult.contains(Main.playerName.get(i))){
					searchResult.add(Main.playerName.get(i));
					if(searchResult.size() >= 10){
						break;
					}
				}
			}
			
			int maxEntries = 10;
		    int count = Math.min(searchResult.size(), maxEntries);
		    
			if(searchResult.size() > 0){
				 for (int i = 0; i < count; i++){
				      final String result = searchResult.get(i);
				      CustomMenuItem item = new CustomMenuItem(new Label(result));
				      item.setOnAction(new EventHandler<ActionEvent>(){
				    	  @Override
				          public void handle(ActionEvent actionEvent) {
				    		  current.setText(result);
				    		  entriesPopup.hide();
				    	  }
				      });
				      correctBox.add(item);
				 }
				    entriesPopup.getItems().clear();
				    entriesPopup.getItems().addAll(correctBox);
	            if (!entriesPopup.isShowing()){
	            	entriesPopup.show((Node)e.getSource(), Side.BOTTOM, 0, 0);
	            }
			}
			else{
				entriesPopup.hide();
			}
		}
	}
	
	private ResultSet queryDatabase(String query){
		Connection conn = null;
		ResultSet result = null;
		try {
			Class.forName("org.postgresql.Driver");
		    conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/team903_10_cfb",
		    		Main.login.user, Main.login.pswd);
		} 
		catch (Exception e) {
		      e.printStackTrace();
		      System.err.println(e.getClass().getName()+": "+e.getMessage());
		      System.exit(0);
		}
		try{
			Statement stmt = conn.createStatement();
		    String sqlStatement = query;
		    result = stmt.executeQuery(sqlStatement);
		    //ResultSetMetaData resultSetMetaData = result.getMetaData();
		    return result;
	    	
		} 
		catch (Exception e){
		   System.out.println("Error accessing Database.");
		   outBox.getChildren().clear();
		   String error_message = "Error accessing Database.";
		   Text text_3 = new Text(error_message); 
		   Color text_color = Color.web("#FFF");
		   text_3.setFill(text_color); 
		   text_3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
	       outBox.getChildren().add(text_3);
		}
		try {
		    conn.close();
		    System.out.println("Connection Closed.");
		} 
		catch(Exception e) {
		    System.out.println("Connection NOT Closed.");
		}
		return result;
	}
	
	private void printMessage(String s){
		Color text_color = Color.web("#FFF");
		String loading_message = s;
    	Text text_1 = new Text(loading_message); 
    	text_1.setFill(text_color); 
    	text_1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
        outBox.getChildren().add(text_1);
	}
	
	@FXML
	public void handleTab(ActionEvent e){
		if(e.getSource() == homeButton) {
			try {
	            Parent parent = FXMLLoader.load(getClass().getResource("Main_Window.fxml"));
	            Stage stage = new Stage(StageStyle.DECORATED);
	            Main.stage.setTitle("College Football Database Manager");
	            Main.stage.setScene(new Scene(parent));
	        } catch (IOException ex) {
	            
	        }
		}
		
		if(e.getSource() == P1Button) {
			try {
	            Parent parent = FXMLLoader.load(getClass().getResource("Problem1.fxml"));
	            Stage stage = new Stage(StageStyle.DECORATED);
	            Main.stage.setTitle("College Football Database Manager");
	            Main.stage.setScene(new Scene(parent));
	        } catch (IOException ex) {
	            
	        }
		}
		
		if(e.getSource() == P3Button) {
			try {
	            Parent parent = FXMLLoader.load(getClass().getResource("Problem3.fxml"));
	            Stage stage = new Stage(StageStyle.DECORATED);
	            Main.stage.setTitle("College Football Database Manager");
	            Main.stage.setScene(new Scene(parent));
	        } catch (IOException ex) {
	            
	        }
			
		}
		if(e.getSource() == P4Button) {
			try {
	            Parent parent = FXMLLoader.load(getClass().getResource("Problem4.fxml"));
	            Stage stage = new Stage(StageStyle.DECORATED);
	            Main.stage.setTitle("College Football Database Manager");
	            Main.stage.setScene(new Scene(parent));
	        } catch (IOException ex) {
	            
	        }
		}
	}
	
}
