package com.giftialab.teyvatmod.entities.human.ai;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.util.QuickMath;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

public class AIBattleSight extends AIUpdater {

	public static final int SIGHT_BLOCK_ANGLE = 30;
	public static final int SIGHT_BLOCK_HALF_ANGLE = SIGHT_BLOCK_ANGLE / 2;
	public static final int SIGHT_BLOCK_NUM = 360 / SIGHT_BLOCK_ANGLE;
	public static final double IGNORE_SIZE_DIST_SQ = Math.sin(SIGHT_BLOCK_HALF_ANGLE) * Math.sin(SIGHT_BLOCK_HALF_ANGLE) * 16.0D;

	private CharacterEntity owner;
	private int hostileCount = 0;
	private SightBlock[] sightBlocks = new SightBlock[SIGHT_BLOCK_NUM];
	private float[] windowRiskScore = new float[SIGHT_BLOCK_NUM];

	public AIBattleSight(CharacterEntity entity, int tickCount) {
		super(tickCount);
		this.owner = entity;
		for (int i = 0, facingAngle = 0; i < SIGHT_BLOCK_NUM; i++, facingAngle += SIGHT_BLOCK_ANGLE) {
			this.sightBlocks[i] = new SightBlock(i, facingAngle);
		}
	}

	public void onUpdate() {
		hostileCount = 0;
		for (int i = 0; i < SIGHT_BLOCK_NUM; i++) {
			windowRiskScore[i] = 0.0F;
			sightBlocks[i].reset();
		}
		for (Entity entity : owner.getDetecter().getEntities()) {
			if (!(entity instanceof LivingEntity) || !entity.isAlive()) {
				continue;
			}
			float dx = (float) (entity.getX() - owner.getX());
			float dz = (float) (entity.getZ() - owner.getZ());
			if (owner.getAIBrain().isHostile((LivingEntity) entity)) {
				int angle = (int) QuickMath.vecToAng(dx, dz);
				int blockId = getBlockId(angle);
				getSightBlock(blockId).addHostile((LivingEntity) entity, (dx * dx + dz * dz));
				continue;
			}
			if (owner.getAIBrain().avoidToAttack((LivingEntity) entity)) {
				float distSq = dx * dx + dz * dz;
				if (entity.getBbWidth() > 0.0F && distSq > 0.0F && distSq < IGNORE_SIZE_DIST_SQ) {
					int angle = (int) QuickMath.vecToAng(dx, dz);
					float angSin = entity.getBbWidth() * 1.4F / Mth.sqrt(distSq);
					int angOff = (angSin <= 1.0D) ? (int) QuickMath.asinA(angSin) : 90;
					for (int begId = getBlockId(angle - angOff), endId = getBlockId(angle + angOff); begId <= endId; begId++) {
						getSightBlock(begId).setAvoidAttackDist((LivingEntity) entity, distSq);
					}
				} else {
					int angle = (int) QuickMath.vecToAng(dx, dz);
					int blockId = getBlockId(angle);
					getSightBlock(blockId).setAvoidAttackDist((LivingEntity) entity, distSq);
				}
			}
		}
		for (SightBlock sightBlock : sightBlocks) {
			sightBlock.done();
			hostileCount += sightBlock.getHostiles().size();
		}
		calculateWindowRiskScore();
	}

	public SightBlock[] getSightBlocks() {
		return sightBlocks;
	}

	public SightBlock getSightBlock(int id) {
		id = id % SIGHT_BLOCK_NUM;
		if (id < 0) {
			id += SIGHT_BLOCK_NUM;
		}
		return sightBlocks[id];
	}

	public SightBlock getSightBlockByAngle(int angle) {
		return getSightBlock(getBlockId(angle));
	}

	public static int getBlockId(int angle) {
		return (angle + SIGHT_BLOCK_HALF_ANGLE) / SIGHT_BLOCK_ANGLE;
	}

	public int getHostileCount() {
		return hostileCount;
	}

	public float[] getWindowRiskScore() {
		return windowRiskScore;
	}

	private void calculateWindowRiskScore() {
		int halfWindowSize = 2;
		float curWindowRiskScore = 0.0F;
		for (int i = -halfWindowSize; i <= halfWindowSize; i++) {
			curWindowRiskScore += (getSightBlock(i)).riskScore;
		}
		windowRiskScore[0] = curWindowRiskScore;
		for (int i = 1, front = -halfWindowSize, next = 1 + halfWindowSize; i < windowRiskScore.length; ++i, ++front, ++next) {
			curWindowRiskScore -= (getSightBlock(front)).riskScore;
			curWindowRiskScore += (getSightBlock(next)).riskScore;
			windowRiskScore[i] = curWindowRiskScore;
		}
		// float[] allRiskScore = new float[windowRiskScore.length];
		// for (int j = 0; j < this.windowRiskScore.length; j++) {
		// 	allRiskScore[j] = (getSightBlock(j)).riskScore;
		// }
	}

	public class SightBlock {
		
		private int blockId;
		private int facingAngle;
		private boolean isBack = false;
		
		private List<LivingEntity> hostiles = Lists.newArrayList();
		private long[] rankList = new long[64];
		
		private float riskScore = 0.0F;
		
		private double attackableDistSq;
		private double nearestHostileDistSq;

		public SightBlock(int blockId, int facingAngle) {
			this.blockId = blockId;
			this.facingAngle = facingAngle;
		}

		public void reset() {
			hostiles.clear();
			attackableDistSq = Double.MAX_VALUE;
			nearestHostileDistSq = Double.MAX_VALUE;
			float angDel = Math.abs((owner.getAIBrain().getFacingAngle() - facingAngle) % 360.0F);
			if (angDel > owner.getAIBrain().getSightAngle() && 360 > (owner.getAIBrain().getSightAngle() + angDel)) {
				isBack = true;
			} else {
				isBack = false;
			}
			riskScore = 0.0F;
		}

		public void addHostile(LivingEntity entity, double distSq) {
			double delY = entity.getY() - owner.getY();
			if ((isBack && distSq >= owner.getAIBrain().getSightRangeBackSQ())
					|| delY < -32.0D || delY > 64.0D
					|| !owner.getSensing().hasLineOfSight(entity)) {
				if (!owner.getAIBrain().isRememberEntity((Entity) entity)) {
					return;
				}
			} else {
				// entity.hurt(DamageSource.mobAttack(owner), 0.0001f);
				owner.getAIBrain().updateEntityRemembered((Entity) entity);
			}
			if (distSq < nearestHostileDistSq) {
				nearestHostileDistSq = distSq;
			}
			addRankInfo(distSq, hostiles.size());
			hostiles.add(entity);
		}

		public void setAvoidAttackDist(LivingEntity entity, double distSq) {
			if (distSq < attackableDistSq) {
				attackableDistSq = distSq;
			}
		}

		public void done() {
			Arrays.sort(rankList, 0, hostiles.size());
			owner.getAIBrain().calculateRiskScore(this, blockId, AIBattleSight.this);
		}

		private void addRankInfo(double distSq, int idx) {
			long rankInfo = ((long) distSq << 32L) + idx;
			if (idx < rankList.length) {
				rankList[idx] = rankInfo;
			} else {
				long[] newRankList = new long[Math.max(rankList.length, idx + 1) << 1];
				for (int i = 0; i < rankList.length; i++) {
					newRankList[i] = rankList[i];
				}
				newRankList[idx] = rankInfo;
				rankList = newRankList;
			}
		}

		public int getBlockId() {
			return blockId;
		}

		public List<LivingEntity> getHostiles() {
			return hostiles;
		}

		public LivingEntity getHostiles(int i) {
			return hostiles.get(i);
		}

		public long[] getRankData() {
			return rankList;
		}

		public void addRiskScore(float score) {
			riskScore += score;
		}

		public float getRiskScore() {
			return riskScore;
		}

		public int getFacingAngle() {
			return facingAngle;
		}

		public double getAttackableDistSq() {
			return attackableDistSq;
		}

		public double getNearestHostileDistSq() {
			return nearestHostileDistSq;
		}
	}
}