package com.giftialab.teyvatmod.entities.human.ai;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class SenseBattleGoal extends Goal {

	protected CharacterEntity owner;
	protected SenseGoalPackage pkg;
	
	public SenseBattleGoal(CharacterEntity owner, SenseGoalPackage pkg) {
		this.owner = owner;
		this.pkg = pkg;
	}
	
	@Override
	public boolean canUse() {
		return owner.getAITask().getTaskType() == pkg.getSense() &&
				owner.getAIBrain().getRiskLevel() != RiskLevelEnum.PEACE;
	}
	
	@Override
	public void tick() {
		switch (owner.getAIBrain().getRiskLevel()) {
		case LOW:
			lowTick();
			break;
		case HIGH:
			highTick();
			break;
		default:
			if (owner.getAIBrain().getOrigRiskLevel() == RiskLevelEnum.LOW) {
				lowTick();
			} else {
				highTick();
			}
			break;
		}
	}
	
	protected abstract void highTick();
	protected abstract void lowTick();

}
