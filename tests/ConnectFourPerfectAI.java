package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ConnectFourPerfectAI {

    private ArrayList<Integer> positions = new ArrayList<>();

    public void addPos(int pos) {
        // Add 1 to the pos because the API uses position 1-7, while we use 0-6
        positions.add(pos + 1);
    }

    public int getBestMove() {
        try {
            // Start connection
            URL url = new URL("https://connect4.gamesolver.org/solve?pos=" + getPos());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Read response
            InputStream responseStream = connection.getInputStream();
            BufferedReader responseStreamBuffered = new BufferedReader(new InputStreamReader(responseStream));

            String response = responseStreamBuffered.readLine();
            String scoresString = response.substring(response.indexOf('[') + 1, response.indexOf(']'));
            String[] scoresArray = scoresString.split(",");

            int bestMove = 0;
            int bestScore = -1000;
            int i = 0;
            for(String scoreString : scoresArray){
                int score = Integer.parseInt(scoreString);
                if (score > bestScore && score != 100){
                    bestMove = i;
                    bestScore = score;
                }
                i++;
            }

            responseStreamBuffered.close();
            return bestMove;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void resetPos(){
        positions.clear();
    }

    public String getPos(){
        String res = "";
        for(int pos : positions){
            res = res.concat(Integer.toString(pos));
        }
        return res;
    }
}
