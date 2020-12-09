package connectfourgame.strategies;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import connectfourgame.ConnectFourBoard;
import connectfourgame.ConnectFourGame;
import gameframework.GameBoardLogic;
import gameframework.aistrategies.MinimaxStrategy;

/**
 * Really simple AI that always chooses a move that wins
 */
public class ConnectFourMinimaxStrategyMulti extends MinimaxStrategy {

    Random random = new Random();

    private static final int DEPTH = 9;
    private static final int[] MOVE_ORDER = new int[] { 3, 2, 4, 1, 5, 0, 6 };

    @Override
    public synchronized int getBestMove(GameBoardLogic board, int player) {

        ConnectFourGame logic = new ConnectFourGame();
        logic.setBoard(board);
        ArrayList<Integer> moves = logic.getMoves();

        if (logic.midEmpty()) {
            return 3;
        }

        // Map that stores the calculated scores for valid moves.
        Map<Integer, Double> results = new ConcurrentHashMap<>();

        // Whether the AI is maximizing or not
        boolean isMax = player == 1;

        for (int move : moves) {
            ConnectFourBoard tempBoard = new ConnectFourBoard();
            tempBoard.setBoard(board.getBoard());
            ConnectFourGame tempLogic = new ConnectFourGame();
            tempLogic.setBoard(tempBoard);
            tempLogic.doMove(move, player);

            MinimaxWorker worker = new MinimaxWorker(tempBoard, ConnectFourMinimaxStrategyMulti.DEPTH, !isMax, move,
                    results);
            Thread thread = new Thread(worker);
            thread.start();
        }

        // Wait untill all results are back
        while (results.size() != moves.size()) {
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Find best eval
        double bestMoveEval = player == 1 ? -1000 : 1000;
        for(double eval : results.values()){
            if(isMax && eval > bestMoveEval || !isMax && eval < bestMoveEval){
                bestMoveEval = eval;
            }
        }

        // Find moves that have the best eval
        ArrayList<Integer> bestMoves = new ArrayList<>();
        for(Map.Entry<Integer, Double> result : results.entrySet()){
            if(result.getValue() == bestMoveEval){
                bestMoves.add(result.getKey());
            }
        }

        // If there are multiple best moves choose one according to move order
        for(int move : MOVE_ORDER){
            if(bestMoves.contains(move)){
                return move;
            }
        }

        // int bestMove = -1;
        // for(Map.Entry<Integer, Double> result : results.entrySet()){
        //     double eval = result.getValue();
        //     int move = result.getKey();
        //     if(isMax && eval > bestMoveEval || !isMax && eval < bestMoveEval){
        //         bestMoveEval = eval;
        //         bestMove = move;
        //     }
        // }

        // System.out.println("Best eval : " + bestMoveEval + " best move : " + bestMove);
        return -1;
        // return bestMove;
    }

    private class MinimaxWorker implements Runnable {

        // Parameters for minimax algorithm.
        private GameBoardLogic board;
        private int depth;
        private boolean isMax;
        private int evalMove;

        // Map that stores the calculated scores for valid moves.
        private Map<Integer, Double> finalResult;

        MinimaxWorker(GameBoardLogic board, int depth, boolean isMax, int move, Map<Integer, Double> result){
            this.board = board;
            this.depth = depth;
            this.isMax = isMax;
            this.evalMove = move;
            this.finalResult = result;
        }

        @Override
        public synchronized void run() {
            double eval = miniMax(board, isMax, depth, -10000, 10000);
            finalResult.put(evalMove, eval);
        }

        private double miniMax(GameBoardLogic board, boolean isMax, int depth, double alpha, double beta){
            // Get game logic
            ConnectFourGame logic = new ConnectFourGame();
            logic.setBoard(board);
    
            if(depth == 0 || logic.gameOver() != 0){
                return evaluate(board, depth);
            }
    
            double bestEval = isMax ? -1000 : 1000;
            int player = isMax ? 1 : 2;
            // Get all valid moves
            // ArrayList<Integer> moves = logic.getMoves(player);

            for(int move : ConnectFourMinimaxStrategyMulti.MOVE_ORDER){
            // for(int move : moves){
                if(logic.isValid(move)){
                    ConnectFourBoard tempBoard = new ConnectFourBoard();
                    tempBoard.setBoard(board.getBoard());
                    ConnectFourGame tempLogic = new ConnectFourGame();
                    tempLogic.setBoard(tempBoard);
                    tempLogic.doMove(move, player);

                    double eval = miniMax(tempBoard, !isMax, depth-1, alpha, beta);

                    if(isMax){
                        if(eval > bestEval) bestEval = eval;
                        if(eval > alpha) alpha = eval;
                    } else {
                        if(eval < bestEval) bestEval = eval;
                        if(eval < beta) beta = eval;
                    }

                    if(beta <= alpha) break;
                }
            }
            return bestEval;
        }
    
        private double evaluate(GameBoardLogic board, int depth){
            ConnectFourGame logic = new ConnectFourGame();
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
                    // return (this.depth - depth + 1) + combo;
                    return (depth + 1) + combo;
                case 2:
                    return (-depth - 1) + combo;
                default:
                    return 0 + combo;
            }
        }

    }
}
