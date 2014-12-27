/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;


public class Border
{
	private final WorldBorder handle;
	//private final net.minecraft.server.v1_8_R1.WorldBorder nmsHandle;
	
	public Border(World world)
	{
		CraftWorld cw = (CraftWorld)world;
		this.handle = cw.getWorldBorder();
		//this.nmsHandle = cw.getHandle().af();
	}
	
	public boolean isInside(Location loc)
	{
		return isInside(loc.getBlockX(), loc.getBlockZ());
	}
	
	public boolean isInside(int x, int z)
	{
		Location center = getCenter();
		final int centerX = center.getBlockX();
		final int centerZ = center.getBlockZ();
		final int radius = 2 + (getSize() / 2);
		
		return
		   x < centerX + radius
		&& x > centerX - radius
		&& z < centerZ + radius
		&& z > centerZ - radius;
		
		//return nmsHandle.isInBounds(x >> 4, z >> 4);
	}
	
	public boolean isOutside(Location loc)
	{
		return !isInside(loc); 
	}
	
	public boolean isOutside(int x, int z)
	{
		return !isInside(x, z);
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
	
	public Location getCenterHighest()
	{
		Location center = getCenter();
		center.getChunk().load();
		World world = center.getWorld();
		return new Location(world, center.getBlockX(), world.getHighestBlockYAt(center), center.getBlockZ());
	}
}
