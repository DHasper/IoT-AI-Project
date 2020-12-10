package connectfourgame.strategies;

import java.util.ArrayList;
import java.util.Random;

import connectfourgame.ConnectFourBoard;
import connectfourgame.ConnectFourGame;
import gameframework.GameBoardLogic;
import gameframework.aistrategies.MinimaxStrategy;

/**
 * Really simple AI that always chooses a move that wins and always
 * chooses a move that won't cause the opponent to win in the next turn.
 * If it can't win or prevent a loss next turn, it chooses a random valid move.
 */
public class ConnectFourSimpleStrategy {

    Random random = new Random();

    public int getBestMove(GameBoardLogic board, int player) {
        ConnectFourGame logic = new ConnectFourGame();
        logic.setBoard(board);
        
        ArrayList<Integer> moves = logic.getMoves();

        for(int move : moves){
            // Do move for AI
            ConnectFourBoard tempBoard = new ConnectFourBoard();
            tempBoard.setBoard(board.getBoard());
            ConnectFourGame tempLogic = new ConnectFourGame();
            tempLogic.setBoard(tempBoard);
            tempLogic.doMove(move, player);

            // if we win return this move
            if(tempLogic.gameOver() == player){
                return move;
            }
        }

        for(int move : moves){
            // Do move for opponent
            ConnectFourBoard tempBoard = new ConnectFourBoard();
            tempBoard.setBoard(board.getBoard());
            ConnectFourGame tempLogic = new ConnectFourGame();
            tempLogic.setBoard(tempBoard);
            tempLogic.doMove(move, 3 - player);

            // if we lose return this move
            if(tempLogic.gameOver() == 3 - player){
                return move;
            }
        }

        // Choose a random move if there is no winning or not losing move.
        return moves.get(random.nextInt(moves.size()));
    }
}
