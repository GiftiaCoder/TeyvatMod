package com.giftialab.teyvatmod.entities.human.ai;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.google.common.collect.Sets;
import java.util.Set;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;

public class CharacterTargetClassifier implements ITargetClassifier {

	@SuppressWarnings("unchecked")
	private static final Set<Class<? extends Entity>> HOSTILE_CLASS = Sets.newHashSet(new Class[] {
			Vindicator.class, Pillager.class,
			Zombie.class, ZombieVillager.class, ZombifiedPiglin.class, Husk.class, Drowned.class,
			Skeleton.class, Stray.class, WitherSkeleton.class,
			Spider.class,
			Creeper.class,
//			Slime.class
	});
	@SuppressWarnings("unchecked")
	private static final Set<Class<? extends Entity>> AVOID_ATTACK_CLASS = Sets.newHashSet(new Class[] {
			Villager.class, WanderingTrader.class,
			IronGolem.class, SnowGolem.class,
			Wolf.class,
	});
	
	@SuppressWarnings("unused")
	private LivingEntity owner;

	public CharacterTargetClassifier(LivingEntity entity) {
		owner = entity;
	}

	public boolean isHostile(LivingEntity entity) {
		return HOSTILE_CLASS.contains(entity.getClass());
	}

	public boolean avoidToAttack(LivingEntity entity) {
		if (entity instanceof CharacterEntity) {
			return true;
		}
		if (entity instanceof Player) {
			return !((Player)entity).isInvulnerable();
		}
		return AVOID_ATTACK_CLASS.contains(entity.getClass());
	}

}
