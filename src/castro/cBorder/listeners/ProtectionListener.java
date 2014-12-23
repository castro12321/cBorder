/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import castro.cBorder.Border;
import castro.cBorder.BorderMgr;

public class ProtectionListener implements Listener
{
	// onLiquidFlow is necessary becuase the water flowing outside the map (on the unloaded chunks) cause lags
	// Previous version stopped water only on last block of buffer area. This one stops any water outside the border, not only the last block
	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event)
	{
		Block from = event.getBlock();
		Border border = BorderMgr.getNewBorder(from.getWorld());
		
		if(border.isOutside(from.getLocation()))
		{
			event.setCancelled(true);
			from.setType(Material.AIR);
		}
		
		Block to = event.getToBlock();
		if(border.isOutside(to.getLocation()))
		{
			event.setCancelled(true);
			to.setType(Material.AIR);
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Location to = event.getTo();
		World toWorld = to.getWorld();
		Border border = BorderMgr.getNewBorder(toWorld);
		if(border.isOutside(to))
			event.setTo(border.getCenter());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) // We have to override MultiVerse spawning
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Location to = event.getRespawnLocation();
		World toWorld = to.getWorld();
		Border border = BorderMgr.getNewBorder(toWorld);
		if(border.isOutside(to))
			event.setRespawnLocation(border.getCenter());
	}
}