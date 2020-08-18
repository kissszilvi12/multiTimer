package gerida.timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;

import androidx.core.content.ContextCompat;

public class CountDown {
    private CountDownTimer countDownTimer;
    private static boolean isPaused;
    private boolean isStarted;
    private String name;
    private long timeRemaining;
    private Button startButton;
    private Button cancelButton;
    private Vibrator vibrator;


    public CountDown(int count, final Button startButton, final Button cancelButton, Vibrator vibrator){
        timeRemaining = 0;
        this.startButton = startButton;
        this.cancelButton = cancelButton;
        isPaused = false;
        isStarted = false;

        this.vibrator = vibrator;
        this.name = startButton.getText().toString();
    }

    public void start(long count){
        if(isStarted || isPaused)
            return;
        isStarted = true;
        countDownTimer = new CountDownTimer(count, 1000){
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                if(isPaused) {
                    cancel();
                } else {
                    startButton.setText(String.format("%s  |  %d", name, millisUntilFinished / 1000));
                    timeRemaining = millisUntilFinished;
                }
            }
            @Override
            public void onFinish() {
                //Activity a = new Activity();
                //Vibrator vibrator = (Vibrator) a.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }else {
                    vibrator.vibrate(500);
                }

                isPaused = false;
                isStarted = false;
                startButton.setText(name);
                cancelButton.setEnabled(false);
                timeRemaining = 0;
                cancel();
            }
        };
        isPaused = false;
        this.cancelButton.setEnabled(true);
        countDownTimer.start();
    }

    public void cancel(){
        countDownTimer.cancel();
        isPaused = false;
        isStarted = false;
        startButton.setText(this.name);
        cancelButton.setEnabled(false);
        timeRemaining = 0;
    }

    public static void pause(){
        isPaused = true;
    }

    public void resume() {
        if (isStarted) {
            isPaused = false;
            isStarted = false;
            start(timeRemaining);
        }
    }

    public static boolean isPaused() {
        return isPaused;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setName(String name){
        this.name = name;
    }
}
