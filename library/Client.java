package library;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Message;

/**
 * This class connects to a ServerSocket via a Socket. It sends and receives
 * Messages in order to communicate with the UAMUD Server.
 * 
 * @author Nicholas Eddy, Mike Novak, Kyohei Mizokami, Chris Panzero
 * 
 */
public class Client implements Serializable {

	private static final long serialVersionUID = 1L;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String username;
	private String charactername;

	public Client(String hostname, int port) {
		socket = null;
		username = "";
		charactername = "";
		try {
			socket = new Socket(hostname, port);
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			// this.send(new Message(null, MessageType.QUIT));
			ois.close();
			oos.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Message m) {
		try {
			oos.writeObject(m);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Message receive() {
		try {
			return (Message) ois.readObject();
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setCharactername(String charactername) {
		this.charactername = charactername;
	}

	public String getCharactername() {
		return charactername;
	}
}
