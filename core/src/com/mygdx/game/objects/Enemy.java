package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.GameClient;
import com.mygdx.game.screens.PlayScreen;

public class Enemy extends GameObject {

    public static final int HP_WIDTH = 50;
    public static final int HP_HEIGHT = 5;
    public static final float DISAPPEAR_TEXTURE_WIDTH = 100f;
    public static final float DISAPPEAR_TEXTURE_HEIGHT = 100f;
    public static final float DEATH_TEXTURE_WIDTH = 200f;
    public static final float DEATH_TEXTURE_HEIGHT = 200f;
    public static final float FIRE_TEXTURE_WIDTH = 200f;
    public static final float FIRE_TEXTURE_HEIGHT = 200f;

    // HP
    private final int maxHp;
    private int hp;
    private final Sprite hpBarSprite;
    private final Sprite hpSprite;
    private final Sprite hpEmptySprite;

    // Disappear animation
    private boolean isDisappeared;
    private boolean startDisappear;
    private float timerDisappear;
    private int indexDisappear;

    // Appear animation
    private boolean isAppeared;
    private boolean startAppear;
    private float timerAppear;
    private int indexAppear;

    // Death animation
    private boolean isDeath;
    private float timerDeath;
    private int indexDeath;
    private float deathX = -99999;
    private float deathY = -99999;

    // Fire animation
    private boolean isFire;
    private float timerFire;
    private int indexFire;

    private boolean isInvulnerable;

    // Animation textures
    private final TextureRegion[] textureRegionsDisappear;
    private final TextureRegion[] textureRegionsDeath;
    private final TextureRegion[] textureRegionsFire;

    public Enemy(Texture texture, float x, float y, float width, float height, int hp, int maxHp) {
        super(texture, x, y, width, height);
        this.maxHp = maxHp;
        this.hp = hp;

        // Disappear animation
        TextureAtlas disappearAtlas = new TextureAtlas("animations/portal.atlas");
        textureRegionsDisappear = new TextureRegion[16];
        for (int i = 0; i < 16; i++) {
            TextureRegion textureRegion = disappearAtlas.findRegion(String.valueOf(i + 1));
            textureRegionsDisappear[i] = textureRegion;
        }

        // Death animation
        TextureAtlas deathAtlas = new TextureAtlas("animations/smoke.atlas");
        textureRegionsDeath = new TextureRegion[40];
        for (int i = 0; i < 40; i++) {
            TextureRegion textureRegion = deathAtlas.findRegion(String.valueOf(i + 1));
            textureRegionsDeath[i] = textureRegion;
        }

        // Fire animation
        TextureAtlas fireAtlas = new TextureAtlas("animations/fire.atlas");
        textureRegionsFire = new TextureRegion[16];
        for (int i = 0; i < 16; i++) {
            TextureRegion textureRegion = fireAtlas.findRegion(String.valueOf(i + 1));
            textureRegionsFire[i] = textureRegion;
        }

        // HP
        hpBarSprite = new Sprite(new Texture("background/hp_bar.png"));
        hpSprite = new Sprite(new Texture("background/hp.png"));
        hpEmptySprite = new Sprite(new Texture("background/hp_empty.png"));
    }

    public void draw(SpriteBatch batch, TiledMapTileLayer layer, float delta) {
        sprite.setPosition(polygon.getX(), polygon.getY()); // set Sprite position equal to Polygon position
        sprite.setRotation(polygon.getRotation()); // set Sprite rotation around the Polygon center

        // If the enemy is alive, he is drawn on the map and chases the player.
        if (isAlive()) {
            sprite.draw(batch);
        }

        fireAnimation(delta, batch);
        drawHP(batch);
        onBuilding(layer);

        disappearAnimation(delta, batch);
        appearAnimation(delta, batch);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Draw a health bar on top of the enemy that changes depending on the remaining health.
     *
     * @param batch batch.
     */
    public void drawHP(SpriteBatch batch) {

        // Empty hp
        hpEmptySprite.setSize(HP_WIDTH, HP_HEIGHT);
        hpEmptySprite.setPosition(polygon.getX() + width / 2f - HP_WIDTH / 2f, polygon.getY() + height + 10);
        hpEmptySprite.draw(batch);

        // Hp
        hpSprite.setSize(HP_WIDTH * (1f / maxHp) * hp, HP_HEIGHT);
        hpSprite.setPosition(polygon.getX() + width / 2f - HP_WIDTH / 2f, polygon.getY() + height + 10);
        hpSprite.draw(batch);

        // Hp bar
        hpBarSprite.setSize(HP_WIDTH, HP_HEIGHT);
        hpBarSprite.setPosition(polygon.getX() + width / 2f - HP_WIDTH / 2f, polygon.getY() + height + 10);
        hpBarSprite.draw(batch);
    }

    /**
     * If the mob is on the building - the mob is invisible and invulnerable.
     * If the mob is outside the building - the mob is visible and vulnerable
     *
     * @param layer buildings layer.
     */
    public void onBuilding(TiledMapTileLayer layer) {
        float x = polygon.getX();
        float y = polygon.getY();
        float tileWidth = PlayScreen.tileWidth;
        float tileHeight = PlayScreen.tileHeight;

        if ( // on the building

            // 1. lower left corner of the enemy
                layer.getCell((int) (x / tileWidth), (int) ((y) / tileHeight)) != null ||

                        // 2. lower right corner of the enemy
                        layer.getCell((int) ((x + width) / tileWidth), (int) ((y) / tileHeight)) != null ||

                        // 3. upper left corner of the enemy
                        layer.getCell((int) (x / tileWidth), (int) ((y + height) / tileHeight)) != null ||

                        // 4. upper right corner of the enemy
                        layer.getCell((int) ((x + width) / tileWidth), (int) (((y) + height) / tileHeight)) != null ||

                        // 5. left center point of the enemy
                        layer.getCell((int) (x / tileWidth), (int) ((y + height / 2) / tileHeight)) != null ||

                        // 6. right center point of the enemy
                        layer.getCell((int) ((x + width) / tileWidth), (int) ((y + height / 2) / tileHeight)) != null ||

                        // 7. lower center point of the enemy
                        layer.getCell((int) ((x + width / 2) / tileWidth), (int) (y / tileHeight)) != null ||

                        // 8. upper center point of the enemy
                        layer.getCell((int) ((x + width / 2) / tileWidth), (int) ((y + height) / tileHeight)) != null) {

            sprite.setColor(1f, 1f, 1f, 0.2f);
            hpBarSprite.setColor(1f, 1f, 1f, 0.0f);
            hpSprite.setColor(1f, 1f, 1f, 0.0f);
            hpEmptySprite.setColor(1f, 1f, 1f, 0.0f);
            isInvulnerable = true;

            // Disappear animation
            if (startDisappear) {
                isDisappeared = true;
            }
            startDisappear = false;

            // Appear animation
            startAppear = true;
        } else { // outside the building

            sprite.setColor(1f, 1f, 1f, 1f);
            hpBarSprite.setColor(1f, 1f, 1f, 1f);
            hpSprite.setColor(1f, 1f, 1f, 1f);
            hpEmptySprite.setColor(1f, 1f, 1f, 1f);
            isInvulnerable = false;

            // Disappear animation
            startDisappear = true;

            // Appear animation
            if (startAppear) {
                isAppeared = true;
            }
            startAppear = false;
        }
    }

    /**
     * Draw disappear animation.
     * @param delta delta.
     * @param batch batch.
     */
    public void disappearAnimation(float delta, SpriteBatch batch) {
        if (indexDisappear == 16) {
            isDisappeared = false;
            indexDisappear = 0;
        }
        if (isDisappeared) {
            GameClient.soundZap.play();
            isFire = false;
            timerDisappear += delta;
            batch.draw(textureRegionsDisappear[indexDisappear],
                    polygon.getX() - DISAPPEAR_TEXTURE_WIDTH / 2 + width / 2,
                    polygon.getY() - DISAPPEAR_TEXTURE_HEIGHT / 2 + height / 2,
                    DISAPPEAR_TEXTURE_WIDTH, DISAPPEAR_TEXTURE_HEIGHT);
            if (timerDisappear > 0.03) {
                indexDisappear++;
                timerDisappear = 0;
            }
        }
    }

    /**
     * Draw appear animation.
     * @param delta delta.
     * @param batch batch.
     */
    public void appearAnimation(float delta, SpriteBatch batch) {
        if (indexAppear == 16) {
            isAppeared = false;
            indexAppear = 0;
        }
        if (isAppeared) {
            GameClient.soundZap.play();
            timerAppear += delta;
            batch.draw(textureRegionsDisappear[indexAppear],
                    polygon.getX() - DISAPPEAR_TEXTURE_WIDTH / 2 + width / 2,
                    polygon.getY() - DISAPPEAR_TEXTURE_HEIGHT / 2 + height / 2,
                    DISAPPEAR_TEXTURE_WIDTH, DISAPPEAR_TEXTURE_HEIGHT);
            if (timerAppear > 0.03) {
                indexAppear++;
                timerAppear = 0;
            }
        }
    }

    /**
     * Draw death animation.
     * @param delta delta.
     * @param batch batch.
     */
    public void deathAnimation(float delta, SpriteBatch batch) {
        if (isDeath && indexDeath < 40) {
            timerDeath += delta;
            batch.draw(textureRegionsDeath[indexDeath],
                    deathX - DEATH_TEXTURE_WIDTH / 2 + width / 2,
                    deathY,
                    DEATH_TEXTURE_WIDTH, DEATH_TEXTURE_HEIGHT);
            if (timerDeath > 0.03) {
                indexDeath++;
                timerDeath = 0;
            }
        }
    }

    /**
     * Draw fire animation.
     * @param delta delta.
     * @param batch batch.
     */
    public void fireAnimation(float delta, SpriteBatch batch) {
        if (indexFire == 16) {
            indexFire = 0;
        }
        if (isFire) {
            timerFire += delta;
            batch.draw(textureRegionsFire[indexFire],
                    polygon.getX() - FIRE_TEXTURE_WIDTH / 2 + width / 2,
                    polygon.getY() - 30,
                    FIRE_TEXTURE_WIDTH, FIRE_TEXTURE_HEIGHT);
            if (timerFire > 0.03) {
                indexFire++;
                timerFire = 0;
            }
        }
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public void setDeath(boolean death, float x, float y) {
        isDeath = death;
        if (deathX == -99999 && deathY == -99999) {
            deathX = x;
            deathY = y;
        }
    }

    public void setFire(boolean fire) {
        isFire = fire;
    }
}
