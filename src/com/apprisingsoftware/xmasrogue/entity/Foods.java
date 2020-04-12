package com.apprisingsoftware.xmasrogue.entity;

import java.util.Random;

public enum Foods {
	
	FOOD_RATION("food ration", 1000),
	GUAVA("guava", 800),
	COOKED_TURKEY("cooked turkey", 1000),
	GINGERBREAD_COOKIE("gingerbread cookie", 400),
	CANDY_CANE("candy cane", 400),
	CANDY_BAR("candy bar", 300),
	;
	
	private final String name;
	private final int nutrition;
	
	private Foods(String name, int nutrition) {
		this.name = name;
		this.nutrition = nutrition;
	}
	
	public Food make() {
		return new Food(name, nutrition);
	}
	
	public static Foods getAppropriate(Random random) {
		Foods[] values = values();
		return values[random.nextInt(values.length)];
	}
	
}
