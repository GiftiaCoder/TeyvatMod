package com.giftialab.teyvatmod.util;

public class DoubleBuffer<T> {
	public DoubleBuffer(T b1, T b2) {
		this.b1 = b1;
		this.b2 = b2;
	}
	public T get() {
		return b1;
	}
	public T mutable() {
		return b2;
	}
	public void swap() {
		T b = b1;
		b1 = b2;
		b2 = b;
	}
	
	private T b1, b2;
}
