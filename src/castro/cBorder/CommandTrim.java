package castro.cBorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;

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
		
		String nextWorld = worldsToTrim.get(0);
		plugin.log("Preparing to trim: " + nextWorld);
		
		CPlot plot = PlotsMgr.get(nextWorld);
		if(plot == null)
		{
			plugin.log("[cbError] Cannot load plot: " + nextWorld);
			plugin.sendMessage(player, "[cbError] Cannot load plot: " + nextWorld);
			return;
		}
		
		plot.load();
		if(plot.getWorld() == null)
		{
			plugin.log("[cbError] Cannot load world: " + nextWorld);
			plugin.sendMessage(player, "[cbError] Cannot load world: " + nextWorld);
			return;
		}
		
		Border border = BorderMgr.getBorder(nextWorld);
		Plugin.dispatchCommand(player, "wb shape rectangular");
		Plugin.dispatchCommand(player, 
				"wb " + nextWorld + " set " + 
				border.radiusX * 16 + " " + border.radiusZ * 16 + " " + 
				border.centerX * 16 + " " + border.centerZ * 16);
		Plugin.dispatchCommand(player, "wb " + nextWorld + " trim 5000 50"); // 5000 freq, 50 padding
	}
	
	private static File getWorldsDir()
	{
		return Bukkit.getWorldContainer();
	}
}