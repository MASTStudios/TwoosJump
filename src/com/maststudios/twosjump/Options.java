package com.maststudios.twosjump;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Options extends Activity{



	Button play;
	TextView h1, h2;
	TextView data_current, data_best;
	LinearLayout scoreLayout, bestLayout;

	// For remembering the highest score and sign-in preferences
	SharedPreferences prefs;
	SharedPreferences.Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		// changing according to open or gameover
		play = (Button) findViewById(R.id.play_button);
		h1 = (TextView) findViewById(R.id.h1);
		data_current = (TextView) findViewById(R.id.data_current);
		data_best =  (TextView) findViewById(R.id.data_best);
		scoreLayout = (LinearLayout)findViewById(R.id.score_layout);
		bestLayout = (LinearLayout)findViewById(R.id.best_layout);

		// initialising shared preference
		prefs = getSharedPreferences("prefs", 0);
		edit = prefs.edit();

	}

	@Override
	protected void onResume() {
		super.onResume();
		int best = prefs.getInt("best", -1);
		int score = getIntent().getIntExtra("score", -1);
		if (getIntent().getIntExtra("score", -1) == -1) {
			play.setText("Play");
			scoreLayout.setVisibility(View.GONE);
		} else {
			h1.setText("Score");
			data_current.setText(score + "");
			if(score>best){
				edit.putInt("best", score);
				edit.commit();
			}
		}
		
		//making best score layout visible
		best = prefs.getInt("best", -1);
		if(best==-1){
			bestLayout.setVisibility(View.GONE);
		}
		else{
			data_best.setText(best +"");
			System.out.println("best in options:"+best);
			bestLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void play(View view) {
		Intent intent = new Intent(this, Game.class);
		startActivity(intent);
		finish();
	}
	
	public void showScoreBoard(View view) {
		Intent intent = new Intent(this, ScoreBoard.class);
		startActivity(intent);
	}
}
