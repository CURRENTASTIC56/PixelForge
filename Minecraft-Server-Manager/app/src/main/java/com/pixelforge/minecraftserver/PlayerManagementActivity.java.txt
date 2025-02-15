/*
 * PixelForge Minecraft Server Manager - Player Management Activity
 * Owner: Ishaan Dnyaneshwar Jadhav
 * Developer: Ishaan Dnyaneshwar Jadhav
 * Copyright © 2025 Ishaan Dnyaneshwar Jadhav. All rights reserved.
 */

package com.pixelforge.minecraftserver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PlayerManagementActivity extends AppCompatActivity {
    private static final String COMPANY_NAME = "PixelForge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_management);
        Button btnKick = findViewById(R.id.btnKickPlayer);
        Button btnBan = findViewById(R.id.btnBanPlayer);
        Button btnWhitelist = findViewById(R.id.btnWhitelistPlayer);
        btnKick.setOnClickListener(v -> promptForPlayer("kick"));
        btnBan.setOnClickListener(v -> promptForPlayer("ban"));
        btnWhitelist.setOnClickListener(v -> promptForPlayer("whitelist"));
    }

    private void promptForPlayer(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter player name to " + action);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String playerName = input.getText().toString().trim();
            if (!playerName.isEmpty()) {
                executePlayerCommand(action, playerName);
            } else {
                Toast.makeText(this, "Player name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void executePlayerCommand(String action, String playerName) {
        String command;
        if (action.equals("kick")) {
            command = "screen -S mcserver -X stuff 'kick " + playerName + " \n'";
        } else if (action.equals("ban")) {
            command = "screen -S mcserver -X stuff 'ban " + playerName + " \n'";
        } else if (action.equals("whitelist")) {
            command = "screen -S mcserver -X stuff 'whitelist add " + playerName + " \n'";
        } else {
            command = "";
        }
        executeTermuxCommand(command);
        Toast.makeText(this, "Executed " + action + " for " + playerName, Toast.LENGTH_SHORT).show();
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
}
