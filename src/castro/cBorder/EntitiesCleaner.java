/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntitiesCleaner implements Runnable
{
	Plugin  plugin = Plugin.get();
	
	public void run()
	{
		List<World> worlds = plugin.getServer().getWorlds();
		for(World world : worlds)
			cleanWorld(world);
	}
	
	private void cleanWorld(World world)
	{
		final Border border = BorderMgr.getNewBorder(world);
		List<Player> entities = world.getPlayers();
		for(final Player player : entities)
		{
			Location loc = player.getLocation();
			if(border.isOutside(loc))
			{
				Entity vehicle = player.getVehicle();
				if(vehicle != null)
					vehicle.eject();
				plugin.scheduleSyncDelayedTask(
					new Runnable() {
						@Override public void run() {
							player.teleport(border.getCenterHighest());
						}
					}
				, 100);
				plugin.sendMessage(player, "&cYou have passed the border of this world");
			}
		}
	}
}
