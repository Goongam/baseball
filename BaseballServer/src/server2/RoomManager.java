package server2;

import java.net.Socket;
import java.util.ArrayList;

public class RoomManager extends Thread {
	Socket m_socket;
	String m_id;
	User m_user;
	public static ArrayList<GameRoom> roomlist = new ArrayList<GameRoom>();
	public static int roomNum = 1;
	
	static public void DeleteRoom(String roomName) {
		for (int i = 0; i < roomlist.size(); i++) {
			if(roomlist.get(i).room_name.equals(roomName)) {
				roomlist.get(i).exitUserAll();
				roomlist.get(i).gaming = false;
				roomlist.remove(roomlist.get(i));
			}
		}
		System.out.println(roomName+"���� �Ϸ�, �����ϴ� �� �� : "+roomlist.size());
	}
	
	public static void createRoom(String roomName, User userOwner) {

		roomlist.add(new GameRoom(roomNum, roomName, userOwner));
		roomNum++;
		userOwner.getRoom().SendMessage(userOwner.getNick()+"���� �濡 �����Ͽ����ϴ�.");
		System.out.println("[" + roomNum + "] " + roomName + " �� �� �߰��Ǿ���.");
	}
	
	public static void joinRoom(int roomid, User user) {
		for(int i =0; i < roomlist.size();i ++) {
			if(roomlist.get(i).room_id == roomid) {
				if(2 <= roomlist.get(i).userList.size()) {
					user.sendUserMessage("/�ο��ʰ�");
					break;
				} 
					
				roomlist.get(i).enterUser(user);
				user.enterRoom(roomlist.get(i));
				System.out.println(user.getNick()+"����" + roomlist.get(i).room_name +"�� �����Ͽ����ϴ�.");
				
			}
		}
	}
	
	
	/*
	public void run() {
		try {
			BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
			String text;

			while (true) {
				
				text = tmpbuf.readLine();
				if (text == null) {
					System.out.println(m_id + "��(��) �������ϴ�.");
					for (int i = 0; i < ChatServer.m_OutputList.size(); i++) {
						ChatServer.m_OutputList.get(i).println(m_id + "��(��) �������ϴ�.");
						ChatServer.m_OutputList.get(i).flush();
					}
					break;
				}
				String[] split = text.split("altcnt");

				if (split.length == 2 && split[0].equals("ID")) {
					m_id = split[1];
					System.out.println(m_id + "��(��) �����Ͽ����ϴ�.");
					m_user = new User(m_id);
					m_user.setSocket(m_socket);
					m_user.start();
					
					for (int i = 0; i < ChatServer.m_OutputList.size(); i++) {
						ChatServer.m_OutputList.get(i).println(m_id + "��(��) �����Ͽ����ϴ�.");
						ChatServer.m_OutputList.get(i).flush();
					}
					continue;
				}
				
				split = text.split(" ");
				
				if(split[0].equals("/room")) {
					System.out.println(split[1]+"|  |"+split[2]);
					roomlist.add(new GameRoom(Integer.parseInt(split[1]),split[2],m_user)); 
					System.out.println("���� �����Ǿ����ϴ�.");
				}
				
				if(split[0].equals("/roomlist")) {
					System.out.println("������� : "+ roomlist.size());
				}
				
				
				for (int i = 0; i < ChatServer.m_OutputList.size(); i++) {
					ChatServer.m_OutputList.get(i).println(m_id + ">"+text);
					ChatServer.m_OutputList.get(i).flush();
					System.out.println(m_id + ">"+text);
				}
				
			}
			
			ChatServer.m_OutputList.remove(new PrintWriter(m_socket.getOutputStream()));
			m_socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	void setSocket(Socket _socket) {
		m_socket = _socket;
	}

}
