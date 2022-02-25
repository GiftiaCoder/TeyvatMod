package com.giftialab.teyvatmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.giftialab.teyvatmod.entities.human.BaronBunnyEntity;
import com.giftialab.teyvatmod.entities.human.BaronBunnyRenderer;
import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.entities.human.CharacterRenderer;
import com.giftialab.teyvatmod.entities.human.EleArrow;
import com.giftialab.teyvatmod.entities.human.EleArrowRenderer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
    		event.getRegistry().register(TeyvatElements.CHAR_HUTAO_TYPE);
    		event.getRegistry().register(TeyvatElements.BARON_BUNNY_ENTITY_TYPE);
    		event.getRegistry().register(TeyvatElements.ENTITY_ELE_ARROW_TYPE);
    	}
    	
    	@SubscribeEvent
    	public static void onEntityCreateAttrHandler(final EntityAttributeCreationEvent event) {
    		event.put(TeyvatElements.CHAR_AMBER_TYPE, CharacterEntity.createAttributes().build());
    		event.put(TeyvatElements.CHAR_HUTAO_TYPE, CharacterEntity.createAttributes().build());
    		event.put(TeyvatElements.BARON_BUNNY_ENTITY_TYPE, BaronBunnyEntity.createAttributes().build());
    	}
    	
    }

    @Mod.EventBusSubscriber(modid=TeyvatElements.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
    public static class ClientRegistryEvents {
    	
    	@SubscribeEvent
        public static void onClientStarting(FMLClientSetupEvent event) {
        	EntityRenderers.<CharacterEntity>register(TeyvatElements.CHAR_AMBER_TYPE, CharacterRenderer::new);
        	EntityRenderers.<CharacterEntity>register(TeyvatElements.CHAR_HUTAO_TYPE, CharacterRenderer::new);
        	EntityRenderers.<BaronBunnyEntity>register(TeyvatElements.BARON_BUNNY_ENTITY_TYPE, BaronBunnyRenderer::new);
        	EntityRenderers.<EleArrow>register(TeyvatElements.ENTITY_ELE_ARROW_TYPE, EleArrowRenderer::new);
        }
    	
    }

    @Mod.EventBusSubscriber(modid=TeyvatElements.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.DEDICATED_SERVER)
    public static class ServerRegistryEvents {
    	
    	@SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {}
    	
//    	private static final Set<Class<? extends Entity>> ENEMY_SET = Sets.newHashSet(
//    			Vindicator.class, Zombie.class);
        
    }
    
}
