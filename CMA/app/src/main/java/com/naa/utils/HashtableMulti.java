package com.naa.utils;

import java.util.Hashtable;

public class HashtableMulti<K, V, Z> extends Hashtable<K, V> {
	 
	private Hashtable<K, Z> hdata = new Hashtable<K, Z>();
	public synchronized V put(K key, V value, Z data) {
		hdata.put(key, data);
		return super.put(key, value);
	}		
	 
	public synchronized V get(Object key) {
		return super.get(key);
	}
	
	public synchronized Z getData(Object key) {
		return hdata.get(key);
	}
}
