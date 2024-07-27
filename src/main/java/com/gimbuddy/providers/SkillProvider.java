package com.gimbuddy.providers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.runelite.api.Client;
import com.gimbuddy.models.SkillsModel;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class SkillProvider {
    private final HttpClient httpClient;
    private final Gson gson;
    private final GroupProvider groupProvider;

    @Inject
    public SkillProvider(Gson gson, GroupProvider groupProvider) {
        this.gson = gson;
        this.groupProvider = groupProvider;
        this.httpClient = HttpClient.newHttpClient();
    }

    public Map<String, List<Map<String, Object>>> getPinnedSkills(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            throw new IllegalArgumentException("groupName cannot be null or empty");
        }

        try {
            String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8).replace("+", "%20");
            String endpoint = groupProvider.getServerAddress() + "/getPinnedSkills?groupName=" + encodedGroupName;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject responseData = gson.fromJson(response.body(), JsonObject.class);
                JsonObject memberSkillsJson = responseData.getAsJsonObject("memberSkills");

                Map<String, List<Map<String, Object>>> memberSkills = new HashMap<>();

                for (Map.Entry<String, JsonElement> entry : memberSkillsJson.entrySet()) {
                    String memberName = entry.getKey();
                    JsonArray skillsDataJson = entry.getValue().getAsJsonArray();
                    if (skillsDataJson.size() == 0) {
                        continue;
                    }

                    List<Map<String, Object>> skillsData = gson.fromJson(skillsDataJson, new TypeToken<List<Map<String, Object>>>() {}.getType());

                    List<Map<String, Object>> validSkills = new ArrayList<>();

                    for (Map<String, Object> skillData : skillsData) {
                        String name = (String) skillData.get("name");

                        if (!SkillsModel.getSkillIds().containsKey(name)) {
                            continue;
                        }

                        validSkills.add(skillData);
                    }

                    memberSkills.put(memberName, validSkills);
                }

                return memberSkills;
            } else {
                return new HashMap<>();
            }
        } catch (IOException | InterruptedException e) {
            return new HashMap<>();
        }
    }

    public boolean pinSkill(String userName, int skillsId, int goal) {
        if (userName == null || userName.isEmpty() || skillsId < 0 || goal <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        try {
;
            String endpoint = groupProvider.getServerAddress() + "/pinSkill";

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("groupName", groupProvider.getCurrentGroupName());
            requestBody.addProperty("userName", userName);
            requestBody.addProperty("skillsId", skillsId);
            requestBody.addProperty("goal", goal);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public boolean unpinSkill(String userName, int skillsId) {
        if (userName == null || userName.isEmpty() || skillsId < 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        try {
            String endpoint = groupProvider.getServerAddress() + "/unpinSkill";

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("groupName", groupProvider.getCurrentGroupName());
            requestBody.addProperty("userName", userName);
            requestBody.addProperty("skillsId", skillsId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
