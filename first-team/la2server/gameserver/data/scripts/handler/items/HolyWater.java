package handler.items;

import l2ft.gameserver.handler.items.IItemHandler;
import l2ft.gameserver.handler.items.ItemHandler;
import l2ft.gameserver.model.GameObject;
import l2ft.gameserver.model.Playable;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.items.ItemInstance;
import l2ft.gameserver.network.l2.components.SystemMsg;
import l2ft.gameserver.network.l2.s2c.MagicSkillUse;
import npc.model.HellboundRemnantInstance;
import l2ft.gameserver.scripts.ScriptFile;

public class HolyWater extends SimpleItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[] { 9673 };

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}


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
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		GameObject target = player.getTarget();

		if(target == null || !(target instanceof HellboundRemnantInstance))
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		HellboundRemnantInstance npc = (HellboundRemnantInstance) target;
		if(npc.isDead())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		player.broadcastPacket(new MagicSkillUse(player, npc, 2358, 1, 0, 0));
		npc.onUseHolyWater(player);

		return true;
	}
}
