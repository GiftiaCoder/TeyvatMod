package com.giftialab.teyvatmod.util;

import com.giftialab.teyvatmod.entities.human.ai.IUpdatable;

public class UpdatableDoubleBuffer<T extends IUpdatable> extends DoubleBuffer<T> {

	public UpdatableDoubleBuffer(T b1, T b2) {
		super(b1, b2);
	}
	
	public void update() {
		mutable().update();
		swap();
	}

}
