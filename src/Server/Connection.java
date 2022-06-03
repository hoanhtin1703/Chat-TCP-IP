package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection extends Thread {
	public Socket maychu;
	public Server server;
	private String nickName;
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean run;
	
	public Connection(Server server, Socket maychu){
		try {
			this.server=server;
			this.maychu=maychu;
			dos= new DataOutputStream(maychu.getOutputStream());
			dis= new DataInputStream(maychu.getInputStream());
			run=true;
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void run(){
		
		String msg=null;
		while(run){
			nickName=getMSG();
			if(nickName.compareTo("0")==0){
				logout();
			}
			else {
				if(checkNick(nickName)){
					sendMSG("0");
				}
				else{
					server.user.append(nickName+" đã kết nối vào room\n");
					server.sendAll(nickName,nickName+" đã vào room \n");
					server.listUser.put(nickName, this);
					server.sendAllUpdate(nickName);
					sendMSG("1");
					diplayAllUser();
					while(run){
						int stt = Integer.parseInt(getMSG());
						switch(stt){
							case 0:
								run=false;
								server.listUser.remove(this.nickName);
								exit();
								break;
							case 1:
								msg = getMSG();
								server.sendAll(nickName,nickName+" : "+msg);
								break;
						}
					}
				}
			}
		}
	}
	private void logout() {
		try {
			dos.close();
			dis.close();
			maychu.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	private void exit(){
		try {
			server.sendAllUpdate(nickName);
			dos.close();
			dis.close();
			maychu.close();
			server.user.append(nickName+" đã thoát\n");
			server.sendAll(nickName,nickName+" đã thoát\n");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	private boolean checkNick(String nick){
		return server.listUser.containsKey(nick);
	}
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void sendMSG(String msg1,String msg2){
		sendMSG(msg1);
		sendMSG(msg2);
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
	private void diplayAllUser(){
		String name = server.getAllName();
		sendMSG("4");
		sendMSG(name);
	}
}
