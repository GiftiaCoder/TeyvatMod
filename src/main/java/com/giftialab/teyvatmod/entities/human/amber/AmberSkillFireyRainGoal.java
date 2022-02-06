package com.giftialab.teyvatmod.entities.human.amber;

import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;

import net.minecraft.world.entity.ai.goal.Goal;

public class AmberSkillFireyRainGoal extends Goal {

	private AmberEntity amber;
	private long timeCd;
	private long nextInvokeTime = 0;
	
	public AmberSkillFireyRainGoal(AmberEntity owner, long timeCd) {
		this.amber = owner;
		this.timeCd = timeCd;
	}
	
	@Override
	public boolean canUse() {
		if (amber.getAIBrain().getRiskLevel() == RiskLevelEnum.PEACE) {
			return false;
		}
		if (nextInvokeTime < amber.getLevel().getGameTime()) {
			amber.activeFireyRain();
			nextInvokeTime = amber.getLevel().getGameTime() + timeCd;
		}
		return false;
	}
	
}
