package com.gimbuddy.providers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;


import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class BankProvider {
    private final Client client;
    private final ItemManager itemManager;
    private final HttpClient httpClient;
    private final ServerProvider serverProvider;

    @Setter
    private String groupName;

    @Inject
    private Gson gson;

    @Inject
    public BankProvider(Client client, ItemManager itemManager, ServerProvider serverProvider, Gson gson) {
        this.client = client;
        this.itemManager = itemManager;
        this.serverProvider = serverProvider;
        this.gson = gson;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        int containerId = event.getContainerId();

        if (containerId == InventoryID.BANK.getId() || containerId == InventoryID.GROUP_STORAGE.getId()) {
            updateBankItems(containerId, serverProvider.getServerAddress());
        }
    }

    public void updateBankItems(int containerId, String serverAddress) {
        if (groupName == null || groupName.isEmpty()) {
            System.out.println("Group name is null or empty.");
            return;
        }

        if (serverAddress == null || serverAddress.isEmpty()) {
            throw new IllegalArgumentException("Server address is not provided or is empty.");
        }

        ItemContainer itemContainer = client.getItemContainer(containerId);
        if (itemContainer != null) {
            Map<String, Map<String, Object>> itemsForStorage = new HashMap<>();
            for (Item item : itemContainer.getItems()) {
                if (item != null) {
                    String itemName = itemManager.getItemComposition(item.getId()).getName();
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("id", item.getId());
                    itemData.put("quantity", item.getQuantity());
                    itemsForStorage.put(itemName, itemData);
                }
            }

            try {
                String userName = client.getLocalPlayer().getName();
                String storageType = (containerId == InventoryID.GROUP_STORAGE.getId()) ? "groupStorage" : userName;
                assert storageType != null;

                String json = gson.toJson(Map.of("group", groupName, "user", storageType, "items", itemsForStorage));
                String endpoint = serverAddress + "/updateBankItems";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new IOException("Unexpected response code: " + response.statusCode());
                }

                System.out.println("Bank items updated successfully for " + storageType);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Item container is null for containerId: " + containerId);
        }
    }

    public Map<String, Item> getBankItems(String selectedBank, String serverAddress) {
        try {
            Map<String, String> requestBody = Map.of(
                    "groupName", groupName,
                    "selectedBank", selectedBank
            );

            String json = gson.toJson(requestBody);
            String endpoint = serverAddress + "/getBankItems";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("Unexpected response code: " + response.statusCode());
            }

            String jsonData = response.body();
            Map<String, Item> items = gson.fromJson(jsonData, new TypeToken<Map<String, Item>>() {}.getType());

            return new TreeMap<>(items);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}

