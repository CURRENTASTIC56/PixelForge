/*
 * PixelForge Minecraft Server Manager - File Manager Activity
 * Owner: Ishaan Dnyaneshwar Jadhav
 * Developer: Ishaan Dnyaneshwar Jadhav
 * Copyright © 2025 Ishaan Dnyaneshwar Jadhav. All rights reserved.
 */

package com.pixelforge.minecraftserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManagerActivity extends AppCompatActivity {
    private static final String COMPANY_NAME = "PixelForge";
    private TextView tvCurrentPath;
    private ListView lvFiles;
    private ProgressDialog progressDialog;
    private String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        tvCurrentPath = findViewById(R.id.tvCurrentPath);
        lvFiles = findViewById(R.id.lvFiles);

        currentPath = getIntent().getStringExtra("path");
        if (currentPath == null) {
            currentPath = "/data/data/com.termux/files/home/mcserver";
        }
        tvCurrentPath.setText("Current Path: " + currentPath);
        loadFiles(currentPath);
    }

    private void loadFiles(String path) {
        showLoading("Loading files...");
        File directory = new File(path);
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();
        List<File> fileList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
                fileList.add(file);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        lvFiles.setAdapter(adapter);
        hideLoading();

        lvFiles.setOnItemClickListener((parent, view, position, id) -> {
            File selectedFile = fileList.get(position);
            if (selectedFile.isDirectory()) {
                Intent intent = new Intent(FileManagerActivity.this, FileManagerActivity.class);
                intent.putExtra("path", selectedFile.getAbsolutePath());
                startActivity(intent);
            } else {
                Intent intent = new Intent(FileManagerActivity.this, FileViewerActivity.class);
                intent.putExtra("filepath", selectedFile.getAbsolutePath());
                startActivity(intent);
            }
        });
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
