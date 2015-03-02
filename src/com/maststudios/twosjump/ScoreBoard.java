package com.maststudios.twosjump;

import googleutils.GameAPIUtils;
import googleutils.GameAPIUtils.BooleanChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.games.Player;

public class ScoreBoard extends Activity {

	// For remembering the highest score and sign-in preferences
	SharedPreferences prefs;
	SharedPreferences.Editor edit;
	GameAPIUtils utils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_board);

		// initialising shared preference
		prefs = getSharedPreferences("prefs", 0);
		edit = prefs.edit();

		utils = new GameAPIUtils(this);
		
		// adding onclicklistner to signin button
		findViewById(R.id.button_sign_in).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				System.out.println("Sign in initiated");
				edit.putBoolean("signIn", true);
				edit.commit();
				utils.connect();
			}
		});

		// adding onclicklistner to signout button
		findViewById(R.id.button_sign_out).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				edit.putBoolean("signIn", false);
				edit.commit();
				utils.signOut();
				utils.disconnect();
				showSignInBar();
			}
		});
		
		utils.setBooleanChangeListener(new BooleanChangeListener() {
			
			@Override
			public void onBooleanChanged(boolean arg0) {
				if(arg0){
					showSignInBar();
				}
				else{
					showSignOutBar();
				}
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (prefs.getBoolean("signIn", false)) {
			utils.connect();
		}
		int best = prefs.getInt("best", -1);
		System.out.println(best);
		utils.submitScore(best);
	}
	// Shows the "sign in" bar (explanation and button).
	private void showSignInBar() {
		findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
		findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
		ImageView vw = (ImageView) findViewById(R.id.avatar);
		vw.setImageBitmap(null);
		TextView name = (TextView) findViewById(R.id.playerName);
		name.setText("");
	}

	// Shows the "sign out" bar (explanation and button).
	private void showSignOutBar() {
		findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
		findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);

		Player player = utils.getCurrentPlayer();
		String url = player.getIconImageUrl();
		TextView name = (TextView) findViewById(R.id.playerName);
		name.setText(player.getDisplayName());
		if (url != null) {
			ImageView vw = (ImageView) findViewById(R.id.avatar);

			// load the image in the background.
			new DownloadImageTask(vw).execute(url);
		}
	}

	public void showScoreBoard(View view) {
		utils.showScoreBoard();
	}

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		@Override
		protected Bitmap doInBackground(String... strings) {
			Bitmap mIcon11 = null;
			String url = strings[0];
			try {
				InputStream in = new URL(url).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (IOException e) {
				Log.e("Some Error", e.getMessage());
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
			bmImage.setVisibility(View.VISIBLE);
		}
	}
}
