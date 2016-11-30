package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	private ArrayList<ClientObserver> clientOutputStreams;
	private static UserList users;
	
	public static void main(String[] args) {
		try {
			new ChatServer().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();//winning
		}
	}

	private void setUpNetworking() throws Exception {
		clientOutputStreams = new ArrayList<ClientObserver>();
		users = new UserList();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4243);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			clientOutputStreams.add(writer);

			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			System.out.println("got a connection");
		}

	}

	private void notifyClients(String message) {


		for (PrintWriter writer : clientOutputStreams) {
			writer.println(message);
			writer.flush();
		}
	}

	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private PrintWriter writer;
		
		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new  PrintWriter(sock.getOutputStream(), true);
		}

		public void run() {
			
		while (true) {
                String name;
				try {
					name = reader.readLine();
					 if (name == null) {
		                    return;
		                }
		                synchronized (users) {
		                    if (!users.contains(name)) {
		                        users.addUser(name);
		                        
		                        break;
		                    }
		                }
		            }
				catch (IOException e) {
					e.printStackTrace();
				}  
			} 
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					notifyClients(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//public static void addUserName(String userName) {
		//clientUsernames.add(userName);
//	}
	
	//public static ArrayList<String> getUsernames() {
//		return clientUsernames;
	//}
	
	class UserList{
			ArrayList<String> userList;
		    public UserList() {
		        userList = new ArrayList<String>();
		    }
		    
		    public void addUser(String user) {
		    	userList.add(user);
		    }
		    
		    public boolean contains(String user) {
		    	if (userList.contains(user)) {
		    		return true;
		    	}
		    	return false;
		    }   
		    
		    public ArrayList<String> getUsers() {
		        return userList;
		    }
		}

	
}
