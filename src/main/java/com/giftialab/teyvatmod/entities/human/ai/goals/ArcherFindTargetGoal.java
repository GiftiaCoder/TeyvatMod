package com.giftialab.teyvatmod.entities.human.ai.goals;

import java.util.List;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class ArcherFindTargetGoal extends Goal {
	
	private CharacterEntity owner;
	private long resetTargetTick = 0;
	
	private LivingEntity expectedTarget = null;
	private double expectedTargetScore = 0;
	private int expectedTargetCnt = 0;
	private RiskLevelEnum origRiskLevel = RiskLevelEnum.PEACE;

	public ArcherFindTargetGoal(CharacterEntity owner) {
		this.owner = owner;
	}

	@Override
	public boolean canUse() {
		RiskLevelEnum riskLevel = owner.getAIBrain().getRiskLevel();
		if (riskLevel == RiskLevelEnum.LOW || riskLevel == RiskLevelEnum.MID) {
			if (owner.getTarget() == null || !owner.getTarget().isAlive()) {
				return findNormalTarget();
			}
			if (owner.getAIBrain().getRiskLevel() != origRiskLevel) {
				origRiskLevel = owner.getAIBrain().getRiskLevel();
				return findNormalTarget();
			}
			if (owner.getLevel().getGameTime() >= resetTargetTick) {
				return findNormalTarget();
			}
		} else if (riskLevel == RiskLevelEnum.HIGH) {
			return findFacingTarget();
		}
		return false;
	}
	
	private boolean findFacingTarget() {
		AIBattleSight.SightBlock facingBlock = owner.getSight().getSightBlockByAngle((int) owner.getAIBrain().getFacingAngle());
		if (facingBlock.getHostiles().size() > 0) {
			owner.setTarget((LivingEntity) facingBlock.getHostiles((int) facingBlock.getRankData()[0]));
		} else {
			owner.setTarget(null);
		}
		return false;
	}
	
	private boolean findNormalTarget() {
		LivingEntity target = findRiskTarget();
		if (target == null) {
			target = findExpectedTarget();
		}
		if (target != null) {
			resetTargetTick = 100 + owner.getLevel().getGameTime();
			owner.setTarget(target);
		} else {
			owner.setTarget(null);
		}
		return false;
	}
	
	private LivingEntity findRiskTarget() {
		LivingEntity target = null;
		double targetDistSQ = Double.MAX_VALUE;
		for (AIBattleSight.SightBlock sightBlock : owner.getSight().getSightBlocks()) {
			List<LivingEntity> hostiles = sightBlock.getHostiles();
			if (hostiles.size() == 0) {
				continue;
			}
			long[] entityRankList = sightBlock.getRankData();

			long rankData = entityRankList[0];
			double distSq = rankData >> 32;
			int rankIdx = (int) rankData;
			LivingEntity entity = (LivingEntity) hostiles.get(rankIdx);
			if (distSq < 12 * 12) {
				double notAttackDistSq = sightBlock.getAttackableDistSq();
				if (notAttackDistSq < distSq) {
					continue;
				}
				if (distSq < targetDistSQ) {
					targetDistSQ = distSq;
					target = entity;
				}
			}
		}
		return target;
	}

	private boolean findExpectedTarget(AIBattleSight.SightBlock sightBlock) {
		double limitAttackRangeSq = sightBlock.getAttackableDistSq();
		List<LivingEntity> hostiles = sightBlock.getHostiles();
		long[] hostileRankList = sightBlock.getRankData();
		for (int i = 0; i < hostiles.size(); ++i) {
			long rankData = hostileRankList[0];
			double distSq = rankData >> 32;
			if (distSq >= limitAttackRangeSq) {
				break;
			}
			
			LivingEntity entity = (LivingEntity) hostiles.get((int) rankData);
			float wantedScore = (float) (entity.getAttributeValue(Attributes.ATTACK_DAMAGE) / (entity.getHealth() + distSq + 1));
			if (expectedTargetScore < wantedScore) {
				expectedTargetScore = wantedScore;
				expectedTarget = entity;
			}
			
			if (++expectedTargetCnt > 20) {
				return true;
			}
		}
		return false;
	}
	
	private LivingEntity findExpectedTarget() {
		expectedTarget = null;
		expectedTargetScore = 0;
		
		int facingBlockId = AIBattleSight.getBlockId((int) owner.getAIBrain().getFacingAngle());
		if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId))) {
			return expectedTarget;
		}
		for (int i = 1, e = AIBattleSight.SIGHT_BLOCK_NUM >> 1; i < e; ++i) {
			if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId + i))) {
				return expectedTarget;
			}
			if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId - i))) {
				return expectedTarget;
			}
		}
		if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId + (AIBattleSight.SIGHT_BLOCK_NUM >> 1)))) {
			return expectedTarget;
		}
		return expectedTarget;
	}
	
}
