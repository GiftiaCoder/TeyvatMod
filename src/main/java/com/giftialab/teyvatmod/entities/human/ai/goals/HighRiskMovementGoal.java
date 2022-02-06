package com.giftialab.teyvatmod.entities.human.ai.goals;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;
import net.minecraft.world.level.pathfinder.Path;

public class HighRiskMovementGoal extends MoveWithRiskLevelGoal {

	private float distance;
	
	public HighRiskMovementGoal(CharacterEntity entity, float distance, long updateCd) {
		super(entity, RiskLevelEnum.HIGH, updateCd);
		this.distance = distance;
	}
	
	@Override
	protected Path findPath() {
		float[] windowRiskScore = owner.getSight().getWindowRiskScore();
		AIBattleSight.SightBlock[] sightBlocks = owner.getSight().getSightBlocks();
		float minScore = Float.MAX_VALUE;
		Path minRiskPath = null;
		//int finalId = 0;
		for (int i = 0; i < windowRiskScore.length; ++i) {
			float score = windowRiskScore[i];
			if (minScore > score) {
				Path path = createPath(sightBlocks[i], distance);
				if (path != null) {
					minScore = score;
					minRiskPath = path;
					//finalId = i;
				}
			}
		}
		return minRiskPath;
	}
	
	@Override
	protected float getMoveSpeed() {
		return 1.5F;
	}
	
}
