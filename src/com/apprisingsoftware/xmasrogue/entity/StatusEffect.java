package com.apprisingsoftware.xmasrogue.entity;

public class StatusEffect {
	
	public enum Type {
		
	}
	
	private final Type type;
	private final int maxDuration;
	private int duration;
	
	public StatusEffect(Type type, int duration) {
		this.type = type;
		this.maxDuration = this.duration = duration;
	}
	
	public Type getType() {
		return type;
	}
	public int getTurnsRemaining() {
		return duration;
	}
	public double getPercentageRemaining() {
		return (double) duration / maxDuration;
	}
	public void tick() {
		duration -= 1;
	}
	public boolean isActive() {
		return duration > 0;
	}
	
}
