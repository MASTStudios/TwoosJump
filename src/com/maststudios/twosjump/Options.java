package com.maststudios.twosjump;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

public class Options extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;

	// Client used to interact with Google APIs.
	private GoogleApiClient mGoogleApiClient;

	// Are we currently resolving a connection failure?
	private boolean mResolvingConnectionFailure = false;

	// Has the user clicked the sign-in button?
	private boolean mSignInClicked = false;

	// Set to true to automatically start the sign in flow when the Activity
	// starts.
	// Set to false to require the user to click the button in order to sign in.
	private boolean mAutoStartSignInFlow = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
        .build();


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		//adding onclicklistner to signin button
		findViewById(R.id.button_sign_in).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				System.out.println("Sign in initiated");
				mSignInClicked = true;
				mGoogleApiClient.connect();
			}
		});
		
		//adding onclicklistner to signout button
				findViewById(R.id.button_sign_out).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						mSignInClicked = false;
						Games.signOut(mGoogleApiClient);
						mGoogleApiClient.disconnect();
						showSignInBar();
					}
		});
		
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
			if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
				Games.Leaderboards.submitScore(mGoogleApiClient, getResources().getString(R.string.leaderboard_global_leaderboard), score);
			}
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

	@Override
	public void onConnected(Bundle connectionHint) {
		showSignOutBar();
		System.out.println("Connected");
	}

	@Override
	public void onConnectionSuspended(int i) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		if (mResolvingConnectionFailure) {
			return;
		}

		if (mSignInClicked || mAutoStartSignInFlow) {
			mAutoStartSignInFlow = false;
			mSignInClicked = false;
			 mResolvingConnectionFailure = BaseGameUtils
	                    .resolveConnectionFailure(this, mGoogleApiClient,
	                            connectionResult, RC_SIGN_IN, "Sign in with error");
		}
		showSignInBar();
	}


	public void showScoreBoard(View view) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getResources().getString(R.string.leaderboard_global_leaderboard)), 0);
		}
	}

	// Shows the "sign in" bar (explanation and button).
	private void showSignInBar() {
		findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
		findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
		ImageView vw = (ImageView) findViewById(R.id.avatar);
		vw.setImageBitmap(null);
		TextView name = (TextView) findViewById(R.id.playerName);
		name.setText("");
		TextView email = (TextView) findViewById((R.id.playerEmail));
		email.setText("");

	}

	// Shows the "sign out" bar (explanation and button).
	private void showSignOutBar() {
		findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
		findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);

		Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);
		String url = player.getIconImageUrl();
		TextView name = (TextView) findViewById(R.id.playerName);
		name.setText(player.getDisplayName());
		if (url != null) {
			ImageView vw = (ImageView) findViewById(R.id.avatar);

			// load the image in the background.
			new DownloadImageTask(vw).execute(url);
		}
		String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
		TextView emailView = (TextView) findViewById((R.id.playerEmail));
		emailView.setText(email);
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
