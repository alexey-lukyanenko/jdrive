package game.tables;

import game.TileIndexDiffC;

public class IndustryTileTable {
	
	public TileIndexDiffC ti;
	public byte map5;

	public IndustryTileTable( int x, int y, int m) {
		ti = new TileIndexDiffC(x,y);
		map5 = (byte) m;
	}

}