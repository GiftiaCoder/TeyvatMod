package com.giftialab.teyvatmod;

import java.util.Set;

import org.apache.commons.compress.utils.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.giftialab.teyvatmod.entities.human.BaronBunnyEntity;
import com.giftialab.teyvatmod.entities.human.BaronBunnyRenderer;
import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.CharacterRenderer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TeyvatElements.MODID)
public class TeyvatMod {

    public static final Logger LOGGER = LogManager.getLogger();
    
    public TeyvatMod() {
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
    }
    
    private void setup(final FMLCommonSetupEvent event) {}
    private void enqueueIMC(final InterModEnqueueEvent event) {}
    private void processIMC(final InterModProcessEvent event) {}
    
    @Mod.EventBusSubscriber(modid=TeyvatElements.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	
    	@SubscribeEvent
    	public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
    		// TODO
    	}
    	
    	@SubscribeEvent
    	public static void onItemRegistry(final RegistryEvent.Register<Item> event) {
    		// TODO
    	}

    	@SubscribeEvent
    	public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
    		event.getRegistry().register(TeyvatElements.CHAR_AMBER_TYPE);
    		event.getRegistry().register(TeyvatElements.BARON_BUNNY_ENTITY_TYPE);
    	}
    	
    	@SubscribeEvent
    	public static void onEntityCreateAttrHandler(final EntityAttributeCreationEvent event) {
    		event.put(TeyvatElements.CHAR_AMBER_TYPE, CharacterEntity.createAttributes().build());
    		event.put(TeyvatElements.BARON_BUNNY_ENTITY_TYPE, BaronBunnyEntity.createAttributes().build());
    	}
    	
    }

    @Mod.EventBusSubscriber(modid=TeyvatElements.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
    public static class ClientRegistryEvents {
    	
    	@SubscribeEvent
        public static void onClientStarting(FMLClientSetupEvent event) {
        	EntityRenderers.<CharacterEntity>register(TeyvatElements.CHAR_AMBER_TYPE, CharacterRenderer::new);
        	EntityRenderers.<BaronBunnyEntity>register(TeyvatElements.BARON_BUNNY_ENTITY_TYPE, BaronBunnyRenderer::new);
        }
    	
    }

    @Mod.EventBusSubscriber(modid=TeyvatElements.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.DEDICATED_SERVER)
    public static class ServerRegistryEvents {
    	
    	@SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {}
    	
    	private static final Set<Class<? extends Entity>> ENEMY_SET = Sets.newHashSet(
    			Vindicator.class, Zombie.class);
    	
    	@SubscribeEvent
    	public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
    		if (ENEMY_SET.contains(event.getEntity().getClass())) {
    			((Mob) event.getEntity()).targetSelector.addGoal(2, new NearestAttackableTargetGoal<>((Mob) event.getEntity(), CharacterEntity.class, true));
    		}
    	}
    	
        @SubscribeEvent
    	public static void onEntityAttacked(final LivingAttackEvent event) {
    		if (event.getEntityLiving() instanceof CharacterEntity) {
//    			((CharacterEntity) event.getEntityLiving()).getAIBrain().onEntityAttacked(event.getSource().getEntity());
    		} else if (event.getSource().getEntity() instanceof CharacterEntity) {
//    			((CharacterEntity) event.getSource().getEntity()).getAIBrain().onAttackEntity(event.getEntity());
    		}
    	}
        
    }
    
}
