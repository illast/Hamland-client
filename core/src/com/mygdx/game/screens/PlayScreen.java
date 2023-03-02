package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.GameClient;
import com.mygdx.game.objects.*;

import java.util.*;

public class PlayScreen implements Screen {

    private final GameClient gameClient;
    private SpriteBatch batch;

    private float prevRotation;

    public Map<Integer, Enemy> enemies = new HashMap<>();
    public Set<Integer> killedEnemiesId = new HashSet<>();
    public Map<String, BulletTeammate> teammateBullets = new HashMap<>();
    public Map<Integer, Loot> spawnedLoot = new HashMap<>();

    // Properties
    private final static int PLAYER_START_HP = 3;

    public static final int PLAYER_X = 1190;
    public static final int PLAYER_Y = 910;

    public static final float TEXTURE_SIZES_CONSTANT = 0.065f;

    public static final int PLAYER_WIDTH = (int) (771 * TEXTURE_SIZES_CONSTANT);
    public static final int PLAYER_HEIGHT = (int) (1054 * TEXTURE_SIZES_CONSTANT);

    public static final int BULLET_WIDTH = (int) (512 * TEXTURE_SIZES_CONSTANT);;
    public static final int BULLET_HEIGHT = (int) (512 * TEXTURE_SIZES_CONSTANT);;

    public static final int ZOMBIE_WIDTH = (int) (694 * TEXTURE_SIZES_CONSTANT);
    public static final int ZOMBIE_HEIGHT = (int) (1167 * TEXTURE_SIZES_CONSTANT);

    public static final int OCTOPUS_WIDTH = (int) (923 * TEXTURE_SIZES_CONSTANT);
    public static final int OCTOPUS_HEIGHT = (int) (986 * TEXTURE_SIZES_CONSTANT);

    public static final int BLUEGUY_WIDTH = (int) (562 * TEXTURE_SIZES_CONSTANT);
    public static final int BLUEGUY_HEIGHT = (int) (776 * TEXTURE_SIZES_CONSTANT);

    public static final int GREENGUY_WIDTH = (int) (887 * TEXTURE_SIZES_CONSTANT);
    public static final int GREENGUY_HEIGHT = (int) (726 * TEXTURE_SIZES_CONSTANT);

    public static final int CRAB_WIDTH = (int) (824 * TEXTURE_SIZES_CONSTANT);
    public static final int CRAB_HEIGHT = (int) (550 * TEXTURE_SIZES_CONSTANT);

    public static final int GRAVE_WIDTH = PLAYER_WIDTH;
    public static final int GRAVE_HEIGHT = PLAYER_WIDTH * 380 / 175;

    public static final int ZOMBIE_MAX_HP = 3;
    public static final int OCTOPUS_MAX_HP = 5;
    public static final int CRAB_MAX_HP = 6;
    public static final int BLUE_GUY_MAX_HP = 1;
    public static final int GREEN_GUY_MAX_HP = 100;

    public static final int AMMO_CRATE_WIDTH = (int) (1213 / 2 * TEXTURE_SIZES_CONSTANT);
    public static final int AMMO_CRATE_HEIGHT = (int) (1157 / 2 * TEXTURE_SIZES_CONSTANT);
    public static final int AMMO_CRATE_REFILL_AMOUNT = 30;

    public static final int MED_KIT_WIDTH = (int) (1073 / 2 * TEXTURE_SIZES_CONSTANT);
    public static final int MED_KIT_HEIGHT = (int) (939 / 2 * TEXTURE_SIZES_CONSTANT);
    public static final int MED_KIT_HP_HEAL_AMOUNT = 3;  //NB! If changing this value also change it on the server!!!

    // Textures
    private Texture playerTexture;
    private Texture zombieTexture;
    private Texture octopusTexture;
    private Texture blueGuyTexture;
    private Texture greenGuyTexture;
    private Texture crabTexture;
    private Texture bulletTexture;
    private Texture ammoCrateTexture;
    private Texture medKitTexture;
    private Texture highScoreTexture;

    // Objects
    private Player player;
    private Bullet bullet;

    // Camera
    private OrthographicCamera camera;
    public static float cameraX, cameraY;

    // TiledMap
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    public static float tileWidth, tileHeight;
    private boolean collisionX;
    private boolean collisionY;
    private TiledMapTileLayer waterCollisionLayer;
    private TiledMapTileLayer buildingsCollisionLayer;

    // Fonts
    private final BitmapFont greenFont;
    private final BitmapFont blackFont;
    private final BitmapFont highScoreFont;

    public PlayScreen(GameClient gameClient) {
        this.gameClient = gameClient;
        // Textures
        playerTexture = new Texture("players/player1.png");
        zombieTexture = new Texture("enemies/zombie_enemy.png");
        octopusTexture = new Texture("enemies/octopus_enemy.png");
        blueGuyTexture = new Texture("enemies/new_enemy_1.png");
        greenGuyTexture = new Texture("enemies/new_enemy_3.png");
        crabTexture = new Texture("enemies/new_enemy_2.png");
        bulletTexture = new Texture("players/bullet.png");
        highScoreTexture = new Texture("background/high_score.png");


        // Loot
        ammoCrateTexture = new Texture("loot/loot_ammo_crate.png");
        medKitTexture = new Texture("loot/loot_medkit.png");

        // Objects
        player = new Player(playerTexture, PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);
        player.setHp(PLAYER_START_HP);
        gameClient.client.setPlayer(player);

        // Fonts
        greenFont = new BitmapFont(Gdx.files.internal("fonts/green.fnt"));
        blackFont = new BitmapFont(Gdx.files.internal("fonts/nickname.fnt"));
        highScoreFont = new BitmapFont(Gdx.files.internal("fonts/reload.fnt"));

        bullet = new Bullet(bulletTexture, 0, 0, BULLET_WIDTH, BULLET_HEIGHT, player);

        for (String teammateNickname : gameClient.client.getTeammates().keySet()) {
            Teammate teammate = new Teammate(playerTexture, PLAYER_X, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT, teammateNickname);
            teammate.setHp(PLAYER_START_HP);
            gameClient.client.getTeammates().put(teammateNickname, teammate);
        }

        camera = new OrthographicCamera(player.polygon.getX(), player.polygon.getY());
        camera.setToOrtho(false);

        // Assign new bullet object for every teammate.
        for (String teammateNickname : gameClient.client.getTeammates().keySet()) {
            this.teammateBullets.put(teammateNickname, new BulletTeammate(bulletTexture, 100, 100, BULLET_WIDTH, BULLET_HEIGHT));
        }

        // TiledMap
        map = new TmxMapLoader().load("tiledmap/samplemap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        waterCollisionLayer = (TiledMapTileLayer) map.getLayers().get("water");
        buildingsCollisionLayer = (TiledMapTileLayer) map.getLayers().get("building");
        tileWidth = waterCollisionLayer.getTileWidth();
        tileHeight = waterCollisionLayer.getTileHeight();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {

        // Update screen white background
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.graphics.setTitle("Play (" + Gdx.graphics.getFramesPerSecond() + "FPS)");

        renderer.setView(camera);
        renderer.render();

        batch.begin(); // start

        // Previous player's position
        float prevPlayerX = player.polygon.getX();
        float prevPlayerY = player.polygon.getY();

        // Camera
        detectEdgeOfTheMap();
        camera.position.set(cameraX, cameraY, 0);
        camera.update();
        updateLootPosition();

        if (!player.isAlive()) {
            player.draw(batch, bullet, camera, delta); // draw player
        }

        updateTeammatePosition(delta); // update teammates' positions

        if (player.isAlive()) {
            player.draw(batch, bullet, camera, delta); // draw player
        }

        detectInput(); // send packet
        detectCollision(prevPlayerX, prevPlayerY); // detect collision
        updateEnemiesPosition(delta);
        checkPlayerAndLootCollision();
        drawBullet(delta); // bullets

        batch.setProjectionMatrix(camera.combined);

        if (this.gameClient.client.getIsGameEnd()) {
            // If game has ended (all players are dead).
            batch.draw(highScoreTexture,
                    cameraX - GameClient.WIDTH / 2f,
                    cameraY - GameClient.HEIGHT / 2f,
                    GameClient.WIDTH,
                    GameClient.HEIGHT);
            String statisticsString = this.gameClient.client.getStatisticsString();
            String[] lines = statisticsString.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String[] score = lines[i].split(" +");
                if (score.length > 1) {
                    highScoreFont.draw(batch, score[0].toUpperCase(Locale.ROOT), cameraX - 480, cameraY + 100 - 50 * i);
                    highScoreFont.draw(batch, score[1], cameraX - 70, cameraY + 100 - 50 * i);
                }
            }
        }

        batch.end(); // end
    }

    /**
     * Send a packet with the player's coordinates to the server, if one of the control buttons is pressed.
     */
    private void detectInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP) ||
                Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
                Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) ||
                player.polygon.getRotation() != prevRotation) {
            prevRotation = player.polygon.getRotation();
            gameClient.client.sendPlayerMovementInformation(player.polygon.getX(), player.polygon.getY(), player.polygon.getRotation());
        }
    }

    public void updateBullet(float delta) {
        // Update player's own bullet.
        bullet.shot(enemies, delta, gameClient, batch);
        bullet.draw(batch);

        // Render teammates' bullets.
        for (String teammateNickname : teammateBullets.keySet()) {
            if (gameClient.client.getTeammatesShot().containsKey(teammateNickname)
                    && gameClient.client.getTeammatesShot().get(teammateNickname)) {
                // If this teammate has made a shot.
                BulletTeammate bulletTeammate = teammateBullets.get(teammateNickname);

                // Render his bullet.
                bulletTeammate.renderShot(gameClient.client.getTeammates().get(teammateNickname).polygon, enemies, delta, batch);
                bulletTeammate.draw(batch);

                // If bullet was shot -> stop rendering it after certain amount of time.
                if (!teammateBullets.get(teammateNickname).isShot) {
                    gameClient.client.getTeammatesShot().put(teammateNickname, false);
                }
            }
        }
    }

    public void checkPlayerAndLootCollision() {

        List<Integer> lootIndexesToRemove = new ArrayList<>();

        for (int lootIndex : this.spawnedLoot.keySet()) {

            Loot loot = this.spawnedLoot.get(lootIndex);
            if (((loot.getX() <= player.polygon.getX() && player.polygon.getX() <= loot.getX() + loot.getWidth())
                    && (loot.getY() <= player.polygon.getY() && player.polygon.getY() <= loot.getY() + loot.getHeight()))
                    || ((loot.getX() <= player.polygon.getX() + player.getWidth() && player.polygon.getX() + player.getWidth() <= loot.getX() + loot.getWidth())
                    && (loot.getY() <= player.polygon.getY() && player.polygon.getY() <= loot.getY() + loot.getHeight()))
                    || ((loot.getX() <= player.polygon.getX() + player.getWidth() && player.polygon.getX() + player.getWidth() <= loot.getX() + loot.getWidth())
                    && (loot.getY() <= player.polygon.getY() + player.getHeight() && player.polygon.getY() + player.getHeight() <= loot.getY() + loot.getHeight()))
                    || ((loot.getX() <= player.polygon.getX() && player.polygon.getX() <= loot.getX() + loot.getWidth())
                    && (loot.getY() <= player.polygon.getY() + player.getHeight() && player.polygon.getY() + player.getHeight() <= loot.getY() + loot.getHeight()))) {
                // If player has collected loot.

                // Send the packet to the server.
                this.gameClient.client.sendPacketLootCollected(lootIndex, loot.getType() == 1);

                if (loot.getType() == 0) {
                    // If this loot is an ammo crate -> refill the ammo.
                    this.bullet.addAmmo(AMMO_CRATE_REFILL_AMOUNT);
                    this.player.setAmmoTaken(true);
                    GameClient.soundAmmo.play();
                }

                if (loot.getType() == 1) {
                    // If this loot is a med kit -> heal the player.
                    this.player.setHp(MED_KIT_HP_HEAL_AMOUNT);
                    this.player.setHealTaken(true);
                    GameClient.soundHeal.play();
                }

                lootIndexesToRemove.add(lootIndex);
            }
        }

        // Stop rendering collected loot objects -> remove them from the hashmaps.
        for (int lootIndex : lootIndexesToRemove) {
            gameClient.client.removeLootPosition(lootIndex);
            this.spawnedLoot.remove(lootIndex);
        }
    }

    /**
     * Draw loot objects when they are spawned.
     */
    public void updateLootPosition() {
        /*
        [1000, 1050], [950, 1050], [900, 1050]
        [1200, 1050], [1250, 1050], [1300, 1050]
        [600, 1750], [250, 1450], [125, 1050]
        [400, 900], [125, 650], [250, 500]
        [650, 450], [700, 150], [950, 250]
        [1800, 125] [1300, 500] [1200, 750] [1400, 850]
         */
        for (int lootPositionIndex : gameClient.client.getLootPositions().keySet()) {

            float[] lootData = gameClient.client.getLootPositions().get(lootPositionIndex);
            float lootType = lootData[2];

            // If there was no new loot object created -> create it.
            if (!this.spawnedLoot.containsKey(lootPositionIndex)) {
                System.out.println(this.gameClient.client.getLootPositions());
                float lootSpawnPosX = lootData[0];
                float lootSpawnPosY = lootData[1];

                if (lootType == 0) {
                    // If loot is an ammo crate.
                    Loot newLoot = new Loot(ammoCrateTexture, lootSpawnPosX, lootSpawnPosY, AMMO_CRATE_WIDTH, AMMO_CRATE_HEIGHT);
                    newLoot.setType(0);
                    this.spawnedLoot.put(lootPositionIndex, newLoot);
                    newLoot.draw(batch);
                }

                if (lootType == 1.0) {
                    // If loot is a med kit.
                    Loot newLoot = new Loot(medKitTexture, lootSpawnPosX, lootSpawnPosY, MED_KIT_WIDTH, MED_KIT_HEIGHT);
                    newLoot.setType(1);
                    this.spawnedLoot.put(lootPositionIndex, newLoot);
                    newLoot.draw(batch);
                }
            }

            else {
                // If loot object with this index was already created -> simply render it.
                this.spawnedLoot.get(lootPositionIndex).draw(batch);
            }
        }
    }

    /**
     * Change the position of another player (works in the loop).
     */
    public void updateTeammatePosition(float delta) {

        for (Teammate teammate : gameClient.client.getTeammates().values()) {

            if (teammate != null) {

                teammate.draw(batch, delta);

                if (!teammate.isAlive()) {
                    // If teammate is dead.
                    teammate.setSpriteDeadPlayer();
                }
            }
        }
    }

    /**
     * Change the positions of all enemies (works in the loop).
     *
     * The data from client.getEnemiesData is received from the server every iteration.
     */
    public void updateEnemiesPosition(float delta) {
        for (int mobId : gameClient.client.getEnemiesData().keySet()) {

            // Mob data: posX, posY, type, hp.
            float[] mobData = gameClient.client.getEnemiesData().get(mobId);

            if (!this.enemies.containsKey(mobId) && !this.killedEnemiesId.contains(mobId)) {
                // If such Enemy object was not created yet -> check Enemy type and create respective object.
                if (mobData[2] == 0.0) {
                    // If mob is zombie.
                    this.enemies.put(mobId, new Zombie(zombieTexture, mobData[0], mobData[1], ZOMBIE_WIDTH, ZOMBIE_HEIGHT, 0, ZOMBIE_MAX_HP));
                }

                if (mobData[2] == 1.0) {
                    // If mob is octopus.
                    this.enemies.put(mobId, new Octopus(octopusTexture, mobData[0], mobData[1], OCTOPUS_WIDTH, OCTOPUS_HEIGHT, 0, OCTOPUS_MAX_HP));
                }

                if (mobData[2] == 2.0) {
                    // If mob is crab.
                    this.enemies.put(mobId, new Crab(crabTexture, mobData[0], mobData[1], CRAB_WIDTH, CRAB_HEIGHT, 0, CRAB_MAX_HP));
                }

                if (mobData[2] == 3.0) {
                    // If mob is a blueguy.
                    this.enemies.put(mobId, new BlueGuy(blueGuyTexture, mobData[0], mobData[1], BLUEGUY_WIDTH, BLUEGUY_HEIGHT, 0, BLUE_GUY_MAX_HP));
                }

                if (mobData[2] == 4.0) {
                    // If mob is a greenguy.
                    this.enemies.put(mobId, new GreenGuy(greenGuyTexture, mobData[0], mobData[1], 250, 250, 0, GREEN_GUY_MAX_HP));
                }

                this.enemies.get(mobId).polygon.setPosition(mobData[0], mobData[1]);
                this.enemies.get(mobId).draw(batch, buildingsCollisionLayer, delta);
            }

            else {
                // If such mob already exists -> update its data.
                Enemy enemyToUpdate = enemies.get(mobId);
                enemyToUpdate.polygon.setPosition(mobData[0], mobData[1]);

                // Only update mob HP if it was hit.
                if (enemyToUpdate.getHp() != mobData[3]) {
                    // mobData[3] contains mob HP value.
                    enemyToUpdate.setHp((int) mobData[3]);
                    if (enemyToUpdate.getHp() != enemyToUpdate.getMaxHp()) enemyToUpdate.setFire(true);
                }

                if (enemyToUpdate.getHp() == 0) {
                    // If mob HP is 0 -> kill him (but actually spawn far beyond the map :))
                    this.killedEnemiesId.add(mobId);
                    enemyToUpdate.setDeath(true, enemyToUpdate.polygon.getX(), enemyToUpdate.polygon.getY());
                    enemyToUpdate.deathAnimation(delta, batch);
                    enemyToUpdate.polygon.setPosition(99999, 99999);  // FIND BETTER SOLUTION?
                }

                if (!this.killedEnemiesId.contains(mobId)) enemyToUpdate.draw(batch, buildingsCollisionLayer, delta);
            }
        }
    }

    /**
     * Check if the player's position is on a blocked cell.
     * If yes, change the player's position to the previous one.
     * @param prevPlayerX player's previous X position.
     * @param prevPlayerY player's previous Y position.
     */

    public void detectCollision(float prevPlayerX, float prevPlayerY) {

        // Water collision
        layerCollision(waterCollisionLayer);

        // Buildings collision
        layerCollision(buildingsCollisionLayer);

        // If collision is detected on X, teleport one step back on X
        if (collisionX) {
            player.polygon.setPosition(prevPlayerX, player.polygon.getY());
            collisionX = false;
        }

        // If collision is detected on Y, teleport one step back on Y
        if (collisionY) {
            player.polygon.setPosition(player.polygon.getX(), prevPlayerY);
            collisionY = false;
        }
    }

    /**
     * If the player is on the cell of the layer - assert a collision.
     *
     * Player:
     * -9--12--10-
     * 3        4
     * |        |
     * 5        6
     * |        |
     * 1        2
     * -7--11--8-
     *
     * @param layer TiledMap layer.
     */
    public void layerCollision(TiledMapTileLayer layer) {

        float playerX = player.polygon.getX();
        float playerY = player.polygon.getY();

        if ( // Horizontal collision

            // 1. lower left corner of the player
                layer.getCell((int) (playerX / tileWidth), (int) ((playerY + 2) / tileHeight)) != null ||

                        // 2. lower right corner of the player
                        layer.getCell((int) ((playerX + PLAYER_WIDTH) / tileWidth), (int) ((playerY + 2) / tileHeight)) != null ||

                        // 3. upper left corner of the player
                        layer.getCell((int) (playerX / tileWidth), (int) ((playerY + PLAYER_HEIGHT - 2) / tileHeight)) != null ||

                        // 4. upper right corner of the player
                        layer.getCell((int) ((playerX + PLAYER_WIDTH) / tileWidth), (int) (((playerY - 2) + PLAYER_HEIGHT) / tileHeight)) != null ||

                        // 5. left center point of the player
                        layer.getCell((int) (playerX / tileWidth), (int) ((playerY + PLAYER_HEIGHT / 2) / tileHeight)) != null ||

                        // 6. right center point of the player
                        layer.getCell((int) ((playerX + PLAYER_WIDTH) / tileWidth), (int) ((playerY + PLAYER_HEIGHT / 2) / tileHeight)) != null) {

            collisionX = true;
        }

        if ( // Vertical collision

            // 7. lower left corner of the player
                layer.getCell((int) ((playerX + 2) / tileWidth), (int) (playerY / tileHeight)) != null ||

                        // 8. lower right corner of the player
                        layer.getCell((int) (((playerX - 2) + PLAYER_WIDTH) / tileWidth), (int) (playerY / tileHeight)) != null ||

                        // 9. upper left corner of the player
                        layer.getCell((int) ((playerX + 2) / tileWidth), (int) ((playerY + PLAYER_HEIGHT) / tileHeight)) != null ||

                        // 10. upper right corner of the player
                        layer.getCell((int) (((playerX - 2) + PLAYER_WIDTH) / tileWidth), (int) ((playerY + PLAYER_HEIGHT) / tileHeight)) != null ||

                        // 11. lower center point of the player
                        layer.getCell((int) ((playerX + PLAYER_WIDTH / 2) / tileWidth), (int) (playerY / tileHeight)) != null ||

                        // 12. upper center point of the player
                        layer.getCell((int) ((playerX + PLAYER_WIDTH / 2) / tileWidth), (int) ((playerY + PLAYER_HEIGHT) / tileHeight)) != null) {

            collisionY = true;
        }
    }

    /**
     * When a player is at the edge of the map, stop following him with the camera.
     */
    public void detectEdgeOfTheMap() {

        // Stop camera horizontally
        if (player.polygon.getX() < 1230 && player.polygon.getX() > 590) {
            cameraX = player.polygon.getX() + 50;
        }

        // Stop camera vertically
        if (player.polygon.getY() < 1510 && player.polygon.getY() > 310) {
            cameraY = player.polygon.getY() + 50;
        }
    }

    /**
     * Draw the player's bullets and the amount of hp if he is alive, else draw the rip.
     * @param delta delta time.
     */
    public void drawBullet(float delta) {
        if (player.isAlive()) {
            // If player is alive.

            updateBullet(delta);
        }

        else {
            // If player has died.

            player.setSpriteDeadPlayer();
            player.getPlayerControl().isControlActive(false);
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {

        // Clear memory when game is off
        batch.dispose();
        playerTexture.dispose();
        zombieTexture.dispose();
        octopusTexture.dispose();
        bulletTexture.dispose();
        gameClient.dispose();
        renderer.dispose();
        map.dispose();
        greenFont.dispose();
        blackFont.dispose();
    }
}
