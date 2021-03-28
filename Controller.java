package application;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import application.dbSetup;

public class Controller implements Initializable{
	
	public boolean saveToFile = false;
	public ArrayList<String> table1Get = new ArrayList<String>();
	public ArrayList<String> table2Get = new ArrayList<String>();
	public ArrayList<String> conditions = new ArrayList<String>();
	public boolean flipFlop = false; //event handler called twice because hide on click is false
	public boolean flipFlop1 = false;
	public File filePathChooser;
	
	@FXML
	public ComboBox<String> table1;
	
	@FXML
	public ComboBox<String> table2;
	
	@FXML
	public ComboBox<String> table11;
	
	@FXML
	public ComboBox<String> comparison;
	
	@FXML
	public MenuButton mb;
	
	@FXML
	public MenuButton mb1;
	
	@FXML
	public MenuButton mb2;
	
	@FXML
	public TextField filePathString;
	
	@FXML
	public TextField conditionalQueryArg;
	
	@FXML
	public CheckBox preventDups;
	
	@FXML
	public Button P1Button;
	
	@FXML
	public Button P2Button;
	
	@FXML
	public Button P3Button;
	
	@FXML
	public Button P4Button;
	
	@FXML
	public Button addCondition;
	
	@FXML
	public Button clearCondition;
	
	
	private ObservableList<String> dbTypeList = FXCollections.observableArrayList("Conference","Team","Game","Player");
	private ObservableList<String> comparisonVals = FXCollections.observableArrayList("Equal","Not Equal","Greater than","Greater than or equal","Less than","Less than or equal");
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		table1.setItems(dbTypeList);
		table2.setItems(dbTypeList);
		table11.setItems(dbTypeList);
		comparison.setItems(comparisonVals);
		
		EventHandler<ActionEvent> handleSelected = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            {
            	flipFlop = !flipFlop;
            	if(!flipFlop)return;
                    String s = ((CheckBox)((CustomMenuItem)e.getSource()).getContent()).getText(); 
                    if(table1Get.contains(s)){
                    	for(int i=0;i<table1Get.size();++i){
                        	if(table1Get.get(i).equals(s)){
                        		table1Get.remove(i);
                        	}
                        }
                    }
                    else{
                    	table1Get.add(s);
                    }  
            } 
        };
        
        EventHandler<ActionEvent> handleSelected2 = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            {
            	flipFlop1 = !flipFlop1;
            	if(!flipFlop1)return;
                    String s = ((CheckBox)((CustomMenuItem)e.getSource()).getContent()).getText(); 
                    if(table2Get.contains(s)){
                    	for(int i=0;i<table2Get.size();++i){
                        	if(table2Get.get(i).equals(s)){
                        		table2Get.remove(i);
                        	}
                        }
                    }
                    else{
                    	table2Get.add(s);
                    }          
            } 
        }; 
        
        EventHandler<ActionEvent> handleSelected3 = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            {
                    String s = ((CheckBox)((CustomMenuItem)e.getSource()).getContent()).getText(); 
                    if(conditions.contains(s)){
                    	for(int i=0;i<conditions.size();++i){
                        	if(conditions.get(i).equals(s)){
                        		conditions.remove(i);
                        	}
                        }
                    }
                    else{
                    	conditions.add(s);
                    }          
            } 
        };
		
        table11.setOnAction((e) -> {
			mb1.getItems().clear();
			if(table11.getSelectionModel().getSelectedItem() == null)return;
			switch(table11.getSelectionModel().getSelectedItem()){
			case "Conference":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Conference Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Conference Name")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Subdivision")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setOnAction(handleSelected3);
				item2.setOnAction(handleSelected3);
				item3.setOnAction(handleSelected3);
				item4.setOnAction(handleSelected3);
				
				mb2.getItems().add(item1);
				mb2.getItems().add(item2);
				mb2.getItems().add(item3);
				mb2.getItems().add(item4);
				break;
			}
			case "Team":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Team Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Conference Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Team Name")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Year")); 	
				
				item1.setOnAction(handleSelected3);
				item2.setOnAction(handleSelected3);
				item3.setOnAction(handleSelected3);
				item4.setOnAction(handleSelected3);
				
				mb2.getItems().add(item1);
				mb2.getItems().add(item2);
				mb2.getItems().add(item3);
				mb2.getItems().add(item4);
				break;
			}
			case "Game":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Game Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Home Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Visitor Code")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Stadium Code")); 
				CustomMenuItem item5 = new CustomMenuItem(new CheckBox("Site")); 
				CustomMenuItem item6 = new CustomMenuItem(new CheckBox("Day")); 
				CustomMenuItem item7 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setOnAction(handleSelected3);
				item2.setOnAction(handleSelected3);
				item3.setOnAction(handleSelected3);
				item4.setOnAction(handleSelected3);
				item5.setOnAction(handleSelected3);
				item6.setOnAction(handleSelected3);
				item7.setOnAction(handleSelected3);
				
				mb2.getItems().add(item1);
				mb2.getItems().add(item2);
				mb2.getItems().add(item3);
				mb2.getItems().add(item4);
				mb2.getItems().add(item5);
				mb2.getItems().add(item6);
				mb2.getItems().add(item7);
				break;
			}
			case "Player":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Player Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Team Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Last Name")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("First Name")); 
				CustomMenuItem item5 = new CustomMenuItem(new CheckBox("Number")); 
				CustomMenuItem item6 = new CustomMenuItem(new CheckBox("Class")); 
				CustomMenuItem item7 = new CustomMenuItem(new CheckBox("Position"));
				CustomMenuItem item8 = new CustomMenuItem(new CheckBox("Height")); 
				CustomMenuItem item9 = new CustomMenuItem(new CheckBox("Weight")); 
				CustomMenuItem item10 = new CustomMenuItem(new CheckBox("Hometown")); 
				CustomMenuItem item11 = new CustomMenuItem(new CheckBox("Home State")); 
				CustomMenuItem item12 = new CustomMenuItem(new CheckBox("Home Country")); 
				CustomMenuItem item13 = new CustomMenuItem(new CheckBox("Last School")); 
				CustomMenuItem item14 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setOnAction(handleSelected3);
				item2.setOnAction(handleSelected3);
				item3.setOnAction(handleSelected3);
				item4.setOnAction(handleSelected3);
				item5.setOnAction(handleSelected3);
				item6.setOnAction(handleSelected3);
				item7.setOnAction(handleSelected3);
				item8.setOnAction(handleSelected3);
				item9.setOnAction(handleSelected3);
				item10.setOnAction(handleSelected3);
				item11.setOnAction(handleSelected3);
				item12.setOnAction(handleSelected3);
				item13.setOnAction(handleSelected3);
				item14.setOnAction(handleSelected3);
				
				mb2.getItems().add(item1);
				mb2.getItems().add(item2);
				mb2.getItems().add(item3);
				mb2.getItems().add(item4);
				mb2.getItems().add(item5);
				mb2.getItems().add(item6);
				mb2.getItems().add(item7);
				mb2.getItems().add(item8);
				mb2.getItems().add(item9);
				mb2.getItems().add(item10);
				mb2.getItems().add(item11);
				mb2.getItems().add(item12);
				mb2.getItems().add(item13);
				mb2.getItems().add(item14);
				break;
			}
			}
       });
        
        table2.setOnAction((e) -> {
			mb1.getItems().clear();
			switch(table2.getSelectionModel().getSelectedItem()){
			case "Conference":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Conference Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Conference Name")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Subdivision")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setOnAction(handleSelected2);
				item2.setOnAction(handleSelected2);
				item3.setOnAction(handleSelected2);
				item4.setOnAction(handleSelected2);
				
				mb1.getItems().add(item1);
				mb1.getItems().add(item2);
				mb1.getItems().add(item3);
				mb1.getItems().add(item4);
				break;
			}
			case "Team":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Team Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Conference Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Team Name")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Year")); 	
				
				item1.setOnAction(handleSelected2);
				item2.setOnAction(handleSelected2);
				item3.setOnAction(handleSelected2);
				item4.setOnAction(handleSelected2);
				
				mb1.getItems().add(item1);
				mb1.getItems().add(item2);
				mb1.getItems().add(item3);
				mb1.getItems().add(item4);
				break;
			}
			case "Game":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Game Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Home Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Visitor Code")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Stadium Code")); 
				CustomMenuItem item5 = new CustomMenuItem(new CheckBox("Site")); 
				CustomMenuItem item6 = new CustomMenuItem(new CheckBox("Day")); 
				CustomMenuItem item7 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setOnAction(handleSelected2);
				item2.setOnAction(handleSelected2);
				item3.setOnAction(handleSelected2);
				item4.setOnAction(handleSelected2);
				item5.setOnAction(handleSelected2);
				item6.setOnAction(handleSelected2);
				item7.setOnAction(handleSelected2);
				
				mb1.getItems().add(item1);
				mb1.getItems().add(item2);
				mb1.getItems().add(item3);
				mb1.getItems().add(item4);
				mb1.getItems().add(item5);
				mb1.getItems().add(item6);
				mb1.getItems().add(item7);
				break;
			}
			case "Player":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Player Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Team Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Last Name")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("First Name")); 
				CustomMenuItem item5 = new CustomMenuItem(new CheckBox("Number")); 
				CustomMenuItem item6 = new CustomMenuItem(new CheckBox("Class")); 
				CustomMenuItem item7 = new CustomMenuItem(new CheckBox("Position"));
				CustomMenuItem item8 = new CustomMenuItem(new CheckBox("Height")); 
				CustomMenuItem item9 = new CustomMenuItem(new CheckBox("Weight")); 
				CustomMenuItem item10 = new CustomMenuItem(new CheckBox("Hometown")); 
				CustomMenuItem item11 = new CustomMenuItem(new CheckBox("Home State")); 
				CustomMenuItem item12 = new CustomMenuItem(new CheckBox("Home Country")); 
				CustomMenuItem item13 = new CustomMenuItem(new CheckBox("Last School")); 
				CustomMenuItem item14 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setOnAction(handleSelected2);
				item2.setOnAction(handleSelected2);
				item3.setOnAction(handleSelected2);
				item4.setOnAction(handleSelected2);
				item5.setOnAction(handleSelected2);
				item6.setOnAction(handleSelected2);
				item7.setOnAction(handleSelected2);
				item8.setOnAction(handleSelected2);
				item9.setOnAction(handleSelected2);
				item10.setOnAction(handleSelected2);
				item11.setOnAction(handleSelected2);
				item12.setOnAction(handleSelected2);
				item13.setOnAction(handleSelected2);
				item14.setOnAction(handleSelected2);
				
				mb1.getItems().add(item1);
				mb1.getItems().add(item2);
				mb1.getItems().add(item3);
				mb1.getItems().add(item4);
				mb1.getItems().add(item5);
				mb1.getItems().add(item6);
				mb1.getItems().add(item7);
				mb1.getItems().add(item8);
				mb1.getItems().add(item9);
				mb1.getItems().add(item10);
				mb1.getItems().add(item11);
				mb1.getItems().add(item12);
				mb1.getItems().add(item13);
				mb1.getItems().add(item14);
				break;
			}
			}
       });
        
		table1.setOnAction((e) -> {
			mb.getItems().clear();
			switch(table1.getSelectionModel().getSelectedItem()){
			case "Conference":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Conference Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Conference Name")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Subdivision")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setHideOnClick(false);
				item2.setHideOnClick(false);
				item3.setHideOnClick(false);
				item4.setHideOnClick(false);
				
				item1.setOnAction(handleSelected);
				item2.setOnAction(handleSelected);
				item3.setOnAction(handleSelected);
				item4.setOnAction(handleSelected);
				
				mb.getItems().add(item1);
				mb.getItems().add(item2);
				mb.getItems().add(item3);
				mb.getItems().add(item4);
				break;
			}
			case "Team":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Team Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Conference Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Team Name")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Year")); 	
				
				item1.setHideOnClick(false);
				item2.setHideOnClick(false);
				item3.setHideOnClick(false);
				item4.setHideOnClick(false);
				
				item1.setOnAction(handleSelected);
				item2.setOnAction(handleSelected);
				item3.setOnAction(handleSelected);
				item4.setOnAction(handleSelected);
				
				mb.getItems().add(item1);
				mb.getItems().add(item2);
				mb.getItems().add(item3);
				mb.getItems().add(item4);
				break;
			}
			case "Game":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Game Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Home Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Visitor Code")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("Stadium Code")); 
				CustomMenuItem item5 = new CustomMenuItem(new CheckBox("Site")); 
				CustomMenuItem item6 = new CustomMenuItem(new CheckBox("Day")); 
				CustomMenuItem item7 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setHideOnClick(false);
				item2.setHideOnClick(false);
				item3.setHideOnClick(false);
				item4.setHideOnClick(false);
				item5.setHideOnClick(false);
				item6.setHideOnClick(false);
				item7.setHideOnClick(false);
				
				item1.setOnAction(handleSelected);
				item2.setOnAction(handleSelected);
				item3.setOnAction(handleSelected);
				item4.setOnAction(handleSelected);
				item5.setOnAction(handleSelected);
				item6.setOnAction(handleSelected);
				item7.setOnAction(handleSelected);
				
				mb.getItems().add(item1);
				mb.getItems().add(item2);
				mb.getItems().add(item3);
				mb.getItems().add(item4);
				mb.getItems().add(item5);
				mb.getItems().add(item6);
				mb.getItems().add(item7);
				break;
			}
			case "Player":{
				CustomMenuItem item1 = new CustomMenuItem(new CheckBox("Player Code")); 
				CustomMenuItem item2 = new CustomMenuItem(new CheckBox("Team Code")); 
				CustomMenuItem item3 = new CustomMenuItem(new CheckBox("Last Name")); 
				CustomMenuItem item4 = new CustomMenuItem(new CheckBox("First Name")); 
				CustomMenuItem item5 = new CustomMenuItem(new CheckBox("Number")); 
				CustomMenuItem item6 = new CustomMenuItem(new CheckBox("Class")); 
				CustomMenuItem item7 = new CustomMenuItem(new CheckBox("Position"));
				CustomMenuItem item8 = new CustomMenuItem(new CheckBox("Height")); 
				CustomMenuItem item9 = new CustomMenuItem(new CheckBox("Weight")); 
				CustomMenuItem item10 = new CustomMenuItem(new CheckBox("Hometown")); 
				CustomMenuItem item11 = new CustomMenuItem(new CheckBox("Home State")); 
				CustomMenuItem item12 = new CustomMenuItem(new CheckBox("Home Country")); 
				CustomMenuItem item13 = new CustomMenuItem(new CheckBox("Last School")); 
				CustomMenuItem item14 = new CustomMenuItem(new CheckBox("Year")); 
				
				item1.setHideOnClick(false);
				item2.setHideOnClick(false);
				item3.setHideOnClick(false);
				item4.setHideOnClick(false);
				item5.setHideOnClick(false);
				item6.setHideOnClick(false);
				item7.setHideOnClick(false);
				item8.setHideOnClick(false);
				item9.setHideOnClick(false);
				item10.setHideOnClick(false);
				item11.setHideOnClick(false);
				item12.setHideOnClick(false);
				item13.setHideOnClick(false);
				item14.setHideOnClick(false);
				
				item1.setOnAction(handleSelected);
				item2.setOnAction(handleSelected);
				item3.setOnAction(handleSelected);
				item4.setOnAction(handleSelected);
				item5.setOnAction(handleSelected);
				item6.setOnAction(handleSelected);
				item7.setOnAction(handleSelected);
				item8.setOnAction(handleSelected);
				item9.setOnAction(handleSelected);
				item10.setOnAction(handleSelected);
				item11.setOnAction(handleSelected);
				item12.setOnAction(handleSelected);
				item13.setOnAction(handleSelected);
				item14.setOnAction(handleSelected);
				
				mb.getItems().add(item1);
				mb.getItems().add(item2);
				mb.getItems().add(item3);
				mb.getItems().add(item4);
				mb.getItems().add(item5);
				mb.getItems().add(item6);
				mb.getItems().add(item7);
				mb.getItems().add(item8);
				mb.getItems().add(item9);
				mb.getItems().add(item10);
				mb.getItems().add(item11);
				mb.getItems().add(item12);
				mb.getItems().add(item13);
				mb.getItems().add(item14);
				break;
			}
			}
       });
	}
	
	@FXML
	public TextField QueryText;
	
	@FXML
	public Button YesButton;
	
	@FXML
	public Button NoButton;
	
	@FXML
	public ToggleButton toggleButton;
	
	@FXML
	public TextFlow outBox;
	
	@FXML
	public TextFlow showConditions;
	
	@FXML
	public Rectangle buttonActive;
	
	@FXML
	public Rectangle buttonClicked;
	
	@FXML
	public Button queryButton;
	
	@FXML
	public ImageView loadingIcon;
	
	public String builtQuery = "";
	public ResultSet queryResults = null;
	
	@FXML
	public void handleQueryButton(ActionEvent event){
		outBox.getChildren().clear();
		builtQuery = "";
		queryResults = null;
		queryDatabase();
	}
	
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	@FXML
	public void handleAddCondition(ActionEvent event){
		Color text_color = Color.web("#FFF");
		
		String comp = conditionalQueryArg.getText();
		if(!isNumeric(comp)){
			comp = "\'" + comp + "\'"; 
		}
		
		String condition_message = table11.getSelectionModel().getSelectedItem()+"."+fieldLookup(table11,conditions.get(0)) +comparisonLookup(comparison.getSelectionModel().getSelectedItem())+comp+"\n";
		
		conditionalQueryArg.clear();
		table11.getSelectionModel().clearSelection();
		mb2.getItems().clear();
		conditions.clear();
		comparison.getSelectionModel().clearSelection();
		
    	Text text_1 = new Text(condition_message); 
    	text_1.setFill(text_color); 
    	text_1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
    	showConditions.getChildren().add(text_1);
	}
	
	@FXML
	public void handleClearCondition(ActionEvent event){
		showConditions.getChildren().clear();
		conditions.clear();
	}
	
	@FXML
	public void handleTab(ActionEvent e){
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
	
	@FXML
	public void toggleSaveToFile(ActionEvent event){
		if(!saveToFile){
			toggleButton.setStyle("-fx-background-color: #1ae839");
		}
		else{
			toggleButton.setStyle("-fx-background-color: #e8271a");
		}
		saveToFile = !saveToFile;
		
	}
	
	@FXML
	public void chooseFileLocation(ActionEvent event){
		JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
        f.showSaveDialog(null);
        filePathChooser = f.getSelectedFile();
	}
	
	private void setNoQueryPrompt(){
		Color text_color = Color.web("#FFF");
		String loading_message = "Please enter a query";
    	Text text_1 = new Text(loading_message); 
    	text_1.setFill(text_color); 
    	text_1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
        outBox.getChildren().add(text_1);
	}
	
	public String comparisonLookup(String s){
		switch(s){
		case "Equal":{
			return " = ";
		}
		case "Not Equal":{
			return " != ";
		}
		case "Greater than":{
			return " > ";
		}
		case "Greater than or equal":{
			return " >= ";
		}
		case "Less than":{
			return " < ";
		}
		case "Less than or equal":{
			return " <= ";
		}
		default:{
			return "";
		}
		}
	}
	
	public String fieldLookup(ComboBox<String> table1, String s){
		if(table1.getSelectionModel().getSelectedItem() == "Conference"){
			switch(s){
			case "Conference Code":{
				return "code";
			}
			case "Conference Name":{
				return "name";
			}
			case "Subdivision":{
				return "subdivision";
			}
			case "Year":{
				return "year"; 
			}
			default: return "";
		}
		}
		else if(table1.getSelectionModel().getSelectedItem() == "Team"){
			switch(s){
			case "Team Code":{
				return "code";
			}
			case "Conference Code":{
				return "conference_code";
			}
			case "Team Name":{
				return "name";
			}
			case "Year":{
				return "year";
			}
			default: return "";
			}
		}
		else if(table1.getSelectionModel().getSelectedItem() == "Game"){
			switch(s){
			case "Game Code":{
				return "game_code";
			}
			case "Home Code":{
				return "home_code";
			}
			case "Visitor Code":{
				return "visitor_code";
			}
			case "Stadium Code":{
				return "stadium_code";
			}
			case "Site":{
				return "site";
			}
			case "Day":{
				return "day";
			}
			case "Year":{
				return "year";
			}
			default: return "";
			}
		}
		else if(table1.getSelectionModel().getSelectedItem() == "Player"){
			switch(s){
			case "Player Code":{
				return "player_code";
			}
			case "Team Code":{
				return "team_code";
			}
			case "Last Name":{
				return "last_name";
			}
			case "First Name":{
				return "first_name";
			}
			case "Number":{
				return "number";
			}
			case "Class":{
				return "class";
			}
			case "Position":{
				return "position";
			}
			case "Height":{
				return "height";
			}
			case "Weight":{
				return "weight";
			}
			case "Hometown":{
				return "hometown";
			}
			case "Home State":{
				return "home_state";
			}
			case "Home Country":{
				return "home_country";
			}
			case "Last School":{
				return "last_school";
			}
			case "Year":{
				return "year";
			}
			default: return "";
			}
		}
		else return "";
	}
	
	private void queryDatabase(){
		if(table1.getSelectionModel().getSelectedItem() == null){
			setNoQueryPrompt();
			return;
		}
		String query = "";
		if(preventDups.isSelected()){
			query = "SELECT DISTINCT ";
		}
		else{
			query = "SELECT ";
		}
		
		for(int i=0;i<table1Get.size();++i){
			String temp = fieldLookup(table1,table1Get.get(i));
			if(i==0){
				query+=(table1.getSelectionModel().getSelectedItem()+"."+temp);
			}
			else{
				query += (","+table1.getSelectionModel().getSelectedItem()+"."+temp);
			}
		}
		query += (" FROM "+table1.getSelectionModel().getSelectedItem());
		
		//join part of query building
		if(table2.getSelectionModel().getSelectedItem() != null){
			query+=(" INNER JOIN "+table2.getSelectionModel().getSelectedItem()+" ON ");
			String temp = fieldLookup(table1,table2Get.get(0));
			String temp1 = fieldLookup(table2,table2Get.get(0));
			query+=(table1.getSelectionModel().getSelectedItem()+"."+temp+" = "+table2.getSelectionModel().getSelectedItem()+"."+temp1);
		}
		
		if(showConditions.getChildren().size()>0){
			query+=(" WHERE ");
		}

		for(int i=0;i<showConditions.getChildren().size();++i){
			Node node = showConditions.getChildren().get(i);
			if (node instanceof Text) {
				if(i==0){
					query+=(((Text) node).getText());
				}
				else{
					query+=(" AND "+(((Text) node).getText()));
				}
			}
		}
		
		query+=";";
		
		System.out.println(query);
		builtQuery = query;
		outBox.getChildren().clear();
		
		buttonActive.setVisible(false);
		buttonClicked.setVisible(true);
		queryButton.setDisable(true);
		//loading icon idea from https://codepen.io/scootman/pen/NMZRNa
		loadingIcon.setVisible(true);
		
		Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
            	executeQuery();
                return null;
            }
        };
        task.setOnSucceeded(taskFinishEvent -> saveResults());
        new Thread(task).start();
	}
	
	public void saveResults(){
		Color text_color = Color.web("#FFF");
		try{	
			ResultSetMetaData resultSetMetaData = queryResults.getMetaData();
			final int columnCount = resultSetMetaData.getColumnCount();
		    if(saveToFile){
		    	String fileLocationSaved="";
		    	PrintWriter out;
		    	if(filePathString.getText().trim().isEmpty()){
		    		fileLocationSaved = filePathChooser.getAbsolutePath()+"\\QueryResults.txt";
		    		out = new PrintWriter(new FileWriter(filePathChooser.getAbsolutePath()+"\\QueryResults.txt", false));
		    	}
		    	else{
		    		fileLocationSaved = filePathString.getText()+"\\QueryResults.txt";
		    		out = new PrintWriter(new FileWriter(filePathString.getText()+"\\QueryResults.txt", false));
		    	}
		    	
		    	for (int i = 1; i <= columnCount; i++) {
	    	    	if(i==1)out.print(resultSetMetaData.getColumnLabel(i));
	    	    	else out.print(":"+resultSetMetaData.getColumnLabel(i));
	    	    }
		    	out.print("\n");
		    	while (queryResults.next()) {
		    	    for (int i = 1; i <= columnCount; i++) {
		    	    	if(i==1)out.print(queryResults.getObject(i));
		    	    	else out.print(":"+queryResults.getObject(i));
		    	    }
		    	    out.print("\n");
		    	}
		    	out.close();
		    	String fileSaved = "File was successfully saved to "+fileLocationSaved;
		    	Text text_2 = new Text(fileSaved); 
	            text_2.setFill(text_color); 
	            text_2.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
	            outBox.getChildren().add(text_2); 
	    	}
	    	else{
	    		String temp_str = "";
	    		int counter = 0;
	    		for (int i = 1; i <= columnCount; i++) {
	    	    	if(i==1)temp_str+=(resultSetMetaData.getColumnLabel(i));
	    	    	else temp_str+=(":"+resultSetMetaData.getColumnLabel(i));
	    	    }
	    		temp_str+=("\n");
	    		Text text_2 = new Text(temp_str); 
	            text_2.setFill(text_color); 
	            text_2.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
	            outBox.getChildren().add(text_2); 
	            temp_str = "";
	            
	            while (queryResults.next()) {
	            	if(counter<5){
	            		for (int i = 1; i <= columnCount; i++) {
			    	    	if(i==1)temp_str+=(queryResults.getObject(i));
			    	    	else temp_str+=(":"+queryResults.getObject(i));
			    	    }
			    	    temp_str+="\n";
			    	    Text text_3 = new Text(temp_str); 
			    	    text_3.setFill(text_color); 
			    	    text_3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
			            outBox.getChildren().add(text_3); 
			            temp_str = "";
	            	}
		            counter++;
		    	}
	            if(counter >= 10){
	            	String end_message = "+ "+counter+" more entries.";
	            	Text text_3 = new Text(end_message); 
	            	text_3.setFill(text_color); 
	            	text_3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
		            outBox.getChildren().add(text_3); 
	            }
	    	}
		    
		} 
		catch (Exception e){
		   System.out.println("Error accessing Database.");
		   outBox.getChildren().clear();
		   String error_message = "Error accessing Database.";
		   Text text_3 = new Text(error_message); 
		   text_3.setFill(text_color); 
		   text_3.setFont(Font.font("Helvetica", FontPosture.ITALIC, 15)); 
	       outBox.getChildren().add(text_3);
		}
		buttonActive.setVisible(true);
		buttonClicked.setVisible(false);
		queryButton.setDisable(false);
		loadingIcon.setVisible(false);
	}
	
	public void executeQuery(){
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
		    conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/team903_10_cfb",
		    		Main.login.user, Main.login.pswd);
		} 
		catch (Exception e) {
		      e.printStackTrace();
		      System.err.println(e.getClass().getName()+": "+e.getMessage());
		      //reset buttons
		}
		try{
			Statement stmt = conn.createStatement();
		    String sqlStatement = builtQuery;
		    ResultSet result = stmt.executeQuery(sqlStatement);
		    queryResults = result;
		}
		catch(Exception e){
			
		}
		try {
		    conn.close();
		    System.out.println("Connection Closed.");
		} 
		catch(Exception e) {
		    System.out.println("Connection NOT Closed.");
		}
	}
	
}
