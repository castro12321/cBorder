/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import castro.base.plugin.CPlugin;
import castro.base.plugin.CPluginSettings;
import castro.cBorder.listeners.BorderListener;
import castro.cBorder.listeners.ProtectionListener;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Plugin extends CPlugin
{
	protected static Plugin instance;
	private static CommandMgr commandMgr;
	
	
	WorldEditPlugin getWorldEdit()
	{
		return (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
	}
	
	
	@Override
	protected CPluginSettings getSettings()
	{
		instance = this;
		
		CPluginSettings settings = new CPluginSettings();
		settings.useConfig = true;
		return settings;
	}
	
	
	@Override
	public void init()
	{
		commandMgr = new CommandMgr();
		new Config();
		
		BorderMgr.init();
		
		scheduleSyncRepeatingTask(new EntitiesCleaner(), 20, 20);
		registerEvents(new ProtectionListener());
		registerEvents(new BorderListener());
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return commandMgr.onCommand(sender, cmd, args);
	}
	
	
	public static Plugin get()
	{
		return instance;
	}
}
