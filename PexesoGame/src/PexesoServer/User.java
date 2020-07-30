package PexesoServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class User{
    Socket s;
    DataInputStream in;
    DataOutputStream out;
    int points;
    int number;
    public User(){
        this.points = 0;
        this.number = 0;
    }
    void initSocket(Socket s) throws IOException {
        this.s = s;
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
    }
    void send(String msg)  throws IOException{
        this.out.writeUTF(msg);
    }

    int get() throws IOException{
        return this.in.readInt();
    }
}
