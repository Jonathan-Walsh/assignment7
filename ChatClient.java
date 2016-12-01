/* CHATROOM <ChatClient.java>
 * EE422C Project 7 submission by
 * Replace <...> with your actual data.
 * Jonathan Walsh
 * jlw4699
 * 16450
 * Tim Yoder
 * tjy263
 * 16450
 * Slip days used: <1>
 * Fall 2016
 */
package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.*;
import java.awt.event.*;
import javafx.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChatClient extends Application {
	private TextField incoming;
	private TextField privateText;
	private TextArea outgoing;
	private BufferedReader reader;
	private ClientObserver writer;
	private AnchorPane anchorPane;
	private Label currentUser;
	private TabPane tabPane;
	private String userName;
	private ArrayList<Node> components;
	private ListView<String> userList;
	private HashSet<String> userNames;
	
	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		userNames = new HashSet<String>();
		setUpNetworking();
		initView();
		primaryStage.setScene(new Scene(anchorPane, 1000, 650));
		primaryStage.show();
		usernameEntry();
		primaryStage.setOnCloseRequest(
				event -> {
					System.exit(0);
				}
				);
	}
	
	private void initView() {
		components = new ArrayList<Node>();
		anchorPane = new AnchorPane();
		
		tabPane = new TabPane();
		components.add(tabPane);
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		Tab publicTab = new Tab();
		publicTab.setText("Public");
		tabPane.getTabs().add(publicTab);
		Tab privateTab = new Tab();
		privateTab.setText("Private");
		tabPane.getTabs().add(privateTab);
		
		incoming = new TextField();
		incoming.setPrefWidth(250.0);
		incoming.setPromptText("Enter message here to send to everyone");
		components.add(incoming);
		incoming.setOnAction(e -> {
			String s = incoming.getText();
    		s= userName+ ": "+s;
    		writer.println(s);
    		writer.flush();
    		incoming.clear();
    		anchorPane.getChildren().clear();
    		anchorPane.getChildren().addAll(components);
		});
		
		privateText = new TextField();
		privateText.setPrefWidth(210.0);
		privateText.setPromptText("Select a user from above");
		components.add(privateText);
		privateText.setOnAction(e -> {
			sendPrivateMessage();
		});
		
		TextArea publicChat = new TextArea();
		publicChat.setWrapText(true);
		publicChat.setPrefSize(200, 600);
		publicTab.setContent(publicChat);
		TextArea privateChat = new TextArea();
		privateChat.setWrapText(true);
		privateTab.setContent(privateChat);
		
		userList = new ListView<String>();
		components.add(userList);
		//userList.setItems((ObservableList<String>) ChatServer.getUsernames());
		
		Label userListLabel = new Label();
		userListLabel.setText("Users Online");
		components.add(userListLabel);

		currentUser = new Label();
		
		components.add(currentUser);
		
		Button sendButton = new Button();
		sendButton.setText("Send Message");
		components.add(sendButton);
		sendButton.setOnAction(new EventHandler<ActionEvent>(){
	    	public void handle(ActionEvent event){
	    		String s = incoming.getText();
	    		s= userName+ ": "+s;
	    		writer.println(s);
	    		writer.flush();
	    		incoming.clear();
	    		anchorPane.getChildren().clear();
	    		anchorPane.getChildren().addAll(components);
	    	}
	    });
		
		Button sendPrivateButton = new Button();
		sendPrivateButton.setText("Send Private Message");
		components.add(sendPrivateButton);
		sendPrivateButton.setOnAction(new EventHandler<ActionEvent>(){
	    	public void handle(ActionEvent event){
	    		sendPrivateMessage();
	    	}
	    });
		
		anchorPane.getChildren().addAll(components);
		AnchorPane.setBottomAnchor(incoming, 100.0);
		AnchorPane.setRightAnchor(incoming, 150.0);
		AnchorPane.setBottomAnchor(sendButton, 100.0);
		AnchorPane.setRightAnchor(sendButton, 50.0);
		AnchorPane.setBottomAnchor(sendPrivateButton, 160.0);
		AnchorPane.setRightAnchor(sendPrivateButton, 50.0);
		AnchorPane.setBottomAnchor(userList, 200.0);
		AnchorPane.setRightAnchor(userList, 50.0);
		AnchorPane.setBottomAnchor(userListLabel, 600.0);
		AnchorPane.setRightAnchor(userListLabel, 50.0);
		AnchorPane.setBottomAnchor(currentUser, 625.0);
		AnchorPane.setRightAnchor(currentUser, 700.0);
		AnchorPane.setBottomAnchor(privateText, 160.0);
		AnchorPane.setRightAnchor(privateText, 190.0);
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4243);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		OutputStream outStream = sock.getOutputStream();
		reader = new BufferedReader(streamReader);
		writer = new ClientObserver(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				
				while ((message = reader.readLine()) != null) {
					if (!message.startsWith("NAME:") && !message.startsWith("ADDNAME:") && !message.startsWith("REMOVE:")) {
						Tab currentTab = tabPane.getTabs().get(0);
						if (message.startsWith("Private")) {
							currentTab = tabPane.getTabs().get(1);
						}
						outgoing = (TextArea) currentTab.getContent();
						if (outgoing instanceof TextArea) {
							outgoing.appendText(message);
							outgoing.appendText("\n");
						}
					}
					else if (message.startsWith("NAME:")) {
						if (!userNames.contains(message)) {
							userNames.add(message);
						}
						if (!userList.getItems().contains(message.substring(5))) {
							userList.getItems().add(message.substring(5));
						}
					}
					else if (message.startsWith("ADDNAME:")) {
						if (!userNames.contains(message)) {
							userNames.add(message.substring(3));
						}
					}
					else if (message.startsWith("REMOVE:")) {
						String closedUser = message.substring(7);
						outgoing = (TextArea) tabPane.getTabs().get(0).getContent();
						outgoing.appendText(closedUser + " has disconnected from the chat\n");
						if (userNames.contains("NAME:" + closedUser)) {
							userNames.remove("NAME:" + closedUser);
						}
						int len = userList.getItems().size();
						for (int i = 0; i < len; i++) {
							userList.getItems().set(i, "");
						}
						userList.getItems().clear();
						for (String name: userNames) {
							userList.getItems().add(name.substring(5));
						}
					}
				}
			} catch (IOException ex) {
			}
			catch (IllegalStateException ex2) {
				
			}
		}
	}
	
	
	private void usernameEntry() {
		final Stage stage = new Stage();
		Button btn4 = new Button();
		btn4.setText("Enter");

		stage.initModality(Modality.APPLICATION_MODAL);
		Text text = new Text(10, 40, "Create a username:");
		text.setFont(new Font(18));
		TextField inputUsername = new TextField();
    	Scene scene = new Scene(new Group(inputUsername,text));
    	stage.setTitle("Username Entry");  
    	btn4.setOnAction(
	        new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	userName = inputUsername.getText();
	            	if (userName.length() == 0) {
	            		inputUsername.clear();
	            		inputUsername.setPromptText("At least 1 character");
	            	}
	            	else if (userName.length() > 15) {
	            		inputUsername.clear();
	            		inputUsername.setPromptText("Less than 16 characters");
	            	}
	            	else if (!userNames.contains("NAME:" + userName)) {	
	            	stage.close();
	            	writer.update(null, userName);
	            	writer.update(null, userName + " has connected to the chat");
	            	currentUser.setText("You are signed in as: " + userName);
	            	}
	            	else {
	            		inputUsername.clear();
	            		inputUsername.setPromptText("Username already in use");
	            	}
	            	
	            }
	       });
    	
    	stage.setOnCloseRequest(
    			event -> {
    				System.exit(0);
    			}
    		);
    	final GridPane inputGridPane = new GridPane();
    
    	GridPane.setConstraints(inputUsername, 1, 0);
    	GridPane.setConstraints(text, 0, 0);
    	GridPane.setConstraints(btn4,2,0);
    	inputGridPane.setHgap(6);
    	inputGridPane.setVgap(6);
    	inputGridPane.getChildren().addAll(inputUsername, text,btn4);

    	final Pane rootGroup = new VBox(12);
    	rootGroup.getChildren().addAll(inputGridPane);
    	rootGroup.setPadding(new Insets(12, 12, 12, 12));
    	stage.setScene(new Scene(rootGroup));
    	stage.show();

	}
	
	private void sendPrivateMessage() {
		if (userList.getSelectionModel().isEmpty()) {
			privateText.clear();
			privateText.setPromptText("Must select a user");
		}
		else {
			String s = privateText.getText();
			String receiver = userList.getSelectionModel().getSelectedItem();
			String rLen = Integer.toString(receiver.length());
			String sender = userName;
			String sLen = Integer.toString(sender.length());
			s= "PRIVATE:" + sLen + "," + rLen + "," + sender + receiver +s;
			writer.println(s);
			writer.flush();
			privateText.clear();
			anchorPane.getChildren().clear();
			anchorPane.getChildren().addAll(components);
		}
	}
	
}
