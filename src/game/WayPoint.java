package game;

import java.util.Iterator;
import java.util.function.Consumer;

import game.util.BitOps;

public class WayPoint implements IPoolItem
{
	public static final int RAIL_TYPE_WAYPOINT = 0xC4;
	public static final int RAIL_WAYPOINT_TRACK_MASK = 1;

	/* Max waypoints: 64000 (8 * 8000) */
	public static final int WAYPOINT_POOL_BLOCK_SIZE_BITS = 3;       /* In bits, so (1 << 3) == 8 */
	public static final int WAYPOINT_POOL_MAX_BLOCKS      = 8000;

	public static final int MAX_WAYPOINTS_PER_TOWN        = 64;


	public TileIndex xy;      ///< Tile of WayPoint
	public int index;      ///< Index of WayPoint

	public int town_index; ///< Town associated with the WayPoint
	public byte town_cn;      ///< The Nth WayPoint for this town (consecutive number)
	public StringID string;   ///< If this is zero (i.e. no custom name), town + town_cn is used for naming

	public ViewportSign sign; ///< Dimensions of sign (not saved)
	public int build_date; ///< Date of construction

	public byte stat_id;      ///< ID of WayPoint within the WayPoint class (not saved)
	public int grfid;      ///< ID of GRF file
	public byte localidx;     ///< Index of station within GRF file

	public byte deleted;      ///< Delete counter. If greater than 0 then it is decremented until it reaches 0; the WayPoint is then is deleted.



	private static IPoolItemFactory<WayPoint> factory = new IPoolItemFactory<WayPoint>() 
	{		
		@Override
		public WayPoint createObject() {
			return new WayPoint();
		}
	};

	private static MemoryPool<WayPoint> _waypoint_pool = new MemoryPool<WayPoint>(factory);
	/* Initialize the town-pool */
	//MemoryPool _waypoint_pool = { "Waypoints", WAYPOINT_POOL_MAX_BLOCKS, WAYPOINT_POOL_BLOCK_SIZE_BITS, sizeof(WayPoint), &WaypointPoolNewBlock, 0, 0, null };

	public static Iterator<WayPoint> getIterator()
	{
		return _waypoint_pool.pool.values().iterator();
	}

	public static void forEach( Consumer<WayPoint> c )
	{
		_waypoint_pool.forEach(c);
	}
	
	
	private void clear()
	{
		xy = null;
		town_index = 0;
		town_cn = 0;
		string = null;
		sign = null;
		build_date = 0;
		stat_id = 0;
		grfid = 0;
		localidx = 0;
		deleted = 0;
	}

	public WayPoint()
	{
		clear();
	}


	/**
	 * Get the pointer to the WayPoint with index 'index'
	 */
	public static WayPoint GetWaypoint(int index)
	{
		return _waypoint_pool.GetItemFromPool(index);
	}

	/**
	 * Get the current size of the WaypointPool
	 */
	private static int GetWaypointPoolSize()
	{
		return _waypoint_pool.total_items();
	}

	public static boolean IsWaypointIndex(int index)
	{
		return (index > 0) && (index < GetWaypointPoolSize());
	}

	//#define FOR_ALL_WAYPOINTS_FROM(wp, start) for (wp = GetWaypoint(start); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null)

	//#define FOR_ALL_WAYPOINTS(wp) FOR_ALL_WAYPOINTS_FROM(wp, 0)



	public static boolean IsRailWaypoint(TileIndex tile)
	{
		return (tile.getMap().m5 & 0xFC) == 0xC4;
	}

	/**
	 * Fetch a WayPoint by tile
	 * @param tile Tile of WayPoint
	 * @return WayPoint
	 */
	public static WayPoint GetWaypointByTile(TileIndex tile)
	{
		assert(tile.IsTileType( TileTypes.MP_RAILWAY) && IsRailWaypoint(tile));
		return GetWaypoint(tile.getMap().m2);
	}


	// TODO NOT CALLED
	/**
	 * Called if a new block is added to the WayPoint-pool
	 * /
private void WaypointPoolNewBlock(int start_item)
{
	WayPoint wp;

	//FOR_ALL_WAYPOINTS_FROM(wp, start_item)
    for (wp = GetWaypoint(start_item); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null)
		wp.index = start_item++;
}*/

	@Override
	public void setIndex(int index) {
		this.index = index;	
	}

	/* Create a new WayPoint */
	private static WayPoint AllocateWaypoint()
	{
		WayPoint [] ret = {null};

		//for (wp = GetWaypoint(0); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null) 
		_waypoint_pool.forEach((i,wp) ->
		{
			if (wp.xy == null) {
				int index = wp.index;

				//memset(wp, 0, sizeof(WayPoint));
				wp.clear();
				wp.index = index;

				ret[0] = wp;
			}
		});

		if( ret[0] != null ) return ret[0];
		
		/* Check if we can add a block to the pool */
		if (_waypoint_pool.AddBlockToPool())
			return AllocateWaypoint();

		return null;
	}

	/* Update the sign for the WayPoint */
	void UpdateWaypointSign()
	{
		Point pt = Point.RemapCoords2(xy.TileX() * 16, xy.TileY() * 16);
		Global.SetDParam(0, index);
		ViewPort.UpdateViewportSignPos(sign, pt.x, pt.y - 0x20, Str.STR_WAYPOINT_VIEWPORT);
	}

	/* Redraw the sign of a WayPoint */
	private void RedrawWaypointSign()
	{
		ViewPort.MarkAllViewportsDirty(
				sign.left - 6,
				sign.top,
				sign.left + (sign.width_1 << 2) + 12,
				sign.top + 48);
	}

	/* Update all signs */
	static void UpdateAllWaypointSigns()
	{
		//WayPoint wp;

		//for (wp = GetWaypoint(0); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null) 
		_waypoint_pool.forEach((i,wp) ->
		{
			if(wp.xy != null)
				wp.UpdateWaypointSign();
		});
	}

	/* Set the default name for a WayPoint */
	private void MakeDefaultWaypointName()
	{
		//WayPoint local_wp;
		boolean used_waypoint[] = new boolean[MAX_WAYPOINTS_PER_TOWN];
		int i;

		town_index = Town.ClosestTownFromTile(xy, (int)-1).index;

		//memset(used_waypoint, 0, sizeof(used_waypoint));
		//used_waypoint.clear();
		/* Find an unused WayPoint number belonging to this town */
		//for (local_wp = GetWaypoint(0); local_wp != null; local_wp = (local_wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(local_wp.index + 1) : null) 
		_waypoint_pool.forEach((ii,local_wp) ->
		{
			if (this == local_wp)
			{
				//continue;
				return;
			}
			
			if (local_wp.xy != null && local_wp.string.id == Str.STR_NULL && local_wp.town_index == town_index)
				used_waypoint[local_wp.town_cn] = true;
		});

		/* Find an empty spot */
		for (i = 0; used_waypoint[i] && i < MAX_WAYPOINTS_PER_TOWN; i++) {}

		string = new StringID( Str.STR_NULL );
		town_cn = (byte) i;
	}

	/* Find a deleted WayPoint close to a tile. */
	private static WayPoint FindDeletedWaypointCloseTo(TileIndex tile)
	{
		WayPoint best = null;
		int thres = 8, cur_dist;

		//for (wp = GetWaypoint(0); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null) 
		//_waypoint_pool.forEach((i,wp) ->
		Iterator<WayPoint> ii = getIterator();
		while(ii.hasNext())
		{
			WayPoint wp = ii.next();
			if ( (0 != wp.deleted) && (null != wp.xy) ) {
				cur_dist = Map.DistanceManhattan(tile, wp.xy);
				if (cur_dist < thres) {
					thres = cur_dist;
					best = wp;
				}
			}
		}

		return best;
	}

	/**
	 * Update WayPoint graphics id against saved GRFID/localidx.
	 * This is to ensure the chosen graphics are correct if GRF files are changed.
	 */
	static void UpdateAllWaypointCustomGraphics()
	{
		//WayPoint wp;

		//for (wp = GetWaypoint(0); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null)
		/* TODO UpdateAllWaypointCustomGraphics
		_waypoint_pool.forEach((ix,wp) ->
		{
			int i;

			if (wp.grfid != 0) 
			{
				for (i = 0; i < GetNumCustomStations(STAT_CLASS_WAYP); i++) {
					final StationSpec spec = GetCustomStation(STAT_CLASS_WAYP, i);
					if (spec != null && spec.grfid == wp.grfid && spec.localidx == wp.localidx) {
						wp.stat_id = (byte) i;
						break;
					}
				}
			}
		});
		*/
	}

	/** Convert existing rail to WayPoint. Eg build a WayPoint station over
	 * piece of rail
	 * @param x,y coordinates where WayPoint will be built
	 * @param p1 graphics for WayPoint type, 0 indicates standard graphics
	 * @param p2 unused
	 *
	 * @todo When checking for the tile slope,
	 * distingush between "Flat land required" and "land sloped in wrong direction"
	 */
	static int CmdBuildTrainWaypoint(int x, int y, int flags, int p1, int p2)
	{
		TileIndex tile = TileIndex.TileVirtXY(x, y);
		WayPoint wp;
		int tileh;
		int dir;

		Player.SET_EXPENSES_TYPE(Player.EXPENSES_CONSTRUCTION);

		/* if custom gfx are used, make sure it is within bounds */
		// TODO if (p1 >= GetNumCustomStations(STAT_CLASS_WAYP)) return Cmd.CMD_ERROR;

		//if (!tile.IsTileType(TileTypes.MP_RAILWAY) || ((dir = 0, tile.getMap().m5 != 1) && (dir = 1, tile.getMap().m5 != 2)))
		//	return Cmd.return_cmd_error(Str.STR_1005_NO_SUITABLE_RAILROAD_TRACK);

		if (!tile.IsTileType(TileTypes.MP_RAILWAY))
			return Cmd.return_cmd_error(Str.STR_1005_NO_SUITABLE_RAILROAD_TRACK);

		if(tile.getMap().m5 == 1)
			dir = 0;
		else if(tile.getMap().m5 == 2)
			dir = 1;
		else
			return Cmd.return_cmd_error(Str.STR_1005_NO_SUITABLE_RAILROAD_TRACK);
		
		
		if (!tile.CheckTileOwnership())
			return Cmd.CMD_ERROR;

		if (!tile.EnsureNoVehicle()) return Cmd.CMD_ERROR;

		tileh = tile.GetTileSlope(null);
		if (tileh != 0) {
			if (!Global._patches.build_on_slopes ||  TileIndex.IsSteepTileh(tileh) || 0==(tileh & (0x3 << dir)) || 0==(tileh & ~(0x3 << dir)))
				return Cmd.return_cmd_error(Str.STR_0007_FLAT_LAND_REQUIRED);
		}

		/* Check if there is an already existing, deleted, WayPoint close to us that we can reuse. */
		wp = FindDeletedWaypointCloseTo(tile);
		if (wp == null) {
			wp = AllocateWaypoint();
			if (wp == null) return Cmd.CMD_ERROR;

			wp.town_index = 0;
			wp.string = new StringID(Str.STR_NULL);
			wp.town_cn = 0;
		}

		if(0 != (flags & Cmd.DC_EXEC)) {
			final StationSpec spec = null;
			boolean reserved = Pbs.PBSTileReserved(tile) != 0;
			Landscape.ModifyTile(tile, TileTypes.MP_MAP2 | TileTypes.MP_MAP5, wp.index, RAIL_TYPE_WAYPOINT | dir);

			// TODO GetCustomStation
			//if (BitOps.GB(p1, 0, 8) < Station.GetNumCustomStations(STAT_CLASS_WAYP))
			//	spec = Station.GetCustomStation(STAT_CLASS_WAYP, BitOps.GB(p1, 0, 8));

			if (spec != null) {
				//SETBIT(Global._m[tile.getTile()].m3, 4);
				tile.setBit_m3(4);
				wp.stat_id = (byte) BitOps.GB(p1, 0, 8);
				wp.grfid = spec.grfid;
				wp.localidx = (byte) spec.localidx;
			} else {
				// Specified custom graphics do not exist, so use default.
				//CLRBIT(Global._m[tile.getTile()].m3, 4);
				tile.clrBit_m3(4);
				wp.stat_id = 0;
				wp.grfid = 0;
				wp.localidx = 0;
			}

			if (reserved) {
				Pbs.PBSReserveTrack(tile, dir);
			} else {
				Pbs.PBSClearTrack(tile, dir);
			}

			wp.deleted = 0;
			wp.xy = tile;
			wp.build_date = Global._date;

			if (wp.town_index == Str.STR_NULL)
				wp.MakeDefaultWaypointName();

			wp.UpdateWaypointSign();
			wp.RedrawWaypointSign();
		}

		return Global._price.build_train_depot;
	}

	/* Internal handler to delete a WayPoint */
	private void DoDeleteWaypoint()
	{
		Order order = new Order();

		xy = null;

		order.type = Order.OT_GOTO_WAYPOINT;
		order.station = index;
		Order.DeleteDestinationFromVehicleOrder(order);

		if (string.id != Str.STR_NULL)
			Global.DeleteName(string);

		RedrawWaypointSign();
	}

	/* Daily loop for waypoints */
	static void WaypointsDailyLoop()
	{
		_waypoint_pool.forEach((i,wp) -> {
			if( (0 != wp.deleted) && (0 == --wp.deleted) )
				wp.DoDeleteWaypoint();

		});
		/*
    WayPoint wp;

	// Check if we need to delete a WayPoint 
	for (wp = GetWaypoint(0); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null) 
    {
		if (wp.deleted && !--wp.deleted) {
			wp.DoDeleteWaypoint();
		}
	}
		 */
	}

	/* Remove a WayPoint */
	static int RemoveTrainWaypoint(TileIndex tile, int flags, boolean justremove)
	{
		WayPoint wp;

		/* Make sure it's a WayPoint */
		if (!tile.IsTileType(TileTypes.MP_RAILWAY) || !IsRailWaypoint(tile))
			return Cmd.CMD_ERROR;

		if (!tile.CheckTileOwnership() && !(Global._current_player.id == Owner.OWNER_WATER))
			return Cmd.CMD_ERROR;

		if (!tile.EnsureNoVehicle())
			return Cmd.CMD_ERROR;

		if(0 != (flags & Cmd.DC_EXEC)) {
			int direction = tile.getMap().m5 & RAIL_WAYPOINT_TRACK_MASK;

			wp = GetWaypointByTile(tile);

			wp.deleted = 30; // let it live for this many days before we do the actual deletion.
			wp.RedrawWaypointSign();

			if (justremove) {
				boolean reserved = Pbs.PBSTileReserved(tile) != 0;
				Landscape.ModifyTile(tile, TileTypes.MP_MAP2_CLEAR | TileTypes.MP_MAP5, 1<<direction);
				//CLRBIT(_m[tile].m3, 4);
				tile.clrBit_m3(4);
				tile.getMap().m4 = 0;
				if (reserved) {
					Pbs.PBSReserveTrack(tile, direction);
				} else {
					Pbs.PBSClearTrack(tile, direction);
				}
			} else {
				Landscape.DoClearSquare(tile);
				Rail.SetSignalsOnBothDir(tile, direction);
			}
		}

		return Global._price.remove_train_depot;
	}

	/** Delete a WayPoint
	 * @param x,y coordinates where WayPoint is to be deleted
	 * @param p1 unused
	 * @param p2 unused
	 */
	static int CmdRemoveTrainWaypoint(int x, int y, int flags, int p1, int p2)
	{
		TileIndex tile = TileIndex.TileVirtXY(x, y);
		Player.SET_EXPENSES_TYPE(Player.EXPENSES_CONSTRUCTION);
		return RemoveTrainWaypoint(tile, flags, true);
	}

	/** Rename a WayPoint.
	 * @param x,y unused
	 * @param p1 id of WayPoint
	 * @param p2 unused
	 */
	static int CmdRenameWaypoint(int x, int y, int flags, int p1, int p2)
	{
		WayPoint wp;
		StringID str;

		if (!IsWaypointIndex(p1)) return Cmd.CMD_ERROR;

		if (Global._cmd_text != null) {
			str = Global.AllocateNameUnique(Global._cmd_text, 0);
			if (str == null)
				return Cmd.CMD_ERROR;

			if(0 != (flags & Cmd.DC_EXEC)) {
				wp = GetWaypoint(p1);
				if (wp.string.id != Str.STR_NULL)
					Global.DeleteName(wp.string);

				wp.string = str;
				wp.town_cn = 0;

				wp.UpdateWaypointSign();
				Hal.MarkWholeScreenDirty();
			} else {
				Global.DeleteName(str);
			}
		} else {
			if(0 != (flags & Cmd.DC_EXEC)) {
				wp = GetWaypoint(p1);
				if (wp.string.id != Str.STR_NULL)
					Global.DeleteName(wp.string);

				wp.MakeDefaultWaypointName();
				wp.UpdateWaypointSign();
				Hal.MarkWholeScreenDirty();
			}
		}
		return 0;
	}

	/* This hacks together some dummy one-shot Station structure for a WayPoint. */
	Station ComposeWaypointStation(TileIndex tile)
	{
		WayPoint wp = WayPoint.GetWaypointByTile(tile);
		Station stat = new Station();

		stat.train_tile = stat.xy = wp.xy;
		stat.town = Town.GetTown(wp.town_index);
		stat.string_id = wp.string.id == Str.STR_NULL ? /* FIXME? */ 0 : wp.string.id;
		stat.build_date = wp.build_date;
		stat.class_id = 6;
		stat.stat_id = wp.stat_id;

		return stat;
	}

	//extern uint16 _custom_sprites_base;

	// TODO fixme
	/* Draw a WayPoint */
	static void DrawWaypointSprite(int x, int y, int stat_id, /* RailType */ int railtype)
	{
		final StationSpec stat;
		int relocation;
		final DrawTileSprites cust;
		//final DrawTileSeqStruct seq;
		final RailtypeInfo rti = Rail.GetRailTypeInfo(railtype);
		int ormod, img;

		ormod = Sprite.SPRITE_PALETTE(Sprite.PLAYER_SPRITE_COLOR(_local_player));

		x += 33;
		y += 17;

		// TODO stat = GetCustomStation(STAT_CLASS_WAYP, stat_id);
		//if (stat == null) {
			// stat is null for default waypoints and when WayPoint graphics are
			// not loaded.
			Rail.DrawDefaultWaypointSprite(x, y, railtype);
			return;
		//}
		/*
		relocation = GetCustomStationRelocation(stat, null, 1);
		// emulate station tile - open with building
		// add 1 to get the other direction
		cust = stat.renderdata[2];

		img = cust.ground_sprite.id;
		img += (img < _custom_sprites_base) ? rti.total_offset : railtype;

		if (img & PALETTE_MODIFIER_COLOR) img = (img & Sprite.SPRITE_MASK);
		Gfx.DrawSprite(img, x, y);

		//foreach_draw_tile_seq(seq, cust.seq) 
		//for (seq = cust.seq; ((byte) seq->delta_x) != 0x80; seq++)
		for( DrawTileSeqStruct seq : cust.seq )
		{
			if( ((byte) seq.delta_x) == 0x80 )
				break;
			
			Point pt = Point.RemapCoords(seq.delta_x, seq.delta_y, seq.delta_z);
			int image = seq.image + relocation;
			Gfx.DrawSprite((image & Sprite.SPRITE_MASK) | ormod, x + pt.x, y + pt.y);
		}
		*/
	}
	
	
	/* Fix savegames which stored waypoints in their old format * /
void FixOldWaypoints()
{
	WayPoint *wp;

	// Convert the old 'town_or_string', to 'string' / 'town' / 'town_cn' * /
	for (wp = GetWaypoint(0); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null) 
    {
		if (wp.xy == 0)
			continue;

		wp.town_index = ClosestTownFromTile(wp.xy, (int)-1).index;
		wp.town_cn = 0;
		if (wp.string & 0xC000) {
			wp.town_cn = wp.string & 0x3F;
			wp.string = STR_NULL;
		}
	}
}
	 */

	static void InitializeWaypoints()
	{
		_waypoint_pool.CleanPool();
		_waypoint_pool.AddBlockToPool();
	}
	/*
static final SaveLoad _waypoint_desc[] = {
	SLE_CONDVAR(WayPoint, xy, SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
	SLE_CONDVAR(WayPoint, xy, SLE_int, 6, 255),
	SLE_CONDVAR(WayPoint, town_index, SLE_UINT16, 12, 255),
	SLE_CONDVAR(WayPoint, town_cn, SLE_UINT8, 12, 255),
	SLE_VAR(WayPoint, string, SLE_UINT16),
	SLE_VAR(WayPoint, deleted, SLE_UINT8),

	SLE_CONDVAR(WayPoint, build_date, SLE_UINT16,  3, 255),
	SLE_CONDVAR(WayPoint, localidx,   SLE_UINT8,   3, 255),
	SLE_CONDVAR(WayPoint, grfid,      SLE_int, 17, 255),

	SLE_END()
};
	 */

	/*
	private static void Load_WAYP()
	{
		int index;

		while ((index = SlIterateArray()) != -1) {
			WayPoint wp;

			if (!AddBlockIfNeeded(&_waypoint_pool, index))
				error("Waypoints: failed loading savegame: too many waypoints");

			wp = GetWaypoint(index);
			SlObject(wp, _waypoint_desc);
		}
	}

	ChunkHandler chandler = new ChunkHandler( "CHKP", ChunkHandler.CH_ARRAY | ChunkHandler.CH_LAST ) {
		
		@Override
		void save_proc() {
			WayPoint wp;

			for (wp = GetWaypoint(0); wp != null; wp = (wp.index + 1 < GetWaypointPoolSize()) ? GetWaypoint(wp.index + 1) : null) 
			{
				if (wp.xy != null) {
					SlSetArrayIndex(wp.index);
					SlObject(wp, _waypoint_desc);
				}
			}
		}
		
		@Override
		void load_proc() {
			// TODO Auto-generated method stub
			
		}
	};
	
	final ChunkHandler _waypoint_chunk_handlers[] = {
			chandler
	};
	*/


}