package game;

import game.util.BitOps;
import game.ai.Ai;

/** @file players.c
 * @todo Cleanup the messy DrawPlayerFace function asap
 */

public class Player 
{

	long name_2;
	int name_1;

	int president_name_1;
	long president_name_2;

	long face;

	int player_money;
	int current_loan;
	long money64; // internal 64-bit version of the money. the 32-bit field will be clamped to plus minus 2 billion

	byte player_color;
	byte player_money_fraction;
	byte avail_railtypes;
	byte block_preview;
	PlayerID index;

	int cargo_types; // which cargo types were transported the last year 

	TileIndex location_of_house;
	TileIndex last_build_coordinate;

	PlayerID share_owners[];

	byte inaugurated_year;
	byte num_valid_stat_ent;

	byte quarters_of_bankrupcy;
	byte bankrupt_asked; // which players were asked about buying it?
	int bankrupt_timeout;
	int bankrupt_value;

	boolean is_active;
	byte is_ai;
	//PlayerAI ai;
	PlayerAiNew ainew;

	long [][] yearly_expenses = new long[3][13];

	PlayerEconomyEntry cur_economy;
	PlayerEconomyEntry old_economy[];
	EngineID engine_replacement[];
	boolean engine_renew;
	boolean renew_keep_length;
	int engine_renew_months;
	long engine_renew_money;


	public Player()
	{
		clear();
	}

	private void clear() {
		is_active = false;

		share_owners = new PlayerID[4];
		old_economy = new PlayerEconomyEntry[24];
		engine_replacement = new EngineID[Global.TOTAL_NUM_ENGINES];


		inaugurated_year = num_valid_stat_ent = quarters_of_bankrupcy = bankrupt_asked =
				is_ai = player_color = player_money_fraction = avail_railtypes = block_preview = 0;

		name_1 = president_name_1 = player_money = current_loan = bankrupt_timeout =
				bankrupt_value = cargo_types = engine_renew_months = 0;

		name_2 = money64 = engine_renew_money = president_name_2 = face = 0;

		engine_renew = renew_keep_length = false;
	}


	//#define MAX_PLAYERS 8
	private static Player [] _players = new Player[Global.MAX_PLAYERS];
	// NOSAVE: can be determined from player structs
	private static byte [] _player_colors = new byte[Global.MAX_PLAYERS];



	private static byte _yearly_expenses_type; // TODO fixme, use parameter where possible
	public static void SET_EXPENSES_TYPE(int x) { _yearly_expenses_type = (byte) x; }

	public static final int EXPENSES_CONSTRUCTION = 0;
	public static final int EXPENSES_NEW_VEHICLES = 1;
	public static final int EXPENSES_TRAIN_RUN = 2;
	public static final int EXPENSES_ROADVEH_RUN = 3;
	public static final int EXPENSES_AIRCRAFT_RUN = 4;
	public static final int EXPENSES_SHIP_RUN = 5;
	public static final int EXPENSES_PROPERTY = 6;
	public static final int EXPENSES_TRAIN_INC = 7;
	public static final int EXPENSES_ROADVEH_INC = 8;
	public static final int EXPENSES_AIRCRAFT_INC = 9;
	public static final int EXPENSES_SHIP_INC = 10;
	public static final int EXPENSES_LOAN_INT = 11;
	public static final int EXPENSES_OTHER = 12;

	private static int _cur_player_tick_index;
	private static int _next_competitor_start;




	public static Player GetPlayer(PlayerID i)
	{
		assert(i.id < _players.length);
		return _players[i.id];
	}

	public static Player GetPlayer(int i)
	{
		assert(i < _players.length);
		return _players[i];
	}

	static boolean IsLocalPlayer()
	{
		return Global._local_player == Global._current_player;
	}


	//static final SpriteID cheeks_table[] = {
	static final int cheeks_table[] = {
			0x325, 0x326,
			0x390, 0x3B0,
	};

	//static final SpriteID mouth_table[3] = {
	static final int mouth_table[] = {
			0x34C, 0x34D, 0x34F
	};

	static void DrawPlayerFace(int face, int color, int x, int y)
	{
		byte flag = 0;

		if ( (int)face < 0)
			flag |= 1;
		if ((((((face >> 7) ^ face) >> 7) ^ face) & 0x8080000) == 0x8000000)
			flag |= 2;

		/* draw the gradient */
		Gfx.DrawSprite((color + 0x307) << Sprite.PALETTE_SPRITE_START | Sprite.PALETTE_MODIFIER_COLOR | Sprite.SPR_GRADIENT, x, y);

		/* draw the cheeks */
		Gfx.DrawSprite(cheeks_table[flag&3], x, y);

		/* draw the chin */
		/* FIXME: real code uses -2 in zoomlevel 1 */
		{
			int val = BitOps.GB(face, 4, 2);
			if (0 == (flag & 2)) {
				Gfx.DrawSprite(0x327 + (0 != (flag&1)?0:val), x, y);
			} else {
				Gfx.DrawSprite((0 != (flag&1)?0x3B1:0x391) + (val>>1), x, y);
			}
		}
		/* draw the eyes */
		{
			int val1 = BitOps.GB(face,  6, 4);
			int val2 = BitOps.GB(face, 20, 3);
			int high = 0x314 << Sprite.PALETTE_SPRITE_START;

			if (val2 >= 6) {
				high = 0x30F << Sprite.PALETTE_SPRITE_START;
				if (val2 != 6)
					high = 0x30D << Sprite.PALETTE_SPRITE_START;
			}

			if (0 == (flag & 2)) {
				if (0 == (flag & 1)) {
					Gfx.DrawSprite(high+((val1 * 12 >> 4) + (0x32B | Sprite.PALETTE_MODIFIER_COLOR)), x, y);
				} else {
					Gfx.DrawSprite(high+(val1 + (0x337 | Sprite.PALETTE_MODIFIER_COLOR)), x, y);
				}
			} else {
				if ( 0 == (flag & 1)) {
					Gfx.DrawSprite(high+((val1 * 11 >> 4) + (0x39A | Sprite.PALETTE_MODIFIER_COLOR)), x, y);
				} else {
					Gfx.DrawSprite(high+(val1 + (0x3B8 | Sprite.PALETTE_MODIFIER_COLOR)), x, y);
				}
			}
		}

		/* draw the mouth */
		{
			int val = BitOps.GB(face, 10, 6);
			int val2;

			if (0 == (flag&1)) {
				val2 = ((val&0xF) * 15 >> 4);

				if (val2 < 3) {
					Gfx.DrawSprite(( 0 != (flag&2) ? 0x397 : 0x367) + val2, x, y);
					/* skip the rest */
					goto skip_mouth;
				}

				val2 -= 3;
				if( 0 != (flag & 2)) {
					if (val2 > 8) val2 = 0;
					val2 += 0x3A5 - 0x35B;
				}
				Gfx.DrawSprite(val2 + 0x35B, x, y);
			} else if (0 == (flag&2)) {
				Gfx.DrawSprite(((val&0xF) * 10 >> 4) + 0x351, x, y);
			} else {
				Gfx.DrawSprite(((val&0xF) * 9 >> 4) + 0x3C8, x, y);
			}

			val >>= 3;

			if (0 == (flag&2)) {
				if (0 == (flag&1)) {
					Gfx.DrawSprite(0x349 + val, x, y);
				} else {
					Gfx.DrawSprite( mouth_table[(val*3>>3)], x, y);
				}
			} else {
				if (0 == (flag&1)) {
					Gfx.DrawSprite(0x393 + (val&3), x, y);
				} else {
					Gfx.DrawSprite(0x3B3 + (val*5>>3), x, y);
				}
			}

			skip_mouth:;
		}


		/* draw the hair */
		{
			int val = BitOps.GB(face, 16, 4);
			if( 0 != (flag & 2)) {
				if( 0 != (flag & 1)) {
					Gfx.DrawSprite(0x3D9 + (val * 5 >> 4), x, y);
				} else {
					Gfx.DrawSprite(0x3D4 + (val * 5 >> 4), x, y);
				}
			} else {
				if( 0 != (flag & 1)) {
					Gfx.DrawSprite(0x38B + (val * 5 >> 4), x, y);
				} else {
					Gfx.DrawSprite(0x382 + (val * 9 >> 4), x, y);
				}
			}
		}

		/* draw the tie */
		{
			int val = BitOps.GB(face, 20, 8);

			if (0 == (flag&1)) {
				Gfx.DrawSprite(0x36B + (BitOps.GB(val, 0, 2) * 3 >> 2), x, y);
				Gfx.DrawSprite(0x36E + (BitOps.GB(val, 2, 2) * 4 >> 2), x, y);
				Gfx.DrawSprite(0x372 + (BitOps.GB(val, 4, 4) * 6 >> 4), x, y);
			} else {
				Gfx.DrawSprite(0x378 + (BitOps.GB(val, 0, 2) * 3 >> 2), x, y);
				Gfx.DrawSprite(0x37B + (BitOps.GB(val, 2, 2) * 4 >> 2), x, y);

				val >>= 4;
				if (val < 3) Gfx.DrawSprite((0 != (flag & 2) ? 0x3D1 : 0x37F) + val, x, y);
			}
		}

		/* draw the glasses */
		{
			int val = BitOps.GB(face, 28, 3);

			if (0 != (flag & 2)) {
				if (val <= 1) Gfx.DrawSprite(0x3AE + val, x, y);
			} else {
				if (val <= 1) Gfx.DrawSprite(0x347 + val, x, y);
			}
		}
	}

	public void InvalidatePlayerWindows()
	{
		PlayerID pid = index;

		if (pid == Global._local_player) Window.InvalidateWindow(Window.WC_STATUS_BAR, 0);
		Window.InvalidateWindow(Window.WC_FINANCES, pid.id);
	}

	/** current one */
	public static boolean CheckPlayerHasMoney(int cost)
	{
		if (cost > 0) {
			PlayerID pid = Global._current_player;
			if (pid.id < Global.MAX_PLAYERS && cost > GetPlayer(pid).player_money) {
				Global.SetDParam(0, cost);
				Global._error_message = STR_0003_NOT_ENOUGH_CASH_REQUIRES;
				return false;
			}
		}
		return true;
	}

	private void SubtractMoneyFromAnyPlayer(int cost)
	{
		money64 -= cost;
		UpdatePlayerMoney32();

		yearly_expenses[0][_yearly_expenses_type] += cost;

		if ( ( 1 << _yearly_expenses_type ) & (1<<7|1<<8|1<<9|1<<10))
			cur_economy.income -= cost;
		else if (( 1 << _yearly_expenses_type ) & (1<<2|1<<3|1<<4|1<<5|1<<6|1<<11))
			cur_economy.expenses -= cost;

		InvalidatePlayerWindows();
	}

	static void SubtractMoneyFromPlayer(int cost)
	{
		PlayerID pid = Global._current_player;
		if (pid.id < Global.MAX_PLAYERS)
			GetPlayer(pid).SubtractMoneyFromAnyPlayer(cost);
	}

	public static void SubtractMoneyFromPlayerFract(PlayerID player, int cost)
	{
		Player p = GetPlayer(player);
		byte m = p.player_money_fraction;
		p.player_money_fraction = (byte) (m - (byte)cost);
		cost >>= 8;
		if (p.player_money_fraction > m)
			cost++;
		if (cost != 0)
			p.SubtractMoneyFromAnyPlayer(cost);
	}

	// the player_money field is kept as it is, but money64 contains the actual amount of money.
	void UpdatePlayerMoney32()
	{
		if (money64 < -2000000000)
			player_money = -2000000000;
		else if (money64 > 2000000000)
			player_money = 2000000000;
		else
			player_money = (int)money64;
	}

	static void GetNameOfOwner(PlayerID owner, TileIndex tile)
	{
		Global.SetDParam(2, owner.id);

		if (owner.id != Owner.OWNER_TOWN) {
			if (owner.id >= 8)
				Global.SetDParam(0, STR_0150_SOMEONE);
			else {
				Player p = GetPlayer(owner);
				Global.SetDParam(0, p.name_1);
				Global.SetDParam(1, p.name_2);
			}
		} else {
			Town t = Town.ClosestTownFromTile(tile, (int)-1);
			Global.SetDParam(0, STR_TOWN);
			Global.SetDParam(1, t.index);
		}
	}


	static boolean CheckOwnership(PlayerID owner)
	{
		assert(owner.id <= Owner.OWNER_WATER);

		if (owner == Global._current_player)
			return true;
		Global._error_message = STR_013B_OWNED_BY;
		GetNameOfOwner(owner, new TileIndex(0) );
		return false;
	}

	static boolean CheckTileOwnership(TileIndex tile)
	{
		PlayerID owner = new PlayerID( tile.GetTileOwner() );

		assert(owner.id <= Owner.OWNER_WATER);

		if (owner == Global._current_player)
			return true;
		Global._error_message = STR_013B_OWNED_BY;

		// no need to get the name of the owner unless we're the local player (saves some time)
		if (IsLocalPlayer()) GetNameOfOwner(owner, tile);
		return false;
	}

	private void GenerateCompanyName()
	{
		TileIndex tile;
		Town t;
		StringID str;
		Player pp;
		int strp;
		String buffer;

		if (name_1 != STR_SV_UNNAMED)
			return;

		tile = last_build_coordinate;
		if (tile == 0)
			return;

		t = ClosestTownFromTile(tile, (int)-1);

		if (IS_INT_INSIDE(t.townnametype, SPECSTR_TOWNNAME_START, SPECSTR_TOWNNAME_LAST+1)) {
			str = t.townnametype - SPECSTR_TOWNNAME_START + SPECSTR_PLAYERNAME_START;
			strp = t.townnameparts;

			verify_name:;
			// No player must have this name already
			FOR_ALL_PLAYERS(pp) {
				if (pp.name_1 == str && pp.name_2 == strp)
					goto bad_town_name;
			}

			buffer = Strings.GetString( str);
			if (buffer.length() >= 32 || Gfx.GetStringWidth(buffer) >= 150)
				goto bad_town_name;

			set_name:;
			p.name_1 = str;
			p.name_2 = strp;

			MarkWholeScreenDirty();

			if (!IS_HUMAN_PLAYER(p.index)) {
				Global.SetDParam(0, t.index);
				News.AddNewsItem(p.index + (4 << 4), NEWS_FLAGS(NM_CALLBACK, NF_TILE, NT_COMPANY_INFO, DNC_BANKRUPCY), last_build_coordinate, 0);
			}
			return;
		}
		bad_town_name:;

		if (president_name_1 == SPECSTR_PRESIDENT_NAME) {
			str = SPECSTR_ANDCO_NAME;
			strp = president_name_2;
			goto set_name;
		} else {
			str = SPECSTR_ANDCO_NAME;
			strp = Hal.Random();
			goto verify_name;
		}
	}

	private static void COLOR_SWAP( byte colors[], int i, int j)  
	{ 
		byte t=colors[i];colors[i]=colors[j];colors[j]=t; 
	} 

	static final byte _color_sort[] = {2, 2, 3, 2, 3, 2, 3, 2, 3, 2, 2, 2, 3, 1, 1, 1};
	static final byte _color_similar_1[] = {8, 6, (byte)255, 12,  (byte)255, 0, 1, 1, 0, 13,  11,  10, 3,   9,  15, 14};
	static final byte _color_similar_2[] = {5, 7, (byte)255, (byte)255, (byte)255, 8, 7, 6, 5, 12, (byte)255, (byte)255, 9, (byte)255, (byte)255, (byte)255};

	private static byte GeneratePlayerColor()
	{
		byte [] colors = new byte [16];
		byte pcolor, t2;
		int i,j,n;
		int r;
		//Player p;

		// Initialize array
		for(i=0; i!=16; i++)
			colors[i] = (byte) i;

		// And randomize it
		n = 100;
		do {
			r = Hal.Random();
			COLOR_SWAP( colors, BitOps.GB(r, 0, 4), BitOps.GB(r, 4, 4));
		} while (--n > 0);

		// Bubble sort it according to the values in table 1
		i = 16;
		do {
			for(j=0; j!=15; j++) {
				if (_color_sort[colors[j]] < _color_sort[colors[j+1]]) {
					COLOR_SWAP( colors, j,j+1);
				}
			}
		} while (--i > 0);

		// Move the colors that look similar to each player's color to the side
		//FOR_ALL_PLAYERS(p)
		for( Player p : _players )
			if (p.is_active) {
				pcolor = p.player_color;
				for(i=0; i!=16; i++) if (colors[i] == pcolor) {
					colors[i] = (byte) 0xFF;

					t2 = _color_similar_1[pcolor];
					if (t2 == 0xFF) break;
					for(i=0; i!=15; i++) {
						if (colors[i] == t2) {
							do COLOR_SWAP( colors, i,i+1); while (++i != 15);
							break;
						}
					}

					t2 = _color_similar_2[pcolor];
					if (t2 == 0xFF) break;
					for(i=0; i!=15; i++) {
						if (colors[i] == t2) {
							do COLOR_SWAP( colors, i,i+1); while (++i != 15);
							break;
						}
					}
					break;
				}
			}

		// Return the first available color
		i = 0;
		for(;;) {
			if (colors[i] != 0xFF)
				return colors[i];
			i++;
		}
	}

	private void GeneratePresidentName() // Player p)
	{
		Player pp;
		char buffer[100], buffer2[40];

		for(;;) {
			restart:;

			president_name_2 = Hal.Random();
			president_name_1 = SPECSTR_PRESIDENT_NAME;

			Global.SetDParam(0, president_name_2);
			GetString(buffer, president_name_1);
			if (strlen(buffer) >= 32 || GetStringWidth(buffer) >= 94)
				continue;
			boolean restart = false;
			FOR_ALL_PLAYERS(pp) {
				if (pp.is_active && p != pp) {
					Global.SetDParam(0, pp.president_name_2);
					GetString(buffer2, pp.president_name_1);
					if (strcmp(buffer2, buffer) == 0)
					{
						restart = true;
						break;
					}
				}
			}
			if( restart ) continue;
			return;
		}
	}

	private static Player AllocatePlayer()
	{
		//Player p;
		// Find a free slot
		for( Player p : _players ) {
			if (!p.is_active) {
				int i = p.index.id;
				//memset(p, 0, sizeof(Player));
				p.clear();
				p.index = new PlayerID(i);
				return p;
			}
		}
		return null;
	}


	static Player DoStartupNewPlayer(boolean is_ai)
	{
		Player p;

		p = AllocatePlayer();
		if (p == null)
			return null;

		// Make a color
		p.player_color = GeneratePlayerColor();
		_player_colors[p.index.id] = p.player_color;
		p.name_1 = STR_SV_UNNAMED;
		p.is_active = true;

		p.money64 = p.player_money = p.current_loan = 100000;

		p.is_ai = is_ai;
		p.ai.state = 5; /* AIS_WANT_NEW_ROUTE */
		p.share_owners[0] = p.share_owners[1] = p.share_owners[2] = p.share_owners[3] = OWNER_SPECTATOR;

		p.avail_railtypes = GetPlayerRailtypes(p.index);
		p.inaugurated_year = Global._cur_year;
		p.face = Hal.Random(); // TODO range?

		/* Engine renewal settings */
		p.InitialiseEngineReplacement();
		p.renew_keep_length = false;
		p.engine_renew = false;
		p.engine_renew_months = -6;
		p.engine_renew_money = 100000;

		p.GeneratePresidentName();

		Window.InvalidateWindow(Window.WC_GRAPH_LEGEND, 0);
		Window.InvalidateWindow(Window.WC_TOOLBAR_MENU, 0);
		Window.InvalidateWindow(Window.WC_CLIENT_LIST, 0);

		if (is_ai && (!Global._networking || Global._network_server) && Ai._ai.enabled)
			Ai.AI_StartNewAI(p.index);

		return p;
	}

	static void StartupPlayers()
	{
		// The AI starts like in the setting with +2 month max
		_next_competitor_start = GameOptions._opt.diff.competitor_start_time * 90 * Global.DAY_TICKS + RandomRange(60 * Global.DAY_TICKS) + 1;
	}

	private static void MaybeStartNewPlayer()
	{
		int n;
		//Player p;

		// count number of competitors
		n = 0;
		for( Player p : _players ) {
			if (p.is_active && p.is_ai != 0)
				n++;
		}

		// when there's a lot of computers in game, the probability that a new one starts is lower
		if (n < (int)GameOptions._opt.diff.max_no_competitors)
			if (n < (Global._network_server ? InteractiveRandomRange(GameOptions._opt.diff.max_no_competitors + 2) : RandomRange(GameOptions._opt.diff.max_no_competitors + 2)) )
				// Send a command to all clients to start  up a new AI. Works fine for Multiplayer and SinglePlayer 
				DoCommandP(0, 1, 0, null, CMD_PLAYER_CTRL);

		// The next AI starts like the difficulty setting said, with +2 month max
		_next_competitor_start = GameOptions._opt.diff.competitor_start_time * 90 * Global.DAY_TICKS + 1;
		_next_competitor_start += Global._network_server ? InteractiveRandomRange(60 * Global.DAY_TICKS) : RandomRange(60 * Global.DAY_TICKS);
	}

	static void InitializePlayers()
	{
		int i;
		//memset(_players, 0, sizeof(_players));

		for(i = 0; i != Global.MAX_PLAYERS; i++)
		{
			_players[i].index=new PlayerID(i);
		}
		_cur_player_tick_index = 0;
	}

	static void OnTick_Players()
	{
		Player p;

		if (Global._game_mode == GameModes.GM_EDITOR)
			return;

		p = GetPlayer(_cur_player_tick_index);
		_cur_player_tick_index = (_cur_player_tick_index + 1) % Global.MAX_PLAYERS;
		if (p.name_1 != 0) p.GenerateCompanyName();

		if (Ai.AI_AllowNewAI() && Global._game_mode != GameModes.GM_MENU && 0 == --_next_competitor_start)
			MaybeStartNewPlayer();
	}

	// index is the next parameter in _decode_parameters to set up
	static StringID GetPlayerNameString(PlayerID player, int index)
	{
		if (IS_HUMAN_PLAYER(player) && player < MAX_PLAYERS) {
			Global.SetDParam(index, player+1);
			return STR_7002_PLAYER;
		}
		return STR_EMPTY;
	}

	//extern void ShowPlayerFinances(int player);

	static void PlayersYearlyLoop()
	{
		//Player p;

		// Copy statistics
		for( Player p : _players ) {
			if (p.is_active) {
				memmove(p.yearly_expenses[1], p.yearly_expenses[0], sizeof(p.yearly_expenses) - sizeof(p.yearly_expenses[0]));
				memset(p.yearly_expenses[0], 0, sizeof(p.yearly_expenses[0]));
				Window.InvalidateWindow(Window.WC_FINANCES, p.index.id);
			}
		}

		if (Global._patches.show_finances && Global._local_player.id != Owner.OWNER_SPECTATOR) {
			ShowPlayerFinances(Global._local_player);
			Player p = GetPlayer(Global._local_player);

			/* TODO sound
			if (p.num_valid_stat_ent > 5 && p.old_economy[0].performance_history < p.old_economy[4].performance_history) {
				SndPlayFx(SND_01_BAD_YEAR);
			} else {
				SndPlayFx(SND_00_GOOD_YEAR);
			}*/
		}
	}

	static void DeletePlayerWindows(PlayerID pi)
	{
		Window.DeleteWindowById(Window.WC_COMPANY, pi.id);
		Window.DeleteWindowById(Window.WC_FINANCES, pi.id);
		Window.DeleteWindowById(Window.WC_STATION_LIST, pi.id);
		Window.DeleteWindowById(Window.WC_TRAINS_LIST,   (INVALID_STATION << 16) | pi.id);
		Window.DeleteWindowById(Window.WC_ROADVEH_LIST,  (INVALID_STATION << 16) | pi.id);
		Window.DeleteWindowById(Window.WC_SHIPS_LIST,    (INVALID_STATION << 16) | pi.id);
		Window.DeleteWindowById(Window.WC_AIRCRAFT_LIST, (INVALID_STATION << 16) | pi.id);
		Window.DeleteWindowById(Window.WC_BUY_COMPANY, pi.id);
	}

	static byte GetPlayerRailtypes(PlayerID p)
	{
		byte rt = 0;
		//EngineID i;
		int i;

		for (i = 0; i != TOTAL_NUM_ENGINES; i++) {
			final Engine e = Engine.GetEngine(i);

			if (e.type == VEH_Train &&
					(BitOps.HASBIT(e.player_avail, p.id) || e.intro_date <= _date) &&
					!(RailVehInfo(i).flags & RVI_WAGON)) {
				assert(e.railtype < RAILTYPE_END);
				rt = BitOps.RETSETBIT(rt, e.railtype);
			}
		}

		return rt;
	}

	private static void DeletePlayerStuff(PlayerID pi)
	{
		Player p;

		DeletePlayerWindows(pi);
		p = GetPlayer(pi);
		Global.DeleteName(p.name_1);
		Global.DeleteName(p.president_name_1);
		p.name_1 = 0;
		p.president_name_1 = 0;
	}

	/** Change engine renewal parameters
	 * @param x,y unused
	 * @param p1 bits 0-3 command
	 * - p1 = 0 - change auto renew boolean
	 * - p1 = 1 - change auto renew months
	 * - p1 = 2 - change auto renew money
	 * - p1 = 3 - change auto renew array
	 * - p1 = 4 - change boolean, months & money all together
	 * - p1 = 5 - change renew_keep_length
	 * @param p2 value to set
	 * if p1 = 0, then:
	 * - p2 = enable engine renewal
	 * if p1 = 1, then:
	 * - p2 = months left before engine expires to replace it
	 * if p1 = 2, then
	 * - p2 = minimum amount of money available
	 * if p1 = 3, then:
	 * - p2 bits  0-15 = old engine type
	 * - p2 bits 16-31 = new engine type
	 * if p1 = 4, then:
	 * - p1 bit     15 = enable engine renewal
	 * - p1 bits 16-31 = months left before engine expires to replace it
	 * - p2 bits  0-31 = minimum amount of money available
	 * if p1 = 5, then
	 * - p2 = enable renew_keep_length
	 */
	static int CmdReplaceVehicle(int x, int y, int flags, int p1, int p2)
	{
		Player p;
		if (!(Global._current_player.id < Global.MAX_PLAYERS))
			return CMD_ERROR;

		p = GetPlayer(_current_player);
		switch (BitOps.GB(p1, 0, 3)) {
		case 0:
			if (p.engine_renew == (boolean)BitOps.GB(p2, 0, 1))
				return CMD_ERROR;

			if (flags & DC_EXEC) {
				p.engine_renew = (boolean)BitOps.GB(p2, 0, 1);
				if (IsLocalPlayer()) {
					Global._patches.autorenew = p.engine_renew;
					InvalidateWindow(WC_GAME_OPTIONS, 0);
				}
			}
			break;
		case 1:
			if (p.engine_renew_months == (int16)p2)
				return CMD_ERROR;

			if (flags & DC_EXEC) {
				p.engine_renew_months = (int16)p2;
				if (IsLocalPlayer()) {
					Global._patches.autorenew_months = p.engine_renew_months;
					InvalidateWindow(WC_GAME_OPTIONS, 0);
				}
			}
			break;
		case 2:
			if (p.engine_renew_money == (int)p2)
				return CMD_ERROR;

			if (flags & DC_EXEC) {
				p.engine_renew_money = (int)p2;
				if (IsLocalPlayer()) {
					Global._patches.autorenew_money = p.engine_renew_money;
					InvalidateWindow(WC_GAME_OPTIONS, 0);
				}
			}
			break;
		case 3: {
			EngineID old_engine_type = BitOps.GB(p2, 0, 16);
			EngineID new_engine_type = BitOps.GB(p2, 16, 16);

			if (new_engine_type != INVALID_ENGINE) {
				/* First we make sure that it's a valid type the user requested
				 * check that it's an engine that is in the engine array */
				if(!IsEngineIndex(new_engine_type))
					return CMD_ERROR;

				// check that the new vehicle type is the same as the original one
				if (GetEngine(old_engine_type).type != GetEngine(new_engine_type).type)
					return CMD_ERROR;

				// make sure that we do not replace a plane with a helicopter or vise versa
				if (GetEngine(new_engine_type).type == VEH_Aircraft && HASBIT(AircraftVehInfo(old_engine_type).subtype, 0) != HASBIT(AircraftVehInfo(new_engine_type).subtype, 0))
					return CMD_ERROR;

				// make sure that the player can actually buy the new engine
				if (!HASBIT(GetEngine(new_engine_type).player_avail, _current_player))
					return CMD_ERROR;

				return AddEngineReplacement(p, old_engine_type, new_engine_type, flags);
			} else {
				return RemoveEngineReplacement(p, old_engine_type, flags);
			}
		}

		case 4:
			if (flags & DC_EXEC) {
				p.engine_renew = (boolean)BitOps.GB(p1, 15, 1);
				p.engine_renew_months = (int16)BitOps.GB(p1, 16, 16);
				p.engine_renew_money = (int)p2;

				if (IsLocalPlayer()) {
					Global._patches.autorenew = p.engine_renew;
					Global._patches.autorenew_months = p.engine_renew_months;
					Global._patches.autorenew_money = p.engine_renew_money;
					InvalidateWindow(WC_GAME_OPTIONS, 0);
				}
			}
			break;
		case 5:
			if (p.renew_keep_length == (boolean)BitOps.GB(p2, 0, 1))
				return CMD_ERROR;

			if (flags & DC_EXEC) {
				p.renew_keep_length = (boolean)BitOps.GB(p2, 0, 1);
				if (IsLocalPlayer()) {
					InvalidateWindow(WC_REPLACE_VEHICLE, VEH_Train);
				}
			}
			break;

		}
		return 0;
	}

	/** Control the players: add, delete, etc.
	 * @param x,y unused
	 * @param p1 various functionality
	 * - p1 = 0 - create a new player, Which player (network) it will be is in p2
	 * - p1 = 1 - create a new AI player
	 * - p1 = 2 - delete a player. Player is identified by p2
	 * - p1 = 3 - merge two companies together. Player to merge #1 with player #2. Identified by p2
	 * @param p2 various functionality, dictated by p1
	 * - p1 = 0 - ClientID of the newly created player
	 * - p1 = 2 - PlayerID of the that is getting deleted
	 * - p1 = 3 - #1 p2 = (bit  0-15) - player to merge (p2 & 0xFFFF)
	 *          - #2 p2 = (bit 16-31) - player to be merged into ((p2>>16)&0xFFFF)
	 * @todo In the case of p1=0, create new player, the clientID of the new player is in parameter
	 * p2. This parameter is passed in at function DEF_SERVER_RECEIVE_COMMAND(PACKET_CLIENT_COMMAND)
	 * on the server itself. First of all this is unbelievably ugly; second of all, well,
	 * it IS ugly! <b>Someone fix this up :)</b> So where to fix?@n
	 * @arg - network_server.c:838 DEF_SERVER_RECEIVE_COMMAND(PACKET_CLIENT_COMMAND)@n
	 * @arg - network_client.c:536 DEF_CLIENT_RECEIVE_COMMAND(PACKET_SERVER_MAP) from where the map has been received
	 */
	static int CmdPlayerCtrl(int x, int y, int flags, int p1, int p2)
	{
		if (flags & DC_EXEC) _current_player = Owner.OWNER_NONE;

		switch (p1) {
		case 0: { // Create a new Player 
			Player p;
			PlayerID pid = p2;

			if (!(flags & DC_EXEC) || pid >= MAX_PLAYERS) return 0;

			p = DoStartupNewPlayer(false);

			/* TODO #ifdef ENABLE_NETWORK
			if (_networking && !_network_server && _local_player == OWNER_SPECTATOR)
				// In case we are a client joining a server... 
				DeleteWindowById(WC_NETWORK_STATUS_WINDOW, 0);
			#endif /* ENABLE_NETWORK */

			if (p != null) {
				if (_local_player == Owner.OWNER_SPECTATOR && (!_ai.network_client || _ai.network_playas == Owner.OWNER_SPECTATOR)) {
					/* Check if we do not want to be a spectator in network */
					if (!_networking || (_network_server && !_network_dedicated) || _network_playas != Owner.OWNER_SPECTATOR || _ai.network_client) {
						if (_ai.network_client) {
							/* As ai-network-client, we have our own rulez (disable GUI and stuff) */
							_ai.network_playas = p.index;
							_local_player      = OWNER_SPECTATOR;
							if (_ai.network_playas != OWNER_SPECTATOR) {
								/* If we didn't join the game as a spectator, activate the AI */
								AI_StartNewAI(_ai.network_playas);
							}
						} else {
							_local_player = p.index;
						}
						MarkWholeScreenDirty();
					}
				} else if (p.index == _local_player) {
					DoCommandP(0, (Global._patches.autorenew << 15 ) | (Global._patches.autorenew_months << 16) | 4, Global._patches.autorenew_money, null, CMD_REPLACE_VEHICLE);
				}
				/* #ifdef ENABLE_NETWORK
				if (_network_server) {
					// * XXX - UGLY! p2 (pid) is mis-used to fetch the client-id, done at server-side
					//  * in network_server.c:838, function DEF_SERVER_RECEIVE_COMMAND(PACKET_CLIENT_COMMAND) 
					NetworkClientInfo *ci = &_network_client_info[pid];
					ci.client_playas = p.index + 1;
					NetworkUpdateClientInfo(ci.client_index);

					if (ci.client_playas != 0 && ci.client_playas <= MAX_PLAYERS) {
						PlayerID player_backup = _local_player;
						_network_player_info[p.index].months_empty = 0;

						// XXX - When a client joins, we automatically set its name to the
						// * player's name (for some reason). As it stands now only the server
						// * knows the client's name, so it needs to send out a "broadcast" to
						// * do this. To achieve this we send a network command. However, it
						// * uses _local_player to execute the command as.  To prevent abuse
						// * (eg. only yourself can change your name/company), we 'cheat' by
						// * impersonation _local_player as the server. Not the best solution;
						// * but it works.
						// * TODO: Perhaps this could be improved by when the client is ready
						// * with joining to let it send itself the command, and not the server?
						// * For example in network_client.c:534? 
						_cmd_text = ci.client_name;
						_local_player = ci.client_playas - 1;
						NetworkSend_Command(0, 0, 0, CMD_CHANGE_PRESIDENT_NAME, null);
						_local_player = player_backup;
					}
				}
			} else if (_network_server) {
				// * XXX - UGLY! p2 (pid) is mis-used to fetch the client-id, done at server-side
				// * in network_server.c:838, function DEF_SERVER_RECEIVE_COMMAND(PACKET_CLIENT_COMMAND) 
				NetworkClientInfo *ci = &_network_client_info[pid];
				ci.client_playas = OWNER_SPECTATOR;
				NetworkUpdateClientInfo(ci.client_index);
			}
			#else */
			}
			//#endif /* ENABLE_NETWORK */
		} break;

		case 1: /* Make a new AI Player /
		if (!(flags & DC_EXEC)) return 0;

		DoStartupNewPlayer(true);
		break;

	case 2: { /* Delete a Player /
		Player p;

		if (p2 >= MAX_PLAYERS) return CMD_ERROR;

		if (!(flags & DC_EXEC)) return 0;

		p = GetPlayer(p2);

		/* Only allow removal of HUMAN companies */
			if (IS_HUMAN_PLAYER(p.index)) {
				/* Delete any open window of the company */
				DeletePlayerWindows(p.index);

				/* Show the bankrupt news */
				Global.SetDParam(0, p.name_1);
				Global.SetDParam(1, p.name_2);
				AddNewsItem( (StringID)(p.index + 16*3), NEWS_FLAGS(NM_CALLBACK, 0, NT_COMPANY_INFO, DNC_BANKRUPCY),0,0);

				/* Remove the company */
				ChangeOwnershipOfPlayerItems(p.index, OWNER_SPECTATOR);
				p.money64 = p.player_money = 100000000; // XXX - wtf?
				p.is_active = false;
			}
		} break;

	case 3: { /* Merge a company (#1) into another company (#2), elimination company #1 */
		PlayerID pid_old = BitOps.GB(p2,  0, 16);
		PlayerID pid_new = BitOps.GB(p2, 16, 16);

		if (pid_old >= MAX_PLAYERS || pid_new >= MAX_PLAYERS) return CMD_ERROR;

		if (!(flags & DC_EXEC)) return CMD_ERROR;

		ChangeOwnershipOfPlayerItems(pid_old, pid_new);
		DeletePlayerStuff(pid_old);
	} break;
	default: return CMD_ERROR;
	}

	return 0;
}

//static final StringID _endgame_perf_titles[] = {
static final int _endgame_perf_titles[] = {
		STR_0213_BUSINESSMAN,
		STR_0213_BUSINESSMAN,
		STR_0213_BUSINESSMAN,
		STR_0213_BUSINESSMAN,
		STR_0213_BUSINESSMAN,
		STR_0214_ENTREPRENEUR,
		STR_0214_ENTREPRENEUR,
		STR_0215_INDUSTRIALIST,
		STR_0215_INDUSTRIALIST,
		STR_0216_CAPITALIST,
		STR_0216_CAPITALIST,
		STR_0217_MAGNATE,
		STR_0217_MAGNATE,
		STR_0218_MOGUL,
		STR_0218_MOGUL,
		STR_0219_TYCOON_OF_THE_CENTURY,
};

//static StringID EndGameGetPerformanceTitleFromValue(int value)
static int EndGameGetPerformanceTitleFromValue(int value)
{

	long lvalue = BitOps.minu(value, 1000) >>> 6;
	if (lvalue >= _endgame_perf_titles.length) 
		lvalue = _endgame_perf_titles.length - 1;

	return _endgame_perf_titles[(int) lvalue];
}


// Save the highscore for the Player 
static byte SaveHighScoreValue(final Player p)
{
	/*
	HighScore hs = _highscore_table[GameOptions._opt.diff_level];
	int i;
	uint16 score = p.old_economy[0].performance_history;

	// Exclude cheaters from the honour of being in the highscore table 
	if (Cheat.CheatHasBeenUsed())
		return -1;

	for (i = 0; i < lengthof(_highscore_table[0]); i++) {
		// You are in the TOP5. Move all values one down and save us there 
		if (hs[i].score <= score) {
			char buf[sizeof(hs[i].company)];

			// move all elements one down starting from the replaced one
			memmove(&hs[i + 1], &hs[i], sizeof(HighScore) * (lengthof(_highscore_table[0]) - i - 1));
			Global.SetDParam(0, p.president_name_1);
			Global.SetDParam(1, p.president_name_2);
			Global.SetDParam(2, p.name_1);
			Global.SetDParam(3, p.name_2);
			GetString(buf, STR_HIGHSCORE_NAME); // get manager/company name string
			ttd_strlcpy(hs[i].company, buf, sizeof(buf));
			hs[i].score = score;
			hs[i].title = EndGameGetPerformanceTitleFromValue(score);
			return i;
		}
	}
	*/
	return -1; // too bad; we did not make it into the top5
}

/* Sort all players given their performance */
static int  HighScoreSorter(final void *a, final void *b)
{
	final Player pa = *(final Player* final*)a;
	final Player pb = *(final Player* final*)b;

	return pb.old_economy[0].performance_history - pa.old_economy[0].performance_history;
}

/* Save the highscores in a network game when it has ended */
//#define LAST_HS_ITEM lengthof(_highscore_table) - 1
static byte SaveHighScoreValueNetwork()
{
	Player player_sort[MAX_PLAYERS];
	size_t count = 0;
	byte player = -1;

	/* Sort all active players with the highest score first */
	for( Player p : _players ) {
		if (p.is_active)
			player_sort[count++] = p;
	}
	qsort(player_sort, count, sizeof(player_sort[0]), HighScoreSorter);

	{
		HighScore hs;
		Player* final *p_cur = &player_sort[0];
		ubyte i;

		memset(_highscore_table[LAST_HS_ITEM], 0, sizeof(_highscore_table[0]));

		/* Copy over Top5 companies */
		for (i = 0; i < lengthof(_highscore_table[LAST_HS_ITEM]) && i < count; i++) {
			char buf[sizeof(_highscore_table[0].company)];

			hs = &_highscore_table[LAST_HS_ITEM][i];
			Global.SetDParam(0, (*p_cur).president_name_1);
			Global.SetDParam(1, (*p_cur).president_name_2);
			Global.SetDParam(2, (*p_cur).name_1);
			Global.SetDParam(3, (*p_cur).name_2);
			GetString(buf, STR_HIGHSCORE_NAME); // get manager/company name string

			ttd_strlcpy(hs.company, buf, sizeof(buf));
			hs.score = (*p_cur).old_economy[0].performance_history;
			hs.title = EndGameGetPerformanceTitleFromValue(hs.score);

			// get the ranking of the local player
			if ((*p_cur).index == _local_player)
				player = i;

			p_cur++;
		}
	}

	/* Add top5 players to highscore table */
	return player;
}

/* Save HighScore table to file */
static void SaveToHighScore()
{
	FILE *fp = fopen(_highscore_file, "wb");

	if (fp != null) {
		int i;
		HighScore hs;

		for (i = 0; i < LAST_HS_ITEM; i++) { // don't save network highscores
			for (hs = _highscore_table[i]; hs != endof(_highscore_table[i]); hs++) {
				/* First character is a command character, so strlen will fail on that */
				byte length = min(sizeof(hs.company), (hs.company[0] == '\0') ? 0 : strlen(&hs.company[1]) + 1);

				fwrite(&length, sizeof(length), 1, fp); // write away string length
				fwrite(hs.company, length, 1, fp);
				fwrite(&hs.score, sizeof(hs.score), 1, fp);
				fwrite("", 2, 1, fp); /* XXX - placeholder for hs.title, not saved anymore; compatibility */
			}
		}
		fclose(fp);
	}
}

/* Initialize the highscore table to 0 and if any file exists, load in values */
static void LoadFromHighScore()
{
	FILE *fp = fopen(_highscore_file, "rb");

	memset(_highscore_table, 0, sizeof(_highscore_table));

	if (fp != null) {
		int i;
		HighScore hs;

		for (i = 0; i < LAST_HS_ITEM; i++) { // don't load network highscores
			for (hs = _highscore_table[i]; hs != endof(_highscore_table[i]); hs++) {
				byte length;
				fread(&length, sizeof(length), 1, fp);

				fread(hs.company, 1, length, fp);
				fread(&hs.score, sizeof(hs.score), 1, fp);
				fseek(fp, 2, SEEK_CUR); /* XXX - placeholder for hs.title, not saved anymore; compatibility */
				hs.title = EndGameGetPerformanceTitleFromValue(hs.score);
			}
		}
		fclose(fp);
	}

	// Initialize end of game variable (when to show highscore chart) 
	Global._patches.ending_date = 2051;
}

public void InitialiseEngineReplacement()
{
	EngineID engine;

	for (engine = 0; engine < TOTAL_NUM_ENGINES; engine++)
		engine_replacement[engine] = INVALID_ENGINE;
}

/**
 * Retrieve the engine replacement for the given player and original engine type.
 * @param p Player.
 * @param engine Engine type.
 * @return Assigned replacement engine.
 */
public EngineID EngineReplacement(EngineID engine)
{
	return engine_replacement[engine];
}

/**
 * Check if an engine has a replacement set up.
 * @param p Player.
 * @param engine Engine type.
 * @return True if there is a replacement for the original engine type.
 */
public boolean EngineHasReplacement(EngineID engine)
{
	return EngineReplacement(engine) != INVALID_ENGINE;
}

/**
 * Add an engine replacement for the player.
 * @param p Player.
 * @param old_engine The original engine type.
 * @param new_engine The replacement engine type.
 * @param flags The calling command flags.
 * @return 0 on success, CMD_ERROR on failure.
 */
public int AddEngineReplacement(EngineID old_engine, EngineID new_engine, int flags)
{
	if (flags & DC_EXEC) engine_replacement[old_engine] = new_engine;
	return 0;
}

/**
 * Remove an engine replacement for the player.
 * @param p Player.
 * @param engine The original engine type.
 * @param flags The calling command flags.
 * @return 0 on success, CMD_ERROR on failure.
 */
public int RemoveEngineReplacement(EngineID engine, int flags)
{
	if (flags & DC_EXEC) engine_replacement[engine] = INVALID_ENGINE;
	return 0;
}
/*
// Save/load of players
static final SaveLoad _player_desc[] = {
		SLE_VAR(Player,name_2,					SLE_UINT32),
		SLE_VAR(Player,name_1,					SLE_STRINGID),

		SLE_VAR(Player,president_name_1,SLE_UINT16),
		SLE_VAR(Player,president_name_2,SLE_UINT32),

		SLE_VAR(Player,face,						SLE_UINT32),

		// money was changed to a 64 bit field in savegame version 1.
		SLE_CONDVAR(Player,money64,			SLE_VAR_I64 | SLE_FILE_I32, 0, 0),
		SLE_CONDVAR(Player,money64,			SLE_INT64, 1, 255),

		SLE_VAR(Player,current_loan,		SLE_INT32),

		SLE_VAR(Player,player_color,		SLE_Ubyte),
		SLE_VAR(Player,player_money_fraction,SLE_Ubyte),
		SLE_VAR(Player,avail_railtypes,		SLE_Ubyte),
		SLE_VAR(Player,block_preview,		SLE_Ubyte),

		SLE_VAR(Player,cargo_types,			SLE_UINT16),
		SLE_CONDVAR(Player, location_of_house,     SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(Player, location_of_house,     SLE_UINT32, 6, 255),
		SLE_CONDVAR(Player, last_build_coordinate, SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(Player, last_build_coordinate, SLE_UINT32, 6, 255),
		SLE_VAR(Player,inaugurated_year,SLE_Ubyte),

		SLE_ARR(Player,share_owners,		SLE_Ubyte, 4),

		SLE_VAR(Player,num_valid_stat_ent,SLE_Ubyte),

		SLE_VAR(Player,quarters_of_bankrupcy,SLE_Ubyte),
		SLE_VAR(Player,bankrupt_asked,	SLE_Ubyte),
		SLE_VAR(Player,bankrupt_timeout,SLE_INT16),
		SLE_VAR(Player,bankrupt_value,	SLE_INT32),

		// yearly expenses was changed to 64-bit in savegame version 2.
		SLE_CONDARR(Player,yearly_expenses,	SLE_FILE_I32|SLE_VAR_I64, 3*13, 0, 1),
		SLE_CONDARR(Player,yearly_expenses,	SLE_INT64, 3*13, 2, 255),

		SLE_CONDVAR(Player,is_ai,			SLE_Ubyte, 2, 255),
		SLE_CONDVAR(Player,is_active,	SLE_Ubyte, 4, 255),

		// Engine renewal settings
		SLE_CONDARR(Player,engine_replacement,  SLE_UINT16, 256, 16, 255),
		SLE_CONDVAR(Player,engine_renew,         SLE_Ubyte,      16, 255),
		SLE_CONDVAR(Player,engine_renew_months,  SLE_INT16,      16, 255),
		SLE_CONDVAR(Player,engine_renew_money,  SLE_UINT32,      16, 255),
		SLE_CONDVAR(Player,renew_keep_length,    SLE_Ubyte,       2, 255),	// added with 16.1, but was blank since 2

		// reserve extra space in savegame here. (currently 63 bytes)
		SLE_CONDARR(NullStruct,null,SLE_FILE_U8  | SLE_VAR_NULL, 7, 2, 255),
		SLE_CONDARR(NullStruct,null,SLE_FILE_U64 | SLE_VAR_NULL, 7, 2, 255),

		SLE_END()
};

static final SaveLoad _player_economy_desc[] = {
		// these were changed to 64-bit in savegame format 2
		SLE_CONDVAR(PlayerEconomyEntry,income,							SLE_INT32, 0, 1),
		SLE_CONDVAR(PlayerEconomyEntry,expenses,						SLE_INT32, 0, 1),
		SLE_CONDVAR(PlayerEconomyEntry,company_value, SLE_FILE_I32 | SLE_VAR_I64, 0, 1),
		SLE_CONDVAR(PlayerEconomyEntry,income,	SLE_FILE_I64 | SLE_VAR_I32, 2, 255),
		SLE_CONDVAR(PlayerEconomyEntry,expenses,SLE_FILE_I64 | SLE_VAR_I32, 2, 255),
		SLE_CONDVAR(PlayerEconomyEntry,company_value, SLE_INT64, 2, 255),

		SLE_VAR(PlayerEconomyEntry,delivered_cargo,			SLE_INT32),
		SLE_VAR(PlayerEconomyEntry,performance_history,	SLE_INT32),

		SLE_END()
};

static final SaveLoad _player_ai_desc[] = {
		SLE_VAR(PlayerAI,state,							SLE_Ubyte),
		SLE_VAR(PlayerAI,tick,							SLE_Ubyte),
		SLE_CONDVAR(PlayerAI,state_counter, SLE_FILE_U16 | SLE_VAR_U32, 0, 12),
		SLE_CONDVAR(PlayerAI,state_counter, SLE_UINT32, 13, 255),
		SLE_VAR(PlayerAI,timeout_counter,		SLE_UINT16),

		SLE_VAR(PlayerAI,state_mode,				SLE_Ubyte),
		SLE_VAR(PlayerAI,banned_tile_count,	SLE_Ubyte),
		SLE_VAR(PlayerAI,railtype_to_use,		SLE_Ubyte),

		SLE_VAR(PlayerAI,cargo_type,				SLE_Ubyte),
		SLE_VAR(PlayerAI,num_wagons,				SLE_Ubyte),
		SLE_VAR(PlayerAI,build_kind,				SLE_Ubyte),
		SLE_VAR(PlayerAI,num_build_rec,			SLE_Ubyte),
		SLE_VAR(PlayerAI,num_loco_to_build,	SLE_Ubyte),
		SLE_VAR(PlayerAI,num_want_fullload,	SLE_Ubyte),

		SLE_VAR(PlayerAI,route_type_mask,		SLE_Ubyte),

		SLE_CONDVAR(PlayerAI, start_tile_a, SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(PlayerAI, start_tile_a, SLE_UINT32, 6, 255),
		SLE_CONDVAR(PlayerAI, cur_tile_a,   SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(PlayerAI, cur_tile_a,   SLE_UINT32, 6, 255),
		SLE_VAR(PlayerAI,start_dir_a,				SLE_Ubyte),
		SLE_VAR(PlayerAI,cur_dir_a,					SLE_Ubyte),

		SLE_CONDVAR(PlayerAI, start_tile_b, SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(PlayerAI, start_tile_b, SLE_UINT32, 6, 255),
		SLE_CONDVAR(PlayerAI, cur_tile_b,   SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(PlayerAI, cur_tile_b,   SLE_UINT32, 6, 255),
		SLE_VAR(PlayerAI,start_dir_b,				SLE_Ubyte),
		SLE_VAR(PlayerAI,cur_dir_b,					SLE_Ubyte),

		SLE_REF(PlayerAI,cur_veh,						REF_VEHICLE),

		SLE_ARR(PlayerAI,wagon_list,				SLE_UINT16, 9),
		SLE_ARR(PlayerAI,order_list_blocks,	SLE_Ubyte, 20),
		SLE_ARR(PlayerAI,banned_tiles,			SLE_UINT16, 16),

		SLE_CONDARR(NullStruct,null,SLE_FILE_U64 | SLE_VAR_NULL, 8, 2, 255),
		SLE_END()
};

static final SaveLoad _player_ai_build_rec_desc[] = {
		SLE_CONDVAR(AiBuildRec,spec_tile, SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(AiBuildRec,spec_tile, SLE_UINT32, 6, 255),
		SLE_CONDVAR(AiBuildRec,use_tile,  SLE_FILE_U16 | SLE_VAR_U32, 0, 5),
		SLE_CONDVAR(AiBuildRec,use_tile,  SLE_UINT32, 6, 255),
		SLE_VAR(AiBuildRec,rand_rng,			SLE_Ubyte),
		SLE_VAR(AiBuildRec,cur_building_rule,SLE_Ubyte),
		SLE_VAR(AiBuildRec,unk6,					SLE_Ubyte),
		SLE_VAR(AiBuildRec,unk7,					SLE_Ubyte),
		SLE_VAR(AiBuildRec,buildcmd_a,		SLE_Ubyte),
		SLE_VAR(AiBuildRec,buildcmd_b,		SLE_Ubyte),
		SLE_VAR(AiBuildRec,direction,			SLE_Ubyte),
		SLE_VAR(AiBuildRec,cargo,					SLE_Ubyte),
		SLE_END()
};
 */

/*
static void SaveLoad_PLYR(Player p) {
	int i;

	SlObject(p, _player_desc);

	// Write AI?
	if (!IS_HUMAN_PLAYER(p.index)) {
		SlObject(&p.ai, _player_ai_desc);
		for(i=0; i!=p.ai.num_build_rec; i++)
			SlObject(&p.ai.src + i, _player_ai_build_rec_desc);
	}

	// Write economy
	SlObject(&p.cur_economy, _player_economy_desc);

	// Write old economy entries.
	{
		PlayerEconomyEntry *pe;
		for(i=p.num_valid_stat_ent,pe=p.old_economy; i!=0; i--,pe++)
			SlObject(pe, _player_economy_desc);
	}
}

static void Save_PLYR()
{
	//Player p;
	for( Player p : _players ) {
		if (p.is_active) {
			SlSetArrayIndex(p.index);
			SlAutolength((AutolengthProc*)SaveLoad_PLYR, p);
		}
	}
}

static void Load_PLYR()
{
	int index;
	while ((index = SlIterateArray()) != -1) {
		Player p = GetPlayer(index);
		SaveLoad_PLYR(p);
		_player_colors[index] = p.player_color;
		UpdatePlayerMoney32(p);

		// This is needed so an AI is attached to a loaded AI 
		if (p.is_ai && (!_networking || _network_server) && _ai.enabled)
			AI_StartNewAI(p.index);
	}
}

final ChunkHandler _player_chunk_handlers[] = {
		{ 'PLYR', Save_PLYR, Load_PLYR, CH_ARRAY | CH_LAST},
};
*/
}
