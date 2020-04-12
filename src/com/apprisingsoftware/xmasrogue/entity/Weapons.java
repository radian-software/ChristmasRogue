package com.apprisingsoftware.xmasrogue.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum Weapons {
	
	POCKETKNIFE("pocketknife",
			Arrays.asList(new Attack("graze", 1, 0.5), new Attack("scrape", 2), new Attack("slice", 3))
	),
	KNIFE("knife",
			Arrays.asList(new Attack("graze", 1, 0.5), new Attack("scrape", 2.25), new Attack("stab", 4), new Attack("slash", 6))
	),
	DAGGER("dagger",
			Arrays.asList(new Attack("graze", 1.5, 0.5), new Attack("scrape", 3), new Attack("stab", 5), new Attack("slash", 7.5))
	),
	SHORTSWORD("shortsword",
			Arrays.asList(new Attack("graze", 2, 0.5), new Attack("scrape", 4.5), new Attack("stab", 7), new Attack("slash", 9))
	),
	CLUB("club",
			Arrays.asList(new Attack("graze", 2.5, 0.5), new Attack("hit", 5.5), new Attack("smash", 9), new Attack("wallop", 14, 0.5))
	),
	AXE("axe",
			Arrays.asList(new Attack("graze", 3, 0.5), new Attack("scrape", 5.5), new Attack("cut", 9.5), new Attack("slash", 12.5), new Attack("chop", 16, 0.5), new Attack("decapitate", 1000, 0.05))
	),
	MACE("mace",
			Arrays.asList(new Attack("graze", 3.5, 0.5), new Attack("hit", 7), new Attack("smash", 11.5), new Attack("wallop", 16.5, 0.5))
	),
	LONGSWORD("longsword",
			Arrays.asList(new Attack("graze", 3.5, 0.5), new Attack("scrape", 7), new Attack("stab", 10), new Attack("slash", 15), new Attack("decapitate", 1000, 0.025))
	),
	BATTLEAXE("battleaxe",
			Arrays.asList(new Attack("graze", 5, 0.5), new Attack("scrape", 7.5), new Attack("cut", 12.5), new Attack("slash", 16.5), new Attack("chop", 19, 0.5), new Attack("decapitate", 1000, 0.1))
	),
	BROADSWORD("broadsword",
			Arrays.asList(new Attack("graze", 5, 0.5), new Attack("scrape", 9), new Attack("stab", 14), new Attack("slash", 19), new Attack("decapitate", 1000, 0.05))
	),
	GREATSWORD("greatsword",
			Arrays.asList(new Attack("graze", 8, 0.5), new Attack("scrape", 13), new Attack("stab", 19), new Attack("slash", 25), new Attack("decapitate", 1000, 0.1))
	),
	HALBERD("Halberd of the Reckoner",
			Arrays.asList(new Attack("graze", 10, 0.5), new Attack("scrape", 15), new Attack("cut", 20), new Attack("slash", 25), new Attack("chop", 30, 0.5), new Attack("stab", 35), new Attack("decapitate", 1000, 0.2))
	),
	;
	
	private final String name;
	private final List<Attack> attacks;
	
	private Weapons(String name, List<Attack> attacks) {
		this.name = name;
		this.attacks = attacks;
	}
	
	public Weapon make() {
		return new Weapon(name, attacks);
	}
	
	public static Weapons getAppropriate(int depth, Random random) {
		double avgDamage = 3 + 12 / (1 + Math.exp(-(depth - 10) / 2.5));
		double scaling = 1 + random.nextGaussian() / 4;
		double desiredDamage = avgDamage * scaling;
		double smallestDifference = Double.MAX_VALUE;
		Weapons bestWeapon = null;
		for (Weapons weapon : values()) {
			double sum = 0;
			int j=0;
			while (j < 10) {
				Attack attack = weapon.attacks.get(random.nextInt(weapon.attacks.size()));
				if (!attack.getVerb().equals("decapitate")) {
					sum += attack.getDamage();
					j++;
				}
			}
			double howClose = Math.abs(desiredDamage - sum / 10);
			if (howClose < smallestDifference) {
				smallestDifference = howClose;
				bestWeapon = weapon;
			}
		}
		return bestWeapon;
	}
	
}
