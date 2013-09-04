/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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