import javax.swing.*;
import java.util.Observer;

/**
 * Created by ariel on 25/04/17.
 */

public interface ITicView extends Observer {
    //functions to set the mouse action.
    void addMouseClick(IMouseEvents mouseEvents);
    //sets a new menuBar
    void setNewMenuBar(JMenuBar newMenuBar);
    //sets the new status of the game.
    void setMsg(String newStatus);


}
