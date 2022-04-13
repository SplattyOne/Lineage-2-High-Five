package npc.model;

import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */

public final class MaguenInstance extends NpcInstance
{
	public MaguenInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		return;
	}
}