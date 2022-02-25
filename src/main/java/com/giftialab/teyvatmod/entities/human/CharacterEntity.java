package com.giftialab.teyvatmod.entities.human;

import com.giftialab.teyvatmod.entities.human.ai.AIBattleSight;
import com.giftialab.teyvatmod.entities.human.ai.AIBrain;
import com.giftialab.teyvatmod.entities.human.ai.AITask;
import com.giftialab.teyvatmod.entities.human.ai.CharacterTargetClassifier;
import com.giftialab.teyvatmod.entities.human.ai.EntityDetectHelper;
import com.giftialab.teyvatmod.util.UpdatableDoubleBuffer;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public abstract class CharacterEntity extends AgeableMob {

	protected AIBrain aibrain;
	protected EntityDetectHelper detecter;
	protected UpdatableDoubleBuffer<AIBattleSight> sight;
	protected AITask aiTask;
	
	public CharacterEntity(EntityType<? extends CharacterEntity> type, Level level) {
		super(type, level);
		aibrain = new AIBrain(this, new CharacterTargetClassifier(this));
		detecter = new EntityDetectHelper(this, 20 * 5);
		sight = new UpdatableDoubleBuffer<AIBattleSight>(new AIBattleSight(this, 3), new AIBattleSight(this, 3));
		aiTask = new AITask(this, 20);
	}
	
	@Override
	public void aiStep() {
		if (!level.isClientSide) {
			detecter.update();
			sight.update();
			aibrain.update();
			aiTask.update();
			
//			String drawMsg = "";
//			drawMsg += getAITask().getTaskType();
//			drawMsg += ",";
//			drawMsg += getAIBrain().getRiskLevel();
//			drawMsg += ",";
//			drawMsg += (int) getAIBrain().getTotalRiskScore();
//			this.setCustomName(new TextComponent(drawMsg));
		}
		super.aiStep();
	}
	
	public EntityDetectHelper getDetecter() { return detecter; }
	public AIBattleSight getSight() { return sight.get(); }
	public AIBrain getAIBrain() { return aibrain; }
	public AITask getAITask() { return aiTask; }
	
	@Override
	public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
		return null;
	}
	
	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 25.0f).add(Attributes.MOVEMENT_SPEED, 0.25f).add(Attributes.ATTACK_DAMAGE, 1.0f);
	}
	
	public abstract ResourceLocation getTexture();
	
}