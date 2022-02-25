//package com.giftialab.teyvatmod.entities.amber;
//
//import com.giftialab.teyvatmod.entities.human.ai.RiskLevelEnum;
//
//import net.minecraft.world.entity.ai.goal.Goal;
//
//public class AmberDropBaronBunnyGoal extends Goal {
//
//	private AmberEntity amber;
//	private long invokeTime = 0;
//	
//	public AmberDropBaronBunnyGoal(AmberEntity entity) {
//		this.amber = entity;
//	}
//	
//	@Override
//	public boolean canUse() {
//		if (amber.getAIBrain().getRiskLevel() == RiskLevelEnum.MID || amber.getAIBrain().getRiskLevel() == RiskLevelEnum.HIGH) {
//			if (invokeTime < amber.getLevel().getGameTime()) {
//				amber.dropBaronBunny();
//				invokeTime = amber.getLevel().getGameTime() + 200;
//			}
//		}
//		return false;
//	}
//	
//}
