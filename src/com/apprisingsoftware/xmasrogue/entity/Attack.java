package com.apprisingsoftware.xmasrogue.entity;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class Attack {
	
	private final String verb;
	private final double damage;
	private final double weight;
	
	public Attack(String verb, double damage, double weight) {
		if (verb == null || damage < 0 || weight < 0) throw new IllegalArgumentException();
		this.verb = verb;
		this.damage = damage;
		this.weight = weight;
	}
	public Attack(String verb, double damage) {
		this(verb, damage, 1);
	}
	
	public String getVerb() {
		return verb;
	}
	public double getDamage() {
		return damage;
	}
	private double getWeight() {
		return weight;
	}
	
	public static Attack selectAttack(List<Attack> attacks, Random random) {
		if (attacks == null || attacks.isEmpty() || random == null) throw new IllegalArgumentException();
		double totalWeight = attacks.stream().collect(Collectors.summingDouble(Attack::getWeight));
		double rand = random.nextDouble() * totalWeight;
		for (Attack attack : attacks) {
			rand -= attack.getWeight();
			if (rand < 0) return attack;
		}
		throw new AssertionError();
	}
	
}
