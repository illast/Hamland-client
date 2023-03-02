package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.mygdx.game.client.KryoClient;
import com.mygdx.game.screens.NicknameScreen;

public class GameClient extends Game {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;

	// Create new client object.
	public KryoClient client = new KryoClient();

	// Music and sounds.
	public static Music music;
	public static Music soundBulletShot;
	public static Music soundDamageTaken;
	public static Music soundExplosion;
	public static Music soundZap;
	public static Music soundHeal;
	public static Music soundAmmo;
	public static Music soundEmptyShot;
	public static Music soundReload;

	@Override
	public void create() {
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music_main.mp3"));
		soundBulletShot = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_shot.wav"));
		soundDamageTaken = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_damage_taken.wav"));
		soundExplosion = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_explosion.wav"));
		soundZap = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_zap.wav"));
		soundHeal = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_heal.wav"));
		soundAmmo = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_ammo.mp3"));
		soundEmptyShot = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_empty_shot.wav"));
		soundReload = Gdx.audio.newMusic(Gdx.files.internal("sounds/sound_reload.mp3"));

		Screen menuScreen = new NicknameScreen(this);
		setScreen(menuScreen);
	}
}
