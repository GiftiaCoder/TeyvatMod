package com.giftialab.teyvatmod.entities.hutao;

import com.giftialab.teyvatmod.TeyvatMod;
import com.giftialab.teyvatmod.entities.amber.TravelToVillageGoal;
import com.giftialab.teyvatmod.entities.human.CharacterEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class HutaoEntity extends CharacterEntity {

	public HutaoEntity(EntityType<? extends CharacterEntity> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		goalSelector.addGoal(5, new TravelToVillageGoal(this, 1.0));
	}
	
	private boolean flag = true;
	
	@Override
	public void tick() {
		// TODO Auto-generated method stub
		super.tick();
		
		if (flag && !level.isClientSide) {
			flag = test();
		}
	}
	
	private boolean test() {
		// TODO
		
		ServerLevel slvl = (ServerLevel) level;
		for (int i = -6; i <= 6; ++i) {
			for (int j = -6; j <= 6; ++j) {
				StructureStart<?> structure = slvl.structureFeatureManager().getStructureWithPieceAt(blockPosition().offset(i * 16, 0, j * 16), StructureFeature.VILLAGE);
				if (!structure.isValid()) {
					continue;
				}
				TeyvatMod.LOGGER.info("====================================");
				TeyvatMod.LOGGER.info(blockPosition().offset(i * 16, 0, j * 16));
				TeyvatMod.LOGGER.info(structure);
				TeyvatMod.LOGGER.info(structure.isValid());
				TeyvatMod.LOGGER.info(structure.getPieces().size());
				int k = 0;
				for (StructurePiece piece : structure.getPieces()) {
					TeyvatMod.LOGGER.info("\t" + k + "\t" + piece);
					if (piece instanceof PoolElementStructurePiece) {
						TeyvatMod.LOGGER.info("\t" + k + "\t" + ((PoolElementStructurePiece) piece).getElement());
						TeyvatMod.LOGGER.info("\t" + k + "\t" + ((PoolElementStructurePiece) piece).getElement().getType());
						if (((PoolElementStructurePiece) piece).getElement() instanceof LegacySinglePoolElement) {
							//((LegacySinglePoolElement) ((PoolElementStructurePiece) piece).getElement()).getType();
						}
					}
					TeyvatMod.LOGGER.info("\t" + k + "\t" + piece.getBoundingBox());
					TeyvatMod.LOGGER.info("\t" + k + "\t" + piece.getType());
					TeyvatMod.LOGGER.info("\t" + k + "\t" + piece.getLocatorPosition());
					TeyvatMod.LOGGER.info("");
					++k;
				}
				TeyvatMod.LOGGER.info(structure.getFeature());
				TeyvatMod.LOGGER.info(structure.getBoundingBox());
				return false;
			}
		}
		return true;
	}
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("teyvatmod", "textures/entity/hutao_skin.png");
	@Override
	public ResourceLocation getTexture() {
		return TEXTURE;
	}

}
