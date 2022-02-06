package com.giftialab.teyvatmod.entities.human.ai.goals;

import java.util.EnumSet;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;

import net.minecraft.world.entity.ai.goal.Goal;

public class MidRiskMovementGoal extends MidRiskProxyGoal {

	public MidRiskMovementGoal(CharacterEntity entity, Goal lowGoal, Goal highGoal) {
		super(entity, lowGoal, highGoal);
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

}
