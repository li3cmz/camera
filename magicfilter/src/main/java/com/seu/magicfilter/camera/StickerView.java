package com.seu.magicfilter.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.seu.magicfilter.utils.DisplayUtil;

public class StickerView extends View {

	/*######################*/
	public static void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation,
									 int viewWidth, int viewHeight) {
		// Need mirror for front camera.
		matrix.setScale(mirror ? -1 : 1, 1);
		// This is the value for android.hardware.Camera.setDisplayOrientation.
		matrix.postRotate(displayOrientation);
		// Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
		// UI coordinates range from (0, 0) to (width, height).
		matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
		matrix.postTranslate(viewWidth / 2f, viewHeight / 1.9f);
	}
	/*######################*/


	public static final float MAX_SCALE_SIZE = 3.2f;
	public static final float MIN_SCALE_SIZE = 0.2f;

	private float[] mOriginPoints;
	private float[] mPoints;
	private RectF mOriginContentRect;
	private RectF mContentRect;
	private RectF mViewRect;

	private Camera.Face[] mFaces;
	private Matrix matrix = new Matrix();
	private RectF mRectF = new RectF();
	private float[] mPointF = new float[]{0,0};
	private int cameraPosition = 1;
	private String mName;//贴纸名

	private float mLastPointX, mLastPointY;

	private Bitmap mBitmap;
	private Bitmap mControllerBitmap, mDeleteBitmap;
	//	private Bitmap mReversalHorBitmap, mReversalVerBitmap;// ˮƽ��ת�ʹ�ֱ��תbitmap
	private Matrix mMatrix;
	private Paint mPaint, mBorderPaint;
	private float mControllerWidth, mControllerHeight, mDeleteWidth,
			mDeleteHeight;
	private float mReversalHorWidth, mReversalHorHeight, mReversalVerWidth,
			mReversalVerHeight;
	private boolean mInController, mInMove;
	private boolean mInReversalHorizontal, mInReversalVertical;

	private boolean mDrawController = true;
	// private boolean mCanTouch;
	private float mStickerScaleSize = 1.0f;

	private OnStickerDeleteListener mOnStickerDeleteListener;

	public StickerView(Context context) {
		this(context, null);
	}

	public StickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setFaces(Camera.Face[] faces, int cameraPos) {
		mFaces = faces;
		cameraPosition = cameraPos;
		//刷新ui
		postInvalidate();
	}
	private void init() {

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(4.0f);
		mPaint.setColor(Color.WHITE);

		mBorderPaint = new Paint(mPaint);
		mBorderPaint.setColor(Color.parseColor("#B2ffffff"));
		mBorderPaint.setShadowLayer(DisplayUtil.dip2px(getContext(), 2.0f), 0,
				0, Color.parseColor("#33000000"));

//		mControllerBitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_sticker_control);
//		mControllerWidth = mControllerBitmap.getWidth();
//		mControllerHeight = mControllerBitmap.getHeight();

//		mDeleteBitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_sticker_delete);
//		mDeleteWidth = mDeleteBitmap.getWidth();
//		mDeleteHeight = mDeleteBitmap.getHeight();

//		mReversalHorBitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_sticker_reversal_horizontal);
//		mReversalHorWidth = mReversalHorBitmap.getWidth();
//		mReversalHorHeight = mReversalHorBitmap.getHeight();

//		mReversalVerBitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_sticker_reversal_vertical);
//		mReversalVerWidth = mReversalVerBitmap.getWidth();
//		mReversalVerHeight = mReversalVerBitmap.getHeight();

	}

	//这个函数把贴纸显示出来
	public void setWaterMark(Bitmap bitmap, String picname) {
		mBitmap = bitmap;
		mName = picname;
		mStickerScaleSize = 1.0f;

		setFocusable(true);
		try {

			float px = mBitmap.getWidth();
			float py = mBitmap.getHeight();

			mOriginPoints = new float[] { 0, 0, px, 0, px, py, 0, py, px / 2,
					py / 2 };
			mOriginContentRect = new RectF(0, 0, px, py);
			mPoints = new float[10];
			mContentRect = new RectF();

			mMatrix = new Matrix();
			float transtLeft = ((float) DisplayUtil
					.getDisplayWidthPixels(getContext()) - mBitmap.getWidth()) / 2;
			float transtTop = ((float) DisplayUtil
					.getDisplayWidthPixels(getContext()) - mBitmap.getHeight()) / 2;

			mMatrix.postTranslate(transtLeft, transtTop);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//刷新UI，调用onDraw
		postInvalidate();
	}

	public Matrix getMarkMatrix() {
		return mMatrix;
	}

	@Override
	public void setFocusable(boolean focusable) {
		super.setFocusable(focusable);
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmap == null || mMatrix == null) {
			return;
		}

		canvas.save();
		mMatrix.mapPoints(mPoints, mOriginPoints);

		mMatrix.mapRect(mContentRect, mOriginContentRect);
		/*canvas.drawBitmap(mBitmap, mMatrix, mPaint);
		if (mDrawController && isFocusable()) {
//			canvas.drawLine(mPoints[0], mPoints[1], mPoints[2], mPoints[3],
//					mBorderPaint);
//			canvas.drawLine(mPoints[2], mPoints[3], mPoints[4], mPoints[5],
//					mBorderPaint);
//			canvas.drawLine(mPoints[4], mPoints[5], mPoints[6], mPoints[7],
//					mBorderPaint);
//			canvas.drawLine(mPoints[6], mPoints[7], mPoints[0], mPoints[1],
//					mBorderPaint);
			canvas.drawBitmap(mControllerBitmap, mPoints[4] - mControllerWidth
					/ 2, mPoints[5] - mControllerHeight / 2, mBorderPaint);
			canvas.drawBitmap(mDeleteBitmap, mPoints[0] - mDeleteWidth / 2,
					mPoints[1] - mDeleteHeight / 2, mBorderPaint);
//			canvas.drawBitmap(mReversalHorBitmap, mPoints[2]
//					- mReversalHorWidth / 2, mPoints[3] - mReversalVerHeight
//					/ 2, mBorderPaint);
//			canvas.drawBitmap(mReversalVerBitmap, mPoints[6]
//					- mReversalVerWidth / 2, mPoints[7] - mReversalVerHeight
//					/ 2, mBorderPaint);
		}*/
		canvas.restore();

		//
		if (mFaces == null || mFaces.length <= 0) {
			return;
		}
		if (cameraPosition == 1) {
			prepareMatrix(matrix, true, 90, getWidth(), getHeight());
		}else{
			prepareMatrix(matrix, false, 90, getWidth(), getHeight());
		}

		canvas.save();
		matrix.postRotate(0);
		canvas.rotate(-0);
		for (int i = 0; i < mFaces.length; i++) {
			mRectF.set(mFaces[i].rect);
			matrix.mapRect(mRectF);
			mPaint.setColor(Color.RED);
//			canvas.drawRect(mRectF, mPaint);

			float w = mRectF.width();
			Matrix mat = new Matrix();
			mat.postScale(1.3f*w/mBitmap.getWidth(),1.3f*w/mBitmap.getWidth());
			mat.postTranslate(mRectF.left-(1.3f*w/8),
					mRectF.top-(mBitmap.getHeight()*1.3f*w/(mBitmap.getWidth()*2)));
			if (mName.equals("rabit"))
				mat.postTranslate(0,
						mRectF.top-3*(mBitmap.getHeight()*1.3f*w/(mBitmap.getWidth()*2)));
			if (mName.equals("hat"))
				mat.postTranslate(mRectF.left/3,
						mRectF.top-3*(mBitmap.getHeight()*1.3f*w/(mBitmap.getWidth()*2)));
			canvas.drawBitmap(mBitmap, mat, mPaint);

//			if (mFaces[i].leftEye != null){
//				mPaint.setColor(Color.GREEN);
//				mPointF[0] = mFaces[i].leftEye.x;
//				mPointF[1] = mFaces[i].leftEye.y;
//				matrix.mapPoints(mPointF);
//				canvas.drawPoints(mPointF, mPaint);
//			}
//			if (mFaces[i].rightEye != null){
//				mPaint.setColor(Color.GREEN);
//				mPointF[0] = mFaces[i].rightEye.x;
//				mPointF[1] = mFaces[i].rightEye.y;
//				matrix.mapPoints(mPointF);
//				canvas.drawPoints(mPointF, mPaint);
//			}
//			if (mFaces[i].mouth != null){
//				mPaint.setColor(Color.GREEN);
//				mPointF[0] = mFaces[i].mouth.x;
//				mPointF[1] = mFaces[i].mouth.y;
//				matrix.mapPoints(mPointF);
//				canvas.drawPoints(mPointF, mPaint);
//			}

		}
		canvas.restore();

	}

	public Bitmap getBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		mDrawController = false;
		draw(canvas);
		mDrawController = true;
		canvas.save();
		return bitmap;
	}

	public void setShowDrawController(boolean show) {
		mDrawController = show;
	}

	private boolean isInController(float x, float y) {
		int position = 4;
		// while (position < 8) {
		float rx = mPoints[position];
		float ry = mPoints[position + 1];
		RectF rectF = new RectF(rx - mControllerWidth / 2, ry
				- mControllerHeight / 2, rx + mControllerWidth / 2, ry
				+ mControllerHeight / 2);
		if (rectF.contains(x, y)) {
			return true;
		}
		// position += 2;
		// }
		return false;

	}

	private boolean isInDelete(float x, float y) {
		int position = 0;
		// while (position < 8) {
		float rx = mPoints[position];
		float ry = mPoints[position + 1];
		RectF rectF = new RectF(rx - mDeleteWidth / 2, ry - mDeleteHeight / 2,
				rx + mDeleteWidth / 2, ry + mDeleteHeight / 2);
		if (rectF.contains(x, y)) {
			return true;
		}
		// position += 2;
		// }
		return false;

	}

//	// �жϵ�������Ƿ���ˮƽ��ת��ť������
//	private boolean isInReversalHorizontal(float x, float y) {
//		int position = 2;
//		float rx = mPoints[position];
//		float ry = mPoints[position + 1];
//
//		RectF rectF = new RectF(rx - mReversalHorWidth / 2, ry
//				- mReversalHorHeight / 2, rx + mReversalHorWidth / 2, ry
//				+ mReversalHorHeight / 2);
//		if (rectF.contains(x, y))
//			return true;
//
//		return false;
//
//	}
//
//	// �жϵ�������Ƿ��ڴ�ֱ��ת��ť������
//	private boolean isInReversalVertical(float x, float y) {
//		int position = 6;
//		float rx = mPoints[position];
//		float ry = mPoints[position + 1];
//
//		RectF rectF = new RectF(rx - mReversalVerWidth / 2, ry
//				- mReversalVerHeight / 2, rx + mReversalVerWidth / 2, ry
//				+ mReversalVerHeight / 2);
//		if (rectF.contains(x, y))
//			return true;
//		return false;
//	}

	private boolean mInDelete = false;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (!isFocusable()) {
			return super.dispatchTouchEvent(event);
		}
		if (mViewRect == null) {
			mViewRect = new RectF(0f, 0f, getMeasuredWidth(),
					getMeasuredHeight());
		}
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (isInController(x, y)) {
					mInController = true;
					mLastPointY = y;
					mLastPointX = x;
					break;
				}

				if (isInDelete(x, y)) {
					mInDelete = true;
					break;
				}

//			if (isInReversalHorizontal(x, y)) {
//				mInReversalHorizontal = true;
//				break;
//			}
//
//			if (isInReversalVertical(x, y)) {
//				mInReversalVertical = true;
//				break;
//			}

				if (mContentRect.contains(x, y)) {
					mLastPointY = y;
					mLastPointX = x;
					mInMove = true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isInDelete(x, y) && mInDelete) {
					doDeleteSticker();
					break;
				}
//			if (isInReversalHorizontal(x, y) && mInReversalHorizontal) {
//				doReversalHorizontal();
//				break;
//			}
//			if (isInReversalVertical(x, y) && mInReversalVertical) {
//				doReversalVertical();
//				break;
//			}
			case MotionEvent.ACTION_CANCEL:
				mLastPointX = 0;
				mLastPointY = 0;
				mInController = false;
				mInMove = false;
				mInDelete = false;
				mInReversalHorizontal = false;
				mInReversalVertical = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mInController) {
					mMatrix.postRotate(rotation(event), mPoints[8], mPoints[9]);
					float nowLenght = caculateLength(mPoints[0], mPoints[1]);
					float touchLenght = caculateLength(event.getX(), event.getY());
					if ((float) Math.sqrt((nowLenght - touchLenght)
							* (nowLenght - touchLenght)) > 0.0f) {
						float scale = touchLenght / nowLenght;
						float nowsc = mStickerScaleSize * scale;
						if (nowsc >= MIN_SCALE_SIZE && nowsc <= MAX_SCALE_SIZE) {
							mMatrix.postScale(scale, scale, mPoints[8], mPoints[9]);
							mStickerScaleSize = nowsc;
						}
					}

					invalidate();
					mLastPointX = x;
					mLastPointY = y;
					break;

				}

				if (mInMove == true) { // �϶��Ĳ���
					float cX = x - mLastPointX;
					float cY = y - mLastPointY;
					mInController = false;
					// Log.i("MATRIX_OK", "ma_jiaodu:" + a(cX, cY));

					if ((float) Math.sqrt(cX * cX + cY * cY) > 2.0f
							&& canStickerMove(cX, cY)) {
						// Log.i("MATRIX_OK", "is true to move");
						mMatrix.postTranslate(cX, cY);
						postInvalidate();
						mLastPointX = x;
						mLastPointY = y;
					}
					break;
				}

				return true;

		}
		return true;
	}

	public void doDeleteSticker() {
		setWaterMark(null,null);
		if (mOnStickerDeleteListener != null) {
			mOnStickerDeleteListener.onDelete();
		}
	}

	// ͼƬˮƽ��ת
	private void doReversalHorizontal() {
		float[] floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
		Matrix tmpMatrix = new Matrix();
		tmpMatrix.setValues(floats);
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), tmpMatrix, true);
		invalidate();
		mInReversalHorizontal = false;
	}

	// ͼƬ��ֱ��ת
	private void doReversalVertical() {
		float[] floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };
		Matrix tmpMatrix = new Matrix();
		tmpMatrix.setValues(floats);
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), tmpMatrix, true);
		invalidate();
		mInReversalVertical = false;
	}

	private boolean canStickerMove(float cx, float cy) {
		float px = cx + mPoints[8];
		float py = cy + mPoints[9];
		if (mViewRect.contains(px, py)) {
			return true;
		} else {
			return false;
		}
	}

	private float caculateLength(float x, float y) {
		float ex = x - mPoints[8];
		float ey = y - mPoints[9];
		return (float) Math.sqrt(ex * ex + ey * ey);
	}

	private float rotation(MotionEvent event) {
		float originDegree = calculateDegree(mLastPointX, mLastPointY);
		float nowDegree = calculateDegree(event.getX(), event.getY());
		return nowDegree - originDegree;
	}

	private float calculateDegree(float x, float y) {
		double delta_x = x - mPoints[8];
		double delta_y = y - mPoints[9];
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	public interface OnStickerDeleteListener {
		public void onDelete();
	}

	public void setOnStickerDeleteListener(OnStickerDeleteListener listener) {
		mOnStickerDeleteListener = listener;
	}
}
