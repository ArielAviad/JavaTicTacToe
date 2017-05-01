
import java.awt.*;
import java.util.Arrays;
import java.util.Stack;

/*
* This class contains the logic and the data
* of the game.
*/
public class TicModel extends AbstractTicModel {
    //the board
    private State[][] board;
    //save the moves.
    private Stack<Point> moves;

    private static int nBoardCels = COL_NUM*ROW_NUM;
    /*constructor create on empty board*/
    public TicModel() {
        board = new State[ROW_NUM][COL_NUM];
        moves = new Stack<>();
        clearBoard();
    }
    /*
    * return the state of the the given row and column.
    * if r or c not in the board range return null.
    * @param r - the row
    * @param c - the column
    * @return null if r or c not in range else return the cell r,c
    */
    @Override
    public State getState(int r, int c){return inBoardRange(r,c) ? board[r][c] : null;}

    /*
    * set cell r,c to State s if r,c in the board range.
    * @param s - the new state
    * @param r - the row
    * @param c - the column
    * @return true on success else false
     */
    @Override
    public boolean setState(State s,int r, int c) {
        if (inBoardRange(r,c) && board[r][c] == State.EMPTY && s != null){
            board[r][c] = s;
            moves.push(new Point(c,r));
            stateChange();
            return true;
        }return false;
    }


    /**
     * return if the user went in r,c with state s won.
     */
    @Override
    public boolean hasWon(State s, int r, int c) {
        if (s == null || s == State.EMPTY || !inBoardRange(r,c))
            return false;
        return isWon(s,r,c);
    }

    /*
    * return if State s can win in row r && col c
    * if board is empty in that cell.
    * @param s - the state.
    * @param r - the row
    * @param c - the col
    * @return - false true if the cell is empty and s can win there.
    */
    @Override
    public boolean canWin(State s, int r, int c) {
        try {
            if (s == null)throw new NullPointerException();
            if (board[r][c] != State.EMPTY)
                return false;
            boolean canWin;
            board[r][c] = s;
            canWin = isWon(s, r, c);
            board[r][c] = State.EMPTY;
            return canWin;
        }catch (Exception e){
            return false;
        }
    }

    private boolean isWon(State s, int r, int c) {
        if (s == null || s == State.EMPTY || !inBoardRange(r,c))
            return false;
        // Return true if the player with the state "s" has won after placing at
        //(currentRow, currentCol).
        return (board[r][0] == s         // 3-in-the-row
                && board[r][1] == s
                && board[r][2] == s
                || board[0][c] == s      // 3-in-the-column
                && board[1][c] == s
                && board[2][c] == s
                || r == c               // 3-in-the-diagonal
                && board[0][0] == s
                && board[1][1] == s
                && board[2][2] == s
                || r + c == 2           // 3-in-the-opposite-diagonal
                && board[0][2] == s
                && board[1][1] == s
                && board[2][0] == s);
    }


    private boolean inBoardRange(int r, int c){
        return between(r,0,ROW_NUM) && between(c,0,COL_NUM);
    }

    //return not exclusive max.
    private boolean between(int n, int min, int max){
        return n >= min && n < max;
    }

    @Override
    public void clearBoard(){
        Arrays.stream(board).forEach(row -> Arrays.setAll(row,(i) -> State.EMPTY));
        moves.removeAllElements();
        stateChange();
    }

    @Override
    public boolean isBoardFull() {
        return getNumberOfMoves() == nBoardCels;
    }

    @Override
    public void undo() {
        if (moves.isEmpty()) return;
        Point lastMove = moves.pop();
        board[lastMove.y][lastMove.x] = State.EMPTY;
        stateChange();
    }

    @Override
    public int getNumberOfMoves() {
        return moves.size();
    }

    private void stateChange() {
        setChanged();
        notifyObservers();
    }
}
