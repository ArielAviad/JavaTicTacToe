
import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * this class represents the controller of the Tic tac toe game.
 */
public class TicController implements ITicController{
    //messages
    private static final String HUMAN_WON_MSG = "Congrats you have won :)";
    private static final String COMPUTER_WON_MSG = "you lost maybe next time...";
    private static final String TIE_MSG = "This is a tie";
    private String humanTurnMsg = "";
    private String computerTurnMsg = "";

    AbstractTicModel m_model;
    ITicView m_view;
    IPlayer computer;

    //game control vars.
    private boolean isHumanTurn;
    private State humanState;
    private boolean gameOver;

    /*
    * constructor create a controller for the tic game
    * @param model - the model
    * @param view - the view
    */
    public TicController(AbstractTicModel model, ITicView view) throws NullModelViewException {
        if (model == null || view == null)
            throw new NullModelViewException();
        m_model = model;
        m_view = view;
        computer = new ComputerPlayer(model);
        initGame();
    }

    private void initGame() {
        //set the mouse event and the menu.
        m_view.addMouseClick(new mouseEvents());
        m_view.setNewMenuBar(initMenu());
    }

    /**
     * set the menuBar
     * @return the new JMenuBar.
     */
    private JMenuBar initMenu() {
        JMenuBar menuBar = new JMenuBar();
        //option menuItem section.
        JMenu options = new JMenu("options");
        //add newGame option
        JMenuItem newGame = new JMenuItem("new game");
        newGame.setAccelerator(KeyStroke.getKeyStroke('n'));
        newGame.addActionListener((e)->gameStart());
        //add undo Option.
        JMenuItem undo = new JMenuItem("undo");
        undo.setAccelerator(KeyStroke.getKeyStroke('u'));
        undo.addActionListener((e) -> undo());

        options.add(newGame);
        options.add(undo);

        //set the exit menu
        JMenu exitMenu = new JMenu("exit");
        //exit option
        JMenuItem exit = new JMenuItem("exit");
        exit.setAccelerator(KeyStroke.getKeyStroke('q'));
        exit.addActionListener((e)->{System.exit(0);});

        exitMenu.add(exit);
        //add the menu
        menuBar.add(options);
        menuBar.add(exitMenu);
        return menuBar;
    }

    /**
     * undo the last move.
     */
    private void undo() {
        if (gameOver)return;//not allow at end of game.
        if (isHumanTurn) {
            int nFullCell = m_model.getNumberOfMoves();
            m_model.undo();//undo last move.
            switch (nFullCell) {
                case 1://case it was the computer first move make his first move.
                    Point computerMove = computer.makeMove();
                    updateGame(computer.getState(),computerMove.x,computerMove.y);
                    break;
                default://else undo again the computer move.
                    m_model.undo();
            }//update the view.
        }
    }

    /**
     * set the game vars for a new game and
     * make the computer first move if he starts.
     */
    @Override
    public void gameStart() {
        Random r = new Random(System.currentTimeMillis());
        //set random start and states.
        isHumanTurn = r.nextBoolean();
        humanState = r.nextBoolean() ? State.O : State.X;
        computer.setState(humanState == State.O ? State.X : State.O);
        //set the board
        m_model.clearBoard();
        //set status msg.
        gameOver = false;
        humanTurnMsg = "Your turn you are " + humanState;
        computerTurnMsg = "Computer turn he is " + computer.getState();
        //case computer start make his first move.
        if (!isHumanTurn) {
            Point computerMove = computer.makeMove();
            updateGame(computer.getState(),computerMove.x,computerMove.y);
        }//set msg human turn
        m_view.setMsg(humanTurnMsg);
    }


    /**
     * make the human move.
     * @param c - the column.
     * @param r - the row
     * @return the if it was a legal move.
     */
    private boolean makeHumanTurn(int r, int c) {
        boolean isLegalMove = false;
        if (isHumanTurn)
            isLegalMove = m_model.setState(humanState,r,c);//make the move.
        return isLegalMove;
    }

    /**
     * update the game after a move.
     * @param s - the state ho played
     * @param c - the col was played
     * @param r - the row was played
     */
    private void updateGame(State s,int c, int r){
        isHumanTurn = (s == computer.getState());
        boolean hasWon = m_model.hasWon(s,r,c);
        //both cases the game ends.
        if (hasWon || m_model.isBoardFull()){
            gameOver = true;
            //set the status msg
            m_view.setMsg(hasWon == false ? TIE_MSG : (s == humanState ? HUMAN_WON_MSG : COMPUTER_WON_MSG));
        }else {//set the status msg
            m_view.setMsg(isHumanTurn == true ? humanTurnMsg : computerTurnMsg);
        }
    }

    /**
     * this class is the mouse event function.
     */
    private class mouseEvents implements IMouseEvents {
        public void clickEvent(int r, int c) {
            if (gameOver || m_model.isBoardFull()) {
                gameStart();
            } else {
                if (makeHumanTurn(r, c)) {//case legal move.
                    updateGame(humanState, c, r);
                    if (!gameOver && !m_model.isBoardFull()) {//do computer move.
                        Point computerMove = computer.makeMove();
                        if (computerMove != null)
                            updateGame(computer.getState(), computerMove.x, computerMove.y);
                    }
                }
            }
        }
    }


    public class NullModelViewException extends Exception{

        public NullModelViewException(){
            this("Model or view are null");
        }

        public NullModelViewException(String msg){
            super(msg);
        }
    }
}
