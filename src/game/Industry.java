package game;

import java.util.function.Consumer;

import game.tables.IndustryTables;
import game.util.BitOps;

public class Industry extends IndustryTables implements IPoolItem {

	TileIndex xy;
	byte width; /* swapped order of w/h with town */
	byte height;
	Town town;

	byte produced_cargo[];
	int cargo_waiting[];
	byte production_rate[];
	byte accepts_cargo[];

	byte prod_level;

	int last_mo_production[];
	int last_mo_transported[];
	byte pct_transported[];
	int total_production[];
	int total_transported[];

	int counter;

	byte type;
	byte owner;
	byte color_map;
	byte last_prod_year;
	byte was_cargo_delivered;

	int index;


	public Industry() {
		produced_cargo = new byte[2];
		cargo_waiting = new int[2];
		production_rate = new byte[2];
		accepts_cargo = new byte[3];
		last_mo_production = new int[2];
		last_mo_transported = new int[2];
		pct_transported = new byte[2];
		total_production = new int[2];
		total_transported = new int[2];
	}


	//public static Industry GetIndustry(int offs) {	}





	//enum {
	/* Max industries: 64000 (8 * 8000) */
	public static final int INDUSTRY_POOL_BLOCK_SIZE_BITS = 3;       /* In bits, so (1 << 3) == 8 */
	public static final int INDUSTRY_POOL_MAX_BLOCKS      = 8000;
	//};

	/**
	 * Called if a new block is added to the industry-pool
	 * /
	void IndustryPoolNewBlock(int start_item)
	{
		Industry i;

		FOR_ALL_INDUSTRIES_FROM(i, start_item) i.index = start_item++;
	}*/

	private static  IPoolItemFactory<Industry> factory = new IPoolItemFactory<Industry>() {

		@Override
		public Industry createObject() {
			return new Industry();
		}
	};
	/* Initialize the industry-pool */
	static MemoryPool<Industry> _industry_pool = new MemoryPool<Industry>(factory); 
	//{ "Industry", INDUSTRY_POOL_MAX_BLOCKS, INDUSTRY_POOL_BLOCK_SIZE_BITS, sizeof(Industry), &IndustryPoolNewBlock, 0, 0, null };




	@Override
	public void setIndex(int index) {
		this.index = index;
	}


	/**
	 * Check if an Industry really exists.
	 */
	public static boolean IsValidIndustry(Industry industry)
	{
		return industry.xy != null; /* XXX: Replace by INVALID_TILE someday */
	}

	/**
	 * Get the pointer to the industry with index 'index'
	 */
	public static Industry GetIndustry(int index)
	{
		return _industry_pool.GetItemFromPool(index);
	}








	static byte _industry_sound_ctr;
	static TileIndex _industry_sound_tile;

	//void ShowIndustryViewWindow(int industry);
	//void BuildOilRig(TileIndex tile);
	//void DeleteOilRig(TileIndex tile);

	class DrawIndustryTileStruct {
		int sprite_1;
		int sprite_2;

		byte subtile_x;
		byte subtile_y;
		byte width;
		byte height;
		byte dz;
		byte proc;
	}


	class DrawIndustrySpec1Struct {
		byte x;
		byte image_1;
		byte image_2;
		byte image_3;
	}

	class DrawIndustrySpec4Struct {
		byte image_1;
		byte image_2;
		byte image_3;
	}

	/*class IndustryTileTable {
		TileIndexDiffC ti;
		byte map5;
	}* /

	class IndustrySpec {
		final IndustryTileTable [][]table;
		byte num_table;
		byte a,b,c;
		byte [] produced_cargo = new byte[2];
		byte [] production_rate = new byte[2];
		byte [] accepts_cargo = new byte[3];
		byte check_proc;
	} */








	static void IndustryDrawTileProc1(final TileInfo ti)
	{
		final DrawIndustrySpec1Struct d;
		int image;

		if (0 == (ti.tile.getMap().m1 & 0x80)) return;

		d = _draw_industry_spec1[ti.tile.getMap().m3];

		ViewPort.AddChildSpriteScreen(0x12A7 + d.image_1, d.x, 0);

		image = d.image_2;
		if (image != 0) ViewPort.AddChildSpriteScreen(0x12B0 + image - 1, 8, 41);

		image = d.image_3;
		if (image != 0) {
			ViewPort.AddChildSpriteScreen(0x12AC + image - 1,
					_drawtile_proc1_x[image - 1], _drawtile_proc1_y[image - 1]);
		}
	}

	static void IndustryDrawTileProc2(final TileInfo ti)
	{
		int x = 0;

		if (ti.tile.getMap().m1 & 0x80) {
			x = _industry_anim_offs[ti.tile.getMap().m3];
			if ( (byte)x == 0xFF)
				x = 0;
		}

		ViewPort.AddChildSpriteScreen(0x129F, 22 - x, 24 + x);
		ViewPort.AddChildSpriteScreen(0x129E, 6, 0xE);
	}

	static void IndustryDrawTileProc3(final TileInfo ti)
	{
		if(0!=(ti.tile.getMap().m1 & 0x80)) {
			ViewPort.AddChildSpriteScreen(0x128B, 5, _industry_anim_offs_2[ti.tile.getMap().m3]);
		} else {
			ViewPort.AddChildSpriteScreen(4746, 3, 67);
		}
	}

	static void IndustryDrawTileProc4(final TileInfo ti)
	{
		final DrawIndustrySpec4Struct d;

		d = _industry_anim_offs_3[ti.tile.getMap().m3];

		if (d.image_1 != 0xFF) {
			ViewPort.AddChildSpriteScreen(0x126F, 0x32 - d.image_1 * 2, 0x60 + d.image_1);
		}

		if (d.image_2 != 0xFF) {
			ViewPort.AddChildSpriteScreen(0x1270, 0x10 - d.image_2 * 2, 100 + d.image_2);
		}

		ViewPort.AddChildSpriteScreen(0x126E, 7, d.image_3);
		ViewPort.AddChildSpriteScreen(0x126D, 0, 42);
	}

	static void DrawCoalPlantSparkles(final TileInfo ti)
	{
		int image = ti.tile.getMap().m1;
		if (image & 0x80) {
			image = BitOps.GB(image, 2, 5);
			if (image != 0 && image < 7) {
				ViewPort.AddChildSpriteScreen(image + 0x806,
						_coal_plant_sparkles_x[image - 1],
						_coal_plant_sparkles_y[image - 1]
						);
			}
		}
	}

	//typedef void IndustryDrawTileProc(final TileInfo ti);
	static final IndustryDrawTileProc  _industry_draw_tile_procs[] = {
			Industry::IndustryDrawTileProc1,
			Industry::IndustryDrawTileProc2,
			Industry::IndustryDrawTileProc3,
			Industry::IndustryDrawTileProc4,
			Industry::DrawCoalPlantSparkles,
	};

	static void DrawTile_Industry(TileInfo ti)
	{
		final Industry  ind;
		final DrawIndustryTileStruct dits;
		byte z;
		int image, ormod;

		/* Pointer to industry */
		ind = GetIndustry(ti.tile.getMap().m2);
		ormod = (ind.color_map + 0x307) << PALETTE_SPRITE_START;

		/* Retrieve pointer to the draw industry tile struct */
		dits = _industry_draw_tile_data[(ti.map5 << 2) | BitOps.GB(ti.tile.getMap().m1, 0, 2)];

		image = dits.sprite_1;
		if (image & PALETTE_MODIFIER_COLOR && (image & PALETTE_SPRITE_MASK) == 0)
			image |= ormod;

		z = ti.z;
		/* Add bricks below the industry? */
		if (ti.tileh & 0xF) {
			ViewPort.AddSortableSpriteToDraw(Sprite.SPR_FOUNDATION_BASE + (ti.tileh & 0xF), ti.x, ti.y, 16, 16, 7, z);
			ViewPort.AddChildSpriteScreen(image, 0x1F, 1);
			z += 8;
		} else {
			/* Else draw regular ground */
			DrawGroundSprite(image);
		}

		/* Add industry on top of the ground? */
		image = dits.sprite_2;
		if (image != 0) {
			if (image & PALETTE_MODIFIER_COLOR && (image & PALETTE_SPRITE_MASK) == 0)
				image |= ormod;

			if (Global._display_opt & DO_TRANS_BUILDINGS) MAKE_TRANSPARENT(image);

			ViewPort.AddSortableSpriteToDraw(image,
					ti.x + dits.subtile_x,
					ti.y + dits.subtile_y,
					dits.width  + 1,
					dits.height + 1,
					dits.dz,
					z);

			if (Global._display_opt & DO_TRANS_BUILDINGS) return;
		}

		{
			int proc = dits.proc - 1;
			if (proc >= 0) _industry_draw_tile_procs[proc](ti);
		}
	}


	static int GetSlopeZ_Industry(final TileInfo  ti)
	{
		return Landscape.GetPartialZ(ti.x & 0xF, ti.y & 0xF, ti.tileh) + ti.z;
	}

	static int GetSlopeTileh_Industry(final TileInfo  ti)
	{
		return 0;
	}

	static AcceptedCargo GetAcceptedCargo_Industry(TileIndex tile)
	{
		AcceptedCargo ac = new AcceptedCargo();
		int m5 = tile.getMap().m5;
		/*CargoID*/ int a;

		a = _industry_map5_accepts_1[m5];
		if (a != AcceptedCargo.CT_INVALID) ac.ct[a] = (a == 0) ? 1 : 8;

		a = _industry_map5_accepts_2[m5];
		if (a != AcceptedCargo.CT_INVALID) ac.ct[a] = 8;

		a = _industry_map5_accepts_3[m5];
		if (a != AcceptedCargo.CT_INVALID) ac.ct[a] = 8;

		return ac;
	}

	static TileDesc GetTileDesc_Industry(TileIndex tile)
	{
		TileDesc td = new TileDesc();
		final Industry  i = GetIndustry(tile.getMap().m2);

		td.owner = i.owner;
		td.str = Str.STR_4802_COAL_MINE + i.type;
		if ((tile.getMap().m1 & 0x80) == 0) {
			Global.SetDParamX(td.dparam, 0, td.str);
			td.str = Str.STR_2058_UNDER_CONSTRUCTION;
		}

		return td;
	}

	static int ClearTile_Industry(TileIndex tile, byte flags)
	{
		Industry i = GetIndustry(tile.getMap().m2);

		/*	* water can destroy industries
		 * in editor you can bulldoze industries
		 * with magic_bulldozer cheat you can destroy industries
		 * (area around OILRIG is water, so water shouldn't flood it
		 */
		if ((Global._current_player.id != Owner.OWNER_WATER && Global._game_mode != GameModes.GM_EDITOR &&
				!Global._cheats.magic_bulldozer.value) ||
				(Global._current_player.id == Owner.OWNER_WATER && i.type == IT_OIL_RIG) ) {
			Global.SetDParam(0, Str.STR_4802_COAL_MINE + i.type);
			return Cmd.return_cmd_error(Str.STR_4800_IN_THE_WAY);
		}

		if( 0!= (flags & Cmd.DC_EXEC)) DeleteIndustry(i);
		return 0;
	}


	static final byte _industry_min_cargo[] = {
			5, 5, 5, 30, 5, 5, 5, 5,
			5, 5, 5, 5, 2, 5, 5, 5,
			5, 5, 5, 15, 15, 5, 5, 5,
			5, 5, 30, 5, 30, 5, 5, 5,
			5, 5, 5, 5, 5,
	};

	static void TransportIndustryGoods(TileIndex tile)
	{
		Industry  i = GetIndustry(tile.getMap().m2);
		int cw, am;

		cw = Math.min(i.cargo_waiting[0], 255);
		if (cw > _industry_min_cargo[i.type]/* && i.produced_cargo[0] != 0xFF*/) {
			byte m5;

			i.cargo_waiting[0] -= cw;

			/* fluctuating economy? */
			if (_economy.fluct <= 0) cw = (cw + 1) / 2;

			i.last_mo_production[0] += cw;

			am = MoveGoodsToStation(i.xy, i.width, i.height, i.produced_cargo[0], cw);
			i.last_mo_transported[0] += am;
			if (am != 0 && (m5 = _industry_produce_map5[tile.getMap().m5]) != 0xFF) {
				tile.getMap().m1 = 0x80;
				tile.getMap().m5 = m5;
				tile.MarkTileDirtyByTile();
			}
		}

		cw = Math.min(i.cargo_waiting[1], 255);
		if (cw > _industry_min_cargo[i.type]) {
			i.cargo_waiting[1] -= cw;

			if (_economy.fluct <= 0) cw = (cw + 1) / 2;

			i.last_mo_production[1] += cw;

			am = MoveGoodsToStation(i.xy, i.width, i.height, i.produced_cargo[1], cw);
			i.last_mo_transported[1] += am;
		}
	}


	static void AnimateTile_Industry(TileIndex tile)
	{
		int m,n;

		switch((int)tile.getMap().m5) {
		case 174:
			if ((Global._tick_counter & 1) == 0) {
				m = tile.getMap().m3 + 1;

				switch(m & 7) {
				case 2:	//SndPlayTileFx(SND_2D_RIP_2, tile); break;
				case 6: //SndPlayTileFx(SND_29_RIP, tile); break;
				}

				if (m >= 96) {
					m = 0;
					DeleteAnimatedTile(tile);
				}
				tile.getMap().m3 = (byte) m;

				tile.MarkTileDirtyByTile();
			}
			break;

		case 165:
			if ((Global._tick_counter & 3) == 0) {
				m = tile.getMap().m3;

				if (_industry_anim_offs[m] == 0xFF) {
					//SndPlayTileFx(SND_30_CARTOON_SOUND, tile);
				}

				if (++m >= 70) {
					m = 0;
					DeleteAnimatedTile(tile);
				}
				tile.getMap().m3 = m;

				tile.MarkTileDirtyByTile();
			}
			break;

		case 162:
			if ((Global._tick_counter&1) == 0) {
				m = tile.getMap().m3;

				if (++m >= 40) {
					m = 0;
					DeleteAnimatedTile(tile);
				}
				tile.getMap().m3 = m;

				tile.MarkTileDirtyByTile();
			}
			break;

			// Sparks on a coal plant
		case 10:
			if ((Global._tick_counter & 3) == 0) {
				m = tile.getMap().m1;
				if (BitOps.GB(m, 2, 5) == 6) {
					tile.getMap().m1 = BitOps.RETSB(tile.getMap().m1, 2, 5, 0);
					DeleteAnimatedTile(tile);
				} else {
					tile.getMap().m1 = m + (1<<2);
					MarkTileDirtyByTile(tile);
				}
			}
			break;

		case 143:
			if ((Global._tick_counter & 1) == 0) {
				m = tile.getMap().m3 + 1;

				if (m == 1) {
					//SndPlayTileFx(SND_2C_MACHINERY, tile);
				} else if (m == 23) {
					//SndPlayTileFx(SND_2B_COMEDY_HIT, tile);
				} else if (m == 28) {
					//SndPlayTileFx(SND_2A_EXTRAAcceptedCargo.CT_AND_POP, tile);
				}

				if (m >= 50) {
					m=0;
					if (++tile.getMap().m4 >= 8) {
						tile.getMap().m4 = 0;
						DeleteAnimatedTile(tile);
					}
				}

				tile.getMap().m3 = m;
				tile.MarkTileDirtyByTile();
			}
			break;

		case 148: case 149: case 150: case 151:
		case 152: case 153: case 154: case 155:
			if ((Global._tick_counter & 3) == 0) {
				m = tile.getMap().m5	+ 1;
				if (m == 155+1) m = 148;
				tile.getMap().m5 = m;

				tile.MarkTileDirtyByTile();
			}
			break;

		case 30: case 31: case 32:
			if ((Global._tick_counter & 7) == 0) {
				boolean b = BitOps.CHANCE16(1,7);
				m = tile.getMap().m1;
				m = (m & 3) + 1;
				n = tile.getMap().m5;
				if (m == 4 && (m=0,++n) == 32+1 && (n=30,b)) {
					tile.getMap().m1 = 0x83;
					tile.getMap().m5 = 29;
					tile.DeleteAnimatedTile();
				} else {
					tile.getMap().m1 = BitOps.RETSB(tile.getMap().m1, 0, 2, m);
					tile.getMap().m5 = n;
					tile.MarkTileDirtyByTile();
				}
			}
			break;

		case 88:
		case 48:
		case 1: {
			int state = Global._tick_counter & 0x7FF;

			if ((state -= 0x400) < 0)
				return;

			if (state < 0x1A0) {
				if (state < 0x20 || state >= 0x180) {
					if (!(tile.getMap().m1 & 0x40)) {
						tile.getMap().m1 |= 0x40;
						SndPlayTileFx(SND_0B_MINING_MACHINERY, tile);
					}
					if (state & 7)
						return;
				} else {
					if (state & 3)
						return;
				}
				m = (tile.getMap().m1 + 1) | 0x40;
				if (m > 0xC2) m = 0xC0;
				tile.getMap().m1 = m;
				tile.MarkTileDirtyByTile();
			} else if (state >= 0x200 && state < 0x3A0) {
				int i;
				i = (state < 0x220 || state >= 0x380) ? 7 : 3;
				if (state & i)
					return;

				m = (tile.getMap().m1 & 0xBF) - 1;
				if (m < 0x80) m = 0x82;
				tile.getMap().m1 = m;				
				tile.MarkTileDirtyByTile();
			}
		} break;
		}
	}

	static void MakeIndustryTileBiggerCase8(TileIndex tile)
	{
		TileInfo ti = new TileInfo();
		FindLandscapeHeight(ti, tile.TileX() * 16, tile.TileY() * 16);
		CreateEffectVehicle(ti.x + 15, ti.y + 14, ti.z + 59 + (ti.tileh != 0 ? 8 : 0), EV_CHIMNEY_SMOKE);
	}

	static void MakeIndustryTileBigger(TileIndex tile, byte size)
	{
		byte b = (byte)((size + (1<<2)) & (3<<2));

		if (b != 0) {
			tile.getMap().m1 = b | (size & 3);
			return;
		}

		size = (byte) ((size + 1) & 3);
		if (size == 3) size |= 0x80;
		tile.getMap().m1 = size | b;

		tile.MarkTileDirtyByTile();

		if (0 == (tile.getMap().m1 & 0x80))
			return;

		switch(tile.getMap().m5) {
		case 8:
			MakeIndustryTileBiggerCase8(tile);
			break;

		case 24:
			if (_m[tile + TileDiffXY(0, 1)].m5 == 24) BuildOilRig(tile);
			break;

		case 143:
		case 162:
		case 165:
			tile.getMap().m3 = 0;
			tile.getMap().m4 = 0;
			break;

		case 148: case 149: case 150: case 151:
		case 152: case 153: case 154: case 155:
			AddAnimatedTile(tile);
			break;
		}
	}

	static final int _tileloop_ind_case_161[] = {
			11, 0, -4, -14,
			-4, -10, -4, 1,
			49, 59, 60, 65,
	};

	static void TileLoopIndustryCase161(TileIndex tile)
	{
		int dir;
		Vehicle v;

		//SndPlayTileFx(SND_2E_EXTRAAcceptedCargo.CT_AND_POP, tile);

		dir = Hal.Random() & 3;

		v = CreateEffectVehicleAbove(
				tile.TileX() * 16 + _tileloop_ind_case_161[dir + 0],
				tile.TileY() * 16 + _tileloop_ind_case_161[dir + 4],
				_tileloop_ind_case_161[dir + 8],
				EV_BUBBLE
				);

		if (v != null) v.u.special.unk2 = dir;
	}

	static void TileLoop_Industry(TileIndex tile)
	{
		byte n;

		if (0 == (tile.getMap().m1 & 0x80)) {
			MakeIndustryTileBigger(tile, tile.getMap().m1);
			return;
		}

		if (Global._game_mode == GameModes.GM_EDITOR) return;

		TransportIndustryGoods(tile);

		n = _industry_map5_animation_next[tile.getMap().m5];
		if (n != 255) {
			tile.getMap().m1 = 0;
			tile.getMap().m5 = n;
			tile.MarkTileDirtyByTile();
			return;
		}

		//#define SET_AND_ANIMATE(tile, a, b)   { tile.getMap().m5 = a; tile.getMap().m1 = b; AddAnimatedTile(tile); }
		//#define SET_AND_UNANIMATE(tile, a, b) { tile.getMap().m5 = a; tile.getMap().m1 = b; DeleteAnimatedTile(tile); }

		switch (tile.getMap().m5) {
		case 0x18: // coast line at oilrigs
		case 0x19:
		case 0x1A:
		case 0x1B:
		case 0x1C:
			TileLoop_Water(tile);
			break;

		case 0:
			if (!(Global._tick_counter & 0x400) && BitOps.CHANCE16(1,2))
				SET_AND_ANIMATE(tile,1,0x80);
			break;

		case 47:
			if (!(Global._tick_counter & 0x400) && BitOps.CHANCE16(1,2))
				SET_AND_ANIMATE(tile,0x30,0x80);
			break;

		case 79:
			if (!(Global._tick_counter & 0x400) && BitOps.CHANCE16(1,2))
				SET_AND_ANIMATE(tile,0x58,0x80);
			break;

		case 29:
			if (BitOps.CHANCE16(1,6))
				SET_AND_ANIMATE(tile,0x1E,0x80);
			break;

		case 1:
			if (!(Global._tick_counter & 0x400))
				SET_AND_UNANIMATE(tile, 0, 0x83);
			break;

		case 48:
			if (!(Global._tick_counter & 0x400))
				SET_AND_UNANIMATE(tile, 0x2F, 0x83);
			break;

		case 88:
			if (!(Global._tick_counter & 0x400))
				SET_AND_UNANIMATE(tile, 0x4F, 0x83);
			break;

		case 10:
			if (BitOps.CHANCE16(1,3)) {
				SndPlayTileFx(SND_0C_ELECTRIC_SPARK, tile);
				AddAnimatedTile(tile);
			}
			break;

		case 49:
			CreateEffectVehicleAbove(TileX(tile) * 16 + 6, TileY(tile) * 16 + 6, 43, EV_SMOKE);
			break;


		case 143: {
			Industry i = GetIndustry(tile.getMap().m2);
			if (i.was_cargo_delivered) {
				i.was_cargo_delivered = false;
				tile.getMap().m4 = 0;
				AddAnimatedTile(tile);
			}
		}
		break;

		case 161:
			TileLoopIndustryCase161(tile);
			break;

		case 165:
			AddAnimatedTile(tile);
			break;

		case 174:
			if (BitOps.CHANCE16(1, 3)) AddAnimatedTile(tile);
			break;
		}
	}


	static void ClickTile_Industry(TileIndex tile)
	{
		ShowIndustryViewWindow(tile.getMap().m2);
	}

	static int GetTileTrackStatus_Industry(TileIndex tile, /*TransportType*/ int mode)
	{
		return 0;
	}

	static ProducedCargo GetProducedCargo_Industry(TileIndex tile)
	{
		ProducedCargo ac = new ProducedCargo();
		final Industry  i = GetIndustry(tile.getMap().m2);

		ac.cargo[0] = i.produced_cargo[0];
		ac.cargo[1] = i.produced_cargo[1];

		return ac;
	}

	static void ChangeTileOwner_Industry(TileIndex tile, PlayerID old_player, PlayerID new_player)
	{
		/* not used */
	}

	void DeleteIndustry(Industry i)
	{
		BEGIN_TILE_LOOP(tile_cur, i.width, i.height, i.xy);
		TileIndex.forAll( i.width, i.height, i.xy, (tile_cur) ->
		if (tile_cur.IsTileType( TileTypes.MP_INDUSTRY)) {
			if (tile_cur.getMap().m2 == i.index) {
				DoClearSquare(tile_cur);
			}
		} else if (tile_cur.IsTileType( TileTypes.MP_STATION) && tile_cur.getMap().m5 == 0x4B) {
			DeleteOilRig(tile_cur);
		});
		//END_TILE_LOOP(tile_cur, i.width, i.height, i.xy);

		i.xy = 0;
		_industry_sort_dirty = true;
		DeleteSubsidyWithIndustry(i.index);
		Window.DeleteWindowById(Window.WC_INDUSTRY_VIEW, i.index);
		Window.InvalidateWindow(Window.WC_INDUSTRY_DIRECTORY, 0);
	}

	static final byte _plantfarmfield_type[] = {1, 1, 1, 1, 1, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6};

	static boolean IsBadFarmFieldTile(TileIndex tile)
	{
		switch (tile.GetTileType()) {
		case MP_CLEAR: {
			int m5 = tile.getMap().m5 & 0x1C;
			return m5 == 0xC || m5 == 0x10;
		}

		case MP_TREES:
			return false;

		default:
			return true;
		}
	}

	static boolean IsBadFarmFieldTile2(TileIndex tile)
	{
		switch (tile.GetTileType()) {
		case MP_CLEAR: {
			int m5 = tile.getMap().m5 & 0x1C;
			return m5 == 0x10;
		}

		case MP_TREES:
			return false;

		default:
			return true;
		}
	}

	static void SetupFarmFieldFence(TileIndex tile, int size, byte type, int direction)
	{
		byte or, and;

		do {
			tile = TILE_MASK(tile);

			if (tile.IsTileType( TileTypes.MP_CLEAR) || tile.IsTileType( TileTypes.MP_TREES)) {

				or = type;
				if (or == 1 && BitOps.CHANCE16(1, 7)) or = 2;

				or <<= 2;
				and = (byte)~0x1C;
				if (direction) {
					or <<= 3;
					and = (byte)~0xE0;
				}
				tile.getMap().m4 = (tile.getMap().m4 & and) | or;
			}

			tile += direction ? TileDiffXY(0, 1) : TileDiffXY(1, 0);
		} while (--size > 0);
	}

	static void PlantFarmField(TileIndex tile)
	{
		int size_x, size_y;
		int r;
		int count;
		int type, type2;

		if (GameOptions._opt.landscape == Landscape.LT_HILLY) {
			if (GetTileZ(tile) + 16 >= GameOptions._opt.snow_line)
				return;
		}

		/* determine field size */
		r = (Hal.Random() & 0x303) + 0x404;
		if (GameOptions._opt.landscape == Landscape.LT_HILLY) r += 0x404;
		size_x = BitOps.GB(r, 0, 8);
		size_y = BitOps.GB(r, 8, 8);

		/* offset tile to match size */
		tile -= TileDiffXY(size_x / 2, size_y / 2);

		/* check the amount of bad tiles */
		count = 0;

		//BEGIN_TILE_LOOP(cur_tile, size_x, size_y, tile)
		TileIndex.forAll(size_x, size_y, tile, (cur_tile) ->
		{
			//cur_tile = TILE_MASK(cur_tile);
			count += IsBadFarmFieldTile(TILE_MASK(cur_tile));
		});
		//END_TILE_LOOP(cur_tile, size_x, size_y, tile)

		if (count * 2 >= size_x * size_y) return;

		/* determine type of field */
		r = Hal.Random();
		type = ((r & 0xE0) | 0xF);
		type2 = BitOps.GB(r, 8, 8) * 9 >> 8;

		/* make field */
		//BEGIN_TILE_LOOP(cur_tile, size_x, size_y, tile)
		TileIndex.forAll(size_x, size_y, tile, (cur_tile1) ->
		{
			TileIndex cur_tile = TILE_MASK(cur_tile1);
			if (!IsBadFarmFieldTile2(cur_tile)) {
				Landscape.ModifyTile(cur_tile,
						TileTypes.MP_SETTYPE(TileTypes.MP_CLEAR) |
						TileTypes.MP_MAP2_CLEAR | TileTypes.MP_MAP3LO | TileTypes.MP_MAP3HI_CLEAR | TileTypes.MP_MAPOWNER | TileTypes.MP_MAP5,
						type2,			/* map3_lo */
						Owner.OWNER_NONE,	/* map_owner */
						type);			/* map5 */
			}
		});
		//END_TILE_LOOP(cur_tile, size_x, size_y, tile)

		type = 3;
		if (GameOptions._opt.landscape != Landscape.LT_HILLY && GameOptions._opt.landscape != Landscape.LT_DESERT) {
			type = _plantfarmfield_type[Hal.Random() & 0xF];
		}

		SetupFarmFieldFence(tile - TileDiffXY(1, 0), size_y, type, 1);
		SetupFarmFieldFence(tile - TileDiffXY(0, 1), size_x, type, 0);
		SetupFarmFieldFence(tile + TileDiffXY(size_x - 1, 0), size_y, type, 1);
		SetupFarmFieldFence(tile + TileDiffXY(0, size_y - 1), size_x, type, 0);
	}

	static void MaybePlantFarmField(final Industry  i)
	{
		if (BitOps.CHANCE16(1, 8)) {
			int x = i.width  / 2 + Hal.Random() % 31 - 16;
			int y = i.height / 2 + Hal.Random() % 31 - 16;
			TileIndex tile = TileAddWrap(i.xy, x, y);
			if (tile != INVALID_TILE) PlantFarmField(tile);
		}
	}

	static final TileIndexDiffC _chop_dir[] = {
			new TileIndexDiffC( 0,  1),
			new TileIndexDiffC( 1,  0),
			new TileIndexDiffC( 0, -1),
			new TileIndexDiffC(-1,  0)
	};

	static void ChopLumberMillTrees(Industry i)
	{

		TileIndex tile = i.xy;
		int a;

		if ((tile.getMap().m1 & 0x80) == 0) return;

		/* search outwards as a rectangular spiral */
		for (a = 1; a != 41; a += 2) {
			int dir;

			for (dir = 0; dir != 4; dir++) {
				int j = a;

				do {
					tile = TILE_MASK(tile);
					if (tile.IsTileType( TileTypes.MP_TREES)) {
						PlayerID old_player = Global._current_player;
						/* found a tree */

						Global._current_player = Owner.OWNER_NONE;
						_industry_sound_ctr = 1;
						_industry_sound_tile = tile;
						//SndPlayTileFx(SND_38_CHAINSAW, tile);

						Cmd.DoCommandByTile(tile, 0, 0, Cmd.DC_EXEC, Cmd.CMD_LANDSCAPE_CLEAR);
						tile.SetMapExtraBits(0);

						i.cargo_waiting[0] = Math.min(0xffff, i.cargo_waiting[0] + 45);

						Global._current_player = old_player;
						return;
					}
					tile += ToTileIndexDiff(_chop_dir[dir]);
				} while (--j > 0);
			}
			tile -= TileDiffXY(1, 1);
		}
	}

	/*
	static final byte _industry_sounds[][] = {
			{0},
			{0},
			{1, SND_28_SAWMILL},
			{0},
			{0},
			{0},
			{1, SND_03_FACTORY_WHISTLE},
			{1, SND_03_FACTORY_WHISTLE},
			{0},
			{3, SND_24_SHEEP},
			{0},
			{0},
			{0},
			{0},
			{1, SND_28_SAWMILL},
			{0},
			{0},
			{0},
			{0},
			{0},
			{0},
			{0},
			{0},
			{1, SND_03_FACTORY_WHISTLE},
			{0},
			{0},
			{0},
			{0},
			{0},
			{0},
			{0},
			{0},
			{1, SND_33_PLASTIC_MINE},
			{0},
			{0},
			{0},
			{0},
	};
	 */

	static void ProduceIndustryGoods(Industry i)
	{
		int r;
		int num;

		/* play a sound? */
		if ((i.counter & 0x3F) == 0) {
			//if (BitOps.CHANCE16R(1,14,r) && (num=_industry_sounds[i.type][0]) != 0) {
				//SndPlayTileFx(					_industry_sounds[i.type][1] + (((r >> 16) * num) >> 16),					i.xy);
			//}
		}

		i.counter--;

		/* produce some cargo */
		if ((i.counter & 0xFF) == 0) {
			i.cargo_waiting[0] = Math.min(0xffff, i.cargo_waiting[0] + i.production_rate[0]);
			i.cargo_waiting[1] = Math.min(0xffff, i.cargo_waiting[1] + i.production_rate[1]);

			if (i.type == IT_FARM) {
				MaybePlantFarmField(i);
			} else if (i.type == IT_LUMBER_MILL && (i.counter & 0x1FF) == 0) {
				ChopLumberMillTrees(i);
			}
		}
	}

	void OnTick_Industry()
	{
		Industry i;

		if (_industry_sound_ctr != 0) {
			_industry_sound_ctr++;

			if (_industry_sound_ctr == 75) {
				//SndPlayTileFx(SND_37_BALLOON_SQUEAK, _industry_sound_tile);
			} else if (_industry_sound_ctr == 160) {
				_industry_sound_ctr = 0;
				//SndPlayTileFx(SND_36_CARTOON_CRASH, _industry_sound_tile);
			}
		}

		if (Global._game_mode == GameModes.GM_EDITOR) return;

		FOR_ALL_INDUSTRIES(i) {
			if (i.xy != 0) ProduceIndustryGoods(i);
		}
	}


	static boolean CheckNewIndustry_null(TileIndex tile, int type)
	{
		return true;
	}

	static boolean CheckNewIndustry_Forest(TileIndex tile, int type)
	{
		if (GameOptions._opt.landscape == Landscape.LT_HILLY) {
			if (GetTileZ(tile) < GameOptions._opt.snow_line + 16U) {
				Global._error_message = Str.STR_4831_FOREST_CAN_ONLY_BE_PLANTED;
				return false;
			}
		}
		return true;
	}

	//extern boolean _ignore_restrictions;

	/* Oil Rig and Oil Refinery */
	static boolean CheckNewIndustry_Oil(TileIndex tile, int type)
	{
		if (Global._game_mode == GameModes.GM_EDITOR && _ignore_restrictions) return true;
		if (Global._game_mode == GameModes.GM_EDITOR && type != IT_OIL_RIG)   return true;
		if (Map.DistanceFromEdge(TILE_ADDXY(tile, 1, 1)) < 16)   return true;

		Global._error_message = Str.STR_483B_CAN_ONLY_BE_POSITIONED;
		return false;
	}

	static boolean CheckNewIndustry_Farm(TileIndex tile, int type)
	{
		if (GameOptions._opt.landscape == Landscape.LT_HILLY) {
			if (tile.GetTileZ() + 16 >= GameOptions._opt.snow_line) {
				Global._error_message = Str.STR_0239_SITE_UNSUITABLE;
				return false;
			}
		}
		return true;
	}

	static boolean CheckNewIndustry_Plantation(TileIndex tile, int type)
	{
		if (tile.GetMapExtraBits() == 1) {
			Global._error_message = Str.STR_0239_SITE_UNSUITABLE;
			return false;
		}

		return true;
	}

	static boolean CheckNewIndustry_Water(TileIndex tile, int type)
	{
		if (tile.GetMapExtraBits() != 1) {
			Global._error_message = Str.STR_0318_CAN_ONLY_BE_BUILT_IN_DESERT;
			return false;
		}

		return true;
	}

	static boolean CheckNewIndustry_Lumbermill(TileIndex tile, int type)
	{
		if (tile.GetMapExtraBits() != 2) {
			Global._error_message = Str.STR_0317_CAN_ONLY_BE_BUILT_IN_RAINFOREST;
			return false;
		}
		return true;
	}

	static boolean CheckNewIndustry_BubbleGen(TileIndex tile, int type)
	{
		return tile.GetTileZ() <= 32;
	}

	static final CheckNewIndustryProc _check_new_industry_procs[] = {
			Industry::CheckNewIndustry_null,
			Industry::CheckNewIndustry_Forest,
			Industry::CheckNewIndustry_Oil,
			Industry::CheckNewIndustry_Farm,
			Industry::CheckNewIndustry_Plantation,
			Industry::CheckNewIndustry_Water,
			Industry::CheckNewIndustry_Lumbermill,
			Industry::CheckNewIndustry_BubbleGen,
	};

	static boolean CheckSuitableIndustryPos(TileIndex tile)
	{
		int x = tile.TileX();
		int y = tile.TileY();

		if (x < 2 || y < 2 || x > Global.MapMaxX() - 3 || y > Global.MapMaxY() - 3) {
			Global._error_message = Str.STR_0239_SITE_UNSUITABLE;
			return false;
		}

		return true;
	}

	static final Town CheckMultipleIndustryInTown(TileIndex tile, int type)
	{
		final Town t;
		final Industry  i;

		t = ClosestTownFromTile(tile, (int)-1);

		if (Global._patches.multiple_industry_per_town) return t;

		FOR_ALL_INDUSTRIES(i) 
		{
			if (i.xy != 0 &&
					i.type == (byte)type &&
					i.town == t) {
				Global._error_message = Str.STR_0287_ONLY_ONE_ALLOWED_PER_TOWN;
				return null;
			}
		}

		return t;
	}

	static final byte _industry_map5_bits[] = {
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 4, 2, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 4, 2, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16, 16,
			16, 16, 16, 16, 16, 16, 16,
	};

	static boolean CheckIfIndustryTilesAreFree(TileIndex tile, final IndustryTileTable it, int type, final Town t)
	{
		TileInfo ti = new TileInfo();

		Global._error_message = Str.STR_0239_SITE_UNSUITABLE;

		do {
			TileIndex cur_tile = tile.iadd( TileIndex.ToTileIndexDiff(it.ti) );

			if (!cur_tile.IsValidTile()) {
				if (it.map5 == 0xff) continue;
				return false;
			}

			FindLandscapeHeightByTile(ti, cur_tile);

			if (it.map5 == 0xFF) {
				if (ti.type != TileTypes.MP_WATER || ti.tileh != 0) return false;
			} else {
				if (!cur_tile.EnsureNoVehicle()) return false;

				if (type == IT_OIL_RIG)  {
					if (ti.type != TileTypes.MP_WATER || ti.map5 != 0) return false;
				} else {
					if (ti.type == TileTypes.MP_WATER && ti.map5 == 0) return false;
					if (IsSteepTileh(ti.tileh))
						return false;

					if (ti.tileh != 0) {
						int t;
						byte bits = _industry_map5_bits[it.map5];

						if (bits & 0x10) return false;

						t = ~ti.tileh;

						if (bits & 1 && (t & (1 + 8))) return false;
						if (bits & 2 && (t & (4 + 8))) return false;
						if (bits & 4 && (t & (1 + 2))) return false;
						if (bits & 8 && (t & (2 + 4))) return false;
					}

					if (type == IT_BANK) {
						if (ti.type != TileTypes.MP_HOUSE || t.population < 1200) {
							Global._error_message = Str.STR_029D_CAN_ONLY_BE_BUILandscape.LT_IN_TOWNS;
							return false;
						}
					} else if (type == IT_BANK_2) {
						if (ti.type != TileTypes.MP_HOUSE) {
							Global._error_message = Str.STR_030D_CAN_ONLY_BE_BUILandscape.LT_IN_TOWNS;
							return false;
						}
					} else if (type == IT_TOY_SHOP) {
						if (DistanceMax(t.xy, cur_tile) > 9) return false;
						if (ti.type != TileTypes.MP_HOUSE) goto do_clear;
					} else if (type == IT_WATER_TOWER) {
						if (ti.type != TileTypes.MP_HOUSE) {
							Global._error_message = Str.STR_0316_CAN_ONLY_BE_BUILandscape.LT_IN_TOWNS;
							return false;
						}
					} else {
						do_clear:
							if (CmdFailed(DoCommandByTile(cur_tile, 0, 0, Cmd.DC_AUTO, Cmd.CMD_LANDSCAPE_CLEAR)))
								return false;
					}
				}
			}
		} while ((++it).ti.x != -0x80);

		return true;
	}

	static boolean CheckIfTooCloseToIndustry(TileIndex tile, int type)
	{
		final IndustrySpec spec = _industry_spec[type];
		final Industry  i;

		// accepting industries won't be close, not even with patch
		if (Global._patches.same_industry_close && spec.accepts_cargo[0] == AcceptedCargo.CT_INVALID)
			return true;

		FOR_ALL_INDUSTRIES(i) 
		{
			// check if an industry that accepts the same goods is nearby
			if (i.xy != 0 &&
					DistanceMax(tile, i.xy) <= 14 &&
					spec.accepts_cargo[0] != AcceptedCargo.CT_INVALID &&
					spec.accepts_cargo[0] == i.accepts_cargo[0] && (
							Global._game_mode != GameModes.GM_EDITOR ||
							!Global._patches.same_industry_close ||
							!Global._patches.multiple_industry_per_town
							)) {
				Global._error_message = Str.STR_INDUSTRY_TOO_CLOSE;
				return false;
			}

			// check "not close to" field.
			if (i.xy != 0 &&
					(i.type == spec.a || i.type == spec.b || i.type == spec.c) &&
					DistanceMax(tile, i.xy) <= 14) {
				Global._error_message = Str.STR_INDUSTRY_TOO_CLOSE;
				return false;
			}
		}
		return true;
	}

	static Industry AllocateIndustry()
	{
		Industry i;

		FOR_ALL_INDUSTRIES(i) 
		{
			if (i.xy == 0) {
				int index = i.index;

				if (i.index > _total_industries) _total_industries = i.index;

				memset(i, 0, sizeof(*i));
				i.index = index;

				return i;
			}
		}

		/* Check if we can add a block to the pool */
		return AddBlockToPool(&_industry_pool) ? AllocateIndustry() : null;
	}

	static void DoCreateNewIndustry(Industry  i, TileIndex tile, int type, final IndustryTileTable it, final Town t, byte owner)
	{
		final IndustrySpec spec;
		int r;
		int j;

		i.xy = tile;
		i.width = i.height = 0;
		i.type = type;

		spec = _industry_spec[type];

		i.produced_cargo[0] = spec.produced_cargo[0];
		i.produced_cargo[1] = spec.produced_cargo[1];
		i.accepts_cargo[0] = spec.accepts_cargo[0];
		i.accepts_cargo[1] = spec.accepts_cargo[1];
		i.accepts_cargo[2] = spec.accepts_cargo[2];
		i.production_rate[0] = spec.production_rate[0];
		i.production_rate[1] = spec.production_rate[1];

		if (Global._patches.smooth_economy) {
			i.production_rate[0] = Math.min((RandomRange(256) + 128) * i.production_rate[0] >> 8 , 255);
			i.production_rate[1] = Math.min((RandomRange(256) + 128) * i.production_rate[1] >> 8 , 255);
		}

		i.town = t;
		i.owner = owner;

		r = Hal.Random();
		i.color_map = BitOps.GB(r, 8, 4);
		i.counter = BitOps.GB(r, 0, 12);
		i.cargo_waiting[0] = 0;
		i.cargo_waiting[1] = 0;
		i.last_mo_production[0] = 0;
		i.last_mo_production[1] = 0;
		i.last_mo_transported[0] = 0;
		i.last_mo_transported[1] = 0;
		i.pct_transported[0] = 0;
		i.pct_transported[1] = 0;
		i.total_transported[0] = 0;
		i.total_transported[1] = 0;
		i.was_cargo_delivered = false;
		i.last_prod_year = _cur_year;
		i.total_production[0] = i.production_rate[0] * 8;
		i.total_production[1] = i.production_rate[1] * 8;

		if (!_generating_world) i.total_production[0] = i.total_production[1] = 0;

		i.prod_level = 0x10;

		do {
			TileIndex cur_tile = tile + ToTileIndexDiff(it.ti);

			if (it.map5 != 0xFF) {
				byte size;

				size = it.ti.x;
				if (size > i.width) i.width = size;
				size = it.ti.y;
				if (size > i.height)i.height = size;

				DoCommandByTile(cur_tile, 0, 0, Cmd.DC_EXEC, Cmd.CMD_LANDSCAPE_CLEAR);

				SetTileType(cur_tile, TileTypes.MP_INDUSTRY);
				_m[cur_tile].m5 = it.map5;
				_m[cur_tile].m2 = i.index;
				_m[cur_tile].m1 = _generating_world ? 0x1E : 0; /* maturity */
			}
		} while ((++it).ti.x != -0x80);

		i.width++;
		i.height++;

		if (i.type == IT_FARM || i.type == IT_FARM_2) {
			tile = i.xy + TileDiffXY(i.width / 2, i.height / 2);
			for (j = 0; j != 50; j++) {
				int x = Hal.Random() % 31 - 16;
				int y = Hal.Random() % 31 - 16;
				TileIndex new_tile = TileAddWrap(tile, x, y);

				if (new_tile != INVALID_TILE) PlantFarmField(new_tile);
			}
		}
		_industry_sort_dirty = true;
		Window.InvalidateWindow(Window.WC_INDUSTRY_DIRECTORY, 0);
	}

	/** Build/Fund an industry
	 * @param x,y coordinates where industry is built
	 * @param p1 industry type @see build_industry.h and @see industry.h
	 * @param p2 unused
	 */
	int CmdBuildIndustry(int x, int y, int flags, int p1, int p2)
	{
		final Town t;
		Industry i;
		TileIndex tile = TileVirtXY(x, y);
		int num;
		final IndustryTileTable [][] itt;
		final IndustryTileTable [] it;
		final IndustrySpec [] spec;

		Player.SET_EXPENSES_TYPE(Player.EXPENSES_OTHER);

		if (!CheckSuitableIndustryPos(tile)) return Cmd.CMD_ERROR;

		/* Check if the to-be built/founded industry is available for this climate.
		 * Unfortunately we have no easy way of checking, except for looping the table */
		{
			//final byte* i;
			int i;
			boolean found = false;

			/* for (
					i = &_build_industry_types[GameOptions._opt_ptr.landscape][0]; 
					i != endof(_build_industry_types[GameOptions._opt_ptr.landscape]); 
					i++) */
			byte [] array = _build_industry_types[GameOptions._opt_ptr.landscape];
			for( i = 0; i < array.length; i++ )
			{
				if (array[i] == p1) {
					found = true;
					break;
				}
			}
			if (!found) return Cmd.CMD_ERROR;
		}

		spec = _industry_spec[p1];
		/* If the patch for raw-material industries is not on, you cannot build raw-material industries.
		 * Raw material industries are industries that do not accept cargo (at least for now)
		 * Exclude the lumber mill (only "raw" industry that can be built) */
		if (!Global._patches.build_rawmaterial_ind &&
				spec.accepts_cargo[0] == AcceptedCargo.CT_INVALID &&
				spec.accepts_cargo[1] == AcceptedCargo.CT_INVALID &&
				spec.accepts_cargo[2] == AcceptedCargo.CT_INVALID &&
				p1 != IT_LUMBER_MILL) {
			return Cmd.CMD_ERROR;
		}

		if (!_check_new_industry_procs[spec.check_proc](tile, p1)) return Cmd.CMD_ERROR;

		t = CheckMultipleIndustryInTown(tile, p1);
		if (t == null) return Cmd.CMD_ERROR;

		num = spec.num_table;
		itt = spec.table;

		do {
			if (--num < 0) return_cmd_error(Str.STR_0239_SITE_UNSUITABLE);
		} while (!CheckIfIndustryTilesAreFree(tile, it = itt[num], p1, t));


		if (!CheckIfTooCloseToIndustry(tile, p1)) return Cmd.CMD_ERROR;

		i = AllocateIndustry();
		if (i == null) return Cmd.CMD_ERROR;

		if (flags & Cmd.DC_EXEC) DoCreateNewIndustry(i, tile, p1, it, t, Owner.OWNER_NONE);

		return (Global._price.build_industry >> 5) * _industry_type_costs[p1];
	}


	Industry CreateNewIndustry(TileIndex tile, int type)
	{
		final Town t;
		final IndustryTileTable it;
		Industry i;

		final IndustrySpec spec;

		if (!CheckSuitableIndustryPos(tile)) return null;

		spec = &_industry_spec[type];

		if (!_check_new_industry_procs[spec.check_proc](tile, type)) return null;

		t = CheckMultipleIndustryInTown(tile, type);
		if (t == null) return null;

		/* pick a random layout */
		it = spec.table[RandomRange(spec.num_table)];

		if (!CheckIfIndustryTilesAreFree(tile, it, type, t)) return null;
		if (!CheckIfTooCloseToIndustry(tile, type)) return null;

		i = AllocateIndustry();
		if (i == null) return null;

		DoCreateNewIndustry(i, tile, type, it, t, Owner.OWNER_NONE);

		return i;
	}

	static final byte _numof_industry_table[][] = {
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 1, 1, 1, 2, 2, 3, 3, 4, 4, 5},
			{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
			{0, 2, 3, 4, 6, 7, 8, 9, 10, 10, 10},
	};

	static void PlaceInitialIndustry(byte type, int amount)
	{
		int num = _numof_industry_table[GameOptions._opt.diff.number_industries][amount];

		if (type == IT_OIL_REFINERY || type == IT_OIL_RIG) {
			// These are always placed next to the coastline, so we scale by the perimeter instead.
			num = Map.ScaleByMapSize1D(num);
		} else {
			num = Map.ScaleByMapSize(num);
		}

		if (GameOptions._opt.diff.number_industries != 0) {
			PlayerID old_player = Global._current_player;
			Global._current_player = new PlayerID( Owner.OWNER_NONE );
			assert(num > 0);

			do {
				int i;

				for (i = 0; i < 2000; i++) {
					if (CreateNewIndustry(Hal.RandomTile(), type) != null) break;
				}
			} while (--num > 0);

			Global._current_player = old_player;
		}
	}

	void GenerateIndustries()
	{
		final byte []b;

		b = _industry_create_table[GameOptions._opt.landscape];
		do {
			PlaceInitialIndustry(b[1], b[0]);
		} while ( (b+=2)[0] != 0);
	}

	static void ExtChangeIndustryProduction(Industry i)
	{
		boolean closeit = true;
		int j;

		switch (_industry_close_mode[i.type]) {
		case INDUSTRY_NOT_CLOSABLE:
			return;

		case INDUSTRY_CLOSABLE:
			if ((byte)(_cur_year - i.last_prod_year) < 5 || !BitOps.CHANCE16(1, 180))
				closeit = false;
			break;

		default: /* INDUSTRY_PRODUCTION */
			for (j = 0; j < 2 && i.produced_cargo[j] != AcceptedCargo.CT_INVALID; j++){
				int r = Hal.Random();
				int old, inew, percent;
				int mag;

				inew = old = i.production_rate[j];
				if (BitOps.CHANCE16I(20, 1024, r))
					inew -= ((Hal.RandomRange(50) + 10) * old) >> 8;
			if (BitOps.CHANCE16I(20 + (i.pct_transported[j] * 20 >> 8), 1024, r >> 16))
				inew += ((Hal.RandomRange(50) + 10) * old) >> 8;

				inew = BitOps.clamp(inew, 0, 255);
				if (inew == old) {
					closeit = false;
					continue;
				}

				percent = inew * 100 / old - 100;
				i.production_rate[j] = inew;

				if (inew >= _industry_spec[i.type].production_rate[j] / 4)
					closeit = false;

				mag = abs(percent);
				if (mag >= 10) {
					Global.SetDParam(2, mag);
					Global.SetDParam(0, _cargoc.names_s[i.produced_cargo[j]]);
					Global.SetDParam(1, i.index);
					NewsItem.AddNewsItem(
							percent >= 0 ? Str.STR_INDUSTRY_PROD_GOUP : Str.STR_INDUSTRY_PROD_GODOWN,
									NewsItem.NEWS_FLAGS(NewsItem.NM_THIN, NewsItem.NF_VIEWPORT|NewsItem.NF_TILE, NewsItem.NT_ECONOMY, 0),
									i.xy + TileDiffXY(1, 1), 0
							);
				}
			}
			break;
		}

		if (closeit) {
			i.prod_level = 0;
			Global.SetDParam(0, i.index);
			NewsItem.AddNewsItem(
					_industry_close_strings[i.type],
					NewsItem.NEWS_FLAGS(NewsItem.NM_THIN, NewsItem.NF_VIEWPORT|NewsItem.NF_TILE, NewsItem.NT_ECONOMY, 0),
					i.xy + TileIndex.TileDiffXY(1, 1), 0
					);
		}
	}


	static void UpdateIndustryStatistics(Industry i)
	{
		byte pct;

		if (i.produced_cargo[0] != AcceptedCargo.CT_INVALID) {
			pct = 0;
			if (i.last_mo_production[0] != 0) {
				i.last_prod_year = _cur_year;
				pct = Math.min(i.last_mo_transported[0] * 256 / i.last_mo_production[0],255);
			}
			i.pct_transported[0] = pct;

			i.total_production[0] = i.last_mo_production[0];
			i.last_mo_production[0] = 0;

			i.total_transported[0] = i.last_mo_transported[0];
			i.last_mo_transported[0] = 0;
		}

		if (i.produced_cargo[1] != AcceptedCargo.CT_INVALID) {
			pct = 0;
			if (i.last_mo_production[1] != 0) {
				i.last_prod_year = _cur_year;
				pct = Math.min(i.last_mo_transported[1] * 256 / i.last_mo_production[1],255);
			}
			i.pct_transported[1] = pct;

			i.total_production[1] = i.last_mo_production[1];
			i.last_mo_production[1] = 0;

			i.total_transported[1] = i.last_mo_transported[1];
			i.last_mo_transported[1] = 0;
		}


		if (i.produced_cargo[0] != AcceptedCargo.CT_INVALID || i.produced_cargo[1] != AcceptedCargo.CT_INVALID)
			Window.InvalidateWindow(Window.WC_INDUSTRY_VIEW, i.index);

		if (i.prod_level == 0) {
			DeleteIndustry(i);
		} else if (Global._patches.smooth_economy) {
			ExtChangeIndustryProduction(i);
		}
	}

	static final byte _new_industry_rand[][] = {
			{12,12,12,12,12,12,12, 0, 0, 6, 6, 9, 9, 3, 3, 3,18,18, 4, 4, 2, 2, 5, 5, 5, 5, 5, 5, 1, 1, 8, 8},
			{16,16,16, 0, 0, 0, 9, 9, 9, 9,13,13, 3, 3, 3, 3,15,15,15, 4, 4,11,11,11,11,11,14,14, 1, 1, 7, 7},
			{21,21,21,24,22,22,22,22,23,23,12,12,12, 4, 4,19,19,19,13,13,20,20,20,11,11,11,17,17,17,10,10,10},
			{30,30,30,36,36,31,31,31,27,27,27,28,28,28,26,26,26,34,34,34,35,35,35,29,29,29,32,32,32,33,33,33},
	};

	static void MaybeNewIndustry(int r)
	{
		int type;
		int j;
		Industry i;

		type = _new_industry_rand[GameOptions._opt.landscape][BitOps.GB(r, 16, 5)];

		if (type == IT_OIL_WELL && _date > 10958) return;
		if (type == IT_OIL_RIG  && _date < 14610) return;

		j = 2000;
		for (;;) {
			i = CreateNewIndustry(Hal.RandomTile(), type);
			if (i != null) break;
			if (--j == 0) return;
		}

		Global.SetDParam(0, type + Str.STR_4802_COAL_MINE);
		Global.SetDParam(1, i.town.index);
		NewsItem.AddNewsItem(
				(type != IT_FOREST) ?
						Str.STR_482D_NEW_UNDER_CONSTRUCTION : Str.STR_482E_NEW_BEING_PLANTED_NEAR,
						NewsItem.NEWS_FLAGS(NewsItem.NM_THIN, NewsItem.NF_VIEWPORT|NewsItem.NF_TILE, NewsItem.NT_ECONOMY,0), i.xy, 0
				);
	}

	static void ChangeIndustryProduction(Industry i)
	{
		boolean only_decrease = false;
		/*StringID*/ int str = Str.STR_NULL;
		int type = i.type;

		switch (_industry_close_mode[type]) {
		case INDUSTRY_NOT_CLOSABLE:
			return;

		case INDUSTRY_PRODUCTION:
			/* decrease or increase */
			if (type == IT_OIL_WELL && GameOptions._opt.landscape == Landscape.LT_NORMAL)
				only_decrease = true;

			if (only_decrease || BitOps.CHANCE16(1,3)) {
				/* If you transport > 60%, 66% chance we increase, else 33% chance we increase */
				if (!only_decrease && (i.pct_transported[0] > 153) != BitOps.CHANCE16(1,3)) {
					/* Increase production */
					if (i.prod_level != 0x80) {
						byte b;

						i.prod_level <<= 1;

						b = i.production_rate[0] * 2;
						if (i.production_rate[0] >= 128)
							b = 0xFF;
						i.production_rate[0] = b;

						b = i.production_rate[1] * 2;
						if (i.production_rate[1] >= 128)
							b = 0xFF;
						i.production_rate[1] = b;

						str = _industry_prod_up_strings[type];
					}
				} else {
					/* Decrease production */
					if (i.prod_level == 4) {
						i.prod_level = 0;
						str = _industry_close_strings[type];
					} else {
						i.prod_level >>= 1;
						i.production_rate[0] = (i.production_rate[0] + 1) >> 1;
						i.production_rate[1] = (i.production_rate[1] + 1) >> 1;

						str = _industry_prod_down_strings[type];
					}
				}
			}
			break;

		case INDUSTRY_CLOSABLE:
			/* maybe close */
			if ( (byte)(_cur_year - i.last_prod_year) >= 5 && BitOps.CHANCE16(1,2)) {
				i.prod_level = 0;
				str = _industry_close_strings[type];
			}
			break;
		}

		if (str != Str.STR_NULL) {
			Global.SetDParam(0, i.index);
			NewsItem.AddNewsItem(str, NewsItem.NEWS_FLAGS(NewsItem.NM_THIN, NewsItem.NF_VIEWPORT|NewsItem.NF_TILE, NewsItem.NT_ECONOMY, 0), i.xy + TileDiffXY(1, 1), 0);
		}
	}

	void IndustryMonthlyLoop()
	{
		Industry i;
		PlayerID old_player = Global._current_player;
		Global._current_player = Owner.OWNER_NONE;

		FOR_ALL_INDUSTRIES(i) 
		{
			if (i.xy != 0) UpdateIndustryStatistics(i);
		}

		/* 3% chance that we start a new industry */
		if (BitOps.CHANCE16(3, 100)) {
			MaybeNewIndustry(Hal.Random());
		} else if (!Global._patches.smooth_economy && _total_industries > 0) {
			i = GetIndustry(Hal.RandomRange(_total_industries));
			if (i.xy != 0) ChangeIndustryProduction(i);
		}

		Global._current_player = old_player;

		// production-change
		_industry_sort_dirty = true;
		Window.InvalidateWindow(Window.WC_INDUSTRY_DIRECTORY, 0);
	}


	void InitializeIndustries()
	{
		_industry_pool.CleanPool();
		_industry_pool.AddBlockToPool();

		_total_industries = 0;
		_industry_sort_dirty = true;
	}

	final TileTypeProcs _tile_type_industry_procs = new TileTypeProcs(
			Industry::DrawTile_Industry,					/* draw_tile_proc */
			Industry::GetSlopeZ_Industry,					/* get_slope_z_proc */
			Industry::ClearTile_Industry,					/* clear_tile_proc */
			Industry::GetAcceptedCargo_Industry,	/* get_accepted_cargo_proc */
			Industry::GetTileDesc_Industry,				/* get_tile_desc_proc */
			Industry::GetTileTrackStatus_Industry,/* get_tile_track_status_proc */
			Industry::ClickTile_Industry,					/* click_tile_proc */
			Industry::AnimateTile_Industry,				/* animate_tile_proc */
			Industry::TileLoop_Industry,					/* tile_loop_proc */
			Industry::ChangeTileOwner_Industry,		/* change_tile_owner_proc */
			Industry::GetProducedCargo_Industry,  /* get_produced_cargo_proc */
			null,												/* vehicle_enter_tile_proc */
			null,												/* vehicle_leave_tile_proc */
			Industry::GetSlopeTileh_Industry			/* get_slope_tileh_proc */
			);
	/*
	static final SaveLoad _industry_desc[] = {
		SLE_CONDVAR(Industry, xy,					SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(Industry, xy,					SLE_UINT32, 6, 255),
		SLE_VAR(Industry,width,						SLE_UINT8),
		SLE_VAR(Industry,height,					SLE_UINT8),
		SLE_REF(Industry,town,						REF_TOWN),
		SLE_ARR(Industry,produced_cargo,  SLE_UINT8, 2),
		SLE_ARR(Industry,cargo_waiting,   SLE_UINT16, 2),
		SLE_ARR(Industry,production_rate, SLE_UINT8, 2),
		SLE_ARR(Industry,accepts_cargo,		SLE_UINT8, 3),
		SLE_VAR(Industry,prod_level,			SLE_UINT8),
		SLE_ARR(Industry,last_mo_production,SLE_UINT16, 2),
		SLE_ARR(Industry,last_mo_transported,SLE_UINT16, 2),
		SLE_ARR(Industry,pct_transported,SLE_UINT8, 2),
		SLE_ARR(Industry,total_production,SLE_UINT16, 2),
		SLE_ARR(Industry,total_transported,SLE_UINT16, 2),

		SLE_VAR(Industry,counter,					SLE_UINT16),

		SLE_VAR(Industry,type,						SLE_UINT8),
		SLE_VAR(Industry,owner,						SLE_UINT8),
		SLE_VAR(Industry,color_map,				SLE_UINT8),
		SLE_VAR(Industry,last_prod_year,	SLE_UINT8),
		SLE_VAR(Industry,was_cargo_delivered,SLE_UINT8),

		// reserve extra space in savegame here. (currently 32 bytes)
		SLE_CONDARR(NullStruct,null,SLE_FILE_U64 | SLE_VAR_null, 4, 2, 255),

		SLE_END()
	};

	static void Save_INDY()
	{
		Industry ind;

		// Write the vehicles
		FOR_ALL_INDUSTRIES(ind) {
			if (ind.xy != 0) {
				SlSetArrayIndex(ind.index);
				SlObject(ind, _industry_desc);
			}
		}
	}

	static void Load_INDY()
	{
		int index;

		_total_industries = 0;

		while ((index = SlIterateArray()) != -1) {
			Industry i;

			if (!AddBlockIfNeeded(&_industry_pool, index))
				error("Industries: failed loading savegame: too many industries");

			i = GetIndustry(index);
			SlObject(i, _industry_desc);

			if (index > _total_industries) _total_industries = index;
		}
	}

	final ChunkHandler _industry_chunk_handlers[] = {
		{ 'INDY', Save_INDY, Load_INDY, CH_ARRAY | CH_LAST},
	};

	 */


















	/* Present in table/build_industry.h" */
	//extern final byte _build_industry_types[4][12];
	//extern final byte _industry_type_costs[37];

	//static void UpdateIndustryProduction(Industry i);
	//extern void DrawArrowButtons(int x, int y, int state);

	static void BuildIndustryWndProc(Window w, WindowEvent e)
	{
		switch(e.event) {
		case WindowEvents.WE_PAINT:
			w.DrawWindowWidgets();
			if (_thd.place_mode == 1 && _thd.window_class == Window.WC_BUILD_INDUSTRY) {
				int ind_type = _build_industry_types[GameOptions._opt_ptr.landscape][w.as_def_d().data_1];

				Global.SetDParam(0, (Global._price.build_industry >> 5) * _industry_type_costs[ind_type]);
				Gfx.DrawStringCentered(85, w.height - 21, Str.STR_482F_COST, 0);
			}
			break;

		case WindowEvents.WE_CLICK: {
			int wid = e.widget;
			if (wid >= 3) {
				if (HandlePlacePushButton(w, wid, Sprite.SPR_CURSOR_INDUSTRY, 1, null))
					w.as_def_d().data_1 = wid - 3;
			}
		} break;

		case WindowEvents.WE_PLACE_OBJ:
			if (Cmd.DoCommandP(e.place.tile, _build_industry_types[GameOptions._opt_ptr.landscape][w.as_def_d().data_1], 0, null, Cmd.CMD_BUILD_INDUSTRY | Cmd.CMD_MSG(Str.STR_4830_CAN_T_CONSTRUCT_THIS_INDUSTRY)))
				ResetObjectToPlace();
			break;

		case WindowEvents.WE_ABORT_PLACE_OBJ:
			w.click_state = 0;
			w.SetWindowDirty();
			break;
		}
	}

	static final Widget _build_industry_land0_widgets[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,											Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,		Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   115, 0x0,														Str.STR_NULL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0241_POWER_STATION,				Str.STR_0263_CONSTRUCT_POWER_STATION),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_0242_SAWMILL,							Str.STR_0264_CONSTRUCT_SAWMILL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_0244_OIL_REFINERY,					Str.STR_0266_CONSTRUCT_OIL_REFINERY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_0246_FACTORY,							Str.STR_0268_CONSTRUCT_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    68,    79, Str.STR_0247_STEEL_MILL,						Str.STR_0269_CONSTRUCT_STEEL_MILL),

	};

	static final Widget _build_industry_land1_widgets[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,											Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,		Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   115, 0x0,														Str.STR_NULL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0241_POWER_STATION,				Str.STR_0263_CONSTRUCT_POWER_STATION),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_024C_PAPER_MILL,						Str.STR_026E_CONSTRUCT_PAPER_MILL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_0244_OIL_REFINERY,					Str.STR_0266_CONSTRUCT_OIL_REFINERY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_024D_FOOD_PROCESSING_PLANT,Str.STR_026F_CONSTRUCT_FOOD_PROCESSING),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    68,    79, Str.STR_024E_PRINTING_WORKS,				Str.STR_0270_CONSTRUCT_PRINTING_WORKS),

	};

	static final Widget _build_industry_land2_widgets[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,											Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,		Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   115, 0x0,														Str.STR_NULL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0250_LUMBER_MILL,					Str.STR_0273_CONSTRUCT_LUMBER_MILL_TO),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_024D_FOOD_PROCESSING_PLANT,Str.STR_026F_CONSTRUCT_FOOD_PROCESSING),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_0244_OIL_REFINERY,					Str.STR_0266_CONSTRUCT_OIL_REFINERY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_0246_FACTORY,							Str.STR_0268_CONSTRUCT_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    68,    79, Str.STR_0254_WATER_TOWER,					Str.STR_0277_CONSTRUCT_WATER_TOWER_CAN),

	};

	static final Widget _build_industry_land3_widgets[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,											Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,		Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   115, 0x0,														Str.STR_NULL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0258_CANDY_FACTORY,				Str.STR_027B_CONSTRUCT_CANDY_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_025B_TOY_SHOP,							Str.STR_027E_CONSTRUCT_TOY_SHOP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_025C_TOY_FACTORY,					Str.STR_027F_CONSTRUCT_TOY_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_025E_FIZZY_DRINK_FACTORY,	Str.STR_0281_CONSTRUCT_FIZZY_DRINK_FACTORY),

	};

	static final Widget _build_industry_land0_widgets_extra[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,							Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   187, 0x0,										Str.STR_NULL),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0241_POWER_STATION,Str.STR_0263_CONSTRUCT_POWER_STATION),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_0242_SAWMILL,			Str.STR_0264_CONSTRUCT_SAWMILL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_0244_OIL_REFINERY,	Str.STR_0266_CONSTRUCT_OIL_REFINERY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_0246_FACTORY,					Str.STR_0268_CONSTRUCT_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    68,    79, Str.STR_0247_STEEL_MILL,		Str.STR_0269_CONSTRUCT_STEEL_MILL),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    84,    95, Str.STR_0240_COAL_MINE,		Str.STR_CONSTRUCT_COAL_MINE_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    97,    108, Str.STR_0243_FOREST,			Str.STR_CONSTRUCT_FOREST_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    110,    121, Str.STR_0245_OIL_RIG,		Str.STR_CONSTRUCT_OIL_RIG_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    123,    134, Str.STR_0248_FARM,						Str.STR_CONSTRUCT_FARM_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    136,    147, Str.STR_024A_OIL_WELLS,			Str.STR_CONSTRUCT_OIL_WELLS_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    149,    160, Str.STR_0249_IRON_ORE_MINE,	Str.STR_CONSTRUCT_IRON_ORE_MINE_TIP),


	};

	static final Widget _build_industry_land1_widgets_extra[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,								Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,		Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   174, 0x0,											Str.STR_NULL),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0241_POWER_STATION,	Str.STR_0263_CONSTRUCT_POWER_STATION),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_024C_PAPER_MILL,			Str.STR_026E_CONSTRUCT_PAPER_MILL),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_0244_OIL_REFINERY,		Str.STR_0266_CONSTRUCT_OIL_REFINERY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_024D_FOOD_PROCESSING_PLANT,Str.STR_026F_CONSTRUCT_FOOD_PROCESSING),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    68,    79, Str.STR_024E_PRINTING_WORKS,	Str.STR_0270_CONSTRUCT_PRINTING_WORKS),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    81+3,    92+3, Str.STR_0240_COAL_MINE,	Str.STR_CONSTRUCT_COAL_MINE_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    94+3,   105+3, Str.STR_0243_FOREST,			Str.STR_CONSTRUCT_FOREST_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    107+3,  118+3, Str.STR_0248_FARM,				Str.STR_CONSTRUCT_FARM_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    120+3,  131+3, Str.STR_024A_OIL_WELLS,	Str.STR_CONSTRUCT_OIL_WELLS_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    133+3,  144+3, Str.STR_024F_GOLD_MINE,	Str.STR_CONSTRUCT_GOLD_MINE_TIP),

	};

	static final Widget _build_industry_land2_widgets_extra[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,							Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,			Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   200, 0x0,										Str.STR_NULL),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0250_LUMBER_MILL,	Str.STR_0273_CONSTRUCT_LUMBER_MILL_TO),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_024D_FOOD_PROCESSING_PLANT,Str.STR_026F_CONSTRUCT_FOOD_PROCESSING),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_0244_OIL_REFINERY,	Str.STR_0266_CONSTRUCT_OIL_REFINERY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_0246_FACTORY,			Str.STR_0268_CONSTRUCT_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    68,    79, Str.STR_0254_WATER_TOWER,	Str.STR_0277_CONSTRUCT_WATER_TOWER_CAN),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    81+3,    92+3, Str.STR_024A_OIL_WELLS,Str.STR_CONSTRUCT_OIL_WELLS_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    94+3,    105+3, Str.STR_0255_DIAMOND_MINE,			Str.STR_CONSTRUCT_DIAMOND_MINE_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    107+3,    118+3, Str.STR_0256_COPPER_ORE_MINE,	Str.STR_CONSTRUCT_COPPER_ORE_MINE_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    120+3,    131+3, Str.STR_0248_FARM,		Str.STR_CONSTRUCT_FARM_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    133+3,    144+3, Str.STR_0251_FRUIT_PLANTATION,	Str.STR_CONSTRUCT_FRUIT_PLANTATION_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    146+3,    157+3, Str.STR_0252_RUBBER_PLANTATION,Str.STR_CONSTRUCT_RUBBER_PLANTATION_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    159+3,    170+3, Str.STR_0253_WATER_SUPPLY,			Str.STR_CONSTRUCT_WATER_SUPPLY_TIP),

	};

	static final Widget _build_industry_land3_widgets_extra[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     7,     0,    10,     0,    13, Str.STR_00C5,								Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     7,    11,   169,     0,    13, Str.STR_0314_FUND_NEW_INDUSTRY,			Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,     7,     0,   169,    14,   187, 0x0,	Str.STR_NULL),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    16,    27, Str.STR_0258_CANDY_FACTORY,	Str.STR_027B_CONSTRUCT_CANDY_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    29,    40, Str.STR_025B_TOY_SHOP,				Str.STR_027E_CONSTRUCT_TOY_SHOP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    42,    53, Str.STR_025C_TOY_FACTORY,		Str.STR_027F_CONSTRUCT_TOY_FACTORY),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    55,    66, Str.STR_025E_FIZZY_DRINK_FACTORY,		Str.STR_0281_CONSTRUCT_FIZZY_DRINK_FACTORY),

			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    68+3,    79+3, Str.STR_0257_COTTON_CANDY_FOREST,Str.STR_CONSTRUCT_COTTON_CANDY_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    81+3,    92+3, Str.STR_0259_BATTERY_FARM,				Str.STR_CONSTRUCT_BATTERY_FARM_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    94+3,    105+3, Str.STR_025A_COLA_WELLS,				Str.STR_CONSTRUCT_COLA_WELLS_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    107+3,    118+3, Str.STR_025D_PLASTIC_FOUNTAINS,Str.STR_CONSTRUCT_PLASTIC_FOUNTAINS_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    120+3,    131+3, Str.STR_025F_BUBBLE_GENERATOR,	Str.STR_CONSTRUCT_BUBBLE_GENERATOR_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    133+3,    144+3, Str.STR_0260_TOFFEE_QUARRY,		Str.STR_CONSTRUCT_TOFFEE_QUARRY_TIP),
			new Widget(    Window.WWT_TEXTBTN,   Window.RESIZE_NONE,    14,     2,   167,    146+3,    157+3, Str.STR_0261_SUGAR_MINE,				Str.STR_CONSTRUCT_SUGAR_MINE_TIP),

	};


	static final WindowDesc _build_industry_land0_desc = new WindowDesc(
			-1, -1, 170, 116,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land0_widgets,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _build_industry_land1_desc = new WindowDesc(
			-1, -1, 170, 116,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land1_widgets,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _build_industry_land2_desc = new WindowDesc(
			-1, -1, 170, 116,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land2_widgets,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _build_industry_land3_desc = new WindowDesc(
			-1, -1, 170, 116,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land3_widgets,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _build_industry_land0_desc_extra = new WindowDesc(
			-1, -1, 170, 188,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land0_widgets_extra,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _build_industry_land1_desc_extra = new WindowDesc(
			-1, -1, 170, 175,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land1_widgets_extra,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _build_industry_land2_desc_extra = new WindowDesc(
			-1, -1, 170, 201,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land2_widgets_extra,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _build_industry_land3_desc_extra = new WindowDesc(
			-1, -1, 170, 188,
			Window.WC_BUILD_INDUSTRY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET,
			_build_industry_land3_widgets_extra,
			Industry::BuildIndustryWndProc
			);

	static final WindowDesc _industry_window_desc[][] = {
			{
				_build_industry_land0_desc,
				_build_industry_land1_desc,
				_build_industry_land2_desc,
				_build_industry_land3_desc,
			},
			{
				_build_industry_land0_desc_extra,
				_build_industry_land1_desc_extra,
				_build_industry_land2_desc_extra,
				_build_industry_land3_desc_extra,
			},
	};

	void ShowBuildIndustryWindow()
	{
		AllocateWindowDescFront(_industry_window_desc[Global._patches.build_rawmaterial_ind][GameOptions._opt_ptr.landscape],0);
	}

	//#define NEED_ALTERB	((Global._game_mode == GameModes.GM_EDITOR || _cheats.setup_prod.value) && (i.accepts_cargo[0] == AcceptedCargo.CT_INVALID || i.accepts_cargo[0] == AcceptedCargo.CT_VALUABLES))
	
	static void IndustryViewWndProc(Window w, WindowEvent e)
	{
		// w.as_vp2_d().data_1 is for the editbox line
		// w.as_vp2_d().data_2 is for the clickline
		// w.as_vp2_d().data_3 is for the click pos (left or right)

		switch(e.event) {
		case WE_PAINT: {
			final Industry i;
			StringID str;

			i = GetIndustry(w.window_number.n);
			Global.SetDParam(0, w.window_number.n);
			w.DrawWindowWidgets();

			if (i.accepts_cargo[0] != AcceptedCargo.CT_INVALID) {
				Global.SetDParam(0, _cargoc.names_s[i.accepts_cargo[0]]);
				str = Str.STR_4827_REQUIRES;
				if (i.accepts_cargo[1] != AcceptedCargo.CT_INVALID) {
					Global.SetDParam(1, _cargoc.names_s[i.accepts_cargo[1]]);
					str++;
					if (i.accepts_cargo[2] != AcceptedCargo.CT_INVALID) {
						Global.SetDParam(2, _cargoc.names_s[i.accepts_cargo[2]]);
						str++;
					}
				}
				DrawString(2, 107, str, 0);
			}

			if (i.produced_cargo[0] != AcceptedCargo.CT_INVALID) {
				DrawString(2, 117, Str.STR_482A_PRODUCTION_LAST_MONTH, 0);

				Global.SetDParam(0, _cargoc.names_long[i.produced_cargo[0]]);
				Global.SetDParam(1, i.total_production[0]);

				Global.SetDParam(2, i.pct_transported[0] * 100 >> 8);
				DrawString(4 + (NEED_ALTERB ? 30 : 0), 127, Str.STR_482B_TRANSPORTED, 0);
				// Let's put out those buttons..
				if (NEED_ALTERB)
					DrawArrowButtons(5, 127, (w.as_vp2_d().data_2 == 1 ? w.as_vp2_d().data_3 : 0));

				if (i.produced_cargo[1] != AcceptedCargo.CT_INVALID) {
					Global.SetDParam(0, _cargoc.names_long[i.produced_cargo[1]]);
					Global.SetDParam(1, i.total_production[1]);
					Global.SetDParam(2, i.pct_transported[1] * 100 >> 8);
					DrawString(4 + (NEED_ALTERB ? 30 : 0), 137, Str.STR_482B_TRANSPORTED, 0);
					// Let's put out those buttons..
					if (NEED_ALTERB)
						DrawArrowButtons(5, 137, (w.as_vp2_d().data_2 == 2 ? w.as_vp2_d().data_3 : 0));
				}
			}

			DrawWindowViewport(w);
		}
		break;

		case WindowEvents.WE_CLICK: {
			Industry i;

			switch(e.widget) {
			case 5: {
				int line;
				int x;
				byte b;

				i = GetIndustry(w.window_number);

				// We should work if needed..
				if (!NEED_ALTERB)
					return;

				x = e.pt.x;
				line = (e.pt.y - 127) / 10;
				if (e.pt.y >= 127 && BitOps.IS_INT_INSIDE(line, 0, 2) && i.produced_cargo[line]) {
					if (BitOps.IS_INT_INSIDE(x, 5, 25) ) {
						// clicked buttons
						if (x < 15) {
							// decrease
							i.production_rate[line] /= 2;
							if (i.production_rate[line] < 4)
								i.production_rate[line] = 4;
						} else {
							// increase
							b = i.production_rate[line] * 2;
							if (i.production_rate[line] >= 128)
								b=255;
							i.production_rate[line] = b;
						}
						UpdateIndustryProduction(i);
						w.SetWindowDirty();
						w.flags4 |= 5 << WF_TIMEOUT_SHL;
						w.as_vp2_d().data_2 = line+1;
						w.as_vp2_d().data_3 = (x < 15 ? 1 : 2);
					} else if (BitOps.IS_INT_INSIDE(x, 34, 160)) {
						// clicked the text
						w.as_vp2_d().data_1 = line;
						Global.SetDParam(0, i.production_rate[line] * 8);
						ShowQueryString(Str.STR_CONFIG_PATCHES_INT32,
								Str.STR_CONFIG_GAME_PRODUCTION,
								10, 100, w.window_class,
								w.window_number);
					}
				}
			}
			break;
			case 6:
				i = GetIndustry(w.window_number);
				ScrollMainWindowToTile(i.xy + TileDiffXY(1, 1));
				break;
			}
		}
		break;
		case WindowEvents.WE_TIMEOUT:
			w.as_vp2_d().data_2 = 0;
			w.as_vp2_d().data_3 = 0;
			w.SetWindowDirty();
			break;

		case WindowEvents.WE_ON_EDIT_TEXT:
			if (e.str != null) {
				Industry i;
				int val;
				int line;

				i = GetIndustry(w.window_number);
				line = w.as_vp2_d().data_1;
				val = atoi(e.edittext.str);
				if (!BitOps.IS_INT_INSIDE(val, 32, 2040)) {
					if (val < 32) val = 32;
					else val = 2040;
				}
				i.production_rate[line] = (byte)(val / 8);
				UpdateIndustryProduction(i);
				w.SetWindowDirty();
			}
		}
	}

	static void UpdateIndustryProduction(Industry i)
	{
		if (i.produced_cargo[0] != AcceptedCargo.CT_INVALID)
			i.total_production[0] = 8 * i.production_rate[0];

		if (i.produced_cargo[1] != AcceptedCargo.CT_INVALID)
			i.total_production[1] = 8 * i.production_rate[1];
	}

	static final Widget _industry_view_widgets[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,     9,     0,    10,     0,    13, Str.STR_00C5,	Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,     9,    11,   247,     0,    13, Str.STR_4801,	Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(  Window.WWT_STICKYBOX,   Window.RESIZE_NONE,     9,   248,   259,     0,    13, 0x0,       Str.STR_STICKY_BUTTON),
			new Widget(     Window.WWT_IMGBTN,   Window.RESIZE_NONE,     9,     0,   259,    14,   105, 0x0,				Str.STR_NULL),
			new Widget(	  Window.WWT_6,   Window.RESIZE_NONE,     9,     2,   257,    16,   103, 0x0,				Str.STR_NULL),
			new Widget(     Window.WWT_IMGBTN,   Window.RESIZE_NONE,     9,     0,   259,   106,   147, 0x0,				Str.STR_NULL),
			new Widget( Window.WWT_PUSHTXTBTN,   Window.RESIZE_NONE,     9,     0,   129,   148,   159, Str.STR_00E4_LOCATION,	Str.STR_482C_CENTER_THE_MAIN_VIEW_ON),
			new Widget(     Window.WWT_IMGBTN,   Window.RESIZE_NONE,     9,   130,   259,   148,   159, 0x0,				Str.STR_NULL),

	};

	static final WindowDesc _industry_view_desc = new WindowDesc(
			-1, -1, 260, 160,
			Window.WC_INDUSTRY_VIEW,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET | WindowDesc.WDF_UNCLICK_BUTTONS | WindowDesc.WDF_STICKY_BUTTON,
			_industry_view_widgets,
			Industry::IndustryViewWndProc
			);

	static void ShowIndustryViewWindow(int industry)
	{
		Window w;
		Industry i;

		w = Window.AllocateWindowDescFront(_industry_view_desc, industry);
		if (w != null) {
			w.flags4 |= Window.WF_DISABLE_VP_SCROLL;
			w.as_vp2_d().data_1 = 0;
			w.as_vp2_d().data_2 = 0;
			w.as_vp2_d().data_3 = 0;
			i = GetIndustry(w.window_number.n);
			w.AssignWindowViewport( 3, 17, 0xFE, 0x56, i.xy + TileIndex.TileDiffXY(1, 1), 1);
		}
	}

	static final Widget _industry_directory_widgets[] = {
			new Widget(   Window.WWT_CLOSEBOX,   Window.RESIZE_NONE,    13,     0,    10,     0,    13, Str.STR_00C5,									Str.STR_018B_CLOSE_WINDOW),
			new Widget(    Window.WWT_CAPTION,   Window.RESIZE_NONE,    13,    11,   495,     0,    13, Str.STR_INDUSTRYDIR_CAPTION,	Str.STR_018C_WINDOW_TITLE_DRAG_THIS),
			new Widget(  Window.WWT_STICKYBOX,   Window.RESIZE_NONE,    13,   496,   507,     0,    13, 0x0,											Str.STR_STICKY_BUTTON),
			new Widget( Window.WWT_PUSHTXTBTN,   Window.RESIZE_NONE,    13,     0,   100,    14,    25, Str.STR_SORT_BY_NAME,					Str.STR_SORT_ORDER_TIP),
			new Widget( Window.WWT_PUSHTXTBTN,   Window.RESIZE_NONE,    13,   101,   200,    14,    25, Str.STR_SORT_BY_TYPE,					Str.STR_SORT_ORDER_TIP),
			new Widget( Window.WWT_PUSHTXTBTN,   Window.RESIZE_NONE,    13,   201,   300,    14,    25, Str.STR_SORT_BY_PRODUCTION,		Str.STR_SORT_ORDER_TIP),
			new Widget( Window.WWT_PUSHTXTBTN,   Window.RESIZE_NONE,    13,   301,   400,    14,    25, Str.STR_SORT_BY_TRANSPORTED,	Str.STR_SORT_ORDER_TIP),
			new Widget(      Window.WWT_PANEL,   Window.RESIZE_NONE,    13,   401,   495,    14,    25, 0x0,											Str.STR_NULL),
			new Widget(     Window.WWT_IMGBTN, Window.RESIZE_BOTTOM,    13,     0,   495,    26,   189, 0x0,											Str.STR_200A_TOWN_NAMES_CLICK_ON_NAME),
			new Widget(  Window.WWT_SCROLLBAR, Window.RESIZE_BOTTOM,    13,   496,   507,    14,   177, 0x0,											Str.STR_0190_SCROLL_BAR_SCROLLS_LIST),
			new Widget(  Window.WWT_RESIZEBOX,     Window.RESIZE_TB,    13,   496,   507,   178,   189, 0x0,											Str.STR_RESIZE_BUTTON),

	};

	static int _num_industry_sort;

	//static char _bufcache[96];
	static int _last_industry_idx;

	static byte _industry_sort_order;

	static int GeneralIndustrySorter(final void *a, final void *b)
	{
		char buf1[96];
		int val;
		Industry i = GetIndustry(*(final int*)a);
		Industry j = GetIndustry(*(final int*)b);
		int r = 0;

		switch (_industry_sort_order >> 1) {
		/* case 0: Sort by Name (handled later) */
		case 1: /* Sort by Type */
			r = i.type - j.type;
			break;
			// FIXME - Production & Transported sort need to be inversed...but, WTF it does not wanna!
			// FIXME - And no simple -. "if (!(_industry_sort_order & 1)) r = -r;" hack at the bottom!!
		case 2: { /* Sort by Production */
			if (i.produced_cargo[0] != AcceptedCargo.CT_INVALID && j.produced_cargo[0] != AcceptedCargo.CT_INVALID) { // both industries produce cargo?
				if (i.produced_cargo[1] == AcceptedCargo.CT_INVALID) // producing one or two things?
					r = j.total_production[0] - i.total_production[0];
				else
					r = (j.total_production[0] + j.total_production[1]) / 2 - (i.total_production[0] + i.total_production[1]) / 2;
			} else if (i.produced_cargo[0] == AcceptedCargo.CT_INVALID && j.produced_cargo[0] == AcceptedCargo.CT_INVALID) // none of them producing anything, let them go to the name-sorting
				r = 0;
			else if (i.produced_cargo[0] == AcceptedCargo.CT_INVALID) // end up the non-producer industry first/last in list
				r = 1;
			else
				r = -1;
			break;
		}
		case 3: /* Sort by Transported amount */
			if (i.produced_cargo[0] != AcceptedCargo.CT_INVALID && j.produced_cargo[0] != AcceptedCargo.CT_INVALID) { // both industries produce cargo?
				if (i.produced_cargo[1] == AcceptedCargo.CT_INVALID) // producing one or two things?
					r = (j.pct_transported[0] * 100 >> 8) - (i.pct_transported[0] * 100 >> 8);
				else
					r = ((j.pct_transported[0] * 100 >> 8) + (j.pct_transported[1] * 100 >> 8)) / 2 - ((i.pct_transported[0] * 100 >> 8) + (i.pct_transported[1] * 100 >> 8)) / 2;
			} else if (i.produced_cargo[0] == AcceptedCargo.CT_INVALID && j.produced_cargo[0] == AcceptedCargo.CT_INVALID) // none of them producing anything, let them go to the name-sorting
				r = 0;
			else if (i.produced_cargo[0] == AcceptedCargo.CT_INVALID) // end up the non-producer industry first/last in list
				r = 1;
			else
				r = -1;
			break;
		}

		// default to string sorting if they are otherwise equal
		if (r == 0) {
			Global.SetDParam(0, i.town.index);
			Global.GetString(buf1, Str.STR_TOWN);

			if ( (val=*(final int*)b) != _last_industry_idx) {
				_last_industry_idx = val;
				Global.SetDParam(0, j.town.index);
				Global.GetString(_bufcache, Str.STR_TOWN);
			}
			r = strcmp(buf1, _bufcache);
		}

		if (_industry_sort_order & 1) r = -r;
		return r;
	}

	static void MakeSortedIndustryList()
	{
		Industry i;
		int n = 0;

		/* Create array for sorting */
		_industry_sort = realloc(_industry_sort, GetIndustryPoolSize() * sizeof(_industry_sort[0]));
		if (_industry_sort == null)
			error("Could not allocate memory for the industry-sorting-list");

		FOR_ALL_INDUSTRIES(i) 
		{
			if(i.xy)
				_industry_sort[n++] = i.index;
		}
		_num_industry_sort = n;
		_last_industry_idx = 0xFFFF; // used for "cache"

		qsort(_industry_sort, n, sizeof(_industry_sort[0]), GeneralIndustrySorter);

		DEBUG_misc( 1, "Resorting Industries list...");
	}


	static void IndustryDirectoryWndProc(Window w, WindowEvent e)
	{
		switch(e.event) {
		case WindowEvents.WE_PAINT: {
			int n;
			int p;
			Industry i;
			static final int _indicator_positions[4] = {88, 187, 284, 387};

			if (_industry_sort_dirty) {
				_industry_sort_dirty = false;
				MakeSortedIndustryList();
			}

			SetVScrollCount(w, _num_industry_sort);

			DrawWindowWidgets(w);
			Gfx.DoDrawString(_industry_sort_order & 1 ? DOWNARROW : UPARROW, _indicator_positions[_industry_sort_order>>1], 15, 0x10);

			p = w.vscroll.pos;
			n = 0;

			while (p < _num_industry_sort) {
				i = GetIndustry(_industry_sort[p]);
				Global.SetDParam(0, i.index);
				if (i.produced_cargo[0] != AcceptedCargo.CT_INVALID) {
					Global.SetDParam(1, _cargoc.names_long[i.produced_cargo[0]]);
					Global.SetDParam(2, i.total_production[0]);

					if (i.produced_cargo[1] != AcceptedCargo.CT_INVALID) {
						Global.SetDParam(3, _cargoc.names_long[i.produced_cargo[1]]);
						Global.SetDParam(4, i.total_production[1]);
						Global.SetDParam(5, i.pct_transported[0] * 100 >> 8);
						Global.SetDParam(6, i.pct_transported[1] * 100 >> 8);
						DrawString(4, 28+n*10, Str.STR_INDUSTRYDIR_ITEM_TWO, 0);
					} else {
						Global.SetDParam(3, i.pct_transported[0] * 100 >> 8);
						DrawString(4, 28+n*10, Str.STR_INDUSTRYDIR_ITEM, 0);
					}
				} else {
					DrawString(4, 28+n*10, Str.STR_INDUSTRYDIR_ITEM_NOPROD, 0);
				}
				p++;
				if (++n == w.vscroll.cap)
					break;
			}
		} break;

		case WindowEvents.WE_CLICK:
			switch(e.widget) {
			case 3: {
				_industry_sort_order = _industry_sort_order==0 ? 1 : 0;
				_industry_sort_dirty = true;
				w.SetWindowDirty();
			} break;

			case 4: {
				_industry_sort_order = _industry_sort_order==2 ? 3 : 2;
				_industry_sort_dirty = true;
				w.SetWindowDirty();
			} break;

			case 5: {
				_industry_sort_order = _industry_sort_order==4 ? 5 : 4;
				_industry_sort_dirty = true;
				w.SetWindowDirty();
			} break;

			case 6: {
				_industry_sort_order = _industry_sort_order==6 ? 7 : 6;
				_industry_sort_dirty = true;
				w.SetWindowDirty();
			} break;

			case 8: {
				int y = (e.pt.y - 28) / 10;
				int p;
				Industry c;

				if (!BitOps.IS_INT_INSIDE(y, 0, w.vscroll.cap))
					return;
				p = y + w.vscroll.pos;
				if (p < _num_industry_sort) {
					c = GetIndustry(_industry_sort[p]);
					ScrollMainWindowToTile(c.xy);
				}
			} break;
			}
			break;

		case WindowEvents.WE_4:
			w.SetWindowDirty();
			break;

		case WindowEvents.WE_RESIZE:
			w.vscroll.cap += e.sizing.diff.y / 10;
			break;
		}
	}


	/* Industry List */
	static final WindowDesc _industry_directory_desc = new WindowDesc(
			-1, -1, 508, 190,
			Window.WC_INDUSTRY_DIRECTORY,0,
			WindowDesc.WDF_STD_TOOLTIPS | WindowDesc.WDF_STD_BTN | WindowDesc.WDF_DEF_WIDGET | WindowDesc.WDF_UNCLICK_BUTTONS | WindowDesc.WDF_STICKY_BUTTON | WindowDesc.WDF_RESIZABLE,
			_industry_directory_widgets,
			Industry::IndustryDirectoryWndProc
			);



	void ShowIndustryDirectory()
	{
		/* Industry List */
		Window w;

		w = Window.AllocateWindowDescFront(_industry_directory_desc, 0);
		if (w) {
			w.vscroll.cap = 16;
			w.resize.height = w.height - 6 * 10; // minimum 10 items
			w.resize.step_height = 10;
			w.SetWindowDirty();
		}
	}







}


//typedef void IndustryDrawTileProc(final TileInfo ti);

@FunctionalInterface
interface IndustryDrawTileProc extends Consumer<TileInfo> {}


//typedef boolean CheckNewIndustryProc(TileIndex tile, int type);

@FunctionalInterface
interface CheckNewIndustryProc  {
	boolean check(TileIndex tile, int type);
}


