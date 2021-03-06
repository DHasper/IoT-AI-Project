package connectfourgame.strategies;

import java.util.ArrayList;
import java.util.Random;

import connectfourgame.ConnectFourGame;
import gameframework.GameBoardLogic;

/**
 * Chooses a random valid move
 */
public class ConnectFourRandomStrategy {

    Random random = new Random();

    public int getBestMove(GameBoardLogic board) {
        ConnectFourGame game = new ConnectFourGame();
        game.setBoard(board);
        ArrayList<Integer> moves = game.getMoves();

        return moves.get(random.nextInt(moves.size()));
    }
}
