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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castro.base.GenericCommandMgr;

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
			return !plugin.sendMessage(sender, "&cBrak uprawnien");
		if(sender instanceof Player)
			player = (Player)sender;
		
		String action = args[0].toLowerCase();
		if(action.equals("set"))
			return set();
		if(action.equals("remove"))
			return remove();
		return false;
	}
	
	
	// cb set <radius> [world] [offsetX] [offsetZ]
	private boolean set()
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
		
		if(world == null)
			return false;
		
		BorderMgr.setBorder(world, new Border(radius, offsetX, offsetZ));
		return plugin.sendMessage(sender, "Created border for " + world + " with radius " + radius + " at chunk " + offsetX + ", " + offsetZ);
	}
	
	
	private boolean remove()
	{
		String target = player.getWorld().getName();
		if(args.length > 1)
			target = args[1];
		BorderMgr.removeBorder(target);
		return plugin.sendMessage(sender, "Removed border for " + target);
	}
}
