package com.giftialab.teyvatmod.entities.human;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharacterRenderer extends LivingEntityRenderer<CharacterEntity, CharacterModel> {
	
	public CharacterRenderer(EntityRendererProvider.Context context) {
		super(context, new CharacterModel(context.bakeLayer(ModelLayers.PLAYER_SLIM)), 0.9f);
		// addLayer(new HumanoidArmorLayer<>(this, new CharacterModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new CharacterModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR))));
		addLayer(new ItemInHandLayer<>(this));
	}
	
	@Override
	protected void renderNameTag(CharacterEntity p_114498_, Component p_114499_, PoseStack p_114500_, MultiBufferSource p_114501_, int p_114502_) {
		double d0 = this.entityRenderDispatcher.distanceToSqr(p_114498_);
		if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_114498_, d0)) {
			float f = p_114498_.getBbHeight() + 0.5F;
			int i = "deadmau5".equals(p_114499_.getString()) ? -10 : 0;
			p_114500_.pushPose();
			p_114500_.translate(0.0D, (double)f, 0.0D);
			p_114500_.mulPose(this.entityRenderDispatcher.cameraOrientation());
			p_114500_.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = p_114500_.last().pose();
			Font font = this.getFont();
			
			float healthRate = p_114498_.getHealth() / p_114498_.getMaxHealth();
			StringBuilder drawMsgBuf = new StringBuilder("OOOOOOOOOO");
			int healthSize = (int) (healthRate * 10);
			for (int msgIdx = 0; msgIdx < healthSize; ++msgIdx) {
				drawMsgBuf.setCharAt(msgIdx, '@');
			}
			String drawMsg = drawMsgBuf.toString();
			var name = p_114498_.getCustomName();
			if (name != null && name.getContents() != null) {
				drawMsg = name.getContents();
			} else {
				drawMsg = "(null)";
			}
			float f2 = (float)(-font.width(drawMsg) / 2);
			
			int color = (255 << 24) + ((int) ((1.0f - healthRate) * 255 + 0.5) << 16) + ((int) (healthRate * 255 + 0.5) << 8);
			font.drawInBatch(drawMsg, f2, (float)i, color, false, matrix4f, p_114501_, false, 0, p_114502_);
			p_114500_.popPose();
		}
	}
	
	@Override
	public ResourceLocation getTextureLocation(CharacterEntity entity) {
		return entity.getTexture();
	}

}
