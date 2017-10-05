package timer;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class TimerService extends Service {

    private final static String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "your_package_name.countdown_br";
    Intent intent = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                intent.putExtra("countdown", millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                sendBroadcast(intent);
            }
        };
        cdt.start();
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}