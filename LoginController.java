package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class LoginController implements Initializable{
	
	@FXML
	public Circle ball;
	@FXML
	public Circle ball1;
	@FXML
	public Circle ball2;
	
	//Fancy login animation
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		double multiplier1 = 1.5;
		double multiplier2 = 1.5;
		double multiplier3 = 1.5;
		Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0*multiplier1),
                        new KeyValue(ball.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(400*multiplier1),
                        new KeyValue(ball.translateYProperty(), -30, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(550*multiplier1),
                        new KeyValue(ball.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(700*multiplier1),
                        new KeyValue(ball.translateYProperty(), -15, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(800*multiplier1),
                        new KeyValue(ball.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(900*multiplier1),
                        new KeyValue(ball.translateYProperty(), -5, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(1000*multiplier1),
                        new KeyValue(ball.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                )
        );
		Timeline timeline2 = new Timeline(
                new KeyFrame(Duration.millis(0*multiplier2),
                        new KeyValue(ball1.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(400*multiplier2),
                        new KeyValue(ball1.translateYProperty(), -30, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(550*multiplier2),
                        new KeyValue(ball1.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(700*multiplier2),
                        new KeyValue(ball1.translateYProperty(), -15, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(800*multiplier2),
                        new KeyValue(ball1.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(900*multiplier2),
                        new KeyValue(ball1.translateYProperty(), -5, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(1000*multiplier2),
                        new KeyValue(ball1.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                )
        );
		Timeline timeline3 = new Timeline(
                new KeyFrame(Duration.millis(0*multiplier3),
                        new KeyValue(ball2.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(400*multiplier3),
                        new KeyValue(ball2.translateYProperty(), -30, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(550*multiplier3),
                        new KeyValue(ball2.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(700*multiplier3),
                        new KeyValue(ball2.translateYProperty(), -15, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(800*multiplier3),
                        new KeyValue(ball2.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                ),
                new KeyFrame(Duration.millis(900*multiplier3),
                        new KeyValue(ball2.translateYProperty(), -5, Interpolator.SPLINE(0.755, 0.050, 0.855, 0.060))
                ),
                new KeyFrame(Duration.millis(1000*multiplier3),
                        new KeyValue(ball2.translateYProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                )
        );
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline2.setCycleCount(Timeline.INDEFINITE);
		timeline3.setCycleCount(Timeline.INDEFINITE);
		
		timeline.setDelay(Duration.valueOf("500ms"));
		timeline2.setDelay(Duration.valueOf("1000ms"));
		timeline3.setDelay(Duration.valueOf("1100ms"));
		
        timeline.play();
        timeline2.play();
        timeline3.play();
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                login();
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> swapScene());
        new Thread(task).start();
	}
	
	//Open main window
	public void swapScene(){
		try {
            Parent parent = FXMLLoader.load(getClass().getResource("Main_Window.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            Main.stage.setTitle("College Football Database Manager");
            Main.stage.setScene(new Scene(parent));
        } catch (IOException ex) {
        	ex.printStackTrace();
		      System.err.println(ex.getClass().getName()+": "+ex.getMessage());
        }
	}
	public void login(){
		System.out.println("login");
		String getTeams = "SELECT name,code FROM team;";
		ResultSet queryResult = queryDatabase(getTeams);
		try {
			while(queryResult.next()){
				Main.name.add(queryResult.getObject(1).toString());
				Main.code.add(Integer.parseInt(queryResult.getObject(2).toString()));
			}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		//Cache these for autofill later
		String getPlayers = "SELECT first_name,last_name, player_code FROM player;";
		queryResult = queryDatabase(getPlayers);
		try {
			while(queryResult.next()){
				if(queryResult.getObject(1) != null){
					Main.playerName.add(queryResult.getObject(1).toString()+" "+ queryResult.getObject(2).toString());
				}
				else{
					Main.playerName.add(queryResult.getObject(2).toString());
				}
				Main.playerCode.add(Integer.parseInt(queryResult.getObject(3).toString()));
			}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		String getConference = "SELECT code,name,year FROM conference;";
		queryResult = queryDatabase(getConference);
		try {
			while(queryResult.next()){
				Main.confereneCode.add(Integer.parseInt(queryResult.getObject(1).toString()));
				Main.conferenceName.add(queryResult.getObject(2).toString());
				Main.conferenceYear.add(Integer.parseInt(queryResult.getObject(3).toString()));
			}
		} 
		catch (NumberFormatException e1) {
			e1.printStackTrace();
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
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
	
}
