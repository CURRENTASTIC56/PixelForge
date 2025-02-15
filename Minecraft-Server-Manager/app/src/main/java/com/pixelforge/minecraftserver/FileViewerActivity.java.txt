/*
 * PixelForge Minecraft Server Manager - File Viewer Activity
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

public class FileViewerActivity extends AppCompatActivity {
    private static final String COMPANY_NAME = "PixelForge";
    private TextView tvFileContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer);
        tvFileContent = findViewById(R.id.tvFileContent);
        String filePath = getIntent().getStringExtra("filepath");
        if (filePath != null) {
            loadFileContent(filePath);
        } else {
            showError("No file path provided.");
        }
    }

    private void loadFileContent(String filePath) {
        File file = new File(filePath);
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            tvFileContent.setText(content.toString());
        } catch (Exception e) {
            showError("Error reading file: " + e.getMessage());
        }
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
            .setTitle("Error - " + COMPANY_NAME)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
}
