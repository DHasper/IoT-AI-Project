package connectfourgame.strategies;

import java.util.ArrayList;
import java.util.Random;

import connectfourgame.ConnectFourGameLogic;
import gameframework.GameBoardLogic;

/**
 * Chooses a random valid move
 */
public class ConnectFourRandomStrategy {

    Random random = new Random();

    public int getBestMove(GameBoardLogic board, int player) {
        ConnectFourGameLogic logic = new ConnectFourGameLogic();
        logic.setBoard(board);
        ArrayList<Integer> moves = logic.getMoves(player);

        return moves.get(random.nextInt(moves.size()));
    }
}
