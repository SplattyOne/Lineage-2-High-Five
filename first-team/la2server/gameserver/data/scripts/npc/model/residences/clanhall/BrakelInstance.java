package npc.model.residences.clanhall;

import l2ft.gameserver.data.xml.holder.ResidenceHolder;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.entity.residence.ClanHall;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2ft.gameserver.templates.npc.NpcTemplate;
import l2ft.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 * @date 18:16/04.03.2011
 */
public class BrakelInstance  extends NpcInstance
{
	public BrakelInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		ClanHall clanhall = ResidenceHolder.getInstance().getResidence(ClanHall.class, 21);
		if(clanhall == null)
			return;
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("residence2/clanhall/partisan_ordery_brakel001.htm");
		html.replace("%next_siege%", TimeUtils.toSimpleFormat(clanhall.getSiegeDate().getTimeInMillis()));
		player.sendPacket(html);
	}
}
