package game.struct;

import game.Landscape;

/**
 * 
 * 
 * @implNote Used in hash map, must have corresp methods
 * 
 * @author dz
 *
 */
public class Point implements Comparable<Point> {
	public int x;
	public int y;

	public Point(int i, int j) {
		x = i;
		y = j;
	}


	public static Point RemapCoords(int x, int y, int z) {
		Point pt = new Point((y - x) * 2, y + x - z);
		//#if !defined(NEW_ROTATION)
		//pt.x = ;
		//pt.y = ;
		/*#else
		pt.x = (x + y) * 2;
		pt.y = x - y - z;
		#endif*/
		return pt;
	}

	public static Point RemapCoords2(int x, int y)
	{
		return RemapCoords(x, y, Landscape.GetSlopeZ(x, y));
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point p = (Point) obj;
			return p.x == x && p.y == y;
		}
		return super.equals(obj);
	}

	@Override
	public int compareTo(Point o) 
	{
		if( y < o.y ) return -1;
		if( y > o.y ) return 1;
		if( x < o.x ) return -1;
		if( x > o.x ) return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		
		//return x + y; diagonal ones will have same code
		return x + (y << 8);
	}

	
	
}