package tests;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NIOClient {

	public static void main(String[] args) {
		SocketChannel sc = null;
		int x = 0;
		while (x < 1000) {

			try {
				sc = SocketChannel.open();
				sc.connect(new InetSocketAddress("localhost", 4000));
				ByteBuffer buffer = ByteBuffer.allocate(32);
				sc.read(buffer);
				buffer.flip();
				System.out.println(Charset.defaultCharset().decode(buffer));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			x++;
			try {
				sc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
