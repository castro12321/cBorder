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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ProtectionListener implements Listener
{
	private boolean disableProtection = Config.protectionDisabled(); 
	
	
	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event)
	{
		if(disableProtection)
			return;
		
		// TODO: sprawdzic, czy event sie pojawia podczas wylewania poza mape
		Border border = BorderMgr.getBorder(event.getBlock().getWorld());
		
		Block from = event.getBlock();
		if(border.isLastBlock(from))
		{
			event.setCancelled(true);
			setStationary(from);
		}
		
		Block to = event.getToBlock();
		if(border.isLastBlock(to))
		{
			event.setCancelled(true);
			setStationary(to);
		}
	}
	private void setStationary(Block block)
	{
		if(block.getTypeId() > 9)
			block.setTypeId(Material.STATIONARY_LAVA.getId(), false);
		else // water
			block.setTypeId(Material.STATIONARY_WATER.getId(), false);
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		/* Disabled. Entities cleaner now handles it.
		if(disableProtection)
			return;
		
		Player player = event.getPlayer();
		Material holded = player.getItemInHand().getType();
		
		switch(holded)
		{
		case EYE_OF_ENDER:
		case FIREWORK:
		case EGG:
		case POTION:
			if(BorderMgr.contains(player.getWorld().getName()))
				event.setCancelled(true);
		default:
		}
		*/
	}
	
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if(disableProtection)
			return;
		
		BorderMgr.checkPlayer(event.getPlayer(), event);
	}
}
