package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import connectfourgame.ConnectFourBoard;
import connectfourgame.ConnectFourGame;
import connectfourgame.strategies.ConnectFourMinimaxStrategy;
import connectfourgame.strategies.ConnectFourMinimaxStrategyMulti;
import connectfourgame.strategies.ConnectFourMinimaxStrategyTest;
import connectfourgame.strategies.ConnectFourRandomStrategy;
import connectfourgame.strategies.ConnectFourSimpleStrategy;

public class ConnectFourTest {

    private ConnectFourBoard board;
    private ConnectFourGame logic;
    private Random random;

    public static void main(String[] args) throws IOException {
        ConnectFourTest test = new ConnectFourTest();
        // test.testMoves();
        test.testAIvAI();
        // test.testAIvPlayer();
        // test.testPositions();
    }

    public ConnectFourTest() {
        this.random = new Random();

        this.board = new ConnectFourBoard();
        this.logic = new ConnectFourGame();
        logic.setBoard(board);
    }

    private void testMoves(){
        board.printBoard();
        System.out.println("available moves: " + logic.getMoves(1));

        for (int i = 0; i < 42; i++) {
            int player = (i % 2) + 1;
            ArrayList<Integer> moves = logic.getMoves(player);
            int move = moves.get(random.nextInt(moves.size()));
            logic.doMove(move, player);
        }
        board.printBoard();
        System.out.println(logic.gameOver());
        System.out.println("available moves: " + logic.getMoves(1));
    }

    private void testGame() throws IOException {

        board.printBoard();

        int i = 0;
        while(logic.gameOver() == 0){
            int player = (i % 2) + 1;

            System.out.print("Choose a move for player " + player + ":");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(' ');
            int move = Integer.parseInt(reader.readLine());
            logic.doMove(move, player);
            board.printBoard();

            i++;
        }

        System.out.println("End state : " + logic.gameOver());
    }

    private void testPositions() throws IOException {
        System.out.print("Input moves: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(' ');
        String moves = reader.readLine();

        int player = 1;
        for(char move : moves.toCharArray()){
            int pos = Integer.parseInt(String.valueOf(move)) - 1;
            System.out.println(pos);
            logic.doMove(pos, player);
            player = 3 - player;
        }

        board.printBoard();

        System.out.println("Player" + logic.gameOver() + " wins");
    }

    private void testAIvPlayer() throws IOException {

        // ConnectFourSimpleStrategy ai = new ConnectFourSimpleStrategy();
        ConnectFourMinimaxStrategyMulti ai = new ConnectFourMinimaxStrategyMulti();

        int i = 0;
        while(logic.gameOver() == 0){
            board.printBoard();

            int player = (i % 2) + 1;

            int move;
            if(player == 2){
                move = ai.getBestMove(board, player);
            } else {
                System.out.print("Choose a move for player " + player + ":");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println(' ');
                move = Integer.parseInt(reader.readLine());
            }
            
            logic.doMove(move, player);
            i++;
        }

        board.printBoard();
        System.out.println("Winner is player " + logic.gameOver());
    }

    private void testAIvAI(){

        // Start the elapsed time timer.
        long startTime = System.currentTimeMillis();

        // ConnectFourRandomStrategy
        // ConnectFourMinimaxStrategy
        // ConnectFourSimpleStrategy
        // ConnectFourMinimaxSimpleStrategy
        // ConnectFourMinimaxStrategyTest
        ConnectFourPerfectAI perfectAI = new ConnectFourPerfectAI();
        ConnectFourMinimaxStrategyMulti ai1 = new ConnectFourMinimaxStrategyMulti();
        ConnectFourSimpleStrategy ai2 = new ConnectFourSimpleStrategy();
        int player1 = 0;
        int player2 = 0;
        int draws = 0;
        int total = 0;

        for(int games = 0; games < 1000; games++){
            int i = 0;
            while(logic.gameOver() == 0){
                int player = (i % 2) + 1;
 
                // if(player == 2){
                    // board.printBoard();
                // }
    
                int move = player == 1
                    ? ai1.getBestMove(board, player)
                    : ai2.getBestMove(board, player);
                // int move = player == 1
                //     ? ai1.getBestMove(board, player)
                //     : perfectAI.getBestMove();

                // System.out.println("Player " + player + " chose " + move);
                logic.doMove(move, player);
                perfectAI.addPos(move);
    
                i++;
            }
    
            switch(logic.gameOver()){
                case 1:
                    player1++;
                    // board.printBoard();
                    break;
                case 2:
                    player2++;
                    // board.printBoard();
                    break;
                case 3:
                    draws++;
                    // board.printBoard();
                    break;
            }
            // board.printBoard();
            total++;
            // System.out.println("Winner game " + total + " : " + logic.gameOver());
            
            if(total % 50 == 0){
                printStats(player1, player2, draws, startTime);
            }
            
            // System.out.println(perfectAI.getPos());
            board.resetBoard();
            perfectAI.resetPos();
        }

        printStats(player1, player2, draws, startTime);

    }

    private void printStats(int player1, int player2, int draws, long startTime){
        System.out.println("Player 1: Minimax AI \n Player 2: Random AI");
        System.out.println("Finished playing " + (player1 + player2 + draws) + " games");
        System.out.println("Player 1 wins: " + player1);
        System.out.println("Player 2 wins: " + player2);
        System.out.println("Draws: " + draws);
        System.out.println("Elapsed time: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds");
    }
}
