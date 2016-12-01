/* CHATROOM <UserList.java>
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

import java.util.ArrayList;

public class UserList{
	ArrayList<String> userList;
    public UserList() {
        userList = new ArrayList<String>();
    }
    
    public void addUser(String user) {
    	userList.add(user);
    }
    
    public void removeUser(String user) {
    	if (userList.contains(user)) {
    		userList.remove(user);
    	}
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
