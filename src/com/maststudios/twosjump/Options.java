package com.maststudios.twosjump;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Options extends Activity
{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		
		// changing according to open or gameover
		Button play = (Button) findViewById(R.id.play_button);
		TextView h1 = (TextView) findViewById(R.id.h1);
		TextView data = (TextView) findViewById(R.id.data);
		int score = getIntent().getIntExtra("score", -1);
		if (getIntent().getIntExtra("score", -1) == -1) {
			play.setText("Play");
			h1.setVisibility(View.GONE);
		} else {
			h1.setText("Score");
			data.setText(score + "");
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

}
