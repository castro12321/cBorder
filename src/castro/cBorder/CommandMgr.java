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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castro.base.GenericCommandMgr;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandMgr implements GenericCommandMgr 
{
	protected static Plugin plugin = Plugin.get();
	
	CommandSender sender;
	Player player;
	Command command;
	String[] args;
	
	
	public boolean onCommand(CommandSender sender, Command command, String[] args)
	{	
		this.sender = sender;
		this.command = command;
		this.args = args;
		
		if(args.length < 1)
			return false;
		if(!sender.hasPermission("cborder."+args[0]))
			return !plugin.sendMessage(sender, "&cYou don't have permission for this command");
		if(sender instanceof Player)
			player = (Player)sender;
		
		String action = args[0].toLowerCase();
		if(action.equals("set"))
			return set();
		if(action.equals("remove"))
			return remove();
		if(action.equals("selected"))
			return selected();
		if(action.equals("info"))
			return info();
		return false;
	}
	
	
	// cb set <radius> [world] [offsetX] [offsetZ]
	private boolean set()
	{
		try
		{
			String world = null;
			int radius, offsetX = 0, offsetZ = 0;
			if(player != null)
			{
				world = player.getWorld().getName();
				
				Chunk chunk = player.getLocation().getChunk();
				offsetX = chunk.getX();
				offsetZ = chunk.getZ();
			}
			
			switch(args.length)
			{
			default:
			case 5:
				offsetZ	= Integer.valueOf(args[4]);
			case 4:
				offsetX	= Integer.valueOf(args[3]);
			case 3:
				world	= args[2];
			case 2:
				radius	= Integer.valueOf(args[1]);
				break;
			case 1:
			case 0:
				return !plugin.sendMessage(sender, "You have passed not enough parameters");
			}
			
			int borderLimit = 3000000;
			if( radius < 0			 || radius  > borderLimit
			|| offsetX > borderLimit || offsetX < -borderLimit
			|| offsetZ > borderLimit || offsetZ < -borderLimit)
				return !plugin.sendMessage(sender, "You have passed illegal values");
			
			return setBorder(sender, world, radius, offsetX, offsetZ);
		}
		catch(NumberFormatException e)
		{
			return !plugin.sendMessage(sender, "You have passed illegal values");
		}
	}
	
	
	private boolean remove()
	{
		String target = player.getWorld().getName();
		if(args.length > 1)
			target = args[1];
		BorderMgr.removeBorder(target);
		return plugin.sendMessage(sender, "Removed border for " + target);
	}
	
	
	private boolean selected()
	{
		if(player == null)
			return !plugin.sendMessage(sender, "&cOnly player can execute this command");
		
		WorldEditPlugin we = plugin.getWorldEdit();
		if(we == null)
			return plugin.sendMessage(sender, "&cWorldEdit is not installed on this server");
		
		Selection selection = we.getSelection(player);
		Location min = selection.getMinimumPoint();
		Location max = selection.getMaximumPoint();
		
		Chunk minChunk = min.getChunk();
		Chunk maxChunk = max.getChunk();
		
		int minX = minChunk.getX();
		int maxX = maxChunk.getX();
		int offsetX = (minX + maxX) / 2;
		int minRadiusX = offsetX - minX;
		int maxRadiusX = maxX - offsetX;
		int radiusX = Math.max(minRadiusX, maxRadiusX);
		
		int minZ = minChunk.getZ();
		int maxZ = maxChunk.getZ();
		int offsetZ = (minZ + maxZ) / 2;
		int minRadiusZ = offsetZ - minZ;
		int maxRadiusZ = maxZ - offsetZ;
		int radiusZ = Math.max(minRadiusZ, maxRadiusZ);
		
		int radius = Math.max(radiusX, radiusZ);
		
		String world = player.getWorld().getName();
		
		return setBorder(sender, world, radius, offsetX, offsetZ);
	}
	
	
	private boolean setBorder(CommandSender sender, String world, int radius, int offsetX, int offsetZ)
	{
		if(world == null)
			return false;
		
		BorderMgr.setBorder(world, new Border(radius, offsetX, offsetZ));
		return plugin.sendMessage(sender, "Created border for " + world + " with radius " + radius + " at chunk " + offsetX + ", " + offsetZ);
	}
	
	
	private boolean info()
	{
		if(player == null)
			return false;
		
		World world	= player.getWorld();
		Border border = BorderMgr.getBorder(world);
		
		plugin.sendMessage(sender, "Border info for world " + world.getName());
		plugin.sendMessage(sender, "center: " + border.centerX + " " + border.centerZ);
		plugin.sendMessage(sender, "radius: " + border.radius);
		plugin.sendMessage(sender, "safe c: " + border.safeLowChunkX + " " + border.safeLowChunkZ + " - " + border.safeHighChunkX + " " + border.safeHighChunkZ);
		
		return true;
	}
}
