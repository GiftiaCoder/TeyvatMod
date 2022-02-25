package com.giftialab.teyvatmod.entities.amber;

import java.util.List;
import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AITask.TaskType;
import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.MovingHelper;
import com.giftialab.teyvatmod.entities.human.ai.SenseAttackingGoal;
import com.giftialab.teyvatmod.entities.human.ai.SenseGoalPackage;
import com.giftialab.teyvatmod.entities.human.ai.SenseMovingGoal;
import com.giftialab.teyvatmod.util.QuickMath;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.pathfinder.Path;

public class AmberGuardVillageGoalPackage extends SenseGoalPackage {
	
	public AmberGuardVillageGoalPackage(AmberEntity owner) {
		super(owner, TaskType.GUARD_VILLAGE);
	}
	
	private static interface AngleFilter {
		public boolean accept(int angle);
	}
	private static interface EntitySelecter {
		public boolean select(LivingEntity entity, double distSq);
		public LivingEntity getTarget();
	}

	@Override
	public SenseMovingGoal createMovingGoal(CharacterEntity owner) {
		return new SenseMovingGoal(owner, this) {
			private boolean isStrafe = false;
			
			@Override
			protected void highTickMove() {
				if (owner.getNavigation().isInProgress()) {
					return;
				}
				
				float[] windowRiskScore = owner.getSight().getWindowRiskScore();
				AIBattleSight.SightBlock[] sightBlocks = owner.getSight().getSightBlocks();
				float minScore = Float.MAX_VALUE;
				Path minRiskPath = null;
				for (int i = 0; i < windowRiskScore.length; ++i) {
					float score = windowRiskScore[i];
					if (minScore > score) {
						Path path = MovingHelper.createPath(owner, sightBlocks[i].getFacingAngle(), 4);
						if (path != null) {
							minScore = score;
							minRiskPath = path;
						}
					}
				}
				if (minRiskPath != null) {
					owner.getNavigation().moveTo(minRiskPath, 1.2);
				}
			}

			@Override
			protected void lowTickMove() {
				if (owner.getTarget() != null && owner.getTarget().isAlive()) {
					if (!isStrafe) {
						owner.getNavigation().stop();
						isStrafe = true;
					}
					if (AmberGuardVillageGoalPackage.this.isSightBlocked()) {
						moveForAvoidBarrier();
					} else if (!AmberGuardVillageGoalPackage.this.isFacingTarget()) {
						moveForFacing();
					} else {
						moveForShoot();
					}
				} else {
					if (isStrafe) {
						isStrafe = false;
					}
					if (owner.getNavigation().isDone()) {
						moveFree();
					}
				}
			}
			
			private void moveFree() {
				float delX = (float) (owner.getAITask().getVillagePos().getX() - owner.getX());
				float delZ = (float) (owner.getAITask().getVillagePos().getZ() - owner.getZ());
				float distSq = delX * delX + delZ * delZ;
				float villageAngle = QuickMath.vecToAng(delX, delZ);
				
				Path minRiskPath = null;
				float minRiskScore = Float.MAX_VALUE;
				for (int i = 0; i < AIBattleSight.SIGHT_BLOCK_NUM; ++i) {
					float riskScore = owner.getSight().getWindowRiskScore()[i];
					if (distSq > 64 * 64) {
						float angDel = Math.abs(villageAngle - AIBattleSight.SIGHT_BLOCK_FACING[i]);
						if (angDel > 180) {
							angDel = 360 - angDel;
						}
						riskScore *= (2.0F - angDel / 180.0F);
					} else if (distSq > 24 * 24) {
						float angDel = Math.abs(villageAngle - AIBattleSight.SIGHT_BLOCK_FACING[i]);
						if (angDel > 180) {
							angDel = 360 - angDel;
						}
						if (90 < angDel) {
							continue;
						}
					}
					if (riskScore < minRiskScore) {
						Path path = MovingHelper.createPath(owner, AIBattleSight.SIGHT_BLOCK_FACING[i], owner.getRandom().nextInt(6) + 4);
						if (path != null) {
							minRiskPath = path;
						}
						minRiskScore = riskScore;
					}
				}
				if (minRiskPath != null) {
					owner.getNavigation().moveTo(minRiskPath, 1.0F);
				}
			}
			
			private void moveForAvoidBarrier() {
				doStrafe((int angle)->{
					return (30 < angle && angle < 150) || (210 < angle && angle < 330);
				});
				// doStrafe(0, ((owner.getLevel().getGameTime() / 17) & 1) - 0.5F);
			}
			private void moveForFacing() {
				doStrafe((int angle)->{
					return (60 < angle && angle < 120) || (240 < angle && angle < 300);
				});
				// doStrafe(0, ((owner.getLevel().getGameTime() / 17) & 1) - 0.5F);
			}
			private void moveForShoot() {
				double distSq = owner.distanceToSqr(owner.getTarget());
				if (distSq < 16 * 16) {
					doStrafe((int angle)->{
						return (90 < angle && angle < 150) || (210 < angle && angle < 270);
					});
					// doStrafe(0.5F, ((owner.getLevel().getGameTime() / 17) & 1) - 0.5F);
				} else if (distSq > 24 * 24) {
					doStrafe((int angle)->{
						return (30 < angle && angle < 90) || (270 < angle && angle < 330);
					});
					// doStrafe(-0.5F, ((owner.getLevel().getGameTime() / 17) & 1) - 0.5F);
				}
			}
			// private void doStrafe(float fv, float rv) {
			// 	if (AmberGuardVillageGoalPackage.this.isFacingTarget() && owner.getNavigation().isDone()) {
			// 		owner.getMoveControl().strafe(fv, rv);
			// 	}
			// }
			private void doStrafe(AngleFilter filter) {
				// if (!AmberGuardVillageGoalPackage.this.isFacingTarget() || !owner.getNavigation().isDone()) {
				// 	return;
				// }
				if (owner.getNavigation().isInProgress()) {
					return;
				}
				// int targetAngle = (int) QuickMath.vecToAng((float) (owner.getTarget().getX() - owner.getX()),
				// 		(float) (owner.getTarget().getZ() - owner.getZ()));
				int targetAngle = (int) (owner.getYRot() + 90);
				int selectedAngle = -1;
				float selectedRiskScore = Float.MAX_VALUE;
				for (int i = 0; i < AIBattleSight.SIGHT_BLOCK_NUM; ++i) {
					int angle = Math.abs(AIBattleSight.SIGHT_BLOCK_FACING[i] - targetAngle) % 360;
					if (filter.accept(angle) && owner.getSight().getWindowRiskScore()[i] < selectedRiskScore) {
						selectedAngle = AIBattleSight.SIGHT_BLOCK_FACING[i];
						selectedRiskScore = owner.getSight().getWindowRiskScore()[i];
					}
				}
				if (selectedAngle != -1) {
					selectedAngle -= (owner.getYRot() + 90);
					owner.getMoveControl().strafe(QuickMath.cosA(selectedAngle), QuickMath.sinA(selectedAngle));
				}
			}
		};
	}

	@Override
	public SenseAttackingGoal createAttackingGoal(CharacterEntity owner) {
		return new SenseAttackingGoal(owner, this) {
			
			private long resetTargetTime = 0;
			
			@Override
			public void stop() {
				owner.setAggressive(false);
				owner.stopUsingItem();
			}
			
			@Override
			protected void lowTick() {
				long timeAfterAttackable = timeAfterAttackable(40);
				
				LivingEntity target = owner.getTarget();
				if (target == null || !target.isAlive()
						|| resetTargetTime < owner.getLevel().getGameTime()
						|| timeAfterAttackable > 20) {
					target = updateTarget();
				}
				owner.setTarget(target);
				
				if (target != null) {
					resetTargetTime = owner.getLevel().getGameTime() + 100;
					owner.setAggressive(true);
					owner.startUsingItem(InteractionHand.MAIN_HAND);
				} else {
					owner.setAggressive(false);
					owner.stopUsingItem();
					return;
				}

				double delX = target.getX() - owner.getX();
				double delZ = target.getZ() - owner.getZ();
				float angle = QuickMath.vecToAng((float) delX, (float) delZ);
				float angDel = Math.abs(angle - owner.getAIBrain().getFacingAngle()) % 360;
				owner.getLookControl().setLookAt(target);
				if (30 < angDel && angDel < 330) {
					AmberGuardVillageGoalPackage.this.setFacingTarget(false);
					return;
				}
				AmberGuardVillageGoalPackage.this.setFacingTarget(true);
				
				if (timeAfterAttackable < 0) {
					return;
				}
				if (!owner.getSensing().hasLineOfSight(target)) {
					AmberGuardVillageGoalPackage.this.setSightBlocked(true);
					return;
				}
				AIBattleSight.SightBlock sightBlock = owner.getSight().getSightBlockByAngle((int) angle);
				if (sightBlock.getAttackableDistSq() < (delX * delX + delZ * delZ)) {
					AmberGuardVillageGoalPackage.this.setSightBlocked(true);
					return;
				}
				AmberGuardVillageGoalPackage.this.setSightBlocked(false);
				
				((AmberEntity) owner).shootTarget(timeAfterAttackable(10), target);
				resetAttackTime();
			}
			
			@Override
			protected void highTick() {
				owner.setAggressive(true);
				owner.startUsingItem(InteractionHand.MAIN_HAND);
				
				long timeAfterAttackable = timeAfterAttackable(10);
				if (timeAfterAttackable < 0) {
					return;
				}
				AIBattleSight.SightBlock facingBlock = owner.getSight().getSightBlockByAngle((int) owner.getAIBrain().getFacingAngle());
				
				List<LivingEntity> hostiles = facingBlock.getHostiles();
				if (hostiles.isEmpty()) {
					return;
				}
				LivingEntity hostile = hostiles.get((int) facingBlock.getRankData()[0]);
				owner.setTarget(hostile);
				((AmberEntity) owner).shootTarget(timeAfterAttackable, hostile);
				resetAttackTime();
			}
			
			private LivingEntity updateTarget() {
				LivingEntity target = findDangerTarget();
				if (target == null) {
					target = findExpectedTarget();
				}
				return target;
			}
			
			private LivingEntity findDangerTarget() {
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
			
			private LivingEntity findExpectedTarget() {
				EntitySelecter selecter = new EntitySelecter() {
					public LivingEntity expectedTarget = null;
					public float expectedTargetScore = 0;
					public int expectedTargetCnt = 0;
					
					@Override
					public boolean select(LivingEntity entity, double distSq) {
						float targetScore = (float) (entity.getAttributeValue(Attributes.ATTACK_DAMAGE) / (entity.getHealth() + distSq + 1));
						if (expectedTargetScore < targetScore) {
							expectedTargetScore = targetScore;
							expectedTarget = entity;
							++expectedTargetCnt;
						}
						if (++expectedTargetCnt > 20) {
							return true;
						}
						return false;
					}

					@Override
					public LivingEntity getTarget() {
						return expectedTarget;
					}
					
				};
				int facingBlockId = AIBattleSight.getBlockId((int) owner.getAIBrain().getFacingAngle());
				if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId), selecter)) {
					return selecter.getTarget();
				}
				for (int i = 1, e = AIBattleSight.SIGHT_BLOCK_NUM >> 1; i < e; ++i) {
					if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId + i), selecter)) {
						return selecter.getTarget();
					}
					if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId - i), selecter)) {
						return selecter.getTarget();
					}
				}
				if (findExpectedTarget(owner.getSight().getSightBlock(facingBlockId + (AIBattleSight.SIGHT_BLOCK_NUM >> 1)), selecter)) {
					return selecter.getTarget();
				}
				return selecter.getTarget();
			}
			
			private boolean findExpectedTarget(AIBattleSight.SightBlock sightBlock, EntitySelecter selecter) {
				double limitAttackRangeSq = sightBlock.getAttackableDistSq();
				List<LivingEntity> hostiles = sightBlock.getHostiles();
				long[] hostileRankList = sightBlock.getRankData();
				for (int i = 0; i < hostiles.size(); ++i) {
					long rankData = hostileRankList[0];
					double distSq = rankData >> 32;
					if (distSq >= limitAttackRangeSq) {
						break;
					}
					
					if (selecter.select((LivingEntity) hostiles.get((int) rankData), distSq)) {
						return true;
					}
				}
				return false;
			}
			
		};
	}

}
