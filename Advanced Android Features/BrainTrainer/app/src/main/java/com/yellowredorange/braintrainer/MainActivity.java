package com.yellowredorange.braintrainer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    Button playAgainButton;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    TextView resultTextView;
    TextView pointsTextView;
    TextView sumTextView;
    TextView timerTextView;

    RelativeLayout gameRelativeLayout;

    ArrayList<Integer> answers = new ArrayList<>();
    int locOfCorrectAnswer;
    int score = 0;
    int total = 0;
    boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        sumTextView = (TextView) findViewById(R.id.sumTextView);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        pointsTextView = (TextView) findViewById(R.id.pointsTextView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);

        gameRelativeLayout = (RelativeLayout) findViewById(R.id.gameRelativeLayout);
    }

    public void start(View view) {
        startButton.setVisibility(View.INVISIBLE);
        gameRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
        init();
    }

    public void chooseAnswer(View view) {
        if(!gameOver) {
            Button button = (Button) view;
            total++;
            int choice = Integer.parseInt(button.getText().toString());
            if(choice != answers.get(locOfCorrectAnswer)) {
                resultTextView.setText("Wrong!");
            } else {
                score++;
                resultTextView.setText("Correct!");
            }
            pointsTextView.setText(score + "/" + total);
            generateQuestion();
        }
    }

    public void generateQuestion() {
        Random rand = new Random();
        int a = rand.nextInt(21);
        int b = rand.nextInt(21);

        sumTextView.setText(String.valueOf(a) + " + " + String.valueOf(b));

        locOfCorrectAnswer = rand.nextInt(4);

        answers.clear();

        int wrongAnswer;

        for(int i = 0; i < 4; i++) {
            if(locOfCorrectAnswer != i) {
                do {
                    wrongAnswer = rand.nextInt(41);
                } while(wrongAnswer == a + b);
                answers.add(wrongAnswer);
            } else {
                answers.add(a + b);
            }
        }

        button0.setText(answers.get(0).toString());
        button1.setText(answers.get(1).toString());
        button2.setText(answers.get(2).toString());
        button3.setText(answers.get(3).toString());
    }

    public void playAgain(View view) {
        init();
    }

    private void init() {
        score = 0;
        total = 0;
        gameOver = false;

        timerTextView.setText("30s");
        pointsTextView.setText("0/0");
        resultTextView.setText("");

        generateQuestion();

        new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timerTextView.setText("0s");
                resultTextView.setText("Your score is :" + score + "/" + total);
                gameOver = true;
                playAgainButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}
