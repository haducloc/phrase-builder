package com.appslandia.core.views;

import com.appslandia.phrasebuilder.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.TextView;

public class LeoTextView extends TextView {

	private static final int UNDERLINE_COLOR = Color.parseColor("#C0C0C0");

	private Paint paint;

	public LeoTextView(Context context) {
		super(context);
	}

	public LeoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray props = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, 0, 0);
		initialize(props);
		props.recycle();
	}

	public LeoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray props = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, defStyle, 0);
		initialize(props);
		props.recycle();
	}

	protected void initialize(TypedArray props) {
		Paint p = new Paint();

		p.setColor(props.getColor(R.styleable.LeoWidgets_underlineColor, UNDERLINE_COLOR));
		p.setStrokeWidth(1);

		p.setStyle(Style.STROKE);
		p.setAntiAlias(true);

		this.paint = p;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		float stopY = getHeight() - 1;
		canvas.drawLine(0.0f, stopY, getWidth(), stopY, this.paint);
	}
}
