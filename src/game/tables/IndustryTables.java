package game.tables;

import game.AcceptedCargo;
import game.Str;
import game.TileIndexDiffC;

public class IndustryTables 
{

	//enum {
		public static final int IT_COAL_MINE = 0;
		public static final int IT_POWER_STATION = 1;
		public static final int IT_SAWMILL = 2;
		public static final int IT_FOREST = 3;
		public static final int IT_OIL_REFINERY = 4;
		public static final int IT_OIL_RIG = 5;
		public static final int IT_FACTORY = 6;
		public static final int IT_PRINTING_WORKS = 7;
		public static final int IT_STEEL_MILL = 8;
		public static final int IT_FARM = 9;
		public static final int IT_COPPER_MINE = 10;
		public static final int IT_OIL_WELL = 11;
		public static final int IT_BANK = 12;
		public static final int IT_FOOD_PROCESS = 13;
		public static final int IT_PAPER_MILL = 14;
		public static final int IT_GOLD_MINE = 15;
		public static final int IT_BANK_2 = 16;
		public static final int IT_DIAMOND_MINE = 17;
		public static final int IT_IRON_MINE = 18;
		public static final int IT_FRUIT_PLANTATION = 19;
		public static final int IT_RUBBER_PLANTATION = 20;
		public static final int IT_WATER_SUPPLY = 21;
		public static final int IT_WATER_TOWER = 22;
		public static final int IT_FACTORY_2 = 23;
		public static final int IT_FARM_2 = 24;
		public static final int IT_LUMBER_MILL = 25;
		public static final int IT_COTTON_CANDY = 26;
		public static final int IT_CANDY_FACTORY = 27;
		public static final int IT_BATTERY_FARM = 28;
		public static final int IT_COLA_WELLS = 29;
		public static final int IT_TOY_SHOP = 30;
		public static final int IT_TOY_FACTORY = 31;
		public static final int IT_PLASTIC_FOUNTAINS = 32;
		public static final int IT_FIZZY_DRINK_FACTORY = 33;
		public static final int IT_BUBBLE_GENERATOR = 34;
		public static final int IT_TOFFEE_QUARRY = 35;
		public static final int IT_SUGAR_MINE = 36;
	
	
	public static final /* StringID */ int _industry_prod_up_strings[] = {
			Str.STR_4836_NEW_COAL_SEAM_FOUND_AT,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4837_NEW_OIL_RESERVES_FOUND,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4838_IMPROVED_FARMING_METHODS,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4837_NEW_OIL_RESERVES_FOUND,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4838_IMPROVED_FARMING_METHODS,
			Str.STR_4838_IMPROVED_FARMING_METHODS,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4838_IMPROVED_FARMING_METHODS,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4838_IMPROVED_FARMING_METHODS,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4838_IMPROVED_FARMING_METHODS,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
			Str.STR_4835_INCREASES_PRODUCTION,
		};

	public static final /* StringID */ int  _industry_prod_down_strings[] = {
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_483A_INSECT_INFESTATION_CAUSES,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_483A_INSECT_INFESTATION_CAUSES,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_483A_INSECT_INFESTATION_CAUSES,
			Str.STR_483A_INSECT_INFESTATION_CAUSES,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_483A_INSECT_INFESTATION_CAUSES,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_483A_INSECT_INFESTATION_CAUSES,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
			Str.STR_4839_PRODUCTION_DOWN_BY_50,
		};
	

	static final /* StringID */ int _industry_close_strings[] = {
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4834_LACK_OF_NEARBY_TREES_CAUSES,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4833_SUPPLY_PROBLEMS_CAUSE_TO,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE,
			Str.STR_4832_ANNOUNCES_IMMINENT_CLOSURE
		};
	

	
	
	
	
	
	
	//enum IndustryType {
	public static final int INDUSTRY_NOT_CLOSABLE = 0;     //! Industry can never close
	public static final int INDUSTRY_PRODUCTION = 1;       //! Industry can close and change of production
	public static final int INDUSTRY_CLOSABLE = 2;         //! Industry can only close (no production change)
	//}


	static final /*IndustryType*/ int _industry_close_mode[] = {
		/* COAL_MINE */         INDUSTRY_PRODUCTION,
		/* POWER_STATION */     INDUSTRY_NOT_CLOSABLE,
		/* SAWMILL */           INDUSTRY_CLOSABLE,
		/* FOREST */            INDUSTRY_PRODUCTION,
		/* OIL_REFINERY */      INDUSTRY_CLOSABLE,
		/* OIL_RIG */           INDUSTRY_PRODUCTION,
		/* FACTORY */           INDUSTRY_CLOSABLE,
		/* PRINTING_WORKS */    INDUSTRY_CLOSABLE,
		/* STEEL_MILL */        INDUSTRY_CLOSABLE,
		/* FARM */              INDUSTRY_PRODUCTION,
		/* COPPER_MINE */       INDUSTRY_PRODUCTION,
		/* OIL_WELL */          INDUSTRY_PRODUCTION,
		/* BANK */              INDUSTRY_NOT_CLOSABLE,
		/* FOOD_PROCESS */      INDUSTRY_CLOSABLE,
		/* PAPER_MILL */        INDUSTRY_CLOSABLE,
		/* GOLD_MINE */         INDUSTRY_PRODUCTION,
		/* BANK_2,  */          INDUSTRY_NOT_CLOSABLE,
		/* DIAMOND_MINE */      INDUSTRY_PRODUCTION,
		/* IRON_MINE */         INDUSTRY_PRODUCTION,
		/* FRUIT_PLANTATION */  INDUSTRY_PRODUCTION,
		/* RUBBER_PLANTATION */ INDUSTRY_PRODUCTION,
		/* WATER_SUPPLY */      INDUSTRY_PRODUCTION,
		/* WATER_TOWER */       INDUSTRY_NOT_CLOSABLE,
		/* FACTORY_2 */         INDUSTRY_CLOSABLE,
		/* FARM_2 */            INDUSTRY_PRODUCTION,
		/* LUMBER_MILL */       INDUSTRY_CLOSABLE,
		/* COTTON_CANDY */      INDUSTRY_PRODUCTION,
		/* CANDY_FACTORY */     INDUSTRY_CLOSABLE,
		/* BATTERY_FARM */      INDUSTRY_PRODUCTION,
		/* COLA_WELLS */        INDUSTRY_PRODUCTION,
		/* TOY_SHOP */          INDUSTRY_NOT_CLOSABLE,
		/* TOY_FACTORY */       INDUSTRY_CLOSABLE,
		/* PLASTIC_FOUNTAINS */ INDUSTRY_PRODUCTION,
		/* FIZZY_DRINK_FACTORY */INDUSTRY_CLOSABLE,
		/* BUBBLE_GENERATOR */  INDUSTRY_PRODUCTION,
		/* TOFFEE_QUARRY */     INDUSTRY_PRODUCTION,
		/* SUGAR_MINE */        INDUSTRY_PRODUCTION
	};


	
	
	
	
	
	
	
	
	
	

	
	
	
	/* $Id: build_industry.h 3135 2005-11-04 16:12:48Z tron $ */

	//private static  MK(x,y, m) {{x, y}, m}

	//#define MKEND {{-0x80, 0}, 0}

	static final IndustryTileTable _tile_table_coal_mine_0[] = {
		new IndustryTileTable(1,1, 0),
		new IndustryTileTable(1,2, 2),
		new IndustryTileTable(0,0, 5),
		new IndustryTileTable(1,0, 6),
		new IndustryTileTable(2,0, 3),
		new IndustryTileTable(2,2, 3),
		//MKEND
	};

	static final IndustryTileTable _tile_table_coal_mine_1[] = {
		new IndustryTileTable(1,1, 0),
		new IndustryTileTable(1,2, 2),
		new IndustryTileTable(2,0, 0),
		new IndustryTileTable(2,1, 2),
		new IndustryTileTable(1,0, 3),
		new IndustryTileTable(0,0, 3),
		new IndustryTileTable(0,1, 4),
		new IndustryTileTable(0,2, 4),
		new IndustryTileTable(2,2, 4),
		//MKEND
	};

	static final IndustryTileTable _tile_table_coal_mine_2[] = {
		new IndustryTileTable(0,0, 0),
		new IndustryTileTable(0,1, 2),
		new IndustryTileTable(0,2, 5),
		new IndustryTileTable(1,0, 3),
		new IndustryTileTable(1,1, 3),
		new IndustryTileTable(1,2, 6),
		//MKEND
	};

	static final IndustryTileTable _tile_table_coal_mine_3[] = {
		new IndustryTileTable(0,1, 0),
		new IndustryTileTable(0,2, 2),
		new IndustryTileTable(0,3, 4),
		new IndustryTileTable(1,0, 5),
		new IndustryTileTable(1,1, 0),
		new IndustryTileTable(1,2, 2),
		new IndustryTileTable(1,3, 3),
		new IndustryTileTable(2,0, 6),
		new IndustryTileTable(2,1, 4),
		new IndustryTileTable(2,2, 3),
		//MKEND
	};

	static final IndustryTileTable [][] _tile_table_coal_mine = {
		_tile_table_coal_mine_0,
		_tile_table_coal_mine_1,
		_tile_table_coal_mine_2,
		_tile_table_coal_mine_3,
	};

	static final IndustryTileTable _tile_table_power_station_0[] = {
		new IndustryTileTable(0,0, 7),
		new IndustryTileTable(0,1, 9),
		new IndustryTileTable(1,0, 7),
		new IndustryTileTable(1,1, 8),
		new IndustryTileTable(2,0, 7),
		new IndustryTileTable(2,1, 8),
		new IndustryTileTable(3,0, 10),
		new IndustryTileTable(3,1, 10),
		// MKEND
	};

	static final IndustryTileTable _tile_table_power_station_1[] = {
		new IndustryTileTable(0,1, 7),
		new IndustryTileTable(0,2, 7),
		new IndustryTileTable(1,0, 8),
		new IndustryTileTable(1,1, 8),
		new IndustryTileTable(1,2, 7),
		new IndustryTileTable(2,0, 9),
		new IndustryTileTable(2,1, 10),
		new IndustryTileTable(2,2, 9),
		// MKEND
	};

	static final IndustryTileTable _tile_table_power_station_2[] = {
		new IndustryTileTable(0,0, 7),
		new IndustryTileTable(0,1, 7),
		new IndustryTileTable(1,0, 9),
		new IndustryTileTable(1,1, 8),
		new IndustryTileTable(2,0, 10),
		new IndustryTileTable(2,1, 9),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_power_station[] = {
		_tile_table_power_station_0,
		_tile_table_power_station_1,
		_tile_table_power_station_2,
	};

	static final IndustryTileTable _tile_table_sawmill_0[] = {
		new IndustryTileTable(1,0, 14),
		new IndustryTileTable(1,1, 12),
		new IndustryTileTable(1,2, 11),
		new IndustryTileTable(2,0, 14),
		new IndustryTileTable(2,1, 13),
		new IndustryTileTable(0,0, 15),
		new IndustryTileTable(0,1, 15),
		new IndustryTileTable(0,2, 12),
		// MKEND
	};

	static final IndustryTileTable _tile_table_sawmill_1[] = {
		new IndustryTileTable(0,0, 15),
		new IndustryTileTable(0,1, 11),
		new IndustryTileTable(0,2, 14),
		new IndustryTileTable(1,0, 15),
		new IndustryTileTable(1,1, 13),
		new IndustryTileTable(1,2, 12),
		new IndustryTileTable(2,0, 11),
		new IndustryTileTable(2,1, 13),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_sawmill[] = {
		_tile_table_sawmill_0,
		_tile_table_sawmill_1,
	};

	static final IndustryTileTable _tile_table_forest_0[] = {
		new IndustryTileTable(0,0, 16),
		new IndustryTileTable(0,1, 16),
		new IndustryTileTable(0,2, 16),
		new IndustryTileTable(0,3, 16),
		new IndustryTileTable(1,0, 16),
		new IndustryTileTable(1,1, 16),
		new IndustryTileTable(1,2, 16),
		new IndustryTileTable(1,3, 16),
		new IndustryTileTable(2,0, 16),
		new IndustryTileTable(2,1, 16),
		new IndustryTileTable(2,2, 16),
		new IndustryTileTable(2,3, 16),
		new IndustryTileTable(3,0, 16),
		new IndustryTileTable(3,1, 16),
		new IndustryTileTable(3,2, 16),
		new IndustryTileTable(3,3, 16),
		new IndustryTileTable(1,4, 16),
		new IndustryTileTable(2,4, 16),
		// MKEND
	};

	static final IndustryTileTable _tile_table_forest_1[] = {
		new IndustryTileTable(0,0, 16),
		new IndustryTileTable(1,0, 16),
		new IndustryTileTable(2,0, 16),
		new IndustryTileTable(3,0, 16),
		new IndustryTileTable(4,0, 16),
		new IndustryTileTable(0,1, 16),
		new IndustryTileTable(1,1, 16),
		new IndustryTileTable(2,1, 16),
		new IndustryTileTable(3,1, 16),
		new IndustryTileTable(4,1, 16),
		new IndustryTileTable(0,2, 16),
		new IndustryTileTable(1,2, 16),
		new IndustryTileTable(2,2, 16),
		new IndustryTileTable(3,2, 16),
		new IndustryTileTable(4,2, 16),
		new IndustryTileTable(0,3, 16),
		new IndustryTileTable(1,3, 16),
		new IndustryTileTable(2,3, 16),
		new IndustryTileTable(3,3, 16),
		new IndustryTileTable(4,3, 16),
		new IndustryTileTable(1,4, 16),
		new IndustryTileTable(2,4, 16),
		new IndustryTileTable(3,4, 16),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_forest[] = {
		_tile_table_forest_0,
		_tile_table_forest_1,
	};

	static final IndustryTileTable _tile_table_oil_refinery_0[] = {
		new IndustryTileTable(0,0, 20),
		new IndustryTileTable(0,1, 21),
		new IndustryTileTable(0,2, 22),
		new IndustryTileTable(0,3, 21),
		new IndustryTileTable(1,0, 20),
		new IndustryTileTable(1,1, 19),
		new IndustryTileTable(1,2, 22),
		new IndustryTileTable(1,3, 20),
		new IndustryTileTable(2,1, 18),
		new IndustryTileTable(2,2, 18),
		new IndustryTileTable(2,3, 18),
		new IndustryTileTable(3,2, 18),
		new IndustryTileTable(3,3, 18),
		new IndustryTileTable(2,0, 23),
		new IndustryTileTable(3,1, 23),
		// MKEND
	};

	static final IndustryTileTable _tile_table_oil_refinery_1[] = {
		new IndustryTileTable(0,0, 18),
		new IndustryTileTable(0,1, 18),
		new IndustryTileTable(0,2, 21),
		new IndustryTileTable(0,3, 22),
		new IndustryTileTable(0,4, 20),
		new IndustryTileTable(1,0, 18),
		new IndustryTileTable(1,1, 18),
		new IndustryTileTable(1,2, 19),
		new IndustryTileTable(1,3, 20),
		new IndustryTileTable(2,0, 18),
		new IndustryTileTable(2,1, 18),
		new IndustryTileTable(2,2, 19),
		new IndustryTileTable(2,3, 22),
		new IndustryTileTable(1,4, 23),
		new IndustryTileTable(2,4, 23),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_oil_refinery[] = {
		_tile_table_oil_refinery_0,
		_tile_table_oil_refinery_1,
	};

	static final IndustryTileTable _tile_table_oil_rig_0[] = {
		new IndustryTileTable(0,0, 24),
		new IndustryTileTable(0,1, 24),
		new IndustryTileTable(0,2, 25),
		new IndustryTileTable(1,0, 26),
		new IndustryTileTable(1,1, 27),
		new IndustryTileTable(1,2, 28),
		new IndustryTileTable(-4,-5, 255),
		new IndustryTileTable(-4,-4, 255),
		new IndustryTileTable(-4,-3, 255),
		new IndustryTileTable(-4,-2, 255),
		new IndustryTileTable(-4,-1, 255),
		new IndustryTileTable(-4,0, 255),
		new IndustryTileTable(-4,1, 255),
		new IndustryTileTable(-4,2, 255),
		new IndustryTileTable(-4,3, 255),
		new IndustryTileTable(-4,4, 255),
		new IndustryTileTable(-4,5, 255),
		new IndustryTileTable(-3,5, 255),
		new IndustryTileTable(-2,5, 255),
		new IndustryTileTable(-1,5, 255),
		new IndustryTileTable(0,6, 255),
		new IndustryTileTable(1,6, 255),
		new IndustryTileTable(2,6, 255),
		new IndustryTileTable(3,6, 255),
		new IndustryTileTable(4,6, 255),
		new IndustryTileTable(5,6, 255),
		new IndustryTileTable(5,5, 255),
		new IndustryTileTable(5,4, 255),
		new IndustryTileTable(5,3, 255),
		new IndustryTileTable(5,2, 255),
		new IndustryTileTable(5,1, 255),
		new IndustryTileTable(5,0, 255),
		new IndustryTileTable(5,-1, 255),
		new IndustryTileTable(5,-2, 255),
		new IndustryTileTable(5,-3, 255),
		new IndustryTileTable(5,-4, 255),
		new IndustryTileTable(4,-4, 255),
		new IndustryTileTable(3,-4, 255),
		new IndustryTileTable(2,-4, 255),
		new IndustryTileTable(1,-4, 255),
		new IndustryTileTable(0,-4, 255),
		new IndustryTileTable(-1,-5, 255),
		new IndustryTileTable(-2,-5, 255),
		new IndustryTileTable(-3,-5, 255),
		new IndustryTileTable(2,0, 255),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_oil_rig[] = {
		_tile_table_oil_rig_0,
	};

	static final IndustryTileTable _tile_table_factory_0[] = {
		new IndustryTileTable(0,0, 39),
		new IndustryTileTable(0,1, 40),
		new IndustryTileTable(1,0, 41),
		new IndustryTileTable(1,1, 42),
		new IndustryTileTable(0,2, 39),
		new IndustryTileTable(0,3, 40),
		new IndustryTileTable(1,2, 41),
		new IndustryTileTable(1,3, 42),
		new IndustryTileTable(2,1, 39),
		new IndustryTileTable(2,2, 40),
		new IndustryTileTable(3,1, 41),
		new IndustryTileTable(3,2, 42),
		// MKEND
	};

	static final IndustryTileTable _tile_table_factory_1[] = {
		new IndustryTileTable(0,0, 39),
		new IndustryTileTable(0,1, 40),
		new IndustryTileTable(1,0, 41),
		new IndustryTileTable(1,1, 42),
		new IndustryTileTable(2,0, 39),
		new IndustryTileTable(2,1, 40),
		new IndustryTileTable(3,0, 41),
		new IndustryTileTable(3,1, 42),
		new IndustryTileTable(1,2, 39),
		new IndustryTileTable(1,3, 40),
		new IndustryTileTable(2,2, 41),
		new IndustryTileTable(2,3, 42),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_factory[] = {
		_tile_table_factory_0,
		_tile_table_factory_1,
	};

	static final IndustryTileTable _tile_table_printing_works_0[] = {
		new IndustryTileTable(0,0, 43),
		new IndustryTileTable(0,1, 44),
		new IndustryTileTable(1,0, 45),
		new IndustryTileTable(1,1, 46),
		new IndustryTileTable(0,2, 43),
		new IndustryTileTable(0,3, 44),
		new IndustryTileTable(1,2, 45),
		new IndustryTileTable(1,3, 46),
		new IndustryTileTable(2,1, 43),
		new IndustryTileTable(2,2, 44),
		new IndustryTileTable(3,1, 45),
		new IndustryTileTable(3,2, 46),
		// MKEND
	};

	static final IndustryTileTable _tile_table_printing_works_1[] = {
		new IndustryTileTable(0,0, 43),
		new IndustryTileTable(0,1, 44),
		new IndustryTileTable(1,0, 45),
		new IndustryTileTable(1,1, 46),
		new IndustryTileTable(2,0, 43),
		new IndustryTileTable(2,1, 44),
		new IndustryTileTable(3,0, 45),
		new IndustryTileTable(3,1, 46),
		new IndustryTileTable(1,2, 43),
		new IndustryTileTable(1,3, 44),
		new IndustryTileTable(2,2, 45),
		new IndustryTileTable(2,3, 46),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_printing_works[] = {
		_tile_table_printing_works_0,
		_tile_table_printing_works_1,
	};

	static final IndustryTileTable _tile_table_steel_mill_0[] = {
		new IndustryTileTable(2,1, 52),
		new IndustryTileTable(2,2, 53),
		new IndustryTileTable(3,1, 54),
		new IndustryTileTable(3,2, 55),
		new IndustryTileTable(0,0, 56),
		new IndustryTileTable(1,0, 57),
		new IndustryTileTable(0,1, 56),
		new IndustryTileTable(1,1, 57),
		new IndustryTileTable(0,2, 56),
		new IndustryTileTable(1,2, 57),
		new IndustryTileTable(2,0, 56),
		new IndustryTileTable(3,0, 57),
		// MKEND
	};

	static final IndustryTileTable _tile_table_steel_mill_1[] = {
		new IndustryTileTable(0,0, 52),
		new IndustryTileTable(0,1, 53),
		new IndustryTileTable(1,0, 54),
		new IndustryTileTable(1,1, 55),
		new IndustryTileTable(2,0, 52),
		new IndustryTileTable(2,1, 53),
		new IndustryTileTable(3,0, 54),
		new IndustryTileTable(3,1, 55),
		new IndustryTileTable(0,2, 56),
		new IndustryTileTable(1,2, 57),
		new IndustryTileTable(2,2, 56),
		new IndustryTileTable(3,2, 57),
		new IndustryTileTable(1,3, 56),
		new IndustryTileTable(2,3, 57),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_steel_mill[] = {
		_tile_table_steel_mill_0,
		_tile_table_steel_mill_1,
	};

	static final IndustryTileTable _tile_table_farm_0[] = {
		new IndustryTileTable(1,0, 33),
		new IndustryTileTable(1,1, 34),
		new IndustryTileTable(1,2, 36),
		new IndustryTileTable(0,0, 37),
		new IndustryTileTable(0,1, 37),
		new IndustryTileTable(0,2, 36),
		new IndustryTileTable(2,0, 35),
		new IndustryTileTable(2,1, 38),
		new IndustryTileTable(2,2, 38),
		// MKEND
	};

	static final IndustryTileTable _tile_table_farm_1[] = {
		new IndustryTileTable(1,1, 33),
		new IndustryTileTable(1,2, 34),
		new IndustryTileTable(0,0, 35),
		new IndustryTileTable(0,1, 36),
		new IndustryTileTable(0,2, 36),
		new IndustryTileTable(0,3, 35),
		new IndustryTileTable(1,0, 37),
		new IndustryTileTable(1,3, 38),
		new IndustryTileTable(2,0, 37),
		new IndustryTileTable(2,1, 37),
		new IndustryTileTable(2,2, 38),
		new IndustryTileTable(2,3, 38),
		// MKEND
	};

	static final IndustryTileTable _tile_table_farm_2[] = {
		new IndustryTileTable(2,0, 33),
		new IndustryTileTable(2,1, 34),
		new IndustryTileTable(0,0, 36),
		new IndustryTileTable(0,1, 36),
		new IndustryTileTable(0,2, 37),
		new IndustryTileTable(0,3, 37),
		new IndustryTileTable(1,0, 35),
		new IndustryTileTable(1,1, 38),
		new IndustryTileTable(1,2, 38),
		new IndustryTileTable(1,3, 37),
		new IndustryTileTable(2,2, 37),
		new IndustryTileTable(2,3, 35),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_farm[] = {
		_tile_table_farm_0,
		_tile_table_farm_1,
		_tile_table_farm_2,
	};

	static final IndustryTileTable _tile_table_copper_mine_0[] = {
		new IndustryTileTable(0,0, 47),
		new IndustryTileTable(0,1, 49),
		new IndustryTileTable(0,2, 51),
		new IndustryTileTable(1,0, 47),
		new IndustryTileTable(1,1, 49),
		new IndustryTileTable(1,2, 50),
		new IndustryTileTable(2,0, 51),
		new IndustryTileTable(2,1, 51),
		// MKEND
	};

	static final IndustryTileTable _tile_table_copper_mine_1[] = {
		new IndustryTileTable(0,0, 50),
		new IndustryTileTable(0,1, 47),
		new IndustryTileTable(0,2, 49),
		new IndustryTileTable(1,0, 47),
		new IndustryTileTable(1,1, 49),
		new IndustryTileTable(1,2, 51),
		new IndustryTileTable(2,0, 51),
		new IndustryTileTable(2,1, 47),
		new IndustryTileTable(2,2, 49),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_copper_mine[] = {
		_tile_table_copper_mine_0,
		_tile_table_copper_mine_1,
	};

	static final IndustryTileTable _tile_table_oil_well_0[] = {
		new IndustryTileTable(0,0, 29),
		new IndustryTileTable(1,0, 29),
		new IndustryTileTable(2,0, 29),
		new IndustryTileTable(0,1, 29),
		new IndustryTileTable(0,2, 29),
		// MKEND
	};

	static final IndustryTileTable _tile_table_oil_well_1[] = {
		new IndustryTileTable(0,0, 29),
		new IndustryTileTable(1,0, 29),
		new IndustryTileTable(1,1, 29),
		new IndustryTileTable(2,2, 29),
		new IndustryTileTable(2,3, 29),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_oil_well[] = {
		_tile_table_oil_well_0,
		_tile_table_oil_well_1,
	};

	static final IndustryTileTable _tile_table_bank_0[] = {
		new IndustryTileTable(0,0, 58),
		new IndustryTileTable(1,0, 59),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_bank[] = {
		_tile_table_bank_0,
	};

	static final IndustryTileTable _tile_table_food_process_0[] = {
		new IndustryTileTable(0,0, 60),
		new IndustryTileTable(1,0, 60),
		new IndustryTileTable(2,0, 60),
		new IndustryTileTable(0,1, 60),
		new IndustryTileTable(1,1, 60),
		new IndustryTileTable(2,1, 60),
		new IndustryTileTable(0,2, 61),
		new IndustryTileTable(1,2, 61),
		new IndustryTileTable(2,2, 63),
		new IndustryTileTable(0,3, 62),
		new IndustryTileTable(1,3, 62),
		new IndustryTileTable(2,3, 63),
		// MKEND
	};

	static final IndustryTileTable _tile_table_food_process_1[] = {
		new IndustryTileTable(0,0, 61),
		new IndustryTileTable(1,0, 60),
		new IndustryTileTable(2,0, 61),
		new IndustryTileTable(3,0, 61),
		new IndustryTileTable(0,1, 62),
		new IndustryTileTable(1,1, 63),
		new IndustryTileTable(2,1, 63),
		new IndustryTileTable(3,1, 63),
		new IndustryTileTable(0,2, 60),
		new IndustryTileTable(1,2, 60),
		new IndustryTileTable(2,2, 60),
		new IndustryTileTable(3,2, 60),
		new IndustryTileTable(0,3, 62),
		new IndustryTileTable(1,3, 62),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_food_process[] = {
		_tile_table_food_process_0,
		_tile_table_food_process_1,
	};

	static final IndustryTileTable _tile_table_paper_mill_0[] = {
		new IndustryTileTable(0,0, 64),
		new IndustryTileTable(1,0, 65),
		new IndustryTileTable(2,0, 66),
		new IndustryTileTable(3,0, 67),
		new IndustryTileTable(0,1, 68),
		new IndustryTileTable(1,1, 69),
		new IndustryTileTable(2,1, 67),
		new IndustryTileTable(3,1, 67),
		new IndustryTileTable(0,2, 66),
		new IndustryTileTable(1,2, 71),
		new IndustryTileTable(2,2, 71),
		new IndustryTileTable(3,2, 70),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_paper_mill[] = {
		_tile_table_paper_mill_0,
	};

	static final IndustryTileTable _tile_table_gold_mine_0[] = {
		new IndustryTileTable(0,0, 72),
		new IndustryTileTable(0,1, 73),
		new IndustryTileTable(0,2, 74),
		new IndustryTileTable(0,3, 75),
		new IndustryTileTable(1,0, 76),
		new IndustryTileTable(1,1, 77),
		new IndustryTileTable(1,2, 78),
		new IndustryTileTable(1,3, 79),
		new IndustryTileTable(2,0, 80),
		new IndustryTileTable(2,1, 81),
		new IndustryTileTable(2,2, 82),
		new IndustryTileTable(2,3, 83),
		new IndustryTileTable(3,0, 84),
		new IndustryTileTable(3,1, 85),
		new IndustryTileTable(3,2, 86),
		new IndustryTileTable(3,3, 87),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_gold_mine[] = {
		_tile_table_gold_mine_0,
	};

	static final IndustryTileTable _tile_table_bank2_0[] = {
		new IndustryTileTable(0,0, 89),
		new IndustryTileTable(1,0, 90),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_bank2[] = {
		_tile_table_bank2_0,
	};

	static final IndustryTileTable _tile_table_diamond_mine_0[] = {
		new IndustryTileTable(0,0, 91),
		new IndustryTileTable(0,1, 92),
		new IndustryTileTable(0,2, 93),
		new IndustryTileTable(1,0, 94),
		new IndustryTileTable(1,1, 95),
		new IndustryTileTable(1,2, 96),
		new IndustryTileTable(2,0, 97),
		new IndustryTileTable(2,1, 98),
		new IndustryTileTable(2,2, 99),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_diamond_mine[] = {
		_tile_table_diamond_mine_0,
	};

	static final IndustryTileTable _tile_table_iron_mine_0[] = {
		new IndustryTileTable(0,0, 100),
		new IndustryTileTable(0,1, 101),
		new IndustryTileTable(0,2, 102),
		new IndustryTileTable(0,3, 103),
		new IndustryTileTable(1,0, 104),
		new IndustryTileTable(1,1, 105),
		new IndustryTileTable(1,2, 106),
		new IndustryTileTable(1,3, 107),
		new IndustryTileTable(2,0, 108),
		new IndustryTileTable(2,1, 109),
		new IndustryTileTable(2,2, 110),
		new IndustryTileTable(2,3, 111),
		new IndustryTileTable(3,0, 112),
		new IndustryTileTable(3,1, 113),
		new IndustryTileTable(3,2, 114),
		new IndustryTileTable(3,3, 115),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_iron_mine[] = {
		_tile_table_iron_mine_0,
	};

	static final IndustryTileTable _tile_table_fruit_plantation_0[] = {
		new IndustryTileTable(0,0, 116),
		new IndustryTileTable(0,1, 116),
		new IndustryTileTable(0,2, 116),
		new IndustryTileTable(0,3, 116),
		new IndustryTileTable(1,0, 116),
		new IndustryTileTable(1,1, 116),
		new IndustryTileTable(1,2, 116),
		new IndustryTileTable(1,3, 116),
		new IndustryTileTable(2,0, 116),
		new IndustryTileTable(2,1, 116),
		new IndustryTileTable(2,2, 116),
		new IndustryTileTable(2,3, 116),
		new IndustryTileTable(3,0, 116),
		new IndustryTileTable(3,1, 116),
		new IndustryTileTable(3,2, 116),
		new IndustryTileTable(3,3, 116),
		new IndustryTileTable(4,0, 116),
		new IndustryTileTable(4,1, 116),
		new IndustryTileTable(4,2, 116),
		new IndustryTileTable(4,3, 116),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_fruit_plantation[] = {
		_tile_table_fruit_plantation_0,
	};

	static final IndustryTileTable _tile_table_rubber_plantation_0[] = {
		new IndustryTileTable(0,0, 117),
		new IndustryTileTable(0,1, 117),
		new IndustryTileTable(0,2, 117),
		new IndustryTileTable(0,3, 117),
		new IndustryTileTable(1,0, 117),
		new IndustryTileTable(1,1, 117),
		new IndustryTileTable(1,2, 117),
		new IndustryTileTable(1,3, 117),
		new IndustryTileTable(2,0, 117),
		new IndustryTileTable(2,1, 117),
		new IndustryTileTable(2,2, 117),
		new IndustryTileTable(2,3, 117),
		new IndustryTileTable(3,0, 117),
		new IndustryTileTable(3,1, 117),
		new IndustryTileTable(3,2, 117),
		new IndustryTileTable(3,3, 117),
		new IndustryTileTable(4,0, 117),
		new IndustryTileTable(4,1, 117),
		new IndustryTileTable(4,2, 117),
		new IndustryTileTable(4,3, 117),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_rubber_plantation[] = {
		_tile_table_rubber_plantation_0,
	};

	static final IndustryTileTable _tile_table_water_supply_0[] = {
		new IndustryTileTable(0,0, 118),
		new IndustryTileTable(0,1, 119),
		new IndustryTileTable(1,0, 118),
		new IndustryTileTable(1,1, 119),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_water_supply[] = {
		_tile_table_water_supply_0,
	};

	static final IndustryTileTable _tile_table_water_tower_0[] = {
		new IndustryTileTable(0,0, 120),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_water_tower[] = {
		_tile_table_water_tower_0,
	};

	static final IndustryTileTable _tile_table_factory2_0[] = {
		new IndustryTileTable(0,0, 121),
		new IndustryTileTable(0,1, 122),
		new IndustryTileTable(1,0, 123),
		new IndustryTileTable(1,1, 124),
		new IndustryTileTable(0,2, 121),
		new IndustryTileTable(0,3, 122),
		new IndustryTileTable(1,2, 123),
		new IndustryTileTable(1,3, 124),
		// MKEND
	};

	static final IndustryTileTable _tile_table_factory2_1[] = {
		new IndustryTileTable(0,0, 121),
		new IndustryTileTable(0,1, 122),
		new IndustryTileTable(1,0, 123),
		new IndustryTileTable(1,1, 124),
		new IndustryTileTable(2,0, 121),
		new IndustryTileTable(2,1, 122),
		new IndustryTileTable(3,0, 123),
		new IndustryTileTable(3,1, 124),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_factory2[] = {
		_tile_table_factory2_0,
		_tile_table_factory2_1,
	};

	static final IndustryTileTable _tile_table_farm2_0[] = {
		new IndustryTileTable(1,0, 33),
		new IndustryTileTable(1,1, 34),
		new IndustryTileTable(1,2, 36),
		new IndustryTileTable(0,0, 37),
		new IndustryTileTable(0,1, 37),
		new IndustryTileTable(0,2, 36),
		new IndustryTileTable(2,0, 35),
		new IndustryTileTable(2,1, 38),
		new IndustryTileTable(2,2, 38),
		// MKEND
	};

	static final IndustryTileTable _tile_table_farm2_1[] = {
		new IndustryTileTable(1,1, 33),
		new IndustryTileTable(1,2, 34),
		new IndustryTileTable(0,0, 35),
		new IndustryTileTable(0,1, 36),
		new IndustryTileTable(0,2, 36),
		new IndustryTileTable(0,3, 35),
		new IndustryTileTable(1,0, 37),
		new IndustryTileTable(1,3, 38),
		new IndustryTileTable(2,0, 37),
		new IndustryTileTable(2,1, 37),
		new IndustryTileTable(2,2, 38),
		new IndustryTileTable(2,3, 38),
		// MKEND
	};

	static final IndustryTileTable _tile_table_farm2_2[] = {
		new IndustryTileTable(2,0, 33),
		new IndustryTileTable(2,1, 34),
		new IndustryTileTable(0,0, 36),
		new IndustryTileTable(0,1, 36),
		new IndustryTileTable(0,2, 37),
		new IndustryTileTable(0,3, 37),
		new IndustryTileTable(1,0, 35),
		new IndustryTileTable(1,1, 38),
		new IndustryTileTable(1,2, 38),
		new IndustryTileTable(1,3, 37),
		new IndustryTileTable(2,2, 37),
		new IndustryTileTable(2,3, 35),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_farm2[] = {
		_tile_table_farm2_0,
		_tile_table_farm2_1,
		_tile_table_farm2_2,
	};

	static final IndustryTileTable _tile_table_lumber_mill_0[] = {
		new IndustryTileTable(0,0, 125),
		new IndustryTileTable(0,1, 126),
		new IndustryTileTable(1,0, 127),
		new IndustryTileTable(1,1, 128),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_lumber_mill[] = {
		_tile_table_lumber_mill_0,
	};

	static final IndustryTileTable _tile_table_cotton_candy_0[] = {
		new IndustryTileTable(0,0, 129),
		new IndustryTileTable(0,1, 129),
		new IndustryTileTable(0,2, 129),
		new IndustryTileTable(0,3, 129),
		new IndustryTileTable(1,0, 129),
		new IndustryTileTable(1,1, 129),
		new IndustryTileTable(1,2, 129),
		new IndustryTileTable(1,3, 129),
		new IndustryTileTable(2,0, 129),
		new IndustryTileTable(2,1, 129),
		new IndustryTileTable(2,2, 129),
		new IndustryTileTable(2,3, 129),
		new IndustryTileTable(3,0, 129),
		new IndustryTileTable(3,1, 129),
		new IndustryTileTable(3,2, 129),
		new IndustryTileTable(3,3, 129),
		new IndustryTileTable(1,4, 129),
		new IndustryTileTable(2,4, 129),
		// MKEND
	};

	static final IndustryTileTable _tile_table_cotton_candy_1[] = {
		new IndustryTileTable(0,0, 129),
		new IndustryTileTable(1,0, 129),
		new IndustryTileTable(2,0, 129),
		new IndustryTileTable(3,0, 129),
		new IndustryTileTable(4,0, 129),
		new IndustryTileTable(0,1, 129),
		new IndustryTileTable(1,1, 129),
		new IndustryTileTable(2,1, 129),
		new IndustryTileTable(3,1, 129),
		new IndustryTileTable(4,1, 129),
		new IndustryTileTable(0,2, 129),
		new IndustryTileTable(1,2, 129),
		new IndustryTileTable(2,2, 129),
		new IndustryTileTable(3,2, 129),
		new IndustryTileTable(4,2, 129),
		new IndustryTileTable(0,3, 129),
		new IndustryTileTable(1,3, 129),
		new IndustryTileTable(2,3, 129),
		new IndustryTileTable(3,3, 129),
		new IndustryTileTable(4,3, 129),
		new IndustryTileTable(1,4, 129),
		new IndustryTileTable(2,4, 129),
		new IndustryTileTable(3,4, 129),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_cotton_candy[] = {
		_tile_table_cotton_candy_0,
		_tile_table_cotton_candy_1,
	};

	static final IndustryTileTable _tile_table_candy_factory_0[] = {
		new IndustryTileTable(0,0, 131),
		new IndustryTileTable(0,1, 132),
		new IndustryTileTable(1,0, 133),
		new IndustryTileTable(1,1, 134),
		new IndustryTileTable(0,2, 131),
		new IndustryTileTable(0,3, 132),
		new IndustryTileTable(1,2, 133),
		new IndustryTileTable(1,3, 134),
		new IndustryTileTable(2,1, 131),
		new IndustryTileTable(2,2, 132),
		new IndustryTileTable(3,1, 133),
		new IndustryTileTable(3,2, 134),
		// MKEND
	};

	static final IndustryTileTable _tile_table_candy_factory_1[] = {
		new IndustryTileTable(0,0, 131),
		new IndustryTileTable(0,1, 132),
		new IndustryTileTable(1,0, 133),
		new IndustryTileTable(1,1, 134),
		new IndustryTileTable(2,0, 131),
		new IndustryTileTable(2,1, 132),
		new IndustryTileTable(3,0, 133),
		new IndustryTileTable(3,1, 134),
		new IndustryTileTable(1,2, 131),
		new IndustryTileTable(1,3, 132),
		new IndustryTileTable(2,2, 133),
		new IndustryTileTable(2,3, 134),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_candy_factory[] = {
		_tile_table_candy_factory_0,
		_tile_table_candy_factory_1,
	};

	static final IndustryTileTable _tile_table_battery_farm_0[] = {
		new IndustryTileTable(0,0, 135),
		new IndustryTileTable(0,1, 135),
		new IndustryTileTable(0,2, 135),
		new IndustryTileTable(0,3, 135),
		new IndustryTileTable(1,0, 135),
		new IndustryTileTable(1,1, 135),
		new IndustryTileTable(1,2, 135),
		new IndustryTileTable(1,3, 135),
		new IndustryTileTable(2,0, 135),
		new IndustryTileTable(2,1, 135),
		new IndustryTileTable(2,2, 135),
		new IndustryTileTable(2,3, 135),
		new IndustryTileTable(3,0, 135),
		new IndustryTileTable(3,1, 135),
		new IndustryTileTable(3,2, 135),
		new IndustryTileTable(3,3, 135),
		new IndustryTileTable(4,0, 135),
		new IndustryTileTable(4,1, 135),
		new IndustryTileTable(4,2, 135),
		new IndustryTileTable(4,3, 135),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_battery_farm[] = {
		_tile_table_battery_farm_0,
	};

	static final IndustryTileTable _tile_table_cola_wells_0[] = {
		new IndustryTileTable(0,0, 137),
		new IndustryTileTable(0,1, 137),
		new IndustryTileTable(0,2, 137),
		new IndustryTileTable(1,0, 137),
		new IndustryTileTable(1,1, 137),
		new IndustryTileTable(1,2, 137),
		new IndustryTileTable(2,1, 137),
		new IndustryTileTable(2,2, 137),
		// MKEND
	};

	static final IndustryTileTable _tile_table_cola_wells_1[] = {
		new IndustryTileTable(0,1, 137),
		new IndustryTileTable(0,2, 137),
		new IndustryTileTable(0,3, 137),
		new IndustryTileTable(1,0, 137),
		new IndustryTileTable(1,1, 137),
		new IndustryTileTable(1,2, 137),
		new IndustryTileTable(2,1, 137),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_cola_wells[] = {
		_tile_table_cola_wells_0,
		_tile_table_cola_wells_1,
	};

	static final IndustryTileTable _tile_table_toy_shop_0[] = {
		new IndustryTileTable(0,0, 138),
		new IndustryTileTable(0,1, 139),
		new IndustryTileTable(1,0, 140),
		new IndustryTileTable(1,1, 141),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_toy_shop[] = {
		_tile_table_toy_shop_0,
	};

	static final IndustryTileTable _tile_table_toy_factory_0[] = {
		new IndustryTileTable(0,0, 147),
		new IndustryTileTable(0,1, 142),
		new IndustryTileTable(1,0, 147),
		new IndustryTileTable(1,1, 143),
		new IndustryTileTable(2,0, 147),
		new IndustryTileTable(2,1, 144),
		new IndustryTileTable(3,0, 146),
		new IndustryTileTable(3,1, 145),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_toy_factory[] = {
		_tile_table_toy_factory_0,
	};

	static final IndustryTileTable _tile_table_plastic_fountain_0[] = {
		new IndustryTileTable(0,0, 148),
		new IndustryTileTable(0,1, 151),
		new IndustryTileTable(0,2, 154),
		// MKEND
	};

	static final IndustryTileTable _tile_table_plastic_fountain_1[] = {
		new IndustryTileTable(0,0, 148),
		new IndustryTileTable(1,0, 151),
		new IndustryTileTable(2,0, 154),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_plastic_fountain[] = {
		_tile_table_plastic_fountain_0,
		_tile_table_plastic_fountain_1,
	};

	static final IndustryTileTable _tile_table_fizzy_drink_0[] = {
		new IndustryTileTable(0,0, 156),
		new IndustryTileTable(0,1, 157),
		new IndustryTileTable(1,0, 158),
		new IndustryTileTable(1,1, 159),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_fizzy_drink[] = {
		_tile_table_fizzy_drink_0,
	};

	static final IndustryTileTable _tile_table_bubble_generator_0[] = {
		new IndustryTileTable(0,0, 163),
		new IndustryTileTable(0,1, 160),
		new IndustryTileTable(1,0, 163),
		new IndustryTileTable(1,1, 161),
		new IndustryTileTable(2,0, 163),
		new IndustryTileTable(2,1, 162),
		new IndustryTileTable(0,2, 163),
		new IndustryTileTable(0,3, 160),
		new IndustryTileTable(1,2, 163),
		new IndustryTileTable(1,3, 161),
		new IndustryTileTable(2,2, 163),
		new IndustryTileTable(2,3, 162),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_bubble_generator[] = {
		_tile_table_bubble_generator_0,
	};

	static final IndustryTileTable _tile_table_toffee_quarry_0[] = {
		new IndustryTileTable(0,0, 164),
		new IndustryTileTable(1,0, 165),
		new IndustryTileTable(2,0, 166),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_toffee_quarry[] = {
		_tile_table_toffee_quarry_0,
	};

	static final IndustryTileTable _tile_table_sugar_mine_0[] = {
		new IndustryTileTable(0,0, 167),
		new IndustryTileTable(0,1, 168),
		new IndustryTileTable(1,0, 169),
		new IndustryTileTable(1,1, 170),
		new IndustryTileTable(2,0, 171),
		new IndustryTileTable(2,1, 172),
		new IndustryTileTable(3,0, 173),
		new IndustryTileTable(3,1, 174),
		// MKEND
	};

	static final IndustryTileTable [] _tile_table_sugar_mine[] = {
		_tile_table_sugar_mine_0,
	};

	//#undef MK
	//#undef MKEND

	//#define MK(tbl, a,b,c, p1,p2, r1,r2, a1,a2,a3, proc) {tbl,lengthof(tbl),a,b,c,{p1,p2},{r1,r2},{a1,a2,a3},proc}

	


	
	public static final IndustrySpec _industry_spec[] = {
	/*        name                    not close to   produce prodrate  accepts     checkproc */
		new IndustrySpec(_tile_table_coal_mine,         1,255,255,    1,255,  15, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_power_station,     0,255,255,  255,255,   0, 0,    1,255,255,  0),
		new IndustrySpec(_tile_table_sawmill,           3,255,255,    5,255,   0, 0,    7,255,255,  0),
		new IndustrySpec(_tile_table_forest,            2, 14,255,    7,255,  13, 0,  255,255,255,  1),
		new IndustrySpec(_tile_table_oil_refinery,      5,255,255,    5,255,   0, 0,    3,255,255,  2),
		new IndustrySpec(_tile_table_oil_rig,           4,255,255,    3,  0,  15, 2,  255,255,255,  2),
		new IndustrySpec(_tile_table_factory,           9,  8,255,    5,255,   0, 0,    4,  6,  9,  0),
		new IndustrySpec(_tile_table_printing_works,   14,255,255,    5,255,   0, 0,    9,255,255,  0),
		new IndustrySpec(_tile_table_steel_mill,       18,  6,255,    9,255,   0, 0,    8,255,255,  0),
		new IndustrySpec(_tile_table_farm,              6, 13,255,    6,  4,  10,10,  255,255,255,  3),
		new IndustrySpec(_tile_table_copper_mine,      23,255,255,    8,255,  10, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_oil_well,          4,255,255,    3,255,  12, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_bank,             12,255,255,   10,255,   6, 0,   10,255,255,  0),
		new IndustrySpec(_tile_table_food_process,      9, 19, 24,   11,255,   0, 0,    4,  6,255,  0),
		new IndustrySpec(_tile_table_paper_mill,        3,  7,255,    9,255,   0, 0,    7,255,255,  0),
		new IndustrySpec(_tile_table_gold_mine,        16,255,255,   10,255,   7, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_bank2,            15, 17,255,  255,255,   0, 0,   10,255,255,  0),
		new IndustrySpec(_tile_table_diamond_mine,     16,255,255,   10,255,   7, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_iron_mine,         8,255,255,    8,255,  10, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_fruit_plantation, 13,255,255,    4,255,  10, 0,  255,255,255,  4),
		new IndustrySpec(_tile_table_rubber_plantation,23,255,255,    1,255,  10, 0,  255,255,255,  4),
		new IndustrySpec(_tile_table_water_supply,     22,255,255,    9,255,  12, 0,  255,255,255,  5),
		new IndustrySpec(_tile_table_water_tower,      21,255,255,  255,255,   0, 0,    9,255,255,  5),
		new IndustrySpec(_tile_table_factory2,         10, 20, 25,    5,255,   0, 0,    1,  8,  7,  4),
		new IndustrySpec(_tile_table_farm2,            13,255,255,    6,255,  11, 0,  255,255,255,  4),
		new IndustrySpec(_tile_table_lumber_mill,      23,255,255,    7,255,   0, 0,  255,255,255,  6),
		new IndustrySpec(_tile_table_cotton_candy,     27,255,255,    8,255,  13, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_candy_factory,    26, 35, 36,    5,255,   0, 0,    1,  6,  8,  0),
		new IndustrySpec(_tile_table_battery_farm,     31,255,255,    4,255,  11, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_cola_wells,       33,255,255,    7,255,  12, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_toy_shop,         31,255,255,  255,255,   0, 0,    3,255,255,  0),
		new IndustrySpec(_tile_table_toy_factory,      30, 28, 32,    3,255,   0, 0,   10,  4,255,  0),
		new IndustrySpec(_tile_table_plastic_fountain, 31,255,255,   10,255,  14, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_fizzy_drink,      29, 34,255,   11,255,   0, 0,    7,  9,255,  0),
		new IndustrySpec(_tile_table_bubble_generator, 33,255,255,    9,255,  13, 0,  255,255,255,  7),
		new IndustrySpec(_tile_table_toffee_quarry,    27,255,255,    6,255,  10, 0,  255,255,255,  0),
		new IndustrySpec(_tile_table_sugar_mine,       27,255,255,    1,255,  11, 0,  255,255,255,  0),
	};
	//#undef MK

	public final int _industry_type_costs[] = {
		210, 30,   28, 200,  31, 240,  26,  26,  27, 250, 205, 220, 193,  26,
		28,  208,  19, 213, 220, 225, 218, 199,  14,  26, 250, 17,  195,  26,
		187, 193,  17,  20, 192,  22, 203, 213, 210
	};

	public final byte _build_industry_types[][] = {
		{ 1,  2, 4,  6, 8, 0, 3, 5,  9, 11, 18 },
		{ 1, 14, 4, 13, 7, 0, 3, 9, 11, 15 },
		{ 25, 13, 4, 23, 22, 11, 17, 10, 24, 19, 20, 21 },
		{ 27, 30, 31, 33, 26, 28, 29, 32, 34, 35, 36 },
	};


	static final byte _industry_create_table_0[] = {
		4, 4,
		5, 3,
		5, 2,
		8, 0,
		5, 1,
		4, 11,
		5, 18,
		9, 9,
		5, 8,
		5, 6,
		0
	};

	static final byte _industry_create_table_1[] = {
		4, 4,
		5, 3,
		5, 14,
		8, 0,
		5, 1,
		5, 11,
		5, 13,
		9, 9,
		4, 15,
		5, 7,
		6, 16,
		0
	};

	static final byte _industry_create_table_2[] = {
		4, 4,
		5, 11,
		5, 16,
		4, 13,
		4, 17,
		4, 10,
		4, 19,
		4, 20,
		4, 21,
		8, 22,
		4, 13,
		4, 23,
		2, 24,
		0
	};

	static final byte _industry_create_table_3[] = {
		5, 26,
		5, 27,
		4, 28,
		5, 29,
		4, 30,
		5, 31,
		5, 32,
		4, 33,
		5, 34,
		5, 35,
		4, 36,
		0
	};

	public static final byte [][] _industry_create_table = {
		_industry_create_table_0,
		_industry_create_table_1,
		_industry_create_table_2,
		_industry_create_table_3,
	};


	private static final int PAS = AcceptedCargo.CT_PASSENGERS;

	private static final int COL = AcceptedCargo.CT_COAL;
	private static final int RUB = AcceptedCargo.CT_RUBBER;
	private static final int  SUG = AcceptedCargo.CT_SUGAR;

	private static final int  MAL = AcceptedCargo.CT_MAIL;

	private static final int  OIL = AcceptedCargo.CT_OIL;
	private static final int  TOY = AcceptedCargo.CT_TOYS;

	private static final int  LIV = AcceptedCargo.CT_LIVESTOCK; // Fruit too
	private static final int  BAT = AcceptedCargo.CT_BATTERIES;

	private static final int  GRA = AcceptedCargo.CT_GRAIN;
	private static final int  WHT = AcceptedCargo.CT_WHEAT; // Maize too
	private static final int  TOF = AcceptedCargo.CT_TOFFEE;

	private static final int  WOD = AcceptedCargo.CT_WOOD;
	private static final int  CLA = AcceptedCargo.CT_COLA;

	private static final int  IRN = AcceptedCargo.CT_IRON_ORE;
	private static final int  COP = AcceptedCargo.CT_COPPER_ORE;
	private static final int  CCY = AcceptedCargo.CT_COTTON_CANDY;

	private static final int  STL = AcceptedCargo.CT_STEEL;
	private static final int  PAP = AcceptedCargo.CT_PAPER;
	private static final int  WAT = AcceptedCargo.CT_WATER;
	private static final int  BBL = AcceptedCargo.CT_BUBBLES;

	private static final int  VAL = AcceptedCargo.CT_VALUABLES;
	private static final int  GLD = AcceptedCargo.CT_GOLD; // Diamonds too
	private static final int  PLC = AcceptedCargo.CT_PLASTIC;

	private static final int  INV = AcceptedCargo.CT_INVALID;

	public static final /*CargoID*/int _industry_map5_accepts_1[] = {
		INV, INV, INV, PAS, INV, INV, INV, INV,
		PAS, INV, INV, PAS, PAS, PAS, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, PAS,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, PAS, PAS, INV, INV, INV, INV, GRA,
		GRA, GRA, GRA, INV, INV, INV, INV, INV,
		INV, INV, PAS, PAS, PAS, PAS, PAS, PAS,
		PAS, PAS, PAS, PAS, WHT, WHT, WHT, WHT,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, COP, COP, COP, COP, INV, INV, INV,
		INV, INV, INV, CCY, CCY, CCY, CCY, INV,
		INV, INV, INV, INV, INV, INV, BAT, BAT,
		BAT, BAT, BAT, BAT, INV, INV, INV, INV,
		INV, INV, INV, INV, BBL, BBL, BBL, BBL,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV
	};


	public static final int _industry_map5_accepts_2[] = {
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, STL,
		STL, STL, STL, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, WOD, WOD, WOD, WOD, INV, INV, INV,
		INV, INV, INV, TOF, TOF, TOF, TOF, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV
	};

	public static final int _industry_map5_accepts_3[] = {
		INV, INV, INV, INV, INV, INV, INV, INV,
		COL, INV, INV, INV, INV, WOD, INV, INV,
		INV, INV, INV, INV, OIL, INV, INV, INV,
		PAS, MAL, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, LIV,
		LIV, LIV, LIV, PAP, PAP, PAP, PAP, INV,
		INV, INV, INV, INV, IRN, IRN, IRN, IRN,
		IRN, IRN, VAL, VAL, LIV, LIV, LIV, LIV,
		WOD, WOD, WOD, WOD, WOD, WOD, WOD, WOD,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, GLD, GLD, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV, INV,
		WAT, RUB, RUB, RUB, RUB, INV, INV, INV,
		INV, INV, INV, SUG, SUG, SUG, SUG, INV,
		INV, INV, TOY, TOY, TOY, TOY, PLC, PLC,
		PLC, PLC, PLC, PLC, INV, INV, INV, INV,
		INV, INV, INV, INV, CLA, CLA, CLA, CLA,
		INV, INV, INV, INV, INV, INV, INV, INV,
		INV, INV, INV, INV, INV, INV, INV
	};
	
	
}



