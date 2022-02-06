package com.giftialab.teyvatmod.entities.human.ai;

public enum RiskLevelEnum {
	PEACE(false, false, false),
	LOW(true, false, false),
	MID(true, true, true),
	HIGH(true, true, true);

	private boolean isFindTarget;
	private boolean shouldQuickShoot;
	private boolean needBreakOut;
	
	private RiskLevelEnum(boolean isFindTarget, boolean shouldQuickShoot, boolean needBreakOut) {
		this.isFindTarget = isFindTarget;
		this.shouldQuickShoot = shouldQuickShoot;
		this.needBreakOut = needBreakOut;
	}
	
	public boolean isFindTarget() {
		return isFindTarget;
	}
	public boolean shouldQuickShoot() {
		return shouldQuickShoot;
	}
	public boolean needBreakOut() {
		return needBreakOut;
	}

}
