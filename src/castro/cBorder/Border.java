/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorldBorder;


public class Border
{
	private final CraftWorldBorder handle;
	private final net.minecraft.server.v1_8_R1.WorldBorder nmsHandle;
	
	public Border(World world)
	{
		CraftWorld cw = (CraftWorld)world;
		this.handle = (CraftWorldBorder)cw.getWorldBorder();
		this.nmsHandle = cw.getHandle().af();
	}
	
	public boolean isInside2(Location loc)
	{
		return nmsHandle.isInBounds(loc.getBlockX(), loc.getBlockZ());
	}
	
	public boolean isInside2(int x, int z)
	{
		return nmsHandle.isInBounds(x, z);
	}
	
	public boolean isOutside(Location loc)
	{
		return !nmsHandle.isInBounds(loc.getBlockX(), loc.getBlockZ()); 
	}
	
	public boolean isOutside(int x, int z)
	{
		return !nmsHandle.isInBounds(x, z);
	}
	
	public int getSize()
	{
		return (int)Math.ceil(handle.getSize());
	}
	
	public int getCenterX()
	{
		return handle.getCenter().getBlockX();
	}
	
	public int getCenterZ()
	{
		return handle.getCenter().getBlockZ();
	}
	
	public Location getCenter()
	{
		return handle.getCenter();
	}
}
