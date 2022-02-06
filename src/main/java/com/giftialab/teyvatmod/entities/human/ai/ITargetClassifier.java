package com.giftialab.teyvatmod.entities.human.ai;

import net.minecraft.world.entity.LivingEntity;

public interface ITargetClassifier {

	public boolean isHostile(LivingEntity entity);

	public boolean avoidToAttack(LivingEntity entity);
	
}
