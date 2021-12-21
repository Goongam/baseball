package server2;

import java.util.ArrayList;

public class GameRoom extends Thread {
	// ArrayList<PrintWriter> user_list = new ArrayList<PrintWriter>();
	ArrayList<User> userList;
	String room_name;
	int room_id;
	User player1 = null;
	User player2 = null;
	String p1_num4 = null;
	String p2_num4 = null;

	int p1_time = 120;
	int p2_time = 120;
	User turn;
	User nturn;
	
	boolean gaming = false;

	public GameRoom(int roomid, String room_name, User user) {
		this.room_name = room_name;
		this.room_id = roomid;

		userList = new ArrayList<User>();
		userList.add(user);
		player1 = user;
		System.out.println(user.getNick());
		user.enterRoom(this); // 방을 만든사람은 자동으로 입장
		// user.isRoomEnter = true;

		// System.out.println("방 접속인원 : "+userList.size());
		// System.out.println("방 처음접속자 닉네임 : "+userList.get(0).getNick());
	}

	public void ready() {
		if (player1 != null && player2 != null) {
			if (player1.isready() && player2.isready()) {
				SendMessage("/gamestart");
			}
		}
	}

	public void inputNum4(String num4, User user) {
		if(!gaming) { //게임중x 초기 num4설정& 시작
			if (user == player1)
				p1_num4 = num4;
			else
				p2_num4 = num4;
			
			// 게임시작
			if (p1_num4 != null && p2_num4 != null) {
				System.out.println("p1:"+p1_num4+"   p2:"+p2_num4);
				SendMessage("/Go");
				start();
			}
		}else { //게임중
			if(turn.equals(user)) {
				
				String compare = "";
				if(turn.equals(player1)) {
				
					CompareNum4(num4, p2_num4);
				}
				else {
					CompareNum4(num4, p1_num4);
					
				}
				
				
			//	turn.sendUserMessage("/mycompare"+compare);
			//	nturn.sendUserMessage("/oppocompare"+compare);
				swapTurn();
			}	
		}
	
	}
	public void swapTurn() {
		User tmp;
		
		tmp = turn;
		turn = nturn;
		nturn = tmp;
		p1_time = 120;
		p2_time = 120;
		//turn.sendUserMessage("/YourTurn");
		//nturn.sendUserMessage("/OpponentTurn");
		System.out.println("현재턴:"+turn.getNick());
	}
	public String CompareNum4(String mynum4, String opponum4) {
		int Strike = 0, ball = 0;
		String str = "";
		// 검사
		for (int i = 0; i < opponum4.length(); i++) {
			for (int j = 0; j < mynum4.length(); j++) {

				if (opponum4.charAt(i) == mynum4.charAt(j)) {
					if (i == j) {
						Strike++;
					} else {
						ball++;
					}
				}
			}
		}
		// 문자 합치기
		if (Strike == 0 && ball == 0)
			str = "OUT!";
		if (Strike != 0)
			str += (Strike + "S");
		if (ball != 0)
			str += (ball + "B");

		if (str.equals("4S")) {
			SendMessage("/gameover");
			exitUserAll();
		}
		
		turn.sendUserMessage("/mycompare"+mynum4+"    "+str);
		nturn.sendUserMessage("/oppocompare"+mynum4+"    "+str);
		
		return str;
	}
	public void enterUser(User user) {
		userList.add(user);
		SendMessage(user.getNick() + "님이 방에 입장하였습니다");

		if (player1 == null) {
			player1 = user;
		} else {
			player2 = user;
		}
	}

	public void exitUser(User user) {
		user.exitRoom(this);
		userList.remove(user);

		if (userList.size() == 0)
			RoomManager.DeleteRoom(room_name);

		if (player1 == user) {// 퇴장한 유저가 player1 이면
			player1 = null;
		} else {
			player2 = null;
		}

		user.sendUserMessage("/exitDone");
	}

	public void exitUserAll() {
		for (int i = 0; i < userList.size(); i++) {
			exitUser(userList.get(i));
		}
	}

	// 유저 채팅
	public void SendMessage(String nick, String msg) {
		for (int i = 0; i < userList.size(); i++) {
			System.out.println(room_id + "번방 에서 [" + msg + "] 보냄" + ", 닉네임 : " + nick);

			userList.get(i).printwriter.println(nick + ">" + msg);
			userList.get(i).printwriter.flush();

		}
	}

	// 방 알림
	public void SendMessage(String msg) {
		for (int i = 0; i < userList.size(); i++) {
			System.out.println(room_id + "번방 에서 [" + msg + "] 보냄");

			userList.get(i).printwriter.println(msg);
			userList.get(i).printwriter.flush();

		}
	}

	// Timer
	@Override
	public void run() {
		gaming = true;
		turn = player1;
		nturn = player2;
		try {
			while (gaming) {
				sleep(1000);

				if (turn.equals(player1))
					--p1_time; 
				else
					--p2_time; 
				
				player1.sendUserMessage("/Time@"+p1_time+"@"+p2_time);
				player2.sendUserMessage("/Time@"+p2_time+"@"+p1_time);
								
				if (p1_time == 0) {
					swapTurn();
				} else if (p2_time == 0) {
					swapTurn();
				}
				
				//System.out.println("p1time :"+p1_time+"   p2time :"+p2_time);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
