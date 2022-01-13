package com.zereb.netarena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.zereb.netarena.utils.HeadlessTmxMapLoader;


public class Map {
    public final int width, height;
    public final String name;
    public final Array<Rectangle> collisions = new Array<>();
    public final Array<Rectangle> enemySpawns = new Array<>();
    public Polygon wall;
    public IntArray bgLayers = new IntArray();
    public IntArray fgLayers = new IntArray();

    public final TiledMap map;
    public MapRender render;

    public Map(String name, boolean isHeadless) {
        this.name = name;
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        parameters.convertObjectToTileSpace = false;
        parameters.flipY = true;

        if (isHeadless) {
            HeadlessTmxMapLoader loader = new HeadlessTmxMapLoader();
            map = loader.load(name + ".tmx", parameters);
            System.out.println(map.getProperties().getValues());
            System.out.println(map.getProperties().getKeys());

        } else {
            TmxMapLoader loader = new TmxMapLoader();
            map = loader.load(name + ".tmx", parameters);
        }

        int tiledHeight = (int) map.getProperties().get("tileheight");
        int tileWidth = (int) map.getProperties().get("tilewidth");
        height = (int) map.getProperties().get("height") * tiledHeight;
        width = (int) map.getProperties().get("width") * tileWidth;

        for (MapObject object : map.getLayers().get("col").getObjects()) {
            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            collisions.add(rectangleMapObject.getRectangle());
        }

        for (MapObject object : map.getLayers().get("wall").getObjects()) {
            if (object instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) object;
                wall = polygonMapObject.getPolygon();
            }
        }

        for (MapObject object : map.getLayers().get("obj").getObjects()) {
            String objectName = object.getName();
            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            Rectangle rectangle = rectangleMapObject.getRectangle();

            if (objectName.equals("enemy"))
                enemySpawns.add(rectangle);
        }


        Gdx.app.log("Map", "loaded map: " + this);
        int index = 0;
        for (MapLayer mapLayer : map.getLayers()) {
            if (mapLayer.getName().startsWith("bg_")) bgLayers.add(index);
            if (mapLayer.getName().startsWith("fg_")) fgLayers.add(index);
            index++;
        }

    }

    public void dispose() {
        map.dispose();
    }

    public Vector2 getRandomSpawn() {
        Vector2 vector2 = new Vector2();
        enemySpawns.get(MathUtils.random(enemySpawns.size - 1)).getCenter(vector2);
        vector2.x += MathUtils.random(-20, 20);
        vector2.y += MathUtils.random(-20, 20);
        return vector2;
    }

    @Override
    public String toString() {
        return "Map{" +
                "width=" + width +
                ", height=" + height +
                ", name='" + name + '\'' +
                ", collisions=" + collisions +
                ", enemySpawns=" + enemySpawns +
                ", wall=" + wall +
                ", bgLayers=" + bgLayers +
                ", fgLayers=" + fgLayers +
                ", map=" + map +
                ", render=" + render +
                '}';
    }
}
