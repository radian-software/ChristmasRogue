package com.apprisingsoftware.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamUtil {
	
	private StreamUtil() {}
	
	// Conversions
	public static <T> Iterable<T> toIterable(Iterator<T> iterator) {
		return () -> iterator;
	}
	public static <T> Stream<T> toStream(Iterator<T> iterator) {
		return toStream(toIterable(iterator));
	}
	public static <T> Iterator<T> toIterator(Iterable<T> iterable) {
		return iterable.iterator();
	}
	public static <T> Stream<T> toStream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	public static <T> Iterator<T> toIterator(Stream<T> stream) {
		return stream.iterator();
	}
	public static <T> Iterable<T> toIterable(Stream<T> stream) {
		return toIterable(toIterator(stream));
	}
	
	// Two-stream zip
	public static <T, U, R> Iterator<R> zip(Iterator<T> first, Iterator<U> second, BiFunction<T, U, R> zipper) {
		return new Iterator<R>() {
			@Override public boolean hasNext() {
				boolean h1 = first.hasNext(), h2 = second.hasNext();
				if (h1 != h2) throw new IllegalStateException();
				return h1;
			}
			@Override public R next() {
				return zipper.apply(first.next(), second.next());
			}
			@Override public void remove() {
				first.remove();
				second.remove();
			}
		};
	}
	public static <T, U, R> Iterable<R> zip(Iterable<T> first, Iterable<U> second, BiFunction<T, U, R> zipper) {
		return () -> zip(first.iterator(), second.iterator(), zipper);
	}
	public static <T, U, R> Stream<R> zip(Stream<T> first, Stream<U> second, BiFunction<T, U, R> zipper) {
		Iterable<R> iterable = () -> zip(first.iterator(), second.iterator(), zipper);
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	// Three-stream zip
	public static <T, U, V, R> Iterator<R> zip(Iterator<T> first, Iterator<U> second, Iterator<V> third, TriFunction<T, U, V, R> zipper) {
		return new Iterator<R>() {
			@Override public boolean hasNext() {
				boolean h1 = first.hasNext(), h2 = second.hasNext(), h3 = third.hasNext();
				if (h1 != h2 || h2 != h3) throw new IllegalStateException();
				return h1;
			}
			@Override public R next() {
				return zipper.apply(first.next(), second.next(), third.next());
			}
			@Override public void remove() {
				first.remove();
				second.remove();
				third.remove();
			}
		};
	}
	public static <T, U, V, R> Iterable<R> zip(Iterable<T> first, Iterable<U> second, Iterable<V> third, TriFunction<T, U, V, R> zipper) {
		return () -> zip(first.iterator(), second.iterator(), third.iterator(), zipper);
	}
	public static <T, U, V, R> Stream<R> zip(Stream<T> first, Stream<U> second, Stream<V> third, TriFunction<T, U, V, R> zipper) {
		Iterable<R> iterable = () -> zip(first.iterator(), second.iterator(), third.iterator(), zipper);
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	// Concat
	private static class IteratorChain<T> implements Iterator<T> {
		private List<Iterator<? extends T>> iterators;
		private int i = 0;
		public IteratorChain(List<Iterator<? extends T>> iterators) {
			this.iterators = iterators;
		}
		@Override public boolean hasNext() {
			while (i < iterators.size()) {
				if (iterators.get(i).hasNext()) return true;
				i += 1;
			}
			return false;
		}
		@Override public T next() {
			while (i < iterators.size()) {
				if (iterators.get(i).hasNext()) return iterators.get(i).next();
				i += 1;
			}
			throw new NoSuchElementException();
		}
		@Override public void remove() {
			iterators.get(i).remove();
		}
	}
	public static <T> Iterator<T> concatIterators(List<Iterator<? extends T>> iterators) {
		return new IteratorChain<>(iterators);
	}
	@SafeVarargs public static <T> Iterator<T> concat(Iterator<? extends T>... iterators) {
		return concatIterators(Arrays.asList(iterators));
	}
	public static <T> Iterable<T> concatIterables(List<Iterable<? extends T>> iterables) {
		return () -> concatIterators(iterables.stream().map(Iterable::iterator).collect(Collectors.toList()));
	}
	@SafeVarargs public static <T> Iterable<T> concat(Iterable<? extends T>... iterables) {
		return concatIterables(Arrays.asList(iterables));
	}
	public static <T> Stream<T> concatStreams(List<Stream<? extends T>> streams) {
		Iterable<T> iterable = () -> concatIterators(streams.stream().map(Stream::iterator).collect(Collectors.toList()));
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	@SafeVarargs public static <T> Stream<T> concat(Stream<? extends T>... streams) {
		return concatStreams(Arrays.asList(streams));
	}
	
	// Enumerate
	private static class EnumerateIterator<T> implements Iterator<Enumerate<T>> {
		private final Iterator<T> it;
		private final int step;
		private int index;
		public EnumerateIterator(Iterator<T> it, int start, int step) {
			this.it = it;
			this.step = step;
			index = start;
		}
		@Override public boolean hasNext() {
			return it.hasNext();
		}
		@Override public Enumerate<T> next() {
			index += step;
			return new Enumerate<>(index - step, it.next());
		}
		@Override public void remove() {
			it.remove();
		}
	}
	public static <T> Iterator<Enumerate<T>> enumerate(Iterator<T> iterator, int start, int step) {
		return new EnumerateIterator<>(iterator, start, step);
	}
	public static <T> Iterator<Enumerate<T>> enumerate(Iterator<T> iterator, int start) {
		return enumerate(iterator, start, 1);
	}
	public static <T> Iterator<Enumerate<T>> enumerate(Iterator<T> iterator) {
		return enumerate(iterator, 0);
	}
	public static <T> Iterable<Enumerate<T>> enumerate(Iterable<T> iterable, int start, int step) {
		return () -> enumerate(iterable.iterator(), start, step);
	}
	public static <T> Iterable<Enumerate<T>> enumerate(Iterable<T> iterable, int start) {
		return enumerate(iterable, start, 1);
	}
	public static <T> Iterable<Enumerate<T>> enumerate(Iterable<T> iterable) {
		return enumerate(iterable, 0);
	}
	public static <T> Stream<Enumerate<T>> enumerate(Stream<T> stream, int start, int step) {
		Iterable<Enumerate<T>> iterable = () -> enumerate(stream.iterator(), start, step);
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	public static <T> Stream<Enumerate<T>> enumerate(Stream<T> stream, int start) {
		return enumerate(stream, start, 1);
	}
	public static <T> Stream<Enumerate<T>> enumerate(Stream<T> stream) {
		return enumerate(stream, 0);
	}
	
	// Group
	private static class GroupTwoIterator<T, R> implements Iterator<R> {
		private Iterator<T> it;
		private BiFunction<T, T, R> concatter;
		private T last, first;
		private int extraGroups;
		public GroupTwoIterator(Iterator<T> it, BiFunction<T, T, R> concatter, boolean wrap) {
			this.it = it;
			last = first = it.next();
			this.concatter = concatter;
			this.extraGroups = wrap ? 1 : 0;
		}
		@Override public boolean hasNext() {
			return extraGroups > 0;
		}
		@Override public R next() {
			if (it.hasNext()) return concatter.apply(last, last = it.next());
			else {
				extraGroups -= 1;
				if (extraGroups == 0) return concatter.apply(last, first);
				else throw new NoSuchElementException();
			}
		}
	}
	public static <T, R> Iterator<R> groupTwo(Iterator<T> iterator, BiFunction<T, T, R> concatter, boolean wrap) {
		return new GroupTwoIterator<>(iterator, concatter, wrap);
	}
	public static <T, R> Iterable<R> groupTwo(Iterable<T> iterable, BiFunction<T, T, R> concatter, boolean wrap) {
		return () -> groupTwo(iterable.iterator(), concatter, wrap);
	}
	public static <T, R> Stream<R> groupTwo(Stream<T> stream, BiFunction<T, T, R> concatter, boolean wrap) {
		Iterable<R> iterable = () -> groupTwo(stream.iterator(), concatter, wrap);
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	private static class GroupThreeIterator<T, R> implements Iterator<R> {
		private Iterator<T> it;
		private TriFunction<T, T, T, R> concatter;
		private T beforeLast, last;
		public GroupThreeIterator(Iterator<T> it, TriFunction<T, T, T, R> concatter) {
			this.it = it;
			beforeLast = it.next();
			last = it.next();
			this.concatter = concatter;
		}
		@Override public boolean hasNext() {
			return it.hasNext();
		}
		@Override public R next() {
			return concatter.apply(beforeLast, beforeLast = last, last = it.next());
		}
	}
	public static <T, R> Iterator<R> groupThree(Iterator<T> iterator, TriFunction<T, T, T, R> concatter) {
		return new GroupThreeIterator<>(iterator, concatter);
	}
	public static <T, R> Iterable<R> groupThree(Iterable<T> iterable, TriFunction<T, T, T, R> concatter) {
		return () -> groupThree(iterable.iterator(), concatter);
	}
	public static <T, R> Stream<R> groupThree(Stream<T> stream, TriFunction<T, T, T, R> concatter) {
		Iterable<R> iterable = () -> groupThree(stream.iterator(), concatter);
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
}
