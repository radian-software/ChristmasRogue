package com.apprisingsoftware.xmasrogue.util;

public final class ShadowCaster {
	
	private interface RadiusStrategy {
		double radius(double x, double y);
	}
	
	private static final class CircleRadius implements RadiusStrategy {
		@Override public double radius(double x, double y) {
			return Math.sqrt(x * x + y * y);
		}
	}
	
	private enum Direction {
		NW(-1, -1),
		NE(1, -1),
		SW(-1, 1),
		SE(1, 1);
		
		public static final Direction[] DIAGONALS = values();
		
		public final int deltaX, deltaY;
		
		private Direction(int x, int y) {
			this.deltaX = x;
			this.deltaY = y;
		}
	}
	
	private ShadowCaster() {}
	
	private static int startx, starty;
	private static float radius;
	private static RadiusStrategy rStrat;
	private static float[][] resistanceMap;
	
	private static int width, height;
	private static float[][] lightMap;
	
	private static float force;
	
	public static float[][] calculateFOV(float[][] resistanceMap, int playerX, int playerY, double radius, double force) {
		ShadowCaster.force = (float) force;
		return calculateFOV(resistanceMap, playerX, playerY, (float) radius, new CircleRadius());
	}
	
	/**
	* Calculates the Field Of View for the provided map from the given x, y
	* coordinates. Returns a lightmap for a result where the values represent a
	* percentage of fully lit.
	*
	* A value equal to or below 0 means that cell is not in the
	* field of view, whereas a value equal to or above 1 means that cell is
	* in the field of view.
	*
	* @param resistanceMap the grid of cells to calculate on where 0 is transparent and 1 is opaque
	* @param startx the horizontal component of the starting location
	* @param starty the vertical component of the starting location
	* @param radius the maximum distance to draw the FOV
	* @param rStrat provides a means to calculate the radius as desired
	* @return the computed light grid
	*/
	public static float[][] calculateFOV(float[][] resistanceMap, int startx, int starty, float radius, RadiusStrategy rStrat) {
	    ShadowCaster.startx = startx;
	    ShadowCaster.starty = starty;
	    ShadowCaster.radius = radius;
	    ShadowCaster.rStrat = rStrat;
	    ShadowCaster.resistanceMap = resistanceMap;
	 
	    width = resistanceMap.length;
	    height = resistanceMap[0].length;
	    lightMap = new float[width][height];
	 
	    lightMap[startx][starty] = force;//light the starting cell
	    for (Direction d : Direction.DIAGONALS) {
	        castLight(1, 1.0f, 0.0f, 0, d.deltaX, d.deltaY, 0);
	        castLight(1, 1.0f, 0.0f, d.deltaX, 0, 0, d.deltaY);
	    }
	 
	    return lightMap;
	}
	 
	private static void castLight(int row, float start, float end, int xx, int xy, int yx, int yy) {
	    float newStart = 0.0f;
	    if (start < end) {
	        return;
	    }
	    boolean blocked = false;
	    for (int distance = row; distance <= radius && !blocked; distance++) {
	        int deltaY = -distance;
	        for (int deltaX = -distance; deltaX <= 0; deltaX++) {
	            int currentX = startx + deltaX * xx + deltaY * xy;
	            int currentY = starty + deltaX * yx + deltaY * yy;
	            float leftSlope = (deltaX - 0.5f) / (deltaY + 0.5f);
	            float rightSlope = (deltaX + 0.5f) / (deltaY - 0.5f);
	 
	            if (!(currentX >= 0 && currentY >= 0 && currentX < ShadowCaster.width && currentY < ShadowCaster.height) || start < rightSlope) {
	                continue;
	            } else if (end > leftSlope) {
	                break;
	            }
	 
	            //check if it's within the lightable area and light if needed
	            if (rStrat.radius(deltaX, deltaY) <= radius) {
	                float bright = (float) (1 - (rStrat.radius(deltaX, deltaY) / radius));
	                lightMap[currentX][currentY] = bright;
	            }
	 
	            if (blocked) { //previous cell was a blocking one
	                if (resistanceMap[currentX][currentY] >= 1) {//hit a wall
	                    newStart = rightSlope;
	                    continue;
	                } else {
	                    blocked = false;
	                    start = newStart;
	                }
	            } else {
	                if (resistanceMap[currentX][currentY] >= 1 && distance < radius) {//hit a wall within sight line
	                    blocked = true;
	                    castLight(distance + 1, start, leftSlope, xx, xy, yx, yy);
	                    newStart = rightSlope;
	                }
	            }
	        }
	    }
	}
	
}
