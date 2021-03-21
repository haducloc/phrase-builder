package com.appslandia.core.views;

import java.util.ArrayList;
import java.util.List;

import com.appslandia.core.adapters.FilterableItem;
import com.appslandia.core.adapters.LabelItem;
import com.appslandia.core.utils.StringUtils;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LabelEditLayout extends RelativeLayout {

	private List<FilterableItem> labelList;
	private LayoutInflater inflater;

	private AutoCompleteTextView labelEditText;
	private SparseArray<LeoLabelX> labelViewMap = new SparseArray<LeoLabelX>();
	private LayoutSizer layoutSizer;

	private int vGapPx;
	private int hGapPx;

	public LabelEditLayout(Context context) {
		super(context);
		this.inflater = LayoutInflater.from(context);
	}

	public LabelEditLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LabelEditLayout(Context context, AttributeSet attrs, int defStyle) {
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

	public void setLabelEditText(AutoCompleteTextView labelEditText) {
		this.labelEditText = labelEditText;
		this.labelEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LabelEditLayout.this.labelEditText.setText(StringUtils.EMPTY_STRING);

				if (id != LabelItem.LABEL_ID_NOT_FOUND) {
					FilterableItem label = (FilterableItem) LabelEditLayout.this.labelEditText.getAdapter().getItem(position);

					if (labelViewMap.indexOfKey(label.getId()) < 0) {
						labelList.add(new LabelItem(label.getId(), label.getName(), label.getFilterName()));
						createLabelViews();
						LabelEditLayout.this.labelEditText.requestFocus();
					}
				}
			}
		});
	}

	public List<FilterableItem> getLabelList() {
		return this.labelList;
	}

	public void setLabelList(List<FilterableItem> labelList) {
		this.labelList = labelList;
	}

	public void setLayoutSizer(LayoutSizer layoutSizer) {
		this.layoutSizer = layoutSizer;
	}

	protected LeoLabelX createLabelView() {
		return (LeoLabelX) this.inflater.inflate(R.layout.libs_label_remove, this, false);
	}

	public void createLabelViews() {
		if (this.labelList == null) {
			this.labelList = new ArrayList<FilterableItem>();
		}
		if (this.layoutSizer == null) {
			throw new IllegalArgumentException("layoutSizer is required.");
		}
		if (this.getChildCount() > 0) {
			this.removeAllViews();
		}
		if (labelList.isEmpty() == false && getVisibility() == View.GONE) {
			setVisibility(View.VISIBLE);
		}

		final int labelFlowWidth = this.layoutSizer.measureWidth(getResources());

		int rowIndex = 0;
		View prevLabelView = null;
		int labelWidths = 0;
		SparseArray<View> upLabelViewMap = new SparseArray<View>();

		for (FilterableItem label : this.labelList) {
			LeoLabelX labelView = this.labelViewMap.get(label.getId());

			// labelView initializing
			if (labelView == null) {
				labelView = createLabelView();

				labelView.setText(label.getName());
				labelView.setTag(label);
				labelView.setOnCloseClickedListener(new LeoLabelX.OnCloseClickedListener() {

					@Override
					public void onCloseClicked(TextView textView) {
						FilterableItem item = (FilterableItem) textView.getTag();

						labelList.remove(item);
						labelViewMap.remove(item.getId());
						createLabelViews();
						labelEditText.requestFocus();

						if (getChildCount() == 0) {
							setVisibility(View.GONE);
						}
					}
				});

				labelView.setId(ViewUtils.nextId());
				labelView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

				this.labelViewMap.put(label.getId(), labelView);
			}

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