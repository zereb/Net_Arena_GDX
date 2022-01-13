package com.zereb.netarena;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapRender implements Debuggable {
    private final OrthogonalTiledMapRenderer renderer;
    private final Map map;

    public MapRender(Map map, SpriteBatch batch) {
        this.map = map;
        renderer = new OrthogonalTiledMapRenderer(map.map, batch);
        renderer.setMap(map.map);
    }

    public void renderBg(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render(map.bgLayers.toArray());
    }

    public void renderFg() {
        renderer.render(map.fgLayers.toArray());
    }


    public void debug(ShapeRenderer sr) {
        if (!Main.isDebug) return;

        sr.setColor(Color.GRAY);

        if (map.wall != null) sr.polygon(map.wall.getTransformedVertices());
        map.collisions.forEach(rectangle ->
                sr.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
        );
        sr.setColor(Color.RED);
        map.enemySpawns.forEach(rectangle ->
                sr.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
        );
        sr.setColor(Color.BLUE);


    }

}
