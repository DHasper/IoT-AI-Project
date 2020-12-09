import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;

import com.sun.net.httpserver.HttpServer;

import connectfourgame.ConnectFourBoardLogic;
import connectfourgame.ConnectFourGameLogic;
import connectfourgame.ConnectFourMinimaxStrategyMulti;

public class ConnectFourAPI {

    private Random random = new Random();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Endpoint for getting move using connect four AI
        server.createContext("/getmove", (req -> {
            // Make sure it is a get request, else return 405
            if (req.getRequestMethod().equals("GET")) {
                String[] params = req.getRequestURI().getRawQuery().split("=");
                String moveStr = params.length > 1 ? params[1] : "";

                ConnectFourAPI API = new ConnectFourAPI();
                String move = String.valueOf(API.getMove(moveStr));

                req.sendResponseHeaders(200, move.getBytes().length);
                OutputStream out = req.getResponseBody();
                out.write(move.getBytes());
                out.flush();
                req.close();
            } else {
                req.sendResponseHeaders(405, -1);
            }
        }));

        server.setExecutor(null);
        server.start();

        System.out.println("Started connect four server");
    }

    private int getMove(String moves) {
        int res = 0;
        try {
            int millis = random.nextInt(1500) + 500;
            Thread.sleep(millis);

            ConnectFourMinimaxStrategyMulti ai = new ConnectFourMinimaxStrategyMulti();
            ConnectFourBoardLogic board = new ConnectFourBoardLogic();
            ConnectFourGameLogic logic = new ConnectFourGameLogic();
            logic.setBoard(board);

            int i = 0;
            for (char move : moves.toCharArray()){
                int pos = Integer.parseInt(String.valueOf(move));
                logic.doMove(pos, ((i % 2) + 1));
                i++;
            }

            board.printBoard();
            if(logic.gameOver() != 0) return -1;
            res = ai.getBestMove(board, ((i % 2) + 1));
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }
}
