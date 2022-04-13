package services;

import l2ft.gameserver.Config;
import l2ft.gameserver.cache.Msg;
import l2ft.gameserver.instancemanager.QuestManager;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.base.Race;
import l2ft.gameserver.model.entity.olympiad.Olympiad;
import l2ft.gameserver.model.quest.Quest;
import l2ft.gameserver.model.quest.QuestState;
import l2ft.gameserver.network.l2.components.SystemMsg;
import l2ft.gameserver.network.l2.s2c.SkillList;
import l2ft.gameserver.scripts.Functions;
import quests._234_FatesWhisper;

public class NoblessSell extends Functions
{
	public void get()
	{
		Player player = getSelf();

		if(player.isNoble())
			return;

		if(player.getSubLevel() < 75)
		{
			player.sendMessage("You must make sub class level 75 first.");
			return;
		}

		if(player.getInventory().destroyItemByItemId(Config.SERVICES_NOBLESS_SELL_ITEM, Config.SERVICES_NOBLESS_SELL_PRICE))
		{
			makeSubQuests();
			becomeNoble();
		}
		else if(Config.SERVICES_NOBLESS_SELL_ITEM == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}

	public void makeSubQuests()
	{
		Player player = getSelf();
		if(player == null)
			return;
		Quest q = QuestManager.getQuest(_234_FatesWhisper.class);
		QuestState qs = player.getQuestState(q.getClass());
		if(qs != null)
			qs.exitCurrentQuest(true);
		q.newQuestState(player, Quest.COMPLETED);

		if(player.getRace() == Race.kamael)
		{
			q = QuestManager.getQuest("_236_SeedsOfChaos");
			qs = player.getQuestState(q.getClass());
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}
		else
		{
			q = QuestManager.getQuest("_235_MimirsElixir");
			qs = player.getQuestState(q.getClass());
			if(qs != null)
				qs.exitCurrentQuest(true);
			q.newQuestState(player, Quest.COMPLETED);
		}
	}

	public void becomeNoble()
	{
		Player player = getSelf();
		if(player == null || player.isNoble())
			return;

		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.broadcastUserInfo(true);
	}
}