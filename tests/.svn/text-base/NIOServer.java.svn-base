package tests;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOServer {

	public static void main(String[] args) {
		ServerSocketChannel ssc = null;
		try {
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(4000));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		while (true) {
			SocketChannel sc = null;
			try {
				sc = ssc.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (sc != null) {
				String data = "The system time is " + System.nanoTime();
				ByteBuffer buffer = ByteBuffer.allocate(48);
				buffer.clear();
				buffer.put(data.getBytes());
				buffer.flip();

				while (buffer.hasRemaining()) {
					try {
						sc.write(buffer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}

	}

}
