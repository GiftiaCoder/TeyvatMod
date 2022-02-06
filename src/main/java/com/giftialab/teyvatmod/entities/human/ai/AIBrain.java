package com.giftialab.teyvatmod.entities.human.ai;

import java.util.HashMap;
import java.util.Map;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.util.DoubleBuffer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AIBrain extends AIUpdater {

	private CharacterEntity owner;
	
	private ITargetClassifier targetClassifier;
	
	private RiskLevelEnum riskLevel = RiskLevelEnum.PEACE, origRiskLevel = RiskLevelEnum.PEACE;
	private float highRiskBound = 10, midRiskBound = 4;
	private double sightAngle = 60;
	private float totalRiskScore;
	
	private static class EntityForgetTimeMap extends HashMap<Integer, Long> { private static final long serialVersionUID = 1L; }
	private DoubleBuffer<EntityForgetTimeMap> entityMemory;
	
	public AIBrain(CharacterEntity entity, ITargetClassifier targetClassifier) {
		super(10);
		owner = entity;
		this.targetClassifier = targetClassifier;
		entityMemory = new DoubleBuffer<>(new EntityForgetTimeMap(), new EntityForgetTimeMap());
	}
	
	public boolean isRememberEntity(Entity entity) {
		Long timeLimit = entityMemory.get().get(entity.getId());
		if (timeLimit == null) {
			return false;
		} else if (timeLimit < owner.getLevel().getGameTime()) {
			return false;
		}
		return true;
	}
	public void updateEntityRemembered(Entity entity) {
		entityMemory.mutable().put(entity.getId(), owner.getLevel().getGameTime() + 100);
	}
	
	public float getHighRiskBound() {
		return highRiskBound;
	}
	public float getMidRiskBound() {
		return midRiskBound;
	}
	public void onEntityAttacked(Entity entity) {
		if (highRiskBound > 8) {
			highRiskBound -= 2;
		}
		if (midRiskBound > 2) {
			midRiskBound -= 1;
		}
	}
	public void onAttackEntity(Entity entity) {
		if (highRiskBound < 32) {
			highRiskBound += 0.2;
		}
		if (midRiskBound < 12) {
			midRiskBound += 0.1;
		}
	}

	public double getSightAngle() {
		return sightAngle;
	}
	public void largeSightAngle() {
		this.sightAngle = 120;
	}
	public void smallSightAngle() {
		this.sightAngle = 30;
	}
	public void normalSightAngle() {
		this.sightAngle = 60;
	}
	public double getSightRangeBackSQ() {
		return 6.0 * 6.0;
	}
	
	private float updateRiskLevel(AIBattleSight sight) {
		if (sight.getHostileCount() == 0) {
			origRiskLevel = riskLevel;
			riskLevel = RiskLevelEnum.PEACE;
			return 0;
		} else {
			float riskScore = 0;
			for (AIBattleSight.SightBlock block : sight.getSightBlocks()) {
				riskScore += block.getRiskScore();
			}
			if (riskScore >= getHighRiskBound()) {
				origRiskLevel = riskLevel;
				riskLevel = RiskLevelEnum.HIGH;
			} else if (riskScore >= getMidRiskBound()) {
				origRiskLevel = riskLevel;
				riskLevel = RiskLevelEnum.MID;
			} else {
				origRiskLevel = riskLevel;
				riskLevel = RiskLevelEnum.LOW;
			}
			return riskScore;
		}
	}
	
	public float getTotalRiskScore() {
		return totalRiskScore;
	}
	public RiskLevelEnum getRiskLevel() {
		return riskLevel;
	}
	public RiskLevelEnum getOrigRiskLevel() {
		return origRiskLevel;
	}
	
	public boolean isHostile(LivingEntity entity) {
		return targetClassifier.isHostile(entity);
	}
	
	public boolean avoidToAttack(LivingEntity entity) {
		return targetClassifier.avoidToAttack(entity);
	}
	
	public void calculateRiskScore(AIBattleSight.SightBlock sightBlock, int sightBlockId, AIBattleSight sight) {
		boolean flag1 = false;
		for (int j = 0; j < sightBlock.getHostiles().size(); ++j) {
			long rankData = sightBlock.getRankData()[j];
			double distSq = (int) (rankData >> 32);
			int rankIdx = (int) rankData;
			LivingEntity entity = sightBlock.getHostiles(rankIdx);
			ItemStack itemInHand = entity.getItemInHand(InteractionHand.MAIN_HAND);
			if (itemInHand != null && (itemInHand.getItem() == Items.BOW || itemInHand.getItem() == Items.CROSSBOW)) {
				float score = (float) (entity.getAttributeValue(Attributes.ATTACK_DAMAGE) / Math.sqrt(distSq));
				sight.getSightBlock(sightBlockId).addRiskScore(score);
				sight.getSightBlock(sightBlockId + (AIBattleSight.SIGHT_BLOCK_NUM >> 1)).addRiskScore(score / 2);
			} else if (entity instanceof Mob) {
				if (flag1) {
					continue;
				}
				flag1 = true;
				
				int riskSec = 24;
				// if (((Mob) entity).getTarget() == owner) {
				// 	riskSec += 16;
				// }
				float exptectedRiskDistSq = entity.getSpeed() * riskSec + entity.getBbWidth() + owner.getBbWidth();
				exptectedRiskDistSq *= exptectedRiskDistSq;
				
				if (exptectedRiskDistSq < distSq) {
					continue;
				}
				float score = (1 - (float) (distSq / exptectedRiskDistSq)) * (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
				// float totalScore = score, origScore = score;
				
				int blockNum = AIBattleSight.SIGHT_BLOCK_NUM >> 2;
				float scoreDec = score / blockNum;
				sight.getSightBlock(sightBlockId).addRiskScore(score);
				for (int i = 1; i <= blockNum; ++i) {
					score -= scoreDec;
					// totalScore += score;
					// totalScore += score;
					sight.getSightBlock(sightBlockId + i).addRiskScore(score);
					sight.getSightBlock(sightBlockId - i).addRiskScore(score);
				}
				
				// livingEntity.setCustomName(new net.minecraft.network.chat.TextComponent("" + sightBlockId + ", " + origScore + ", " + totalScore));
			}
		}
	}
	
	private void updateEntityMemory() {
		EntityForgetTimeMap dst = entityMemory.mutable();
		for (Map.Entry<Integer, Long> entry : entityMemory.get().entrySet()) {
			if (entry.getValue() <= owner.getLevel().getGameTime()) {
				continue;
			}
			Long val = dst.get(entry.getKey());
			if (val == null) {
				dst.put(entry.getKey(), entry.getValue());
			}
		}
		if (owner.getLastHurtByMob() != null) {
			dst.put(owner.getLastHurtByMob().getId(), owner.getLevel().getGameTime() + 100);
			owner.setLastHurtByMob(null);
		}
		entityMemory.swap();
	}
	
	@Override
	public void onUpdate() {
		totalRiskScore = updateRiskLevel(owner.getSight());
		updateEntityMemory();
		// if (riskLevel == RiskLevelEnum.HIGH) {
		// 	largeSightAngle();
		// } else {
		// 	normalSightAngle();
		// }
		// TODO
	}
	
	public float getFacingAngle() {
		return owner.getYHeadRot() + 90;
	}
	
}
