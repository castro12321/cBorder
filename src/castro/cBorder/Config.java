/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

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
	}
	
	private void checkSet(String key, Object value)
	{
		if(!con.isSet(key))
			set(key, value);
		saveConfig();
	}
	
	public static void saveConfig()
	{
		plugin.saveConfig();
	}
	
	public static void removeWorld(String world)
	{
		set("worlds."+world, null);
		saveConfig();
	}
	
	private static void set(String key, Object value)
	{
		con.set(key, value);
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
			return new Border(radiusX, radiusZ, offsetX, offsetZ);
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
	
	public static boolean protection()
	{
		return con.getBoolean("protection");
	}
	
	public static boolean bounce()
	{
		return con.getBoolean("bounce");
	}
	
	public static int additionalSafeChunks()
	{
		return con.getInt("additional-safe-chunks");
	}
}