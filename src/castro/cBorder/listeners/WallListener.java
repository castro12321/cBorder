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
	private final int SAFE_DISTANCE   = 12;
	private Set<Player> movedPlayers  = new HashSet<>();
	private Set<Player> nearWall      = new HashSet<>();
	private Set<Block>  walls         = new HashSet<>();
	
	
	public WallListener(Plugin plugin)
	{
		plugin.scheduleSyncRepeatingTask(this, 1*10, 1*10);
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
		for(Player player : nearWall)
			if(!addWallsIfNeeded(player))
				nearWall.remove(player);
		
		movedPlayers.removeAll(nearWall);
		
		for(Player player : movedPlayers)
			if(addWallsIfNeeded(player))
				nearWall.add(player);
		movedPlayers.clear();
	}
	
	
	private boolean addWallsIfNeeded(Player player)
	{
		World  world   = player.getWorld();
		Border border  = BorderMgr.getBorder(world);
		Location loc   = player.getLocation();
		
		boolean createdWalls = false;
		if(loc.getBlockX() < border.safeLowBlockX + SAFE_DISTANCE)
		{
			addWallsX(world, border.safeLowBlockX, loc.getBlockY(), loc.getBlockZ());
			createdWalls = true;
		}
		else if(loc.getBlockX() > border.safeHighBlockX - SAFE_DISTANCE)
		{
			addWallsX(world, border.safeHighBlockX, loc.getBlockY(), loc.getBlockZ());
			createdWalls = true;
		}
		
		if(loc.getBlockZ() < border.safeLowBlockZ + SAFE_DISTANCE)
		{
			addWallsZ(world, loc.getBlockX(), loc.getBlockY(), border.safeLowBlockZ);
			createdWalls = true;
		}
		else if(loc.getBlockZ() > border.safeHighBlockZ - SAFE_DISTANCE)
		{
			addWallsZ(world, loc.getBlockX(), loc.getBlockY(), border.safeHighBlockZ);
			createdWalls = true;
		}
		return createdWalls;
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
		for(int y = centerY-2; y <= centerY+4; ++y)
			for(int x = lowX; x <= highX; ++x)
				for(int z = lowZ; z <= highZ; ++z)
				{
					Block block = world.getBlockAt(x, y, z);
					if(block.getType() == Material.AIR
					|| block.getType() == Material.GLASS)
					{
						walls.add(block);
						block.setType(Material.GLASS);
					}
				}
	}
	
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if(Config.protectionDisabled())
			return;
		movedPlayers.add(event.getPlayer());
	}
}