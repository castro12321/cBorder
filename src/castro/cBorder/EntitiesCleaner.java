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

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;


public class EntitiesCleaner implements Runnable
{
	Plugin plugin = Plugin.get();
	boolean bounce = Config.bouncePlayers();
	
	public void run()
	{
		if(Config.protectionDisabled())
			return;
		
		List<World> worlds = plugin.getServer().getWorlds();
		for(World world : worlds)
			cleanWorld(world);
	}
	
	
	private void cleanWorld(World world)
	{
		List<Entity> entities = world.getEntities();
		for(Entity entity : entities)
		{
			if(entity instanceof Painting
			|| entity instanceof ItemFrame
			|| entity instanceof Vehicle)
			{
				entity.eject();  // In case entity had passengers
				entity.remove(); // Those entities are a bit laggy so remove them
			}
			else
				bounce(entity, world);
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
