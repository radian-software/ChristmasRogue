package com.apprisingsoftware.xmasrogue.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Result {
	
	public static final Result NONE = new Result(),
			SUCCESS = new Result(true, null, null),
			FAILURE = new Result(false, null, null),
			PLAYER_IS_DEAD = new Result(null, true, null),
			PLAYER_IS_ALIVE = new Result(null, false, null),
			WORLD_TICK = new Result(null, null, true),
			NO_WORLD_TICK = new Result(null, null, false);
	
	private final List<Message> messages;
	private final Boolean success;
	private final Boolean playerHasDied;
	private final Boolean worldTick;
	
	private Result() {
		this(Collections.emptyList());
	}
	private Result(List<Message> messages) {
		this(messages, null, null, null);
	}
	private Result(Boolean success, Boolean playerHasDied, Boolean worldTick) {
		this(Collections.emptyList(), success, playerHasDied, worldTick);
	}
	private Result(List<Message> messages, Boolean success, Boolean playerHasDied, Boolean worldTick) {
		this.messages = messages;
		this.success = success;
		this.playerHasDied = playerHasDied;
		this.worldTick = worldTick;
	}
	
	public static Result from(List<Message> messages) {
		return new Result(messages);
	}
	public static Result from(Message... messages) {
		return Result.from(Arrays.asList(messages));
	}
	public Result with(List<Message> messages) {
		List<Message> combined = new ArrayList<>(this.messages);
		combined.addAll(messages);
		return new Result(combined, success, playerHasDied, worldTick);
	}
	public Result with(Message... messages) {
		return this.with(Arrays.asList(messages));
	}
	public Result withSuccess() {
		return new Result(messages, true, playerHasDied, worldTick);
	}
	public Result withoutSuccess() {
		return new Result(messages, false, playerHasDied, worldTick);
	}
	public Result withPlayerDead() {
		return new Result(messages, success, true, worldTick);
	}
	public Result withPlayerAlive() {
		return new Result(messages, success, false, worldTick);
	}
	public Result withWorldTick() {
		return new Result(messages, success, playerHasDied, true);
	}
	public Result withoutWorldTick() {
		return new Result(messages, success, playerHasDied, false);
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	public boolean wasSuccessful() {
		if (success == null) throw new IllegalStateException();
		return success;
	}
	public boolean playerHasDied() {
		if (playerHasDied == null) throw new IllegalStateException();
		return playerHasDied;
	}
	public boolean tickWorld() {
		return worldTick;
	}
	
}
