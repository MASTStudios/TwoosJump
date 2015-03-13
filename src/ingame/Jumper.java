package ingame;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;

import com.maststudios.twosjump.R;

public class Jumper {
	public final static int RUNNING = 1, INAIR = 2, FLIPPING = 3, POWERING = 4,
			DEAD = 5;
	private int balls;
	private float y, vy, ay, yOld;
	private float jumv, powerFactor;
	private long powerUpStart;
	private float r, vr;
	protected int colors, color;
	protected boolean rotating, alive;
	protected World world;
	private Paint paint;
	private boolean pressed;
	private Context context;
	public int state;
	private Bitmap[] running, inair;

	// is true while the jumper is flipping
	protected boolean flipping;

	// CONSTRUCTOR
	public Jumper(World world, int colors, Context context) {
		alive = true;
		ay = -context.getResources().getDimension(R.dimen.jumper_accleration) / 1000;
		y = 800;
		state = INAIR;
		jumv = 0;
		powerFactor = context.getResources().getDimension(R.dimen.jumper_power_factor) / 1000;
		pressed = false;

		this.world = world;
		this.balls = balls;
		this.context = context;
		this.color = 0;
		this.colors = colors;
		running = new Bitmap[3];
		inair = new Bitmap[3];

		running[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumper_normal);
		running[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumper_normal_1);
		running[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumper_normal_2);

		inair[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumper_inair);
		inair[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumper_inair_1);
		inair[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumper_inair_2);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public void move() {
		// starting thread to move
		new Mthread(this) {

			@Override
			public void run() {
				Jumper jumper = (Jumper) callback;
				long lu = 0, dt = 0, cur = 0;
				while (alive) {

					// setting the current time and time difference
					cur = System.currentTimeMillis();
					dt = cur - lu;

					/*
					 * updating the attributes only if the last updated time has
					 * been set ones to the current time.
					 */
					if (lu == 0) {
						lu = cur;
						continue;
					}

					// ---------------------------------------------------------
					// -------------loopy magic happens here--------------------
					// ---------------------------------------------------------

					if (y < -40) {
						jumper.state = DEAD;
					}

					if (jumper.state == INAIR || jumper.state == FLIPPING) {
						// moving
						jumper.yOld = jumper.y;
						jumper.vy = jumper.vy + jumper.ay * dt;
						jumper.y = jumper.y + jumper.vy * dt;

						// checking if landed
						if (((y == world.getPlatformHeight()) || (yOld > world.getPlatformHeight() && y < world.getPlatformHeight())) && jumper.color == world.getPlatformColor()) {
							y = world.getPlatformHeight();
							{
								world.score();
								jumper.state = RUNNING;
								jumper.vy = 0;
							}
						}
					}

					if (jumper.state == RUNNING) {
						if (y != world.getPlatformHeight()) {
							jumper.state = INAIR;
						}
					}

					if (jumper.state == POWERING) {
						if (y != world.getPlatformHeight()) {
							jump();
							flip();
							jumper.state = INAIR;
						} else {
							jumv = context.getResources().getDimension(R.dimen.jumper_min_start_velocity) / 1000 + (System.currentTimeMillis() - powerUpStart) * powerFactor;
							if (jumv >= context.getResources().getDimension(R.dimen.jumper_max_start_velocity) / 1000) {
								/*
								 * Breakjump is when the jumper is powering up
								 * and it reaches the maximum power. In this
								 * case the jumper jumps with the power it has
								 * accumulated. The color is changed as soon as
								 * it jumps and whenever the user taps while the
								 * jumper is in air.
								 */
								jump();
								flip();
								jumper.state = INAIR;
							}
						}
					}

					// ---------------------------------------------------------
					// --------------loppy magic ends
					// here----------------------
					// ---------------------------------------------------------

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// setting the last updated time to the current time
					lu = cur;
				}

			}
		}.start();
	}

	public void press() {
		if(pressed){
			return;
		}
		pressed=true;
		switch (state) {
		case RUNNING:
			powerUpStart = System.currentTimeMillis();
			state = POWERING;
			break;
		case INAIR:
			flip();
			break;
		}
	}

	public void release() {
		pressed=false;
		switch (state) {
		case POWERING:
			jump();
			flip();
			state = INAIR;
			break;
		}
	}

	public void jump() {
		// TODO get value from dimens
		if (jumv < context.getResources().getDimension(R.dimen.jumper_min_start_velocity) / 1000) {
			jumv = (float) context.getResources().getDimension(R.dimen.jumper_min_start_velocity) / 1000;
		}
		this.vy = jumv;
		jumv = 0;
	}

	public void flip() {
		color = color + 1;
		if (color == colors) {
			color = 0;
		}
	}

	public void draw(Canvas canvas, float dx, float dy, float mx, float my) {

		float radius = context.getResources().getDimension(R.dimen.jumper_radius);
		float x = world.jumX * mx + dx;
		float y = this.y * my + dy;

		// drawing shadow

		// drawing jumper
		switch (color) {
		case 0:
			paint.setColor(context.getResources().getColor(R.color.primary_3));
			break;
		case 1:
			paint.setColor(context.getResources().getColor(R.color.secondary_1_3));
			break;
		case 2:
			paint.setColor(context.getResources().getColor(R.color.secondary_2_3));
			break;
		}
		canvas.drawCircle(x, y - radius, radius, paint);

	}
}
