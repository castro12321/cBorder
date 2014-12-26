/* cBorder
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.cBorder;


public class Config
{
	protected static Plugin plugin;
	//private static FileConfiguration con;
	
	public Config()
	{
		plugin = Plugin.get();
		//con = plugin.getConfig();
	}
	
	/*
	public static void removeWorld(String world)
	{
		con.set("worlds."+world, null);
		plugin.saveConfig();
	}
	*/
	
	/*
	public static OldBorder getOldBorder(String world)
	{
		if(!con.contains("worlds." + world))
			return null;
		int radiusX = con.getInt("worlds."+world+".radiusX");
		int radiusZ = con.getInt("worlds."+world+".radiusZ");
		int offsetX = 8 + (16 * con.getInt("worlds."+world+".offsetX"));
		int offsetZ = 8 + (16 * con.getInt("worlds."+world+".offsetZ"));
		int size = 16 * Math.max(radiusX, radiusZ);
		if(con.contains("worlds."+world+".radius"))
			radiusX = radiusZ = con.getInt("worlds."+world+".radius");
		return new OldBorder(size, offsetX, offsetZ);
	}
	*/
}