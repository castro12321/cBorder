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
		Border border = BorderMgr.getNewBorder(world);
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities)
		{
			Location loc = entity.getLocation();
			if(border.isOutside(loc))
			{
				if(entity instanceof Player)
				{
					Player player = (Player)entity;
					player.teleport(border.getCenterHighest());
					plugin.sendMessage(player, "&cYou have passed the border of this world");
				}
				else
				{
					entity.eject();  // In case entity had passengers
					entity.remove(); // Those entities are a bit laggy so remove them
				}
			}
		}
	}
}
