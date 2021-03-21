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

@SuppressLint("DefaultLocale")
public class ArrayAdapterImpl<T> extends BaseAdapter implements Filterable {

	protected List<T> mObjects;

	protected final Object mLock = new Object();

	protected int mResource;

	protected int mDropDownResource;

	protected int mFieldId = 0;

	protected boolean mNotifyOnChange = true;

	protected Context mContext;

	// A copy of the original mObjects array, initialized from and then used instead as soon as
	// the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
	protected ArrayList<T> mOriginalValues;
	protected ArrayFilter mFilter;

	protected LayoutInflater mInflater;

	// Extended properties
	protected int textColor = 0;

	public ArrayAdapterImpl(Context context, int resource) {
		init(context, resource, 0, new ArrayList<T>());
	}

	public ArrayAdapterImpl(Context context, int resource, int textViewResourceId) {
		init(context, resource, textViewResourceId, new ArrayList<T>());
	}

	public ArrayAdapterImpl(Context context, int resource, T[] objects) {
		init(context, resource, 0, Arrays.asList(objects));
	}

	public ArrayAdapterImpl(Context context, int resource, int textViewResourceId, T[] objects) {
		init(context, resource, textViewResourceId, Arrays.asList(objects));
	}

	public ArrayAdapterImpl(Context context, int resource, List<T> objects) {
		init(context, resource, 0, objects);
	}

	public ArrayAdapterImpl(Context context, int resource, int textViewResourceId, List<T> objects) {
		init(context, resource, textViewResourceId, objects);
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	protected List<T> mLastObjects;

	public boolean checkObjects(List<T> objects) {
		return mLastObjects != objects;
	}

	public boolean setObjects(List<T> objects) {
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

	public void add(T object) {
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

	public void addAll(Collection<? extends T> collection) {
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

	@SuppressWarnings("unchecked")
	public void addAll(T... items) {
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

	public void insert(T object, int index) {
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

	public void remove(T object) {
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

	public void sort(Comparator<? super T> comparator) {
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

	private void init(Context context, int resource, int textViewResourceId, List<T> objects) {
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

	public T getItem(int position) {
		return mObjects.get(position);
	}

	public int getPosition(T item) {
		return mObjects.indexOf(item);
	}

	public long getItemId(int position) {
		Object item = getItem(position);
		if (item instanceof Item) {
			return ((Item) item).getId();
		}
		return -1;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource, this.textColor);
	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource, int textColor) {
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
			throw new IllegalStateException("ArrayAdapterImpl requires the resource ID to be a TextView", e);
		}

		// textColor
		if (textColor != 0) {
			text.setTextColor(textColor);
		}

		T item = getItem(position);
		if (item instanceof Item) {
			text.setText(((Item) item).getName());
		} else {
			text.setText(item.toString());
		}

		return view;
	}

	public void setDropDownViewResource(int resource) {
		this.mDropDownResource = resource;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mDropDownResource, 0);
	}

	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {

		@Override
		public CharSequence convertResultToString(Object resultValue) {
			if (resultValue == null) {
				return StringUtils.EMPTY_STRING;
			}
			if (resultValue instanceof Item) {
				return ((Item) resultValue).getName();
			} else {
				return resultValue.toString();
			}
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<T>(mObjects);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				ArrayList<T> list;
				synchronized (mLock) {
					list = new ArrayList<T>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();

				ArrayList<T> values;
				synchronized (mLock) {
					values = new ArrayList<T>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<T> newValues = new ArrayList<T>();

				for (int i = 0; i < count; i++) {
					final T item = values.get(i);
					String valueText;

					if (item instanceof Item) {
						valueText = ((Item) item).getName().toLowerCase();
					} else {
						valueText = item.toString().toLowerCase();
					}

					// First match against the whole
					if (valueText.startsWith(prefixString)) {
						newValues.add(item);
					} else {
						final String[] words = valueText.split(" ");
						final int wordCount = words.length;

						// Start at index 0, in case valueText starts with space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {
								newValues.add(item);
								break;
							}
						}
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mObjects = (List<T>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
