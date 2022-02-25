package com.giftialab.teyvatmod.entities.human.ai;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;
import com.giftialab.teyvatmod.util.QuickMath;

import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.pathfinder.Path;

public class MovingHelper {
	
	public static Path createPath(CharacterEntity owner, int angle, float distance) {
		double dirX = QuickMath.cosA(angle) * distance + owner.getX();
		double dirZ = QuickMath.sinA(angle) * distance + owner.getZ();
		double dirY = owner.getLevel().getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int) dirX, (int) dirZ);
		Path path = owner.getNavigation().createPath(dirX, dirY, dirZ, 1);
		if (path != null && path.canReach()) {
			return path;
		}
		return null;
	}
	
}
