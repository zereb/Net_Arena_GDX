package com.zereb.netarena.utils;

import com.zereb.netarena.net.packets.GameInstanceSnapshot;

public class Packet {
    public final long time;
    public final GameInstanceSnapshot snapshot;

    public Packet(GameInstanceSnapshot snapshot) {
        this.time = System.nanoTime();
        this.snapshot = snapshot;
    }

}
