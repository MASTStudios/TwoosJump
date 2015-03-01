package ingame;

import java.util.Random;

import com.maststudios.twosjump.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

public class Platform {

	// STATIC
	public static float maxH, minH, maxL, minL, minDiffInc, maxDiffInc,
			minDiffDec, maxDiffDec, minAbsDiff;
	private static Bitmap platformLeft, platformRight, platformCenter;
	public static int colors;
	protected boolean touched;//used for scoring

	// initializing static variables
	public static void init(Context context, World world,int colors) {
		// setting platform static members
		Platform.colors = colors;
		Platform.maxH = context.getResources().getDimension(R.dimen.platform_max_height);
		Platform.minH = context.getResources().getDimension(R.dimen.sw_platform_top_height);
		Platform.minL = context.getResources().getDimension(R.dimen.platform_min_length);
		Platform.maxL = context.getResources().getDimension(R.dimen.platform_max_length);
		Platform.minDiffInc = context.getResources().getDimension(R.dimen.platform_min_inc_diff);
		Platform.maxDiffInc = context.getResources().getDimension(R.dimen.platform_max_inc_diff);
		Platform.minDiffDec = context.getResources().getDimension(R.dimen.platform_min_dec_diff);
		Platform.maxDiffDec = context.getResources().getDimension(R.dimen.platform_max_dec_diff);
		Platform.minAbsDiff = context.getResources().getDimension(R.dimen.platform_absolute_min_diff);
		platformLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.platform_base_tile_left);
		platformRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.platform_base_tile_right);
		platformCenter = BitmapFactory.decodeResource(context.getResources(), R.drawable.platform_base_tile_center);
	}

	// NON STATIC
	protected float start, height, length;
	protected int color;
	private Paint paint;
	private Context context;

	public Platform(float start, float length, float height, int color, Context context) {
		this.start = start;
		this.height = height;
		this.length = length;
		this.color = color;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.context = context;
		touched=false;
	}

	public Platform(float start, float lastHeight, Context context) {
		this.context = context;
		Random random = new Random(System.currentTimeMillis());
		color = random.nextInt(colors);
		this.start = start;
		touched=false;
		
		boolean flag = true;
		while (flag) {
			flag = false;
			float up = lastHeight + random.nextFloat() * (maxDiffInc - minDiffInc) + minDiffInc;
			float down = lastHeight - random.nextFloat() * (maxDiffDec - minDiffDec) - minDiffDec;

			if (up > maxH) {
				if (down > minH && down < maxH) {
					this.height = down;
				}
				if (down < minH) {
					if (up - maxH > minH - down) {
						if (lastHeight - minH < minAbsDiff) {
							flag = true;
						}
						this.height = minH;
					} else {
						if (maxH - lastHeight < minAbsDiff) {
							flag = true;
						}
						this.height = maxH;
					}
				}
			} else {
				if (down < minH) {
					this.height = up;
				} else {
					if (up - lastHeight < lastHeight - down) {
						this.height = down;
					} else {
						this.height = up;
					}
				}
			}
		}
		this.length = random.nextFloat() * (maxL - minL) + minL;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public void draw(Canvas canvas, float dx, float dy, float mx, float my) {
		float start = this.start * mx + dx;
		float length = this.length * mx;
		float height = this.height * my + dy;
		// setting other drawing properties
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(context.getResources().getDimension(R.dimen.sw_platform_strokewidth));

		// drawing base
		// float tileHeight = platformCenter.getHeight();
		// float tileWidth = platformCenter.getWidth();
		//
		// float drawWidth = length - 2 *
		// context.getResources().getDimension(R.dimen.sw_platform_base_margin);
		// float drawHeight = height;
		//
		// int tileCols = (int) (drawWidth / tileWidth + 0.5);
		// int tileRows = (int) (this.height / tileHeight) + 1;
		// float margin = (length - tileWidth * tileCols) / 2;
		// Bitmap b;
		//
		// for (int i = 0; i < tileRows; i++) {
		// for (int j = 0; j < tileCols; j++) {
		// if (j == 0) {
		// b = platformLeft;
		// } else if (j == tileCols - 1) {
		// b = platformRight;
		// } else {
		// b = platformCenter;
		// }
		// canvas.drawBitmap(b, start + margin + tileHeight * j,
		// (this.height-tileHeight * i)*my+dy, paint);
		// }
		// }

		// paint.setStyle(Style.FILL);
		// paint.setColor(context.getResources().getColor(R.color.platform_base));
		// canvas.drawRect(start +
		// context.getResources().getDimension(R.dimen.sw_platform_base_margin),
		// height, start + length -
		// context.getResources().getDimension(R.dimen.sw_platform_base_margin),
		// dy, paint);
		//
		// paint.setStyle(Style.STROKE);
		// paint.setColor(context.getResources().getColor(R.color.platform_base_border));
		// canvas.drawRect(start +
		// context.getResources().getDimension(R.dimen.sw_platform_base_margin),
		// height, start + length -
		// context.getResources().getDimension(R.dimen.sw_platform_base_margin),
		// dy, paint);

		// drawing top
		// setting color
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
		canvas.drawRect(start, height, start + length, height + context.getResources().getDimension(R.dimen.sw_platform_top_height), paint);
	}
}
