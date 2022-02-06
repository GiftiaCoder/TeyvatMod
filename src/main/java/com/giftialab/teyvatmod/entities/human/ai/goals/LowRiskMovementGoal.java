package com.giftialab.teyvatmod.entities.human.ai.goals;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;
import net.minecraft.world.level.pathfinder.Path;

public class LowRiskMovementGoal extends MoveWithRiskLevelGoal {

	private ArcherAttackTargetGoal attackGoal;
	private float expectedAttackRangeSq;
	
	public LowRiskMovementGoal(CharacterEntity entity, long updateCd, float expectedAttackRange, ArcherAttackTargetGoal attackGoal) {
		super(entity, RiskLevelEnum.LOW, updateCd);
		this.attackGoal = attackGoal;
		this.expectedAttackRangeSq = expectedAttackRange * expectedAttackRange;
	}

	@Override
	public void tick() {
		if (owner.getTarget() == null || !owner.getTarget().isAlive()) {
			super.tick();
		} else {
			owner.getNavigation().stop();
			double distSq = owner.distanceToSqr(owner.getTarget());
			float rightSpeed = 0;
			ArcherAttackTargetGoal.AttackFailReason reason = attackGoal.getAttackFailReason();
//			if (reason == ArcherAttackTargetGoal.AttackFailReason.NOT_FACING) {
//				float rot = owner.getTarget().getYRot() - owner.getYHeadRot();
//				rightSpeed = (QuickMath.cosA(rot) > 0) ? 0.6f : -0.6f;
//			} else
			if (reason == ArcherAttackTargetGoal.AttackFailReason.BLOCK_OUT || reason == ArcherAttackTargetGoal.AttackFailReason.UNSEEN) {
				rightSpeed = (owner.getLevel().getGameTime() & 1) == 0 ? 0.4f : -0.4f;
			}
			owner.getMoveControl().strafe(distSq < expectedAttackRangeSq ? -0.4f : 0.4f, rightSpeed);
		}
	}
	
	@Override
	protected Path findPath() {
		int curTime = (int) owner.getLevel().getGameTime();
		return createPath(owner.getSight().getSightBlock(curTime % AIBattleSight.SIGHT_BLOCK_NUM), curTime % 6 + 4.0f);
	}
	
	@Override
	protected float getMoveSpeed() {
		return 0.8f;
	}

}
