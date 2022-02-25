package com.giftialab.teyvatmod.entities.human.ai;

import java.util.EnumSet;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class SenseMovingGoal extends SenseBattleGoal {

	private boolean isLowRisk = true;
	
	public SenseMovingGoal(CharacterEntity owner, SenseGoalPackage pkg) {
		super(owner, pkg);
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}
	
	@Override
	protected void highTick() {
		if (!isLowRisk) {
			low2High();
		}
		highTickMove();
		isLowRisk = false;
	}
	
	@Override
	protected void lowTick() {
		if (!isLowRisk) {
			high2Low();
		}
		lowTickMove();
		isLowRisk = true;
	}

	protected void low2High() {
		owner.getMoveControl().strafe(0, 0);
		owner.getNavigation().stop();
	}
	protected void high2Low() {
		owner.getMoveControl().strafe(0, 0);
		owner.getNavigation().stop();
	}
	protected abstract void highTickMove();
	protected abstract void lowTickMove();

}
