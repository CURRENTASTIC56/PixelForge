/*
 * PixelForge Minecraft Server Manager - Player Notification Service
 * Owner: Ishaan Dnyaneshwar Jadhav
 * Developer: Ishaan Dnyaneshwar Jadhav
 * Copyright © 2025 Ishaan Dnyaneshwar Jadhav. All rights reserved.
 */

package com.pixelforge.minecraftserver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerNotificationService extends Service {
    private Timer timer;
    private long lastFileLength = 0;
    private final String LOG_FILE_PATH = "/data/data/com.termux/files/home/mcserver/logs/latest.log";
    private final String CHANNEL_ID = "PlayerNotificationsChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkLogFile();
            }
        }, 0, 10000); // check every 10 seconds
    }

    private void checkLogFile() {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) return;
        try (RandomAccessFile file = new RandomAccessFile(logFile, "r")) {
            file.seek(lastFileLength);
            String line;
            while ((line = file.readLine()) != null) {
                String decodedLine = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                if (decodedLine.contains("joined the game")) {
                    String player = extractPlayerName(decodedLine);
                    sendNotification("Player Joined", player + " joined the game");
                } else if (decodedLine.contains("left the game")) {
                    String player = extractPlayerName(decodedLine);
                    sendNotification("Player Left", player + " left the game");
                }
            }
            lastFileLength = file.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractPlayerName(String logLine) {
        int start = logLine.indexOf(":");
        if (start != -1) {
            String afterColon = logLine.substring(start + 1).trim();
            String[] parts = afterColon.split(" ");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return "Unknown Player";
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager =
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Player Notifications";
            String description = "Notifications for player join/leave events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
