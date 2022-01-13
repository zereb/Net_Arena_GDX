package com.zereb.netarena.net.packets;

public class ClientConnected {
    public String name;
    public int id;

    public ClientConnected(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public ClientConnected(){}
}
