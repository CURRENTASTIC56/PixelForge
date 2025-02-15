/*
 * PixelForge Minecraft Server Manager - Configuration Activity
 * Owner: Ishaan Dnyaneshwar Jadhav
 * Developer: Ishaan Dnyaneshwar Jadhav
 * Copyright © 2025 Ishaan Dnyaneshwar Jadhav. All rights reserved.
 */

package com.pixelforge.minecraftserver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class ConfigurationActivity extends AppCompatActivity {
    private static final String COMPANY_NAME = "PixelForge";
    private EditText etConfig;
    private ProgressDialog progressDialog;
    private final String CONFIG_PATH = "/data/data/com.termux/files/home/mcserver/server.properties";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        etConfig = findViewById(R.id.etConfig);
        Button btnSave = findViewById(R.id.btnSaveConfig);
        loadConfiguration();
        btnSave.setOnClickListener(v -> {
            showLoading("Saving configuration...");
            saveConfiguration();
        });
    }

    private void loadConfiguration() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            etConfig.setText("server-port=25565\nenable-command-block=true\nmax-players=10");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            StringBuilder config = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                config.append(line).append("\n");
            }
            etConfig.setText(config.toString());
        } catch (IOException e) {
            showError("Error loading configuration: " + e.getMessage());
        }
    }

    private void saveConfiguration() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(CONFIG_PATH)))) {
            writer.write(etConfig.getText().toString());
            hideLoading();
            Toast.makeText(this, "Configuration saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            hideLoading();
            showError("Error saving configuration: " + e.getMessage());
        }
    }

    private void showLoading(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
            .setTitle("Error - " + COMPANY_NAME)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
}
