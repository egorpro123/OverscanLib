package ru.overscan.lib.data;

import java.util.Iterator;

import android.util.SparseArray;

public class SparseArrayIterator<T> implements Iterator<T> {
	SparseArray<T> array;
	int ind;
	
	public SparseArrayIterator(SparseArray<T> a) {
		array = a;
		ind = 0;
	}

	@Override
	public boolean hasNext() {
		//
		return ind < array.size();
	}

	@Override
	public T next() {
		//
		if (hasNext()) {
			return array.get(array.keyAt(ind++));
		}
		else return null;
	}

	@Override
	public void remove() {
		//
		
	}

}
