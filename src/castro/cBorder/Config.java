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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
	protected static Plugin plugin = Plugin.get();
	private static FileConfiguration con = plugin.getConfig();
	private static ConfigurationSection worldsSection = con.getConfigurationSection("worlds");
	
	
	public static void saveConfig()
	{
		plugin.saveConfig();
	}
	
	
	public static void setBorder(String world, Border border)
	{
		set(world+".radius",  border.radius);
		set(world+".offsetX", border.centerX);
		set(world+".offsetZ", border.centerZ);
	}
	
	
	public static void removeWorld(String world)
	{
		set(world, null);
	}
	
	
	private static void set(String key, Object value)
	{
		worldsSection.set(key, value);
		saveConfig();
	}
	
	
	private static Border getBorder(String world)
	{
		return new Border(
				worldsSection.getInt(world+".radius"),
				worldsSection.getInt(world+".offsetX"),
				worldsSection.getInt(world+".offsetZ"));
	}
	
	
	public static HashMap<String, Border> getAllBorders()
	{
		HashMap<String, Border> borders = new HashMap<String, Border>();
		
		Set<String> worlds = worldsSection.getKeys(false);
		for(String world : worlds)
			borders.put(world, getBorder(world));
		
		return borders;
	}
	
	
	public static boolean protectionDisabled()
	{
		return !con.getBoolean("protection");
	}
}