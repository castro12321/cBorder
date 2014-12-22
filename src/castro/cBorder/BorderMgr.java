/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Player;


public class BorderMgr
{
	private static HashMap<String, Border> limits = new HashMap<String, Border>();
	
	public static void init()
	{
		limits = Config.getAllBorders();
	}
	
	private static WorldBorder getBorderFromWorld(World world)
	{
		CraftWorld cw = (CraftWorld)world;
		return cw.getWorldBorder();
	}
	
	private static Border fromWorldBorder(WorldBorder wb)
	{
		int radius = (int)Math.ceil(wb.getSize()/2);
		return new Border(radius, radius, (int)Math.round(wb.getCenter().getX()), (int)Math.round(wb.getCenter().getX()));
	}
	
	public static Border getBorder(World world)
	{
		if(world == null)
			return null;
		WorldBorder wb = getBorderFromWorld(world);
		if(wb.getSize() > 100001.d)
		{
			Border oldBorder = limits.get(world.getName());
			if(oldBorder != null)
				setBorder(world, oldBorder);
			else
				setBorder(world, new Border(50000, 50000, (int)Math.round(wb.getCenter().getX()), (int)Math.round(wb.getCenter().getX())));
		}
		return fromWorldBorder(wb);
	}
	
	public static void setBorder(World world, Border newBorder)
	{
		WorldBorder wb = getBorderFromWorld(world);
		Border oldBorder = fromWorldBorder(wb);
		if(newBorder.equals(oldBorder))
			return;
		
		int size = 2 * Math.max(newBorder.radiusX, newBorder.radiusZ);
		if(size > 100000)
			size = 100000;
		wb.setSize(size);
		wb.setCenter(newBorder.centerX, newBorder.centerZ);
		wb.setWarningDistance(0);
		
		refreshChunks(world, newBorder);
		
		// TODO: remove later
		String worldname = world.getName();
		if(limits.containsKey(worldname))
		{
			limits.remove(worldname);
			Config.removeWorld(worldname);
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
	
	
	private static void refreshChunks(World world, Border border)
	{
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
		server.getScheduler().scheduleSyncDelayedTask(plugin, teleportBack, 50);
	}
}
