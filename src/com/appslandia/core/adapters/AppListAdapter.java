package com.appslandia.core.adapters;

import java.util.List;

import com.appslandia.core.utils.StringUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter<T extends AboutItem> extends ArrayAdapterImpl<T> {

	protected int resourceId;
	protected LayoutInflater inflater;

	public AppListAdapter(Activity context, int resourceId, List<T> objects) {
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

			holder.iconView = (ImageView) convertView.findViewById(android.R.id.icon1);
			holder.nameTextView = (TextView) convertView.findViewById(android.R.id.text1);
			holder.descTextView = (TextView) convertView.findViewById(android.R.id.text2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Model to views
		AppItem obj = (AppItem) getItem(position);
		holder.iconView.setImageResource(obj.iconResId);
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
		ImageView iconView;
		TextView nameTextView;
		TextView descTextView;
	}
}
