package PexesoServer;
class PexesoBoard{
    int[][] board;
    public PexesoBoard(int n, int m) throws Exception{
        if((n*m)%2 != 0) throw new Exception("Please choose n*m even");
        this.board = new int[n][m];
        int element = 1;
        int changeFlag = 1;
        for(int i=0; i<n; i++){
            for(int j=0; j<m; j++){
                this.board[i][j] = element;
                if(changeFlag%2 == 0) element++;
                changeFlag++;
            }
        }
        this.shuffle();
    }

    private void shuffle(){
        java.util.Random rnd = new java.util.Random();
        for(int i=0; i<this.board.length; i++){
            for(int j=0; j<this.board[i].length; j++){
                int newRow = rnd.nextInt(this.board.length);
                int newColumn = rnd.nextInt(this.board[i].length);
                int copyEl = this.board[newRow][newColumn];
                this.board[newRow][newColumn] = this.board[i][j];
                this.board[i][j] = copyEl;
            }
        }
    }

    int doMove(int i, int j, int n, int m) throws Exception{
        if(i<0 || i>this.board.length ||
                j<0 || j>this.board.length ||
                n<0 || n>this.board[0].length ||
                m<0 || m>this.board[0].length ||
                (i==n && j==m) ||
                this.board[i][j] == -1 ||
                this.board[n][m] == -1
        ){
            throw new Exception("Invalid coordinates");
        }
        if(this.board[i][j] == this.board[n][m]){
            int result = this.board[i][j];
            this.board[i][j] = -1;
            this.board[n][m] = -1;
            return result;
        }
        else return -1;
    }

    boolean noMoreElements(){
        for(int i=0; i<this.board.length; i++){
            for(int j=0; j<this.board[i].length; j++){
                if(this.board[i][j]!=-1) return false;
            }
        }
        return true;
    }
}
