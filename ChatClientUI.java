
package assignment7;

import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.*;


public class ChatClientUI extends Application{
	
	

	public void launchClient() {
		launch();
	}
	
	 public static AnchorPane anchorPane = new AnchorPane();
	 public ArrayList<String> messages = new ArrayList<String>();
	 public String userName;
	 public Label outputText;
	 @SuppressWarnings("static-access")
	 
	public void start(Stage primaryStage) {
		//anchorPane.setStyle("-fx-background-color: #cf5300");

		ArrayList<Node> components = new ArrayList<Node>();
		
		final Popup popup = new Popup();
        popup.setX(360);
        popup.setY(650);
        final Popup popup2 = new Popup();
        popup2.setX(360);
        popup2.setY(625);
        final Popup popup3 = new Popup();
        popup3.setX(600);
        popup3.setY(650);
        final Popup popup4 = new Popup();
        popup4.setX(600);
        popup4.setY(675);
        
		TextField inputUsername = new TextField();
		inputUsername.setMinWidth(200);
		inputUsername.setPrefWidth(225);
		inputUsername.setMaxWidth(250);
		Label inputUsernamePrompt = new Label();
		inputUsernamePrompt.setText("Enter your new Username here:");
		inputUsernamePrompt.setMinWidth(200);
		inputUsernamePrompt.setPrefWidth(225);
		inputUsernamePrompt.setMaxWidth(400);
		inputUsernamePrompt.setTextFill(Color.BLACK);
		Button inputUsernameBtn = new Button();
        inputUsernameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               userName = inputUsername.getText();
            }
        });   
		inputUsernameBtn.setText("Enter");

        Button hide = new Button("Exit and begin messaging");
        popup.getContent().addAll(inputUsername);
        popup2.getContent().addAll(inputUsernamePrompt);
        popup3.getContent().addAll(inputUsernameBtn);
        popup4.getContent().addAll(hide);
        Button show = new Button("Enter your current Username");
        show.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.show(primaryStage);
                popup2.show(primaryStage);
                popup3.show(primaryStage);
                popup4.show(primaryStage);
            }
        });    
        hide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.hide();
                popup2.hide();
                popup3.hide();
                popup4.hide();
            }
        });
		
		Button sendBtn = new Button();
		sendBtn.setText("Send");
		
		Label inputTextPrompt = new Label();
		inputTextPrompt.setText("Type to your friends here:");
		inputTextPrompt.setMinWidth(50);
		inputTextPrompt.setPrefWidth(225);
		inputTextPrompt.setMaxWidth(400);
		inputTextPrompt.setTextFill(Color.BLACK);
		outputText = new Label();
		outputText.setTextFill(Color.BLACK);
		TextField inputText = new TextField();
		inputText.setMinWidth(500);
		inputText.setPrefWidth(525);
		inputText.setMaxWidth(550);
		components.add(inputText);
		components.add(inputTextPrompt);
		components.add(sendBtn);
		components.add(outputText);
		components.add(show);
		//components.add(hide);
		
		sendBtn.setOnAction(new EventHandler<ActionEvent>(){
	    	public void handle(ActionEvent event){
	    		String s = inputText.getText();
	    		s=userName+ ": "+s;
	    		anchorPane.getChildren().clear();
	    		anchorPane.getChildren().addAll(components);
	    		
	    	}
	    });
		
	anchorPane.setBottomAnchor(outputText,300.0);
	anchorPane.setLeftAnchor(outputText, 50.0);
    anchorPane.setBottomAnchor(inputText, 200.0);
    anchorPane.setRightAnchor(inputText,400.0);
    anchorPane.setBottomAnchor(inputTextPrompt, 225.0);
    anchorPane.setRightAnchor(inputTextPrompt,700.0);
    anchorPane.setBottomAnchor(sendBtn, 200.0);
    anchorPane.setRightAnchor(sendBtn,200.0);
    // Show and hide for Pop up
    anchorPane.setBottomAnchor(show, 150.0);
    anchorPane.setRightAnchor(show,200.0);
//    anchorPane.setBottomAnchor(hide, 100.0);
//    anchorPane.setRightAnchor(hide,200.0);

    	anchorPane.setStyle("-fx-background-color: cornsilk; -fx-padding: 10;");
	    anchorPane.getChildren().addAll(components);
		primaryStage.setScene(new Scene(anchorPane, 1000, 650));
		primaryStage.show();
		   
		   
	 }

	 public void printText(String message) {
		 messages.add(message);
		 outputText.setText(message);
	 }
	 
}
