package com.giftialab.teyvatmod.entities.human;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharacterModel extends HumanoidModel<CharacterEntity> {

	public CharacterModel(ModelPart modelPart) {
		super(modelPart);
	}
	
	@Override
	public void prepareMobModel(CharacterEntity p_102861_, float p_102862_, float p_102863_, float p_102864_) {
		this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
		this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
		ItemStack itemstack = p_102861_.getItemInHand(InteractionHand.MAIN_HAND);
		if (itemstack.is(Items.BOW) && p_102861_.isAggressive()) {
			if (p_102861_.getMainArm() == HumanoidArm.RIGHT) {
				this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			} else {
				this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
			}
		}
		super.prepareMobModel(p_102861_, p_102862_, p_102863_, p_102864_);
	}

}
