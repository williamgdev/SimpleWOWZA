package com.randmcnally.bb.poc.interactor;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.SeekBar;

import com.randmcnally.bb.poc.custom.BBPlayer;


public class TimerInteractor {
    private static final TimerInteractor instance = new TimerInteractor();
    private static final String TAG = "TimerInteractor ->";
    private SeekBar seekBar;
    private BBPlayer bbPlayer;
    private long millisDuration;
    private long delay;
    private CountDownTimer timer;
    private String audioFile;

    public static TimerInteractor getInstance(long millisDuration, long delay) {
        instance.millisDuration = millisDuration;
        instance.delay = delay;
        return instance;
    }

    private TimerInteractor() {
    }

    public void pause() {
        timer.cancel();
        seekBar = null;
    }

    public void play(SeekBar seekBar, BBPlayer bbPlayer) throws Exception {
        if (this.seekBar != null){
            throw new Exception("Error");
        }
        this.seekBar = seekBar;
        this.bbPlayer = bbPlayer;
        audioFile = bbPlayer.getAudioUrl();

        startTimer();
    }

    private void startTimer() {
        timer = new CountDownTimer(millisDuration, delay) {
            @Override
            public void onTick(long millisUntilFinished) {
                int bbPlayerTime = bbPlayer.getTimeElapsed();
                if (bbPlayerTime > seekBar.getProgress() &&
                        bbPlayerTime <= seekBar.getMax()) {
                    seekBar.setProgress(bbPlayer.getTimeElapsed());
                }else if(seekBar.getProgress() > 0 && bbPlayerTime == 0 && ((seekBar.getProgress() + (millisUntilFinished / delay)) < seekBar.getMax())) {
                    seekBar.setProgress(seekBar.getMax() - (int) (millisUntilFinished / delay));
                }
                Log.d(TAG, "onTick: seekBar-" + seekBar.getProgress() + " bbplayer-" + bbPlayerTime);
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: seekBar-" + seekBar.getMax() + " bbplayer-" + seekBar.getProgress());
                seekBar.setProgress((int) millisDuration);
            }
        };
        timer.start();
    }

    public void stop() {
        timer.onFinish();
        seekBar = null;
    }
}
