package application;
	
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	public static Stage stage;
	
	public static dbSetup login = new dbSetup();
	
	public static ArrayList<String> name = new ArrayList<String>();
	public static ArrayList<Integer> code = new ArrayList<Integer>();
	
	public static ArrayList<String> playerName = new ArrayList<String>();
	public static ArrayList<Integer> playerCode = new ArrayList<Integer>();
	
	public static ArrayList<String> conferenceName = new ArrayList<String>();
	public static ArrayList<Integer> confereneCode = new ArrayList<Integer>();
	public static ArrayList<Integer> conferenceYear = new ArrayList<Integer>();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Login");
			/*BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();*/
			System.out.println("App started");
		} catch(Exception e) {
			e.printStackTrace();
		}
		stage = primaryStage;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
