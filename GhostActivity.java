/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String currentWord = "";
    private int computerScore = 0;
    private int playerScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        // keyboard attempt
        //   this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // CODE GOES BELOW HERE
        // SimpleDictionary sDictionary = new SimpleDictionary(getAssets().open("words.txt"));
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();    // Randomly chooses who's turn it is
        TextView text = (TextView) findViewById(R.id.ghostText);
        currentWord = "";
        text.setText(currentWord);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Code here
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public boolean challengeButton(View view) {
        // challenge button when pressed
        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        String word = text.getText().toString();
        TextView score1 = (TextView) findViewById(R.id.playerScore);
        TextView score2 = (TextView) findViewById(R.id.computerScore);

        if (word.length() <= 3) {
            label.setText("You must challenge after 4 letters");
            return true;
        }
        if (word.length() >= 4 && dictionary.isWord(word)) {
            label.setText("Player Wins!");
            playerScore++;
            String player = "Player Score: " + playerScore;
            score1.setText(player);
            return true;    // Don't know if boolean is correct or even useful
        }

        String possibleWord = dictionary.getGoodWordStartingWith(word);
        if (possibleWord == null) {
            label.setText("Player wins!");
            playerScore++;
            String player = "Player Score: " + playerScore;
            score1.setText(player);
            return true;
        }
        else {
            // if possibleWord is a word then display that word and tell user they lost
            String message = "Sorry Computer wins, a possible word is ";
            String var = message + possibleWord;
            label.setText(var);
            computerScore++;
            String comp = "Computer Score : " + computerScore;
            score2.setText(comp);
            return true;
        }
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        // For keeping track of score
        TextView score2 = (TextView) findViewById(R.id.computerScore);

        String vic = "Computer Wins!";

        TextView text = (TextView) findViewById(R.id.ghostText);
        String prefix = text.getText().toString();  // Retrieve word fragment

        String wordSelected = dictionary.getGoodWordStartingWith(prefix);   // word that could be made

        if (prefix.length() >= 4) {
            if (dictionary.isWord(prefix)) {
                label.setText(vic); // Update the gameStatus to victory
                computerScore++;
                String comp = "Computer Score : " + computerScore;
                score2.setText(comp);
                return;
            }
        }

        if (wordSelected == null && !prefix.equals("")) {
            // if str is null then challenge the user's fragment and declare victory
            // need to figure out how to do the challenge
            computerScore++;
            String comp = "Computer Score : " + computerScore;
            score2.setText(comp);
            label.setText(vic);
        }
        else {
            text.setText(prefix + wordSelected.charAt(prefix.length()));
            userTurn = true;
            label.setText(USER_TURN);
        }
    }
    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /* YOUR CODE GOES HERE */
        char c = (char) event.getUnicodeChar();
        TextView text = (TextView) findViewById(R.id.ghostText);
        String word = text.getText().toString();    // Retrieve Word Fragment

        if (c >= 'a' && c <= 'z') {
            word += c;
            TextView label = (TextView) findViewById(R.id.gameStatus);
            // TextView text = (TextView) findViewById(R.id.ghostText);
            text.setText(word);
            computerTurn();
//            if (dictionary.isWord(word)) {
//                label.setText("You Lose");
//            }
//            else {
//                label.setText("Computer's Turn");
//                computerTurn();
//            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
