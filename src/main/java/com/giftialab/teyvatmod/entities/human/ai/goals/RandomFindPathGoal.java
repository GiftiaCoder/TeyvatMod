package com.giftialab.teyvatmod.entities.human.ai.goals;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;

import net.minecraft.world.level.pathfinder.Path;

public class RandomFindPathGoal extends MoveWithRiskLevelGoal {

	public RandomFindPathGoal(CharacterEntity entity, long updateCd) {
		super(entity, RiskLevelEnum.LOW, updateCd);
	}

	@Override
	public boolean canUse() {
		return super.canUse() && (owner.getTarget() == null || !owner.getTarget().isAlive());
	}
	
	@Override
	protected float getMoveSpeed() {
		return 1.0f;
	}

	@Override
	protected Path findPath() {
		int gameTime = (int) owner.getLevel().getGameTime();
		return createPath(owner.getSight().getSightBlock(gameTime % AIBattleSight.SIGHT_BLOCK_NUM), gameTime % 6 + 6);
	}

}
