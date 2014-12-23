package castro.cBorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import castro.cWorlds.CPlot;
import castro.cWorlds.PlotsMgr;

public class CommandTrim implements Runnable
{
	private Plugin plugin = Plugin.get();
	private Player player;
	
	private List<String> worldsToTrim = new ArrayList<>();
	
	public CommandTrim(Player player)
	{
		this.player = player;
	}
	
	public boolean runCommand()
	{
		File[] worlds  = getWorldsDir().listFiles();
		for(File world : worlds)
			worldsToTrim.add(world.getName());
		worldsToTrim.add(0, worldsToTrim.get(0)); // double the 0th element because it'll be removed below
		
		plugin.scheduleSyncRepeatingTask(this, 1, 1);
		
		return true;
	}
	
	public void run()
	{
		if(worldsToTrim.size() == 0)
			return;
		
		// Check if there is a trim task running already
		if (com.wimbli.WorldBorder.Config.trimTask != null
		&&  com.wimbli.WorldBorder.Config.trimTask.valid())
		{
			plugin.log("Currently trimming " + worldsToTrim.get(0));
			return;
		}
		
		String previousWorld = worldsToTrim.remove(0);
		CPlot previousPlot = PlotsMgr.get(previousWorld);
		previousPlot.unload();
		plugin.log("Finished! " + previousWorld);
		
		String nextWorldName = worldsToTrim.get(0);
		plugin.log("Preparing to trim: " + nextWorldName);
		
		CPlot plot = PlotsMgr.get(nextWorldName);
		if(plot == null)
		{
			plugin.log("[cbError] Cannot load plot: " + nextWorldName);
			plugin.sendMessage(player, "[cbError] Cannot load plot: " + nextWorldName);
			return;
		}
		
		plot.load();
		World nextWorld = plot.getWorld();
		if(nextWorld == null)
		{
			plugin.log("[cbError] Cannot load world: " + nextWorldName);
			plugin.sendMessage(player, "[cbError] Cannot load world: " + nextWorldName);
			return;
		}
		
		Border border = BorderMgr.getNewBorder(nextWorld);
		Plugin.dispatchCommand(player, "wb shape rectangular");
		Plugin.dispatchCommand(player, 
				"wb " + nextWorldName + " set " + 
				(int)(border.getSize()/2) + " " + (int)(border.getSize()/2) + " " + 
				border.getCenterX() + " " + border.getCenterZ());
		Plugin.dispatchCommand(player, "wb " + nextWorldName + " trim 5000 32"); // 5000 freq, 32 padding
	}
	
	private static File getWorldsDir()
	{
		return Bukkit.getWorldContainer();
	}
}