package com.giftialab.teyvatmod.entities.human.ai;

import com.giftialab.teyvatmod.entities.human.CharacterEntity;

public abstract class SenseGoalPackage {

	private CharacterEntity owner;
	private SenseMovingGoal moveGoal;
	private SenseAttackingGoal attackGoal;
	private AITask.TaskType sense;
	
	private boolean isFacingTarget = true;
	private boolean isSightBlocked = false;
	
	public SenseGoalPackage(CharacterEntity owner, AITask.TaskType sense) {
		this.owner = owner;
		this.sense = sense;
		this.moveGoal = createMovingGoal(owner);
		this.attackGoal = createAttackingGoal(owner);
	}
	
	public AITask.TaskType getSense() { return sense; }
	
	public boolean isFacingTarget() { return isFacingTarget; }
	public void setFacingTarget(boolean flag) { isFacingTarget = flag; }
	
	public boolean isSightBlocked() { return isSightBlocked; }
	public void setSightBlocked(boolean flag) { isSightBlocked = flag; }
	
	public abstract SenseMovingGoal createMovingGoal(CharacterEntity owner);
	public abstract SenseAttackingGoal createAttackingGoal(CharacterEntity owner);
	public void registerGoals() {
		owner.goalSelector.addGoal(5, moveGoal);
		owner.goalSelector.addGoal(5, attackGoal);
	}
	
}
