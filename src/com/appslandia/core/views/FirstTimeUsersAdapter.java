package com.appslandia.core.views;

import java.util.List;

import com.appslandia.core.adapters.ArrayAdapterImpl;
import com.appslandia.core.adapters.SimpleItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FirstTimeUsersAdapter<T extends SimpleItem> extends ArrayAdapterImpl<T> {

	protected int resourceId;
	protected LayoutInflater inflater;

	public FirstTimeUsersAdapter(Activity context, int resourceId, List<T> objects) {
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
			holder.indexTextView = (TextView) convertView.findViewById(android.R.id.text1);
			holder.descTextView = (TextView) convertView.findViewById(android.R.id.text2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SimpleItem obj = getItem(position);

		// Model to views
		holder.indexTextView.setText(Integer.toString(obj._id));
		holder.descTextView.setText(obj.name);

		return convertView;
	}

	static class ViewHolder {
		TextView indexTextView;
		TextView descTextView;
	}
}
