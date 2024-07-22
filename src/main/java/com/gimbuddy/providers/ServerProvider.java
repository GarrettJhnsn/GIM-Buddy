package com.gimbuddy.providers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerProvider {
    private String serverAddress;

    public ServerProvider() {
        this.serverAddress = "http://localhost:5000";
    }

    public ServerProvider(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
