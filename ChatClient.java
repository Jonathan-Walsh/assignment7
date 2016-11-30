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
	private TextArea outgoing;
	private BufferedReader reader;
	private ClientObserver writer;
	private AnchorPane anchorPane;
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
		incoming.setPrefWidth(300.0);
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

		Button sendButton = new Button();
		sendButton.setText("Send");
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
		sendPrivateButton.setText("Send Private");
		components.add(sendPrivateButton);
		sendPrivateButton.setOnAction(new EventHandler<ActionEvent>(){
	    	public void handle(ActionEvent event){
	    		sendPrivateMessage();
	    	}
	    });
		
		anchorPane.getChildren().addAll(components);
		AnchorPane.setBottomAnchor(incoming, 100.0);
		AnchorPane.setRightAnchor(incoming, 100.0);
		AnchorPane.setBottomAnchor(sendButton, 100.0);
		AnchorPane.setRightAnchor(sendButton, 50.0);
		AnchorPane.setBottomAnchor(sendPrivateButton, 175.0);
		AnchorPane.setRightAnchor(sendPrivateButton, 50.0);
		AnchorPane.setBottomAnchor(userList, 200.0);
		AnchorPane.setRightAnchor(userList, 50.0);
		AnchorPane.setBottomAnchor(userListLabel, 600.0);
		AnchorPane.setRightAnchor(userListLabel, 50.0);
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
					if (!message.startsWith("NAME:") && !message.startsWith("ADDNAME:")) {
						Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
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
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	private void usernameEntry() {
		final Stage stage = new Stage();
		Button btn4 = new Button();
		btn4.setText("Enter");

		stage.initModality(Modality.APPLICATION_MODAL);
		Text text = new Text(10, 40, "Enter your new Username here:");
		text.setFont(new Font(18));
		TextField inputUsername = new TextField();
    	Scene scene = new Scene(new Group(inputUsername,text));
    	stage.setTitle("Username Entry");  
    	btn4.setOnAction(
	        new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	System.out.println(Arrays.toString(userNames.toArray()));
	            	userName = inputUsername.getText();
	            	if (!userNames.contains("NAME:" + userName)) {
	            	stage.close();
	            	writer.update(null, userName);
	            	writer.update(null, userName + " has connected to the chat");
	            	}
	            	else {
	            		inputUsername.clear();
	            		inputUsername.setPromptText("Username already in use");
	            	}
	            	
	            }
	       });
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
			incoming.clear();
			incoming.setPromptText("Select a user to private message first");
		}
		else {
			String s = incoming.getText();
			String receiver = userList.getSelectionModel().getSelectedItem();
			String rLen = Integer.toString(receiver.length());
			String sender = userName;
			String sLen = Integer.toString(sender.length());
			s= "PRIVATE:" + sLen + "," + rLen + "," + sender + receiver +s;
			writer.println(s);
			writer.flush();
			incoming.clear();
			anchorPane.getChildren().clear();
			anchorPane.getChildren().addAll(components);
		}
	}
	
}
