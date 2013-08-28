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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;


class UnloadedChunk
{
	public final int x, z;
	public UnloadedChunk(int x, int y)
	{
		this.x = x;
		this.z = y;
	}
}


public class UnloadedChunks
{
	public final String worldname;
	List<UnloadedChunk> unloadedChunks = new ArrayList<>();
	
	
	public UnloadedChunks(String world)
	{
		this.worldname = world;
	}
	
	
	public void addUnloaded(Chunk chunk)
	{
		UnloadedChunk uChunk = new UnloadedChunk(chunk.getX(), chunk.getZ());
		unloadedChunks.add(uChunk);
	}
	
	
	Plugin plugin = Plugin.get();
	public void loadUnloaded(Border border)
	{
		final boolean generate = true;
		
		World world = Plugin.get().getServer().getWorld(worldname);
		List<UnloadedChunk> unloadedChunks = new ArrayList<>(this.unloadedChunks);
		for(UnloadedChunk uChunk : unloadedChunks)
			if(border.isInsideLimit(uChunk.x, uChunk.z))
				world.getChunkAt(uChunk.x, uChunk.z).load(generate);
	}
	
	
	public void unloadLoaded(Border border)
	{
		final boolean save = true, safe = false;
		
		World world = Plugin.get().getServer().getWorld(worldname);
		Chunk[] loadedChunks = world.getLoadedChunks();
		for(Chunk chunk : loadedChunks)
			if(border.isOutsideLimit(chunk))
				chunk.unload(save, safe);
	}
}