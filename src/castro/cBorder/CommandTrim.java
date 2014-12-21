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
		
		plugin.scheduleSyncRepeatingTask(this, 20, 20);
		
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
		}
		
		String previousWorld = worldsToTrim.remove(0);
		plugin.log("Finished!");
		
		String nextWorld = worldsToTrim.get(0);
		plugin.log("Preparing to trim: " + nextWorld);
		
		CPlot plot = PlotsMgr.get(nextWorld);
		if(plot == null)
			plugin.log("Cannot load plot: " + nextWorld);
		
		player.teleport(plot.safeSpawn());
		Border border = BorderMgr.getBorder(nextWorld);
		Plugin.dispatchConsoleCommand("wb shape rectangular");
		Plugin.dispatchConsoleCommand(
				"wb " + nextWorld + " set " + 
				border.radiusX * 16 + " " + border.radiusZ * 16 + " " + 
				border.centerX * 16 + " " + border.centerZ * 16);
		Plugin.dispatchConsoleCommand("wb " + nextWorld + " trim 5000 50"); // 5000 freq, 50 padding
		
		CPlot previousPlot = PlotsMgr.get(previousWorld);
		if(previousPlot.getWorld().getPlayers().size() == 0)
			previousPlot.unload();
	}
	
	private static File getWorldsDir()
	{
		return Bukkit.getWorldContainer();
	}
}