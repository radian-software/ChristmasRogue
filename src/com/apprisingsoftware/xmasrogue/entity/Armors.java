package com.apprisingsoftware.xmasrogue.entity;

import java.util.Random;


public enum Armors {
	
	HAWAIIAN_SHIRT("Hawaiian shirt", 0.05),
	LEATHER_JACKET("leather jacket", 0.10),
	LEATHER_TUNIC("leather tunic", 0.15),
	CHITIN_ARMOR("chitin armor", 0.25),
	RING_MAIL("ring mail", 0.30),
	CHAIN_MAIL("chain mail", 0.35),
	BANDED_MAIL("banded mail", 0.45),
	MITHRIL_COAT("mithril-mail coat", 0.55),
	BRONZE_PLATE_MAIL("bronze plate mail", 0.65),
	IRON_PLATE_MAIL("iron plate mail", 0.70),
	STEEL_PLATE_MAIL("steel plate mail", 0.75),
	MITHRIL_PLATE_MAIL("mithril plate mail", 0.80),
	CRYSTAL_PLATE_MAIL("crystal plate mail", 0.85),
	DRAGONSCALE_ARMOR("dragonscale armor", 0.90),
	WYRMSCALE_ARMOR("ancient wyrmscale armor", 0.95),
	;
	
	private final String name;
	private final double protection;
	
	private Armors(String name, double damageBlocked) {
		this.name = name;
		this.protection = damageBlocked;
	}
	
	public Armor make() {
		return new Armor(name, protection);
	}
	
	public static Armors getAppropriate(int depth, Random random) {
		double avgProtection = -0.1 + 0.9 / (1 + Math.exp(-(depth - 5) / 5.0));
		double scaling = 1 + random.nextGaussian() / 4;
		double desiredProtection = avgProtection * scaling;
		double smallestDifference = Double.MAX_VALUE;
		Armors bestArmor = null;
		for (Armors armor : values()) {
			double howClose = Math.abs(desiredProtection - armor.protection);
			if (howClose < smallestDifference) {
				smallestDifference = howClose;
				bestArmor = armor;
			}
		}
		return bestArmor;
	}
	
}
