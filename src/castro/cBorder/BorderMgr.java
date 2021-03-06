/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Player;

public class BorderMgr
{
	private static HashMap<String, Border> borderCache = new HashMap<>();
	
	static void init(Plugin plugin)
	{
		// clear cache every 10 minutes
		plugin.scheduleSyncRepeatingTask(new Runnable()
		{
			@Override public void run() { borderCache.clear(); }
		}
		, 600*20, 600*20);
	}
	
	private static WorldBorder getBorderFromWorld(World world)
	{
		CraftWorld cw = (CraftWorld)world;
		return cw.getWorldBorder();
	}
	
	public static Border getNewBorder(World world)
	{
		if(world == null)
			return null;
		String worldName = world.getName();
		Border cached = borderCache.get(worldName);
		if(cached != null)
			return cached;
		
		WorldBorder wb = getBorderFromWorld(world);
		
		/*
		// Check if we had a border from previous cBorder version
		OldBorder oldBorder = Config.getOldBorder(worldName);
		if(oldBorder != null)
		{
			setBorderImpl(wb, oldBorder.size, oldBorder.centerX, oldBorder.centerZ);
			Config.removeWorld(worldName);
		}
		*/
		
		// Disallow too big worlds 100k blocks is really more than enough
		if(wb.getSize() > 100000.d)
			setBorderImpl(world, wb, 320, world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
		
		Border border = new Border(world);
		borderCache.put(worldName, border);
		return border;
	}
	
	public static void setNewBorder(World world, double size, double centerX, double centerZ)
	{
		if(size > 100000.d)
			size = 100000.d;
		
		WorldBorder wb = getBorderFromWorld(world);
		// Change only if there is difference
		if(wb.getSize() == size
		&& wb.getCenter().getX() == centerX
		&& wb.getCenter().getZ() == centerZ)
			return;
		
		setBorderImpl(world, wb, size, centerX, centerZ);
		
		for(Player player : world.getPlayers())
			refreshChunks(player);
		
		//Config.removeWorld(world.getName()); // TODO: remove later
	}
	
	private static void setBorderImpl(World world, WorldBorder wb, double size, double centerX, double centerZ)
	{
		Plugin.get().log(world.getName() + "Setting border " + size + "; " + centerX + "; " + centerZ);
		wb.setSize(size);
		wb.setCenter(centerX, centerZ);
		wb.setWarningDistance(0);
		borderCache.put(world.getName(), new Border(world));
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
		server.getScheduler().scheduleSyncDelayedTask(plugin, teleportBack, 50);
	}
}
