package com.zereb.netarena;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.zereb.netarena.utils.Utils;

public class CollisionFiltering {

    private final Map map;
    private final Array<Player> entities = new Array<>();

    public CollisionFiltering(Map map) {
        this.map = map;
    }

    public void addEntity(Player entity) {
        entities.add(entity);
    }

    public void removeEntity(Player entity) {
        entities.removeValue(entity, true);
    }

    public void filter() {
        for (Player entity : entities) {
            Shape2D shape = map.wall;
            if (!shape.contains(entity.position)) {
                if (shape instanceof Polygon) {
                    Polygon wall = (Polygon) shape;

                    Vector2 first = new Vector2();
                    Vector2 last = new Vector2();
                    float[] transformedVertices = wall.getTransformedVertices();

                    float shortest = Float.MAX_VALUE;
                    float shortest2 = Float.MAX_VALUE;

                    int skipThisI = 0;
                    for (int i = 0; i < transformedVertices.length - 1; i += 2) {
                        float dist = entity.position.dst2(transformedVertices[i], transformedVertices[i + 1]);
                        if (dist < shortest) {
                            shortest = dist;
                            first.set(transformedVertices[i], transformedVertices[i + 1]);
                            skipThisI = i;
                        }
                    }

                    for (int i = 0; i < transformedVertices.length - 1; i += 2) {
                        float dist = entity.position.dst2(transformedVertices[i], transformedVertices[i + 1]);
                        if (skipThisI == i) continue;
                        if (dist < shortest2) {
                            shortest2 = dist;
                            last.set(transformedVertices[i], transformedVertices[i + 1]);
                        }
                    }

                    entity.position.set(Utils.getProjectedPointOnLine(entity.position, first, last));
                } else {
                    Ellipse wall = (Ellipse) shape;
                }
            }


            //collisions
            Vector2 d = new Vector2();
            Vector2 centerOfMapObject = new Vector2();

            for (int i = 0; i < entities.size; i++) {
                if (entity.equals(entities.get(i))) continue;

                Rectangle colision = entities.get(i).box;

                if (!entity.box.overlaps(colision)) continue;


                colision.getCenter(centerOfMapObject);

                //vector between centres of objects
                d.set(entity.position.x - centerOfMapObject.x, entity.position.y - centerOfMapObject.y);


                //player on right side
                float eW = entity.box.width / 2;
                float eH = entity.box.height / 2;
                float cW = colision.width / 2;
                float cH = colision.height / 2;

                if (Math.abs(d.x) > Math.abs(d.y)) {
                    if (d.x > 0) //player on the right
                        entity.position.set(entity.position.x + (eW + cW - d.x), entity.position.y);
                    if (d.x < 0)   //player on the left
                        entity.position.set(entity.position.x - (eW + cW + d.x), entity.position.y);
                } else {
                    if (d.y > 0)    //player above
                        entity.position.set(entity.position.x, entity.position.y + (eH + cH - d.y));
                    if (d.y < 0)    //player under
                        entity.position.set(entity.position.x, entity.position.y - (eH + cH + d.y));
                }

            }


            for (Rectangle colision : map.collisions) {
                //to avoid nested if's skip current iteration if there is no collision
                if (!entity.box.overlaps(colision))
                    continue;

                colision.getCenter(centerOfMapObject);

                //vector between centres of objects
                d.set(entity.position.x - centerOfMapObject.x, entity.position.y - centerOfMapObject.y);


                //player on right side
                float eW = entity.box.width / 2;
                float eH = entity.box.height / 2;
                float cW = colision.width / 2;
                float cH = colision.height / 2;

                if (Math.abs(d.x) > Math.abs(d.y)) {
                    if (d.x > 0) //player on the right
                        entity.position.set(entity.position.x + (eW + cW - d.x), entity.position.y);
                    if (d.x < 0)   //player on the left
                        entity.position.set(entity.position.x - (eW + cW + d.x), entity.position.y);
                } else {
                    if (d.y > 0)    //player above
                        entity.position.set(entity.position.x, entity.position.y + (eH + cH - d.y));
                    if (d.y < 0)    //player under
                        entity.position.set(entity.position.x, entity.position.y - (eH + cH + d.y));
                }
            }
        }
    }

}
