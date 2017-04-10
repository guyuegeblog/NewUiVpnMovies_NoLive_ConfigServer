package de.blinkt.openvpn.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import de.blinkt.openvpn.R;

/**
 * 开关组件
 * @author pc
 *
 */
public class KySwitch extends View implements OnGestureListener {
	public static int OFF = 0;
	public static int ON = 1;
    private GestureDetector mGestureDetector;
    private OnSwitchChangedListener onSwitchChangedListener;
	private Paint paint = new Paint();
	//属性
    private int onBgColor = Color.GREEN;
    private int offBgColor = Color.GRAY;
    private int thumbColor = 0xFFFFFFFF;
    private int thumbMarginLeft;
	private int thumbMarginTop;
	private int thumbMarginRight;
	private int thumbMarginBottom;
	private int paddingLeft;
	private int paddingTop;
	private int paddingRight;
	private int paddingBottom;
	//运行时状态
    private int thumbLeft;//滑块的位置
    private int thumbDownLeft ;//down事件发生时，滑块的位置
    private int value = OFF;//当前状态，开或关
    private boolean scrolling = false;//是否正在拖动s

	public KySwitch(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(this);
		thumbMarginLeft = dip2px(this.getContext(), 2);
		thumbMarginTop = dip2px(this.getContext(), 1);
		thumbMarginRight = dip2px(this.getContext(), 2);
		thumbMarginBottom = dip2px(this.getContext(), 1);
	}
	public KySwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(this);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.kyswitch);

		int onBgColor = (int)a.getColor(R.styleable.kyswitch_onBgColor, 0);
		if(onBgColor == 0)
			onBgColor = a.getResourceId(R.styleable.kyswitch_onBgColor, 0);
		if(onBgColor != 0)
			this.onBgColor = onBgColor;

		int offBgColor = (int)a.getColor(R.styleable.kyswitch_offBgColor, 0);
		if(offBgColor == 0)
			offBgColor = a.getResourceId(R.styleable.kyswitch_offBgColor, 0);
		if(offBgColor != 0)
			this.offBgColor = offBgColor;

		int thumbColor = (int)a.getColor(R.styleable.kyswitch_thumbColor, 0);
		if(thumbColor == 0)
			thumbColor = a.getResourceId(R.styleable.kyswitch_thumbColor, 0);
		if(thumbColor != 0)
			this.thumbColor = thumbColor;

		thumbMarginLeft = (int)a.getDimension(R.styleable.kyswitch_thumbMarginLeft, dip2px(this.getContext(), 2));
		thumbMarginTop = (int)a.getDimension(R.styleable.kyswitch_thumbMarginTop, dip2px(this.getContext(), 1));
		thumbMarginRight = (int)a.getDimension(R.styleable.kyswitch_thumbMarginRight, dip2px(this.getContext(), 2));
		thumbMarginBottom = (int)a.getDimension(R.styleable.kyswitch_thumbMarginBottom, dip2px(this.getContext(), 1));

		paddingLeft = (int)a.getDimension(R.styleable.kyswitch_paddingLeft, 0);
		paddingTop = (int)a.getDimension(R.styleable.kyswitch_paddingTop, 0);
		paddingRight = (int)a.getDimension(R.styleable.kyswitch_paddingRight, 0);
		paddingBottom = (int)a.getDimension(R.styleable.kyswitch_paddingBottom, 0);

	}
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }
	/**
     * 计算组件宽度
     */
    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
        	result = dip2px(this.getContext(), 120);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * 计算组件高度
     */
    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = dip2px(this.getContext(), 50);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		//初始化thumbLeft
//		if(this.thumbLeft == 0){
//			int padding = this.paramsCreator.getPadding(this.getHeight());
//			int thumbRadius = this.getThumbRadius();
//			int minLeft = this.paramsCreator.getPadding(this.getHeight());
//			int maxLeft = this.getWidth() - thumbRadius * 2 - padding;
//			this.thumbLeft = minLeft;
//			if(this.value ==ON){
//				this.thumbLeft = maxLeft;
//			}		
//		}
//		//画背景
//		int bgColor = this.value == ON ? this.onBgColor : this.offBgColor;
//		paint.setColor(bgColor);
//		paint.setAntiAlias(true);
//		RectF rectF = new RectF(0, 0, this.getWidth() , this.getHeight());
//		int radius = this.getHeight()/2; 
//		canvas.drawRoundRect(rectF, radius, radius, paint);
//		//画滑块thumb
//		paint.setColor(thumbColor);
//		int padding = this.paramsCreator.getPadding(this.getHeight());
//		int left = this.thumbLeft;
//		int top = padding;
//		int right = left + (this.getHeight()-padding*2);
//		int bottom = this.getHeight() - padding;
//		RectF oval=new RectF(left, top, right, bottom);
//		canvas.drawArc(oval, 0, 360, false, paint);
		drawBackground(canvas);
		drawThumb(canvas);
	}
	/**
     * 画背景
     */
    private void drawBackground(Canvas canvas){
    	int backgroundColor = getResources().getColor(R.color.chunbai);
		if(this.value == ON){
    		backgroundColor = getResources().getColor(R.color.chuntianlv);
    	}
    	RectF rectF = new RectF();
    	rectF.left = this.paddingLeft;
    	rectF.top = this.paddingTop;
    	rectF.right = this.getWidth() - this.paddingRight;
    	rectF.bottom = this.getHeight() - this.paddingBottom;
    	paint.setAntiAlias(true);
		paint.setColor(backgroundColor);
    	int radius = (this.getHeight() - this.paddingTop - this.paddingBottom)/2;
    	canvas.drawRoundRect(rectF, radius, radius, paint);
    }
    /**
     * 画滑块
     */
    private void drawThumb(Canvas canvas){
    	paint.setColor(this.thumbColor);
    	RectF rectF = new RectF();
    	if(scrolling)
    	    rectF.left = this.thumbLeft;
    	else{
    		rectF.left = getMinThumbLeft();
    		if(this.value ==ON){
    			rectF.left = getMaxThumbLeft();
    		}
    	}
    	rectF.top = this.paddingTop + this.thumbMarginTop;
    	rectF.right = rectF.left + getThumbRadius()*2;
    	rectF.bottom = this.getHeight() - this.paddingBottom - this.thumbMarginBottom;
    	paint.setAntiAlias(true);
    	canvas.drawArc(rectF, 0, 360, false, paint);
    }
    /**
	 * 获得minThumbLeft
	 */
	private int getMinThumbLeft(){
		return this.paddingLeft + this.thumbMarginLeft;
	}
	/**
	 * 获得maxThumbLeft
	 */
	private int getMaxThumbLeft(){
		return this.getWidth() - this.paddingRight - this.thumbMarginRight - getThumbRadius()*2;
	}
	/**
	 * 获得滑块宽度
	 */
	private int getThumbRadius(){
		return (this.getHeight() - this.paddingTop - this.paddingBottom - this.thumbMarginTop - this.thumbMarginBottom)/2;
	}
	/**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
	/**
	 * 触碰事件
	 */
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	try {
    		mGestureDetector.onTouchEvent(event);
    		if(event.getAction() == MotionEvent.ACTION_UP){
    			upEventHandler();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return true;
    }
	///手势监听
	@Override
	public boolean onDown(MotionEvent event) {
		downEventHandler();
		return true;
	}
	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float arg2, float arg3) {
		flingEventHandler((int)event1.getX(), (int)event2.getX());
		return true;
	}
	@Override
	public void onLongPress(MotionEvent event) {
	}
	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float arg2, float arg3) {
		scrollEventHandler((int)event1.getX(), (int)event2.getX());
		return true;
	}
	@Override
	public void onShowPress(MotionEvent event) {
	}
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		clickEventHandler((int)event.getX(), (int)event.getY());
		return true;
	}
	/**
	 * down事件处理
	 */
	private void downEventHandler(){
		this.thumbLeft = getMinThumbLeft();
		if(this.value ==ON){
			this.thumbLeft = getMaxThumbLeft();
		}
		this.thumbDownLeft = this.thumbLeft;
		this.scrolling = true;
	}
	/**
	 * 滑动事件处理
	 */
	private void scrollEventHandler(int x1, int x2){
		this.thumbLeft = this.thumbDownLeft + (x2 - x1);
		int minThumbLeft = getMinThumbLeft();
		int maxThumbLeft = getMaxThumbLeft();
		if(this.thumbLeft < minThumbLeft)
			this.thumbLeft = minThumbLeft;
		if(this.thumbLeft > maxThumbLeft)
			this.thumbLeft = maxThumbLeft;
		this.invalidate();
	}
	/**
	 * 单击事件处理
	 */
	private void clickEventHandler(int x, int y){
		this.thumbLeft = getMaxThumbLeft();
		int minThumbLeft = getMinThumbLeft();
		int thumbContaierWidth = this.getWidth() - this.paddingLeft - this.paddingRight - this.thumbMarginLeft - this.thumbMarginRight;
		if(x <= (minThumbLeft + thumbContaierWidth/2))
			this.thumbLeft = getMinThumbLeft();
	}
	/**
	 * fling事件处理
	 */
	private void flingEventHandler(int x1, int x2){
		if(x1 == x2)
			return ;
		this.thumbLeft = getMaxThumbLeft();
		if(x2 < x1){//向左滑动
			this.thumbLeft = getMinThumbLeft();
		}
	}
	/**
	 * up事件处理
	 */
	private void upEventHandler(){
		this.scrolling = false;
		int minThumbLeft = getMinThumbLeft();
		int thumbContaierWidth = this.getWidth() - this.paddingLeft - this.paddingRight - this.thumbMarginLeft - this.thumbMarginRight;
		int value = ON;
		if(this.thumbLeft <= (minThumbLeft + thumbContaierWidth/2 - getThumbRadius()))
			value = OFF;
		int oldValue = this.value;
		this.setValue(value);
		if(this.onSwitchChangedListener != null && oldValue != value)
			this.onSwitchChangedListener.onSwitchChanged(this, value);
	}
	/**
	 * 设置状态，或值
	 */
	public void setValue(int value){
		if(value != OFF && value != ON)
			return ;
		this.value = value;
		this.invalidate();
	}

	public interface OnSwitchChangedListener {
		public void onSwitchChanged(View view, int value);
	}


    public void setOnSwitchChangedListener(
			OnSwitchChangedListener onSwitchChangedListener) {
		this.onSwitchChangedListener = onSwitchChangedListener;
	}

	public int getOnBgColor() {
		return onBgColor;
	}
	public void setOnBgColor(int onBgColor) {
		this.onBgColor = onBgColor;
	}
	public int getOffBgColor() {
		return offBgColor;
	}
	public void setOffBgColor(int offBgColor) {
		this.offBgColor = offBgColor;
	}
	public int getThumbColor() {
		return thumbColor;
	}
	public void setThumbColor(int thumbColor) {
		this.thumbColor = thumbColor;
	}
	public int getThumbMarginLeft() {
		return thumbMarginLeft;
	}
	public void setThumbMarginLeft(int thumbMarginLeft) {
		this.thumbMarginLeft = thumbMarginLeft;
	}
	public int getThumbMarginTop() {
		return thumbMarginTop;
	}
	public void setThumbMarginTop(int thumbMarginTop) {
		this.thumbMarginTop = thumbMarginTop;
	}
	public int getThumbMarginRight() {
		return thumbMarginRight;
	}
	public void setThumbMarginRight(int thumbMarginRight) {
		this.thumbMarginRight = thumbMarginRight;
	}
	public int getThumbMarginBottom() {
		return thumbMarginBottom;
	}
	public void setThumbMarginBottom(int thumbMarginBottom) {
		this.thumbMarginBottom = thumbMarginBottom;
	}
	public int getPaddingLeft() {
		return paddingLeft;
	}
	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}
	public int getPaddingTop() {
		return paddingTop;
	}
	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}
	public int getPaddingRight() {
		return paddingRight;
	}
	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}
	public int getPaddingBottom() {
		return paddingBottom;
	}
	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

}
