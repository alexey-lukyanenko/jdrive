package game.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.Vehicle;
import game.ids.VehicleID;
import game.struct.Point;

/**
 * 
 * TODO spatial hash to find vehicles intersecting some part of map.
 *
 * General idea is to reduce coordinates by / 8 and check all possible
 * squares of 256*256 size.
 * 
 * @author dz
 *
 */

public class VehicleHash 
{
	
	//private static final int DELETE_BITS = 8;
	private static final int DELETE_BITS = 3;

	static int hashFunc(int x, int y )
	{
		return (x << 16) + (y & 0xFFFF);
	}
	
	private int hashFunc(Point prev) {
		return hashFunc(prev.x >> DELETE_BITS, prev.y >> DELETE_BITS);
	}
	
	//ArrayList<VehicleID> list = new ArrayList<VehicleID>();
	final Map<Integer,VehicleID> map = new HashMap<Integer,VehicleID>();
		
	public List<VehicleID> get(int x1, int y1, int x2, int y2) {
		x1 >>= DELETE_BITS; // down
		x2 = (x2 >> DELETE_BITS) + 1; // up

		y1 >>= DELETE_BITS;
		y2 = (y2 >> DELETE_BITS) + 1;

		ArrayList<VehicleID> list = new ArrayList<VehicleID>();  
		
		for(int x = x1; x <= x2; x++ )
		{
			for(int y = y1; y <= y2; y++ )
			{
				VehicleID item = map.get(hashFunc(x, y));
				if(item != null)
					list.add(item);
			}
		}
		
		return list;
	}
	
	public void update(Point prev, Point tobe, Vehicle vehicle) 
	{
		int hash1 = hashFunc(prev);
		int hash2 = hashFunc(tobe);
		
		//if( hash1 == hash2 ) return;
		
		map.remove(Integer.valueOf(hash1));
		map.put(Integer.valueOf(hash2), VehicleID.get(vehicle.index));
		
	}

	public void clear() {
		map.clear();		
	}
}
