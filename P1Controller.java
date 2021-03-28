package application;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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

import application.GameNode;

public class P1Controller implements Initializable{
	
	int teamLoserCode = -1;
	int teamWinnerCode = -1;
	
	
	@FXML
	public Button homeButton;
	
	@FXML
	public Button P2Button;
	
	@FXML
	public Button P3Button;
	
	@FXML
	public Button P4Button;
	
	@FXML
	public TextField team1;
	
	@FXML
	public TextField team2;
	
	@FXML
	public TextFlow outBox;
	
	@FXML
	public ContextMenu team1Context;
	
	@FXML
	public ContextMenu team2Context;
	
	@FXML
	public Rectangle buttonActive;
	
	@FXML
	public Rectangle buttonClicked;
	
	@FXML
	public Button queryButton;
	
	@FXML
	public ImageView loadingIcon;
	
	ArrayList<GameNode> printPath;
	
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
		
		//display loading gif
		
		teamWinnerCode = -1;
		teamLoserCode = -1;

		
		String teamWinner = team1.getText();
		String teamLoser = team2.getText();
		
		teamWinnerCode = Main.code.get(Main.name.indexOf(teamWinner));
		teamLoserCode = Main.code.get(Main.name.indexOf(teamLoser));
		
		if(teamWinnerCode == -1 || teamLoserCode == -1){
			outBox.getChildren().clear();
			printMessage("Incorrect Team Input");
			return;
		}
		
		System.out.println(teamWinnerCode);
		System.out.println(teamLoserCode);
		
		Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
            	executeProblem1();
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> displayResults());
        new Thread(task).start();
		
		
		
	}
	
	public void displayResults(){
		if(printPath.get(printPath.size()-1).loser != teamLoserCode){
			printMessage("Could Not Find Victory Chain");
		}
		else{
			for(int i=0;i<printPath.size();++i){
				printMessage(Main.name.get(Main.code.indexOf(printPath.get(i).winner)) +" beat "+Main.name.get(Main.code.indexOf(printPath.get(i).loser))+" in "+ printPath.get(i).year +"\n" );
			}
		}
		buttonActive.setVisible(true);
		buttonClicked.setVisible(false);
		queryButton.setDisable(false);
		loadingIcon.setVisible(false);
	}
	
	public void executeProblem1(){
		ArrayList<GameNode> games = new ArrayList<GameNode>();

		ResultSet queryResult = null;

		//String homeWinQuery = "SELECT game_code,team_code,points FROM team_game_stats ORDER BY game_code;";
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

		System.out.println("Size:"+games.size());

		ArrayList<GameNode> pathToWin = new ArrayList<GameNode>();
		
		ArrayList<Long> visited = new ArrayList<Long>();
		LinkedList<GameNode> queue = new LinkedList<GameNode>(); 
		
		GameNode temp = new GameNode(new Long(-1), teamWinnerCode,0,0);
		queue.add(temp); 
  
        while (queue.size() != 0){ 
            GameNode sNode = queue.poll();
            int s;
            if(sNode == temp){
            	s = sNode.team1Code;
            }
            else{
            	s = sNode.loser; 
            }
            pathToWin.add(sNode);
            if(s == teamLoserCode){
            	System.out.println("-----------------Broke---------------------");
            	break;
            }
  
            for(int i=0;i<games.size();++i){
            	if(games.get(i).winner == s){
            		Long n = games.get(i).gameCode;
            		if (!visited.contains(n)){ 
                        visited.add(n);
                        games.get(i).parent = sNode;
                        queue.add(games.get(i));//.loser); 
                    } 
            	}
            } 
        }
		
		System.out.println("Path to the win: ");
		System.out.println("--------------------------------------------------------");
		
		ArrayList<GameNode> shortPath = new ArrayList<GameNode>();
		GameNode backProp = pathToWin.get(pathToWin.size()-1);
		
		while( backProp.parent != null){
			shortPath.add(backProp);
			backProp = backProp.parent;
		}
		
		Collections.reverse(shortPath);
		
		System.out.println("Visited size="+visited.size());
		printPath = shortPath;
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
	public void updateAutoCorrext(KeyEvent e){
		TextField current;
		ContextMenu entriesPopup;
		ArrayList<CustomMenuItem> correctBox = new ArrayList<CustomMenuItem>();
		if(e.getSource() == team1){
			current = team1;
			entriesPopup = team1Context;
		}
		else{
			current = team2;
			entriesPopup = team1Context;
		}
		String currentString = current.getText();
		currentString = currentString +e.getCharacter();

		if(currentString.length() == 0){
			entriesPopup.hide();
		}
		else{
			ArrayList<String> searchResult = new ArrayList<String>();
			for(int i=0;i<Main.name.size();++i){
				if(Main.name.get(i).toLowerCase().contains(currentString.toLowerCase()) && !searchResult.contains(Main.name.get(i))){
					searchResult.add(Main.name.get(i));
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
		
		if(e.getSource() == P2Button) {
			try {
	            Parent parent = FXMLLoader.load(getClass().getResource("Problem2.fxml"));
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
