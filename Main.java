import javax.swing.*;

public class Main {
   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            try {
                AbstractTicModel m = new TicModel();
                ITicView v = new TicView(m);
                TicController c = new TicController(m,v);
                c.gameStart();
            } catch (TicController.NullModelViewException e) {
                System.err.println(e.getMessage());
            }

        });
    }
}
