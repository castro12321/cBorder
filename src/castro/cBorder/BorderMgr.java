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
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;


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
	
	
	public static void setBorder(String worldname, Border newBorder)
	{		
		if(newBorder.equals(getBorder(worldname)))
			return;
		
		Config.setBorder(worldname, newBorder);
		limits.put(worldname, newBorder);
		
		refreshChunks(worldname, newBorder);
	}
	
	
	public static void removeBorder(String world)
	{
		if(limits.containsKey(world))
		{
			limits.remove(world);
			Config.removeWorld(world);
		}
	}
	
	
	/**
	 * Teleports player to other map and teleports him back
	 * @param player
	 */
	private static class TeleportBack implements Runnable
	{
		Player player;
		Location back;
		
		TeleportBack(Player player, Location back)
		{
			this.player = player;
			this.back = back;
		}
		
		@Override
		public void run()
		{
			player.teleport(back);
		}
	}
	
	
	private static void refreshChunks(String worldname, Border border)
	{
		World world = Plugin.get().getServer().getWorld(worldname);
		if(world == null)
			return;
		
		Chunk[] loadedChunks = world.getLoadedChunks();
		for(Chunk chunk : loadedChunks)
			if(border.isOutsideLimit(chunk))
				chunk.unload(true, false);
		
		for(Player player : world.getPlayers())
			refreshChunks(player);
	}
	
	
	private static void refreshChunks(Player player)
	{
		Plugin plugin = Plugin.get();
		Server server = plugin.getServer();
		
		Location oldLocation = player.getLocation();
		World tmpWorld = server.getWorlds().get(0);
		if(tmpWorld.equals(oldLocation.getWorld()))
			tmpWorld = server.getWorlds().get(1);
		Location tmpLocation = tmpWorld.getSpawnLocation();
		
		player.teleport(tmpLocation);
		
		TeleportBack teleportBack = new TeleportBack(player, oldLocation);
		server.getScheduler().scheduleSyncDelayedTask(plugin, teleportBack);
	}
}
