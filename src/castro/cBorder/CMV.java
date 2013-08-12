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

import org.bukkit.plugin.PluginManager;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class CMV
{
	private static MultiverseCore multiverse;
	
	public CMV(Plugin plugin)
	{
		PluginManager PM = plugin.getServer().getPluginManager();
		multiverse	= (MultiverseCore)PM.getPlugin("Multiverse-Core");
	}
	
	public static MVWorldManager getMVMgr()
	{
		return multiverse.getMVWorldManager();
	}
	
	
	public static boolean unloadWorld(String world)
	{
		return getMVMgr().unloadWorld(world);
	}
	
	
	public static boolean loadWorld(String world)
	{
		return getMVMgr().loadWorld(world);
	}
	
	
	public static boolean regenWorldWithRandomSeed(String world)
	{
		return regenWorld(world, true, true, null);
	}
	public static boolean regenWorld(String world, boolean useNewSeed, boolean randomSeed, String seed)
	{
		return getMVMgr().regenWorld(world, useNewSeed, randomSeed, seed);
	}
}
