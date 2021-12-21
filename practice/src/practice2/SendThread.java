package practice2;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Painter;
import javax.swing.RepaintManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import practice2.NumberBaseball.MultiGamePanel;
 
public class SendThread extends JFrame{

	Socket m_socket;
	PrintWriter sendWriter ;
	
	boolean isfirst = true;
	MultiGamePanel bb;
	
	
	public void sendEnterGame(String nickname) {
		sendWriter.println("/Nickaltcnt"+nickname);
		sendWriter.flush();
	}

	public void joinRoomRefresh(String[] split) {
	/*
		joinScroll.remove(joinRoomList);
		joinRoomList = new JList<String>(split);
		RefreshEvent s = new RefreshEvent();
		joinRoomList.addListSelectionListener(s);
		joinScroll.setViewportView(joinRoomList);
		
		joinRoomframe.revalidate();
		joinRoomframe.repaint();
		*/
	}
/*
	class RefreshEvent implements ListSelectionListener{

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) {
				String[] split = joinRoomList.getSelectedValue().split(" ");
				sendmsg = "/join "+ split[0]; //[0]아이디
				System.out.println(sendmsg);
				send();
				createRoomBtn.setVisible(false);
				exitRoom.setVisible(true);
				joinRoomBtn.setVisible(false);
				joinRoomframe.dispose();
			}
			
		}
		
	}
	*/
	void send(String sendmsg) {
		try {
			
			//exit
			if (sendmsg.equals("exit")) {
				sendWriter.close();
				m_socket.close();
			}
			
			//이후 채팅
			sendWriter.println(sendmsg);
			sendWriter.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSocket(Socket c_socket, MultiGamePanel bb) {
		m_socket = c_socket;
		this.bb = bb;
		
		try {
			sendWriter = new PrintWriter(m_socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	
}
