package com.giftialab.teyvatmod.entities.human;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class BaronBunnyEntity extends Mob {
	
//	private static final Pose[] POSE_LIST = new Pose[] {
//			Pose.FALL_FLYING, Pose.STANDING, Pose.FALL_FLYING, Pose.STANDING,
//	};
//	private static final long[] POST_TIME_LIST = new long[] {
//			15, 8, 15, 8,
//	};
	
//	private long nextChangePoseTime;
//	private int poseIndex = 0;
	private long surviveTime = -1;
	private Entity master = null;
	
	public BaronBunnyEntity(EntityType<? extends Mob> type, Level level) {
		super(type, level);
	}

	public BaronBunnyEntity(EntityType<? extends Mob> type, Level level, Entity master) {
		this(type, level);
		setPos(master.getX(), master.getY(), master.getZ());
		this.master = master;
	}
	
	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 0.0);
	}
	
	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new Goal() {
			private long invokeTime = 0;
			
			@Override
			public boolean canUse() {
				if (invokeTime < getLevel().getGameTime()) {
					invoke();
					invokeTime = getLevel().getGameTime() + 10;
				}
				return false;
			}
			
			private void invoke() {
				int cnt = 0;
				for (Entity entity : getLevel().getEntities(BaronBunnyEntity.this, getBoundingBox().inflate(8))) {
					if (entity instanceof Mob && ((Mob) entity).getTarget() == master) {
						((Mob) entity).setTarget(BaronBunnyEntity.this);
						if (++cnt >= 3) {
							break;
						}
					}
				}
			}
		});
	}
	
	@Override
	public void tick() {
//		if (getLevel().getGameTime() > nextChangePoseTime) {
//			nextChangePoseTime = getLevel().getGameTime() + POST_TIME_LIST[poseIndex];
//			if (level.isClientSide) {
//				setPose(POSE_LIST[poseIndex]);
//			}
//			getJumpControl().jump();
//			if (++poseIndex >= POSE_LIST.length) {
//				poseIndex = 0;
//				setYRot(rotate(Rotation.CLOCKWISE_180));
//			}
//		}
		super.tick();
		
		if (surviveTime == -1) {
			surviveTime = level.getGameTime() + 100;
		} else if (surviveTime < level.getGameTime()) {
			explode();
		}
	}
	
	@Override
	public void die(DamageSource p_21014_) {
		super.die(p_21014_);
		explode();
	}
	
	private void explode() {
		if (master != null) {
			level.explode(master, getX(), getY(), getZ(), 2.0f, Explosion.BlockInteraction.NONE);
		} else {
			level.explode(this, getX(), getY(), getZ(), 2.0f, Explosion.BlockInteraction.NONE);
		}
		discard();
	}
	
}
