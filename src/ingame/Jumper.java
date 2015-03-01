package ingame;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

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
	private boolean breakJump;
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
		breakJump = false;

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
								breakJump = false;
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
						}
						jumv = context.getResources().getDimension(R.dimen.jumper_min_start_velocity) / 1000 + (System.currentTimeMillis() - powerUpStart) * powerFactor;
						if (jumv >= context.getResources().getDimension(R.dimen.jumper_max_start_velocity) / 1000) {
							breakJump = true;
							jump();
							flip();
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
		if (state == RUNNING) {
			powerUpStart = System.currentTimeMillis();
			state = POWERING;
		}
	}

	public void release() {
		if (state == POWERING) {
			jump();
		}
		if (!breakJump) {
			flip();
		}
		breakJump = false;
	}

	public void jump() {
		// TODO get value from dimens
		if (jumv < context.getResources().getDimension(R.dimen.jumper_min_start_velocity) / 1000) {
			jumv = (float) context.getResources().getDimension(R.dimen.jumper_min_start_velocity) / 1000;
		}
		this.vy = jumv;
		jumv = 0;
		state = INAIR;
	}

	public void flip() {
		color = color + 1;
		if (color == colors) {
			color = 0;
		}
	}

	public void draw(Canvas canvas, float dx, float dy, float mx, float my) {
		switch (color) {
		case 0:
			paint.setColor(context.getResources().getColor(R.color.A));
			break;
		case 1:
			paint.setColor(context.getResources().getColor(R.color.B));
			break;
		case 2:
			paint.setColor(context.getResources().getColor(R.color.C));
			break;
		}
		canvas.drawCircle(world.jumX, y * my + dy - context.getResources().getDimension(R.dimen.jumper_radius), context.getResources().getDimension(R.dimen.jumper_radius), paint);
	}

}
