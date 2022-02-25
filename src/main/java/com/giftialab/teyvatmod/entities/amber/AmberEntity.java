package com.giftialab.teyvatmod.entities.amber;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.EleArrow;
import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class AmberEntity extends CharacterEntity {

//	private long fireyRainEndTime;
//	private AmberAttackTargetGoal attackTargetGoal;
	
	public AmberEntity(EntityType<AmberEntity> type, Level level) {
		super(type, level);
		this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
//		fireyRainEndTime = 0;
	}

	@Override
	public boolean removeWhenFarAway(double p_21542_) {
		return false;
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));

		// normal move goals
		this.goalSelector.addGoal(7, new TravelToVillageGoal(this, 1.0));
		this.goalSelector.addGoal(7, new GoRoundInVillageGoal(this));
		// this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		
		// this.goalSelector.addGoal(5, new AmberDropBaronBunnyGoal(this));
		// this.goalSelector.addGoal(5, new AmberSkillFireyRainGoal(this, 100));

		new AmberGuardVillageGoalPackage(this).registerGoals();
		
	}
	
	@Override
	public void tick() {
//		if (!level.isClientSide) {
//			if (level.getGameTime() < fireyRainEndTime) {
//				spawnFireyRain();
//			}
//		}
		super.tick();
	}
	
//	public void dropBaronBunny() {
//		getLevel().addFreshEntity(new BaronBunnyEntity(TeyvatElements.BARON_BUNNY_ENTITY_TYPE, getLevel(), this));
//	}
	
//	public void activeFireyRain() {
//		fireyRainEndTime = level.getGameTime() + 20;
//	}
//	private void spawnFireyRain() {
//		for (int i = 0; i < 10; ++i) {
//			float range = random.nextInt(4) + 2;
//			float angle = random.nextInt(360);
//			float delX = range * QuickMath.cosA(angle);
//			float delZ = range * QuickMath.sinA(angle);
//			// SmallFireball fireBoll = new SmallFi
//			Arrow arrow = new Arrow(level, this);
//			arrow.setPos(getX() + delX, getY() + 6.0D, getX() + delZ);
//			arrow.shoot(0.0D, -1.0D, 0.0D, 100.0F, 0.0F);
//			arrow.setSecondsOnFire(100);
//			level.addFreshEntity(arrow);
//		}
//	}

	private static final ResourceLocation TEXTURE = new ResourceLocation("teyvatmod", "textures/entity/amber_skin.png");
	@Override
	public ResourceLocation getTexture() {
		return TEXTURE;
	}

	public void shootTarget(float chargeTimeTick) {
		shootTarget(chargeTimeTick, getTarget());
	}
	public void shootTarget(float chargeTimeTick, Entity target) {
		// EleArrow arrow = new EleArrow(level, this);
		Arrow arrow = new Arrow(level, this);
		// TODO arrow.setEffectsFromItem(p_40514_);
		// arrow.setEnchantmentEffectsFromEntity(p_37301_, p_37303_);
		
		float velH = 2f;
		
		float d0 = (float) (target.getX() - getX());
		float distV = (float) (target.getEyeY() - arrow.getY());
		float d2 = (float) (target.getZ() - getZ());
		float distH = Mth.sqrt(d0 * d0 + d2 * d2);

		// calcuate delta
		float estimateTime = distH / velH;
		d0 += (target.getDeltaMovement().x * estimateTime);
		d2 += (target.getDeltaMovement().z * estimateTime);
		
		// float alpha = 0.0075F * distH;
		// float velV = (alpha - Mth.sqrt((alpha * alpha) + (4 * d1))) / 2;
		float velV = (distV * velH / distH) + (0.05F * distH) / (2.0F * velH);
		float velocity = Mth.sqrt(velH * velH + velV * velV);
		arrow.shoot(d0, velV * distH / velH, d2, velocity, Math.max(0.01F, 1.0F - 0.1F * chargeTimeTick));
		arrow.setBaseDamage(arrow.getBaseDamage() + Math.min(chargeTimeTick, 30.0F) * 0.2D);
		if (chargeTimeTick > 10.0F) {
			arrow.setSecondsOnFire(100);
		}
		playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
		getLevel().addFreshEntity(arrow);
	}
	
}
			