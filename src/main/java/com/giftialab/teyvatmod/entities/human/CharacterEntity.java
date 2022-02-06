package com.giftialab.teyvatmod.entities.human;

import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.AIBrain;
import com.giftialab.teyvatmod.entities.human.ai.CharacterTargetClassifier;
import com.giftialab.teyvatmod.entities.human.ai.EntityDetectHelper;
import com.giftialab.teyvatmod.entities.human.ai.goals.ArcherAttackTargetGoal;
import com.giftialab.teyvatmod.entities.human.ai.goals.ArcherFindTargetGoal;
import com.giftialab.teyvatmod.entities.human.ai.goals.HighRiskMovementGoal;
import com.giftialab.teyvatmod.entities.human.ai.goals.LowRiskMovementGoal;
import com.giftialab.teyvatmod.entities.human.ai.goals.MidRiskMovementGoal;
import com.giftialab.teyvatmod.util.UpdatableDoubleBuffer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class CharacterEntity extends AgeableMob {

	protected AIBrain aibrain;
	protected EntityDetectHelper detecter;
	// protected UpdatableDoubleBuffer<AISight> sight;
	protected UpdatableDoubleBuffer<AIBattleSight> sight;
	
	private ArcherAttackTargetGoal attackTargetGoal;
	
	public CharacterEntity(EntityType<? extends CharacterEntity> type, Level level) {
		super(type, level);
		aibrain = new AIBrain(this, new CharacterTargetClassifier(this));
		detecter = new EntityDetectHelper(this, 20 * 5);
		sight = new UpdatableDoubleBuffer<AIBattleSight>(new AIBattleSight(this, 3), new AIBattleSight(this, 3));
		
		this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
	}
	
	@Override
	public void aiStep() {
		if (!level.isClientSide) {
			detecter.update();
			sight.update();
			aibrain.update();			
//			String drawMsg = "";
//			drawMsg += attackTargetGoal.getAttackFailReason() != null ? attackTargetGoal.getAttackFailReason() : "(null)";
//			drawMsg += ",";
//			drawMsg += getAIBrain().getRiskLevel();
//			drawMsg += ",";
//			drawMsg += getAIBrain().getOrigRiskLevel();
//			drawMsg += ",";
//			drawMsg += (int) getAIBrain().getTotalRiskScore();
//			this.setCustomName(new TextComponent(drawMsg));
		}
		super.aiStep();
	}
	
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		
		attackTargetGoal = new ArcherAttackTargetGoal(this, 10);
		
		Goal lowRiskMovementGoal = new LowRiskMovementGoal(this, 10, 16.0f, attackTargetGoal);
		Goal highRiskMovementGoal = new HighRiskMovementGoal(this, 10, 6);
		this.goalSelector.addGoal(5, lowRiskMovementGoal);
		this.goalSelector.addGoal(5, highRiskMovementGoal);
		this.goalSelector.addGoal(5, new MidRiskMovementGoal(this, lowRiskMovementGoal, highRiskMovementGoal));
		this.goalSelector.addGoal(6, attackTargetGoal);
		
		// this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		// this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
		
		this.targetSelector.addGoal(6, new ArcherFindTargetGoal(this));
	}
	
	public EntityDetectHelper getDetecter() { return detecter; }
	public AIBattleSight getSight() { return sight.get(); }
	public AIBrain getAIBrain() { return aibrain; }
	
	@Override
	public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
		return null;
	}
	
	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 25.0f).add(Attributes.MOVEMENT_SPEED, 0.3f).add(Attributes.ATTACK_DAMAGE, 1.0f);
	}
	
	public abstract ResourceLocation getTexture();
	
}