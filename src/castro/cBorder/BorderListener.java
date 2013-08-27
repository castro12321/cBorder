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

import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;


public class BorderListener implements Listener
{
	private static HashMap<String, UnloadedChunks> unloadedChunks = new HashMap<>();
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkLoadEvent event)
	{
		World world = event.getWorld();
		Border border = BorderMgr.getBorder(world);
		
		Chunk chunk = event.getChunk();
		if(border.isOutsideLimit(chunk)) // Check if chunk is beyond limit --> unload
		{
			chunk.unload(false, false);
			unloadedChunks.get(world.getName()).addUnloaded(chunk);
		}
	}
	
	
	public void onWorldLoad(WorldLoadEvent event)
	{
		String worldname = event.getWorld().getName();
		unloadedChunks.put(worldname, new UnloadedChunks(worldname));
	}
	
	
	public void onWorldUnload(WorldUnloadEvent event)
	{
		String worldname = event.getWorld().getName();
		unloadedChunks.remove(worldname);
	}
	
	
	public static void refreshBorder(Border oldBorder, Border newBorder, String worldname)
	{
		if(oldBorder != null)
		{
			World world = Plugin.get().getServer().getWorld(worldname);
			if(world != null)
			{
				// If new radius is greater, load unloaded chunks
				// If new radius is smaller, unload chunks beyond limit
				if(newBorder.radius > oldBorder.radius)
					unloadedChunks.get(worldname).refreshUnloaded();
				else if(newBorder.radius < oldBorder.radius)
					unloadedChunks.get(worldname).refreshLoaded();
			}
		}
	}
}
