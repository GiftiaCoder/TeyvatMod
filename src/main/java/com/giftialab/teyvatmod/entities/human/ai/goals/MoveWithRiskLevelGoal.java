package com.giftialab.teyvatmod.entities.human.ai.goals;

import java.util.EnumSet;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;
import com.giftialab.teyvatmod.util.QuickMath;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public abstract class MoveWithRiskLevelGoal extends Goal {

	protected CharacterEntity owner;
	private long updateCd, updateTime;
	private RiskLevelEnum riskLevel;
	
	public MoveWithRiskLevelGoal(CharacterEntity entity, RiskLevelEnum riskLevel, long updateCd) {
		this.owner = entity;
		this.riskLevel = riskLevel;
		this.updateCd = updateCd;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		return owner.getAIBrain().getRiskLevel() == riskLevel;
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
		Path path = owner.getNavigation().getPath();
		if (path == null || path.isDone() || owner.getLevel().getGameTime() >= updateTime) {
			path = findPath();
			if (path != null) {
				owner.getNavigation().moveTo(path, getMoveSpeed());
				updateTime = owner.getLevel().getGameTime() + updateCd;
			}
		}
	}

	protected abstract float getMoveSpeed();
	protected abstract Path findPath();
	
	protected Path createPath(AIBattleSight.SightBlock sightBlock, float distance) {
		double dirX = QuickMath.cosA(sightBlock.getFacingAngle()) * distance + owner.getX();
		double dirZ = QuickMath.sinA(sightBlock.getFacingAngle()) * distance + owner.getZ();
		double dirY = -1;
		for (double y = owner.getY() - distance, e = owner.getY() + distance; y <= e; ++y) {
			BlockPos pos = new BlockPos(dirX, y, dirZ);
			if (owner.getLevel().getBlockState(pos).isAir()) {
				continue;
			}
			pos = pos.above();
			if (!owner.getLevel().getBlockState(pos).isAir()) {
				continue;
			}
			pos = pos.above();
			if (!owner.getLevel().getBlockState(pos).isAir()) {
				continue;
			}
			dirY = y;
			break;
		}
		if (dirY < 0) {
			return null;
		}
		Path path = owner.getNavigation().createPath(dirX, dirY, dirZ, 1);
		if (path != null && path.canReach()) {
			return path;
		}
		return null;
	}
	
}
