package services;

import l2ft.gameserver.Config;
import l2ft.gameserver.data.htm.HtmCache;
import l2ft.gameserver.data.xml.holder.ItemHolder;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.network.l2.components.SystemMsg;
import l2ft.gameserver.scripts.Functions;

public class BuyWashPk extends Functions
{
	public void list()
	{
		Player player = getSelf();
		if(!Config.SERVICES_WASH_PK_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}
		String html = null;
		html = HtmCache.getInstance().getNotNull("scripts/services/BuyWashPk.htm", player);
		String add = "";
		if(player.getPkKills() > 0)
		{
			add += "<center>";
			add += player.isLangRus() ? "Вас счётчик ПК равен " + player.getPkKills() + ".": "You count the PK is " + player.getPkKills() + ".";
			add += player.isLangRus() ? "<br1>Стоимоcть отмывки равна " + Config.SERVICES_WASH_PK_PRICE * player.getPkKills() + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "." : "<br1>The cost of cleaning is one of the first " + Config.SERVICES_WASH_PK_PRICE * player.getPkKills() + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + ".";
			add += "<br>";
			add += "<a action=\"bypass -h scripts_services.BuyWashPk:get " + player.getPkKills() + "\">";
			add += player.isLangRus() ? "Обнулить счётчик ПК.</a>" : "Cleaning count the PK.</a>";
			add += "<br>";
			add += "</center>";
		}
		else
		{
			add += "<center>";
			add += player.isLangRus() ? "Ваш счётчик ПК чист." : "Your counter PK clean.";
			add += "</center>";
		}
		show(html.replaceFirst("%toreplace%", add), player);
	}

	public void get(String[] param)
	{
		Player player = getSelf();
		if(!Config.SERVICES_WASH_PK_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}
		int i = Integer.parseInt(param[0]);
		if(getItemCount(player, Config.SERVICES_WASH_PK_ITEM) >= Config.SERVICES_WASH_PK_PRICE * i)
		{
			int kills = player.getPkKills();
			removeItem(player, Config.SERVICES_WASH_PK_ITEM, (long)(Config.SERVICES_WASH_PK_PRICE * i));
			player.setPkKills(kills - i);
			player.broadcastCharInfo();
		}
		else if(getItemCount(player, Config.SERVICES_WASH_PK_ITEM) <= 0)
			player.sendMessage(player.isLangRus() ? "У вас нет " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "!" : "You do not " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "!");
		else
			player.sendMessage(player.isLangRus() ? "Не достаточное количество " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "!" : "Not enough " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "!");
	}
}