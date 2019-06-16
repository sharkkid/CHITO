package com.example.chito.Util;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lungyu on 8/29/17.
 */

public class Beacon {
    private String prefix;
    private String uuid;
    private int major;
    private int minor;
    private int tx_power;
    private String macAddress;
    private int rssi;

    static {
        hexArray = "0123456789ABCDEF".toCharArray();
    }

    public Beacon(String uuid,int major,int minor,int txPower,int rssi){
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.tx_power = txPower;
        this.rssi = rssi;
    }
    public Beacon(String uuid,int major,int minor,int txPower,int rssi,String mac){
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.tx_power = txPower;
        this.rssi = rssi;
        this.macAddress = mac;
    }

    public Beacon(byte[] package_raw, BluetoothDevice device,int rssi) throws BeaconFormateNotFoundException {
        this.rssi = rssi;
        this.macAddress = device.getAddress();
        package_parser(package_raw);
    }

    private void package_parser(byte[] package_raw) throws BeaconFormateNotFoundException {
        int startByte = 2;
        boolean patternFound = false;
        // 尋找ibeacon
        // 先依序尋找第2到第8陣列的元素
        while (startByte <= 5) {
            // Identifies an iBeacon
            if (((int) package_raw[startByte + 2] & 0xff) == 0x02 &&
                    // Identifies correct data length
                    ((int) package_raw[startByte + 3] & 0xff) == 0x15) {

                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            byte[] uuidBytes = new byte[16];
            // 來源、起始位置
            System.arraycopy(package_raw, startByte + 4, uuidBytes, 0, 16);
            String hexString = Beacon.bytesToHex(uuidBytes);

            // UUID
            this.uuid = hexString.substring(0, 8) + "-"
                    + hexString.substring(8, 12) + "-"
                    + hexString.substring(12, 16) + "-"
                    + hexString.substring(16, 20) + "-"
                    + hexString.substring(20, 32);

            // Major
            this.major = (package_raw[startByte + 20] & 0xff) * 0x100
                    + (package_raw[startByte + 21] & 0xff);

            // Minor
            this.minor = (package_raw[startByte + 22] & 0xff) * 0x100
                    + (package_raw[startByte + 23] & 0xff);

            // txPower
            this.tx_power = (package_raw[startByte + 24]);
        }else {
            throw new BeaconFormateNotFoundException("patternFound");
        }
    }

    private final static char[] hexArray;

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0)
            return -1.0;

        double ratio = rssi * 1.0 / txPower;

        if (ratio < 1.0)
            return Math.pow(ratio, 10);
        else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    public double distance(){
        return calculateAccuracy(this.tx_power,this.rssi);
    }

    public String getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getMacAddress() {
        return macAddress;
    }
    public int getRssi(){
        return rssi;
    }

    public int getTxPower(){
        return tx_power;
    }

    public int getX() {
        return this.major;
    }

    public int getY() {
        return this.minor;
    }

    public static List<Beacon> getSamples(){
        List<Beacon> list = new ArrayList<>();
        Beacon b1 = new Beacon("00910-412f-4124-1245",5,2,-35,12);
        list.add(b1);
        Beacon b2 = new Beacon("00910-412f-4124-1245",1,5,-45,12);
        list.add(b2);
        Beacon b3 = new Beacon("00910-412f-4124-1245",8,6,-25,12);
        list.add(b3);
        return list;
    }

}