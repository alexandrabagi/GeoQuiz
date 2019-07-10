package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

// FINISH CHALLENGES ON PG. 112 //

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private Toast endToast;
    private boolean mIsCheater;
    private boolean tfPushed = false; // True or False button pushed
    private boolean cheatPushed = false; // Cheat button is pushed

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String IS_CHEATER = "cheater";
    private static final String IS_TF_PUSHED = "pushed";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int numberOfRight = 0;
    private int numberOfCheat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        Log.d(TAG, ("onCreate(Bundle) called and current index is " + mCurrentIndex) );
        setContentView(R.layout.activity_quiz);

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);

        if (savedInstanceState != null) {
            Log.i(TAG, "savedInstanceState");
            Log.i(TAG, "index from Bundle is " + savedInstanceState.getInt(KEY_INDEX, 0) );
            Log.i(TAG, "is cheater? " + savedInstanceState.getBoolean(IS_CHEATER));
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(IS_CHEATER);
             if (mIsCheater) mCheatButton.setEnabled(false);
            tfPushed = savedInstanceState.getBoolean(IS_TF_PUSHED);
            //if (tfPushed) {
            //    mTrueButton.setEnabled(false);
            //    mFalseButton.setEnabled(false);
            //}
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // public static Toast makeText(Context context, int resId, int duration)
                // Toast.makeText(QuizActivity.this,
                //                R.string.correct_toast,
                //                Toast.LENGTH_SHORT).show();
                checkAnswer(true);
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                tfPushed = true;
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast toast = Toast.makeText(QuizActivity.this,
                //        R.string.incorrect_toast,
                //        Toast.LENGTH_SHORT);
                // toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 150);
                // toast.show();
                checkAnswer(false);
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
                tfPushed = true;
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
                // mCheatButton.setEnabled(false);
                cheatPushed = true;
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex < mQuestionBank.length-1) {
                    mCurrentIndex = (mCurrentIndex + 1);
                    mIsCheater = false;
                    updateQuestion();
                }
                else {
                    if (endToast == null) {
                        endToast = Toast.makeText(QuizActivity.this,
                                "You have earned " + 0 + " points",
                                Toast.LENGTH_SHORT);
                    }
                    endToast.setText("You have earned " + numberOfRight + " points!");
                    endToast.show();
                    if (numberOfCheat > 0) {
                        Toast extraToast = Toast.makeText(QuizActivity.this,
                                "But you have cheated " + 0 + " time(s)!",
                                Toast.LENGTH_SHORT);
                        extraToast.setText("But you have cheated " + numberOfCheat + " time(s)!");
                        extraToast.show();
                        Toast lastToast = Toast.makeText(QuizActivity.this,
                                ":(((((((((((((",
                                Toast.LENGTH_SHORT);
                        lastToast.setText(":(((((((((((((");
                        lastToast.show();
                    }
                }

                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);
                mCheatButton.setEnabled(true);

                tfPushed = false;
                cheatPushed = false;
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex > 0) {
                    mCurrentIndex = mCurrentIndex - 1;
                }
                else {
                    mCurrentIndex = mQuestionBank.length - 1;
                }
                updateQuestion();
            }
        });
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
            numberOfRight++;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                if (numberOfRight < mQuestionBank.length) numberOfRight++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        // Log.d - debug
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            Log.i(TAG, "mIsCheater from the other activity " + mIsCheater);
            mCheatButton.setEnabled(false);
            if (mIsCheater) numberOfCheat++;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Log.i - info
        Log.i(TAG, ("onSaveInstanceState and current index was " + mCurrentIndex));
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(IS_CHEATER, mIsCheater);
        Log.i(TAG, "onSavedInstanceState passed " + mIsCheater);
        savedInstanceState.putBoolean(IS_TF_PUSHED, tfPushed);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
