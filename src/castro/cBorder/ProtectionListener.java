/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ProtectionListener implements Listener
{
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Location to = event.getTo();
		World toWorld = to.getWorld();
		Border border = BorderMgr.getNewBorder(toWorld);
		if(border.isOutside(to))
		{
			Location highest = border.getCenterHighest();
			event.setTo(highest);
			Plugin.get().log("Setting opt to " + highest);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) // We have to override MultiVerse spawning
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Location to = event.getRespawnLocation();
		World toWorld = to.getWorld();
		Border border = BorderMgr.getNewBorder(toWorld);
		if(border.isOutside(to))
			event.setRespawnLocation(border.getCenterHighest());
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent event)
	{
		Location loc = event.getLocation();
		Border border = BorderMgr.getNewBorder(loc.getWorld());
		if(border.isOutside(loc))
			event.setCancelled(true);
	}
}