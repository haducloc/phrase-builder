package com.appslandia.core.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.appslandia.core.utils.StringUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class LabelListAdapter extends BaseAdapter implements Filterable {

	protected List<FilterableItem> mObjects;

	protected final Object mLock = new Object();

	protected int mResource;

	protected int mDropDownResource;

	protected int mFieldId = 0;

	protected boolean mNotifyOnChange = true;

	protected Context mContext;

	// A copy of the original mObjects array, initialized from and then used instead as soon as
	// the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
	protected ArrayList<FilterableItem> mOriginalValues;
	protected ArrayFilter mFilter;

	protected LayoutInflater mInflater;

	public LabelListAdapter(Context context, int resource) {
		init(context, resource, 0, new ArrayList<FilterableItem>());
	}

	public LabelListAdapter(Context context, int resource, int textViewResourceId) {
		init(context, resource, textViewResourceId, new ArrayList<FilterableItem>());
	}

	public LabelListAdapter(Context context, int resource, FilterableItem[] objects) {
		init(context, resource, 0, Arrays.asList(objects));
	}

	public LabelListAdapter(Context context, int resource, int textViewResourceId, FilterableItem[] objects) {
		init(context, resource, textViewResourceId, Arrays.asList(objects));
	}

	public LabelListAdapter(Context context, int resource, List<FilterableItem> objects) {
		init(context, resource, 0, objects);
	}

	public LabelListAdapter(Context context, int resource, int textViewResourceId, List<FilterableItem> objects) {
		init(context, resource, textViewResourceId, objects);
	}

	protected List<? extends FilterableItem> mLastObjects;

	public boolean setObjects(List<? extends FilterableItem> objects) {
		synchronized (mLock) {
			if (mLastObjects == objects) {
				return false;
			}
			if (mOriginalValues != null) {
				mOriginalValues.clear();
				if (objects != null) {
					mOriginalValues.addAll(objects);
				}
			} else {
				mObjects.clear();
				if (objects != null) {
					mObjects.addAll(objects);
				}
			}
			mLastObjects = objects;
		}
		if (mNotifyOnChange) {
			notifyDataSetChanged();
		}
		return true;
	}

	public void add(FilterableItem object) {
		synchronized (mLock) {
			if (mOriginalValues != null) {
				mOriginalValues.add(object);
			} else {
				mObjects.add(object);
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void addAll(Collection<? extends FilterableItem> collection) {
		synchronized (mLock) {
			if (mOriginalValues != null) {
				mOriginalValues.addAll(collection);
			} else {
				mObjects.addAll(collection);
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void addAll(FilterableItem... items) {
		synchronized (mLock) {
			if (mOriginalValues != null) {
				Collections.addAll(mOriginalValues, items);
			} else {
				Collections.addAll(mObjects, items);
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void insert(FilterableItem object, int index) {
		synchronized (mLock) {
			if (mOriginalValues != null) {
				mOriginalValues.add(index, object);
			} else {
				mObjects.add(index, object);
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void remove(FilterableItem object) {
		synchronized (mLock) {
			if (mOriginalValues != null) {
				mOriginalValues.remove(object);
			} else {
				mObjects.remove(object);
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void clear() {
		synchronized (mLock) {
			if (mOriginalValues != null) {
				mOriginalValues.clear();
			} else {
				mObjects.clear();
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	public void sort(Comparator<? super FilterableItem> comparator) {
		synchronized (mLock) {
			if (mOriginalValues != null) {
				Collections.sort(mOriginalValues, comparator);
			} else {
				Collections.sort(mObjects, comparator);
			}
		}
		if (mNotifyOnChange)
			notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mNotifyOnChange = true;
	}

	public void setNotifyOnChange(boolean notifyOnChange) {
		mNotifyOnChange = notifyOnChange;
	}

	private void init(Context context, int resource, int textViewResourceId, List<FilterableItem> objects) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = mDropDownResource = resource;
		mObjects = objects;
		mFieldId = textViewResourceId;
	}

	public Context getContext() {
		return mContext;
	}

	public int getCount() {
		return mObjects.size();
	}

	public FilterableItem getItem(int position) {
		return mObjects.get(position);
	}

	public int getPosition(FilterableItem item) {
		return mObjects.indexOf(item);
	}

	public long getItemId(int position) {
		return getItem(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
		View view;
		TextView text;

		if (convertView == null) {
			view = mInflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		try {
			if (mFieldId == 0) {
				// If no custom field is assigned, assume the whole resource is a TextView
				text = (TextView) view;
			} else {
				// Otherwise, find the TextView field within the layout
				text = (TextView) view.findViewById(mFieldId);
			}
		} catch (ClassCastException e) {
			throw new IllegalStateException("LabelListAdapter requires the resource ID to be a TextView", e);
		}

		FilterableItem item = getItem(position);
		text.setText(item.getName());

		return view;
	}

	public void setDropDownViewResource(int resource) {
		this.mDropDownResource = resource;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mDropDownResource);
	}

	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	@SuppressLint("DefaultLocale")
	private class ArrayFilter extends Filter {

		@Override
		public CharSequence convertResultToString(Object resultValue) {
			return resultValue != null ? ((FilterableItem) resultValue).getName() : StringUtils.EMPTY_STRING;
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<FilterableItem>(mObjects);
				}
			}

			// Prefix empty?
			if (prefix == null || prefix.length() == 0) {
				ArrayList<FilterableItem> list;
				synchronized (mLock) {
					list = new ArrayList<FilterableItem>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {

				// Prefix not empty
				String prefixString = StringUtils.toSearchable(prefix.toString());

				ArrayList<FilterableItem> values;
				synchronized (mLock) {
					values = new ArrayList<FilterableItem>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<FilterableItem> newValues = new ArrayList<FilterableItem>();

				for (int i = 0; i < count; i++) {
					final FilterableItem item = values.get(i);

					if (item.getFilterName().startsWith(prefixString)) {
						newValues.add(item);
					}
				}

				// Not found
				if (newValues.isEmpty()) {
					newValues.add(LabelItem.getLabelNotFound(mContext));
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mObjects = (List<FilterableItem>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
