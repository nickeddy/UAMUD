package server;

import java.io.Serializable;

/**
 * Message is an encapsulating class for data sent between Server and Client. It
 * has a MessageType in order to distinguish what kind of data will be enclosed.
 * 
 * @author Nicholas Eddy, Mike Novak, Kyohei Mizokami, Chris Panzero
 * 
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -5930043385151862011L;
	private Object o;
	private MessageType t;

	public Message(Object data, MessageType type) {
		this.o = data;
		this.t = type;
	}

	public Object getData() {
		return o;
	}

	public MessageType getMessageType() {
		return t;
	}
}
