package com.giftialab.teyvatmod.entities.amber;

import java.util.ArrayList;
import java.util.EnumSet;
import org.apache.commons.compress.utils.Lists;

import com.giftialab.teyvatmod.TeyvatMod;
import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.ai.AITask;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class GoRoundInVillageGoal extends Goal {

	private CharacterEntity owner;
	
	private StructurePiece targetStructure = null;
	
	private int goRoundIdx = 0;
	private ArrayList<Integer> goRoundTargetList = Lists.newArrayList();
	
	public GoRoundInVillageGoal(CharacterEntity owner) {
		this.owner = owner;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		if (owner.getAIBrain().getRiskLevel() != RiskLevelEnum.PEACE) {
//			TeyvatMod.LOGGER.info(">>>>>>>>>>>>>>>>> exit 1");
			return false;
		}
		if (owner.getAITask().getTaskType() != AITask.TaskType.GUARD_VILLAGE) {
//			TeyvatMod.LOGGER.info(">>>>>>>>>>>>>>>>> exit 2");
			return false;
		}
		if (!owner.getAITask().getVillageStructure().isValid()) {
//			TeyvatMod.LOGGER.info(">>>>>>>>>>>>>>>>> exit 3");
			return false;
		}
		
		if (goRoundTargetList.isEmpty()) {
			for (int i = 0, sz = owner.getAITask().getVillageStructure().getPieces().size(); i < sz; ++i) {
				goRoundTargetList.add(i);
			}
		}
		
		if (targetStructure != null) {
			BlockPos pos = targetStructure.getLocatorPosition();
			if (owner.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 6 * 6) {
//				TeyvatMod.LOGGER.info(">>>>>>>>>>>>>>>>> exit 4 " + owner.blockPosition() + " " + owner.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()));
				targetStructure = null;
				return false;
			}
		} else {
			int step = goRoundTargetList.size() / 50;
			targetStructure = owner.getAITask().getVillageStructure().getPieces().get(goRoundTargetList.get(goRoundIdx));
			if ((goRoundIdx += (owner.getRandom().nextInt(step) + step)) >= goRoundTargetList.size()) {
				goRoundIdx = goRoundIdx % goRoundTargetList.size();
			}
		}

		Path targetPath = null;
		if (owner.getNavigation().isDone()) {
			BlockPos pos = targetStructure.getLocatorPosition();
			Vec3 targetPos = null;
			for (int t = 0; t < 5; ++t) {
				targetPos = LandRandomPos.getPosTowards(owner, 16, 6, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
				if (targetPos != null) {
					targetPath = owner.getNavigation().createPath(targetPos.x, targetPos.y, targetPos.z, 8);
					if (targetPath != null && targetPath.canReach()) {
						break;
					}
				}
			}
			if (targetPath == null) {
//				TeyvatMod.LOGGER.info(">>>>>>>>>>>>>>>>> exit 5 " + pos + owner.blockPosition());
				return false;
			}
			
//			for (int i = 0; i < targetPath.getNodeCount(); ++i) {
//				Node node = targetPath.getNode(i);
//				owner.getLevel().setBlockAndUpdate(node.asBlockPos().below(), Blocks.YELLOW_WOOL.defaultBlockState());
//			}
			owner.getNavigation().moveTo(targetPath, 1.0);
		}
		
		return true;
	}
	
	@Override
	public void start() {
//		TeyvatMod.LOGGER.info(">>>>>>>>>>> cur target " + targetStructure);
		owner.getNavigation().stop();
	}
	
	@Override
	public void stop() {
		owner.getNavigation().stop();
		targetStructure = null;
	}
	
}
