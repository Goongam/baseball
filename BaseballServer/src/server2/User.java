package server2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class User extends Thread {
	private int id;
	private GameRoom room = null;
	private Socket socket;
	private String nick;
	private boolean ready = false;
	public boolean isRoomEnter = false;
	public PrintWriter printwriter;

	public User(Socket socket) {
		try {

			this.socket = socket;
			printwriter = new PrintWriter(socket.getOutputStream());
			System.out.println("���� ����");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enterRoom(GameRoom room) {
		System.out.println(room.room_name + "��" + nick + "����");
		this.room = room;
		printwriter.println("/joinDone");
		printwriter.flush();
	}

	public void exitRoom(GameRoom room) {
		room.SendMessage(nick + "���� ���� �������ϴ�.");
		this.room = null;
		setIsready(false);
		// ����ó��
	}

	public void sendRoomList() {
		String msg = "";

		for (int i = 0; i < RoomManager.roomlist.size(); i++) {
			msg += RoomManager.roomlist.get(i).room_id + "     " + RoomManager.roomlist.get(i).room_name
					+ "              " + RoomManager.roomlist.get(i).userList.size() + "��    #";
		}
		System.out.println("���� : " + msg);
		printwriter.println("refreshaltcnw" + msg + "#");
		printwriter.flush();
	}

	public void sendUserMessage(String s) {
		printwriter.println(s);
		printwriter.flush();
	}

	public void run() {

		try {
			while (true) {

				BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String message = tmpbuf.readLine();
				System.out.println("����ä�� : " + message);

				// �Ҽӵ� ���� �������
				if (room != null) {
					// ���� ����
					if (message == null) {
						room.exitUser(this);
						break;
					}
					if (message.equals("������")) {
						room.exitUser(this);
						ready = false;
						continue;
					}
					if (message.equals("/ready")) {
						if (ready) {
							ready = false;
						} else {
							ready = true;
						}
						room.ready();
						continue;
					} 
					if (message.startsWith("/num4")) {
						String num4 = message.trim().substring(6, message.length());
						room.inputNum4(num4, this);
						sendUserMessage("/WaitOpponent");
						continue;
					}
					room.SendMessage(nick, message);

					// ���°��
				} else {
					if (message == null) {
						System.out.println(nick + "���� ������ ������ϴ�.");
						break;
					}
					// �г��� ����(ó������)
					String[] split = message.split("altcnt");

					if (split.length == 2 && split[0].equals("/Nick")) {
						nick = split[1];
						System.out.println(nick + "��(��) ������ �����Ͽ����ϴ�.");
						continue;
					}

					// �� �����
					split = message.split(" ");

					if (message.startsWith("/room")) {
						String roomName = message.trim().substring(6, message.length());
						RoomManager.createRoom(roomName, this);

					}

					// �� �����ϱ�
					else if (message.startsWith("/join")) {
						String roomid = message.trim().substring(6, message.length());
						int roomidInt = Integer.parseInt(roomid);
						RoomManager.joinRoom(roomidInt, this);

					} else if (message.startsWith("/refresh")) {
						sendRoomList();

					} else if (message.startsWith("/delete")) {
						String roomName = message.trim().substring(8, message.length());
						RoomManager.DeleteRoom(roomName);

					}

				}
			}
			ChatServer.userList.remove(this);
			socket.close();
		} catch (Exception e) {
			System.out.println("���� : ");
			e.printStackTrace();
			room.exitUser(this);
			ChatServer.userList.remove(this);
		}
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public GameRoom getRoom() {
		return room;
	}

	public void setRoom(GameRoom room) {
		this.room = room;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public boolean isready() {
		return ready;
	}

	public void setIsready(boolean ready) {
		this.ready = ready;
	}

}
