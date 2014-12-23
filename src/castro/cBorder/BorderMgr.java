/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;

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
		
		// Check if we had a border from previous cBorder version
		OldBorder oldBorder = Config.getOldBorder(worldName);
		if(oldBorder != null)
		{
			setBorderImpl(wb, oldBorder.size, oldBorder.centerX, oldBorder.centerZ);
			Config.removeWorld(worldName);
		}
		
		// Disallow too big worlds 100k blocks is really more than enough
		if(wb.getSize() > 100000.d)
			setBorderImpl(wb, 100000, wb.getCenter().getX(), wb.getCenter().getZ());
		
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
		
		setBorderImpl(wb, size, centerX, centerZ);
		
		Config.removeWorld(world.getName()); // TODO: remove later
	}
	
	private static void setBorderImpl(WorldBorder wb, double size, double centerX, double centerZ)
	{
		wb.setSize(size);
		wb.setCenter(centerX, centerZ);
		wb.setWarningDistance(0);
	}
}
