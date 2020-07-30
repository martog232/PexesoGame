package PexesoServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;

public class PexesoServer{
    static int port = 1234;
    static int usersPerGame = 3;
    public static void main(String [] args){
        ServerSocket servSock;
        try{
            servSock = new ServerSocket(port);
        }
        catch(IOException e){
            System.err.println("Can't start server");
            return; }
        System.out.println("Server started");
        int lastGameID = 1;
        LinkedList<User> usersQueue = new LinkedList<User>();
        synchronized(usersQueue){
            mainloop: while(true){
                User u = new User();
                try{
                    u.initSocket(servSock.accept());
                    System.out.println("New user is connected");
                    usersQueue.add(u);
                    u.send("SRV: Please wait for opponents");
                }
                catch(IOException e){
                    System.out.println("Connection to user failed");
                    usersQueue.remove(usersQueue.indexOf(u));
                    continue mainloop;
                }

                if(usersQueue.size() == usersPerGame){
                    for(User tu: usersQueue){
                        try{
                            tu.send("SRV: Please wait a bit more...");
                        }
                        catch(IOException userIsGone){
                            usersQueue.remove(usersQueue.indexOf(tu));
                        }
                    }

                    if(usersQueue.size() != usersPerGame) continue mainloop;
                    else{
                        LinkedList<User> UQCopy = new LinkedList<User>();
                        for(User i: usersQueue){
                            UQCopy.add(i);
                        }
                        usersQueue = new LinkedList<User>();
                        Thread t = new Thread(
                                new PexesoGame(lastGameID, UQCopy));
                        t.start();
                        System.out.println("Game id "+lastGameID+" started");
                        lastGameID++;
                    }
                }
            }
        }
    }
}
