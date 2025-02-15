/*
 * PixelForge Minecraft Server Manager
 * Owner: Ishaan Dnyaneshwar Jadhav
 * Developer: Ishaan Dnyaneshwar Jadhav
 * Copyright © 2025 Ishaan Dnyaneshwar Jadhav. All rights reserved.
 *
 * Licensed under the PixelForge License.
 */

package com.pixelforge.minecraftserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String COMPANY_NAME = "PixelForge";
    private TextView tvStatus;
    private EditText etRam;
    private Spinner spinnerServerType;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        etRam = findViewById(R.id.etRam);
        spinnerServerType = findViewById(R.id.spinnerServerType);

        findViewById(R.id.btnStartServer).setOnClickListener(v -> {
            try {
                showLoading("Starting server...");
                startServer();
                // Start notification service
                startService(new Intent(this, PlayerNotificationService.class));
            } catch (Exception e) {
                hideLoading();
                showErrorDialog("Error starting server: " + e.getMessage());
            }
        });
        findViewById(R.id.btnStopServer).setOnClickListener(v -> {
            try {
                showLoading("Stopping server...");
                stopServer();
            } catch (Exception e) {
                hideLoading();
                showErrorDialog("Error stopping server: " + e.getMessage());
            }
        });
        findViewById(R.id.btnPortForward).setOnClickListener(v -> {
            try {
                showLoading("Enabling port forwarding...");
                enablePortForwarding();
            } catch (Exception e) {
                hideLoading();
                showErrorDialog("Error enabling port forwarding: " + e.getMessage());
            }
        });
        findViewById(R.id.btnViewLogs).setOnClickListener(v -> viewConsoleLogs());
        findViewById(R.id.btnBackup).setOnClickListener(v -> {
            try {
                showLoading("Creating backup...");
                backupWorld();
            } catch (Exception e) {
                hideLoading();
                showErrorDialog("Error backing up world: " + e.getMessage());
            }
        });
        findViewById(R.id.btnManageFiles).setOnClickListener(v -> {
            Intent intent = new Intent(this, FileManagerActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnManagePlayers).setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerManagementActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnConfigureServer).setOnClickListener(v -> {
            Intent intent = new Intent(this, ConfigurationActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnManagePlugins).setOnClickListener(v -> {
            Intent intent = new Intent(this, PluginManagementActivity.class);
            startActivity(intent);
        });
    }

    private void startServer() {
        String ram = etRam.getText().toString().trim();
        if (ram.isEmpty()) {
            ram = "1024"; // default to 1GB
        }
        String serverType = spinnerServerType.getSelectedItem().toString();
        String serverUrl;
        switch (serverType) {
            case "PaperMC":
                serverUrl = "https://papermc.io/api/v2/projects/paper/versions/latest/builds/latest/downloads/paper-latest.jar";
                break;
            case "Spigot":
                serverUrl = "https://cdn.getbukkit.org/spigot/spigot-latest.jar";
                break;
            case "Vanilla":
                serverUrl = "https://launcher.mojang.com/v1/objects/latest/server.jar";
                break;
            default:
                serverUrl = "https://launcher.mojang.com/v1/objects/latest/server.jar";
                break;
        }
        String command = "mkdir -p ~/mcserver && cd ~/mcserver && " +
                         "wget -O server.jar " + serverUrl + " && " +
                         "echo 'eula=true' > eula.txt && " +
                         "java -Xmx" + ram + "M -jar server.jar nogui";
        executeTermuxCommand(command);
        tvStatus.setText("Server Starting...");
        handler.postDelayed(this::hideLoading, 15000);
    }

    private void stopServer() {
        executeTermuxCommand("pkill java");
        tvStatus.setText("Server Stopped");
        handler.postDelayed(this::hideLoading, 3000);
    }

    private void enablePortForwarding() {
        String command = "cd ~/mcserver && playit";
        executeTermuxCommand(command);
        tvStatus.setText("Port Forwarding Enabled");
        handler.postDelayed(this::hideLoading, 10000);
    }

    private void viewConsoleLogs() {
        startActivity(new Intent(this, LogActivity.class));
    }

    private void backupWorld() {
        String command = "cd ~/mcserver && tar -czf backup.tar.gz world/";
        executeTermuxCommand(command);
        Toast.makeText(this, "Backup Created", Toast.LENGTH_SHORT).show();
        handler.postDelayed(this::hideLoading, 5000);
    }

    private void executeTermuxCommand(String command) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.termux", "com.termux.app.RunCommandService");
            intent.setAction("com.termux.RUN_COMMAND");
            intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{"sh", "-c", command});
            startService(intent);
        } catch (Exception e) {
            showErrorDialog("[" + COMPANY_NAME + "] Failed to execute command: " + e.getMessage());
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

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
            .setTitle("Error - " + COMPANY_NAME)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
}
