package com.company.example;

public class Main {

    public static void main(String[] args) {
	    System.out.println("Looking for lights...");
        for ( var light : ElgatoControlCenterParser.getKeyLights() ) {
            System.out.println(String.format("Got %s at %s", light.getName(), light.getServerAddress() ));
        }
        System.out.println("Done");
    }
}

