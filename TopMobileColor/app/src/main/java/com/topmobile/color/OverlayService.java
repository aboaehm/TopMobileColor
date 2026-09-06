package com.topmobile.color;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.core.app.NotificationCompat;

public class OverlayService extends Service {

    private WindowManager windowManager;
    private LinearLayout overlayView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        startForeground(1, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "add_default".equals(intent.getStringExtra("action"))) {
            addOverlay(Color.argb(80, 0, 255, 0), 100, 100, 300, 300);
        }
        return START_STICKY;
    }

    private void addOverlay(int color, int x, int y, int width, int height) {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
        overlayView = new LinearLayout(this);
        overlayView.setBackgroundColor(color);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = x;
        params.y = y;
        windowManager.addView(overlayView, params);
    }

    private android.app.Notification createNotification() {
        String channelId = "overlay_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Overlay Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("TopMobile Color")
                .setContentText("الطبقات نشطة")
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }
}
