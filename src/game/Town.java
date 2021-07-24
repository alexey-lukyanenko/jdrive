package game;

import java.util.Iterator;
import java.util.function.Consumer;
import game.util.BitOps;
import game.util.TownTables;

public class Town extends TownTables implements IPoolItem 
{

	TileIndex xy;

	// Current population of people and amount of houses.
	int num_houses;
	int population;

	// Town name
	int townnametype;
	int townnameparts;

	// NOSAVE: Location of name sign, UpdateTownVirtCoord updates this.
	ViewportSign sign;

	// Makes sure we don't build certain house types twice.
	byte flags12;

	// Which players have a statue?
	byte statues;

	// Sort index in listings
	byte sort_index_obsolete;

	// Player ratings as well as a mask that determines which players have a rating.
	byte have_ratings;
	int unwanted[]; // how many months companies aren't wanted by towns (bribe)
	PlayerID exclusivity;        // which player has exslusivity
	int exclusive_counter;     // months till the exclusivity expires
	int ratings[];

	// Maximum amount of passengers and mail that can be transported.
	int max_pass;
	int max_mail;
	int new_max_pass;
	int new_max_mail;
	int act_pass;
	int act_mail;
	int new_act_pass;
	int new_act_mail;

	// Amount of passengers that were transported.
	byte pct_pass_transported;
	byte pct_mail_transported;

	// Amount of food and paper that was transported. Actually a bit mask would be enough.
	int act_food;
	int act_water;
	int new_act_food;
	int new_act_water;

	// Time until we rebuild a house.
	byte time_until_rebuild;

	// When to grow town next time.
	byte grow_counter;
	byte growth_rate;

	// Fund buildings program in action?
	byte fund_buildings_months;

	// Fund road refinalruction in action?
	byte road_build_months;

	// Index in town array
	int index;

	// NOSAVE: UpdateTownRadius updates this given the house count.
	int radius[];


	public Town() {
		radius = new int[5];
		unwanted = new int[Global.MAX_PLAYERS]; // how many months companies aren't wanted by towns (bribe)
		ratings = new int[Global.MAX_PLAYERS];

	}

	// These refer to the maximums, so Appalling is -1000 to -400
	// MAXIMUM RATINGS BOUNDARIES
	public static final int RATING_MINIMUM 		= -1000;
	public static final int RATING_APPALLING 	= -400;
	public static final int RATING_VERYPOOR 	= -200;
	public static final int RATING_POOR 			= 0;
	public static final int RATING_MEDIOCRE		= 200;
	public static final int RATING_GOOD				= 400;
	public static final int RATING_VERYGOOD		= 600;
	public static final int RATING_EXCELLENT	= 800;
	public static final int RATING_OUTSTANDING= 1000; 	// OUTSTANDING

	public static final int RATING_MAXIMUM = RATING_OUTSTANDING;

	// RATINGS AFFECTING NUMBERS
	public static final int RATING_TREE_DOWN_STEP = -35;
	public static final int RATING_TREE_MINIMUM = RATING_MINIMUM;
	public static final int RATING_TREE_UP_STEP = 7;
	public static final int RATING_TREE_MAXIMUM = 220;

	public static final int RATING_TUNNEL_BRIDGE_DOWN_STEP = -250;
	public static final int RATING_TUNNEL_BRIDGE_MINIMUM = 0;

	public static final int RATING_INDUSTRY_DOWN_STEP = -1500;
	public static final int RATING_INDUSTRY_MINIMUM = RATING_MINIMUM;

	public static final int RATING_ROAD_DOWN_STEP = -50;
	public static final int RATING_ROAD_MINIMUM = -100;
	public static final int RATING_HOUSE_MINIMUM = RATING_MINIMUM;

	public static final int RATING_BRIBE_UP_STEP = 200;
	public static final int RATING_BRIBE_MAXIMUM = 800;
	public static final int RATING_BRIBE_DOWN_TO = -50; 					// XXX SHOULD BE SOMETHING LOWER?


	public static final int ROAD_REMOVE = 0;
	public static final int UNMOVEABLE_REMOVE = 1;
	public static final int TUNNELBRIDGE_REMOVE = 1;
	public static final int INDUSTRY_REMOVE = 2;


	public static final int HOUSE_TEMP_CHURCH    = 0x03;
	public static final int HOUSE_STADIUM        = 0x14;
	public static final int HOUSE_MODERN_STADIUM = 0x20;
	public static final int HOUSE_ARCT_CHURCH    = 0x3c;
	public static final int HOUSE_SNOW_CHURCH    = 0x3d;
	public static final int HOUSE_TROP_CHURCH    = 0x53;
	public static final int HOUSE_TOY_CHURCH     = 0x5b;

	
	


	private static IPoolItemFactory<Town> factory = new IPoolItemFactory<Town>() {		
		@Override
		public Town createObject() { return new Town(); }
	};
	private static MemoryPool<Town> _town_pool = new MemoryPool<Town>(factory);


	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Check if a Town really exists.
	 */
	public boolean IsValidTown()
	{
		return xy.getTile() != 0; /* XXX: Replace by INVALID_TILE someday */
	}

	/**
	 * Get the pointer to the town with index 'index'
	 */
	public static Town GetTown(int index)
	{
		return _town_pool.GetItemFromPool(index);
	}

	/**
	 * Get the current size of the TownPool
	 */
	static  int GetTownPoolSize()
	{
		return _town_pool.total_items();
	}

	static  boolean IsTownIndex(int index)
	{
		return index > 0 && index < GetTownPoolSize(); 
	}



	/* $Id: town_cmd.c 3339 2005-12-24 20:54:31Z tron $ */
























	/* Max towns: 64000 (8 * 8000) */
	//TOWN_POOL_BLOCK_SIZE_BITS = 3,       /* In bits, so (1 << 3) == 8 */
	//TOWN_POOL_MAX_BLOCKS      = 8000,

	/**
	 * Called if a new block is added to the town-pool
	 * /
	static void TownPoolNewBlock(int start_item)
	{
		Town t;

		FOR_ALL_TOWNS_FROM(t, start_item)
		t.index = start_item++;
	}*/

	/* Initialize the town-pool */
	//MemoryPool _town_pool = { "Towns", TOWN_POOL_MAX_BLOCKS, TOWN_POOL_BLOCK_SIZE_BITS, sizeof(Town), &TownPoolNewBlock, 0, 0, null };


	/* This is the base "normal" number of towns on the 8x8 map, when
	 * one town should get grown per tick. The other numbers of towns
	 * are then scaled based on that. */
	private static final int TOWN_GROWTH_FREQUENCY = 23;


	private static final int TOWN_HAS_CHURCH     = 0x02;
	private static final int TOWN_HAS_STADIUM    = 0x04;


	// Local
	private static int _grow_town_result;






	static void TownDrawHouseLift(final TileInfo ti)
	{
		AddChildSpriteScreen(0x5A3, 0xE, 0x3C - BitOps.GB(ti.tile.getMap().m1, 0, 7));
	}

	static TownDrawTileProc _town_draw_tile_procs[] = {
			Town::TownDrawHouseLift
	};


	static void DrawTile_Town(TileInfo ti)
	{
		final DrawTownTileStruct dcts;
		byte z;
		int image;

		/* Retrieve pointer to the draw town tile struct */
		{
			/* this "randomizes" on the (up to) 4 variants of a building */
			byte gfx   = ti.tile.getMap().m4;
			byte stage = (byte) BitOps.GB(ti.tile.getMap().m3, 6, 2);
			int variant;
			variant  = ti.x >> 4;
		variant ^= ti.x >> 6;
		variant ^= ti.y >> 4;
		variant -= ti.y >> 6;
		variant &= 3;
		dcts = _town_draw_tile_data[gfx << 4 | variant << 2 | stage];
		}

		z = (byte) ti.z;

		/* Add bricks below the house? */
		if(0 != (ti.tileh)) {
			AddSortableSpriteToDraw(SPR_FOUNDATION_BASE + ti.tileh, ti.x, ti.y, 16, 16, 7, z);
			AddChildSpriteScreen(dcts.sprite_1, 0x1F, 1);
			z += 8;
		} else {
			/* Else draw regular ground */
			Gfx.DrawGroundSprite(dcts.sprite_1);
		}

		/* Add a house on top of the ground? */
		image = dcts.sprite_2;
		if (image != 0) {
			if (Global._display_opt & DO_TRANS_BUILDINGS) 
				image = Sprite.RET_MAKE_TRANSPARENT(image);

			ViewPort.AddSortableSpriteToDraw(image,
					ti.x + dcts.subtile_x,
					ti.y + dcts.subtile_y,
					dcts.width + 1,
					dcts.height + 1,
					dcts.dz,
					z);

			if (Global._display_opt & DO_TRANS_BUILDINGS) return;
		}

		{
			int proc = dcts.proc - 1;

			if (proc >= 0) _town_draw_tile_procs[proc](ti);
		}
	}

	static int GetSlopeZ_Town(final TileInfo  ti)
	{
		int z = GetPartialZ(ti.x & 0xF, ti.y & 0xF, ti.tileh) + ti.z;
		if (ti.tileh != 0) z = (z & ~7) + 4;
		return (int) z;
	}

	static int GetSlopeTileh_Town(final TileInfo ti)
	{
		return ti.tileh;
	}

	static void AnimateTile_Town(TileIndex tile)
	{
		int old;
		int a,b;

		if( 0 != (Global._tick_counter & 3)) return;

		// If the house is not one with a lift anymore, then stop this animating.
		// Not exactly sure when this happens, but probably when a house changes.
		// Before this was just a return...so it'd leak animated tiles..
		// That bug seems to have been here since day 1??
		if (!(_housetype_extra_flags[tile.getMap().m4] & 0x20)) {
			TextEffect.DeleteAnimatedTile(tile);
			return;
		}

		if (0 == ((old = tile.getMap().m1) & 0x80)) 
		{
			int i;

			tile.getMap().m1 |= 0x80;

			do {
				i = (Hal.Random() & 7) - 1;
			} while (i < 0 || i == 1 || i * 6 == old);

			tile.getMap().m5 = (byte) BitOps.RETSB(tile.getMap().m5, 0, 6, i);
		}

		a = BitOps.GB(tile.getMap().m1, 0, 7);
		b = BitOps.GB(tile.getMap().m5, 0, 6) * 6;
		a += (a < b) ? 1 : -1;
		tile.getMap().m1 = BitOps.RETSB(tile.getMap().m1, 0, 7, a);

		if (a == b) {
			tile.getMap().m1 &= 0x7F;
			tile.getMap().m5 &= 0x40;
			TextEffect.DeleteAnimatedTile(tile);
		}

		MarkTileDirtyByTile(tile);
	}

	//static void UpdateTownRadius(Town t);

	static boolean IsCloseToTown(TileIndex tile, int dist)
	{
		//FOR_ALL_TOWNS(t)
		for(Iterator<Town> i = _town_pool.getIterator(); i.hasNext();)
		{
			final Town t = i.next();
			if (t.xy != null && Map.DistanceManhattan(tile, t.xy) < dist) 
				return true;
		}
		return false;
	}

	static void MarkTownSignDirty(Town t)
	{
		ViewPort.MarkAllViewportsDirty(
				t.sign.left-6,
				t.sign.top-3,
				t.sign.left+t.sign.width_1*4+12,
				t.sign.top + 45
				);
	}

	static void UpdateTownVirtCoord(Town t)
	{
		Point pt;

		MarkTownSignDirty(t);
		pt = Point.RemapCoords2(t.xy.TileX() * 16, t.xy.TileY() * 16);
		Global.SetDParam(0, t.index);
		Global.SetDParam(1, t.population);
		UpdateViewportSignPos(&t.sign, pt.x, pt.y - 24,
				Global._patches.population_in_label ? STR_TOWN_LABEL_POP : STR_TOWN_LABEL);
		MarkTownSignDirty(t);
	}

	static void ChangePopulation(Town t, int mod)
	{
		t.population += mod;
		Window.InvalidateWindow(Window.WC_TOWN_VIEW, t.index);
		UpdateTownVirtCoord(t);

		if (_town_sort_order & 2) _town_sort_dirty = true;
	}

	int GetWorldPopulation()
	{
		int pop;
		final Town* t;

		pop = 0;
		FOR_ALL_TOWNS(t) pop += t.population;
		return pop;
	}

	static void MakeSingleHouseBigger(TileIndex tile)
	{
		assert(tile.IsTileType( TileTypes.MP_HOUSE));

		if( 0!= (tile.getMap().m5 & 0x80)) return;

		tile.getMap().m5 = (byte) BitOps.RETAB(tile.getMap().m5, 0, 3, 1);
		if (BitOps.GB(tile.getMap().m5, 0, 3) != 0) return;

		tile.getMap().m3 = (byte) (tile.getMap().m3 + 0x40);

		if ((tile.getMap().m3 & 0xC0) == 0xC0) {
			ChangePopulation(GetTown(tile.getMap().m2), _housetype_population[tile.getMap().m4]);
		}
		MarkTileDirtyByTile(tile);
	}

	static void MakeTownHouseBigger(TileIndex tile)
	{
		int flags = _house_more_flags[tile.getMap().m4];
		if( 0 != (flags & 8)) MakeSingleHouseBigger(TILE_ADDXY(tile, 0, 0));
		if( 0 != (flags & 4)) MakeSingleHouseBigger(TILE_ADDXY(tile, 0, 1));
		if( 0 != (flags & 2)) MakeSingleHouseBigger(TILE_ADDXY(tile, 1, 0));
		if( 0 != (flags & 1)) MakeSingleHouseBigger(TILE_ADDXY(tile, 1, 1));
	}

	static void TileLoop_Town(TileIndex tile)
	{
		int house;
		Town t;
		int r;

		if ((tile.getMap().m3 & 0xC0) != 0xC0) {
			MakeTownHouseBigger(tile);
			return;
		}

		house = tile.getMap().m4;
		if (_housetype_extra_flags[house] & 0x20 &&
				!(tile.getMap().m5 & 0x80) &&
				BitOps.CHANCE16(1, 2) &&
				AddAnimatedTile(tile)) {
			tile.getMap().m5 = (tile.getMap().m5 & 0x40) | 0x80;
		}

		t = GetTown(tile.getMap().m2);

		r = Random();

		if (BitOps.GB(r, 0, 8) < _housetype_population[house]) {
			int amt = BitOps.GB(r, 0, 8) / 8 + 1;
			int moved;

			if (_economy.fluct <= 0) amt = (amt + 1) >> 1;
			t.new_max_pass += amt;
			moved = MoveGoodsToStation(tile, 1, 1, AcceptedCargo.CT_PASSENGERS, amt);
			t.new_act_pass += moved;
		}

		if (BitOps.GB(r, 8, 8) < _housetype_mailamount[house] ) {
			int amt = BitOps.GB(r, 8, 8) / 8 + 1;
			int moved;

			if (_economy.fluct <= 0) amt = (amt + 1) >> 1;
			t.new_max_mail += amt;
			moved = MoveGoodsToStation(tile, 1, 1, AcceptedCargo.CT_MAIL, amt);
			t.new_act_mail += moved;
		}

		if (_house_more_flags[house] & 8 && (t.flags12 & 1) && --t.time_until_rebuild == 0) {
			t.time_until_rebuild = BitOps.GB(r, 16, 6) + 130;

			Global._current_player = Owner.OWNER_TOWN;

			ClearTownHouse(t, tile);

			// rebuild with another house?
			if (BitOps.GB(r, 24, 8) >= 12) DoBuildTownHouse(t, tile);

			Global._current_player = Owner.OWNER_NONE;
		}
	}

	static void ClickTile_Town(TileIndex tile)
	{
		/* not used */
	}

	static int ClearTile_Town(TileIndex tile, byte flags)
	{
		int house, rating;
		int cost;
		Town t;

		// safety checks
		if (!tile.EnsureNoVehicle()) return Cmd.CMD_ERROR;
		if (flags&Cmd.DC_AUTO && !(flags&Cmd.DC_AI_BUILDING)) return_cmd_error(STR_2004_BUILDING_MUST_BE_DEMOLISHED);

		house = tile.getMap().m4;
		cost = _price.remove_house * _housetype_remove_cost[house] >> 8;

		rating = _housetype_remove_ratingmod[house];
		_cleared_town_rating += rating;
		_cleared_town = t = GetTown(tile.getMap().m2);

		if (Global._current_player < Global.MAX_PLAYERS) {
			if (rating > t.ratings[Global._current_player] && !(flags & Cmd.DC_NO_TOWN_RATING) && !_cheats.magic_bulldozer.value) {
				Global.SetDParam(0, t.index);
				return_cmd_error(STR_2009_LOCAL_AUTHORITY_REFUSES);
			}
		}

		if (flags & Cmd.DC_EXEC) {
			ChangeTownRating(t, -rating, RATING_HOUSE_MINIMUM);
			ClearTownHouse(t, tile);
		}

		return cost;
	}

	static AcceptedCargo GetAcceptedCargo_Town(TileIndex tile)
	{
		AcceptedCargo ac = new AcceptedCargo();
		byte type = tile.getMap().m4;

		ac.ct[AcceptedCargo.CT_PASSENGERS] = _housetype_cargo_passengers[type];
		ac.ct[AcceptedCargo.CT_MAIL]       = _housetype_cargo_mail[type];
		ac.ct[AcceptedCargo.CT_GOODS]      = _housetype_cargo_goods[type];
		ac.ct[AcceptedCargo.CT_FOOD]       = _housetype_cargo_food[type];
	}

	static TileDesc GetTileDesc_Town(TileIndex tile)
	{
		TileDesc td = new TileDesc();
		td.str = _town_tile_names[tile.getMap().m4];
		if ((tile.getMap().m3 & 0xC0) != 0xC0) {
			Global.SetDParamX(td.dparam, 0, td.str);
			td.str = STR_2058_UNDER_CONSTRUCTION;
		}

		td.owner = Owner.OWNER_TOWN;
	}

	static int GetTileTrackStatus_Town(TileIndex tile, int mode)
	{
		/* not used */
		return 0;
	}

	static void ChangeTileOwner_Town(TileIndex tile, PlayerID old_player, PlayerID new_player)
	{
		/* not used */
	}


	static final TileIndexDiffC _roadblock_tileadd[] = {
			new TileIndexDiffC( 0, -1),
			new TileIndexDiffC( 1,  0),
			new TileIndexDiffC( 0,  1),
			new TileIndexDiffC(-1,  0),

			// Store the first 3 elements again.
			// Lets us rotate without using &3.
			new TileIndexDiffC( 0, -1),
			new TileIndexDiffC( 1,  0),
			new TileIndexDiffC( 0,  1)
	};

	static void TownTickHandler(Town t)
	{
		if (t.flags12&1) {
			int i = t.grow_counter - 1;
			if (i < 0) {
				if (GrowTown(t)) {
					i = t.growth_rate;
				} else {
					i = 0;
				}
			}
			t.grow_counter = i;
		}

		UpdateTownRadius(t);
	}

	static void OnTick_Town()
	{
		if (Global._game_mode == GameModes.GM_EDITOR) return;

		/* Make sure each town's tickhandler invocation frequency is about the
		 * same - TOWN_GROWTH_FREQUENCY - independent on the number of towns. */
		for (_cur_town_iter += GetTownPoolSize();
				_cur_town_iter >= TOWN_GROWTH_FREQUENCY;
				_cur_town_iter -= TOWN_GROWTH_FREQUENCY) {
			int i = _cur_town_ctr;
			Town t;

			if (++_cur_town_ctr >= GetTownPoolSize())
				_cur_town_ctr = 0;

			t = GetTown(i);

			if (t.xy != 0) TownTickHandler(t);
		}
	}

	static byte GetTownRoadMask(TileIndex tile)
	{
		byte b = GetRoadBitsByTile(tile);
		byte r = 0;

		if (b & 0x01) r |= 10;
		if (b & 0x02) r |=  5;
		if (b & 0x04) r |=  9;
		if (b & 0x08) r |=  6;
		if (b & 0x10) r |=  3;
		if (b & 0x20) r |= 12;
		return r;
	}

	static boolean IsRoadAllowedHere(TileIndex tile, int dir)
	{
		int k;
		int slope;

		// If this assertion fails, it might be because the world contains
		//  land at the edges. This is not ok.
		TILE_ASSERT(tile);

		for (;;) {
			// Check if there already is a road at this point?
			if (GetRoadBitsByTile(tile) == 0) {
				// No, try to build one in the direction.
				// if that fails clear the land, and if that fails exit.
				// This is to make sure that we can build a road here later.
				if (Cmd.CmdFailed(Cmd.DoCommandByTile(tile, (dir&1)?0xA:0x5, 0, Cmd.DC_AUTO, Cmd.CMD_BUILD_ROAD)) &&
						Cmd.CmdFailed(Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_AUTO, Cmd.CMD_LANDSCAPE_CLEAR)))
					return false;
			}

			slope = GetTileSlope(tile, null);
			if (slope == 0) {
				no_slope:
					// Tile has no slope
					// Disallow the road if any neighboring tile has a road.
					if (BitOps.HASBIT(GetTownRoadMask(TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[dir+1]))), dir^2) ||
							BitOps.HASBIT(GetTownRoadMask(TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[dir+3]))), dir^2) ||
							BitOps.HASBIT(GetTownRoadMask(TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[dir+1]) + ToTileIndexDiff(_roadblock_tileadd[dir+2]))), dir) ||
							BitOps.HASBIT(GetTownRoadMask(TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[dir+3]) + ToTileIndexDiff(_roadblock_tileadd[dir+2]))), dir))
						return false;

				// Otherwise allow
				return true;
			}

			// If the tile is not a slope in the right direction, then
			// maybe terraform some.
			if ((k = (dir&1)?0xC:0x9) != slope && (k^0xF) != slope) {
				int r = Hal.Random();

				if (BitOps.CHANCE16I(1, 8, r) && !Global._generating_world) {
					int res;

					if (BitOps.CHANCE16I(1, 16, r)) {
						res = Cmd.DoCommandByTile(tile, slope, 0, Cmd.DC_EXEC | Cmd.DC_AUTO | Cmd.DC_NO_WATER,
								Cmd.CMD_TERRAFORM_LAND);
					} else {
						res = Cmd.DoCommandByTile(tile, slope^0xF, 1, Cmd.DC_EXEC | Cmd.DC_AUTO | Cmd.DC_NO_WATER,
								Cmd.CMD_TERRAFORM_LAND);
					}
					if (Cmd.CmdFailed(res) && BitOps.CHANCE16I(1, 3, r)) {
						// We can consider building on the slope, though.
						goto no_slope;
					}
				}
				return false;
			}
			return true;
		}
	}

	static boolean TerraformTownTile(TileIndex tile, int edges, int dir)
	{
		int r;

		TILE_ASSERT(tile);

		r = Cmd.DoCommandByTile(tile, edges, dir, Cmd.DC_AUTO | Cmd.DC_NO_WATER, Cmd.CMD_TERRAFORM_LAND);
		if (Cmd.CmdFailed(r) || r >= 126 * 16) return false;
		Cmd.DoCommandByTile(tile, edges, dir, Cmd.DC_AUTO | Cmd.DC_NO_WATER | Cmd.DC_EXEC, Cmd.CMD_TERRAFORM_LAND);
		return true;
	}

	static void LevelTownLand(TileIndex tile)
	{
		TileInfo ti;

		TILE_ASSERT(tile);

		// Don't terraform if land is plain or if there's a house there.
		FindLandscapeHeightByTile(&ti, tile);
		if (ti.tileh == 0 || ti.type == TileTypes.MP_HOUSE) return;

		// First try up, then down
		if (!TerraformTownTile(tile, ~ti.tileh & 0xF, 1)) {
			TerraformTownTile(tile, ti.tileh & 0xF, 0);
		}
	}


	static void GrowTownInTile(TileIndex tile_ptr, int mask, int block, Town t1)
	{
		int a,b,rcmd;
		TileIndex tmptile;
		TileInfo ti;
		int i;
		int j;
		TileIndex tile = *tile_ptr;

		TILE_ASSERT(tile);

		if (mask == 0) {
			// Tile has no road. First reset the status counter
			// to say that this is the last iteration.
			_grow_town_result = 0;

			// Remove hills etc
			LevelTownLand(tile);

			// Is a road allowed here?
			if (!IsRoadAllowedHere(tile, block)) return;

			// Randomize new road block numbers
			a = block;
			b = block ^ 2;
			if (BitOps.CHANCE16(1, 4)) {
				do {
					a = BitOps.GB(Random(), 0, 2);
				} while(a == b);
			}

			if (!IsRoadAllowedHere(TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[a])), a)) {
				// A road is not allowed to continue the randomized road,
				//   return if the road we're trying to build is curved.
				if (a != (b ^ 2)) return;

				// Return if neither side of the new road is a house
				if (!IsTileType(TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[a + 1])), TileTypes.MP_HOUSE) &&
						!IsTileType(TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[a + 3])), TileTypes.MP_HOUSE))
					return;

				// That means that the road is only allowed if there is a house
				//  at any side of the new road.
			}
			rcmd = (1 << a) + (1 << b);

		} else if (block < 5 && !BitOps.HASBIT(mask,block^2)) {
			// Continue building on a partial road.
			// Always OK.
			_grow_town_result = 0;
			rcmd = 1 << (block ^ 2);
		} else {

			// Reached a tunnel? Then continue at the other side of it.
			if (tile.IsTileType( TileTypes.MP_TUNNELBRIDGE) && (tile.getMap().m5& ~3) == 4) {
				FindLengthOfTunnelResult flotr = FindLengthOfTunnel(tile, BitOps.GB(tile.getMap().m5, 0, 2));
				*tile_ptr = flotr.tile;
				return;
			}

			// For any other kind of tunnel/bridge, bail out.
			if (tile.IsTileType( TileTypes.MP_TUNNELBRIDGE)) return;

			// Possibly extend the road in a direction.
			// Randomize a direction and if it has a road, bail out.
			i = BitOps.GB(Random(), 0, 2);
			if (BitOps.HASBIT(mask, i)) return;

			// This is the tile we will reach if we extend to this direction.
			tmptile = TILE_ADD(tile, ToTileIndexDiff(_roadblock_tileadd[i]));

			// Don't do it if it reaches to water.
			if (tmptile.IsWaterTile()) return;

			// Build a house at the edge. 60% chance or
			//  always ok if no road allowed.
			if (!IsRoadAllowedHere(tmptile, i) || BitOps.CHANCE16(6, 10)) {
				// But not if there already is a house there.
				if (!IsTileType(tmptile, TileTypes.MP_HOUSE)) {
					// Level the land if possible
					LevelTownLand(tmptile);

					// And build a house.
					// Set result to -1 if we managed to build it.
					if (BuildTownHouse(t1, tmptile)) _grow_town_result = -1;
				}
				return;
			}

			_grow_town_result = 0;
			rcmd = 1 << i;
		}

		FindLandscapeHeightByTile(&ti, tile);

		// Return if a water tile
		if (ti.type == TileTypes.MP_WATER && ti.map5 == 0) return;

		// Determine direction of slope,
		//  and build a road if not a special slope.
		if ((i=0,ti.tileh != 3) &&
				(i++,ti.tileh != 9) &&
				(i++,ti.tileh != 12) &&
				(i++,ti.tileh != 6)) {
			build_road_and_exit:
				if (!Cmd.CmdFailed(Cmd.DoCommandByTile(tile, rcmd, t1.index, Cmd.DC_EXEC | Cmd.DC_AUTO | Cmd.DC_NO_WATER, Cmd.CMD_BUILD_ROAD)))
					_grow_town_result = -1;
			return;
		}

		tmptile = tile;

		// Now it contains the direction of the slope
		j = -11;	// max 11 tile long bridges
		do {
			if (++j == 0)
				goto build_road_and_exit;
			tmptile = TILE_MASK(tmptile + TileOffsByDir(i));
		} while (tmptile.IsWaterTile());

		// no water tiles in between?
		if (j == -10)
			goto build_road_and_exit;

		// Quit if it selecting an appropiate bridge type fails a large number of times.
		j = 22;
		{
			int bridge_len = GetBridgeLength(tile, tmptile);
			do {
				byte bridge_type = RandomRange(MAX_BRIDGES - 1);
				if (CheckBridge_Stuff(bridge_type, bridge_len)) {
					if (!Cmd.CmdFailed(Cmd.DoCommandByTile(tile, tmptile, 0x8000 + bridge_type, Cmd.DC_EXEC | Cmd.DC_AUTO, Cmd.CMD_BUILD_BRIDGE)))
						_grow_town_result = -1;

					// obviously, if building any bridge would fail, there is no need to try other bridge-types
					return;
				}
			} while(--j != 0);
		}
	}



	// Returns true if a house was built, or no if the build failed.
	static int GrowTownAtRoad(Town t, TileIndex tile)
	{
		int mask;
		int block = 5; // special case

		TILE_ASSERT(tile);

		// Number of times to search.
		_grow_town_result = 10 + t.num_houses * 4 / 9;

		do {
			// Get a bitmask of the road blocks on a tile
			mask = GetTownRoadMask(tile);

			// Try to grow the town from this point
			GrowTownInTile(&tile,mask,block,t);

			// Exclude the source position from the bitmask
			// and return if no more road blocks available
			CLRBIT(mask, (block ^ 2));
			if (mask == 0)
				return _grow_town_result;

			// Select a random bit from the blockmask, walk a step
			// and continue the search from there.
			do block = Random() & 3; while (!BitOps.HASBIT(mask,block));
			tile += ToTileIndexDiff(_roadblock_tileadd[block]);

			if (tile.IsTileType( TileTypes.MP_STREET)) {
				/* Don't allow building over roads of other cities */
				if (IsTileOwner(tile, Owner.OWNER_TOWN) && GetTown(tile.getMap().m2) != t)
					_grow_town_result = -1;
				else if (Global._game_mode == GameModes.GM_EDITOR) {
					/* If we are in the SE, and this road-piece has no town owner yet, it just found an
					 *  owner :) (happy happy happy road now) */
					SetTileOwner(tile, Owner.OWNER_TOWN);
					tile.getMap().m2 = t.index;
				}
			}

			// Max number of times is checked.
		} while (--_grow_town_result >= 0);

		return (_grow_town_result == -2);
	}

	// Generate a random road block
	// The probability of a straight road
	// is somewhat higher than a curved.
	static int GenRandomRoadBits()
	{
		int r = Hal.Random();
		int a = BitOps.GB(r, 0, 2);
		int b = BitOps.GB(r, 8, 2);
		if (a == b) b ^= 2;
		return (1 << a) + (1 << b);
	}

	static final TileIndexDiffC _town_coord_mod[] = {
			new TileIndexDiffC(-1,  0),
			new TileIndexDiffC( 1,  1),
			new TileIndexDiffC( 1, -1),
			new TileIndexDiffC(-1, -1),
			new TileIndexDiffC(-1,  0),
			new TileIndexDiffC( 0,  2),
			new TileIndexDiffC( 2,  0),
			new TileIndexDiffC( 0, -2),
			new TileIndexDiffC(-1, -1),
			new TileIndexDiffC(-2,  2),
			new TileIndexDiffC( 2,  2),
			new TileIndexDiffC( 2, -2),
			new TileIndexDiffC( 0,  0)
	};

	// Grow the town
	// Returns true if a house was built, or no if the build failed.
	boolean GrowTown(Town t)
	{
		TileIndex tile;
		final TileIndexDiffC ptr;
		TileInfo ti;
		PlayerID old_player;


		// Current player is a town
		old_player = Global._current_player;
		Global._current_player = Owner.OWNER_TOWN;

		// Find a road that we can base the finalruction on.
		tile = t.xy;
		for (ptr = _town_coord_mod; ptr != endof(_town_coord_mod); ++ptr) {
			if (GetRoadBitsByTile(tile) != 0) {
				int r = GrowTownAtRoad(t, tile);
				Global._current_player = old_player;
				return r;
			}
			tile = TILE_ADD(tile, ToTileIndexDiff(*ptr));
		}

		// No road available, try to build a random road block by
		// clearing some land and then building a road there.
		tile = t.xy;
		for (ptr = _town_coord_mod; ptr != endof(_town_coord_mod); ++ptr) {
			FindLandscapeHeightByTile(&ti, tile);

			// Only work with plain land that not already has a house with map5=0
			if (ti.tileh == 0 && (ti.type != TileTypes.MP_HOUSE || ti.map5 != 0)) {
				if (!Cmd.CmdFailed(Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_AUTO, Cmd.CMD_LANDSCAPE_CLEAR))) {
					Cmd.DoCommandByTile(tile, GenRandomRoadBits(), t.index, Cmd.DC_EXEC | Cmd.DC_AUTO, Cmd.CMD_BUILD_ROAD);
					Global._current_player = old_player;
					return true;
				}
			}
			tile = TILE_ADD(tile, ToTileIndexDiff(*ptr));
		}

		Global._current_player = old_player;
		return false;
	}

	static final int _town_radius_data[][] = {
			{ 4,  0,  0,  0,  0}, // 0
			{16,  0,  0,  0,  0},
			{25,  0,  0,  0,  0},
			{36,  0,  0,  0,  0},
			{49,  0,  4,  0,  0},
			{64,  0,  4,  0,  0}, // 20
			{64,  0,  9,  0,  1},
			{64,  0,  9,  0,  4},
			{64,  0, 16,  0,  4},
			{81,  0, 16,  0,  4},
			{81,  0, 16,  0,  4}, // 40
			{81,  0, 25,  0,  9},
			{81, 36, 25,  0,  9},
			{81, 36, 25, 16,  9},
			{81, 49,  0, 25,  9},
			{81, 64,  0, 25,  9}, // 60
			{81, 64,  0, 36,  9},
			{81, 64,  0, 36, 16},
			{100, 81,  0, 49, 16},
			{100, 81,  0, 49, 25},
			{121, 81,  0, 49, 25}, // 80
			{121, 81,  0, 49, 25},
			{121, 81,  0, 49, 36}, // 88
	};

	static void UpdateTownRadius(Town t)
	{

		if (t.num_houses < 92) {
			t.radius = new int[5];			
			System.arraycopy(_town_radius_data[t.num_houses / 4], 0, t.radius, 0, t.radius.length);
		} else {
			int mass = t.num_houses / 8;
			// At least very roughly extrapolate. Empirical numbers dancing between
			// overwhelming by cottages and skyscrapers outskirts.
			t.radius[0] = mass * mass;
			// Actually we are proportional to sqrt() but that's right because
			// we are covering an area.
			t.radius[1] = mass * 7;
			t.radius[2] = 0;
			t.radius[3] = mass * 4;
			t.radius[4] = mass * 3;
			//debug("%d (.%d): %d %d %d %d\n", t.num_houses, mass, t.radius[0], t.radius[1], t.radius[3], t.radius[4]);
		}
	}

	static boolean CreateTownName(int []townnameparts)
	{
		Town t2;
		String buf1;
		String buf2;
		int r;
		/* Do not set too low tries, since when we run out of names, we loop
		 * for #tries only one time anyway - then we stop generating more
		 * towns. Do not show it too high neither, since looping through all
		 * the other towns may take considerable amount of time (10000 is
		 * too much). */
		int tries = 1000;
		int townnametype = SPECSTR_TOWNNAME_START + Global._opt.town_name;

		assert(townnameparts != null);

		for(;;) {
			restart:
				r = Hal.Random();

			Global.SetDParam(0, r);
			buf1 = Global.GetString(townnametype);

			// Check size and width
			//if (strlen(buf1) >= 31 || Global.GetStringWidth(buf1) > 130) continue;
			if (Global.GetStringWidth(buf1) > 130) continue; // TODO Why?

			FOR_ALL_TOWNS(t2) 
			{
				if (t2.xy != null) {
					// We can't just compare the numbers since
					// several numbers may map to a single name.
					Global.SetDParam(0, t2.index);
					buf2 = Global.GetString(STR_TOWN);
					if (buf1.equals(buf2)) {
						if (tries-- < 0) return false;
						goto restart;
					}
				}
			}
			townnameparts[0] = r;
			return true;
		}
	}

	void UpdateTownMaxPass(Town t)
	{
		t.max_pass = t.population >> 3;
						t.max_mail = t.population >> 4;
	}

	static void DoCreateTown(Town t, TileIndex tile, int townnameparts)
	{
		int x, i;

		// clear the town struct
		i = t.index;
		memset(t, 0, sizeof(Town));
		t.index = i;

		t.xy = tile;
		t.num_houses = 0;
		t.time_until_rebuild = 10;
		UpdateTownRadius(t);
		t.flags12 = 0;
		t.population = 0;
		t.grow_counter = 0;
		t.growth_rate = 250;
		t.new_max_pass = 0;
		t.new_max_mail = 0;
		t.new_act_pass = 0;
		t.new_act_mail = 0;
		t.max_pass = 0;
		t.max_mail = 0;
		t.act_pass = 0;
		t.act_mail = 0;

		t.pct_pass_transported = 0;
		t.pct_mail_transported = 0;
		t.fund_buildings_months = 0;
		t.new_act_food = 0;
		t.new_act_water = 0;
		t.act_food = 0;
		t.act_water = 0;

		for(i = 0; i != Global.MAX_PLAYERS; i++)
			t.ratings[i] = 500;

		t.have_ratings = 0;
		t.exclusivity = (byte)-1;
		t.exclusive_counter = 0;
		t.statues = 0;

		t.townnametype = SPECSTR_TOWNNAME_START + _opt.town_name;
		t.townnameparts = townnameparts;

		UpdateTownVirtCoord(t);
		_town_sort_dirty = true;

		x = (Random() & 0xF) + 8;
		if (Global._game_mode == GameModes.GM_EDITOR)
			x = _new_town_size * 16 + 3;

		t.num_houses += x;
		UpdateTownRadius(t);

		i = x * 4;
		do {
			GrowTown(t);
		} while (--i);

		t.num_houses -= x;
		UpdateTownRadius(t);
		UpdateTownMaxPass(t);
	}

	static Town AllocateTown()
	{
		Town t;
		FOR_ALL_TOWNS(t) {
			if (t.xy == 0) {
				int index = t.index;

				if (t.index > _total_towns)
					_total_towns = t.index;

				memset(t, 0, sizeof(Town));
				t.index = index;

				return t;
			}
		}

		/* Check if we can add a block to the pool */
		if (AddBlockToPool(&_town_pool))
			return AllocateTown();

		return null;
	}

	/** Create a new town.
	 * This obviously only works in the scenario editor. Function not removed
	 * as it might be possible in the future to fund your own town :)
	 * @param x,y coordinates where town is built
	 * @param p1 unused
	 * @param p2 unused
	 */
	int CmdBuildTown(int x, int y, int flags, int p1, int p2)
	{
		TileIndex tile = TileVirtXY(x, y);
		TileInfo ti;
		Town t;
		int townnameparts;

		/* Only in the scenario editor */
		if (Global._game_mode != GameModes.GM_EDITOR) return Cmd.CMD_ERROR;

		Player.SET_Player.EXPENSES_TYPE(EXPENSES_OTHER);

		// Check if too close to the edge of map
		if (DistanceFromEdge(tile) < 12)
			return_cmd_error(STR_0237_TOO_CLOSE_TO_EDGE_OF_MAP);

		// Can only build on clear flat areas.
		FindLandscapeHeightByTile(&ti, tile);
		if (ti.type != TileTypes.MP_CLEAR || ti.tileh != 0)
			return_cmd_error(STR_0239_SITE_UNSUITABLE);

		// Check distance to all other towns.
		if (IsCloseToTown(tile, 20))
			return_cmd_error(STR_0238_TOO_CLOSE_TO_ANOTHER_TOWN);

		// Get a unique name for the town.
		if (!CreateTownName(&townnameparts))
			return_cmd_error(STR_023A_TOO_MANY_TOWNS);

		// Allocate town struct
		t = AllocateTown();
		if (t == null) return_cmd_error(STR_023A_TOO_MANY_TOWNS);

		// Create the town
		if (flags & Cmd.DC_EXEC) {
			_generating_world = true;
			DoCreateTown(t, tile, townnameparts);
			_generating_world = false;
		}
		return 0;
	}

	Town CreateRandomTown(int attempts)
	{
		TileIndex tile;
		TileInfo ti;
		Town t;
		int townnameparts;

		do {
			// Generate a tile index not too close from the edge
			tile = RandomTile();
			if (DistanceFromEdge(tile) < 20)
				continue;

			// Make sure the tile is plain
			FindLandscapeHeightByTile(&ti, tile);
			if (ti.type != TileTypes.MP_CLEAR || ti.tileh != 0)
				continue;

			// Check not too close to a town
			if (IsCloseToTown(tile, 20))
				continue;

			// Get a unique name for the town.
			if (!CreateTownName(&townnameparts))
				break;

			// Allocate a town struct
			t = AllocateTown();
			if (t == null)
				break;

			DoCreateTown(t, tile, townnameparts);
			return t;
		} while (--attempts);
		return null;
	}

	static final byte _num_initial_towns[] = {11, 23, 46};

	boolean GenerateTowns()
	{
		int num = 0;
		int n = ScaleByMapSize(_num_initial_towns[_opt.diff.number_towns] + (Random() & 7));

		do {
			if (CreateRandomTown(20) != null) 	//try 20 times for the first loop
				num++;
		} while (--n);

		// give it a last try, but now more aggressive
		if (num == 0 && CreateRandomTown(10000) == null) {
			Town t;
			FOR_ALL_TOWNS(t) { if (IsValidTown(t)) {num = 1; break;}}

			//XXX can we handle that more gracefully?
			if (num == 0) error("Could not generate any town");
			return false;
		}

		return true;
	}

	static boolean CheckBuildHouseMode(Town t1, TileIndex tile, int tileh, int mode)
	{
		int b;
		int slope;

		static final byte _masks[] = {
				0xC,0x3,0x9,0x6,
				0x3,0xC,0x6,0x9,
		};

		slope = GetTileSlope(tile, null);
		if (slope & 0x10)
			return false;

		b = 0;
		if ((slope & 0xF && ~slope & _masks[mode])) b = ~b;
		if ((tileh & 0xF && ~tileh & _masks[mode+4])) b = ~b;
		if (b)
			return false;

		return !Cmd.CmdFailed(Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_EXEC | Cmd.DC_AUTO | Cmd.DC_NO_WATER, Cmd.CMD_LANDSCAPE_CLEAR));
	}

	int GetTownRadiusGroup(final Town t, TileIndex tile)
	{
		int dist;
		int i,smallest;

		dist = Map.DistanceSquare(tile, t.xy);
		if (t.fund_buildings_months != 0 && dist <= 25)
			return 4;

		smallest = 0;
		for (i = 0; i != t.radius.length; i++) {
			if (dist < t.radius[i])
				smallest = i;
		}

		return smallest;
	}

	static final TileIndexDiffC _tile_add[] = {
			new TileIndexDiffC(0    , 0    ),
			new TileIndexDiffC(0 - 0, 1 - 0),
			new TileIndexDiffC(1 - 0, 0 - 1),
			new TileIndexDiffC(1 - 1, 1 - 0)
	};

	static boolean CheckFree2x2Area(Town t1, TileIndex tile)
	{
		int i;

		for(i=0; i!=4; i++) {
			tile += ToTileIndexDiff(_tile_add[i]);

			if (TileIndex.GetTileSlope(tile, null))
				return false;

			if (Cmd.CmdFailed(Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_EXEC | Cmd.DC_AUTO | Cmd.DC_NO_WATER | Cmd.DC_FORCETEST, Cmd.CMD_LANDSCAPE_CLEAR)))
				return false;
		}

		return true;
	}

	static void DoBuildTownHouse(Town t, TileIndex tile)
	{
		int i;
		int bitmask;
		int house;
		int slope;
		int z;
		int oneof;

		// Above snow?
		slope = GetTileSlope(tile, &z);

		// Get the town zone type
		{
			int rad = GetTownRadiusGroup(t, tile);

			int land = _opt.landscape;
			if (land == LT_HILLY && z >= _opt.snow_line)
				land = -1;

			bitmask = (1 << rad) + (1 << (land + 12));
		}

		// bits 0-4 are used
		// bits 11-15 are used
		// bits 5-10 are not used.
		{
			byte houses[lengthof(_housetype_flags)];
			int num = 0;

			// Generate a list of all possible houses that can be built.
			for(i=0; i!=lengthof(_housetype_flags); i++) {
				if ((~_housetype_flags[i] & bitmask) == 0)
					houses[num++] = (byte)i;
			}

			for(;;) {
				house = houses[RandomRange(num)];

				if (_cur_year < _housetype_years[house].min || _cur_year > _housetype_years[house].max)
					continue;

				// Special houses that there can be only one of.
				switch (house) {
				case HOUSE_TEMP_CHURCH:
				case HOUSE_ARCT_CHURCH:
				case HOUSE_SNOW_CHURCH:
				case HOUSE_TROP_CHURCH:
				case HOUSE_TOY_CHURCH:
					oneof = TOWN_HAS_CHURCH;
					break;
				case HOUSE_STADIUM:
				case HOUSE_MODERN_STADIUM:
					oneof = TOWN_HAS_STADIUM;
					break;
				default:
					oneof = 0;
					break;
				}

				if (t.flags12 & oneof)
					continue;

				// Make sure there is no slope?
				if (_housetype_extra_flags[house]&0x12 && slope)
					continue;

				if (_housetype_extra_flags[house]&0x10) {
					if (CheckFree2x2Area(t, tile) ||
							CheckFree2x2Area(t, (tile += TileDiffXY(-1,  0))) ||
							CheckFree2x2Area(t, (tile += TileDiffXY( 0, -1))) ||
							CheckFree2x2Area(t, (tile += TileDiffXY( 1,  0))))
						break;
					tile += TileDiffXY(0,1);
				} else if (_housetype_extra_flags[house]&4) {
					if (CheckBuildHouseMode(t, tile + TileDiffXY(1, 0), slope, 0)) break;

					if (CheckBuildHouseMode(t, tile + TileDiffXY(-1, 0), slope, 1)) {
						tile += TileDiffXY(-1, 0);
						break;
					}
				} else if (_housetype_extra_flags[house]&8) {
					if (CheckBuildHouseMode(t, tile + TileDiffXY(0, 1), slope, 2)) break;

					if (CheckBuildHouseMode(t, tile + TileDiffXY(0, -1), slope, 3)) {
						tile += TileDiffXY(0, -1);
						break;
					}
				} else
					break;
			}
		}

		t.num_houses++;

		// Special houses that there can be only one of.
		t.flags12 |= oneof;

		{
			int m3lo,m5,eflags;

			// ENDING_2
			m3lo = 0;
			m5 = 0;
			if (_generating_world) {
				int r = Random();

				// Value for map3lo
				m3lo = 0xC0;
				if (BitOps.GB(r, 0, 8) >= 220) m3lo &= (r>>8);

				if (m3lo == 0xC0)
					ChangePopulation(t, _housetype_population[house]);

				// Initial value for map5.
				m5 = BitOps.GB(r, 16, 6);
			}

			assert(tile.IsTileType( TileTypes.MP_CLEAR));

			Landscape.ModifyTile(tile,
					TileTypes.MP_SETTYPE(TileTypes.MP_HOUSE) | TileTypes.MP_MAP3HI | TileTypes.MP_MAP3LO | TileTypes.MP_MAP2 | TileTypes.MP_MAP5 | TileTypes.MP_MAPOWNER,
					t.index,
					m3lo,   /* map3_lo */
					house,  /* map3_hi */
					0,     /* map_owner */
					m5		 /* map5 */
					);

			eflags = _housetype_extra_flags[house];

			if (eflags&0x18) {
				assert(IsTileType(tile + TileDiffXY(0, 1), TileTypes.MP_CLEAR));
				Landscape.ModifyTile(tile + TileDiffXY(0, 1),
						TileTypes.MP_SETTYPE(TileTypes.MP_HOUSE) | TileTypes.MP_MAP2 | TileTypes.MP_MAP3LO | TileTypes.MP_MAP3HI | TileTypes.MP_MAP5 | TileTypes.MP_MAPOWNER,
						t.index,
						m3lo,			/* map3_lo */
						++house,	/* map3_hi */
						0,				/* map_owner */
						m5				/* map5 */
						);
			}

			if (eflags&0x14) {
				assert(IsTileType(tile + TileDiffXY(1, 0), TileTypes.MP_CLEAR));
				Landscape.ModifyTile(tile + TileDiffXY(1, 0),
						TileTypes.MP_SETTYPE(TileTypes.MP_HOUSE) | TileTypes.MP_MAP2 | TileTypes.MP_MAP3LO | TileTypes.MP_MAP3HI | TileTypes.MP_MAP5 | TileTypes.MP_MAPOWNER,
						t.index,
						m3lo,			/* map3_lo */
						++house,	/* map3_hi */
						0,				/* map_owner */
						m5				/* map5 */
						);
			}

			if (eflags&0x10) {
				assert(IsTileType(tile + TileDiffXY(1, 1), TileTypes.MP_CLEAR));
				Landscape.ModifyTile(tile + TileDiffXY(1, 1),
						TileTypes.MP_SETTYPE(TileTypes.MP_HOUSE) | TileTypes.MP_MAP2 | TileTypes.MP_MAP3LO | TileTypes.MP_MAP3HI | TileTypes.MP_MAP5 | TileTypes.MP_MAPOWNER,
						t.index,
						m3lo,			/* map3_lo */
						++house,	/* map3_hi */
						0,				/* map_owner */
						m5				/* map5 */
						);
			}
		}

		// ENDING
	}

	static boolean BuildTownHouse(Town t, TileIndex tile)
	{
		int r;

		// make sure it's possible
		if (!(tile.EnsureNoVehicle)) return false;
		if (GetTileSlope(tile, null) & 0x10) return false;

		r = Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_EXEC | Cmd.DC_AUTO | Cmd.DC_NO_WATER, Cmd.CMD_LANDSCAPE_CLEAR);
		if (Cmd.CmdFailed(r)) return false;

		DoBuildTownHouse(t, tile);
		return true;
	}


	static void DoClearTownHouseHelper(TileIndex tile)
	{
		assert(tile.IsTileType( TileTypes.MP_HOUSE));
		DoClearSquare(tile);
		TextEffect.DeleteAnimatedTile(tile);
	}

	static void ClearTownHouse(Town t, TileIndex tile)
	{
		int house = tile.getMap().m4;
		int eflags;

		assert(tile.IsTileType( TileTypes.MP_HOUSE));

		// need to align the tile to point to the upper left corner of the house
		if (house >= 3) { // house id 0,1,2 MUST be single tile houses, or this code breaks.
			if (_housetype_extra_flags[house-1] & 0x04) {
				house--;
				tile += TileDiffXY(-1, 0);
			} else if (_housetype_extra_flags[house-1] & 0x18) {
				house--;
				tile += TileDiffXY(0, -1);
			} else if (_housetype_extra_flags[house-2] & 0x10) {
				house-=2;
				tile += TileDiffXY(-1, 0);
			} else if (_housetype_extra_flags[house-3] & 0x10) {
				house-=3;
				tile += TileDiffXY(-1, -1);
			}
		}

		// Remove population from the town if the
		// house is finished.
		if ((~tile.getMap().m3 & 0xC0) == 0) {
			ChangePopulation(t, -_housetype_population[house]);
		}

		t.num_houses--;

		// Clear flags for houses that only may exist once/town.
		switch (house) {
		case HOUSE_TETileTypes.MP_CHURCH:
		case HOUSE_ARAcceptedCargo.CT_CHURCH:
		case HOUSE_SNOW_CHURCH:
		case HOUSE_TROP_CHURCH:
		case HOUSE_TOY_CHURCH:
			t.flags12 &= ~TOWN_HAS_CHURCH;
			break;
		case HOUSE_STADIUM:
		case HOUSE_MODERN_STADIUM:
			t.flags12 &= ~TOWN_HAS_STADIUM;
			break;
		default:
			break;
		}

		// Do the actual clearing of tiles
		eflags = _housetype_extra_flags[house];
		DoClearTownHouseHelper(tile);
		if (eflags & 0x14) DoClearTownHouseHelper(tile + TileDiffXY(1, 0));
		if (eflags & 0x18) DoClearTownHouseHelper(tile + TileDiffXY(0, 1));
		if (eflags & 0x10) DoClearTownHouseHelper(tile + TileDiffXY(1, 1));
	}

	/** Rename a town (server-only).
	 * @param x,y unused
	 * @param p1 town ID to rename
	 * @param p2 unused
	 */
	int CmdRenameTown(int x, int y, int flags, int p1, int p2)
	{
		StringID str;
		Town t;

		if (!IsTownIndex(p1) || Global._cmd_text == null) return Cmd.CMD_ERROR;

		t = GetTown(p1);

		str = AllocateNameUnique(Global._cmd_text, 4);
		if (str == 0) return Cmd.CMD_ERROR;

		if (flags & Cmd.DC_EXEC) {
			DeleteName(t.townnametype);
			t.townnametype = str;

			UpdateTownVirtCoord(t);
			_town_sort_dirty = true;
			UpdateAllStationVirtCoord();
			MarkWholeScreenDirty();
		} else {
			DeleteName(str);
		}
		return 0;
	}

	// Called from GUI
	void DeleteTown(Town t)
	{
		Industry i;
		TileIndex tile;

		// Delete town authority window
		//  and remove from list of sorted towns
		Window.DeleteWindowById(Window.WC_TOWN_VIEW, t.index);
		_town_sort_dirty = true;

		// Delete all industries belonging to the town
		FOR_ALL_INDUSTRIES(i) {
			if (i.xy && i.town == t)
				DeleteIndustry(i);
		}

		// Go through all tiles and delete those belonging to the town
		for (tile = 0; tile < MapSize(); ++tile) {
			switch (GetTileType(tile)) {
			case TileTypes.MP_HOUSE:
				if (GetTown(tile.getMap().m2) == t)
					Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_EXEC, Cmd.CMD_LANDSCAPE_CLEAR);
				break;

			case TileTypes.MP_STREET:
			case TileTypes.MP_TUNNELBRIDGE:
				if (IsTileOwner(tile, Owner.OWNER_TOWN) &&
						ClosestTownFromTile(tile, (int)-1) == t)
					Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_EXEC, Cmd.CMD_LANDSCAPE_CLEAR);
				break;

			default:
				break;
			}
		}

		t.xy = 0;
		DeleteName(t.townnametype);

		MarkWholeScreenDirty();
	}

	// Called from GUI
	void ExpandTown(Town t)
	{
		int amount, n;

		Global._generating_world = true;

		/* The more houses, the faster we grow */
		amount = RandomRange(t.num_houses / 10) + 3;
		t.num_houses += amount;
		UpdateTownRadius(t);

		n = amount * 10;
		do GrowTown(t); while (--n);

		t.num_houses -= amount;
		UpdateTownRadius(t);

		UpdateTownMaxPass(t);
		_generating_world = false;
	}

	final int _town_action_costs[] = {
			2, 4, 9, 35, 48, 53, 117, 175
	};

	//typedef void TownActionProc(Town t, int action);

	static void TownActionAdvertise(Town t, int action)
	{
		static final byte _advertising_amount[3] = {0x40, 0x70, 0xA0};
		static final byte _advertising_radius[3] = {10,15,20};
		ModifyStationRatingAround(t.xy, Global._current_player,
				_advertising_amount[action],
				_advertising_radius[action]);
	}

	static void TownActionRoadRebuild(Town t, int action)
	{
		final Player  p;

		t.road_build_months = 6;

		Global.SetDParam(0, t.index);

		p = Player.GetPlayer(Global._current_player);
		Global.SetDParam(1, p.name_1);
		Global.SetDParam(2, p.name_2);

		NewsItem.AddNewsItem(STR_2055_TRAFFIC_CHAOS_IN_ROAD_REBUILDING,
			NewsItem.NEWS_FLAGS(NewsItem.NM_NORMAL, NewsItem.NF_TILE, NewsItem.NT_GENERAL, 0), t.xy, 0);
	}

	static boolean DoBuildStatueOfCompany(TileIndex tile)
	{
		TileInfo ti;
		PlayerID old;
		int r;

		FindLandscapeHeightByTile(&ti, tile);
		if (ti.tileh != 0) return false;

		if (ti.type != TileTypes.MP_HOUSE && ti.type != TileTypes.MP_CLEAR && ti.type != TileTypes.MP_TREES)
			return false;


		old = Global._current_player;
		Global._current_player = Owner.OWNER_NONE;
		r = Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_EXEC, Cmd.CMD_LANDSCAPE_CLEAR);
		Global._current_player = old;

		if (Cmd.CmdFailed(r)) return false;

		Landscape.ModifyTile(tile, TileTypes.MP_SETTYPE(TileTypes.MP_UNMOVABLE) | TileTypes.MP_MAPOwner.OWNER_CURRENT | TileTypes.MP_MAP5,
				2 /* map5 */
				);

		return true;
	}

	// Layouted as an outward spiral
	static final TileIndexDiffC _statue_tiles[] = {
			new TileIndexDiffC(-1, 0),
			new TileIndexDiffC( 0, 1),
			new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0),
			new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1),
			new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0),
			new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1),
			new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0),
			new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1),
			new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0),
			new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1),
			new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0),
			new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1),
			new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0),
			new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1), new TileIndexDiffC( 0, 1),
			new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0), new TileIndexDiffC( 1, 0),
			new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1), new TileIndexDiffC( 0,-1),
			new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0), new TileIndexDiffC(-1, 0),
			new TileIndexDiffC( 0, 0)
	};

	static void TownActionBuildStatue(Town t, int action)
	{
		TileIndex tile = t.xy;
		final TileIndexDiffC p;

		t.statues = BitOps.RETSETBIT(t.statues, Global._current_player);

		for (p = _statue_tiles; p != endof(_statue_tiles); ++p) 
		{
			if (DoBuildStatueOfCompany(tile)) return;
			tile = TILE_ADD(tile, ToTileIndexDiff(*p));
		}
	}

	static void TownActionFundBuildings(Town t, int action)
	{
		t.grow_counter = 1;
		t.flags12 |= 1;
		t.fund_buildings_months = 3;
	}

	static void TownActionBuyRights(Town t, int action)
	{
		t.exclusive_counter = 12;
		t.exclusivity = Global._current_player;

		ModifyStationRatingAround(t.xy, Global._current_player, 130, 17);
	}

	static void TownActionBribe(Town t, int action)
	{
		if (!RandomRange(15)) {
			Station st;

			// set as unwanted for 6 months
			t.unwanted[Global._current_player] = 6;

			// set all close by station ratings to 0
			FOR_ALL_STATIONS(st) {
				if (st.town == t && st.owner == Global._current_player) {
					int i;

					for (i = 0; i != NUM_CARGO; i++) st.goods[i].rating = 0;
				}
			}

			// only show errormessage to the executing player. All errors are handled command.c
			// but this is special, because it can only 'fail' on a Cmd.DC_EXEC
			if (IsLocalPlayer()) ShowErrorMessage(STR_BRIBE_FAILED_2, STR_BRIBE_FAILED, 0, 0);

			/*	decrease by a lot!
			 *	ChangeTownRating is only for stuff in demolishing. Bribe failure should
			 *	be independent of any cheat settings
			 */
			if (t.ratings[Global._current_player] > RATING_BRIBE_DOWN_TO) {
				t.ratings[Global._current_player] = RATING_BRIBE_DOWN_TO;
			}
		} else {
			ChangeTownRating(t, RATING_BRIBE_UP_STEP, RATING_BRIBE_MAXIMUM);
		}
	}

	static TownActionProc * final _town_action_proc[] = {
			TownActionAdvertise,
			TownActionAdvertise,
			TownActionAdvertise,
			TownActionRoadRebuild,
			TownActionBuildStatue,
			TownActionFundBuildings,
			TownActionBuyRights,
			TownActionBribe
	};

	extern int GetMaskOfTownActions(int *nump, PlayerID pid, final Town t);

	/** Do a town action.
	 * This performs an action such as advertising, building a statue, funding buildings,
	 * but also bribing the town-council
	 * @param x,y unused
	 * @param p1 town to do the action at
	 * @param p2 action to perform, @see _town_action_proc for the list of available actions
	 */
	int CmdDoTownAction(int x, int y, int flags, int p1, int p2)
	{
		int cost;
		Town t;

		if (!IsTownIndex(p1) || p2 > lengthof(_town_action_proc)) return Cmd.CMD_ERROR;

		t = GetTown(p1);

		if (!BitOps.HASBIT(GetMaskOfTownActions(null, Global._current_player, t), p2)) return Cmd.CMD_ERROR;

		Player.SET_Player.EXPENSES_TYPE(EXPENSES_OTHER);

		cost = (_price.build_industry >> 8) * _town_action_costs[p2];

		if (flags & Cmd.DC_EXEC) {
			_town_action_proc[p2](t, p2);
			InvalidateWindow(Window.WC_TOWN_AUTHORITY, p1);
		}

		return cost;
	}

	static void UpdateTownGrowRate(Town t)
	{
		int n;
		Station st;
		byte m;
		Player p;

		// Reset player ratings if they're low
		FOR_ALL_PLAYERS(p) {
			if (p.is_active && t.ratings[p.index] <= 200) {
				t.ratings[p.index] += 5;
			}
		}

		n = 0;
		FOR_ALL_STATIONS(st) {
			if (DistanceSquare(st.xy, t.xy) <= t.radius[0]) {
				if (st.time_since_load <= 20 || st.time_since_unload <= 20) {
					n++;
					if (st.owner < Global.MAX_PLAYERS && t.ratings[st.owner] <= 1000-12)
						t.ratings[st.owner] += 12;
				} else {
					if (st.owner < Global.MAX_PLAYERS && t.ratings[st.owner] >= -1000+15)
						t.ratings[st.owner] -= 15;
				}
			}
		}

		t.flags12 &= ~1;

		if (t.fund_buildings_months != 0) {
			static final byte _grow_count_values[6] = {
					60, 60, 60, 50, 40, 30
			};
			m = _grow_count_values[min(n, 5)];
			t.fund_buildings_months--;
		} else if (n == 0) {
			m = 160;
			if (!BitOps.CHANCE16(1, 12))
				return;
		} else {
			static final byte _grow_count_values[5] = {
					210, 150, 110, 80, 50
			};
			m = _grow_count_values[min(n, 5) - 1];
		}

		if (_opt.landscape == LT_HILLY) {
			if (TilePixelHeight(t.xy) >= _opt.snow_line && t.act_food == 0 && t.population > 90)
				return;
		} else if (_opt.landscape == LT_DESERT) {
			if (GetMapExtraBits(t.xy) == 1 && (t.act_food==0 || t.act_water==0) && t.population > 60)
				return;
		}

		t.growth_rate = m / (t.num_houses / 50 + 1);
		if (m <= t.grow_counter)
			t.grow_counter = m;

		t.flags12 |= 1;
	}

	static void UpdateTownAmounts(Town t)
	{
		// Using +1 here to prevent overflow and division by zero
		t.pct_pass_transported = t.new_act_pass * 256 / (t.new_max_pass + 1);

		t.max_pass = t.new_max_pass; t.new_max_pass = 0;
		t.act_pass = t.new_act_pass; t.new_act_pass = 0;
		t.act_food = t.new_act_food; t.new_act_food = 0;
		t.act_water = t.new_act_water; t.new_act_water = 0;

		// Using +1 here to prevent overflow and division by zero
		t.pct_mail_transported = t.new_act_mail * 256 / (t.new_max_mail + 1);
		t.max_mail = t.new_max_mail; t.new_max_mail = 0;
		t.act_mail = t.new_act_mail; t.new_act_mail = 0;

		InvalidateWindow(Window.WC_TOWN_VIEW, t.index);
	}

	static void UpdateTownUnwanted(Town t)
	{
		Player p;

		FOR_ALL_PLAYERS(p) {
			if (t.unwanted[p.index] > 0)
				t.unwanted[p.index]--;
		}
	}

	boolean CheckIfAuthorityAllows(TileIndex tile)
	{
		Town t;

		if (Global._current_player >= Global.MAX_PLAYERS)
			return true;

		t = ClosestTownFromTile(tile, _patches.dist_local_authority);
		if (t == null)
			return true;

		if (t.ratings[Global._current_player] > -200)
			return true;

		_error_message = STR_2009_LOCAL_AUTHORITY_REFUSES;
		Global.SetDParam(0, t.index);

		return false;
	}


	static Town ClosestTownFromTile(TileIndex tile, int threshold)
	{
		Town t;
		int dist, best = threshold;
		Town best_town = null;

		// XXX - Fix this so for a given tiletype the owner of the type is in the same variable
		if (tile.IsTileType( TileTypes.MP_HOUSE) || (
				tile.IsTileType( TileTypes.MP_STREET) &&
				(IsLevelCrossing(tile) ? tile.getMap().m3 : GetTileOwner(tile)) == Owner.OWNER_TOWN
				))
			return GetTown(tile.getMap().m2);

		FOR_ALL_TOWNS(t) {
			if (t.xy != 0) {
				dist = DistanceManhattan(tile, t.xy);
				if (dist < best) {
					best = dist;
					best_town = t;
				}
			}
		}

		return best_town;
	}

	static void ChangeTownRating(Town t, int add, int max)
	{
		int rating;

		//	if magic_bulldozer cheat is active, town doesn't penaltize for removing stuff
		if (t == null ||
				Global._current_player >= Global.MAX_PLAYERS ||
				(_cheats.magic_bulldozer.value && add < 0)) {
			return;
		}

		SETBIT(t.have_ratings, Global._current_player);

		rating = t.ratings[Global._current_player];

		if (add < 0) {
			if (rating > max) {
				rating += add;
				if (rating < max) rating = max;
			}
		} else {
			if (rating < max) {
				rating += add;
				if (rating > max) rating = max;
			}
		}
		t.ratings[Global._current_player] = rating;
	}

	/*	penalty for removing town-owned stuff */
	static final int _default_rating_settings [3][3] = {
			// ROAD_REMOVE, TUNNELBRIDGE_REMOVE, INDUSTRY_REMOVE
			{  0, 128, 384},	// Permissive
			{ 48, 192, 480},	// Neutral
			{ 96, 384, 768},	// Hostile
	};

	boolean CheckforTownRating(TileIndex tile, int flags, Town t, byte type)
	{
		int modemod;

		//	if magic_bulldozer cheat is active, town doesn't restrict your destructive actions
		if (t == null || Global._current_player >= Global.MAX_PLAYERS || _cheats.magic_bulldozer.value)
			return true;

		/*	check if you're allowed to remove the street/bridge/tunnel/industry
		 *	owned by a town	no removal if rating is lower than ... depends now on
		 *	difficulty setting. Minimum town rating selected by difficulty level
		 */
		modemod = _default_rating_settings[_opt.diff.town_council_tolerance][type];

		if (t.ratings[Global._current_player] < 16 + modemod && !(flags & Cmd.DC_NO_TOWN_RATING)) {
			Global.SetDParam(0, t.index);
			_error_message = STR_2009_LOCAL_AUTHORITY_REFUSES;
			return false;
		}

		return true;
	}

	void TownsMonthlyLoop()
	{
		Town t;

		FOR_ALL_TOWNS(t) if (t.xy != 0) {
			if (t.road_build_months != 0)
				t.road_build_months--;

			if (t.exclusive_counter != 0)
				if(--t.exclusive_counter==0)
					t.exclusivity = (byte)-1;

			UpdateTownGrowRate(t);
			UpdateTownAmounts(t);
			UpdateTownUnwanted(t);
			MunicipalAirport(t);
		}
	}

	static void InitializeTowns()
	{
		Subsidy s;

		/* Clean the town pool and create 1 block in it */
		_town_pool.CleanPool();
		_town_pool.AddBlockToPool();

		memset(_subsidies, 0, sizeof(_subsidies));
		for (s=_subsidies; s != endof(_subsidies); s++)
			s.cargo_type = AcceptedCargo.CT_INVALID;

		_cur_town_ctr = 0;
		_cur_town_iter = 0;
		_total_towns = 0;
		_town_sort_dirty = true;
	}

	final static TileTypeProcs _tile_type_town_procs = new TileTypeProcs(
			Town::DrawTile_Town,						/* draw_tile_proc */
			Town::GetSlopeZ_Town,						/* get_slope_z_proc */
			Town::ClearTile_Town,						/* clear_tile_proc */
			Town::GetAcceptedCargo_Town,		/* get_accepted_cargo_proc */
			Town::GetTileDesc_Town,					/* get_tile_desc_proc */
			Town::GetTileTrackStatus_Town,	/* get_tile_track_status_proc */
			Town::ClickTile_Town,						/* click_tile_proc */
			Town::AnimateTile_Town,					/* animate_tile_proc */
			Town::TileLoop_Town,						/* tile_loop_clear */
			Town::ChangeTileOwner_Town,			/* change_tile_owner_clear */
			null,											/* get_produced_cargo_proc */
			null,											/* vehicle_enter_tile_proc */
			null,											/* vehicle_leave_tile_proc */
			Town::GetSlopeTileh_Town				/* get_slope_tileh_proc */
			);


	public static void forEach(Consumer<Town> c) {
		_town_pool.forEach(c);
	}

	/*
	// Save and load of towns.
	static final SaveLoad _town_desc[] = {
			SLE_CONDVAR(Town, xy, SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
			SLE_CONDVAR(Town, xy, SLE_int32, 6, 255),

			SLE_CONDVAR(Town,population,	SLE_FILE_U16 | SLE_VAR_U32, 0, 2),
			SLE_CONDVAR(Town,population,	SLE_int32, 3, 255),


			SLE_VAR(Town,num_houses,	SLE_int16),
			SLE_VAR(Town,townnametype,SLE_int16),
			SLE_VAR(Town,townnameparts,SLE_int32),

			SLE_VAR(Town,flags12,			SLE_int8),
			SLE_VAR(Town,statues,			SLE_int8),

			// sort_index_obsolete was stored here in savegame format 0 - 1
			SLE_CONDARR(NullStruct,null,SLE_FILE_U8 | SLE_VAR_null, 1, 0, 1),

			SLE_VAR(Town,have_ratings,SLE_int8),
			SLE_ARR(Town,ratings,			SLE_INT16, 8),
			// failed bribe attempts are stored since savegame format 4
			SLE_CONDARR(Town,unwanted,			SLE_INT8, 8, 4,255),

			SLE_CONDVAR(Town,max_pass,		SLE_FILE_U16 | SLE_VAR_U32, 0, 8),
			SLE_CONDVAR(Town,max_mail,		SLE_FILE_U16 | SLE_VAR_U32, 0, 8),
			SLE_CONDVAR(Town,new_max_pass,SLE_FILE_U16 | SLE_VAR_U32, 0, 8),
			SLE_CONDVAR(Town,new_max_mail,SLE_FILE_U16 | SLE_VAR_U32, 0, 8),
			SLE_CONDVAR(Town,act_pass,		SLE_FILE_U16 | SLE_VAR_U32, 0, 8),
			SLE_CONDVAR(Town,act_mail,		SLE_FILE_U16 | SLE_VAR_U32, 0, 8),
			SLE_CONDVAR(Town,new_act_pass,SLE_FILE_U16 | SLE_VAR_U32, 0, 8),
			SLE_CONDVAR(Town,new_act_mail,SLE_FILE_U16 | SLE_VAR_U32, 0, 8),

			SLE_CONDVAR(Town,max_pass,		SLE_int32, 9, 255),
			SLE_CONDVAR(Town,max_mail,		SLE_int32, 9, 255),
			SLE_CONDVAR(Town,new_max_pass,SLE_int32, 9, 255),
			SLE_CONDVAR(Town,new_max_mail,SLE_int32, 9, 255),
			SLE_CONDVAR(Town,act_pass,		SLE_int32, 9, 255),
			SLE_CONDVAR(Town,act_mail,		SLE_int32, 9, 255),
			SLE_CONDVAR(Town,new_act_pass,SLE_int32, 9, 255),
			SLE_CONDVAR(Town,new_act_mail,SLE_int32, 9, 255),

			SLE_VAR(Town,pct_pass_transported,SLE_int8),
			SLE_VAR(Town,pct_mail_transported,SLE_int8),

			SLE_VAR(Town,act_food,		SLE_int16),
			SLE_VAR(Town,act_water,		SLE_int16),
			SLE_VAR(Town,new_act_food,SLE_int16),
			SLE_VAR(Town,new_act_water,SLE_int16),

			SLE_VAR(Town,time_until_rebuild,		SLE_int8),
			SLE_VAR(Town,grow_counter,					SLE_int8),
			SLE_VAR(Town,growth_rate,						SLE_int8),
			SLE_VAR(Town,fund_buildings_months,	SLE_int8),
			SLE_VAR(Town,road_build_months,			SLE_int8),

			SLE_VAR(Town,exclusivity,						SLE_int8),
			SLE_VAR(Town,exclusive_counter,			SLE_int8),
			// reserve extra space in savegame here. (currently 30 bytes)
			SLE_CONDARR(NullStruct,null,SLE_FILE_U8 | SLE_VAR_null, 30, 2, 255),

			SLE_END()
	};

	static void Save_TOWN()
	{
		Town t;

		FOR_ALL_TOWNS(t) {
			if (t.xy != 0) {
				SlSetArrayIndex(t.index);
				SlObject(t, _town_desc);
			}
		}
	}

	static void Load_TOWN()
	{
		int index;

		_total_towns = 0;

		while ((index = SlIterateArray()) != -1) {
			Town t;

			if (!AddBlockIfNeeded(&_town_pool, index))
				error("Towns: failed loading savegame: too many towns");

			t = GetTown(index);
			SlObject(t, _town_desc);

			if ((int)index > _total_towns)
				_total_towns = index;
		}

		// This is to ensure all pointers are within the limits of
		//  the size of the TownPool 
		if (_cur_town_ctr >= GetTownPoolSize())
			_cur_town_ctr = 0;
	}

	void AfterLoadTown()
	{
		Town t;
		FOR_ALL_TOWNS(t) {
			if (t.xy != 0) {
				UpdateTownRadius(t);
				UpdateTownVirtCoord(t);
			}
		}
		_town_sort_dirty = true;
	}


	final ChunkHandler _town_chunk_handlers[] = {
			{ 'CITY', Save_TOWN, Load_TOWN, CH_ARRAY | CH_LAST},
	};
	 */

}


@FunctionalInterface
interface TownDrawTileProc extends Consumer<TileInfo>;


