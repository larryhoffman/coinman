package com.larryhoffman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.5f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRec;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRect = new ArrayList<Rectangle>();
 	Texture coin;
	int coinCount = 0;


	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRect = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount = 0;

	ArrayList<Integer> bcXs = new ArrayList<Integer>();
	ArrayList<Integer> bcYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bcRect = new ArrayList<Rectangle>();
	int bcCount = 0;
	Texture bonusCoin;


	int score = 0;

	BitmapFont font;

	int gameState = 0;

	Texture dizzy;

	Random random;


	private Music music;
	private Music ballonPop;
	private Music jump;
	private Music pickup;
	private Music bonusCoinMusic;



	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		man = new Texture[6];
		man[0] = new Texture("person2/frame-1.png");
		man[1] = new Texture("person2/frame-2.png");
		man[2] = new Texture("person2/frame-3.png");
		man[3] = new Texture("person2/frame-4.png");
        man[4] = new Texture("person2/frame-5.png");
        man[5] = new Texture("person2/frame-6.png");




        manY = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");

		//dizzy = new Texture("dizzy-1.png");
		dizzy = new Texture("person2/frame-got-hit.png");

		bonusCoin = new Texture("bonus_coin.png");


		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		random = new Random();


		music = Gdx.audio.newMusic(Gdx.files.internal("HonkyTonkVillain.mp3"));
		music.setLooping(true);
		music.setVolume(0.1f);
		music.play();


		ballonPop = Gdx.audio.newMusic(Gdx.files.internal("nuke.mp3"));
		jump = Gdx.audio.newMusic(Gdx.files.internal("jump_01.wav"));
		pickup = Gdx.audio.newMusic(Gdx.files.internal("pop.mp3"));

		bonusCoinMusic  = Gdx.audio.newMusic(Gdx.files.internal("idg.mp3"));

	}


	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}


	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}


	public void makeBonusCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bcYs.add((int)height);
		bcXs.add(Gdx.graphics.getWidth());
	}





	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0 , Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

			music.setVolume(0.1f);

			// Game is live

			// Bonus Coin
			if (bcCount < 550) {
				bcCount++;
			} else {
				bcCount = 0;
				makeBonusCoin();
			}

			// Display bonus coins
			bcRect.clear();
			for(int i=0; i< bcXs.size(); i++){
				batch.draw(bonusCoin,bcXs.get(i),bcYs.get(i));
				bcXs.set(i,bcXs.get(i)-8);
				bcRect.add(new Rectangle(bcXs.get(i), bcYs.get(i), bonusCoin.getWidth(), bonusCoin.getHeight()));
			}



			// Bomb
			if (bombCount < 257) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			// Display coins
			bombRect.clear();
			for(int i=0; i< bombXs.size(); i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-8);
				bombRect.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}


			// Coin
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			// Display coins
			coinRect.clear();
			for(int i=0; i< coinXs.size(); i++){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-4);
				coinRect.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;

				jump.setLooping(false);
				jump.setVolume(0.1f);
				jump.play();
			}


			if (pause < 6) {
				pause++;
			} else {

				pause = 0;

				if (manState < 3) {
					manState++;
				} else
					manState = 0;

			}


			velocity += gravity;
			manY -= velocity;


			if (manY  <= 0) {
				manY = 0;
			}

		} else if  (gameState == 0) {

			// Waiting to start


			if (Gdx.input.justTouched()) {
				gameState = 1;
				//music.setVolume(0.1f);

			}


		} else if (gameState == 2) {

			//Game Over

			music.setVolume(0f);

			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity=0;
				coinXs.clear();
				coinYs.clear();
				coinRect.clear();
				coinCount=0;

				bombXs.clear();
				bombYs.clear();
				bombRect.clear();
				bombCount=0;


				bcXs.clear();
				bcYs.clear();
				bcRect.clear();
				bcCount=0;


			}

		}


		if (gameState == 2) {
			batch.draw(dizzy,  Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY );
		} else {
			batch.draw(man[manState],  Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY );
		}


		manRec = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY, man[manState].getWidth(), man[manState].getHeight());

		for (int i=0; i<coinRect.size(); i++) {
			if (Intersector.overlaps(manRec, coinRect.get(i))) {
				Gdx.app.log("Coin!", "Collision");
				score++;

				coinRect.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);


				pickup.setLooping(false);
				pickup.setVolume(0.9f);
				pickup.play();

				break;
			}
		}



		for (int i=0; i<bcRect.size(); i++) {
			if (Intersector.overlaps(manRec, bcRect.get(i))) {
				Gdx.app.log("Coin!", "Collision");
				score = score + 5;

				bcRect.remove(i);
				bcXs.remove(i);
				bcYs.remove(i);

				bonusCoinMusic.setLooping(false);
				bonusCoinMusic.setVolume(0.1f);
				bonusCoinMusic.play();


				break;
			}
		}


		for (int i=0; i<bombRect.size(); i++) {
			if (Intersector.overlaps(manRec, bombRect.get(i))) {
				Gdx.app.log("Bomb!", "Collision");
				gameState = 2;


				bombRect.remove(i);
				bombXs.remove(i);
				bombYs.remove(i);

				ballonPop.setLooping(false);
				ballonPop.setVolume(0.2f);
				ballonPop.play();
			}
		}

		font.draw(batch, String.valueOf(score),100, 200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		music.dispose();
	}
}
