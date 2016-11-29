package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.event.*;
import javafx.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ChatClient extends Application {
	private TextField incoming;
	private TextArea outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	private AnchorPane anchorPane;
	private TabPane tabPane;
	private String userName;
	private ArrayList<Node> components;

	public static void main(String[] args) {
		try {
			new ChatClient().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() throws Exception {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		setUpNetworking();
		initView();
		primaryStage.setScene(new Scene(anchorPane, 1000, 650));
		primaryStage.show();
		writer.println(userName + " has connected to the chat");
		writer.flush();
	}
	
	private void initView() {
		
		
		
		components = new ArrayList<Node>();
		anchorPane = new AnchorPane();
		
		tabPane = new TabPane();
		components.add(tabPane);
		
		Tab publicTab = new Tab();
		publicTab.setText("Public");
		tabPane.getTabs().add(publicTab);
		Tab privateTab = new Tab();
		privateTab.setText("Private");
		tabPane.getTabs().add(privateTab);
		
		incoming = new TextField();
		components.add(incoming);

		publicTab.setContent(new TextArea());
		privateTab.setContent(new TextArea());
		
		userName = "Tim";
		
		Button sendButton = new Button();
		components.add(sendButton);
		sendButton.setOnAction(new EventHandler<ActionEvent>(){
	    	public void handle(ActionEvent event){
	    		String s = incoming.getText();
	    		s= userName+ ": "+s;
	    		writer.println(s);
	    		writer.flush();
	    		anchorPane.getChildren().clear();
	    		anchorPane.getChildren().addAll(components);
	    	}
	    });
		
		anchorPane.getChildren().addAll(components);
		AnchorPane.setBottomAnchor(incoming, 100.0);
		AnchorPane.setRightAnchor(incoming, 100.0);
		AnchorPane.setBottomAnchor(sendButton, 200.0);
		AnchorPane.setRightAnchor(sendButton, 200.0);
		
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4242);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
						Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
						outgoing = (TextArea) currentTab.getContent();
						if (outgoing instanceof TextArea) {
							outgoing.appendText(message);
							outgoing.appendText("\n");
						}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}


}
