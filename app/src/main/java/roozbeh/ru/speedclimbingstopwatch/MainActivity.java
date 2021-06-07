package roozbeh.ru.speedclimbingstopwatch;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView textView ;
    Button start;
    ImageButton resetBtn, voiceBtn, settingBtn;
    boolean isResume = true;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;

    Handler handler;

    int Seconds, MilliSeconds ;

    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        start = findViewById(R.id.btnCenter);
        resetBtn = findViewById(R.id.restBtn);
        voiceBtn = findViewById(R.id.voiceBtn);
        settingBtn = findViewById(R.id.settingBtn);






        resetBtn.setEnabled(false);

        handler = new Handler() ;



        voiceBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim2);
                animation.setFillAfter(true);
                animation.setDuration(250);

                voiceBtn.startAnimation(animation);

                promptSpeechInput();
                hideSystemUI();
            }
        });


        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim2);
                animation.setFillAfter(true);
                animation.setDuration(250);
                settingBtn.startAnimation(animation);
                presentActivity(v);
                hideSystemUI();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim2);
                animation.setFillAfter(true);
                animation.setDuration(250);
                hideSystemUI();
                start.startAnimation(animation);

                if (isResume){
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    isResume = false;
                    start.setText("Stop");
                    resetBtn.setEnabled(false);
                    settingBtn.setEnabled(false);
                    voiceBtn.setEnabled(false);
                } else {
                    TimeBuff += MillisecondTime;
                    handler.removeCallbacks(runnable);
                    isResume = true;
                    start.setText("Start");
                    resetBtn.setVisibility(View.VISIBLE);
                    resetBtn.setEnabled(true);
                    settingBtn.setEnabled(true);
                    voiceBtn.setEnabled(true);
                }


            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim2);
                animation.setFillAfter(true);
                animation.setDuration(250);
                resetBtn.startAnimation(animation);
                hideSystemUI();
                textView.setText("0.000");
                Seconds = 0;
                MilliSeconds = 0;
                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;

                resetBtn.setEnabled(false);
            }
        });

    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(SettingsActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(SettingsActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String result2 = result.get(0);
                    if (result2.indexOf("start") != -1){
                        StartTime = SystemClock.uptimeMillis();
                        handler.postDelayed(runnable, 0);
                        isResume = false;
                        start.setText("Stop");
                        resetBtn.setEnabled(false);
                        settingBtn.setEnabled(false);
                        voiceBtn.setEnabled(false);
                    } else {
                        TimeBuff += MillisecondTime;
                        handler.removeCallbacks(runnable);
                        isResume = true;
                        start.setText("Start");
                        resetBtn.setVisibility(View.VISIBLE);
                        resetBtn.setEnabled(true);
                        settingBtn.setEnabled(true);
                        voiceBtn.setEnabled(true);
                    }

                }
                break;
            }

        }
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            MilliSeconds = (int) (UpdateTime % 1000);

            textView.setText(String.format("%01d", Seconds) + "."
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}