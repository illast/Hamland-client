package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.GameClient;
import com.mygdx.game.client.KryoClient;
import com.mygdx.game.objects.Button;

import static com.mygdx.game.client.KryoClient.inscription;

public class NicknameScreen implements Screen, Input.TextInputListener {

    private final GameClient gameClient;
    private SpriteBatch batch;

    // Properties
    public static final float ENTER_NAME_BUTTON_X = (float) GameClient.WIDTH / 2 - 10;
    public static final float ENTER_NAME_BUTTON_Y = (float) GameClient.HEIGHT / 2 - 150;
    public static final float ENTER_NAME_BUTTON_WIDTH = 200f;
    public static final float ENTER_NAME_BUTTON_HEIGHT = (float) 817 / 1116 * ENTER_NAME_BUTTON_WIDTH;

    public static final int WELCOME_X = GameClient.WIDTH / 2 - 190;
    public static final int WELCOME_Y = GameClient.HEIGHT / 2 - 15;
    public static final int WELCOME_WIDTH = 385;
    public static final int WELCOME_HEIGHT = 305;

    // Textures
    private Texture playButtonTexture;
    private Texture playButtonWhiteTexture;
    private Texture backgroundTexture;
    private Texture welcomeTexture;

    // Objects
    private Button playButton;
    private Button playButtonWhite;

    // Text input window
    private String nickname;
    public static boolean isWindowOpened;

    // Fonts
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    public NicknameScreen(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Start the music.
        GameClient.music.setLooping(true);
        GameClient.music.play();
        // Textures
        playButtonTexture = new Texture("buttons/enter_name_active.png");
        playButtonWhiteTexture = new Texture("buttons/enter_name_inactive.png");
        backgroundTexture = new Texture("background/background.png");
        welcomeTexture = new Texture("background/welcome.png");

        // Button objects
        playButton = new Button(playButtonTexture, ENTER_NAME_BUTTON_X, ENTER_NAME_BUTTON_Y, ENTER_NAME_BUTTON_WIDTH, ENTER_NAME_BUTTON_HEIGHT);
        playButtonWhite = new Button(playButtonWhiteTexture, ENTER_NAME_BUTTON_X,ENTER_NAME_BUTTON_Y, ENTER_NAME_BUTTON_WIDTH,ENTER_NAME_BUTTON_HEIGHT);

        // Font
        font = new BitmapFont(Gdx.files.internal("fonts/inscription.fnt"));
        glyphLayout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {

        Gdx.graphics.setTitle("Nickname (" + Gdx.graphics.getFramesPerSecond() + "FPS)");

        batch.begin(); // start

        batch.draw(backgroundTexture, 0, 0, GameClient.WIDTH, GameClient.HEIGHT);
        batch.draw(welcomeTexture, WELCOME_X, WELCOME_Y, WELCOME_WIDTH, WELCOME_HEIGHT);

        writeError(batch);

        // if mouse X-coordinate and Y-coordinate on the button
        if (Gdx.input.getX() > playButton.polygon.getX() && Gdx.input.getX() < playButton.polygon.getX() + ENTER_NAME_BUTTON_WIDTH &&
                GameClient.HEIGHT - Gdx.input.getY() - 15 > playButton.polygon.getY() && GameClient.HEIGHT - Gdx.input.getY() + 20 < playButton.polygon.getY() + ENTER_NAME_BUTTON_HEIGHT) {
            playButton.draw(batch); // draw in color selected button

            // if click - open text input window
            if (Gdx.input.justTouched() && !isWindowOpened) {
                isWindowOpened = true;
                Gdx.input.getTextInput(this, "Enter your name", "", "name");

                if (!gameClient.client.isToServerConnected) gameClient.client.connectToServer();
            }

        } else {
            playButtonWhite.draw(batch); // draw transparent buttons
        }

        // If nickname is entered
        if (nickname != null) {
            if (!nickname.matches("[a-zA-Z\\d]+")) {
                // Check if nickname is not инвалид.
                inscription = "USERNAME CONTAINS INVALID CHARACTERS";
                isWindowOpened = false;
            } else if (nickname.trim().length() < 3 || nickname.trim().length() > 16) {
                // Check if nickname contains 3-16 characters.
                inscription = "USERNAME MUST CONTAIN 3-16 CHARACTERS";
                isWindowOpened = false;
            }

            // Send a request to the server to check if player's nickname is unique.
            else gameClient.client.sendPacketCheckNickname(nickname);
        }

        // If player's nickname is unique -> proceed and show menu screen.
        if (KryoClient.isNicknameUnique) {
            gameClient.setScreen(new MenuScreen(gameClient));

            // When client has connected to the server -> send packet to discover if there are any players on the server already.
            gameClient.client.sendPacketRequestAllPlayersConnected();
        }

        if (!KryoClient.isNicknameUnique) {
            nickname = null;
        }

        batch.end(); //end
    }

    /**
     * Display an error message on the screen if a nickname is entered incorrectly.
     * @param batch batch.
     */
    public void writeError(SpriteBatch batch) {
        glyphLayout.setText(font, inscription);
        font.draw(batch, glyphLayout, GameClient.WIDTH / 2f - glyphLayout.width / 2, 100);
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
        backgroundTexture.dispose();
        welcomeTexture.dispose();
        gameClient.dispose();
    }

    @Override
    public void input(String text) {
        this.nickname = text;
    }

    @Override
    public void canceled() {
        isWindowOpened = false;
    }
}
