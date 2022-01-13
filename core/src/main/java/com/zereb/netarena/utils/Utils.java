package com.zereb.netarena.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Utils {

    public static Vector2 getProjectedPointOnLine(Vector2 point, Vector2 first, Vector2 last){
        // get dot product of e1, e2
        Vector2 e1 = new Vector2(last.x - first.x, last.y - first.y);
        Vector2 e2 = new Vector2(point.x - first.x, point.y - first.y);
        float valDp = e1.dot(e2);
        // get squared length of e1
        float len2 = e1.x * e1.x + e1.y * e1.y;
        Vector2 p = new Vector2((int) (first.x + (valDp * e1.x) / len2),
                (int) (first.y + (valDp * e1.y) / len2));

        return p;
    }


    public static Array<TextureAtlas.TextureAtlasData.Region> findRegions (String name, Array<TextureAtlas.TextureAtlasData.Region> regions) {
        Array<TextureAtlas.TextureAtlasData.Region> matched = new Array<>();
        for (int i = 0, n = regions.size; i < n; i++) {

            TextureAtlas.TextureAtlasData.Region region = regions.get(i);
            if (region.name.equals(name)) matched.add(region);
        }
        return matched;
    }
}
