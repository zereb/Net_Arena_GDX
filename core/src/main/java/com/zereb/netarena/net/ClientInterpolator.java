package com.zereb.netarena.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.zereb.netarena.Player;
import com.zereb.netarena.net.packets.GameInstanceSnapshot;
import com.zereb.netarena.net.packets.PlayerSnapshot;
import com.zereb.netarena.utils.Packet;

import java.util.HashMap;
import java.util.Vector;

public class ClientInterpolator {

    public static float INTERPOLATION_DELAY = GameServer.TARGET_DELTA;
    private Packet pastPack;
    private Packet futurePack;
    private GameInstanceSnapshot currentGIS;

    public GameInstanceSnapshot getCurrentSnapshot(Queue<Packet> packets) {
        if (!packets.isEmpty()) {
            if (futurePack == null)
                futurePack = packets.removeFirst();

            if (futurePack == null) return currentGIS;

            long elapcedTime = TimeUtils.timeSinceNanos(futurePack.time);
            if (elapcedTime / 1000000000f > INTERPOLATION_DELAY) {
                pastPack = futurePack;
                futurePack = packets.removeFirst();
                currentGIS = new GameInstanceSnapshot(futurePack.snapshot);
                for (PlayerSnapshot playerSnapshot : currentGIS.players.values()) {
                    PlayerSnapshot past = pastPack.snapshot.players.get(playerSnapshot.id);
                    if (past == null) continue;
                    playerSnapshot.position.set(past.position);
                }

                if (currentGIS != null)
                    Gdx.app.log("pac", currentGIS.players.toString());

            }
        }

        return interpolate();

    }

    private GameInstanceSnapshot interpolate() {
        if (pastPack == null || futurePack == null) return null;

        for (PlayerSnapshot past : pastPack.snapshot.players.values()) {
            PlayerSnapshot future = futurePack.snapshot.players.get(past.id);

            if (future == null) continue;

            PlayerSnapshot current = currentGIS.players.get(past.id);
            float alpha = (INTERPOLATION_DELAY / Gdx.graphics.getDeltaTime());
            Vector2 lerp = new Vector2();
            lerp.x = -(past.position.x - future.position.x) / alpha;
            lerp.y = -(past.position.y - future.position.y) / alpha;

            if (current.position.dst(future.position) < lerp.len()){
                lerp.x = future.position.x - current.position.x;
                lerp.y = future.position.y - current.position.y;
            }
            current.position.add(lerp);
        }

        return currentGIS;
    }
}
