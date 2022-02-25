package com.giftialab.teyvatmod.util;

import net.minecraft.world.entity.Entity;

public class Util {

	public static long getChunkSign(Entity entity) {
		return (((long) entity.getX()) >> 4) | ((((long) entity.getZ()) >> 4) << 32);
	}
	
}
