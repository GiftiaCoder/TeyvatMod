package com.giftialab.teyvatmod.entities.human.ai;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.util.Util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class AITask extends AIUpdater {

	public static enum TaskType {
		TASK_INIT {
			@Override
			public TaskType getNextType(CharacterEntity owner, AITask task) {
				if (task.findVillage()) {
					return GUARD_VILLAGE;
				} else {
					return this;
				}
			}
		},
		GUARD_VILLAGE {
			@Override
			public TaskType getNextType(CharacterEntity owner, AITask task) {
				// TODO
				return this;
			}
		},
		ATTACK_REGION;
		
		public TaskType getNextType(CharacterEntity owner, AITask task) { return this; }
		
		public void start(CharacterEntity owner, AITask task) {}
		public void stop(CharacterEntity owner, AITask task) {}
		public void update(CharacterEntity owner, AITask task) {}
		
	}
	
	private CharacterEntity owner;
	private TaskType type = TaskType.TASK_INIT;
	
	private BlockPos villagePos = null;
	private long lastStartMovingTime = Long.MAX_VALUE;
	
	private long lastVillageStructureFindingChunkSign = -1;
	private StructureStart<?> villageStructure = StructureStart.INVALID_START;
	
	public AITask(CharacterEntity owner, int updateCd) {
		super(updateCd);
		this.owner = owner;
	}

	@Override
	public void onUpdate() {
		TaskType nextType = type.getNextType(owner, this);
		if (nextType != type) {
			type.stop(owner, this);
			nextType.start(owner, this);
			type = nextType;
		}
		type.update(owner, this);
		
		if (!owner.getNavigation().isInProgress()) {
			lastStartMovingTime = owner.getLevel().getGameTime() + 100;
		}
		
		// get village structure
		long chunkSign = Util.getChunkSign(owner);
		if (!villageStructure.isValid() && type == TaskType.GUARD_VILLAGE && chunkSign != lastVillageStructureFindingChunkSign) {
			villageStructure = ((ServerLevel) owner.getLevel()).structureFeatureManager().getStructureAt(owner.blockPosition(), StructureFeature.VILLAGE);
		} else {
			lastVillageStructureFindingChunkSign = chunkSign;
		}
	}
	
	public TaskType getTaskType() { return type; }
	
	public boolean needHaveRest() {
		return owner.getLevel().getGameTime() > lastStartMovingTime;
	}
	
	public BlockPos getVillagePos() { return villagePos; }
	public void updateVillagePos(BlockPos pos) { if (pos != null) villagePos = pos; }
	public StructureStart<?> getVillageStructure() { return villageStructure; }
	
	private boolean findVillage() {
		if (villagePos != null) {
			return true;
		} else {
			return (villagePos = findStructure(StructureFeature.VILLAGE)) != null;
		}
	}
	private BlockPos findStructure(StructureFeature<?> structFeature) {
		ServerLevel level = (ServerLevel) owner.getLevel();
		return level.getChunkSource().getGenerator().findNearestMapFeature(level, structFeature, owner.blockPosition(), 100, false);
	}
	
}
