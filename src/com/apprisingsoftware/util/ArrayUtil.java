package com.apprisingsoftware.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ArrayUtil {
	
	public static class NaturalOrdering<T> implements Comparator<T> {
		@SuppressWarnings("unchecked")
		@Override public int compare(T o1, T o2) {
			return ((Comparable<T>) o1).compareTo(o2);
		}
	}
	
	private ArrayUtil() {}
	
	// List operations
	
	public static <T> void setAll(List<? super T> dest, int index, Iterator<? extends T> src) {
		for (int i=index; i<dest.size(); i++) {
			dest.set(i, src.next());
		}
	}
	public static <T> void setAll(List<? super T> dest, int index, Iterable<? extends T> src) {
		setAll(dest, index, src.iterator());
	}
	public static <T> void setAll(List<? super T> dest, Iterator<? extends T> src) {
		setAll(dest, 0, src);
	}
	public static <T> void setAll(List<? super T> dest, Iterable<? extends T> src) {
		setAll(dest, 0, src);
	}
	
	public static <T> List<T> cartesianProduct(int width, int height, BiIntFunction<T> pairFunction) {
		return IntStream.range(0, width).boxed().flatMap(x -> IntStream.range(0, height).<T>mapToObj(y -> pairFunction.apply(x, y))).collect(Collectors.toList());
	}
	
	// Array operations
	
	public static boolean[] nCopies(boolean b, int n) {
		boolean[] a = new boolean[n];
		Arrays.fill(a, b);
		return a;
	}
	public static char[] nCopies(char b, int n) {
		char[] a = new char[n];
		Arrays.fill(a, b);
		return a;
	}
	public static byte[] nCopies(byte b, int n) {
		byte[] a = new byte[n];
		Arrays.fill(a, b);
		return a;
	}
	public static short[] nCopies(short b, int n) {
		short[] a = new short[n];
		Arrays.fill(a, b);
		return a;
	}
	public static int[] nCopies(int b, int n) {
		int[] a = new int[n];
		Arrays.fill(a, b);
		return a;
	}
	public static long[] nCopies(long b, int n) {
		long[] a = new long[n];
		Arrays.fill(a, b);
		return a;
	}
	public static float[] nCopies(float b, int n) {
		float[] a = new float[n];
		Arrays.fill(a, b);
		return a;
	}
	public static double[] nCopies(double b, int n) {
		double[] a = new double[n];
		Arrays.fill(a, b);
		return a;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] nCopies(T b, int n) {
		T[] a = (T[]) Array.newInstance(b.getClass(), n);
		Arrays.fill(a, b);
		return a;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] nCopies(T b, Class<T> $class, int n) {
		T[] a = (T[]) Array.newInstance($class, n);
		Arrays.fill(a, b);
		return a;
	}
	public static String nString(char c, int n) {
		return new String(nCopies(c, n));
	}
	
	public static int sum(boolean[] a) {
		int s = 0;
		for (boolean b : a) if (b) s++;
		return s;
	}
	public static byte sum(byte[] a) {
		byte s = 0;
		for (byte b : a) s += b;
		return s;
	}
	public static short sum(short[] a) {
		short s = 0;
		for (short b : a) s += b;
		return s;
	}
	public static int sum(int[] a) {
		int s = 0;
		for (int b : a) s += b;
		return s;
	}
	public static long sum(long[] a) {
		long s = 0;
		for (long b : a) s += b;
		return s;
	}
	public static float sum(float[] a) {
		float s = 0;
		for (float b : a) s += b;
		return s;
	}
	public static double sum(double[] a) {
		double s = 0;
		for (double b : a) s += b;
		return s;
	}
	
	public static Boolean[] box(boolean[] a) {
		Boolean[] b = new Boolean[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Boolean.valueOf(a[i]);
		}
		return b;
	}
	public static Character[] box(char[] a) {
		Character[] b = new Character[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Character.valueOf(a[i]);
		}
		return b;
	}
	public static Byte[] box(byte[] a) {
		Byte[] b = new Byte[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Byte.valueOf(a[i]);
		}
		return b;
	}
	public static Short[] box(short[] a) {
		Short[] b = new Short[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Short.valueOf(a[i]);
		}
		return b;
	}
	public static Integer[] box(int[] a) {
		Integer[] b = new Integer[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Integer.valueOf(a[i]);
		}
		return b;
	}
	public static Long[] box(long[] a) {
		Long[] b = new Long[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Long.valueOf(a[i]);
		}
		return b;
	}
	public static Float[] box(float[] a) {
		Float[] b = new Float[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Float.valueOf(a[i]);
		}
		return b;
	}
	public static Double[] box(double[] a) {
		Double[] b = new Double[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = Double.valueOf(a[i]);
		}
		return b;
	}
	
	public static boolean[] unbox(Boolean[] a) {
		boolean[] b = new boolean[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].booleanValue();
		}
		return b;
	}
	public static char[] unbox(Character[] a) {
		char[] b = new char[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].charValue();
		}
		return b;
	}
	public static byte[] unbox(Byte[] a) {
		byte[] b = new byte[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].byteValue();
		}
		return b;
	}
	public static short[] unbox(Short[] a) {
		short[] b = new short[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].shortValue();
		}
		return b;
	}
	public static int[] unbox(Integer[] a) {
		int[] b = new int[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].intValue();
		}
		return b;
	}
	public static long[] unbox(Long[] a) {
		long[] b = new long[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].longValue();
		}
		return b;
	}
	public static float[] unbox(Float[] a) {
		float[] b = new float[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].floatValue();
		}
		return b;
	}
	public static double[] unbox(Double[] a) {
		double[] b = new double[a.length];
		for (int i=0; i<a.length; i++) {
			b[i] = a[i].doubleValue();
		}
		return b;
	}
	
	// Multidimensional array operations
	
	public static void fill(boolean[][] a, boolean val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static void fill(char[][] a, char val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static void fill(byte[][] a, byte val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static void fill(short[][] a, short val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static void fill(int[][] a, int val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static void fill(long[][] a, long val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static void fill(float[][] a, float val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static void fill(double[][] a, double val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	public static <T> void fill(T[][] a, T val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val;
			}
		}
	}
	
	public static void fill(boolean[][] a, BoolSupplier val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val.getAsBool();
			}
		}
	}
	public static void fill(int[][] a, IntSupplier val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val.getAsInt();
			}
		}
	}
	public static void fill(double[][] a, DoubleSupplier val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val.getAsDouble();
			}
		}
	}
	public static <T> void fill(T[][] a, Supplier<T> val) {
		for (int i=0; i<a.length; i++) {
			for (int j=0; j<a[i].length; j++) {
				a[i][j] = val.get();
			}
		}
	}
	
	public static boolean index(boolean[][] array, int x, int y, boolean defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static char index(char[][] array, int x, int y, char defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static byte index(byte[][] array, int x, int y, byte defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static short index(short[][] array, int x, int y, short defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static int index(int[][] array, int x, int y, int defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static long index(long[][] array, int x, int y, long defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static float index(float[][] array, int x, int y, float defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static double index(double[][] array, int x, int y, double defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	public static <T> T index(T[][] array, int x, int y, T defaultValue) {
		try { return array[x][y]; }
		catch (IndexOutOfBoundsException e) { return defaultValue; }
	}
	
	public static boolean[][] copy(boolean[][] src) {
		boolean[][] dest = new boolean[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	public static char[][] copy(char[][] src) {
		char[][] dest = new char[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	public static byte[][] copy(byte[][] src) {
		byte[][] dest = new byte[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	public static short[][] copy(short[][] src) {
		short[][] dest = new short[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	public static int[][] copy(int[][] src) {
		int[][] dest = new int[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	public static long[][] copy(long[][] src) {
		long[][] dest = new long[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	public static float[][] copy(float[][] src) {
		float[][] dest = new float[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	public static double[][] copy(double[][] src) {
		double[][] dest = new double[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dest;
	}
	@SuppressWarnings("unchecked")
	public static <T> T[][] copy(T[][] src) {
		Object[][] dest = new Object[src.length][];
		for (int i=0; i<src.length; i++) {
			dest[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return (T[][])dest;
	}
	
}
