package com.giftialab.teyvatmod.entities.human;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EleArrowRenderer extends EntityRenderer<EleArrow> {
	public EleArrowRenderer(EntityRendererProvider.Context p_173917_) {
		super(p_173917_);
	}

	@Override
	public void render(EleArrow p_113839_, float p_113840_, float p_113841_, PoseStack p_113842_,
			MultiBufferSource p_113843_, int p_113844_) {
		p_113842_.pushPose();
		p_113842_.mulPose(
				Vector3f.YP.rotationDegrees(Mth.lerp(p_113841_, p_113839_.yRotO, p_113839_.getYRot()) - 90.0F));
		p_113842_.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(p_113841_, p_113839_.xRotO, p_113839_.getXRot())));
		float f9 = (float) p_113839_.shakeTime - p_113841_;
		if (f9 > 0.0F) {
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			p_113842_.mulPose(Vector3f.ZP.rotationDegrees(f10));
		}

		p_113842_.mulPose(Vector3f.XP.rotationDegrees(45.0F));
		p_113842_.scale(0.05625F, 0.05625F, 0.05625F);
		p_113842_.translate(-4.0D, 0.0D, 0.0D);
		VertexConsumer vertexconsumer = p_113843_
		 		.getBuffer(RenderType.entityCutout(this.getTextureLocation(p_113839_)));
		VertexConsumer leashVertex = p_113843_.getBuffer(RenderType.leash());
		PoseStack.Pose posestack$pose = p_113842_.last();
		Matrix4f matrix4f = posestack$pose.pose();
		Matrix3f matrix3f = posestack$pose.normal();
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, p_113844_);
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, p_113844_);
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, p_113844_);
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, p_113844_);
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, p_113844_);
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, p_113844_);
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, p_113844_);
		this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, p_113844_);

		for (int j = 0; j < 4; ++j) {
			p_113842_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, p_113844_);
			this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, p_113844_);
			this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, p_113844_);
			this.vertex(matrix4f, matrix3f, vertexconsumer, leashVertex, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, p_113844_);
		}

		p_113842_.popPose();
		super.render(p_113839_, p_113840_, p_113841_, p_113842_, p_113843_, p_113844_);
	}

	public void vertex(Matrix4f p_113826_, Matrix3f p_113827_, VertexConsumer p_113828_, VertexConsumer leashVertex, int p_113829_, int p_113830_,
			int p_113831_, float p_113832_, float p_113833_, int p_113834_, int p_113835_, int p_113836_,
			int p_113837_) {
		p_113828_.vertex(p_113826_, (float) p_113829_, (float) p_113830_, (float) p_113831_)
				.color(255, 255, 255, 255)
		 		.uv(p_113832_, p_113833_)
		 		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_113837_)
				.normal(p_113827_, (float) p_113834_, (float) p_113836_, (float) p_113835_)
				.endVertex();
		leashVertex.vertex(p_113826_, (float) p_113829_, (float) p_113830_, (float) p_113831_)
				.color(255, 255, 255, 255)
				.uv2(p_113837_)
				.endVertex();
	}
	
	@Override
	protected int getSkyLightLevel(EleArrow p_114509_, BlockPos p_114510_) {
		return 15;
	}
	
	@Override
	protected int getBlockLightLevel(EleArrow p_114496_, BlockPos p_114497_) {
		return 15;
	}
	
	public static final ResourceLocation SPECTRAL_ARROW_LOCATION = new ResourceLocation(
			"textures/entity/projectiles/spectral_arrow.png");

	@Override
	public ResourceLocation getTextureLocation(EleArrow p_114482_) {
		return SPECTRAL_ARROW_LOCATION;
	}
}
