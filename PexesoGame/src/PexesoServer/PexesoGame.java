package PexesoServer;

import java.io.IOException;
import java.util.LinkedList;

class PexesoGame implements Runnable{
    int id;
    int activePlayers;
    LinkedList<User> users;
    int playerOnTurn;
    PexesoBoard PB;

    // Конструктор
    public PexesoGame(int id, LinkedList<User> users){
        this.id = id;
        this.users = users;
        this.playerOnTurn = 0;
        this.sendToAll("SRV: Game started. "+
                "Player 1 is choosing board size..."); }
    public void run(){
        try{
            users.get(playerOnTurn).send("SRV: Please send board size:");
            this.PB = new PexesoBoard(users.get(playerOnTurn).get(), users.get(playerOnTurn).get());
        }
        catch(Exception e){
            sendToAll("SRV: Player 1 sent incorrect board size");
            sendToAll("SRV: We will use the default board size - (5,4)");
            try{
                this.PB = new PexesoBoard(5,4);
            }
            catch(Exception e3){
                System.err.println("Something bad happened in game "+this.id);
            }
        }

        for(this.playerOnTurn=0; playerOnTurn<users.size(); playerOnTurn++){
            try{
                users.get(playerOnTurn).number = playerOnTurn+1;
                users.get(playerOnTurn).send("SRV: You are player "+users.get(playerOnTurn).number);

            }
            catch(IOException e){
                this.sendToAll("SRV: Player "+users.get(playerOnTurn).number+" has left the game");
            }
        }
        this.sendToAll("SRV: Sending board...");
        this.sendToAll(this.PB.board.length+","+this.PB.board[0].length);
        playerOnTurn = 0;
        this.sendToAll("SRV: Board is updated");
        this.sendToAll("SRV: We are ready to go. Player 2's turn");
        runloop: while(true){
            if(playerOnTurn < PexesoServer.usersPerGame-1) playerOnTurn++;
            else playerOnTurn=0;
            if(this.users.size()<2){
                this.sendToAll("SRV: All players left. You win");
                this.sendToAll("SRV: BYE");
                break runloop;}
            if(PB.noMoreElements()){
                this.sendToAll("SRV: Board is clear");
                StringBuilder strb = new StringBuilder();
                for(int i=0; i<users.size(); i++){
                    strb.append("Player "+(i+1)+": "+this.users.get(i).points+" points\n"); }
                this.sendToAll("SRV: Standings:\n"+strb.toString());
                this.sendToAll("SRV: BYE");
                break runloop;
            }
            byte mistakes = 0;
            int fountMatch = -1;

            moveloop:while(true){
                if(mistakes==3){
                    sendToAll("SRV: Player "+users.get(playerOnTurn).number+
                            " is disconnected for too many wrong moves");
                    this.removeUser(playerOnTurn);
                    break moveloop;
                }
                try{
                    users.get(playerOnTurn).send("SRV: Please enter (i,j) and (n,m):");
                    int moveI = users.get(playerOnTurn).get();
                    int moveJ = users.get(playerOnTurn).get();
                    int moveN = users.get(playerOnTurn).get();
                    int moveM = users.get(playerOnTurn).get();

                    fountMatch = this.PB.doMove(moveI,moveJ,moveN,moveM);

                    if(fountMatch!=-1){
                        this.users.get(playerOnTurn).points++;
                        this.sendToAll("SRV: Player "+users.get(playerOnTurn).number+" scores!");
                        this.sendToAll("SRV: Updating board...");
                        this.sendToAll(""+fountMatch);
                        this.sendToAll(moveI+","+moveJ);
                        this.sendToAll(moveN+","+moveM); }
                    else{
                        this.sendToAll("SRV: Player "+users.get(playerOnTurn).number+" misses!"); } }
                catch(IOException e){
                    sendToAll("SRV: Player "+users.get(playerOnTurn).number+" has left the game"); }
                catch(Exception e2){
                    mistakes++;
                    continue moveloop; }
                break moveloop;}}
        for(int i=0; i<users.size(); i++){
            removeUser(i); }
        System.out.println("Game "+this.id+" ended");}
    private void sendToAll(String msg){
        for(int i=0; i<users.size(); i++){
            try{
                users.get(i).send(msg); }
            catch(IOException e){
                this.removeUser(i);
           }}}
    private void removeUser(int i){
        try{
            if(users.get(i).in!=null) users.get(i).in.close();
            if(users.get(i).out!=null) users.get(i).out.close();
            if(users.get(i).s!=null) users.get(i).s.close();
        }
        catch(IOException e){}
        this.users.remove(i);
        this.sendToAll("SRV: Player "+
                (i+1)+" has left the game");
    }
}
