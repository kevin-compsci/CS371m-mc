 /*
 ADDITIONAL INFORMATION
    Kevin Nguyen
    kdn433 - Mobile Computing Course, 2016

    Project design and tutorial provided by Mike Scott, University of Texas at Austin. Project completion by Kevin.
 */

package tutorials.cs371m.androidtictactoe;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class AndroidTicTacToe extends AppCompatActivity {

    private static final String TAG = "Tic Tac Toe Activity";

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    private boolean mHumanGoesFirst;

    // is the game over or not?
    private boolean mGameOver;

    // Buttons making up the board
    private Button mBoardButtons[];

    // Various text display
    private TextView mInfoTextView;

    // tracks how many time each outcome occurs (human wins,
    // tie, android wins
    private WinData mWinData;

    // displays for the number of each outcome
    private TextView[] mOutcomeCounterTextViews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        // get references to all the buttons on the board
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        final int[] BUTTON_IDS = {R.id.one, R.id.two, R.id.three, R.id.four,
                R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine};
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            mBoardButtons[i] = (Button) findViewById(BUTTON_IDS[i]);
        }

        mInfoTextView = (TextView) findViewById(R.id.information);
        mGame = new TicTacToeGame();
        mHumanGoesFirst = true;
        mWinData = new WinData();
        initOutcomeTextViews();
        startNewGame();
    }

    private void initOutcomeTextViews() {
        mOutcomeCounterTextViews = new TextView[3];
        mOutcomeCounterTextViews[0] = (TextView) findViewById(R.id.human_wins_tv);
        mOutcomeCounterTextViews[1] = (TextView) findViewById(R.id.ties_tv);
        mOutcomeCounterTextViews[2] = (TextView) findViewById(R.id.android_wins_tv);
        Log.d(TAG, "text view array: " + Arrays.toString(mOutcomeCounterTextViews));
    }

    // Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        mGame.clearBoard();

        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        if (mHumanGoesFirst) {
            // Human goes first
            mInfoTextView.setText(R.string.human_first);
        }
        else {
            // Android goes first
            computerMove();
        }
    }

    private void setMove(char player, int location) {

        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER) {
            mBoardButtons[location].setTextColor(getResources().getColor(R.color.xPieceColor));
        } else {
            mBoardButtons[location].setTextColor(getResources().getColor(R.color.oPieceColor));
        }
    }

    private void computerMove() {
        mInfoTextView.setText(R.string.computer_turn);
        int move = mGame.getComputerMove();
        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
        int winner = mGame.checkForWinner();
        if (winner == 0) {
            mInfoTextView.setText(R.string.human_turn);
        } else {
            handleEndGame(winner);
        }
    }

    private void handleEndGame(int winner) {
        WinData.Outcome outcome;
        if (winner == 1) {
            outcome = WinData.Outcome.TIE;
            mInfoTextView.setText(R.string.result_tie);
        } else if (winner == 2) {
            outcome = WinData.Outcome.HUMAN;
            mInfoTextView.setText(R.string.result_human_wins);
        }
        else {
            outcome = WinData.Outcome.ANDROID;
            mInfoTextView.setText(R.string.result_computer_wins);
        }
        mWinData.incrementWin(outcome);
        int index = outcome.ordinal();
        Log.d(TAG, "text view array: " + Arrays.toString(mOutcomeCounterTextViews));
        String display = "" + mWinData.getCount(outcome);
        mOutcomeCounterTextViews[index].setText(display);
        mGameOver = true;
        mHumanGoesFirst = !mHumanGoesFirst;
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    computerMove();
                }
                else {
                    handleEndGame(winner);
                }
            }
        }
    }

    // Code below this point was added in tutorial 3.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                int currentDifficulty = mGame.getDifficultyLevel().ordinal();
                DifficultyDialogFragment difficultyDialogFragment
                        = DifficultyDialogFragment.newInstance(currentDifficulty);
                difficultyDialogFragment.show(fm, "difficulty");
                return true;
            case R.id.quit:
                QuitDialogFragment quitDialogFragment = new QuitDialogFragment();
                quitDialogFragment.show(fm, "quit");
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set the difficulty. Presumably called by DifficultyDialogFragment;
     * @param difficulty The new difficulty for the game.
     */
    public void setDifficulty(int difficulty) {
        // check bounds;
        if (difficulty < 0 || difficulty >= TicTacToeGame.DifficultyLevel.values().length) {
            Log.d(TAG, "Unexpected difficulty: " + difficulty + "." +
                    " Setting difficulty to Easy / 0.");
            difficulty = 0; // if out of bounds set to 0
        }
        TicTacToeGame.DifficultyLevel newDifficulty
                = TicTacToeGame.DifficultyLevel.values()[difficulty];

        mGame.setDifficultyLevel(newDifficulty);
        String message = "Difficulty set to " +
                newDifficulty.toString().toLowerCase() + " .";

        // Display the selected difficulty level
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();

    }
}
