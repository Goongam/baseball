package server2;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	
	//public static ArrayList<PrintWriter> m_OutputList;
	public static ArrayList<User> userList;
	
	public static void main(String[] args) {
		
		
		//m_OutputList = new ArrayList<PrintWriter>();
		userList = new ArrayList<User>();
		
		try {
			ServerSocket s_socket = new ServerSocket(8888);
			
			while(true) {
				Socket c_socket = s_socket.accept();
				User user = new User(c_socket);
				
				
				//m_OutputList.add(new PrintWriter(c_socket.getOutputStream()));
				userList.add(user);
				user.start();
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
