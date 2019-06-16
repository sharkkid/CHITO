package com.example.chito.Util;

public class BeaconFormateNotFoundException extends Exception {
    public BeaconFormateNotFoundException(String message){
        super("parser error:" + message);
    }
}
