package com.giftialab.teyvatmod.entities.amber;

import java.util.EnumSet;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AITask;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Path;

public class TravelToVillageGoal extends Goal {
	
	private CharacterEntity owner;
	private double speed;
	private boolean goToVillage = true;
	
	public TravelToVillageGoal(CharacterEntity owner, double speed) {
		this.owner = owner;
		this.speed = speed;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		boolean ret = owner.getAIBrain().getRiskLevel() == RiskLevelEnum.PEACE
				&& owner.getAITask().getTaskType() == AITask.TaskType.GUARD_VILLAGE;
//				&& owner.getLevel().isDay();
		if (ret) {
			double delX = owner.getAITask().getVillagePos().getX() - owner.getX();
			double delZ = owner.getAITask().getVillagePos().getZ() - owner.getZ();
			double distSq = delX * delX + delZ * delZ;
			if (goToVillage) {
				if (distSq > 8 * 8) {
					return true;
				} else {
					goToVillage = false;
					return false;
				}
			} else {
				if (distSq > 64 * 64) {
					goToVillage = true;
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void start() {
		owner.getNavigation().stop();
	}
	
	@Override
	public void stop() {
		owner.getNavigation().stop();
	}
	
	@Override
	public void tick() {
		if (!owner.getNavigation().isInProgress()) {
			doMove();
		}
	}
	
	private void doMove() {
		BlockPos pos = owner.getAITask().getVillagePos();
		double delX = pos.getX() - owner.getX();
		double delZ = pos.getZ() - owner.getZ();
		delX += (owner.getRandom().nextDouble() - 0.5) * delX * 0.25;
		delZ += (owner.getRandom().nextDouble() - 0.5) * delZ * 0.25;
		double scale = Math.abs(delX) + Math.abs(delZ);
		if (scale > 16.0) {
			scale = 16.0 / scale;
			delX *= scale;
			delZ *= scale;
		}
		if (moveTo((int) delX, (int) delZ)) {
			return;
		}
		moveTo((int) ((owner.getRandom().nextDouble() - 0.3) * delX), (int) ((owner.getRandom().nextDouble() - 0.3) * delZ));
	}

	protected boolean moveTo(int x, int z) {
		int posX = (int) (owner.getX() + x);
		int posZ = (int) (owner.getZ() + z);
		int posY = owner.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, posX, posZ);
		Path path = owner.getNavigation().createPath(posX, posY, posZ, 1);
		if (path != null && path.canReach()) {
			owner.getNavigation().moveTo(path, speed);
			return true;
		} else {
			return false;
		}
	}
	
}

