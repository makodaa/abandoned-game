package com.teammerge.abandoned;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;
import com.teammerge.abandoned.screens.MainMenuScreen;

/*
* AbandonedGame Runner Class
*
* */
public class AbandonedGame extends Game {

	// TODO: declare viewport width, height
	public SpriteBatch batch;

	public MainMenuScreen screen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		VisUI.load();
		setScreen(screen = new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		screen.dispose();
	}
}
