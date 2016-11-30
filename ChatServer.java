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
	private int currentNumOnline;
	
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
		currentNumOnline = 0;
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4243);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			clientOutputStreams.add(writer);

			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			//System.out.println("got a connection");
			 ArrayList<String> userNames = users.getUsers();
             for (String userName : userNames) {
             	writer.update(null, "ADD" + userName);
             }
			
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
		                        users.addUser("NAME:" + name);
		                        clientOutputStreams.get(currentNumOnline).userName = name;
		                        currentNumOnline += 1;
		                        ArrayList<String> userNames = users.getUsers();
		                        for (String userName : userNames) {
		                        	notifyClients(userName);
		                        }
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
				//Private message
				if (message.startsWith("PRIVATE:")) {
					System.out.println(message);
					message = message.substring(8);
					int comma = message.indexOf(",");
					int lenSender = Integer.parseInt(message.substring(0, comma));
					message = message.substring(comma + 1);
					comma = message.indexOf(",");
					int lenReceiver = Integer.parseInt(message.substring(0, comma));
					message = message.substring(comma + 1);
					String sender = message.substring(0, lenSender);
					String receiver = message.substring(lenSender, lenSender + lenReceiver);
					System.out.println(sender + " " + receiver);
					ClientObserver sendWriter = null;
					ClientObserver receiveWriter = null;
					for (ClientObserver cO : clientOutputStreams) {
						if (cO.userName.equals(sender)) {
							sendWriter = cO;
						}
						if (cO.userName.equals(receiver)) {
							receiveWriter = cO;
						}
					}
					message = message.substring(lenSender + lenReceiver);
					if (sendWriter != null && receiveWriter != null) {
						sendWriter.println("Private message to " + receiver + ": " + message);
						sendWriter.flush();
						receiveWriter.println("Private message from " + receiver + ": " + message);
						receiveWriter.flush();
					}
					
				}
				//Group message
				else {
						notifyClients(message);
					}
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
