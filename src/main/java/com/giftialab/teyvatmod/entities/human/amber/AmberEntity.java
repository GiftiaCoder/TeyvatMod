package com.giftialab.teyvatmod.entities.human.amber;

import com.giftialab.teyvatmod.TeyvatElements;
import com.giftialab.teyvatmod.entities.human.BaronBunnyEntity;
import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.util.QuickMath;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class AmberEntity extends CharacterEntity {

	private long fireyRainEndTime;
	
	public AmberEntity(EntityType<AmberEntity> type, Level level) {
		super(type, level);
		
		fireyRainEndTime = 0;
	}

	@Override
	protected void registerGoals() {
		// this.goalSelector.addGoal(5, new AmberDropBaronBunnyGoal(this));
		// this.goalSelector.addGoal(5, new AmberSkillFireyRainGoal(this, 100));
		super.registerGoals();
	}
	
	@Override
	public void tick() {
		if (!level.isClientSide) {
			if (level.getGameTime() < fireyRainEndTime) {
				spawnFireyRain();
			}
		}
		super.tick();
	}
	
	public void dropBaronBunny() {
		getLevel().addFreshEntity(new BaronBunnyEntity(TeyvatElements.BARON_BUNNY_ENTITY_TYPE, getLevel(), this));
	}
	
	public void activeFireyRain() {
		fireyRainEndTime = level.getGameTime() + 20;
	}
	private void spawnFireyRain() {
		for (int i = 0; i < 10; ++i) {
			float range = random.nextInt(4) + 2;
			float angle = random.nextInt(360);
			float delX = range * QuickMath.cosA(angle);
			float delZ = range * QuickMath.sinA(angle);
			// SmallFireball fireBoll = new SmallFi
			Arrow arrow = new Arrow(level, this);
			arrow.setPos(getX() + delX, getY() + 6.0D, getX() + delZ);
			arrow.shoot(0.0D, -1.0D, 0.0D, 100.0F, 0.0F);
			arrow.setSecondsOnFire(100);
			level.addFreshEntity(arrow);
		}
	}

	private static final ResourceLocation TEXTURE = new ResourceLocation("teyvatmod", "textures/entity/amber_skin.png");
	public ResourceLocation getTexture() {
		return TEXTURE;
	}
}
			