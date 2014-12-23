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
		//if(action.equals("info"))
			//return info();
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
			int size, offsetX = 0, offsetZ = 0;
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
			case 4:
				offsetZ	= Integer.valueOf(args[4]);
			case 3:
				offsetX	= Integer.valueOf(args[3]);
			case 2:
				size = Integer.valueOf(args[1]);
				world = args[2];
				
				break;
			case 1:
				size = Integer.valueOf(args[1]);
				break;
			case 0:
				return !plugin.sendMessage(sender, "You have passed not enough parameters");
			}
			
			int borderLimit = 100000;
			if(size < 0 || size > borderLimit
			|| offsetX > borderLimit || offsetX < -borderLimit
			|| offsetZ > borderLimit || offsetZ < -borderLimit)
				return !plugin.sendMessage(sender, "You have passed illegal values");
			
			World w = Bukkit.getWorld(world);
			if(w == null)
				return false;
			return setBorder(sender, w, size, offsetX, offsetZ);
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
		BorderMgr.setNewBorder(target, 100000, 0, 0);
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
		
		World world = player.getWorld();
		Location minX = new Location(world, min.getX(), 0, 0);
		Location maxX = new Location(world, max.getX(), 0, 0);
		Location minZ = new Location(world, 0, 0, min.getZ());
		Location maxZ = new Location(world, 0, 0, max.getZ());
		double sizeX = minX.distance(maxX);
		double sizeZ = minZ.distance(maxZ);
		double size = Math.max(sizeX, sizeZ);
		
		return setBorder(sender, world, size, min.getX() + sizeX/2, min.getZ() + sizeZ/2);
	}
	
	private String doubleToStr(double val)
	{
		return Double.toString(val);
	}
	private boolean setBorder(CommandSender sender, World world, double size, double offsetX, double offsetZ)
	{
		if(world == null)
			return false;
		
		BorderMgr.setNewBorder(world, size, offsetX, offsetZ);
		String msg = "Ceated border for $world$ with size $size$ centered at $offsetX$, $offsetZ$";
		msg = msg.replace("$world$", world.getName())
			.replace("$radX$", doubleToStr(size))
			.replace("$offsetX$", doubleToStr(offsetX)).replace("$offsetZ$", doubleToStr(offsetZ));
		return plugin.sendMessage(sender, msg);
	}
	/*
	private boolean info()
	{
		if(player == null)
			return false;
		
		World world	= player.getWorld();
		Border border = BorderMgr.getNewBorder(world);
		
		plugin.sendMessage(sender, "Border info for world " + world.getName());
		plugin.sendMessage(sender, "center: " + border.centerX + " " + border.centerZ);
		plugin.sendMessage(sender, "radius: " + border.radiusX + " " + border.radiusZ);
		plugin.sendMessage(sender, "safe chunks: " + border.safeLowChunkX + " " + border.safeLowChunkZ + " - " + border.safeHighChunkX + " " + border.safeHighChunkZ);
		plugin.sendMessage(sender, "safe blocks: " + border.safeLowBlockX + " " + border.safeLowBlockZ + " - " + border.safeHighBlockX + " " + border.safeHighBlockZ);
		
		return true;
	}
	*/
}
