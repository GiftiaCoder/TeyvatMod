package com.giftialab.teyvatmod;

import com.giftialab.teyvatmod.entities.human.BaronBunnyEntity;
import com.giftialab.teyvatmod.entities.human.EleArrow;
import com.giftialab.teyvatmod.entities.hutao.HutaoEntity;
import com.giftialab.teyvatmod.entities.amber.AmberEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class TeyvatElements {

	public static final String MODID = "teyvatmod";
	
	@SuppressWarnings("unchecked")
	public static final EntityType<AmberEntity> CHAR_AMBER_TYPE = (EntityType<AmberEntity>) EntityType.Builder.<AmberEntity>of(AmberEntity::new, MobCategory.CREATURE).sized(0.6F, 1.7F).build("char_amber").setRegistryName(new ResourceLocation(MODID, "char_amber"));
	@SuppressWarnings("unchecked")
	public static final EntityType<HutaoEntity> CHAR_HUTAO_TYPE = (EntityType<HutaoEntity>) EntityType.Builder.<HutaoEntity>of(HutaoEntity::new, MobCategory.CREATURE).sized(0.6F, 1.7F).build("char_hutao").setRegistryName(new ResourceLocation(MODID, "char_hutao"));
	@SuppressWarnings("unchecked")
	public static final EntityType<BaronBunnyEntity> BARON_BUNNY_ENTITY_TYPE = (EntityType<BaronBunnyEntity>) EntityType.Builder.<BaronBunnyEntity>of(BaronBunnyEntity::new, MobCategory.CREATURE).sized(0.3F, 0.8F).build("baron_bunny").setRegistryName(new ResourceLocation(MODID, "entity_baron_bunny"));
	@SuppressWarnings("unchecked")
	public static final EntityType<EleArrow> ENTITY_ELE_ARROW_TYPE = (EntityType<EleArrow>) EntityType.Builder.<EleArrow>of(EleArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("ele_arrow").setRegistryName(new ResourceLocation(MODID, "ele_arrow"));;
}
