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
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

//HAVE A BUG THAT SOMETIMES GIVES COMPUTER A FREE TURN ON ROTATE
//RESET ALSO DOES NOT START NEW GAME UNLESS SOMEONE HAS WON
//ALL THIS RELATED TO SAVING GAME STATE

public class GhostActivity extends AppCompatActivity{
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private static boolean userTurn = false;
    private Random random = new Random();
    private Button challenge;

    private TextView ghostText;
    private TextView gameStatus;
    private String fragment;

    static final String TEXT_FRAGMENT = "currentWord";
    static final String GAME_STATE = "gameState";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            fragment = savedInstanceState.getString(TEXT_FRAGMENT);
        }else{
            fragment = "";
        }

        setContentView(R.layout.activity_ghost);

        AssetManager assetManager = getAssets();

        try {
            InputStream is = assetManager.open("words.txt");
            //dictionary = new SimpleDictionary(is);
            dictionary = new FastDictionary(is);
        }catch(IOException e){
            System.out.println(e.getCause());
        }

        ghostText = (TextView) findViewById(R.id.ghostText);
        gameStatus = (TextView) findViewById(R.id.gameStatus);
        challenge = (Button) findViewById(R.id.challenge);

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
        challenge.setEnabled(true);

        userTurn = random.nextBoolean();
        ghostText.setText(fragment);

        if (userTurn) {
            gameStatus.setText(USER_TURN);
        } else {
            gameStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        if(fragment.length() >= 4 && dictionary.isWord(fragment)) {
            gameStatus.setText("COMPUTER WINS!");
            fragment = "";
            challenge.setEnabled(false);
        }else {

            String longer = dictionary.getGoodWordStartingWith(fragment);

            if (longer == null) {
                //Challenge - no word can be made from user turn
                gameStatus.setText("COMPUTER WINS!");
                fragment = "";
                challenge.setEnabled(false);
            } else {
                fragment += longer.charAt(fragment.length());
                ghostText.setText(fragment);

                userTurn = true;
                gameStatus.setText(USER_TURN);
            }
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
        if(keyCode >= 29 && keyCode <= 54){
            fragment += (char) event.getUnicodeChar();
            ghostText.setText(fragment);

            computerTurn();

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    public void onChallenge(View v){
        if(fragment.length() >= 4 && dictionary.isWord(fragment)){
            gameStatus.setText("USER WINS!");
            fragment = "";
            challenge.setEnabled(false);
        } else {

            String longer = dictionary.getAnyWordStartingWith(fragment);

            if (longer == null) {
                gameStatus.setText("USER WINS!");
                fragment = "";
                challenge.setEnabled(false);
            } else {
                ghostText.setText(longer);
                gameStatus.setText("COMPUTER WINS");
                fragment = "";
                challenge.setEnabled(false);
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(TEXT_FRAGMENT, fragment);
    }

    public static boolean getTurn(){
        return userTurn;
    }
}
