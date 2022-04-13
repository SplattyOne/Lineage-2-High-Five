package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import l2ft.commons.dbutils.DbUtils;
import l2ft.gameserver.Config;
import l2ft.gameserver.data.htm.HtmCache;
import l2ft.gameserver.data.xml.holder.BuyListHolder;
import l2ft.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2ft.gameserver.data.xml.holder.MultiSellHolder;
import l2ft.gameserver.data.xml.holder.NpcHolder;
import l2ft.gameserver.database.DatabaseFactory;
import l2ft.gameserver.handler.bbs.CommunityBoardManager;
import l2ft.gameserver.handler.bbs.ICommunityBoardHandler;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.Zone.ZoneType;
import l2ft.gameserver.network.l2.components.CustomMessage;
import l2ft.gameserver.network.l2.s2c.ExBuySellList;
import l2ft.gameserver.network.l2.s2c.ShowBoard;
import l2ft.gameserver.scripts.ScriptFile;
import l2ft.gameserver.scripts.Scripts;
import l2ft.gameserver.tables.ClanTable;
import l2ft.gameserver.templates.npc.NpcTemplate;
import l2ft.gameserver.utils.BbsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoard implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityBoard.class);

	@Override
	public void onLoad()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: service loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
			CommunityBoardManager.getInstance().removeHandler(this);
	}

	@Override
	public void onShutdown()
	{}

	@Override
	public String[] getBypassCommands()
	{
		return new String[] { "_bbshome", "_bbslink", "_bbsmultisell", "_bbssell", "_bbspage", "_bbsscripts" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if(!CheckCondition(player))
			return;

		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";
		if("bbshome".equals(cmd))
		{
			if(!CheckCondition(player))
				return;
			StringTokenizer p = new StringTokenizer(Config.BBS_DEFAULT, "_");
			String dafault = p.nextToken();
			if(dafault.equals(cmd))
			{
				html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/main.htm", player);
				html = BbsUtil.htmlAll(html, player);
			}
			else
			{
				onBypassCommand(player, Config.BBS_DEFAULT);
				return;
			}
		}
		else if("bbslink".equals(cmd))
		{
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_homepage.htm", player);
			html = BbsUtil.htmlAll(html, player);
		}
		else if(bypass.startsWith("_bbspage"))
		{
			if(!CheckCondition(player))
				return;
			//Example: "bypass _bbspage:index".
			String[] b = bypass.split(":");
			String page = b[1];
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/" + page + ".htm", player);
			html = BbsUtil.htmlAll(html, player);
		}
		else if(bypass.startsWith("_bbsmultisell"))
		{
			if(!CheckCondition(player))
				return;
			if(!Config.COMMUNITYBOARD_SHOP_NO_IS_IN_PEACE_ENABLED && !player.isInPeaceZone())
			{
				player.sendMessage(player.isLangRus() ? "Эта функция доступна только в мирной зоне!" : "This feature is only available in a peaceful area!");
				return;}
			//Example: "_bbsmultisell:10000;_bbspage:index" or "_bbsmultisell:10000;_bbshome" or "_bbsmultisell:10000"...
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
			{
				ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
				if(handler != null)
					handler.onBypassCommand(player, pBypass);
			}

			int listId = Integer.parseInt(mBypass[1]);
			MultiSellHolder.getInstance().SeparateAndSend(listId, player, 0);
			return;
		}
		else if(bypass.startsWith("_bbssell"))
		{
			if(!CheckCondition(player))
				return;
			if(!Config.COMMUNITYBOARD_SHOP_NO_IS_IN_PEACE_ENABLED && !player.isInPeaceZone())
			{
				player.sendMessage(player.isLangRus() ? "Эта функция доступна только в мирной зоне!" : "This feature is only available in a peaceful area!");
				return;}
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
			{
				ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
				if(handler != null)
					handler.onBypassCommand(player, pBypass);
			}
			player.setIsBBSUse(true);
			NpcTradeList list = BuyListHolder.getInstance().getBuyList(-1);
			player.sendPacket(new ExBuySellList.BuyList(list, player, 0.), new ExBuySellList.SellRefundList(player, false));
			return;
		}
		else if(bypass.startsWith("_bbsscripts"))
		{
			if(!CheckCondition(player))
				return;
			//Example: "_bbsscripts:events.GvG.GvG:addGroup;_bbspage:index" or "_bbsscripts:events.GvG.GvG:addGroup;_bbshome" or "_bbsscripts:events.GvG.GvG:addGroup"...
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String sBypass = st2.nextToken().substring(12);
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
			{
				ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
				if(handler != null)
					handler.onBypassCommand(player, pBypass);
			}

			String[] word = sBypass.split("\\s+");
			String[] args = sBypass.substring(word[0].length()).trim().split("\\s+");
			String[] path = word[0].split(":");
			if(path.length != 2)
				return;

			Scripts.getInstance().callScripts(player, path[0], path[1], word.length == 1 ? new Object[] {} : new Object[] { args });
			return;
		}

		ShowBoard.separateAndSend(html, player);
	}

	private boolean CheckCondition(Player player)
	{
		if(player == null)
            return false;

		if(player.isDead())
            return false;

		if(!Config.ALLOW_COMMUNITYBOARD_IN_COMBAT && player.isInCombat())
		{
			player.sendMessage(new CustomMessage("l2ft.gameserver.communitybbs.NotUse", player));
			return false;
		}

		if(!Config.ALLOW_COMMUNITYBOARD_IS_IN_SIEGE && player.isInZone(ZoneType.SIEGE))
		{
        	if (player.isLangRus())
        		player.sendMessage("В зоне, находящейся в осаде, использовать запрещено.");
        	else
        		player.sendMessage("In the zone, located in the siege, use prohibited.");
			return false;
		}

		if(!Config.ALLOW_COMMUNITYBOARD_IN_COMBAT && (player.isInDuel() || player.isInCombat() || player.isAttackingNow()))
		{
			if (player.isLangRus())
				player.sendMessage("Во время боя нельзя использовать данную функцию.");
			else
				player.sendMessage("During combat, you can not use this feature.");
			return false;
		}

		if(player.isInOlympiadMode())
		{
			player.sendMessage(player.isLangRus() ? "Во время олимпийского боя нельзя использовать данную функцию." : "During the Olympic battle you can not use this feature.");
			return false;
		}
		return true;
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{}
}
