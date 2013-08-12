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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BorderMgr
{
	private static HashMap<String, Border> limits = new HashMap<String, Border>();
	private static Border noBorder = new NoBorder();
	
	public static void init()
	{
		limits = Config.getAllBorders();
	}
	
	
	public static boolean contains(String world)
	{
		return limits.containsKey(world);
	}
	
	
	public static Border getBorder(World world)
	{ return getBorder(world.getName()); }
	public static Border getBorder(String world)
	{
		Border border = limits.get(world);
		if(border == null)
			return noBorder;
		return border;
	}
	
	
	public static void setBorder(String world, Border border)
	{
		if(border.equals(getBorder(world)))
			return;
		
		limits.put(world, border);
		Config.setBorder(world, border);
	}
	
	
	public static void removeBorder(String world)
	{
		if(limits.containsKey(world))
		{
			limits.remove(world);
			Config.removeWorld(world);
		}
	}
	
	
	// Check player for being near plot edge (causes world loading errors) If so, teleport to safe location
	public static void checkPlayer(Player player, PlayerTeleportEvent event)
	{
		Location newLocation = getSafe(event.getTo());
		if(newLocation != null)
			event.setTo(newLocation);
			// schedule teleport back to position where logged in in one tick/second
			// maybe do the same with other entities?
			// --- nope, added buffer chunk
	}
	
	
	public static Location getSafe(Location from)
	{
		World world = from.getWorld();
		Border border = getBorder(world);
		
		int x, z, newX, newZ;
		x = newX = from.getBlockX();
		z = newZ = from.getBlockZ();
		
		if(x > border.safeHighBlockX) newX = border.safeHighBlockX-3;
		if(z > border.safeHighBlockZ) newZ = border.safeHighBlockZ-3;
		if(x < border.safeLowBlockX)  newX = border.safeLowBlockX+3;
		if(z < border.safeLowBlockZ)  newZ = border.safeLowBlockZ+3;	
		
		if(x != newX || z != newZ)
			return world.getHighestBlockAt(newX, newZ).getLocation();
		return null;
	}
}
