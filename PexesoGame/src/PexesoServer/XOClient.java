package PexesoServer;
import java.util.Scanner;
import java.io.IOException;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;

public class XOClient{
    final static String host = "localhost";
    final static int port = 1234;
    static int[][] board = new int[5][4];
    public static void main(String[] args){
        System.out.println("Connecting to server... ");
        Socket s;
        DataInputStream in;
        DataOutputStream out;
        Scanner keybIn = new Scanner(System.in);
        try{
            s = new Socket(host, port);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream()); }
        catch(IOException e){
            System.out.println("Cannot connect");
            return; }
        System.out.println("done!");
        String msgFromServer;
        int numToSend;
        try{
            while(true){
                msgFromServer = in.readUTF();
                System.out.println(msgFromServer);
                if(msgFromServer.equals("SRV: BYE")){
                    break; }
                else if(msgFromServer.equals("SRV: Please send board size:")){
                    System.out.print("Enter integer for n: ");
                    numToSend = keybIn.nextInt();
                    out.writeInt(numToSend);
                    System.out.print("Enter integer for m: ");
                    numToSend = keybIn.nextInt();
                    out.writeInt(numToSend);
                }
                else if(msgFromServer.equals("SRV: Sending board...")){
                    String bs = in.readUTF();
                    int n = Integer.parseInt(bs.substring(0,bs.indexOf(",")));
                    int m = Integer.parseInt(bs.substring(bs.indexOf(",")+1, bs.length()));
                    board = new int[n][m];
                    for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                            board[i][j] = -1;
                        }
                    }
                    printBoard();
                }
                else if(msgFromServer.equals("SRV: Updating board...")){
                    String bs = in.readUTF();
                    int element = Integer.parseInt(bs);
                    bs = in.readUTF();
                    int i = Integer.parseInt(bs.substring(0,bs.indexOf(",")));
                    int j = Integer.parseInt(bs.substring(bs.indexOf(",")+1, bs.length()));
                    bs = in.readUTF();
                    int n = Integer.parseInt(bs.substring(0,bs.indexOf(",")));
                    int m = Integer.parseInt(bs.substring(bs.indexOf(",")+1, bs.length()));
                    board[i][j] = element;
                    board[n][m] = element;
                    printBoard();
                }
                else if(msgFromServer.equals("SRV: Please enter (i,j) and (n,m):")){
                    System.out.print("Enter integer for i: ");
                    numToSend = keybIn.nextInt();
                    out.writeInt(numToSend);
                    System.out.print("Enter integer for j: ");
                    numToSend = keybIn.nextInt();
                    out.writeInt(numToSend);
                    System.out.print("Enter integer for n: ");
                    numToSend = keybIn.nextInt();
                    out.writeInt(numToSend);
                    System.out.print("Enter integer for m: ");
                    numToSend = keybIn.nextInt();
                    out.writeInt(numToSend);
                }
            }
        }
        catch(IOException e){
            System.out.println("Connection lost");
        }
        finally{
            try{
                if(in!=null) in.close();
                if(out!=null) out.close();
                if(s!=null) s.close();
            }
            catch(IOException e2){}
        }
    }

    static void printBoard(){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++){
                if(board[i][j]==-1) System.out.print("X ");
                else System.out.print(board[i][j]+" ");
            }
            System.out.println();
        }
    }
}
