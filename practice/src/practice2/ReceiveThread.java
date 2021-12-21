package practice2;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JList;
import javax.swing.JScrollPane;

import practice2.NumberBaseball.MultiGamePanel;
import practice2.NumberBaseball.MultiGameStart;

public class ReceiveThread extends Thread{
	Socket m_socket;
	MultiGamePanel mp;
	MultiGameStart mgs;
	
	public void run() {
		try {
			BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
			
			String receiveString;
			String split[];
			
			while(true) {
				receiveString = tmpbuf.readLine();
				
				System.out.println("메시지 받음 : "+receiveString );
				
				if(receiveString.equals("/joinDone")) {
					mp.ViewJoinRoom();
					continue;
				}
				else if(receiveString.equals("/gamestart")) {
					mp.Gamestartsetting();
					continue;
				}
				else if(receiveString.equals("/exitDone")) {
					mp.ViewRoomlist();
				}
				else if(receiveString.split("altcnw")[0].equals("refresh")) {
					split = receiveString.split("refreshaltcnw");
					split = split[1].split("#");

					mp.Refresh(split);
					System.out.println(mp.joinRoomList.getModel().getSize());
					continue;
				}
				else if(receiveString.startsWith("/WaitOpponent")) {
					mgs.inputNumArea.setText("상대방을 기다리는 중...");
				}
				else if(receiveString.startsWith("/Go")) {
					mgs.inputNumArea.setVisible(false);
					mgs.makeFieldAndBack();
					mgs.MakeTimer();
				}
				else if(receiveString.startsWith("/YourTurn")) {
					System.out.println("MY TURN");
				}
				else if(receiveString.startsWith("/OpponentTurn")) {
					System.out.println("YOUR TURN");
				}
				else if(receiveString.startsWith("/mycompare")) {
					String mynum4comp = receiveString.substring(10, receiveString.length()); 
					System.out.println("my : "+mynum4comp);
					mgs.resultLabel2.setText(mgs.resultLabel2.getText()+"\n"
							 + mynum4comp);
				}
				else if(receiveString.startsWith("/oppocompare")) {
					String opponum4comp = receiveString.substring(12, receiveString.length());
					System.out.println("oppo :"+ opponum4comp);
					mgs.resultLabel.setText(mgs.resultLabel.getText()+"\n"
							 + opponum4comp);
				}
				else if(receiveString.startsWith("/Time@")) {
					String[] time = receiveString.split("@");
					mgs.timermy.setText("Time:"+time[1]);
					mgs.timeroppo.setText("Time:"+time[2]);
				}
				else if(receiveString.startsWith("/gameover")) {
					mgs.gameover();
				}
				
				
				//SendThread.area.append(receiveString + "\n");
			//	SendThread.scroll.getVerticalScrollBar().setValue(SendThread.scroll.getVerticalScrollBar().getMaximum());

			}
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("오류 : "+e.getStackTrace());
		}
		
		
	}

	public void setSocket(Socket c_socket, MultiGamePanel mp) {	
		m_socket = c_socket;
		this.mp = mp;
	}
	public void setSocket(MultiGameStart mgs) {
		this.mgs = mgs;
	}

}
