package com.giftialab.teyvatmod.entities.human.ai;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.giftialab.teyvatmod.util.DoubleBuffer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class PathFinderEx {
	
	public Path findPathTowards(PathfinderMob mob, int x, int y, int z, int nodeLimit) {
		List<Node>  nodeList = Lists.newArrayList();
		int dirX = x - (int) mob.getX(), dirY = y - (int) mob.getY(), dirZ = z - (int) mob.getZ();
		MutableBlockPos pos = new MutableBlockPos(mob.getX(), mob.getY() - 1, mob.getZ());
		
		Path path = new Path(nodeList, new BlockPos(x, y, z), false);
		return null;
	}
	
}
