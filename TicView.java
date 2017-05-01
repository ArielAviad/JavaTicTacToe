import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.stream.IntStream;

public class TicView extends JFrame implements ITicView {
    private AbstractTicModel m_model;
    private String title = "Tic Tac Toe";
    Board panel;
    JPanel statusPanel;
    JLabel status;

    public TicView(AbstractTicModel model){
        super();
        m_model = model;
        m_model.addObserver(TicView.this);
        panel = new Board();
        //set status panel.
        statusPanel = new JPanel();
        status = new JLabel("Playing");
        statusPanel.add(status);

        //add panels
        setLayout(new BorderLayout());
        add(panel,BorderLayout.CENTER);
        add(status,BorderLayout.SOUTH);

        setTitle(title);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("images/icon.png").getImage());
        setSize(250,400);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    /*
     * set the mouse listener action.
     */
    @Override
    public void addMouseClick(IMouseEvents mouseClick) {
        panel.setMouseListener(mouseClick);
        //panel.addMouseListener(mouseAdapter);
    }

    @Override
    public void setNewMenuBar(JMenuBar newMenuBar) {
        setJMenuBar(newMenuBar);
    }

    /*set the status panel*/
    @Override
    public void setMsg(String newStatus) {
        status.setText(newStatus);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (m_model.equals(o))
            panel.repaint();
    }

    // panel that's paint the model board.
    private class Board extends JPanel{
        public Board(){
            super();
            setTitle("Tic Panel");
        }

        /**
         * return the ciel point for pixels xPixel,yPixel
         * and null if the pixels are out of the panel.
         */
        public Point getCiel(int xPixel, int yPixel) {
            if (xPixel < 0 || xPixel > getWidth() || yPixel < 0 || yPixel > getHeight())
                return null;
            int widthCell = panel.getWidth()/AbstractTicModel.COL_NUM;
            int x = xPixel < widthCell ? 0 : xPixel < 2*widthCell ? 1 : 2;
            int heightCell = panel.getHeight()/AbstractTicModel.ROW_NUM;
            int y = yPixel < heightCell ? 0 : yPixel < 2*heightCell ? 1 : 2;
            return new Point(x,y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            int w = getWidth();
            int h = getHeight();
            //draw the board lines.
            g.setColor(Color.GRAY);
            g.drawLine(w/3,0,w/3,h);
            g.drawLine(2*w/3,0,2*w/3,h);
            g.drawLine(0,h/3,w,h/3);
            g.drawLine(0,2*h/3,w,2*h/3);
            int rOffset = h/3,cOffset = w/3;
            //draw the ceil's.
            IntStream.range(0,AbstractTicModel.ROW_NUM).forEach(
                    (row)-> IntStream.range(0,AbstractTicModel.COL_NUM).forEach(
                            (col) -> drawCell(g, rOffset, cOffset, row, col)
                    )
            );
        }

        private void drawCell(Graphics g, int rOffset, int cOffset, int row, int col) {
            State s = m_model.getState(row,col);
            switch (s) {
                case EMPTY:
                    break;
                case O:
                    g.setColor(Color.BLUE);
                    drawO(g, col * cOffset, row * rOffset, (col + 1) * cOffset, (row + 1) * rOffset);
                    break;
                case X:
                    g.setColor(Color.RED);
                    drawX(g, col * cOffset, row * rOffset, (col + 1) * cOffset, (row + 1) * rOffset);
                    break;
                default:
                    break;
            }
        }

        /* draw x in cell from x1,y1 point to x2, y2 point */
        private void drawX(Graphics g, int x1, int y1, int x2, int y2) {
            int x = Math.min(x1,x2) + (int) (0.1*(Math.max(x1,x2) - Math.min(x1,x2)));
            int y = Math.min(y1,y2) + (int) (0.1*(Math.max(y1,y2) - Math.min(y1,y2)));
            int w = Math.min(x1,x2) + (int) (0.9*(Math.max(x1,x2) - Math.min(x1,x2)));
            int h = Math.min(y1,y2) + (int) (0.9*(Math.max(y1,y2) - Math.min(y1,y2)));

            g.drawLine(x,y,w,h);
            g.drawLine(w,y,x,h);
        }

        /* draw O in cell from x1,y1 point to x2, y2 point */
        private void drawO(Graphics g, int x1, int y1, int x2, int y2) {
            int x = Math.min(x1,x2) + (int) (0.1*(Math.max(x1,x2) - Math.min(x1,x2)));
            int y = Math.min(y1,y2) + (int) (0.1*(Math.max(y1,y2) - Math.min(y1,y2)));
            int w = (int) (0.75*(Math.max(x1,x2) - Math.min(x1,x2)));
            int h = (int) (0.75*(Math.max(y1,y2) - Math.min(y1,y2)));
            g.drawOval(x,y,w,h);
        }

        /**
         * this rap every mouse action in IMouseEvents will be wrap
         * with getting the ciel.
         * this why every new mouse action will need to be added in this way.
         * @param mouseClick - the mouse event class.
         */
        public void setMouseListener(IMouseEvents mouseClick) {
           addMouseListener((new MouseAdapter() {
               @Override
               public void mouseClicked(MouseEvent e) {
                   Point ciel = getCiel(e.getX(),e.getY());
                   mouseClick.clickEvent(ciel.y,ciel.x);
               }
           }));
        }
    }
}
