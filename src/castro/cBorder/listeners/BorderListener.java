/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder.listeners;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import castro.cBorder.Border;
import castro.cBorder.BorderMgr;


public class BorderListener implements Listener
{
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkLoad(ChunkLoadEvent event)
	{
		World world = event.getWorld();
		Border border = BorderMgr.getBorder(world);
		
		Chunk chunk = event.getChunk();
		if(border.isOutsideLimit(chunk)) // Check if chunk is beyond limit --> unload
			chunk.unload(false, false);
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST) // We have to override MultiVerse spawning
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Location to = event.getRespawnLocation();
		Border border = BorderMgr.getBorder(to.getWorld());
		
		Location newLocation = border.getSafe(to);
		if(newLocation != null)
			event.setRespawnLocation(newLocation);
	}
	
	
	@EventHandler
	public void onMobSpawn(CreatureSpawnEvent event)
	{
		Location spawnLocation = event.getLocation();
		Border border = BorderMgr.getBorder(spawnLocation.getWorld());
		
		if(border.isNotSafe(spawnLocation.getChunk()))
			event.setCancelled(true);
	}
}
