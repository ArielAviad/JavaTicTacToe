
import java.awt.*;
import java.util.Random;

public class ComputerPlayer implements IPlayer {

    private State m_state;
    private State o_state;

    private AbstractTicModel m_model;

    public ComputerPlayer(AbstractTicModel model) {
        m_model = model;
    }

    /**
     * set the computer state and save the human state two.
     * @param s - the computer state.
     */
    @Override
    public void setState(State s) {
        if (s != null && s != State.EMPTY){
            m_state = s;
            o_state = s == State.O ? State.X : State.O;
        }
    }

    @Override
    public State getState() {
        return m_state;
    }

    /**
     * make the computer move assumes the board nut full.
     * @return the point that the computer made
     */
    @Override
    public Point makeMove() {
        Point p = chooseComputerMove();//get the move.
        m_model.setState(getState(),p.y,p.x);//do the move.
        return p;
    }

    /**
     * @return the computer move.
     */
    private Point chooseComputerMove() {
        Point p = null;
        //check if computer can win or human.
        for (int c = 0;c < AbstractTicModel.COL_NUM;++c){
            for (int r = 0; r < AbstractTicModel.ROW_NUM; r++) {
                if (m_model.canWin(getState(),r,c)){//if computer can win choose it.
                    return new Point(c,r);
                }else if (p == null && m_model.canWin(o_state,r,c)){//if human can win save the first point
                    p = new Point(c,r);
                }
            }
        }//case human can win return that
        if (p != null)return p;
        //else choose random move.
        return getRandomMove();
    }

    /**
     * @return random move on the board.
     */
    private Point getRandomMove() {
        int x,y;
        do{
            final Random r = new Random(System.currentTimeMillis());
            x = r.nextInt(AbstractTicModel.COL_NUM);
            y = r.nextInt(AbstractTicModel.ROW_NUM);
            y = AbstractTicModel.ROW_NUM-1-y;//set the correct coordinate.
        }while (m_model.getState(y, x) != State.EMPTY);//if legal move.
        return new Point(x,y);
    }
}
