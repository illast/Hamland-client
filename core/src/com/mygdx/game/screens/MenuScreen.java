package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.GameClient;
import com.mygdx.game.objects.Button;

public class MenuScreen implements Screen {

    private final GameClient gameClient;
    private SpriteBatch batch;

    // Properties
    public static final float BUTTON_X = (float) GameClient.WIDTH / 2;
    public static final float BUTTON_Y = (float) GameClient.HEIGHT / 2;
    public static final float BUTTON_WIDTH = 200f;
    public static final float BUTTON_HEIGHT = (float) 790 / 973 * BUTTON_WIDTH;

    public static final int HAMSTERS_X = 120;
    public static final int HAMSTERS_Y = 350;
    public static final int HAMSTERS_WIDTH = 200;
    public static final int HAMSTERS_HEIGHT = 200;

    public static final int WELCOME_X = 870;
    public static final int WELCOME_Y = 380;
    public static final int WELCOME_WIDTH = (int) (385 * 0.75);
    public static final int WELCOME_HEIGHT = (int) (305 * 0.75);

    // Textures
    private Texture playButtonTexture;
    private Texture playButtonWhiteTexture;
    private Texture settingsButtonTexture;
    private Texture settingsButtonWhiteTexture;
    private Texture quitButtonTexture;
    private Texture quitButtonWhiteTexture;
    private Texture backgroundTexture;
    private Texture hamstersTexture;
    private Texture welcomeTexture;

    // Objects
    private Button playButton;
    private Button playButtonWhite;
    private Button settingsButton;
    private Button settingsButtonWhite;
    private Button quitButton;
    private Button quitButtonWhite;

    public MenuScreen(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Textures
        playButtonTexture = new Texture("buttons/play_button_active.png");
        playButtonWhiteTexture = new Texture("buttons/play_button_inactive.png");
        settingsButtonTexture = new Texture("buttons/tools_button_active.png");
        settingsButtonWhiteTexture = new Texture("buttons/tools_button_inactive.png");
        quitButtonTexture = new Texture("buttons/quit_button_active.png");
        quitButtonWhiteTexture = new Texture("buttons/quit_button_inactive.png");

        backgroundTexture = new Texture("background/background.png");
        hamstersTexture = new Texture("background/hamsters.png");
        welcomeTexture = new Texture("background/welcome.png");

        // Button objects
        playButton = new Button(playButtonTexture, BUTTON_X, BUTTON_Y + BUTTON_WIDTH, BUTTON_WIDTH, BUTTON_HEIGHT);
        playButtonWhite = new Button(playButtonWhiteTexture, BUTTON_X, BUTTON_Y + BUTTON_WIDTH, BUTTON_WIDTH,BUTTON_HEIGHT);
        settingsButton = new Button(settingsButtonTexture, BUTTON_X, BUTTON_Y, BUTTON_WIDTH,BUTTON_HEIGHT);
        settingsButtonWhite = new Button(settingsButtonWhiteTexture, BUTTON_X,BUTTON_Y, BUTTON_WIDTH,BUTTON_HEIGHT);
        quitButton = new Button(quitButtonTexture, BUTTON_X, BUTTON_Y - BUTTON_WIDTH, BUTTON_WIDTH,BUTTON_HEIGHT);
        quitButtonWhite = new Button(quitButtonWhiteTexture, BUTTON_X, BUTTON_Y - BUTTON_WIDTH, BUTTON_WIDTH,BUTTON_HEIGHT);
    }

    @Override
    public void render(float delta) {

        Gdx.graphics.setTitle("Menu (" + Gdx.graphics.getFramesPerSecond() + "FPS)");

        batch.begin(); // start

        batch.draw(backgroundTexture,0,0, GameClient.WIDTH, GameClient.HEIGHT);
        batch.draw(hamstersTexture, HAMSTERS_X, HAMSTERS_Y, HAMSTERS_WIDTH, HAMSTERS_HEIGHT);
        batch.draw(welcomeTexture, WELCOME_X, WELCOME_Y, WELCOME_WIDTH, WELCOME_HEIGHT);

        // if mouse X-coordinate and Y-coordinate on the left button
        if (Gdx.input.getX() > playButton.polygon.getX() && Gdx.input.getX() < playButton.polygon.getX() + BUTTON_WIDTH &&
                GameClient.HEIGHT - Gdx.input.getY() - 15 > playButton.polygon.getY() && GameClient.HEIGHT - Gdx.input.getY() + 25 < playButton.polygon.getY() + BUTTON_HEIGHT) {

            playButton.draw(batch); // draw in color selected button

            // if click - set screen to PlayScreen
            if (Gdx.input.isTouched()) {
                gameClient.setScreen(new LobbyScreen(gameClient));
            }

        } else {
            playButtonWhite.draw(batch); // draw transparent button
        }

        // if mouse X-coordinate and Y-coordinate on the center button
        if (Gdx.input.getX() > settingsButton.polygon.getX() && Gdx.input.getX() < settingsButton.polygon.getX() + BUTTON_WIDTH &&
                GameClient.HEIGHT - Gdx.input.getY() - 15 > settingsButton.polygon.getY() && GameClient.HEIGHT - Gdx.input.getY() + 25 < settingsButton.polygon.getY() + BUTTON_HEIGHT) {

            settingsButton.draw(batch); // draw in color selected button

            // if click - set screen to SettingsScreen
            if (Gdx.input.isTouched()) {
                gameClient.setScreen(new SettingsScreen(gameClient));
            }

            // if mouse X-coordinate and Y-coordinate on the right button
        } else {
            settingsButtonWhite.draw(batch); // draw transparent button
        }

        if (Gdx.input.getX() > quitButton.polygon.getX() && Gdx.input.getX() < quitButton.polygon.getX() + BUTTON_WIDTH &&
                GameClient.HEIGHT - Gdx.input.getY() - 15 > quitButton.polygon.getY() && GameClient.HEIGHT - Gdx.input.getY() + 25 < quitButton.polygon.getY() + BUTTON_HEIGHT) {

            quitButton.draw(batch); // draw in color selected button

            // if click - app exit
            if (Gdx.input.isTouched()) {
                Gdx.app.exit();
            }

        } else {
            quitButtonWhite.draw(batch); // draw transparent button
        }

        batch.end(); //end
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
        playButtonTexture.dispose();
        playButtonWhiteTexture.dispose();
        settingsButtonTexture.dispose();
        settingsButtonWhiteTexture.dispose();
        quitButtonTexture.dispose();
        quitButtonWhiteTexture.dispose();
        backgroundTexture.dispose();
        hamstersTexture.dispose();
        welcomeTexture.dispose();
        gameClient.dispose();
    }
}
