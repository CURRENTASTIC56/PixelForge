/*
 * PixelForge Minecraft Server Manager - Log Activity
 * Owner: Ishaan Dnyaneshwar Jadhav
 * Developer: Ishaan Dnyaneshwar Jadhav
 * Copyright © 2025 Ishaan Dnyaneshwar Jadhav. All rights reserved.
 */

package com.pixelforge.minecraftserver;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class LogActivity extends AppCompatActivity {
    private TextView logView;
    private static final String COMPANY_NAME = "PixelForge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        logView = findViewById(R.id.logView);
        loadLogs();
    }

    private void loadLogs() {
        File logFile = new File("/data/data/com.termux/files/home/mcserver/logs/latest.log");
        StringBuilder logs = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }
            logView.setText(logs.toString());
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                .setTitle("Error - " + COMPANY_NAME)
                .setMessage("Error loading logs: " + e.getMessage())
                .setPositiveButton("OK", null)
                .show();
        }
    }
}
