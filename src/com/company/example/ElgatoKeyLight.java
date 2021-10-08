package com.company.example;

public class ElgatoKeyLight {
    public ElgatoKeyLight(String name, String serverAddress) {
        _name = name;
        _serverAddress = serverAddress;
    }

    private String _name;

    public String getName() {
        return _name;
    }

    private String _serverAddress;

    public String getServerAddress() {
        return _serverAddress;
    }
}
