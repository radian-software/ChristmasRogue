package com.apprisingsoftware.xmasrogue.entity;

import com.apprisingsoftware.xmasrogue.io.Message;
import com.apprisingsoftware.xmasrogue.io.Result;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Inventory {
	
	private final List<Item> items;
	private Weapon currentWeapon;
	private Armor currentArmor;
	
	public Inventory() {
		this.items = new ArrayList<>(Collections.nCopies(26, null));
	}
	
	public boolean addItem(Item item) {
		for (int i=0; i<26; i++) {
			if (items.get(i) == null) {
				items.set(i, item);
				return true;
			}
		}
		return false;
	}
	public boolean addItem(char index, Item item) {
		char currentIndex = 'a';
		for (int i=0; i<26; i++) {
			if (items.get(i) == null && currentIndex == index) {
				items.set(i, item);
				return true;
			}
			else {
				currentIndex += 1;
			}
		}
		return false;
	}
	public boolean contains(char index) {
		return getItem(index) != null;
	}
	public Item getItem(char index) {
		return items.get(index - 'a');
	}
	public Item removeItem(char index) {
		Item item = getItem(index);
		items.set(index - 'a', null);
		return item;
	}
	public Weapon getCurrentWeapon() {
		return currentWeapon;
	}
	public Armor getCurrentArmor() {
		return currentArmor;
	}
	public boolean containsSleigh() {
		return items.stream().anyMatch(item -> item instanceof Sleigh);
	}
	
	public Result apply(char index, Entity player) {
		Item item = getItem(index);
		if (item == null) {
			return Result.FAILURE.withoutWorldTick();
		}
		else if (item instanceof Weapon) {
			if (currentWeapon == item) {
				return Result.FAILURE.withoutWorldTick().with(new Message(String.format("You are already wielding the %s.", item.getName()), Message.MENU_COLOR));
			}
			currentWeapon = (Weapon) item;
			return Result.SUCCESS.withWorldTick().with(new Message(String.format("You wield the %s.", item.getName()), Message.MENU_COLOR));
		}
		else if (item instanceof Armor) {
			if (currentArmor == item) {
				return Result.FAILURE.withoutWorldTick().with(new Message(String.format("You are already wearing the %s.", item.getName()), Message.MENU_COLOR));
			}
			currentArmor = (Armor) item;
			return Result.SUCCESS.withWorldTick().with(new Message(String.format("You wear the %s.", item.getName()), Message.MENU_COLOR));
		}
		else if (item instanceof Food) {
			player.eat(((Food) item).getNutrition());
			removeItem(index);
			return Result.SUCCESS.withoutWorldTick().with(new Message(String.format("You eat the %s.", item.getName()), Message.MENU_COLOR));
		}
		else return Result.FAILURE.withoutWorldTick().with(new Message(String.format("You cannot wield, wear, or eat a %s.", item.getName()), Message.MENU_COLOR));
	}
	public Result unequip(char index) {
		Item item = getItem(index);
		if (item == null) {
			return Result.FAILURE.withoutWorldTick();
		}
		else if (item == currentWeapon) {
			currentWeapon = null;
			return Result.SUCCESS.withWorldTick().with(new Message(String.format("You are no longer wielding the %s.", item.getName()), Message.MENU_COLOR));
		}
		else if (item == currentArmor) {
			currentArmor = null;
			return Result.SUCCESS.withWorldTick().with(new Message(String.format("You take off the %s.", item.getName()), Message.MENU_COLOR));
		}
		else if (item instanceof Weapon) {
			return Result.FAILURE.withoutWorldTick().with(new Message(String.format("You are not wielding the %s.", item.getName()), Message.MENU_COLOR));
		}
		else if (item instanceof Armor) {
			return Result.FAILURE.withoutWorldTick().with(new Message(String.format("You are not wearing the %s.", item.getName()), Message.MENU_COLOR));
		}
		else return Result.FAILURE.withoutWorldTick().with(new Message(String.format("You are not wielding or wearing the %s.", item.getName()), Message.MENU_COLOR));
	}
	
	public List<String> getItemNames() {
		List<String> names = new ArrayList<>();
		for (int i=0; i<26; i++) {
			Item item = items.get(i);
			if (item != null) {
				names.add(String.format("%c (%c) a %s%s%s", item.getCharacter(), 'a' + i, item.getName(), item == currentWeapon ? " (wielding)" : "", item == currentArmor ? " (wearing)" : ""));
			}
		}
		return names;
	}
	
}
