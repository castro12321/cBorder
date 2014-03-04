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

package castro.cBorder.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import castro.cBorder.Border;
import castro.cBorder.BorderMgr;
import castro.cBorder.Config;
import castro.cBorder.Plugin;

public class WallListener implements Listener, Runnable
{
	private final int safeDistance    = 10;
	private boolean disableProtection = Config.protectionDisabled();
	private Set<Player> movedPlayers  = new HashSet<>();
	private Set<Player> nearWall      = new HashSet<>();
	private Set<Block>  walls         = new HashSet<>();
	
	
	public WallListener(Plugin plugin)
	{
		plugin.scheduleSyncRepeatingTask(this, 1*20, 1*20);
	}
	
	
	
	public void run()
	{
		removeWalls();
		addWalls();
	}
	
	
	private void removeWalls()
	{
		for(Block block : walls)
			block.setType(Material.AIR);
		walls.clear();
	}
	
	
	private void addWalls()
	{
		for(Player player : movedPlayers)
			if(isNearWall(player))
				nearWall.add(player);
		
		for(Player player : nearWall)
			if(isNearWall(player))
				addWalls(player);
			else
				nearWall.remove(player);
		
		movedPlayers.clear();
		
		for(Block block : walls)
			block.setType(Material.GLASS);
	}
	
	
	private void addWalls(Player player)
	{
		World  world  = player.getWorld();
		Border border = BorderMgr.getBorder(world);
		Location loc  = player.getLocation();
		
		// Math.abs(lowX) - Math.abs(playerX) < minDistance ???
		if(Math.abs(border.safeLowBlockX  + loc.getBlockX()) < safeDistance) addWallsX(world, border.safeLowBlockX , loc.getBlockY(), loc.getBlockZ());
		if(Math.abs(border.safeLowBlockZ  + loc.getBlockZ()) < safeDistance) addWallsZ(world, loc.getBlockX()      , loc.getBlockY(), border.safeLowBlockZ);
		if(Math.abs(border.safeHighBlockX - loc.getBlockX()) < safeDistance) addWallsX(world, border.safeHighBlockX, loc.getBlockY(), loc.getBlockZ());
		if(Math.abs(border.safeHighBlockZ - loc.getBlockZ()) < safeDistance) addWallsZ(world, loc.getBlockX()      , loc.getBlockY(), border.safeHighBlockZ);
	}
	
	
	private void addWallsX(World world, int x, int y, int z)
	{
		addWalls(world, x, z-3, x, z+3, y);
	}
	
	
	private void addWallsZ(World world, int x, int y, int z)
	{
		addWalls(world, x-3, z, x+3, z, y);
	}
	
	
	private void addWalls(World world, int lowX, int lowZ, int highX, int highZ, int centerY)
	{
		for(int y = centerY-3; y < centerY+3; ++y)
			for(int x = lowX; x < highX; ++x)
				for(int z = lowZ; z < highZ; ++z)
				{
					Block block = world.getBlockAt(x, y, z);
					if(block.getType() == Material.AIR)
						walls.add(block);
				}
	}
	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if(disableProtection)
			return;
		
		movedPlayers.add(event.getPlayer());
	}
	
	
	private boolean isNearWall(Player player)
	{
		Border border = BorderMgr.getBorder(player.getWorld());
		Location loc  = player.getLocation();
		
		if(Math.abs(border.safeLowBlockX  + loc.getBlockX()) < safeDistance)
			return true;
		if(Math.abs(border.safeLowBlockZ  + loc.getBlockZ()) < safeDistance)
			return true;
		if(Math.abs(border.safeHighBlockX - loc.getBlockX()) < safeDistance)
			return true;
		if(Math.abs(border.safeHighBlockZ - loc.getBlockZ()) < safeDistance)
			return true;
		return false;
	}
}