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

public class P3Controller implements Initializable{
	
	@FXML
	public Button homeButton;
	
	@FXML
	public Button P1Button;
	
	@FXML
	public Button P2Button;
	
	@FXML
	public Button P4Button;
	
	@FXML
	public TextField team;
	
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
	
	int teamCode = -1;
	String output= "";
	String teamName = "";
	
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
		output = "";
		
		teamName = team.getText();
		int teamIndex = Main.name.indexOf(teamName);
		if(teamIndex == -1){
			outBox.getChildren().clear();
			printMessage("Incorrect Team Input");
			return;
		}
		teamCode = Main.code.get(teamIndex);
		
		Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
            	executeProblem3();
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> displayResults());
        new Thread(task).start();
        
	}
	
	public void executeProblem3(){
		ArrayList<Integer> teams = new ArrayList<Integer>();
		ArrayList<Integer> yards = new ArrayList<Integer>();
		ArrayList<Integer> year = new ArrayList<Integer>();
		
		ResultSet queryResult = null;

		String rushQuery = "SELECT a.game_code,a.team_code,b.year,a.rush_yard FROM team_game_stats a, game b WHERE a.game_code = b.game_code AND (b.visitor_code = "+teamCode+"OR b.home_code = "+teamCode+" ) ORDER BY game_code;";
		queryResult = queryDatabase(rushQuery);
		try {
			while(queryResult.next()){
				if(Integer.parseInt(queryResult.getObject(2).toString()) != teamCode){
					teams.add(Integer.parseInt(queryResult.getObject(2).toString()));
					yards.add(Integer.parseInt(queryResult.getObject(4).toString()));
					year.add(Integer.parseInt(queryResult.getObject(3).toString()));
				}
			}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		int largest = -1;
		int index = -1;
		for(int i=0;i<yards.size();++i){
			if(largest < yards.get(i)){
				largest = yards.get(i);
				index = i;
			}
		}
		output = Main.name.get(Main.code.indexOf(teams.get(index))) + " had the most rushing yards of "+yards.get(index)+" in "+year.get(index)+" against "+teamName;
	}
	
	public void displayResults(){
		outBox.getChildren().clear();
		buttonActive.setVisible(true);
		buttonClicked.setVisible(false);
		queryButton.setDisable(false);
		loadingIcon.setVisible(false);
		
		printMessage(output);
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
