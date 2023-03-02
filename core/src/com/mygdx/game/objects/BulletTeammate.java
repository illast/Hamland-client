package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.game.GameClient;

import java.util.Map;

public class BulletTeammate extends GameObject {

    public boolean isShot;
    private float bulletRotation;

    private float time = 0;

    // Explosion
    private boolean isHit;
    private float timeHit = 0;
    private int explosionTextureIndex = 0;
    private float x = 0, y = 0;

    // Textures
    private final TextureRegion[] textureRegions;

    public BulletTeammate(Texture texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);

        // Textures
        TextureAtlas explosionAtlas = new TextureAtlas("animations/explosion.atlas");
        textureRegions = new TextureRegion[40];
        for (int i = 0; i < 40; i++) {
            textureRegions[i] = explosionAtlas.findRegion("exp" + (i + 1));
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isShot) {
            super.draw(batch);
        }
    }

    public void renderShot(Polygon playerPolygon, Map<Integer, Enemy> enemyList, float delta, SpriteBatch batch) {

        if (!isShot) {
            polygon.setPosition(playerPolygon.getX() + polygon.getOriginX(), playerPolygon.getY() + polygon.getOriginX());
            bulletRotation = playerPolygon.getRotation() + 90;
            isShot = true;
            GameClient.soundBulletShot.play();
        }

        time += delta;
        if (time > 0.49) {
            time = 0;
            isShot = false;
        }

        if (isShot) {
            polygon.setPosition(polygon.getX() + 1000 * delta * MathUtils.cosDeg(bulletRotation),
                    polygon.getY() + 1000 * delta * MathUtils.sinDeg(bulletRotation));
            polygon.setRotation(bulletRotation);

            // Check bullet hit enemy.
            for (Enemy enemy : enemyList.values()) {
                if (polygon.getX() > enemy.polygon.getX() && polygon.getX() < enemy.polygon.getX() + enemy.getWidth()
                        && polygon.getY() > enemy.polygon.getY() && polygon.getY() < enemy.polygon.getY() + enemy.getHeight()
                        && !enemy.isInvulnerable()) {
                    GameClient.soundExplosion.play();
                    explosionTextureIndex = 0;
                    x = polygon.getX();
                    y = polygon.getY();
                    isHit = true;
                    polygon.setPosition(9999999, 9999999);
                }
            }
        }

        // Draws explosion animation
        if (isHit) {
            if (explosionTextureIndex == 40) {
                isHit = false;
                explosionTextureIndex = 0;
            }
            timeHit += delta;
            batch.draw(textureRegions[explosionTextureIndex], x - 64, y - 64);
            if (timeHit > 0.01) {
                explosionTextureIndex++;
                timeHit = 0;
            }
        }
    }
}
