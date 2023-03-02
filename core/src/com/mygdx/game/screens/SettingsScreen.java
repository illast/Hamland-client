package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.GameClient;
import com.mygdx.game.objects.Button;

public class SettingsScreen implements Screen {

    private final GameClient gameClient;
    private SpriteBatch batch;

    // Properties
    public static final int EXIT_BUTTON_X = 175;
    public static final int EXIT_BUTTON_Y = 100;
    public static final float EXIT_BUTTON_WIDTH = 200f;
    public static final float EXIT_BUTTON_HEIGHT = (float) 1264 / 1383 * EXIT_BUTTON_WIDTH;

    public static final float SOUND_MUSIC_BUTTON_X = (float) GameClient.WIDTH / 2;
    public static final float SOUND_MUSIC_BUTTON_Y = (float) GameClient.HEIGHT / 2;
    public static final float SOUND_MUSIC_BUTTON_WIDTH = 100f;
    public static final float SOUND_MUSIC_BUTTON_HEIGHT = 100f;

    // Textures
    private Texture exitButtonTexture;
    private Texture exitButtonWhiteTexture;
    private Texture soundUpButtonTexture;
    private Texture soundMuteButtonTexture;
    private Texture musicUpButtonTexture;
    private Texture musicMuteButtonTexture;
    private Texture backgroundTexture;

    // Objects
    private Button exitButton;
    private Button exitButtonWhite;
    private Button soundUpButton;
    private Button soundMuteButton;
    private Button musicUpButton;
    private Button musicMuteButton;

    private boolean isMusicUp;
    private boolean isSoundUp;

    public SettingsScreen(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Textures
        exitButtonTexture = new Texture("buttons/exit_button_active.png");
        exitButtonWhiteTexture = new Texture("buttons/exit_button_inactive.png");
        soundUpButtonTexture = new Texture("buttons/sound_up.png");
        soundMuteButtonTexture = new Texture("buttons/sound_mute.png");
        musicUpButtonTexture = new Texture("buttons/music_up.png");
        musicMuteButtonTexture = new Texture("buttons/music_mute.png");
        backgroundTexture = new Texture("background/background.png");

        // Button objects
        exitButton = new Button(exitButtonTexture, EXIT_BUTTON_X, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        exitButtonWhite = new Button(exitButtonWhiteTexture, EXIT_BUTTON_X, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        soundUpButton = new Button(soundUpButtonTexture, SOUND_MUSIC_BUTTON_X - SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_Y, SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_HEIGHT);
        soundMuteButton = new Button(soundMuteButtonTexture, SOUND_MUSIC_BUTTON_X - SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_Y, SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_HEIGHT);
        musicUpButton = new Button(musicUpButtonTexture, SOUND_MUSIC_BUTTON_X + SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_Y, SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_HEIGHT);
        musicMuteButton = new Button(musicMuteButtonTexture, SOUND_MUSIC_BUTTON_X + SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_Y, SOUND_MUSIC_BUTTON_WIDTH, SOUND_MUSIC_BUTTON_HEIGHT);

        isMusicUp = true;
        isSoundUp = true;
    }

    @Override
    public void render(float delta) {

        Gdx.graphics.setTitle("Settings (" + Gdx.graphics.getFramesPerSecond() + "FPS)");

        batch.begin(); // start

        batch.draw(backgroundTexture, 0, 0, GameClient.WIDTH, GameClient.HEIGHT);

        // if mouse X-coordinate and Y-coordinate on the exit button
        if (Gdx.input.getX() > exitButton.polygon.getX() && Gdx.input.getX() + 30 < exitButton.polygon.getX() + EXIT_BUTTON_WIDTH &&
                GameClient.HEIGHT - Gdx.input.getY() - 45 > exitButton.polygon.getY() && GameClient.HEIGHT - Gdx.input.getY() + 30 < exitButton.polygon.getY() + EXIT_BUTTON_HEIGHT) {
            exitButton.draw(batch); // draw in color selected button

            // if click - set screen to MenuScreen
            if (Gdx.input.isTouched()) {
                gameClient.setScreen(new MenuScreen(gameClient));
            }

        } else {
            exitButtonWhite.draw(batch); // draw transparent exit button
        }

        // if mouse X-coordinate and Y-coordinate on the sound button
        if (Gdx.input.getX() > soundUpButton.polygon.getX() && Gdx.input.getX() < soundUpButton.polygon.getX() + SOUND_MUSIC_BUTTON_WIDTH &&
                GameClient.HEIGHT - Gdx.input.getY() > soundUpButton.polygon.getY() && GameClient.HEIGHT - Gdx.input.getY() < soundUpButton.polygon.getY() + SOUND_MUSIC_BUTTON_HEIGHT) {

            // if click - change sound volume
            if (Gdx.input.justTouched()) {
                isSoundUp = !isSoundUp;

                float soundVolume = GameClient.soundBulletShot.getVolume();
                if (soundVolume == 1f) {
                    GameClient.soundBulletShot.setVolume(0);
                    GameClient.soundExplosion.setVolume(0);
                    GameClient.soundDamageTaken.setVolume(0);
                    GameClient.soundZap.setVolume(0);
                    GameClient.soundHeal.setVolume(0);
                    GameClient.soundAmmo.setVolume(0);
                    GameClient.soundEmptyShot.setVolume(0);
                    GameClient.soundReload.setVolume(0);
                }
                else if (soundVolume == 0) {
                    GameClient.soundBulletShot.setVolume(1f);
                    GameClient.soundExplosion.setVolume(1f);
                    GameClient.soundDamageTaken.setVolume(1f);
                    GameClient.soundZap.setVolume(1f);
                    GameClient.soundHeal.setVolume(1f);
                    GameClient.soundAmmo.setVolume(1f);
                    GameClient.soundEmptyShot.setVolume(1f);
                    GameClient.soundReload.setVolume(1f);
                }
            }
        }

        // if mouse X-coordinate and Y-coordinate on the music button
        if (Gdx.input.getX() > musicUpButton.polygon.getX() && Gdx.input.getX() < musicUpButton.polygon.getX() + SOUND_MUSIC_BUTTON_WIDTH &&
                GameClient.HEIGHT - Gdx.input.getY() > musicUpButton.polygon.getY() && GameClient.HEIGHT - Gdx.input.getY() < musicUpButton.polygon.getY() + SOUND_MUSIC_BUTTON_HEIGHT) {

            // if click - change music volume
            if (Gdx.input.justTouched()) {
                isMusicUp = !isMusicUp;

                float musicVolume = GameClient.music.getVolume();
                if (musicVolume == 1f) GameClient.music.setVolume(0);
                else if (musicVolume == 0) GameClient.music.setVolume(1f);
            }
        }

        // draw sound/music buttons
        if (isSoundUp) {
            soundUpButton.draw(batch);
        } else {
            soundMuteButton.draw(batch);
        }

        if (isMusicUp) {
            musicUpButton.draw(batch);
        } else {
            musicMuteButton.draw(batch);
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
        exitButtonTexture.dispose();
        exitButtonWhiteTexture.dispose();
        soundUpButtonTexture.dispose();
        soundMuteButtonTexture.dispose();
        musicUpButtonTexture.dispose();
        musicMuteButtonTexture.dispose();
        backgroundTexture.dispose();
        gameClient.dispose();
    }
}
