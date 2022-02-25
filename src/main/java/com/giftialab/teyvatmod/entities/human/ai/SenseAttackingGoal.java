package com.giftialab.teyvatmod.entities.human.ai;

import java.util.EnumSet;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class SenseAttackingGoal extends SenseBattleGoal {

	protected long prepareAttackGameTime = 0;
	
	public SenseAttackingGoal(CharacterEntity owner, SenseGoalPackage pkg) {
		super(owner, pkg);
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
	}
	
	@Override
	public void start() {
		prepareAttackGameTime = owner.getLevel().getGameTime();
	}

	public long timeAfterAttackable(long cd) {
		return owner.getLevel().getGameTime() - (prepareAttackGameTime + cd);
	}
	public void resetAttackTime() { prepareAttackGameTime = owner.getLevel().getGameTime(); }
	
}
