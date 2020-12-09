package connectfourgame;

import java.util.ArrayList;
import java.util.Random;

import gameframework.GameBoardLogic;
import gameframework.aistrategies.MinimaxStrategy;

/**
 * Really simple AI that always chooses a move that wins
 */
public class ConnectFourMinimaxStrategy extends MinimaxStrategy {

    Random random = new Random();

    private static final int DEPTH = 5;

    @Override
    public int getBestMove(GameBoardLogic board, int player) {

        boolean isMax;
        double bestMoveEval;
        int bestMove = -1;
        if(player == 1){
            bestMoveEval = -1000;
            isMax = true;
        } else {
            isMax = false;
            bestMoveEval = 1000;
        }

        ConnectFourGameLogic logic = new ConnectFourGameLogic();
        logic.setBoard(board);
        ArrayList<Integer> moves = logic.getMoves(player);

        if(logic.midEmpty()){
            return 3;
        }

        for(int move : moves){
            ConnectFourBoardLogic tempBoard = new ConnectFourBoardLogic();
            tempBoard.setBoard(board.getBoard());
            ConnectFourGameLogic tempLogic = new ConnectFourGameLogic();
            tempLogic.setBoard(tempBoard);
            tempLogic.doMove(move, player);
            double moveEval = miniMax(!isMax, DEPTH, tempBoard);
            // System.out.println("move: " + move + " eval: " + moveEval);
            if(isMax && moveEval > bestMoveEval || !isMax && moveEval < bestMoveEval){
                bestMoveEval = moveEval;
                bestMove = move;
            }
        }
        // System.out.println("Best eval : " + bestMoveEval + " best move : " + bestMove);
        return bestMove;
    }
    
    private double miniMax(boolean isMax, int depth, GameBoardLogic board){

        int player;
        double bestEval;
        if(isMax){
            bestEval = -1000;
            player = 1;
        } else {
            bestEval = 1000;
            player = 2;
        }

        ConnectFourGameLogic logic = new ConnectFourGameLogic();
        logic.setBoard(board);

        if(depth == 0 || logic.gameOver() != 0){
            return evaluate(board, depth);
        }

        ArrayList<Integer> moves = logic.getMoves(player);
        
        for(int move : moves){
            ConnectFourBoardLogic tempBoard = new ConnectFourBoardLogic();
            tempBoard.setBoard(board.getBoard());
            ConnectFourGameLogic tempLogic = new ConnectFourGameLogic();
            tempLogic.setBoard(tempBoard);
            tempLogic.doMove(move, player);
            if(isMax){
                double eval = miniMax(false, depth-1, tempBoard);
                // System.out.print(move + ":" + eval + " ");
                if(eval > bestEval){
                    bestEval = eval;
                }
            } else {
                double eval = miniMax(true, depth-1, tempBoard);
                // System.out.print(eval + " ");
                if(eval < bestEval){
                    bestEval = eval;
                }
            }
        }
        // System.out.println(' ');
        return bestEval;
    }

    private double evaluate(GameBoardLogic board, int depth){
        ConnectFourGameLogic logic = new ConnectFourGameLogic();
        logic.setBoard(board);


        ArrayList<Integer> discs = new ArrayList<>();
        for(int pos = 0; pos < board.getBoard().length; pos++){
            if(board.getBoardPos(pos) != 0){
                discs.add(pos);
            }
        }

        // Check for every non empty pos if there is 3 in a row
        double combo = 0;
        for(int discPos : discs){
            if(logic.isCombination(discPos, 4, true)){
                combo = board.getBoardPos(discPos) == 1 ? combo + 1 : combo - 1;
            }
        }
        combo = combo * 0.5;

        switch(logic.gameOver()){
            case 1:
                return (depth + 1) + combo;
            case 2:
                return (-depth - 1) + combo;
            default:
                return 0 + combo;
        }
    }
}
