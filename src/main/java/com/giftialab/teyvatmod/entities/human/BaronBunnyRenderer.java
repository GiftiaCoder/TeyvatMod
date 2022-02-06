package com.giftialab.teyvatmod.entities.human;

import com.giftialab.teyvatmod.TeyvatElements;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BaronBunnyRenderer extends LivingEntityRenderer<BaronBunnyEntity, BaronBunnyModel>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(TeyvatElements.MODID, "textures/entity/amber_skin.png");
	
	public BaronBunnyRenderer(EntityRendererProvider.Context context) {
		super(context, new BaronBunnyModel(context.bakeLayer(ModelLayers.PLAYER_SLIM)), 0.5f);
	}

	@Override
	protected void scale(BaronBunnyEntity p_115314_, PoseStack p_115315_, float p_115316_) {
		super.scale(p_115314_, p_115315_, p_115316_);
		p_115315_.scale(0.55f, 0.5f, 0.55f);
	}
	
	@Override
	protected void renderNameTag(BaronBunnyEntity p_114498_, Component p_114499_, PoseStack p_114500_,
			MultiBufferSource p_114501_, int p_114502_) {}
	
	@Override
	public ResourceLocation getTextureLocation(BaronBunnyEntity p_114482_) {
		return TEXTURE;
	}

}
