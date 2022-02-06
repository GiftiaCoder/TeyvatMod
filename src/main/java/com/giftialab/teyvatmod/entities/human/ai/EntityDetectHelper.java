package com.giftialab.teyvatmod.entities.human.ai;

import java.util.ArrayList;
import java.util.List;
import com.giftialab.teyvatmod.entities.human.CharacterEntity;

import net.minecraft.world.entity.Entity;

public class EntityDetectHelper extends AIUpdater {

	private static List<Entity> EMPTY_ENTITIES = new ArrayList<>();
	
	public EntityDetectHelper(CharacterEntity owner, int updateTick) {
		super(updateTick);
		this.owner = owner;
		this.entities = EMPTY_ENTITIES;
	}
	
	@Override
	public void onUpdate() {
//  this.getEntities().get(p_46537_, (p_151522_) -> {
//      if (p_151522_ != p_46536_ && p_46538_.test(p_151522_)) {
//         list.add(p_151522_);
//      }
//
//      if (p_151522_.isMultipartEntity()) {
//         for(net.minecraftforge.entity.PartEntity<?> enderdragonpart : p_151522_.getParts()) {
//            if (p_151522_ != p_46536_ && p_46538_.test(enderdragonpart)) {
//               list.add(enderdragonpart);
//            }
//         }
//      }
//
//   });
		entities = owner.getLevel().getEntities(owner, owner.getBoundingBox().inflate(32, 24, 32));
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	private CharacterEntity owner;
	private List<Entity> entities;
	
}
