package com.giftialab.teyvatmod.entities.human;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EleArrow extends Arrow {
	
	private MethodHandle PROJECTILE_TICK;
	
	public EleArrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
		super(p_36858_, p_36859_);
		initProjectileTick();
	}
	public EleArrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
		super(p_36861_, p_36862_, p_36863_, p_36864_);
		initProjectileTick();
	}
	public EleArrow(Level p_36866_, LivingEntity p_36867_) {
		super(p_36866_, p_36867_);
		initProjectileTick();
	}

	private void initProjectileTick() {
		try {
			PROJECTILE_TICK = MethodHandles.lookup().findVirtual(Projectile.class, "tick", MethodType.methodType(void.class));
			PROJECTILE_TICK.bindTo(this);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void tick() {
		try {
			PROJECTILE_TICK.invoke();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		Vec3 vec3 = this.getDeltaMovement();
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			double d0 = vec3.horizontalDistance();
			this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
			this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
			this.yRotO = this.getYRot();
			this.xRotO = this.getXRot();
		}
		BlockPos blockpos = this.blockPosition();
		BlockState blockstate = this.level.getBlockState(blockpos);
		if (!blockstate.isAir()) {
			VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
			if (!voxelshape.isEmpty()) {
				Vec3 vec31 = this.position();
				for(AABB aabb : voxelshape.toAabbs()) {
					if (aabb.move(blockpos).contains(vec31)) {
						this.discard();
						return;
					}
				}
			}
		}
		if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
			// TODO clear fire element
		}
		// TODO set other elements
		Vec3 vec32 = this.position();
		Vec3 vec33 = vec32.add(vec3);
		HitResult hitresult = this.level.clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (hitresult.getType() != HitResult.Type.MISS) {
			vec33 = hitresult.getLocation();
		}
		
		while(!this.isRemoved()) {
			EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
			if (entityhitresult != null) {
				hitresult = entityhitresult;
			}
		
			if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
				Entity entity = ((EntityHitResult)hitresult).getEntity();
				Entity entity1 = this.getOwner();
				if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
					hitresult = null;
					entityhitresult = null;
				}
			}
		
			if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
				this.onHit(hitresult);
				this.hasImpulse = true;
			}
		
			if (entityhitresult == null || this.getPierceLevel() <= 0) {
				break;
			}
		
			hitresult = null;
		}

		vec3 = this.getDeltaMovement();
		double d5 = vec3.x;
		double d6 = vec3.y;
		double d1 = vec3.z;
		if (this.isCritArrow()) {
			for(int i = 0; i < 4; ++i) {
				this.level.addParticle(ParticleTypes.CRIT, this.getX() + d5 * (double)i / 4.0D, this.getY() + d6 * (double)i / 4.0D, this.getZ() + d1 * (double)i / 4.0D, -d5, -d6 + 0.2D, -d1);
			}
		}

		double d7 = this.getX() + d5;
		double d2 = this.getY() + d6;
		double d3 = this.getZ() + d1;
		double d4 = vec3.horizontalDistance();
		this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));

		this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
		this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
		this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
		if (this.isInWater()) {
			for(int j = 0; j < 4; ++j) {
				this.level.addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25D, d2 - d6 * 0.25D, d3 - d1 * 0.25D, d5, d6, d1);
			}
			this.setDeltaMovement(vec3.scale((double)this.getWaterInertia()));
		}

		
		if (!this.isNoGravity()) {
			Vec3 vec34 = this.getDeltaMovement();
			this.setDeltaMovement(vec34.x, vec34.y - (double)0.05F, vec34.z);
		}

		this.setPos(d7, d2, d3);
		this.checkInsideBlocks();
	}
	
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		this.discard();
	}
	
}
