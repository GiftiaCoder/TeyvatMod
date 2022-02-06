package com.giftialab.teyvatmod.entities.human.ai.goals;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;

import net.minecraft.world.entity.ai.goal.Goal;

public class MidRiskProxyGoal extends Goal {

	private CharacterEntity owner;
	private Goal lowGoal, highGoal;
	private Goal curGoal;
	
	public MidRiskProxyGoal(CharacterEntity entity, Goal lowGoal, Goal highGoal) {
		this.owner = entity;
		this.lowGoal = lowGoal;
		this.highGoal = highGoal;
		this.curGoal = null;
	}
	
	@Override
	public boolean canUse() {
		if (owner.getAIBrain().getRiskLevel() == RiskLevelEnum.MID) {
			if (owner.getAIBrain().getOrigRiskLevel() == RiskLevelEnum.HIGH) {
				curGoal = highGoal;
			} else {
				curGoal = lowGoal;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void start() {
		curGoal.start();
	}
	
	@Override
	public void stop() {
		curGoal.stop();
	}
	
	@Override
	public void tick() {
		curGoal.tick();
	}
	
}
