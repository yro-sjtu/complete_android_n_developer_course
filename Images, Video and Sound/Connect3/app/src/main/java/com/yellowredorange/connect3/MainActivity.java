package com.yellowredorange.connect3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import static com.yellowredorange.connect3.R.id.playAgainLayout;

public class MainActivity extends AppCompatActivity {

    // activePlayer: 0 - yellow, 1 - red
    int activePlayer = 0;

    boolean gameOver = false;

    int[] gameState = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

    int[][] winningPositions = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};

    public void dropIn(View view) {
        ImageView counter = (ImageView) view;
        int tappedCounter = Integer.parseInt(counter.getTag().toString());
        if(gameState[tappedCounter] == -1 && !gameOver) {
            gameState[tappedCounter] = activePlayer;
            counter.setTranslationY(-1000f);
            if(activePlayer == 0) {
                counter.setImageResource(R.drawable.yellow);
            } else {
                counter.setImageResource(R.drawable.red);
            }
            activePlayer = 1 - activePlayer;
            counter.animate().translationYBy(1000f).rotationBy(360f).setDuration(300);

            for(int[] winningPosition: winningPositions) {
                if(gameState[winningPosition[0]] != -1
                        && gameState[winningPosition[0]] == gameState[winningPosition[1]]
                        && gameState[winningPosition[1]] == gameState[winningPosition[2]]) {
                    TextView msg = (TextView) findViewById(R.id.winningMessage);
                    String winner = "Red";
                    if(gameState[winningPosition[0]] == 0) {
                        winner = "Yellow";
                    }
                    msg.setText(winner + " has won!");
                    LinearLayout playAgainLayout = (LinearLayout) findViewById(R.id.playAgainLayout);
                    playAgainLayout.setVisibility(View.VISIBLE);
                    gameOver = true;
                }
            }

            if(!gameOver) {
                boolean isDraw = true;
                for(int i = 0; i < gameState.length; i++) {
                    if(gameState[i] == -1) {
                        isDraw = false;
                    }
                }
                if(isDraw) {
                    TextView msg = (TextView) findViewById(R.id.winningMessage);
                    msg.setText("It's a draw");
                    LinearLayout playAgainLayout = (LinearLayout) findViewById(R.id.playAgainLayout);
                    playAgainLayout.setVisibility(View.VISIBLE);
                    gameOver = true;
                }
            }
        }
    }

    public void playAgain(View view) {
        gameOver = false;

        LinearLayout playAgainLayout = (LinearLayout) findViewById(R.id.playAgainLayout);
        playAgainLayout.setVisibility(View.INVISIBLE);

        Arrays.fill(gameState, -1);
        activePlayer = 0;

        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        for(int i = 0; i < gridLayout.getChildCount(); i++) {
            ((ImageView)gridLayout.getChildAt(i)).setImageResource(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
