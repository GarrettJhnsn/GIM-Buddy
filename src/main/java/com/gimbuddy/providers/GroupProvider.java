package com.gimbuddy.providers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.runelite.api.Client;
import net.runelite.api.Friend;
import net.runelite.api.GameState;
import net.runelite.api.FriendContainer;

import javax.inject.Inject;
import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupProvider {
    private final Client client;
    private final HttpClient httpClient;

    @Inject
    private final Gson gson;

    @Inject
    public GroupProvider(Client client, Gson gson) {
        this.client = client;
        this.gson = gson;
        this.httpClient = HttpClient.newHttpClient();
    }

    private boolean isLoggedIn() {
        if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Please log in to RuneLite first", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<String> fetchGroupMembers(String groupName, String serverAddress) {
        if (isLoggedIn()) {
            String currentUsername = client.getLocalPlayer().getName();

            try {
                String endpoint = serverAddress + "/fetchGroupIronmanMembers/" + groupName;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    List<String> groupMembers = gson.fromJson(response.body(), new TypeToken<List<String>>() {}.getType());

                    if (groupMembers.contains(currentUsername)) {
                        return groupMembers;
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid group name or user not in group", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public String fetchGroupRank(String groupName, String serverAddress) {
        if (isLoggedIn()) {
            try {
                String endpoint = serverAddress + "/fetchGroupRank/" + groupName;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String rawResponse = response.body();
                    if (rawResponse.startsWith("\"") && rawResponse.endsWith("\"")) {
                        return rawResponse.substring(1, rawResponse.length() - 1);
                    } else {
                        return rawResponse.replace(",", "").trim();
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return "Rank not found";
    }

    public Map<String, Integer> getOnlineFriends() {
        Map<String, Integer> onlineFriends = new HashMap<>();
        FriendContainer friendContainer = client.getFriendContainer();
        if (friendContainer != null) {
            for (Friend friend : friendContainer.getMembers()) {
                int world = friend.getWorld();
                if (world > 0) {
                    onlineFriends.put(friend.getName(), world);
                }
            }
        }
        return onlineFriends;
    }

    public String updateMemberList(List<String> groupMembers) {
        Map<String, Integer> onlineFriends = getOnlineFriends();

        StringBuilder memberListBuilder = new StringBuilder();
        for (String member : groupMembers) {
            String displayName = member;
            if (onlineFriends.containsKey(member)) {
                displayName = member + " (World " + onlineFriends.get(member) + ")";
            }
            memberListBuilder.append(displayName).append("\n");
        }
        if (memberListBuilder.length() > 0) {
            memberListBuilder.setLength(memberListBuilder.length() - 1);
        }
        return memberListBuilder.toString();
    }

    public boolean isServerOnline(String serverAddress) {
        try {
            String endpoint = serverAddress + "/status";

            URI serverUri = URI.create(endpoint);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(serverUri)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
