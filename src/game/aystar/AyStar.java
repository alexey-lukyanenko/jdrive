package game.aystar;

import game.NPFFoundTargetData;
import game.struct.OpenListNode;
import game.struct.PathNode;
import game.util.Hash;
import game.util.TTDQueue;
import game.util.TTDQueueImpl;

public class AyStar 
{


	public static final int	AYSTAR_FOUND_END_NODE = 1;
	public static final int	AYSTAR_EMPTY_OPENLIST = 2;
	public static final int	AYSTAR_STILL_BUSY = 3;
	public static final int	AYSTAR_NO_PATH = 4;
	public static final int	AYSTAR_LIMIT_REACHED = 5;
	public static final int	AYSTAR_DONE = 6;

	public static final int	AYSTAR_INVALID_NODE = -1;



	/* These fields should be filled before initting the AyStar, but not changed
	 * afterwards (except for user_data and user_path)! (free and init again to change them) */

	/* These should point to the application specific routines that do the
	 * actual work */ 
	public AyStar_CalculateG CalculateG;
	public AyStar_CalculateH CalculateH;
	public AyStar_GetNeighbours GetNeighbours;
	public AyStar_EndNodeCheck EndNodeCheck;
	public AyStar_FoundEndNode FoundEndNode;
	public AyStar_BeforeExit BeforeExit;




	/* These are completely untouched by AyStar, they can be accesed by
	 * the application specific routines to input and output data.
	 * user_path should typically contain data about the resulting path
	 * afterwards, user_target should typically contain information about
	 * what where looking for, and user_data can contain just about
	 * everything */
	// TODO resurrect
	//Object user_path;
	public Object user_target;
	public final int [] user_data = new int[10];

	// [dz] can be some superclass or interface of NPFFoundTargetData? 
	public NPFFoundTargetData user_path;

	/* How many loops are there called before AyStarMain_Main gives
	 * control back to the caller. 0 = until done */
	private int loops_per_tick;
	/* If the g-value goes over this number, it stops searching
	 *  0 = infinite */
	private int max_path_cost;
	/* The maximum amount of nodes that will be expanded, 0 = infinite */
	private int max_search_nodes;

	/* These should be filled with the neighbours of a tile by
	 * GetNeighbours */
	public final AyStarNode[] neighbours;
	public int num_neighbours;

	/* These will contain the methods for manipulating the AyStar. Only
	 * main() should be called externally */
	public AyStar_AddStartNode addstart;
	AyStar_Main main;
	AyStar_Loop loop;
	AyStar_Free free;
	AyStar_Clear clear;
	AyStar_CheckTile checktile;


	/*
	abstract void addstart(AyStarNode start_node, int g);
	abstract int main();
	abstract int loop();
	abstract int checktile(AyStarNode current, OpenListNode parent);
	abstract void free();
	abstract void clear();
	*/

	/* These will contain the open and closed lists */

	/* The actual closed list */
	final Hash ClosedListHash = new Hash();
	/* The open queue */
	final TTDQueue<OpenListNode> OpenListQueue = new TTDQueueImpl<OpenListNode>();
	/* An extra hash to speed up the process of looking up an element in
	 * the open list */
	final Hash OpenListHash = new Hash();




	public AyStar() {
		neighbours = new AyStarNode[12];
	}

	public static void init_AyStar(AyStar aystar, Hash_HashProc hash, int num_buckets) {
		// Allocated the Hash for the OpenList and ClosedList
		// TODO init_Hash(aystar.OpenListHash, hash, num_buckets);
		// TODO init_Hash(aystar.ClosedListHash, hash, num_buckets);

		// Set up our sorting queue
		//  BinaryHeap allocates a block of 1024 nodes
		//  When that one gets full it reserves an other one, till this number
		//  That is why it can stay this high
		//init_BinaryHeap(aystar.OpenListQueue, 102400); // TODO kill init_BinaryHeap?

		aystar.addstart	= AyStar::AyStarMain_AddStartNode;
		aystar.main		= AyStar::AyStarMain_Main;
		aystar.loop		= AyStar::AyStarMain_Loop;
		aystar.free		= AyStar::AyStarMain_Free;
		aystar.clear		= AyStar::AyStarMain_Clear;
		aystar.checktile	= AyStar::AyStarMain_CheckTile;
	}


	// Adds a node to the OpenList
	//  It makes a copy of node, and puts the pointer of parent in the struct
	void AyStarMain_OpenList_Add(PathNode parent, AyStarNode node, int f, int g)
	{
		// Add a new Node to the OpenList
		OpenListNode new_node = new OpenListNode();
		new_node.g = g;
		new_node.path.parent = parent;
		new_node.path.node = new AyStarNode( node );
		OpenListHash.Hash_Set(node.tile, node.direction, new_node);

		// Add it to the queue
		OpenListQueue.push(new_node, f);
	}














	// This looks in the Hash if a node exists in ClosedList
	//  If so, it returns the PathNode, else NULL
	static PathNode AyStarMain_ClosedList_IsInList(AyStar aystar, AyStarNode node)
	{
		return (PathNode)aystar.ClosedListHash.Hash_Get( node.tile, node.direction);
	}

	// This adds a node to the ClosedList
	//  It makes a copy of the data
	static void AyStarMain_ClosedList_Add(AyStar aystar, PathNode node)
	{
		// Add a node to the ClosedList
		PathNode new_node = new PathNode( node );
		aystar.ClosedListHash.Hash_Set( node.node.tile, node.node.direction, new_node);
	}

	// Checks if a node is in the OpenList
	//   If so, it returns the OpenListNode, else NULL
	static OpenListNode AyStarMain_OpenList_IsInList(AyStar aystar, AyStarNode node)
	{
		return (OpenListNode)aystar.OpenListHash.Hash_Get(node.tile, node.direction);
	}

	// Gets the best node from OpenList
	//  returns the best node, or NULL of none is found
	// Also it deletes the node from the OpenList
	static OpenListNode AyStarMain_OpenList_Pop(AyStar aystar)
	{
		// Return the item the Queue returns.. the best next OpenList item.
		OpenListNode res = aystar.OpenListQueue.pop();
		if (res != null)
			aystar.OpenListHash.Hash_Delete(res.path.node.tile, res.path.node.direction);

		return res;
	}

	// Adds a node to the OpenList
	//  It makes a copy of node, and puts the pointer of parent in the struct
	static void AyStarMain_OpenList_Add(AyStar aystar, PathNode parent, AyStarNode node, int f, int g)
	{
		// Add a new Node to the OpenList
		OpenListNode new_node = new OpenListNode();
		new_node.g = g;
		new_node.path.parent = parent;
		new_node.path.node = node;
		aystar.OpenListHash.Hash_Set(node.tile, node.direction, new_node);

		// Add it to the queue
		aystar.OpenListQueue.push(new_node, f);
	}


	/*
	 * Checks one tile and calculate his f-value
	 *  return values:
	 *	AYSTAR_DONE : indicates we are done
	 */
	static int AyStarMain_CheckTile(AyStar aystar, AyStarNode current, OpenListNode parent) {
		int new_f, new_g, new_h;
		PathNode closedlist_parent;
		OpenListNode check;

		// Check the new node against the ClosedList
		if (AyStarMain_ClosedList_IsInList(aystar, current) != null) return AYSTAR_DONE;

		// Calculate the G-value for this node
		new_g = aystar.CalculateG.apply(aystar, current, parent);
		// If the value was INVALID_NODE, we don't do anything with this node
		if (new_g == AYSTAR_INVALID_NODE) return AYSTAR_DONE;

		// There should not be given any other error-code..
		assert(new_g >= 0);
		// Add the parent g-value to the new g-value
		new_g += parent.g;
		if (aystar.getMax_path_cost() != 0 && new_g > aystar.getMax_path_cost()) return AYSTAR_DONE;

		// Calculate the h-value
		new_h = aystar.CalculateH.apply(aystar, current, parent);
		// There should not be given any error-code..
		assert(new_h >= 0);

		// The f-value if g + h
		new_f = new_g + new_h;

		// Get the pointer to the parent in the ClosedList (the currentone is to a copy of the one in the OpenList)
		closedlist_parent = AyStarMain_ClosedList_IsInList(aystar, parent.path.node);

		// Check if this item is already in the OpenList
		if ((check = AyStarMain_OpenList_IsInList(aystar, current)) != null) {
			int i;
			// Yes, check if this g value is lower..
			if (new_g > check.g) return AYSTAR_DONE;
			aystar.OpenListQueue.del( check, 0);
			// It is lower, so change it to this item
			check.g = new_g;
			check.path.parent = closedlist_parent;
			// Copy user data, will probably have changed 
			for (i=0;i< current.user_data.length;i++)
				check.path.node.user_data[i] = current.user_data[i];
			// Readd him in the OpenListQueue
			aystar.OpenListQueue.push(check, new_f);
		} else {
			// A new node, add him to the OpenList
			AyStarMain_OpenList_Add(aystar, closedlist_parent, current, new_f, new_g);
		}

		return AYSTAR_DONE;
	}


	/*
	 * This function is the core of AyStar. It handles one item and checks
	 *  his neighbour items. If they are valid, they are added to be checked too.
	 *  return values:
	 *	AYSTAR_EMPTY_OPENLIST : indicates all items are tested, and no path
	 *	has been found.
	 *	AYSTAR_LIMIT_REACHED : Indicates that the max_nodes limit has been
	 *	reached.
	 *	AYSTAR_FOUND_END_NODE : indicates we found the end. Path_found now is true, and in path is the path found.
	 *	AYSTAR_STILL_BUSY : indicates we have done this tile, did not found the path yet, and have items left to try.
	 */
	static int AyStarMain_Loop(AyStar aystar) {
		int i, r;

		// Get the best node from OpenList
		OpenListNode current = AyStarMain_OpenList_Pop(aystar);
		// If empty, drop an error
		if (current == null) return AYSTAR_EMPTY_OPENLIST;

		// Check for end node and if found, return that code
		if (aystar.EndNodeCheck.apply(aystar, current) == AYSTAR_FOUND_END_NODE) {
			if (aystar.FoundEndNode != null)
				aystar.FoundEndNode.apply(aystar, current);
			//free(current);
			return AYSTAR_FOUND_END_NODE;
		}

		// Add the node to the ClosedList
		AyStarMain_ClosedList_Add(aystar, current.path);

		// Load the neighbours
		aystar.GetNeighbours.apply(aystar, current);

		// Go through all neighbours
		for (i=0;i<aystar.num_neighbours;i++) {
			// Check and add them to the OpenList if needed
			r = aystar.checktile.apply(aystar, aystar.neighbours[i], current);
		}

		// Free the node
		//free(current);

		if (aystar.max_search_nodes != 0 && aystar.ClosedListHash.Hash_Size() >= aystar.max_search_nodes)
			// We've expanded enough nodes 
			return AYSTAR_LIMIT_REACHED;
		else
			// Return that we are still busy
			return AYSTAR_STILL_BUSY;
	}


	/*
	 * This function frees the memory it allocated
	 */
	static void AyStarMain_Free(AyStar aystar) 
	{
		/* TODO ok?
		aystar.OpenListQueue.free(false);
		// 2nd argument above is false, below is true, to free the values only once 
		aystar.OpenListHash.delete_Hash(true);
		aystar.ClosedListHash.delete_Hash(true);
		*/
		/*#ifdef AYSTAR_DEBUG
		printf("[AyStar] Memory free'd\n");
	#endif*/
	}

	/*
	 * This function make the memory go back to zero
	 *  This function should be called when you are using the same instance again.
	 */
	static void AyStarMain_Clear(AyStar aystar) {
		// Clean the Queue
		aystar.OpenListQueue.clear();
		// Clean the hashes
		aystar.OpenListHash.clear_Hash(true);
		aystar.ClosedListHash.clear_Hash(true);

		/*#ifdef AYSTAR_DEBUG
		printf("[AyStar] Cleared AyStar\n");
	#endif*/
	}

	/*
	 * This is the function you call to run AyStar.
	 *  return values:
	 *	AYSTAR_FOUND_END_NODE : indicates we found an end node.
	 *	AYSTAR_NO_PATH : indicates that there was no path found.
	 *	AYSTAR_STILL_BUSY : indicates we have done some checked, that we did not found the path yet, and that we still have items left to try.
	 * When the algorithm is done (when the return value is not AYSTAR_STILL_BUSY)
	 * aystar.clear() is called. Note that when you stop the algorithm halfway,
	 * you should still call clear() yourself!
	 */
	public static int AyStarMain_Main(AyStar aystar) {
		int r, i = 0;
		// Loop through the OpenList
		//  Quit if result is no AYSTAR_STILL_BUSY or is more than loops_per_tick
		//noinspection StatementWithEmptyBody
		while ((r = aystar.loop.apply(aystar)) == AYSTAR_STILL_BUSY && (aystar.getLoops_per_tick() == 0 || ++i < aystar.getLoops_per_tick()))
			{ }
		/*#ifdef AYSTAR_DEBUG
		if (r == AYSTAR_FOUND_END_NODE)
			printf("[AyStar] Found path!\n");
		else if (r == AYSTAR_EMPTY_OPENLIST)
			printf("[AyStar] OpenList run dry, no path found\n");
		else if (r == AYSTAR_LIMIT_REACHED)
			printf("[AyStar] Exceeded search_nodes, no path found\n");
	#endif*/

		if (aystar.BeforeExit != null)
			aystar.BeforeExit.apply(aystar);

		if (r != AYSTAR_STILL_BUSY)
			// We're done, clean up 
			aystar.clear.apply(aystar);

		// Check result-value
		if (r == AYSTAR_FOUND_END_NODE) return AYSTAR_FOUND_END_NODE;
		// Check if we have some left in the OpenList
		if (r == AYSTAR_EMPTY_OPENLIST || r == AYSTAR_LIMIT_REACHED) return AYSTAR_NO_PATH;

		// Return we are still busy
		return AYSTAR_STILL_BUSY;
	}


	/*
	 * Adds a node from where to start an algorithm. Multiple nodes can be added
	 * if wanted. You should make sure that clear() is called before adding nodes
	 * if the AyStar has been used before (though the normal main loop calls
	 * clear() automatically when the algorithm finishes
	 * g is the cost for starting with this node.
	 */
	static void AyStarMain_AddStartNode(AyStar aystar, AyStarNode start_node, int g) {
		/*#ifdef AYSTAR_DEBUG
		printf("[AyStar] Starting A* Algorithm from node (%d, %d, %d)\n",
			TileX(start_node.tile), TileY(start_node.tile), start_node.direction);
	#endif*/
		AyStarMain_OpenList_Add(aystar, null, start_node, 0, g);
	}

	public int getMax_search_nodes() {
		return max_search_nodes;
	}

	public void setMax_search_nodes(int max_search_nodes) {
		this.max_search_nodes = max_search_nodes;
	}

	public int getMax_path_cost() {
		return max_path_cost;
	}

	public void setMax_path_cost(int max_path_cost) {
		this.max_path_cost = max_path_cost;
	}

	public int getLoops_per_tick() {
		return loops_per_tick;
	}

	public void setLoops_per_tick(int loops_per_tick) {
		this.loops_per_tick = loops_per_tick;
	}	


}

