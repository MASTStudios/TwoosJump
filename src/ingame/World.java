package ingame;

import java.util.LinkedList;
import java.util.List;

import com.maststudios.twosjump.Game;
import com.maststudios.twosjump.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;

public class World extends SurfaceView implements OnTouchListener, SurfaceHolder.Callback {
	protected List<Platform> platforms;
	protected Jumper jumper;
	protected Context context;
	protected float jumX;
	protected float width, height;
	protected float vx;
	private Game callback;
	private Paint paint;
	private int score, count;

	private float dx, dy, mx, my;

	public World(Context context, Game callback) {
		super(context);
		setOnTouchListener(this);
		getHolder().addCallback(this);
		count = 2;
		this.context = context;
		this.callback = callback;
		platforms = new LinkedList<Platform>();
		jumper = new Jumper(this, count, context);
		Platform.init(context, this, count);
		// TODO get this from dimens
		jumX = context.getResources().getDimension(R.dimen.jumX);
		// TODO define speed x
		vx = context.getResources().getDimension(R.dimen.x_speed);
		paint=new Paint();
	}

	public float getPlatformHeight() {
		float temp = platforms.get(0).start;
		int i;
		for (i = 0; temp < jumX; i++) {
			temp += platforms.get(i).length;
		}
		return platforms.get(i - 1).height;
	}

	public float getPlatformColor() {
		float temp = platforms.get(0).start;
		int i;
		for (i = 0; temp < jumX; i++) {
			temp += platforms.get(i).length;
		}
		return platforms.get(i - 1).color;
	}

	public void start() {

		// adding initial platform
		platforms.add(new Platform(0, (float) (width * 1.5), context.getResources().getDimension(R.dimen.platform_min_height), 0, context));

		// starting jumper motion
		jumper.move();

		// starting the life thread
		new Mthread(this) {
			@Override
			public void run() {

				World world = (World) callback;
				long lu = 0, dt = 0, cur = 0;

				// get a limit for this PAUSE/STOP
				while (true) {
					// setting the current time and time difference
					cur = System.currentTimeMillis();
					dt = cur - lu;

					/*
					 * updating the attributes only if the last updated time has
					 * been set ones to the current time.
					 */

					// ---------------------------------------------------------
					// -------------loopy magic happens here--------------------
					// ---------------------------------------------------------
					if (lu != 0) {

						// moving platforms
						for (Platform p : world.platforms) {
							p.start = p.start - vx * dt;
						}

						// adding new platform if required
						if (world.getPlatformEnd() < world.width + 100) {
							world.platforms.add(new Platform(world.getPlatformEnd(), platforms.get(platforms.size() - 1).height, world.context));
						}

						// removing platforms if out of view
						if (world.platforms.get(0).start + world.platforms.get(0).length < 0) {
							world.platforms.remove(0);
						}

						// drawing things
						world.draw();

						if (jumper.state == jumper.DEAD) {
							world.gameover();
							break;
						}
					}
					// ---------------------------------------------------------
					// --------------loppy magic ends here----------------------
					// ---------------------------------------------------------

					// sleeping for some time
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

	public float getPlatformEnd() {
		float end = platforms.get(0).start;
		for (Platform p : platforms) {
			end += p.length;
		}
		return end;
	}

	public void score() {

		float temp = platforms.get(0).start;
		int i;
		for (i = 0; temp < jumX; i++) {
			temp += platforms.get(i).length;
		}
		if (!platforms.get(i).touched) {
			score++;
		}
		platforms.get(i).touched = true;
	}

	public void draw() {
		SurfaceHolder holder = getHolder();
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(context.getResources().getColor(R.color.background));

		for (Platform p : platforms) {
			p.draw(canvas, dx, dy, mx, my);
		}

		jumper.draw(canvas, dx, dy, mx, my);

		// displaying score
		paint.setColor(context.getResources().getColor(R.color.foreground));
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(context.getResources().getDimension(R.dimen.score_text_size));
		canvas.drawText(score+"", width/2, context.getResources().getDimension(R.dimen.activity_vertical_margin)+context.getResources().getDimension(R.dimen.score_text_size), paint);
		holder.unlockCanvasAndPost(canvas);
	}

	public void gameover() {
		callback.gameover(score);
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		dx = 0;
		dy = height;
		mx = 1;
		my = -1;
		setMeasuredDimension((int) this.width, (int) this.height);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (arg1.getAction() == 1) {
			jumper.release();
		} else {
			jumper.press();
		}
		return true;
	}
}
