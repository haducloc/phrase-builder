package com.appslandia.core.views;

import java.util.List;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.utils.ViewUtils;
import com.appslandia.phrasebuilder.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LabelLayout extends RelativeLayout {

	private List<FilterableItem> labelList;
	private LayoutInflater inflater;
	private LayoutSizer layoutSizer;

	private int vGapPx;
	private int hGapPx;

	private View.OnClickListener onLabelClickListener;

	public LabelLayout(Context context) {
		super(context);
		this.inflater = LayoutInflater.from(context);
	}

	public LabelLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LabelLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.inflater = LayoutInflater.from(context);

		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeoWidgets, defStyle, 0);
		int vGapDp = ta.getInt(R.styleable.LeoWidgets_vGapDp, 8);
		int hGapDp = ta.getInt(R.styleable.LeoWidgets_hGapDp, 8);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		vGapPx = ViewUtils.dpToPx(dm, vGapDp);
		hGapPx = ViewUtils.dpToPx(dm, hGapDp);

		ta.recycle();
	}

	public void setLabelList(List<FilterableItem> labelList) {
		this.labelList = labelList;
	}

	public void setLayoutSizer(LayoutSizer layoutSizer) {
		this.layoutSizer = layoutSizer;
	}

	public void setOnLabelClickListener(View.OnClickListener listener) {
		this.onLabelClickListener = listener;
	}

	protected TextView createLabelView() {
		return (TextView) this.inflater.inflate(R.layout.libs_label, this, false);
	}

	public void createLabelViews() {
		if (this.labelList == null) {
			throw new IllegalArgumentException("labelList is required.");
		}
		if (this.layoutSizer == null) {
			throw new IllegalArgumentException("layoutSizer is required.");
		}
		if (this.getChildCount() > 0) {
			this.removeAllViews();
		}
		final int labelFlowWidth = this.layoutSizer.measureWidth(getResources());

		int rowIndex = 0;
		View prevLabelView = null;
		int labelWidths = 0;
		SparseArray<View> upLabelViewMap = new SparseArray<View>();

		for (FilterableItem label : this.labelList) {

			// labelView initializing
			TextView labelView = createLabelView();

			labelView.setText(label.getName());
			labelView.setId(ViewUtils.nextId());
			labelView.setTag(label);
			labelView.setOnClickListener(this.onLabelClickListener);

			labelView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

			// labelWidths
			if (labelWidths == 0) {
				labelWidths = labelView.getMeasuredWidth();
			} else {
				labelWidths += (hGapPx + labelView.getMeasuredWidth());
			}
			if (labelWidths >= labelFlowWidth) {
				rowIndex++;
				upLabelViewMap.put(rowIndex, prevLabelView);
				prevLabelView = null;
				labelWidths = labelView.getMeasuredWidth();
			}

			// labelView layoutParams
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(labelView.getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
			if (prevLabelView == null) {
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

				View upLabelView = upLabelViewMap.get(rowIndex);
				if (upLabelView == null) {
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					layoutParams.topMargin = vGapPx;
					layoutParams.addRule(RelativeLayout.BELOW, upLabelView.getId());
				}
			} else {
				layoutParams.leftMargin = hGapPx;
				layoutParams.addRule(RelativeLayout.RIGHT_OF, prevLabelView.getId());
				layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, prevLabelView.getId());
			}

			prevLabelView = labelView;
			this.addView(labelView, layoutParams);
		}
	}
}
