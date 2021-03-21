package com.appslandia.core.adapters;

import java.util.List;

import com.appslandia.core.utils.StringUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutAdapter<T extends AboutItem> extends ArrayAdapterImpl<T> {

	protected int resourceId;
	protected LayoutInflater inflater;

	public AboutAdapter(Activity context, int resourceId, List<T> objects) {
		super(context, resourceId, objects);

		this.resourceId = resourceId;
		this.inflater = context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(this.resourceId, parent, false);

			// View holder
			holder = new ViewHolder();
			holder.nameTextView = (TextView) convertView.findViewById(android.R.id.text1);
			holder.descTextView = (TextView) convertView.findViewById(android.R.id.text2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AboutItem obj = getItem(position);

		// Model to views
		holder.nameTextView.setText(obj.name);

		String desc = StringUtils.trimToNull(obj.desc);
		if (desc != null) {
			holder.descTextView.setText(desc);
		} else {
			holder.descTextView.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView nameTextView;
		TextView descTextView;
	}
}
