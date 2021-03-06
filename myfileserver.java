import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
public class myfileserver {
     
    public final static int SOCKET_PORT = 13267;
    public int n = 0, m = 0; //initialise variables to hold N and M
 
    public static void main (String [] args ) throws IOException {
         
        ServerSocket servsock = null;
        Socket sock = null;
 
        try {
            servsock = new ServerSocket(SOCKET_PORT);
             
            while (true) {
                sock = servsock.accept();
                ExecutorService executor = Executors.newFixedThreadPool(10);
                Runnable worker = new senderWorker(sock);
                executor.execute(worker);
                executor.shutdown();
                }
            }
        finally {
            if (servsock != null) servsock.close();
            }
        }
    //Create getters and setters for N and M values
    public synchronized int getM() {
        return m;
    }
    public synchronized int getN() {
        return n;
    }
    //mutex only one sync 
    public synchronized void setM(int passedM) {
        m = passedM;
    }
    public synchronized void setN(int passedN) {
        n = passedN;
    }
}
