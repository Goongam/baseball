package practice2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import practice2.NumberBaseball.GamePanel;

import java.net.Socket;

class NumberBaseball extends JFrame {

	public int[] computerNum4 = new int[4];
	public int[] num4 = new int[4];
	public String str;
	public int padX = 320;
	public int padY = 380;
	public static final int SCREEN_WIDTH = 395;
	public static final int SCREEN_HEIGHT = 600;
	int mouseX, mouseY;
	public int turn = 0;
	

	Color bgColor = new Color(116, 234, 228, 0);
	Color bgColor2 = new Color(116, 234, 228);

	public NumberBaseball() {

		setTitle("숫자야구");
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.white);
		setLocationRelativeTo(null);
		add(new MainPanel());
		setVisible(true);
	}

	public void ChangeScene(String s) {
		getContentPane().removeAll();

		if (s == "singleplay") {
			getContentPane().add(new SingleGameStart());

		} else if (s == "multiplay") {
			getContentPane().add(new MultiGamePanel());

		} else if (s == "mainScene") {
			getContentPane().add(new MainPanel());

		} else if (s == "gameover") {
			getContentPane().add(new GameResultPanel());
		} else if (s == "multistart") {
			
		}

		revalidate();
		repaint();
	}
	
	public void multistart(ReceiveThread r, SendThread s) {
		getContentPane().removeAll();
		getContentPane().add(new MultiGameStart(r, s));
		revalidate();
		repaint();
	}
	
	class MainPanel extends JPanel {
		
		JButton singlePlaybutton = new JButton("싱글플레이");
		JButton multiPlaybutton = new JButton("멀티플레이");

		MainPanel() {
			setBackground(Color.yellow);
			setLayout(null);

			// 싱글버튼
			singlePlaybutton.setBounds(20, 250, 200, 100);
			singlePlaybutton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ChangeScene("singleplay");
				}
			});
			add(singlePlaybutton);

			// 멀티버튼
			multiPlaybutton.setBounds(20, 400, 200, 100);
			multiPlaybutton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ChangeScene("multiplay");
				}
			});
			add(multiPlaybutton);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		//	g.drawImage(mainImage, 0, 0, 380, 100, null);
		}

	}
	
	
	// 멀티
	class MultiGamePanel extends JPanel {
		
		String nick;
		
		JButton j = new JButton("아직 안만듦");
		JButton enterButton = new JButton("입장");
		JTextField nickText = new JTextField();
		
		//방 list frame
		JList<String> joinRoomList = new JList<String>();
		JScrollPane joinScroll = new JScrollPane(joinRoomList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JButton refreshBtn = new JButton("새로고침");
		
		//방 만들기 frame
		JButton createRoomBtn = new JButton("방만들기");
		JFrame createRoomframe = new JFrame("방만들기");
		JTextField Roomname = new JTextField();
		JButton createRoomDone = new JButton("만들기");
		
		//방입장
		JButton readybtn = new JButton("레디");
		JButton exitroombtn = new JButton("나가기");
		
		ReceiveThread r;
		SendThread s;
		public MultiGamePanel() {
			try {
				//Socket socket = new Socket("118.218.165.100", 8888);
				Socket socket = new Socket("0.0.0.0", 8888);
				r = new ReceiveThread();
				r.setSocket(socket, this);
				r.start();
				
				s = new SendThread();
				s.setSocket(socket, this);

				
				setLayout(null);
				j.setBounds(100, 100, 200, 100);
				j.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						ChangeScene("mainScene");
					}
				});
				add(j);
				
				nickText.setBounds(100, 400, 100, 30);
				add(nickText);
				
				enterButton.setBounds(300, 400, 30, 30);
				enterButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						nick = nickText.getText();
						s.sendEnterGame(nick);
						ViewRoomlist();
					}
				});
				add(enterButton);
				
				refreshBtn.setVisible(false);
				refreshBtn.setBounds(0, 0, SCREEN_WIDTH/2, 30);
				refreshBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						s.send("/refresh");
						//String[] tmp = {"1 방1    1명","3 방3    1명","2 방2    1명"};
						//joinRoomRefresh(tmp);
					}
				});
				add(refreshBtn);
				
				joinScroll.setBounds(0, 30, SCREEN_WIDTH, SCREEN_HEIGHT-30);
				joinScroll.setVisible(false);
				add(joinScroll);
				
				createRoomframe.setLocation(400,400);
				createRoomframe.setSize(100, 100);
				createRoomframe.add(Roomname,BorderLayout.NORTH);
				createRoomframe.add(createRoomDone, BorderLayout.SOUTH);
				
				createRoomDone.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {		
						s.send("/room " + Roomname.getText());
						createRoomframe.dispose();
						//방 만들고 버튼 사라짐...
					}
				});
				
				createRoomBtn.setVisible(false);
				createRoomBtn.setBounds(SCREEN_WIDTH/2, 0, SCREEN_WIDTH/2, 30);
				createRoomBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						createRoomframe.setVisible(true);
					}
				});
				add(createRoomBtn);
				
				readybtn.setVisible(false);
				readybtn.setBounds(0, 0, SCREEN_WIDTH/2, 30);
				readybtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
							s.send("/ready");
					}
				});
				add(readybtn);
				
				exitroombtn.setVisible(false);
				exitroombtn.setBounds(SCREEN_WIDTH/2, 0, SCREEN_WIDTH/2, 30);
				exitroombtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
							s.send("나가기");
					}
				});
				add(exitroombtn);
				/*
				exitRoom.setVisible(false);
				exitRoom.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendmsg = "나가기";
						send();
						createRoomBtn.setVisible(true);
						exitRoom.setVisible(false);
						joinRoomBtn.setVisible(true);
					}
				});
					
				
				*/
			} catch (Exception e) {

			}
		}
		
		void ViewRoomlist() {
			j.setVisible(false);
			nickText.setVisible(false);
			enterButton.setVisible(false);
			
			readybtn.setVisible(false);
			exitroombtn.setVisible(false);
			
			joinScroll.setVisible(true);
			refreshBtn.setVisible(true);
			createRoomBtn.setVisible(true);
			
			s.send("/refresh");
		}
		
		public void Refresh(String[] split) {
			
			System.out.println("방 새로고침됨.");
			joinScroll.remove(joinRoomList);
			joinRoomList = new JList<String>(split);
			joinRoomList.addListSelectionListener(new RoomJoinEvent());
			joinScroll.setViewportView(joinRoomList);
			revalidate();
			repaint();
		}
		
		public void ViewJoinRoom() {
			createRoomBtn.setVisible(false);
			refreshBtn.setVisible(false);
			createRoomframe.dispose();
			joinScroll.setVisible(false);
			
			
			readybtn.setVisible(true);
			exitroombtn.setVisible(true);
		}

		public void Gamestartsetting() {
			System.out.println("게임시작!");
			//ChangeScene("multistart");
			multistart(r, s);
		}
		
		class RoomJoinEvent implements ListSelectionListener{

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					String[] split = joinRoomList.getSelectedValue().split(" ");
					s.send("/join "+ split[0]);
				}
				
			}
			
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
		}
		
	}

	class GameResultPanel extends JPanel {

		JLabel Gameresult = new JLabel("");
		JButton button = new JButton("메인화면으로 돌아가기");

		GameResultPanel() {
			setLayout(null);
			setBackground(Color.yellow);

			Gameresult.setBounds(10, 10, 1000, 1000);
			Gameresult.setText("게임결과 : " + computerNum4[0] + computerNum4[1] + computerNum4[2] + computerNum4[3]
					+ "           " + turn + "번 만에 정답을 맞혔다!");
			Gameresult.setVerticalAlignment(SwingConstants.TOP);
			Gameresult.setHorizontalAlignment(SwingConstants.LEFT);
			button.setBounds(10, 100, 300, 50);
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					ChangeScene("mainScene");
				}
			});
			add(Gameresult);
			add(button);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		}

	}

	class GamePanel extends JPanel {

		JPanel keypadPanel = new JPanel();

		JLabel label1 = new JLabel("○○○○");
		JLabel label2 = new JLabel("");

		JTextArea resultLabel2 = new JTextArea("");
		//JLabel resultLabel = new JLabel("<html></html>");
		
		JTextArea resultLabel = new JTextArea("");
		
		JScrollPane resultfield = new JScrollPane(resultLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane resultfield2 = new JScrollPane(resultLabel2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JButton backmainbutton = new JButton("");
		JButton openmemobutton = new JButton("메모장");
		JButton keyPadDrag = new JButton();

		JFrame memoJframe = new JFrame("메모장");
		JTextArea memoTextArea = new JTextArea();

		JLabel timermy = new JLabel("");
		JLabel timeroppo = new JLabel("");
		
		public GamePanel() {
			turn = 0;
			setBackground(bgColor2);
			setLayout(null);
		}
		public void MakeTimer() {
			timermy.setBounds(190, 10, 70, 20);
			timeroppo.setBounds(2, 10, 70, 20);
			timermy.setText("Time:");
			timeroppo.setText("Time:");
			add(timermy);
			add(timeroppo);
		}
		public void MakeKeyPad() {
			
			
			//createNum();
			// 키패드
			for (int i = 1; i < 14; i++) {
				int x = 0;
				int y = 0;
				String text = Integer.toString(i);
				;
				if (1 <= i && i <= 3) {
					x = 60 * (i - 1);
				} else if (4 <= i && i <= 6) {
					x = 60 * (i - 4);
					y = 30;
				} else if (7 <= i && i <= 9) {
					x = 60 * (i - 7);
					y = 60;
				} else if (i == 10) {
					x = 60;
					y = 90;
					text = "0";
				} else if (i == 11) {
					x = 120;
					y = 90;
					text = "◁";
				} else if (i == 12) {
					y = 90;
					text = "확인";
				}

				JButton keyPad = new JButton(text);
				keyPad.setBounds(x + 5, y + 5, 60, 30);
				keyPad.setBackground(Color.white);
				keyPad.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						String clickPadString = e.getActionCommand().toString();

						if (clickPadString == "◁") {
							if (label1.getText() == "○○○○")
								return;
							if (label1.getText().length() == 1) {
								label1.setText("○○○○");
							} else if (label1.getText().length() <= 4) {
								label1.setText(label1.getText().substring(0, label1.getText().length() - 1));
							}
							return;
						} else if (clickPadString == "확인") {
							String inputcheck = GameInputCheck(label1.getText());
							
							label2.setText(inputcheck);

							if (inputcheck.length() == 4) {
								turn++;
								pressDone(); //싱글 : 화면에 띄움, 멀티 : 서버로 보냄
								label1.setText("○○○○");
								
								//스크롤 밑으로
								resultfield.getVerticalScrollBar().setValue(resultfield.getVerticalScrollBar().getMaximum());
							}

							return;
						}

						if (label1.getText() == "○○○○")
							label1.setText("");
						if (label1.getText().length() <= 3) 
							label1.setText(label1.getText() + clickPadString);
						

					}
				});
				keypadPanel.add(keyPad);
			}
			keyPadDrag.setSize(5, 5);
			keyPadDrag.setLocation(0, 0);
			keyPadDrag.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					int X = e.getX(), Y = e.getY();

					if (0 > padX + X || padX + X > SCREEN_WIDTH - 20 || 0 > padY + Y || padY + Y > SCREEN_HEIGHT - 50)
						return;
					padX = padX + X;
					padY = padY + Y;
					keypadPanel.setLocation(padX, padY);
				}
			});
			keypadPanel.add(keyPadDrag);
			keypadPanel.setBackground(bgColor);
			keypadPanel.setLayout(null);
			keypadPanel.setSize(185, 125);
			keypadPanel.setLocation(190, 425);
			add(keypadPanel);
		}
		public void makeLabel() {
			// 입력 라벨
			label1.setBounds(10, 350, 400, 200);
			label1.setFont(label1.getFont().deriveFont(40.0f));
			add(label1);
			// 안내 라벨
			label2.setBounds(10, 450, 200, 100);
			add(label2);

			memoTextArea.setFont(memoTextArea.getFont().deriveFont(15.0f));
			memoJframe.setSize(300, 300);
			memoJframe.dispose();
			memoJframe.add(memoTextArea);

			openmemobutton.setBounds(10, 510, 80, 30);
			openmemobutton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					memoJframe.setVisible(true);
				}
			});
			add(openmemobutton);

		}
		public void makeFieldAndBack() {
			//표시되는 화면 라벨(TextArea)
			resultLabel.setEditable(false);
			resultLabel.setFont(label1.getFont().deriveFont(25.0f));
			resultLabel.setOpaque(true);
			resultLabel.setBackground(Color.WHITE);

			resultfield.setBounds(0, 25, 190, 390);
			add(resultfield);

			resultLabel2.setEditable(false);
			resultLabel2.setFont(label1.getFont().deriveFont(25.0f));
			resultLabel2.setOpaque(true);
			resultLabel2.setBackground(Color.WHITE);

			resultfield2.setBounds(190, 25, 190, 390);
			add(resultfield2);

			backmainbutton.setBounds(355, 3, 20, 20);
			backmainbutton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ChangeScene("mainScene");
				}
			});
			add(backmainbutton);
		}
		public void pressDone() {
			resultLabel2.setText(resultLabel2.getText()+"\n"
					 + label1.getText() + "    " + CompareNum4());
		}
		public void createNum() {
			Random random = new Random();
			String num4S = "";

			for (int i = 0; i < 4; i++) {
				num4S += Integer.toString(random.nextInt(10));
			}

			if (GameInputCheck(num4S).length() == 4) {
				computerNum4[0] = num4[0];
				computerNum4[1] = num4[1];
				computerNum4[2] = num4[2];
				computerNum4[3] = num4[3];
			} else {
				createNum();
				return;
			}

			for (int j = 0; j < computerNum4.length; j++) {
				System.out.println(computerNum4[j]);
			}

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			

		}

	}
	class SingleGameStart extends GamePanel{
		public SingleGameStart(){
			createNum();
			MakeKeyPad();
			makeLabel();
			makeFieldAndBack();
			MakeTimer();
		}
	}
	class MultiGameStart extends GamePanel{
		ReceiveThread r;
		SendThread s;
		boolean Done4num = false;
		JTextArea inputNumArea = new JTextArea("자신의 4자리 숫자를 입력하세요");
		
		public MultiGameStart(ReceiveThread receive, SendThread send) {
			r = receive;
			s = send;
			r.setSocket(this);
			
			inputNumArea.setBounds(0, 25, 380, 390);
			resultLabel.setFont(label1.getFont().deriveFont(25.0f));
			add(inputNumArea);
			
			MakeKeyPad();
			makeLabel();
			
		}
		
		@Override
		public void createNum() {
			System.out.println("multiGame override");
			if(Done4num)  super.createNum(); 	
		}

		@Override
		public void pressDone() {
			s.send("/num4 "+label1.getText());
		}
		public void gameover() {
			ChangeScene("gameover");
		}
		
	
	}

	

	public String CompareNum4() {
		int Strike = 0, ball = 0;
		String str = "";
		// 검사
		for (int i = 0; i < computerNum4.length; i++) {
			for (int j = 0; j < num4.length; j++) {

				if (computerNum4[i] == num4[j]) {
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
			return "OUT!";
		if (Strike != 0)
			str += (Strike + "S");
		if (ball != 0)
			str += (ball + "B");

		if (str.equals("4S")) {
			ChangeScene("gameover");
		}

		return str;
	}

	public String GameInputCheck(String num1) {

		if (num1.length() != 4) {
			return "4자리 수를 입력해 주세요.";
		}

		try {
			int numInt = Integer.parseInt(num1);
	
			for(int i = num1.length()-1 ; i >= 0; i--) {	
				num4[i] = numInt%10;
				numInt /= 10;
			}
			str = "" + num4[0] + num4[1] + num4[2] + num4[3];

		} catch (Exception e) {
			return "숫자를 입력해주세요";
		}

		for (int i = 0; i < num4.length; i++) {

			for (int j = i + 1; j < num4.length; j++) {

				if (num4[i] == num4[j]) {

					return "수를 중복으로 입력했습니다";
				}
			}
		}

		return str;
	}

}

public class baseball {
	public static void main(String[] args) {
		new NumberBaseball();
	}
}
