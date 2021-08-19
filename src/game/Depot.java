package game;

import java.util.Iterator;
import java.util.function.Consumer;

import game.ifaces.IPoolItem;
import game.ifaces.IPoolItemFactory;
import game.util.BitOps;

public class Depot implements IPoolItem
{
	TileIndex xy;
	int town_index;
	int index;
	
	private void clear() {
		xy = null;
		town_index = 0;
		index = 0;
	}
	
	static IPoolItemFactory<Depot> factory = new IPoolItemFactory<Depot>() {		
		@Override
		public Depot createObject() {
			return new Depot();
		}
	};
	
	static MemoryPool<Depot> _depot_pool = new MemoryPool<Depot>(factory);

	static TileIndex _last_built_train_depot_tile;
	static TileIndex _last_built_road_depot_tile;
	static TileIndex _last_built_aircraft_depot_tile;
	static TileIndex _last_built_ship_depot_tile;

	/**
	 * Get the pointer to the depot with index 'index'
	 */
	static Depot GetDepot(int index)
	{
		return _depot_pool.GetItemFromPool(index);
	}

	/**
	 * Get the current size of the DepotPool
	 */
	static int GetDepotPoolSize()
	{
		return _depot_pool.total_items();
	}

	static boolean IsDepotIndex(int index)
	{
		return index < GetDepotPoolSize();
	}

	public static Iterator<Depot> getIterator()
	{
		return _depot_pool.pool.values().iterator();
	}

	static void forEach( Consumer<Depot> c )
	{
		_depot_pool.forEach(c);
	}

	
	
	
	
	
	//public static final int  FOR_ALL_DEPOTS_FROM(d, start) for (d = GetDepot(start); d != null; d = (d.index + 1 < GetDepotPoolSize()) ? GetDepot(d.index + 1) : null)
	//public static final int  FOR_ALL_DEPOTS(d) FOR_ALL_DEPOTS_FROM(d, 0)

	public static final int  MIN_SERVINT_PERCENT =  5;
	public static final int  MAX_SERVINT_PERCENT = 90;
	public static final int  MIN_SERVINT_DAYS    = 30;
	public static final int  MAX_SERVINT_DAYS    = 800;

	/** Get the service interval domain.
	 * Get the new proposed service interval for the vehicle is indeed, clamped
	 * within the given bounds. @see MIN_SERVINT_PERCENT ,etc.
	 * @param index proposed service interval
	 */
	static int GetServiceIntervalClamped(int index)
	{
		return (Global._patches.servint_ispercent) ? BitOps.clamp(index, MIN_SERVINT_PERCENT, MAX_SERVINT_PERCENT) : BitOps.clamp(index, MIN_SERVINT_DAYS, MAX_SERVINT_DAYS);
	}


	/**
	 * Check if a depot really exists.
	 */
	public boolean IsValidDepot()
	{
		return (xy != null) && (xy.getTile() != 0); /* XXX: Replace by INVALID_TILE someday */
	}

	/**
	 * Check if a tile is a depot of the given type.
	 */
	//static private boolean IsTileDepotType(TileIndex tile, TransportType type)
	public static boolean IsTileDepotType(TileIndex tile, int type)
	{
		if( tile == null ) return false;
		
		switch(type)
		{
			case Global.TRANSPORT_RAIL:
				return tile.IsTileType( TileTypes.MP_RAILWAY) && (tile.getMap().m5 & 0xFC) == 0xC0;

			case Global.TRANSPORT_ROAD:
				return tile.IsTileType( TileTypes.MP_STREET) && (tile.getMap().m5 & 0xF0) == 0x20;

			case Global.TRANSPORT_WATER:
				return tile.IsTileType( TileTypes.MP_WATER) && (tile.getMap().m5 & ~3) == 0x80;

			default:
				assert false;
				return false;
		}
	}

	/**
	 * Returns the direction the exit of the depot on the given tile is facing.
	 */
	//private DiagDirection GetDepotDirection(TileIndex tile, TransportType type)
	public static /*DiagDirection*/ int GetDepotDirection(TileIndex tile, int type)
	{
		assert(IsTileDepotType(tile, type));

		switch (type)
		{
			case Global.TRANSPORT_RAIL:
			case Global.TRANSPORT_ROAD:
				/* Rail and road store a diagonal direction in bits 0 and 1 */
				return BitOps.GB(Global._m[tile.getTile()].m5, 0, 2);
			case Global.TRANSPORT_WATER:
				/* Water is stubborn, it stores the directions in a different order. */
				switch (BitOps.GB(Global._m[tile.getTile()].m5, 0, 2)) {
					case 0: return Tile.DIAGDIR_NE;
					case 1: return Tile.DIAGDIR_SW;
					case 2: return Tile.DIAGDIR_NW;
					case 3: return Tile.DIAGDIR_SE;
				}
			default:
				return Tile.INVALID_DIAGDIR; /* Not reached */
		}
	}

	/**
		Find out if the slope of the tile is suitable to build a depot of given direction
		@param direction The direction in which the depot's exit points. Starts with 0 as NE and goes Clockwise
		@param tileh The slope of the tile in question
		@return true if the construction is possible


	    This is checked by the ugly 0x4C >> direction magic, which does the following:
	      0x4C is 0100 1100 and tileh has only bits 0..3 set (steep tiles are ruled out)
	      So: for direction (only the significant bits are shown)<p>
	      00 (exit towards NE) we need either bit 2 or 3 set in tileh: 0x4C >> 0 = 1100<p>
	      01 (exit towards SE) we need either bit 1 or 2 set in tileh: 0x4C >> 1 = 0110<p>
	      02 (exit towards SW) we need either bit 0 or 1 set in tileh: 0x4C >> 2 = 0011<p>
	      03 (exit towards NW) we need either bit 0 or 4 set in tileh: 0x4C >> 3 = 1001<p>
	      So ((0x4C >> p2) & tileh) determines whether the depot can be built on the current tileh
	*/
	public static boolean CanBuildDepotByTileh(int direction, int tileh)
	{
		return 0 != ((0x4C >> direction) & tileh);
	}

	
	


		/* Max depots: 64000 (8 * 8000) */
		//DEPOT_POOL_BLOCK_SIZE_BITS = 3,       /* In bits, so (1 << 3) == 8 */
		//DEPOT_POOL_MAX_BLOCKS      = 8000,

	// TODO fixme index
	/**
	 * Called if a new block is added to the depot-pool
	 * /
	static private void DepotPoolNewBlock(int start_item)
	{
		Depot depot;

		FOR_ALL_DEPOTS_FROM(depot, start_item)
			depot.index = start_item++;
	}

	/* Initialize the town-pool */
	//MemoryPool _depot_pool = { "Depots", DEPOT_POOL_MAX_BLOCKS, DEPOT_POOL_BLOCK_SIZE_BITS, sizeof(Depot), &DepotPoolNewBlock, 0, 0, null };


	/**
	 * Gets a depot from a tile
	 *
	 * @return Returns the depot if the tile had a depot, else it returns null
	 */
	static Depot GetDepotByTile(TileIndex tile)
	{
		Depot [] ret = {null};
		_depot_pool.forEach( (i,depot) ->
		{
			if (depot.xy.getTile() == tile.getTile())
				ret[0] = depot;
		});

		return ret[0];
	}

	/**
	 * Allocate a new depot
	 */
	static Depot AllocateDepot()
	{
		//Depot *depot;
		Depot [] ret = {null};

		//FOR_ALL_DEPOTS(depot) {
		_depot_pool.forEach( (i,depot) ->
		{
			if (!depot.IsValidDepot()) {
				int index = depot.index;
				depot.clear();
				//memset(depot, 0, sizeof(Depot));
				depot.index = index;

				ret[0] = depot;
			}
		});

		if( ret[0] != null) return ret[0];
		
		/* Check if we can add a block to the pool */
		if (_depot_pool.AddBlockToPool())
			return AllocateDepot();

		return null;
	}


	@Override
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * Delete a depot
	 */
	static void DoDeleteDepot(TileIndex tile)
	{
		Order order = new Order();
		Depot depot;

		/* Get the depot */
		depot = GetDepotByTile(tile);

		/* Clear the tile */
		Landscape.DoClearSquare(tile);

		/* Clear the depot */
		depot.xy = null;

		/* Clear the depot from all order-lists */
		order.type    = Order.OT_GOTO_DEPOT;
		order.station = depot.index;
		Order.DeleteDestinationFromVehicleOrder(order);

		/* Delete the depot-window */
		Window.DeleteWindowById(Window.WC_VEHICLE_DEPOT, tile.tile);
	}


	static void InitializeDepot()
	{
		_depot_pool.CleanPool();
		_depot_pool.AddBlockToPool();
	}

	/*
	static final SaveLoad _depot_desc[] = {
		SLE_CONDVAR(Depot, xy,			SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(Depot, xy,			SLE_UINT32, 6, 255),
		SLE_VAR(Depot,town_index,		SLE_UINT16),
		SLE_END()
	};

	static void Save_DEPT()
	{
		//Depot depot;

		//FOR_ALL_DEPOTS(depot) 
		_depot_pool.forEach( (i,depot) -> {
			if (depot.IsValidDepot()) {
				SlSetArrayIndex(depot.index);
				SlObject(depot, _depot_desc);
			}
		});
	}

	static void Load_DEPT()
	{
		int index;

		while ((index = SlIterateArray()) != -1) {
			Depot depot;

			if (!_depot_pool.AddBlockIfNeeded(index))
				Global.error("Depots: failed loading savegame: too many depots");

			depot = GetDepot(index);
			SlObject(depot, _depot_desc);
		}
	}

	staic final ChunkHandler _depot_chunk_handlers[] = {
		{ 'DEPT', Save_DEPT, Load_DEPT, CH_ARRAY | CH_LAST},
	};
	*/
	

}


