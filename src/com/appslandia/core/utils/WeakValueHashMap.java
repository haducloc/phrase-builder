package com.appslandia.core.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeakValueHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

	private HashMap<K, WeakValue<K, V>> hash;
	private ReferenceQueue<V> queue;

	public WeakValueHashMap() {
		this(16);
	}

	public WeakValueHashMap(int capacity) {
		hash = new HashMap<K, WeakValue<K, V>>(capacity);
		queue = new ReferenceQueue<V>();
	}

	@Override
	public V put(K key, V value) {
		processQueue();

		WeakValue<K, V> weakValue = (value != null) ? new WeakValue<K, V>(key, value, queue) : (null);
		return getReferenceValue(hash.put(key, weakValue));
	}

	@Override
	public V get(Object key) {
		processQueue();
		return getReferenceValue(hash.get(key));
	}

	@Override
	public V remove(Object key) {
		processQueue();
		return getReferenceValue(hash.get(key));
	}

	@Override
	public void clear() {
		processQueue();
		hash.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		processQueue();
		return hash.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		processQueue();
		for (Map.Entry<K, WeakValue<K, V>> entry : hash.entrySet()) {
			if (value == getReferenceValue(entry.getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<K> keySet() {
		processQueue();
		return hash.keySet();
	}

	@Override
	public int size() {
		processQueue();
		return hash.size();
	}

	@Override
	public boolean isEmpty() {
		processQueue();
		return hash.size() == 0;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		processQueue();

		Set<Map.Entry<K, V>> entries = new HashSet<Map.Entry<K, V>>(hash.size());
		for (Map.Entry<K, WeakValue<K, V>> entry : hash.entrySet()) {
			entries.add(new AbstractMap.SimpleEntry<K, V>(entry.getKey(), getReferenceValue(entry.getValue())));
		}
		return Collections.unmodifiableSet(entries);
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	private V getReferenceValue(WeakValue<K, V> weakValue) {
		return (weakValue != null) ? weakValue.get() : (null);
	}

	@SuppressWarnings("unchecked")
	private void processQueue() {
		WeakValue<K, V> weakValue;
		while ((weakValue = (WeakValue<K, V>) queue.poll()) != null) {
			hash.remove(weakValue.getKey());
		}
	}

	private static class WeakValue<K, V> extends WeakReference<V> {
		final K key;

		public WeakValue(K key, V value, ReferenceQueue<V> queue) {
			super(value, queue);
			this.key = key;
		}

		public K getKey() {
			return key;
		}
	}
}
