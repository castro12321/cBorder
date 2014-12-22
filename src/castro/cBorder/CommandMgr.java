/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.Bukkit;
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
		if(action.equals("trimallworlds"))
			return new CommandTrim(player).runCommand();
		return false;
	}
	
	
	// cb set <radius> [world] [offsetX] [offsetZ]
	private boolean set()
	{
		try
		{
			String world = null;
			int radiusX, radiusZ, offsetX = 0, offsetZ = 0;
			if(player != null)
			{
				world = player.getWorld().getName();
				
				Chunk chunk = player.getLocation().getChunk();
				offsetX = chunk.getX();
				offsetZ = chunk.getZ();
			}
			
			switch(args.length)
			{
			// /cb set radiusX radiusZ worldname centerX centerZ
			//     <0>   <1>     <2>       <3>      <4>    <5>
			default:
			case 6:
				offsetZ	= Integer.valueOf(args[5]);
			case 5:
				offsetX	= Integer.valueOf(args[4]);
			case 4:
				world = args[3];
			case 3:
				radiusX = Integer.valueOf(args[1]);
				try
				{
					radiusZ = Integer.valueOf(args[2]);
				}
				catch(NumberFormatException e)
				{
					radiusZ = radiusX;
					world = args[2];
				}
				
				break;
			case 2:
				radiusX = radiusZ = Integer.valueOf(args[1]);
				break;
			case 1:
			case 0:
				return !plugin.sendMessage(sender, "You have passed not enough parameters");
			}
			
			int borderLimit = 3000000;
			if(radiusX < 0			 || radiusX > borderLimit
			|| radiusZ < 0			 || radiusZ > borderLimit
			|| offsetX > borderLimit || offsetX < -borderLimit
			|| offsetZ > borderLimit || offsetZ < -borderLimit)
				return !plugin.sendMessage(sender, "You have passed illegal values");
			
			return setBorder(sender, world, radiusX, radiusZ, offsetX, offsetZ);
		}
		catch(NumberFormatException e)
		{
			return !plugin.sendMessage(sender, "You have passed illegal values");
		}
	}
	
	
	private boolean remove()
	{
		World target = player.getWorld();
		if(args.length > 1)
			target = Bukkit.getWorld(args[1]);
		if(target == null)
			return false;
		BorderMgr.setBorder(target, new Border(3125, 3125, 0, 0));
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
		
		String world = player.getWorld().getName();
		
		return setBorder(sender, world, radiusX, radiusZ, offsetX, offsetZ);
	}
	
	private String intToStr(int val)
	{
		return Integer.toString(val);
	}
	private boolean setBorder(CommandSender sender, String worldname, int radiusX, int radiusZ, int offsetX, int offsetZ)
	{
		if(worldname == null)
			return false;
		World world = Bukkit.getWorld(worldname);
		if(world == null)
			return false;
		
		BorderMgr.setBorder(world, new Border(radiusX, radiusZ, offsetX, offsetZ));
		String msg = "Ceated border for $world$ with radius $radX$, $radZ$ at chunk $chunkX$, $chunkZ$";
		msg = msg.replace("$world$", worldname)
			.replace("$radX$", intToStr(radiusX)).replace("$radZ$", intToStr(radiusZ))
			.replace("$chunkX$", intToStr(offsetX)).replace("$chunkZ$", intToStr(offsetZ));
		return plugin.sendMessage(sender, msg);
	}
	
	
	private boolean info()
	{
		if(player == null)
			return false;
		
		World world	= player.getWorld();
		Border border = BorderMgr.getBorder(world);
		
		plugin.sendMessage(sender, "Border info for world " + world.getName());
		plugin.sendMessage(sender, "center: " + border.centerX + " " + border.centerZ);
		plugin.sendMessage(sender, "radius: " + border.radiusX + " " + border.radiusZ);
		plugin.sendMessage(sender, "safe chunks: " + border.safeLowChunkX + " " + border.safeLowChunkZ + " - " + border.safeHighChunkX + " " + border.safeHighChunkZ);
		plugin.sendMessage(sender, "safe blocks: " + border.safeLowBlockX + " " + border.safeLowBlockZ + " - " + border.safeHighBlockX + " " + border.safeHighBlockZ);
		
		return true;
	}
}
