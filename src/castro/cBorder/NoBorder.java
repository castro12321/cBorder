/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

/**
 * Represents map with no border.
 * Each check return value as if the map had infinite border size
 */
public class NoBorder extends Border
{
	private static final int million = 1000000;
	public NoBorder()
	{
		// Minecraft is limited to 30 million blocks,
		// so border that have 3 million chunks (48 million blocks) is big enough.
		super(3*million, 3*million, 0, 0);
	}
	
	
	/**
	 * Overriden methods to disable redundant calculations
	 */
	@Override public boolean isSafe(Block block)			{ return true; }
	@Override public boolean isSafe(Chunk chunk)			{ return true; }
	
	@Override public boolean isNotSafe(Block block)			{ return false; }
	@Override public boolean isNotSafe(Chunk chunk)			{ return false; }
	
	@Override public boolean isOutsideLimit(Block block)	{ return false; }
	@Override public boolean isOutsideLimit(Chunk chunk)	{ return false; }
	
	@Override public boolean isInsideLimit(Block block)		{ return true; }
	@Override public boolean isInsideLimit(Chunk chunk)		{ return true; }
	
	@Override public boolean isLastBlock(Block block)		{ return false; }
	@Override public boolean isLastChunk(Chunk chunk)		{ return false; }
}