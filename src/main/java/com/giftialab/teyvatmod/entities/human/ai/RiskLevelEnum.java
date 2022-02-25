package com.giftialab.teyvatmod.entities.human.ai;

public enum RiskLevelEnum {
	PEACE(false, false),
	LOW(true, false),
	MID(true, true),
	HIGH(false, true);
	
	private boolean enbaleLow, enbaleHigh;
	
	private RiskLevelEnum(boolean enbaleLow, boolean enbaleHigh) {
		this.enbaleLow = enbaleLow;
		this.enbaleHigh = enbaleHigh;
	}
	
	public boolean enableLow() {
		return enbaleLow;
	}
	
	public boolean enableHigh() {
		return enbaleHigh;
	}

}
