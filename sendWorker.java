import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class senderWorker extends myfileserver implements Runnable {

	private Socket sock;

	public senderWorker(Socket passedSock){
		this.sock = passedSock;         
	}

	@Override
	public void run() {
		try {
			processCommand();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processCommand() throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		DataOutputStream dos = null;
		InputStream is = null;
		DataInputStream dis = null;

		try {
			is = sock.getInputStream();
			dis = new DataInputStream(is);

			String fileString = dis.readUTF();

			System.out.println("REQ " + super.getM() + " File " + fileString + " requested from " + sock.getInetAddress());

			File fileName = new File(fileString);
			//test if passed filename is valid
			if(!fileString.contains("\\") && !fileString.contains("/") && fileName.exists() && !fileName.isDirectory()) {
				os = sock.getOutputStream();
				dos = new DataOutputStream(os);

				//send ready to send signal
				dos.writeUTF("ReadyToSend");
				dos.writeInt(super.getM());
				dos.writeInt(super.getN());
				super.setN(super.getN()+1);
				super.setN(super.getN()+1);
				String response = dis.readUTF();
				//wait for ready to receive signal
				if (response.equals("ReadyToReceive")) {

					// send file
					byte [] mybytearray  = new byte [(int)fileName.length()];
					System.out.println("3");
					fis = new FileInputStream(fileName);
					bis = new BufferedInputStream(fis);
					bis.read(mybytearray,0,mybytearray.length);
					os = sock.getOutputStream();
					os.write(mybytearray,0,mybytearray.length);
					os.flush();
					super.setM(super.getM()+1);
					System.out.println("REQ " + super.getN() + " Succesful");
				}
			}
			else {
				//otherwise file not found
				os = sock.getOutputStream();
				dos = new DataOutputStream(os);

				dos.writeUTF("FileNotFound");
				dos.writeInt(super.getM());
				dos.writeInt(super.getN());
				System.out.println("REQ " + super.getN() + " Not Succesful");              
			}       
		}
		finally {
			if (bis != null) bis.close();
			if (os != null) os.close();
			if (sock!=null) sock.close();
		}

		System.out.println("REQ " + super.getN() + " Total successful requests so far = " + super.getN());
		System.out.println("REQ " + super.getN() + " File transfer complete");
	}
}
