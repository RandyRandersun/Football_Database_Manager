package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

public class P4Controller implements Initializable{
	
	@FXML
	public Button homeButton;
	
	@FXML
	public Button P1Button;
	
	@FXML
	public Button P2Button;
	
	@FXML
	public Button P3Button;
	
	@FXML
	public TextField team;
	
	@FXML
	public TextField yearField;
	
	@FXML
	public ContextMenu teamContext;
	
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
	
	ArrayList<Integer> teamsInSelectedConf = new ArrayList<Integer>();
	ArrayList<Integer> tempWins = new ArrayList<Integer>();
	ArrayList<Integer> tempLosses = new ArrayList<Integer>();
	
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
		tempWins.clear();
		tempLosses.clear();
		teamsInSelectedConf.clear();
		
		Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
            	executeProblem4();
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> displayResults());
        new Thread(task).start();
	}
	
	public void executeProblem4(){
		ArrayList<GameNode> games = new ArrayList<GameNode>();
		ResultSet queryResult = null;
		String homeWinQuery = "SELECT a.game_code,a.team_code,a.points,b.year,b.home_code FROM team_game_stats a, game b WHERE a.game_code = b.game_code AND b.year = "+yearField.getText()+" ORDER BY game_code;";
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
					games.add(new GameNode(Long.parseLong(queryResult.getObject(1).toString()),Integer.parseInt(queryResult.getObject(2).toString()),Integer.parseInt(queryResult.getObject(3).toString()),Integer.parseInt(queryResult.getObject(4).toString()),Integer.parseInt(queryResult.getObject(5).toString())));
				}
			}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		System.out.println("games size: "+games.size());
		int index = Main.conferenceName.indexOf(team.getText());
		if (index == -1){
			printMessage("invalid conference input");
			return;
		}
		int conferenceCode = Main.confereneCode.get(index);
		
		
		
		
		ArrayList<Integer> teamCodes = new ArrayList<Integer>();
		ArrayList<Integer> conferenceCodes = new ArrayList<Integer>();
		
		String teamQuery = "SELECT code,conference_code FROM team WHERE year = "+yearField.getText()+";";
		queryResult = queryDatabase(teamQuery);
		try {
			while(queryResult.next()){
				teamCodes.add(Integer.parseInt(queryResult.getObject(1).toString()));
				conferenceCodes.add(Integer.parseInt(queryResult.getObject(2).toString()));
			}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}

		teamsInSelectedConf = new ArrayList<Integer>();
		
		for(int i=0;i<teamCodes.size();++i){
			if(conferenceCodes.get(i) == conferenceCode){
				teamsInSelectedConf.add(teamCodes.get(i));
			}
		}

		tempWins = new ArrayList<Integer>();
		tempLosses = new ArrayList<Integer>();
		
		for(int i=0;i<teamsInSelectedConf.size();++i){
			tempWins.add(0);
			tempLosses.add(0);
		}

		for(int i=0;i<games.size();++i){
			if(teamsInSelectedConf.contains(games.get(i).winner) && games.get(i).home_team == games.get(i).winner){
				int tempIndex = teamsInSelectedConf.indexOf(games.get(i).winner);
				tempWins.set(tempIndex, tempWins.get(tempIndex)+1);
			}
			if(teamsInSelectedConf.contains(games.get(i).loser) && games.get(i).home_team == games.get(i).loser){
				int tempIndex = teamsInSelectedConf.indexOf(games.get(i).loser);
				tempLosses.set(tempIndex, tempLosses.get(tempIndex)+1);
			}
		}
	}
	
	public void displayResults(){
		outBox.getChildren().clear();
		buttonActive.setVisible(true);
		buttonClicked.setVisible(false);
		queryButton.setDisable(false);
		loadingIcon.setVisible(false);
		
		for(int i=0;i<teamsInSelectedConf.size();++i){
			double ss = ((double)tempWins.get(i)/((double)tempLosses.get(i)+(double)tempWins.get(i)))*100;
			String messageOut = Main.name.get(Main.code.indexOf(teamsInSelectedConf.get(i)))+" : "+Math.round(ss * 100.0) / 100.0;
			printMessage(messageOut);
			if((i+1)%2 ==0 && i!=0){
				printMessage("\n");
			}
			else{
				for(int j=0;j<70-messageOut.length();++j){
					printMessage(" ");
				}
			}
		}
		
	}
	
	@FXML
	public void updateAutoCorrext(KeyEvent e){
		TextField current;
		ContextMenu entriesPopup;
		ArrayList<CustomMenuItem> correctBox = new ArrayList<CustomMenuItem>();
		
		current = team;
		entriesPopup = teamContext;
		
		String currentString = current.getText();
		currentString = currentString +e.getCharacter();

		if(currentString.length() == 0){
			entriesPopup.hide();
		}
		else{
			ArrayList<String> searchResult = new ArrayList<String>();
			for(int i=0;i<Main.conferenceName.size();++i){
				if(Main.conferenceName.get(i).toLowerCase().contains(currentString.toLowerCase()) && !searchResult.contains(Main.conferenceName.get(i))){
					searchResult.add(Main.conferenceName.get(i));
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
	}
	
}
