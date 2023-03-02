package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.GameClient;
import com.mygdx.game.client.KryoClient;
import com.mygdx.game.objects.Button;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LobbyScreen implements Screen {

    private final GameClient gameClient;
    private SpriteBatch batch;

    // Player readiness data.
    private boolean isPlayerReady;
    private Map<String, Boolean> teammatesReady;

    // Server game begin timer.
    private int serverGameBeginTimerCurrent;
    private int serverGameBeginTimerStop;

    // Properties
    public static final float EXIT_BUTTON_WIDTH = 200f;
    public static final float EXIT_BUTTON_HEIGHT = (float) 1264 / 1383 * EXIT_BUTTON_WIDTH;
    public static final int EXIT_BUTTON_X = 175;
    public static final int EXIT_BUTTON_Y = 100;

    public static final float READY_BUTTON_WIDTH = 170f;
    public static final float READY_BUTTON_HEIGHT = (float) 808 / 1107 * READY_BUTTON_WIDTH;
    public static final int READY_BUTTON_X = 650;
    public static final int READY_BUTTON_Y = 120;

    public static final int TIMER_X = 990;
    public static final int TIMER_Y = 497;

    public static final int[] LEFT_BUTTON_VERTICES = {850, 880, 480, 520};
    public static final int[] RIGHT_BUTTON_VERTICES = {1160, 1190, 480, 520};

    public static final int NICKNAME_X = 150;
    public static final int NICKNAME_Y = 545;
    public static final int SIGN_X = 100;
    public static final int SIGN_Y = 518;

    public static final int TEAMMATES_NICKNAME_X = 150;
    public static final int TEAMMATES_NICKNAME_Y = 502;
    public static final int TEAMMATES_SIGN_X = 100;
    public static final int TEAMMATES_SIGN_Y = 475;

    public static final int SIGN_SIZE = 29;
    public static final int DISTANCE_CONSTANT = 43;

    // Textures
    private Texture exitButtonActiveTexture;
    private Texture exitButtonInactiveTexture;
    private Texture readyButtonActiveTexture;
    private Texture readyButtonInactiveTexture;
    private Texture tick;
    private Texture cross;

    // Objects
    private Button exitButtonActive;
    private Button exitButtonInactive;
    private Button readyButtonActive;
    private Button readyButtonInactive;

    // Font
    private BitmapFont blackFont;
    private BitmapFont redFont;

    // Lobby maps
    private Texture[] lobbyMaps;
    private int mapIndex;

    public LobbyScreen(GameClient gameClient) {
        this.gameClient = gameClient;
        this.isPlayerReady = false;
        this.teammatesReady = gameClient.client.getTeammatesReady();
        this.serverGameBeginTimerCurrent = gameClient.client.getServerTimerGameBeginCurrent();
        this.serverGameBeginTimerStop = gameClient.client.getServerTimerGameBeginStopValue();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Textures
        exitButtonActiveTexture = new Texture("buttons/exit_button_active.png");
        exitButtonInactiveTexture = new Texture("buttons/exit_button_inactive.png");
        readyButtonActiveTexture = new Texture("buttons/ready_button_active.png");
        readyButtonInactiveTexture = new Texture("buttons/ready_button_inactive.png");
        tick = new Texture("background/tick.png");
        cross = new Texture("background/cross.png");

        // Button objects
        exitButtonActive = new Button(exitButtonActiveTexture, EXIT_BUTTON_X, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        exitButtonInactive = new Button(exitButtonInactiveTexture, EXIT_BUTTON_X, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        readyButtonActive = new Button(readyButtonActiveTexture, READY_BUTTON_X, READY_BUTTON_Y, READY_BUTTON_WIDTH, READY_BUTTON_HEIGHT);
        readyButtonInactive = new Button(readyButtonInactiveTexture, READY_BUTTON_X, READY_BUTTON_Y, READY_BUTTON_WIDTH, READY_BUTTON_HEIGHT);

        // Font
        blackFont = new BitmapFont(Gdx.files.internal("fonts/black.fnt"));
        redFont = new BitmapFont(Gdx.files.internal("fonts/red_big.fnt"));

        // Lobby maps
        lobbyMaps = new Texture[2];
        lobbyMaps[0] = new Texture("background/countryside_lobby.png");
        lobbyMaps[1] = new Texture("background/clear_lobby.png");
    }

    @Override
    public void render(float delta) {

        Gdx.graphics.setTitle("Lobby (" + Gdx.graphics.getFramesPerSecond() + "FPS)");

        batch.begin(); // start

        changeMap();

        drawButtons();

        drawNicknamesAndSigns();

        // Update teammates' readiness data.
        this.teammatesReady = gameClient.client.getTeammatesReady();

        updateAndDrawTimer();

        // Start the game when global timer (Server timer) is up.
        if (this.serverGameBeginTimerStop == this.serverGameBeginTimerCurrent + 1) {
            gameClient.setScreen(new PlayScreen(gameClient));
        }

        batch.end(); //end
    }

    /**
     * Change the map to the PREVIOUS one by pressing the LEFT button.
     * Change the map to the NEXT one by pressing the RIGHT button
     */
    public void changeMap() {

        // Right button
        if (Gdx.input.getX() > RIGHT_BUTTON_VERTICES[0]
                && Gdx.input.getX() < RIGHT_BUTTON_VERTICES[1]
                && Gdx.input.getY() > RIGHT_BUTTON_VERTICES[2]
                && Gdx.input.getY() < RIGHT_BUTTON_VERTICES[3]
                && Gdx.input.justTouched()) {
            if (mapIndex + 1 != lobbyMaps.length) mapIndex++;
            else mapIndex--;
        }

        // Left button
        if (Gdx.input.getX() > LEFT_BUTTON_VERTICES[0]
                && Gdx.input.getX() < LEFT_BUTTON_VERTICES[1]
                && Gdx.input.getY() > LEFT_BUTTON_VERTICES[2]
                && Gdx.input.getY() < LEFT_BUTTON_VERTICES[3]
                && Gdx.input.justTouched()) {
            if (mapIndex != 0) mapIndex--;
            else mapIndex++;
        }

        batch.draw(lobbyMaps[mapIndex], 0, 0, GameClient.WIDTH, GameClient.HEIGHT);
    }

    /**
     * Draw buttons and check if there is a cursor on them.
     * If yes - change the button to the active one.
     * If the exit button is pressed - change the screen to the menu screen.
     * If the ready button is pressed - ...
     */
    public void drawButtons() {

        // if mouse X-coordinate and Y-coordinate on the exit button
        if (Gdx.input.getX() > exitButtonActive.polygon.getX()
         && Gdx.input.getX() + 30 < exitButtonActive.polygon.getX() + EXIT_BUTTON_WIDTH
         && GameClient.HEIGHT - Gdx.input.getY() - 45 > exitButtonActive.polygon.getY()
         && GameClient.HEIGHT - Gdx.input.getY() + 30 < exitButtonActive.polygon.getY() + EXIT_BUTTON_HEIGHT) {

            exitButtonActive.draw(batch); // draw in color selected button

            // if click - set screen to MenuScreen
            if (Gdx.input.isTouched()) {
                gameClient.setScreen(new MenuScreen(gameClient));
            }

        } else {
            exitButtonInactive.draw(batch); // draw transparent exit button
        }

        // if mouse X-coordinate and Y-coordinate on the ready button
        if (Gdx.input.getX() > readyButtonActive.polygon.getX()
                && Gdx.input.getX() < readyButtonActive.polygon.getX() + READY_BUTTON_WIDTH
                && GameClient.HEIGHT - Gdx.input.getY() > readyButtonActive.polygon.getY()
                && GameClient.HEIGHT - Gdx.input.getY() + 22 < readyButtonActive.polygon.getY() + READY_BUTTON_HEIGHT) {

            readyButtonActive.draw(batch); // draw in color selected button

            // if click - send packet PacketPlayerReady to notify the server (and other players) that this player is ready to play.
            if (Gdx.input.justTouched()) {
                // When buttons is pressed -> change readiness to the opposite value.
                isPlayerReady = !isPlayerReady;
                this.gameClient.client.sendPacketPlayerReady(isPlayerReady);
            }

        } else {
            readyButtonInactive.draw(batch); // draw transparent ready button
        }
    }

    /**
     * Display your nickname, nicknames of teammates and signs of readiness.
     */
    public void drawNicknamesAndSigns() {
        List<String> nicknames = new LinkedList<>(teammatesReady.keySet());

        // Write own nickname first
        blackFont.draw(batch, KryoClient.nickname.toUpperCase(Locale.ROOT) + " (YOU)", NICKNAME_X, NICKNAME_Y);

        // Draw a sign of readiness near the nickname
        if (isPlayerReady) {
            batch.draw(tick, SIGN_X, SIGN_Y, SIGN_SIZE, SIGN_SIZE);
        } else {
            batch.draw(cross, SIGN_X, SIGN_Y, SIGN_SIZE, SIGN_SIZE);
        }
        
        // Write all nicknames of teammates
        StringBuilder teammates = new StringBuilder();
        for (String teammateNickname : nicknames) {
            teammates.append(teammateNickname.toUpperCase(Locale.ROOT)).append("\n");

            // Draw a sign of readiness near the nickname
            if (teammatesReady.get(teammateNickname)) {
                batch.draw(tick, TEAMMATES_SIGN_X, TEAMMATES_SIGN_Y - nicknames.indexOf(teammateNickname) * DISTANCE_CONSTANT, SIGN_SIZE, SIGN_SIZE);
            } else {
                batch.draw(cross, TEAMMATES_SIGN_X, TEAMMATES_SIGN_Y - nicknames.indexOf(teammateNickname) * DISTANCE_CONSTANT, SIGN_SIZE, SIGN_SIZE);
            }
        }
        blackFont.draw(batch, teammates, TEAMMATES_NICKNAME_X, TEAMMATES_NICKNAME_Y);
    }

    /**
     * Update the timer, draw on the screen when it starts.
     */
    public void updateAndDrawTimer() {

        // Update timer
        this.serverGameBeginTimerCurrent = gameClient.client.getServerTimerGameBeginCurrent();
        this.serverGameBeginTimerStop = gameClient.client.getServerTimerGameBeginStopValue();

        // Draw timer
        if (serverGameBeginTimerCurrent > 0) {
            redFont.draw(batch, String.valueOf(3 - serverGameBeginTimerCurrent), TIMER_X, TIMER_Y);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        // Clear memory when game is off
        batch.dispose();
        exitButtonActiveTexture.dispose();
        exitButtonInactiveTexture.dispose();
        readyButtonActiveTexture.dispose();
        readyButtonInactiveTexture.dispose();
        blackFont.dispose();
        redFont.dispose();
        gameClient.dispose();
    }
}
