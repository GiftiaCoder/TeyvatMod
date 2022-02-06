package com.giftialab.teyvatmod.entities.human.ai.goals;

import java.util.EnumSet;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;
import com.giftialab.teyvatmod.util.QuickMath;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.Arrow;

public class ArcherAttackTargetGoal extends Goal {

	public static enum AttackFailReason {
		UNSEEN,
		BLOCK_OUT,
		NOT_FACING,
		HIGH_RISK;
	}
	
	private CharacterEntity owner;
	private long nextAttackableTime = 0;
	private long cdTime;
	private AttackFailReason attackFailReason = null;
	
	public ArcherAttackTargetGoal(CharacterEntity entity, long cdTime) {
		this.owner = entity;
		this.nextAttackableTime = 0;
		this.cdTime = cdTime;
		// this.setFlags(EnumSet.of(Goal.Flag.JUMP));
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
		// this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}
	
	public AttackFailReason getAttackFailReason() {
		return attackFailReason;
	}
	
	@Override
	public boolean canUse() {
		if (owner.getTarget() != null && owner.getTarget().isAlive()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void start() {
		owner.setAggressive(true);
		owner.startUsingItem(InteractionHand.MAIN_HAND);
		nextAttackableTime = owner.getLevel().getGameTime() + cdTime;
	}
	
	@Override
	public void stop() {
		owner.setAggressive(false);
		owner.stopUsingItem();
	}
	
	@Override
	public void tick() {
		float delX = (float) (owner.getTarget().getX() - owner.getX());
		float delZ = (float) (owner.getTarget().getZ() - owner.getZ());
		boolean isFacing = (Math.abs(owner.getAIBrain().getFacingAngle() - QuickMath.vecToAng(delX, delZ)) % 360) <= AIBattleSight.SIGHT_BLOCK_HALF_ANGLE;
		
		if (isFacing) {
			float distSq = delX * delX + delZ * delZ;
			if (distSq < owner.getSight().getSightBlockByAngle((int) owner.getYHeadRot()).getAttackableDistSq()) {
				if (owner.getSensing().hasLineOfSight(owner.getTarget())) {
					RiskLevelEnum riskLevel = owner.getAIBrain().getRiskLevel();
					long attackTime = nextAttackableTime;
					if (riskLevel == RiskLevelEnum.LOW) {
						attackTime += 20;
					}
					if (owner.getLevel().getGameTime() >= attackTime) {
						performRangedAttack();
						nextAttackableTime = owner.getLevel().getGameTime() + cdTime;
						owner.stopUsingItem();
						owner.startUsingItem(InteractionHand.MAIN_HAND);
					}
					attackFailReason = null;
					return;
				} else {
					attackFailReason = AttackFailReason.UNSEEN;
				}
			} else {
				attackFailReason = AttackFailReason.BLOCK_OUT;
			}
		} else if (owner.getAIBrain().getRiskLevel() != RiskLevelEnum.HIGH) {
			// owner.lookAt(owner, 30.0F, 30.0F);
			owner.getLookControl().setLookAt(owner.getTarget());
			attackFailReason = AttackFailReason.NOT_FACING;
		} else {
			attackFailReason = AttackFailReason.HIGH_RISK;
		}
	}
	
	private void performRangedAttack() {
		Arrow arrow = new Arrow(owner.getLevel(), owner);
		// TODO arrow.setEffectsFromItem(p_40514_);
		// arrow.setEnchantmentEffectsFromEntity(p_37301_, p_37303_);
		
		float chargeTimeTick = owner.getLevel().getGameTime() - nextAttackableTime;
		
		Entity target = owner.getTarget();
		float d0 = (float) (target.getX() - owner.getX());
		float d1 = (float) (target.getY() - arrow.getY());
		float d2 = (float) (target.getZ() - owner.getZ());
		float distH = Mth.sqrt(d0 * d0 + d2 * d2);

		// calcuate delta
		float estimateTime = distH * 0.5F;
		d0 += (target.getDeltaMovement().x * estimateTime);
		d2 += (target.getDeltaMovement().z * estimateTime);
		
		float velV = (d1 / distH) + (0.0075F * distH);
		float velocity = Mth.sqrt(1.0F + velV * velV);
		arrow.shoot(d0, (velV * distH), d2, velocity * 2.0F, Math.max(0.05F, 4.0F - 0.4F * chargeTimeTick));
		arrow.setBaseDamage(arrow.getBaseDamage() + Math.min(chargeTimeTick, 20.0F) * 0.2D);
		if (chargeTimeTick > 10.0F) {
			arrow.setSecondsOnFire(100);
		}
		owner.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (owner.getRandom().nextFloat() * 0.4F + 0.8F));
		owner.getLevel().addFreshEntity(arrow);
	}
}