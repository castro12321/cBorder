/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;


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
		return new Border(radius >> 4, radius >> 4, (int)Math.round(wb.getCenter().getX()) >> 4, (int)Math.round(wb.getCenter().getX()) >> 4);
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
				setBorder(world, new Border(3125, 3125, (int)Math.round(wb.getCenter().getX()) >> 4, (int)Math.round(wb.getCenter().getX()) >> 4));
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
		size *= 16; // Our size is in chunks, Mojang's is in blocks
		if(size > 100000)
			size = 100000;
		wb.setSize(size);
		wb.setCenter(newBorder.centerX << 4, newBorder.centerZ << 4);
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
	
	private static void refreshChunks(World world, Border border)
	{
		if(world == null)
			return;
		
		Chunk[] loadedChunks = world.getLoadedChunks();
		for(Chunk chunk : loadedChunks)
			if(border.isOutsideLimit(chunk))
				chunk.unload(true, false);
	}
}
