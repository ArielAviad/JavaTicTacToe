
import java.awt.*;

public interface IPlayer {
    Point makeMove();

    void setState(State s);

    State getState();
}
