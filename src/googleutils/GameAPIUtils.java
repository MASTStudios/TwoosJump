package googleutils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.maststudios.twosjump.R;

public class GameAPIUtils implements ConnectionCallbacks, OnConnectionFailedListener {

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

	public boolean showSignInBar = true;
	private Context context;
	BooleanChangeListener booleanChangeListener;

	public GameAPIUtils(Context context) {
		this.context = context;
		mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).addApi(Games.API).addScope(Games.SCOPE_GAMES).build();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		boolean temp = showSignInBar;
		showSignInBar = false;
		if (showSignInBar != temp && this.booleanChangeListener != null) {
			this.booleanChangeListener.onBooleanChanged(showSignInBar);
		}
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
			mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure((Activity) context, mGoogleApiClient, connectionResult, RC_SIGN_IN, "Sign in with error");
		}
		boolean temp = showSignInBar;
		showSignInBar = true;
		if (showSignInBar != temp && this.booleanChangeListener != null) {
			this.booleanChangeListener.onBooleanChanged(showSignInBar);
		}
	}

	public void connect() {
		mGoogleApiClient.connect();
	}

	public void disconnect() {
		mGoogleApiClient.disconnect();
	}

	public void signOut() {
		Games.signOut(mGoogleApiClient);
	}

	public void submitScore(int score) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			Games.Leaderboards.submitScore(mGoogleApiClient, context.getResources().getString(R.string.leaderboard_global_leaderboard), score);
		}
	}

	public Player getCurrentPlayer() {
		return Games.Players.getCurrentPlayer(mGoogleApiClient);
	}

	public void showScoreBoard() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			((Activity) context).startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, context.getResources().getString(R.string.leaderboard_global_leaderboard)), 0);
		}
	}

	public interface BooleanChangeListener {
		public void onBooleanChanged(boolean arg0);
	}

	public void setBooleanChangeListener(BooleanChangeListener booleanChangeListener) {
		this.booleanChangeListener = booleanChangeListener;
	}
}
