/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;


public class EntitiesCleaner implements Runnable
{
	Plugin  plugin = Plugin.get();
	boolean bounce = Config.bounce();
	
	public void run()
	{
		if(!Config.protection())
			return;
		
		List<World> worlds = plugin.getServer().getWorlds();
		for(World world : worlds)
			cleanWorld(world);
	}
	
	
	private void cleanWorld(World world)
	{
		Border border = BorderMgr.getBorder(world);
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities)
		{
			Location newSafe = border.getSafe(entity.getLocation());
			if(newSafe != null)
			{
				if(entity instanceof Hanging
				|| entity instanceof Vehicle)
				{
					entity.eject();  // In case entity had passengers
					entity.remove(); // Those entities are a bit laggy so remove them
				}
				else
					bounce(entity, world);
			}
		}
	}
	
	
	private void bounce(Entity entity, World world)
	{
		Border border = BorderMgr.getBorder(world);
		Location newSafe = border.getSafe(entity.getLocation());
		if(newSafe != null)
		{
			entity.leaveVehicle(); // We can't teleport entity if it's in vehicle
			
			if(entity instanceof Player)
			{						
				if(!bounce)
					return;
				plugin.sendMessage((Player)entity, "&cYou have passed the border of this world");
			}
			entity.teleport(newSafe);
		}
	}
}
