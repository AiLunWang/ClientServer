import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class myfileclient {
	public static void main (String [] args ) throws IOException {
		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		OutputStream os = null;
		DataOutputStream dos = null;
		InputStream is = null;
		DataInputStream dis = null;
		Socket sock = null;
		int n, m;
		if (args.length!=3) {
			System.out.println("Error: This program requires exactly three command line arguments.");
		}
		else {
			try {
				//creates stream socket connects to args[0]: IP address and args[1]: port
				sock = new Socket(args[0], Integer.parseInt(args[1]));
				System.out.println("Connecting...");

				os = sock.getOutputStream();
				dos = new DataOutputStream(os);
				
				//args[2]: files to be sent 
				dos.writeUTF(args[2]);
				is = sock.getInputStream();
				dis = new DataInputStream(is);

				String response = dis.readUTF();
				n = dis.readInt();
				m = dis.readInt();
				System.out.println(response);
				if (response.equals("ReadyToSend")) {
					System.out.println("Server handled " + n + " requests, " + m + " requests were successful");

					System.out.println("File " + args[2] + " found");

					dos.writeUTF("ReadyToReceive");

					// receive file
					byte [] mybytearray  = new byte [FILE_SIZE];
					is = sock.getInputStream();
					fos = new FileOutputStream(args[0]);
					bos = new BufferedOutputStream(fos);
					bytesRead = is.read(mybytearray,0,mybytearray.length);
					current = bytesRead;

					do {
						bytesRead =
								is.read(mybytearray, current, (mybytearray.length-current));
						if(bytesRead >= 0) current += bytesRead;
					} while(bytesRead > -1);

					bos.write(mybytearray, 0 , current);
					bos.flush();
					System.out.println("Download Completed");

				}
				else if (response.equals("FileNotFound")) {
					System.out.println("File " + args[0] + " not found");
				}
			}
			catch (SocketException e) {
				System.out.println("Connection Not Made");
			}   	
			finally {
				if (fos != null) fos.close();
				if (bos != null) bos.close();
				if (sock != null) sock.close();
			}
		}
	}
}
