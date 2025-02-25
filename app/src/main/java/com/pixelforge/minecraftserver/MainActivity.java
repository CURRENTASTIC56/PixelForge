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
import android.os.AsyncTask;
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
            showLoading("Starting server...");
            new ServerTask().execute("start");
            startService(new Intent(this, PlayerNotificationService.class));
        });

        findViewById(R.id.btnStopServer).setOnClickListener(v -> {
            showLoading("Stopping server...");
            new ServerTask().execute("stop");
        });

        findViewById(R.id.btnPortForward).setOnClickListener(v -> {
            showLoading("Enabling port forwarding...");
            new ServerTask().execute("port");
        });

        findViewById(R.id.btnViewLogs).setOnClickListener(v -> viewConsoleLogs());

        findViewById(R.id.btnBackup).setOnClickListener(v -> {
            showLoading("Creating backup...");
            new ServerTask().execute("backup");
        });

        findViewById(R.id.btnManageFiles).setOnClickListener(v -> {
            startActivity(new Intent(this, FileManagerActivity.class));
        });

        findViewById(R.id.btnManagePlayers).setOnClickListener(v -> {
            startActivity(new Intent(this, PlayerManagementActivity.class));
        });

        findViewById(R.id.btnConfigureServer).setOnClickListener(v -> {
            startActivity(new Intent(this, ConfigurationActivity.class));
        });
    }

    private class ServerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String action = params[0];
            try {
                switch (action) {
                    case "start":
                        return startServer();
                    case "stop":
                        return stopServer();
                    case "port":
                        return enablePortForwarding();
                    case "backup":
                        return backupWorld();
                    default:
                        return "Unknown command";
                }
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            tvStatus.setText(result);
        }
    }

    private String startServer() {
        String ram = etRam.getText().toString().trim();
        if (ram.isEmpty()) ram = "1024";
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
                return "Invalid server type";
        }

        String command = "mkdir -p ~/mcserver && cd ~/mcserver && " +
                "wget -O server.jar " + serverUrl + " && " +
                "echo 'eula=true' > eula.txt && " +
                "java -Xmx" + ram + "M -jar server.jar nogui";
        return executeTermuxCommand(command) ? "Server Starting..." : "Failed to start server";
    }

    private String stopServer() {
        return executeTermuxCommand("pkill java") ? "Server Stopped" : "Failed to stop server";
    }

    private String enablePortForwarding() {
        return executeTermuxCommand("cd ~/mcserver && playit") ? "Port Forwarding Enabled" : "Failed to enable port forwarding";
    }

    private String backupWorld() {
        return executeTermuxCommand("cd ~/mcserver && tar -czf backup.tar.gz world/") ? "Backup Created" : "Failed to create backup";
    }

    private boolean executeTermuxCommand(String command) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.termux", "com.termux.app.RunCommandService");
            intent.setAction("com.termux.RUN_COMMAND");
            intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{"sh", "-c", command});
            startService(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void viewConsoleLogs() {
        startActivity(new Intent(this, LogActivity.class));
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
}
