package Client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener{
	private JButton send,clear,exit,login,logout;
    private JPanel p_login,p_chat;
    private JTextField Ten1,Ten2,tn;
    private JTextArea msg,online;
    
    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
	private DataInputStream dis;
    
	public Client(){
		super("Client");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}	
		});
		setSize(600, 400);
		addItem();
		setVisible(true);
	}
	private void addItem() {
		getContentPane().setLayout(new BorderLayout());
		
		exit = new JButton("Thoát");
		exit.addActionListener(this);
		send = new JButton("Gửi");
		send.addActionListener(this);
		clear = new JButton("Xóa");
		clear.addActionListener(this);
		login= new JButton("Đăng nhập");
		login.addActionListener(this);
		logout= new JButton("Đăng Xuất");
		logout.addActionListener(this);
		
		p_chat = new JPanel();
		p_chat.setLayout(new BorderLayout());
		
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		Ten1 = new JTextField(20);
		p1.add(new JLabel("Tên người dùng :"));
		p1.add(Ten1);
		p1.add(exit);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		
		JPanel p22 = new JPanel();
		p22.setLayout(new FlowLayout(FlowLayout.CENTER));
		p22.add(new JLabel("Danh sách người dùng"));
		p2.add(p22,BorderLayout.NORTH);
		
		online = new JTextArea(10,10);
		online.setEditable(false);
		p2.add(new JScrollPane(online),BorderLayout.CENTER);
		p2.add(new JLabel("     "),BorderLayout.SOUTH);
		p2.add(new JLabel("     "),BorderLayout.EAST);
		p2.add(new JLabel("     "),BorderLayout.WEST);
		
		msg = new JTextArea(10,20);
		msg.setEditable(false);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		p3.add(new JLabel("Tin nhắn"));
		tn = new JTextField(30);
		p3.add(tn);
		p3.add(send);
		p3.add(clear);
		
		p_chat.add(new JScrollPane(msg),BorderLayout.CENTER);
		p_chat.add(p1,BorderLayout.NORTH);
		p_chat.add(p2,BorderLayout.EAST);
		p_chat.add(p3,BorderLayout.SOUTH);
		p_chat.add(new JLabel("     "),BorderLayout.WEST);
		
		p_chat.setVisible(false);
		getContentPane().add(p_chat,BorderLayout.CENTER);
		
		p_login = new JPanel();
		p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
		p_login.add(new JLabel("Tên người dùng :"));
		Ten2=new JTextField(20);
		p_login.add(Ten2);
		p_login.add(login);
		p_login.add(logout);
		
		getContentPane().add(p_login,BorderLayout.NORTH);
	}
	
	private void go() {
		try {
			client = new Socket("localhost",2207);
			dos=new DataOutputStream(client.getOutputStream());
			dis=new DataInputStream(client.getInputStream());
		
			//client.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"Lỗi đăng nhập, chưa mở server","Message Dialog",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		new Client().go();
	}
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void getMSG(String msg1, String msg2){
		int stt = Integer.parseInt(msg1);
		switch (stt) {
		
		case 3:
			this.msg.append(msg2);
			break;
		
		case 4:
			this.online.setText(msg2);
			break;
		
		case 5:
			dataStream.stopThread();
			exit();
			break;
		
		default:
			break;
		}
	}

	private void checkSend(String msg){
		if(msg.compareTo("\n")!=0){
			this.msg.append("Tôi : "+msg);
			sendMSG("1");
			sendMSG(msg);
		}
	}
	private boolean checkLogin(String Ten1){
		if(Ten1.compareTo("")==0)
			return false;
		else if(Ten1.compareTo("0")==0){
			return false;
		}
		else{
			sendMSG(Ten1);
			int sst = Integer.parseInt(getMSG());
			if(sst==0)
				 return false;
			else return true;
		}
	}

	private void exit(){
		try {
			sendMSG("0");
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exit){
			dataStream.stopThread();
			exit();
		}
		else if(e.getSource()==clear){
			tn.setText("");
		}
		else if(e.getSource()==send){
			checkSend(tn.getText()+"\n");
			tn.setText("");
		}
		else if(e.getSource()==login){
			if(checkLogin(Ten2.getText())){
				p_chat.setVisible(true);
				p_login.setVisible(false);
				Ten1.setText(Ten2.getText());
				Ten1.setEditable(false);
				this.setTitle(Ten2.getText());
				msg.append("Đăng nhập thành công");
				dataStream = new DataStream(this, this.dis);
			}
			else{
				JOptionPane.showMessageDialog(this,"Đã có tài khoản này , đăng nhập lại","Message Dialog",JOptionPane.WARNING_MESSAGE);
			}
		}
		else if(e.getSource()==logout){
			exit();
		}
	}


}
