/*
 * PixelForge Minecraft Server Manager - Plugin Management Activity
 * Owner: Ishaan Dnyaneshwar Jadhav
 * Developer: Ishaan Dnyaneshwar Jadhav
 * Copyright © 2025 Ishaan Dnyaneshwar Jadhav. All rights reserved.
 */

package com.pixelforge.minecraftserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginManagementActivity extends AppCompatActivity {
    private static final String COMPANY_NAME = "PixelForge";
    private ListView lvPlugins;
    private ProgressDialog progressDialog;
    private String pluginsPath = "/data/data/com.termux/files/home/mcserver/plugins";
    private List<File> pluginFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_management);
        lvPlugins = findViewById(R.id.lvPlugins);
        Button btnAddPlugin = findViewById(R.id.btnAddPlugin);
        loadPlugins();
        btnAddPlugin.setOnClickListener(v -> promptAddPlugin());
        lvPlugins.setOnItemLongClickListener((parent, view, position, id) -> {
            File plugin = pluginFiles.get(position);
            promptDeletePlugin(plugin);
            return true;
        });
    }

    private void loadPlugins() {
        showLoading("Loading plugins...");
        File dir = new File(pluginsPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        pluginFiles = new ArrayList<>();
        List<String> pluginNames = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                pluginFiles.add(f);
                pluginNames.add(f.getName());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pluginNames);
        lvPlugins.setAdapter(adapter);
        hideLoading();
    }

    private void promptAddPlugin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Plugin URL");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                downloadPlugin(url);
            } else {
                Toast.makeText(this, "URL cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void downloadPlugin(String url) {
        String command = "mkdir -p " + pluginsPath + " && cd " + pluginsPath + " && wget " + url;
        executeTermuxCommand(command);
        Toast.makeText(this, "Downloading plugin...", Toast.LENGTH_SHORT).show();
        lvPlugins.postDelayed(this::loadPlugins, 10000);
    }

    private void promptDeletePlugin(File plugin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Plugin")
            .setMessage("Delete " + plugin.getName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                if (plugin.delete()) {
                    Toast.makeText(this, "Plugin deleted", Toast.LENGTH_SHORT).show();
                    loadPlugins();
                } else {
                    Toast.makeText(this, "Failed to delete plugin", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void executeTermuxCommand(String command) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.termux", "com.termux.app.RunCommandService");
            intent.setAction("com.termux.RUN_COMMAND");
            intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{"sh", "-c", command});
            startService(intent);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                .setTitle("Error - " + COMPANY_NAME)
                .setMessage("Failed to execute command: " + e.getMessage())
                .setPositiveButton("OK", null)
                .show();
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
}
