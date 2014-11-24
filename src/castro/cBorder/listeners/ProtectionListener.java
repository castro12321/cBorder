/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import castro.cBorder.Border;
import castro.cBorder.BorderMgr;
import castro.cBorder.Config;

public class ProtectionListener implements Listener
{
	private boolean disableProtection = !Config.protection(); 
	
	
	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event)
	{
		if(disableProtection)
			return;
		
		// TODO: sprawdzic, czy event sie pojawia podczas wylewania poza mape
		Border border = BorderMgr.getBorder(event.getBlock().getWorld());
		
		Block from = event.getBlock();
		if(border.isLastBlock(from))
		{
			event.setCancelled(true);
			from.setType(Material.AIR);
		}
		
		Block to = event.getToBlock();
		if(border.isLastBlock(to))
		{
			event.setCancelled(true);
			to.setType(Material.AIR);
		}
	}
	
	
	// Check player for being near plot edge (causes world loading errors) If so, teleport to safe location
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if(disableProtection)
			return;
		
		Location to = event.getTo();
		Border border = BorderMgr.getBorder(to.getWorld());
		
		Location newLocation = border.getSafe(to);
		if(newLocation != null)
			event.setTo(newLocation);
			// schedule teleport back to position where logged in in one tick/second
			// maybe do the same with other entities?
			// --- nope, added buffer chunk
	}
}