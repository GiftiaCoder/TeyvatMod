package com.giftialab.teyvatmod.entities.human.ai;

public abstract class AIUpdater implements IUpdatable {

	public AIUpdater(int tickCount) {
		this.curTick = tickCount;
		this.tickCount = tickCount;
	}
	
	public abstract void onUpdate();
	
	@Override
	public void update() {
		if (++curTick < tickCount) {
			return;
		} else {
			onUpdate();
			curTick = 0;
		}
	}
	
	private int curTick, tickCount;
	
}
