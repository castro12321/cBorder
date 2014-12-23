/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;

public class BorderMgr
{
	private static WorldBorder getBorderFromWorld(World world)
	{
		CraftWorld cw = (CraftWorld)world;
		return cw.getWorldBorder();
	}
	
	public static Border getNewBorder(World world)
	{
		if(world == null)
			return null;
		WorldBorder wb = getBorderFromWorld(world);
		
		// Check if we had a border from previous cBorder version
		OldBorder oldBorder = Config.getOldBorder(world.getName());
		if(oldBorder != null)
			setBorderImpl(wb, oldBorder.size, oldBorder.centerX, oldBorder.centerZ);
		else
			setBorderImpl(wb, 100000, wb.getCenter().getX(), wb.getCenter().getZ());
		
		// Disallow too big worlds 100k blocks is really more than enough
		if(wb.getSize() > 100000.d)
			setBorderImpl(wb, 100000, wb.getCenter().getX(), wb.getCenter().getZ());
		
		return new Border(world);
	}
	
	public static void setNewBorder(World world, double size, double centerX, double centerZ)
	{
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
