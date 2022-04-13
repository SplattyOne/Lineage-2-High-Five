package npc.model.residences.fortress.peace;

import l2ft.gameserver.dao.SiegeClanDAO;
import l2ft.gameserver.data.xml.holder.EventHolder;
import l2ft.gameserver.data.xml.holder.ResidenceHolder;
import l2ft.gameserver.model.pledge.Clan;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.entity.events.EventType;
import l2ft.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2ft.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2ft.gameserver.model.entity.events.impl.SiegeEvent;
import l2ft.gameserver.model.entity.events.objects.SiegeClanObject;
import l2ft.gameserver.model.entity.residence.Castle;
import l2ft.gameserver.model.entity.residence.Fortress;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.model.pledge.Privilege;
import l2ft.gameserver.network.l2.components.SystemMsg;
import l2ft.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2ft.gameserver.network.l2.s2c.SystemMessage2;
import l2ft.gameserver.network.l2.s2c.CastleSiegeInfo;
import l2ft.gameserver.network.l2.s2c.CastleSiegeAttackerList;
import l2ft.gameserver.templates.npc.NpcTemplate;
import l2ft.gameserver.templates.item.ItemTemplate;

public class SuspiciousMerchantInstance extends NpcInstance
{
	public SuspiciousMerchantInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		else if(command.equalsIgnoreCase("showSiegeReg"))
			showSiegeRegWindow(player);
		else if(command.equalsIgnoreCase("showSiegeInfo"))
			showSiegeInfoWindow(player);
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		Fortress fortress = getFortress();
		if(fortress.getOwner() != null)
		{
			html.setFile("residence2/fortress/fortress_ordery001a.htm");
			html.replace("%clan_name%", fortress.getOwner().getName());
		}
		else
			html.setFile("residence2/fortress/fortress_ordery001.htm");

		player.sendPacket(html);
	}

	public void showSiegeInfoWindow(Player player)
	{
		Fortress fortress = getFortress();
		player.sendPacket(new CastleSiegeInfo(fortress, player));
	}

	public void showSiegeRegWindow(Player player)
	{
		Fortress fortress = getFortress();
		player.sendPacket(new CastleSiegeAttackerList(fortress));
	}
}