package services.community;

import java.util.StringTokenizer;

import l2ft.gameserver.Config;
import l2ft.gameserver.data.htm.HtmCache;
import l2ft.gameserver.handler.bbs.CommunityBoardManager;
import l2ft.gameserver.handler.bbs.ICommunityBoardHandler;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.items.ItemInstance;
import l2ft.gameserver.network.l2.s2c.ShowBoard;
import l2ft.gameserver.scripts.Functions;
import l2ft.gameserver.scripts.ScriptFile;
import l2ft.gameserver.utils.BbsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageServices  extends Functions implements ScriptFile, ICommunityBoardHandler
{
    static final Logger _log = LoggerFactory.getLogger(ManageServices.class);
	
    @Override
	public void onLoad()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: Manager Community services loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
		{
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public void onShutdown(){}
	
	@Override
	public String[] getBypassCommands()
	{
		return new String[] {
				"_bbsservices",
				"_bbslvlup",
				"_bbsenchant",
				"_bbsenchantatt",
				"_bbssellhero"
				};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if(!CheckCondition(player))
			return;

		String page = "index";
		if(bypass.startsWith("_bbsservices"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			page = mBypass[1];
			//Заготовка...
		}

		if(bypass.startsWith("_bbslvlup"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			page = mBypass[1];
			//Заготовка...
		}
		else if(bypass.startsWith("_bbsenchant"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			page = mBypass[1];
			//Заготовка...
		}
		else if(bypass.startsWith("_bbsenchantatt"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			page = mBypass[1];
			//Заготовка...
		}
		
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/services/" + page + ".htm", player);
		ShowHtml(html, player);
		
	}

	private void ShowHtml(String html, Player player)
	{
		ShowBoard.separateAndSend(BbsUtil.htmlBuff(html, player), player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{}

	private static boolean checkEnchant(ItemInstance item)
	{
		if(item == null)
		{
			return false;
		}
		if(!item.canBeEnchanted(true))
		{
			return false;
		}
		return true;
	}

	private static boolean CheckCondition(Player player)
	{
		if (player == null)
            return false;

        if (player.isInOlympiadMode()) 
        {
        	if (player.isLangRus())
				player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
        	else
        		player.sendMessage("During the Olympics you can not use this feature.");
            return false;
        }

        if (player.getReflection().getId() != 0 && !Config.COMMUNITYBOARD_INSTANCE_ENABLED) 
        {
        	if (player.isLangRus())
        		player.sendMessage("Сервис доступен только в обычном мире.");
        	else
        		player.sendMessage("Service is only available in the real world.");
            return false;
        }
        return true;
	}
}