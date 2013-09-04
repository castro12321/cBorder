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

import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
	protected static Plugin plugin;// = Plugin.get();
	private static FileConfiguration con;// = plugin.getConfig();
	
	
	public Config()
	{
		plugin = Plugin.get();
		con = plugin.getConfig();
		
		checkSet("bounce", true);
		checkSet("protection", true);
		saveConfig();
	}
	
	
	private void checkSet(String key, Object value)
	{
		if(!con.isSet(key))
			set(key, value);
	}
	
	
	public static void saveConfig()
	{
		plugin.saveConfig();
	}
	
	
	public static void setBorder(String world, Border border)
	{
		set("worlds."+world+".radiusX", border.radiusX);
		set("worlds."+world+".radiusZ", border.radiusZ);
		set("worlds."+world+".offsetX", border.centerX);
		set("worlds."+world+".offsetZ", border.centerZ);
		saveConfig();
	}
	
	
	public static void removeWorld(String world)
	{
		set("worlds."+world, null);
		saveConfig();
	}
	
	
	private static void set(String key, Object value)
	{
		con.set(key, value);
		//worldsSection.set(key, value);
	}
	
	
	private static Border getBorder(String world)
	{
		int radiusX, radiusZ, offsetX, offsetZ;
		radiusX = con.getInt("worlds."+world+".radiusX");
		radiusZ = con.getInt("worlds."+world+".radiusZ");
		offsetX = con.getInt("worlds."+world+".offsetX");
		offsetZ = con.getInt("worlds."+world+".offsetZ");
		
		if(con.contains("worlds."+world+".radius"))
		{
			radiusX = radiusZ = con.getInt("worlds."+world+".radius");
			con.set("worlds."+world+".radius", null);
			Border border = new Border(radiusX, radiusZ, offsetX, offsetZ);
			setBorder(world, border);
			return border;
		}
		
		return new Border(radiusX, radiusZ, offsetX, offsetZ);
	}
	
	
	public static HashMap<String, Border> getAllBorders()
	{
		HashMap<String, Border> borders = new HashMap<String, Border>();
		
		Set<String> worlds = con.getConfigurationSection("worlds").getKeys(false);
		for(String world : worlds)
			borders.put(world, getBorder(world));
		
		return borders;
	}
	
	
	public static boolean protectionDisabled()
	{
		return !con.getBoolean("protection");
	}
	
	
	public static boolean bouncePlayers()
	{
		return con.getBoolean("bounce");
	}
}