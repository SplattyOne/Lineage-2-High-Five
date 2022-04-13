package handler.items;

import l2ft.gameserver.handler.items.IItemHandler;
import l2ft.gameserver.handler.items.ItemHandler;
import l2ft.gameserver.model.Playable;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.items.ItemInstance;
import l2ft.gameserver.network.l2.components.SystemMsg;
import l2ft.gameserver.network.l2.s2c.SystemMessage2;
import l2ft.gameserver.scripts.ScriptFile;

public class TeleportBookmark extends SimpleItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[] { 13015 };

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		if(player == null || item == null || !(player instanceof Player))
			return false;

		if(player.bookmarks.getCapacity() >= 30)
		{
			player.sendPacket(SystemMsg.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
			return false;
		}
		else
		{
			player.getInventory().destroyItem(item, 1);
			player.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_DISAPPEARED).addItemName(item.getItemId()));
			player.bookmarks.setCapacity(player.bookmarks.getCapacity() + 3);
			player.sendPacket(SystemMsg.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);
			return true;
		}
	}
}
