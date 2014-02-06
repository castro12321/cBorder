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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import castro.base.plugin.CPlugin;
import castro.base.plugin.CPluginSettings;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;


// TODO: Store unloaded chunks in array and then reload them after limit change
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
		
		final int second = 20;
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new EntitiesCleaner(), second, 1*second);
		
		PluginManager PM = getServer().getPluginManager();
		PM.registerEvents(new ProtectionListener(), this);
		PM.registerEvents(new BorderListener(), this);
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
