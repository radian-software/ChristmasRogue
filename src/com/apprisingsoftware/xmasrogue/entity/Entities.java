package com.apprisingsoftware.xmasrogue.entity;

import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public enum Entities {
	
	//						turns/
	//				maxHP	regen	char	color						name
	PLAYER(			20,		10,		'@',	AsciiPanel.brightYellow,	"player",
			Arrays.asList(new Attack("hit", 1), new Attack("punch", 1.5), new Attack("bash", 2), new Attack("pummel", 5, 0.3)), EnumSet.noneOf(EntityAttribute.class)
	),
	// Rats, cats, and dogs
	RAT(			5,		20,		'r',	Color.LIGHT_GRAY,			"rat",
			Arrays.asList(new Attack("scratches", 0.5), new Attack("bites", 1), new Attack("claws", 1.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	GIANT_RAT(		14,		20,		'r',	new Color(0x622c13),		"giant rat",
			Arrays.asList(new Attack("scratches", 1), new Attack("bites", 2), new Attack("claws", 3)), EnumSet.noneOf(EntityAttribute.class)
	),
	JACKAL(			15,		20,		'j',	new Color(0xde663b),		"jackal",
			Arrays.asList(new Attack("scratches", 1), new Attack("bites", 2), new Attack("claws", 3)), EnumSet.noneOf(EntityAttribute.class)
	),
	PANTHER(		16,		20,		'f',	Color.DARK_GRAY,			"panther",
			Arrays.asList(new Attack("scratches", 1), new Attack("bites", 2), new Attack("claws", 3)), EnumSet.noneOf(EntityAttribute.class)
	),
	// Other real-life animals
	NEWT(			4,		20,		':',	new Color(0x70f000),		"newt",
			Arrays.asList(new Attack("bites", 1)), EnumSet.noneOf(EntityAttribute.class)
	),
	MONKEY(			10,		20,		'm',	new Color(0xde663b),		"monkey",
			Arrays.asList(new Attack("scratches", 0.5), new Attack("hits", 0.75), new Attack("punches", 1.5), new Attack("bites", 2.25)), EnumSet.noneOf(EntityAttribute.class)
	),
	BAT(			9,		20,		'B',	new Color(0x60482e),		"bat",
			Arrays.asList(new Attack("scratches", 0.75), new Attack("claws", 1.5), new Attack("bites", 2.25)), EnumSet.noneOf(EntityAttribute.class)
	),
	GIANT_BAT(		14,		20,		'B',	new Color(0x53483c),		"giant bat",
			Arrays.asList(new Attack("scratches", 1.25), new Attack("claws", 2.5), new Attack("bites", 3.75)), EnumSet.noneOf(EntityAttribute.class)
	),
	VAMPIRE_BAT(	20,		5,		'V',	new Color(0x8a1e1e),		"vampire bat",
			Arrays.asList(new Attack("scratches", 2), new Attack("claws", 4), new Attack("bites", 6)), EnumSet.noneOf(EntityAttribute.class)
	),
	// Humanoids et cetera
	KOBOLD(			15,		20,		'k',	new Color(0x6d8a1e),		"kobold",
			Arrays.asList(new Attack("hits", 1), new Attack("punches", 2), new Attack("bashes", 3)), EnumSet.noneOf(EntityAttribute.class)
	),
	GOBLIN(			20,		20,		'g',	new Color(0x93a338),		"goblin",
			Arrays.asList(new Attack("hits", 1.25), new Attack("punches", 2.5), new Attack("bashes", 3.75)), EnumSet.noneOf(EntityAttribute.class)
	),
	LARGE_GOBLIN(	30,		20,		'g',	new Color(0x838c4f),		"large goblin",
			Arrays.asList(new Attack("hits", 2), new Attack("punches", 4), new Attack("bashes", 6)), EnumSet.noneOf(EntityAttribute.class)
	),
	GOBLIN_WARLORD(	40,		20,		'g',	new Color(0xbfb11d),		"goblin warlord",
			Arrays.asList(new Attack("hits", 3), new Attack("punches", 6), new Attack("bashes", 9)), EnumSet.noneOf(EntityAttribute.class)
	),
	GNOME(			17,		20,		'h',	new Color(0x6d8a1e),		"gnome",
			Arrays.asList(new Attack("hits", 1.25), new Attack("punches", 2.5), new Attack("bashes", 3.75)), EnumSet.noneOf(EntityAttribute.class)
	),
	CORRUPT_ELF(	15,		20,		'e',	new Color(0x1d8bbf),		"corrupt elf",
			Arrays.asList(new Attack("hits", 1.75), new Attack("punches", 3.5), new Attack("bashes", 5.25)), EnumSet.noneOf(EntityAttribute.class)
	),
	DARK_ELF(		30,		20,		'e',	new Color(0x5c18a0),		"dark elf",
			Arrays.asList(new Attack("hits", 4), new Attack("punches", 8), new Attack("bashes", 12)), EnumSet.noneOf(EntityAttribute.class)
	),
	ZOMBIE(			30,		100,	'Z',	new Color(0x5e8c40),		"zombie",
			Arrays.asList(new Attack("hits", 3), new Attack("punches", 5), new Attack("bashes", 7)), EnumSet.noneOf(EntityAttribute.class)
	),
	SKELETON(		30,		100,	'S',	Color.WHITE,				"skeleton",
			Arrays.asList(new Attack("hits", 3.5), new Attack("punches", 5.5), new Attack("bashes", 8)), EnumSet.noneOf(EntityAttribute.class)
	),
	OGRE(			70,		20,		'O',	Color.GRAY,					"ogre",
			Arrays.asList(new Attack("grazes", 4), new Attack("hits", 8), new Attack("smashes", 12), new Attack("wallops", 16.5, 0.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	TROLL(			50,		10,		'T',	Color.LIGHT_GRAY,			"troll",
			Arrays.asList(new Attack("grazes", 3.5), new Attack("hits", 7), new Attack("smashes", 10.5), new Attack("wallops", 15, 0.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	NYMPH(			20,		0.00001,'n',	new Color(0xff85f7),		"nymph",
			Arrays.asList(new Attack("smacks", 0.5), new Attack("slaps", 1), new Attack("hits", 1.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	// Weird things
	BLOB(			15,		0.1,	'b',	new Color(0x52ffba),		"gelatinous blob",
			Arrays.asList(new Attack("smears", 1.25), new Attack("slimes", 2.5), new Attack("smothers", 3.75)), EnumSet.noneOf(EntityAttribute.class)
	),
	GIANT_BLOB(		35,		0.1,	'b',	new Color(0x4df3ff),		"giant gelatinous blob",
			Arrays.asList(new Attack("smears", 3), new Attack("slimes", 6), new Attack("smothers", 8.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	// Magical constructs
	CLAY_GOLEM(		20,		100,	'G',	new Color(0xf4a67c),		"clay golem",
			Arrays.asList(new Attack("punches", 1, 0.5), new Attack("kicks", 2), new Attack("slams", 3), new Attack("smashes", 4), new Attack("wallops", 6, 0.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	STONE_GOLEM(	30,		100,	'G',	new Color(0xb8b8b8),		"stone golem",
			Arrays.asList(new Attack("punches", 1.5, 0.5), new Attack("kicks", 3), new Attack("slams", 4.5), new Attack("smashes", 6), new Attack("wallops", 9, 0.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	IRON_GOLEM(		40,		100,	'G',	new Color(0x6e6e6e),		"iron golem",
			Arrays.asList(new Attack("punches", 2.25, 0.5), new Attack("kicks", 4.5), new Attack("slams", 6.75), new Attack("smashes", 9), new Attack("wallops", 13.5, 0.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	STEEL_GOLEM(	50,		100,	'G',	new Color(0x575757),		"steel golem",
			Arrays.asList(new Attack("punches", 3, 0.5), new Attack("kicks", 6), new Attack("slams", 9), new Attack("smashes", 12), new Attack("wallops", 18, 0.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	MITHRIL_GOLEM(	100,	100,	'G',	Color.WHITE,				"mithril golem",
			Arrays.asList(new Attack("punches", 6, 0.5), new Attack("kicks", 12), new Attack("slams", 18), new Attack("smashes", 24), new Attack("wallops", 30, 0.5)), EnumSet.noneOf(EntityAttribute.class)
	),
	// Legendary
	DRAGON(			100,	5,		'D',	Color.ORANGE,				"dragon",
			Arrays.asList(new Attack("claws", 10), new Attack("lacerates", 18), new Attack("bites", 10), new Attack("burns", 15), new Attack("scorches", 18)), EnumSet.noneOf(EntityAttribute.class)
	),
	ANCIENT_WYRM(	200,	5,		'W',	new Color(0xfbff0f),		"ancient wyrm",
			Arrays.asList(new Attack("claws", 12), new Attack("lacerates", 20), new Attack("bites", 12), new Attack("burns", 18), new Attack("scorches", 20)), EnumSet.noneOf(EntityAttribute.class)
	),
	SANTABOT(		300,	1,		'&',	Color.RED,				"Anti-Santa Sentinel NX3100",
			Arrays.asList(new Attack("punches", 15, 0.5), new Attack("slices", 20), new Attack("crushes", 25), new Attack("shoots", 30)), EnumSet.noneOf(EntityAttribute.class)
	),
	;
	
	private final double maxHP;
	private final double turnsPerRegeneration;
	private final char character;
	private final String name;
	private final Color color;
	private final List<Attack> attacks;
	private final EnumSet<EntityAttribute> attributes;
	
	private Entities(double maxHP, double turnsPerRegeneration, char character, Color color, String name, List<Attack> attacks, EnumSet<EntityAttribute> attributes) {
		this.maxHP = maxHP;
		this.turnsPerRegeneration = turnsPerRegeneration;
		this.character = character;
		this.name = name;
		this.color = color;
		this.attacks = attacks;
		this.attributes = attributes;
	}
	
	public Entity make() {
		return new Entity(maxHP, turnsPerRegeneration, character, name, color, attacks, attributes, this);
	}
	
	public static Entities getAppropriate(int depth, double maxPlayerHealth, Random random) {
		double avgPlayerDamage = 3 + 12 / (1 + Math.exp(-((depth - 5) - 10) / 2.5));
		if (depth + 1 <= 3) avgPlayerDamage = 1;
		Armor avgArmor = Armors.getAppropriate(depth - 5, random).make();
		double avgDamage = 1/avgArmor.reduceDamage(1) * maxPlayerHealth * 0.4;
		double scaling = 1 + random.nextGaussian() / 2;
		double desiredDamage = avgDamage * scaling;
		double smallestDifference = Double.MAX_VALUE;
		Entities bestEntity = null;
		for (Entities entity : values()) {
			if (entity == Entities.PLAYER) continue;
			double sum = 0;
			for (int i=0; i<10; i++) {
				double entityHealth = entity.maxHP;
				while (entityHealth > 0) {
					entityHealth -= avgPlayerDamage;
					sum += avgArmor.reduceDamage(Attack.selectAttack(entity.attacks, random).getDamage());
				}
			}
			double howClose = Math.abs(desiredDamage - sum / 10);
			if (howClose < smallestDifference) {
				smallestDifference = howClose;
				bestEntity = entity;
			}
		}
		return bestEntity;
	}
	
}
