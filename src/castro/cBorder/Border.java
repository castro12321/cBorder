/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;


public class Border
{
	public final int safeRadiusX, safeRadiusZ;
	public int radiusX, radiusZ; // Radius of the map from center (in chunk)
	public int centerX, centerZ; // Center of the border (in chunks) from 0, 0
	
	public int
		lowBlockX,
		lowBlockZ,
		lowChunkX,
		lowChunkZ,
		
		highBlockX,
		highBlockZ,
		highChunkX,
		highChunkZ,
		
		safeLowBlockX,
		safeLowBlockZ,
		safeLowChunkX,
		safeLowChunkZ,
	
		safeHighBlockX,
		safeHighBlockZ,
		safeHighChunkX,
		safeHighChunkZ;
	
	
	// all values are provided in chunks
	public Border(int radiusX, int radiusZ, int centerX, int centerZ)
	{
		this.safeRadiusX = radiusX;
		this.safeRadiusZ = radiusZ;
		this.radiusX = radiusX;
		this.radiusZ = radiusZ;
		this.centerX = centerX;
		this.centerZ = centerZ;
		
		init();
	}
	
	
	private void init()
	{
		radiusX += 1; // Have to add at least one safe chunk. The last chunk is bugged as hell
		radiusZ += 1; // See above
		radiusX += Config.additionalSafeChunks();
		radiusZ += Config.additionalSafeChunks();
		
		/*
		 * Settings chunks limit
		 */
		lowChunkX  = centerX - radiusX;
		lowChunkZ  = centerZ - radiusZ;
		highChunkX = centerX + radiusX;
		highChunkZ = centerZ + radiusZ;
		
		/*
		 * Settings blocks limit
		 */
		int centerBlockX  = centerX << 4;
		int centerBlockZ  = centerZ << 4;
		int radiusBlocksX  = radiusX * 16;
		int radiusBlocksZ  = radiusZ * 16;
		lowBlockX  = centerBlockX - radiusBlocksX;
		lowBlockZ  = centerBlockZ - radiusBlocksZ;
		highBlockX = centerBlockX + radiusBlocksX;
		highBlockZ = centerBlockZ + radiusBlocksZ;
		
		/*
		 * Adjusting blocks limit due to chunk 0, 0
		 * Chunks counting starts from 0 for positive and from -1 for negative...
		 */
		highBlockX += 15;
		highBlockZ += 15;
		
		/*
		 * Setting safe chunks
		 */
		int safeOffsetChunks = 1; // Have to add at least one safe chunk. The last chunk is bugged as hell
		safeOffsetChunks += Config.additionalSafeChunks();
		safeLowChunkX  = lowChunkX  + safeOffsetChunks;
		safeLowChunkZ  = lowChunkZ  + safeOffsetChunks;
		safeHighChunkX = highChunkX - safeOffsetChunks;
		safeHighChunkZ = highChunkZ - safeOffsetChunks;
		
		/*
		 * Setting safe blocks
		 */
		int safeOffsetBlocks = safeOffsetChunks * 16;
		safeLowBlockX  = lowBlockX  + safeOffsetBlocks;
		safeLowBlockZ  = lowBlockZ  + safeOffsetBlocks;
		safeHighBlockX = highBlockX - safeOffsetBlocks;
		safeHighBlockZ = highBlockZ - safeOffsetBlocks;
		
		radiusX -= 1; // Remove the safe radius increase applied above
		radiusZ -= 1; // See above
		radiusX -= Config.additionalSafeChunks();
		radiusZ -= Config.additionalSafeChunks();
		
		// Just log, debug purpose
		/**
		Plugin.instance.log("CALCULATED BORDER:");
		Plugin.instance.log(radius + " " + centerX + " " + centerZ);
		Plugin.instance.log(lowChunkX + " " + lowChunkZ + " --- " + highChunkX + " " + highChunkZ);
		Plugin.instance.log(lowBlockX + " " + lowBlockZ + " --- " + highBlockX + " " + highBlockZ);
		Plugin.instance.log(safeLowChunkX + " " + safeLowChunkZ + " --- " + safeHighChunkX + " " + safeHighChunkZ);
		Plugin.instance.log(safeLowBlockX + " " + safeLowBlockZ + " --- " + safeHighBlockX + " " + safeHighBlockZ);
		/**/
	}
	
	
	public boolean isSafe(Block block)
	{ return isSafe(block.getChunk()); }
	public boolean isSafe(Chunk chunk)
	{
		return chunk.getX() >= safeLowChunkX
			&& chunk.getZ() >= safeLowChunkZ
			&& chunk.getX() <= safeHighChunkX
			&& chunk.getZ() <= safeHighChunkZ;
			
	}
	
	
	public boolean isNotSafe(Block block)
	{ return isNotSafe(block.getChunk()); }
	public boolean isNotSafe(Chunk chunk)
	{
		return !isSafe(chunk);
	}
	
	
	public boolean isOutsideLimit(Block block) // If we have block then it is inside limit, because chunk is loaded
	{ return isOutsideLimit(block.getChunk()); }
	public boolean isOutsideLimit(Chunk chunk)
	{ return isOutsideLimit(chunk.getX(), chunk.getZ()); }
	public boolean isOutsideLimit(int chunkX, int chunkZ)
	{
		return chunkX > highChunkX
			|| chunkZ > highChunkZ
			|| chunkX < lowChunkX
			|| chunkZ < lowChunkZ;
	}
	
	
	public boolean isInsideLimit(Block block)
	{ return isInsideLimit(block.getChunk()); }
	public boolean isInsideLimit(Chunk chunk)
	{ return isInsideLimit(chunk.getX(), chunk.getZ()); }
	public boolean isInsideLimit(int chunkX, int chunkZ)
	{
		return !isOutsideLimit(chunkX, chunkZ);
	}
	
	
	public boolean isLastBlock(Block block)
	{
		return block.getX() == lowBlockX
			|| block.getX() == highBlockX
			|| block.getZ() == lowBlockZ
			|| block.getZ() == highBlockZ;
	}
	
	
	public boolean isLastChunk(Chunk chunk)
	{
		return chunk.getX() == lowChunkX
			|| chunk.getX() == highChunkX
			|| chunk.getZ() == lowChunkZ
			|| chunk.getZ() == highChunkZ;
	}
	
	
	public boolean equals(Object o)
	{
		if(o instanceof Border)
		{
			Border border = (Border)o;
			return radiusX == border.radiusX
				&& radiusZ == border.radiusZ
				&& centerX == border.centerX
				&& centerZ == border.centerZ;
		}
		return false;
	}
	
	
	public Location getSafe(Location from)
	{		
		int x, z, newX, newZ;
		x = newX = from.getBlockX();
		z = newZ = from.getBlockZ();
		
		if(x > safeHighBlockX) newX = safeHighBlockX-3;
		if(z > safeHighBlockZ) newZ = safeHighBlockZ-3;
		if(x < safeLowBlockX)  newX = safeLowBlockX+3;
		if(z < safeLowBlockZ)  newZ = safeLowBlockZ+3;	
		
		if(x != newX || z != newZ)
			return from.getWorld().getHighestBlockAt(newX, newZ).getLocation();
		return null;
	}
}
