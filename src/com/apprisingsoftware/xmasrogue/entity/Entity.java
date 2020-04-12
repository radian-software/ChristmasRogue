package com.apprisingsoftware.xmasrogue.entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Entity {
	
	public enum Placement {
		UP_STAIRCASE,
		DOWN_STAIRCASE,
		RANDOM,
		;
	}
	public enum State {
		SLEEPING,
		TRACKING,
		;
	}
	
	// After about 80 turns without damage, the regeneration rate will be the same as specified in Entities.
	// (if standing still -- if moving, then the rate will be 1/3 as specified in Entities.)
	public static final double regenerationGrowthFactor = Math.log(2) / 80;
	public static final double healthAbsorptionFactor = 0.04;
	
	private double originalMaxHP, maxHP, hp, regenerationRate;
	private int maxSatiation, satiation;
	private char character;
	private String name;
	private Color color;
	private List<Attack> attacks;
	private EnumSet<EntityAttribute> attributes;
	private List<StatusEffect> statusEffects;
	private Placement placement;
	private Entities type;
	private State state;
	
	private int turnsSinceDamaged = 0;
	private boolean justMoved = false;
	
	public Entity(double hp, double turnsPerRegeneration, char character, String name, Color color, List<Attack> attacks, EnumSet<EntityAttribute> attributes, Entities type) {
		if (turnsPerRegeneration == 0 || color == null || attributes == null) throw new IllegalArgumentException();
		this.hp = this.maxHP = this.originalMaxHP = hp;
		this.regenerationRate = 1 / turnsPerRegeneration;
		this.character = character;
		this.name = name;
		this.color = color;
		this.attacks = attacks;
		this.attributes = attributes;
		this.statusEffects = new ArrayList<>();
		this.placement = Placement.RANDOM;
		this.type = type;
		this.satiation = this.maxSatiation = 3000;
		this.state = State.SLEEPING;
	}
	
	public double getMaxHP() {
		return maxHP;
	}
	public double getHP() {
		return hp;
	}
	public double getHPFraction() {
		return hp / maxHP;
	}
	public int getMaxSatiation() {
		return maxSatiation;
	}
	public int getSatiation() {
		return satiation;
	}
	public int getHunger() {
		return maxSatiation - satiation;
	}
	public double getSatiationFraction() {
		return (double) satiation / maxSatiation;
	}
	public double getRegenerationRate() {
		return regenerationRate;
	}
	public double getTurnsPerRegeneration() {
		return 1 / regenerationRate;
	}
	public char getCharacter() {
		return character;
	}
	public Color getColor() {
		return color;
	}
	public boolean hasAttribute(EntityAttribute attribute) {
		return attributes.contains(attribute);
	}
	public List<StatusEffect> getStatusEffects() {
		return Collections.unmodifiableList(statusEffects);
	}
	public Placement getPlacement() {
		return placement;
	}
	public void setPlacement(Placement newPlacement) {
		if (newPlacement == null) throw new IllegalArgumentException();
		placement = newPlacement;
	}
	public Entities getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public Attack getAttack(Random random) {
		return Attack.selectAttack(attacks, random);
	}
	public Attack getAttack(Random random, Inventory inventory) {
		if (inventory.getCurrentWeapon() != null) {
			return inventory.getCurrentWeapon().getAttack(random);
		}
		else {
			return getAttack(random);
		}
	}
	public State getState() {
		return state;
	}
	public void setState(State newState) {
		state = newState;
	}
	
	public void justMoved() {
		justMoved = true;
	}
	
	public void heal(double amt) {
		hp += amt;
		clampHealth();
	}
	public void damage(double amt) {
		heal(-amt);
		turnsSinceDamaged = 0;
	}
	public void damage(double amt, Inventory inventory) {
		if (inventory.getCurrentArmor() != null) {
			damage(inventory.getCurrentArmor().reduceDamage(amt));
		}
		else {
			damage(amt);
		}
	}
	public boolean isDead() {
		return hp <= 0 || satiation <= 0;
	}
	public void clampHealth() {
		hp = Math.max(Math.min(hp, maxHP), 0);
	}
	public void makeHungrier(int amt) {
		satiation -= amt;
	}
	public void eat(int amt) {
		satiation += amt;
		clampSatiation();
	}
	public void clampSatiation() {
		satiation = Math.max(Math.min(satiation, maxSatiation), 0);
	}
	public void increaseMaxHP(double amt) {
		maxHP += amt;
		hp += amt;
	}
	
	public void tick() {
		for (StatusEffect statusEffect : statusEffects) {
			statusEffect.tick();
		}
		heal(regenerationRate * maxHP/originalMaxHP * Math.expm1(turnsSinceDamaged * regenerationGrowthFactor) / (justMoved ? 3 : 1));
		makeHungrier(1);
		justMoved = false;
		turnsSinceDamaged += 1;
	}
	
}
