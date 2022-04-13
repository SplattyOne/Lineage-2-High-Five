package quests;

import l2ft.gameserver.Config;
import l2ft.gameserver.scripts.ScriptFile;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.model.quest.Quest;
import l2ft.gameserver.model.quest.QuestState;

public class _639_GuardiansOfTheHolyGrail extends Quest implements ScriptFile
{
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	private final int DROP_CHANCE = 10; // Для х1 мобов

	// NPCS
	private final int DOMINIC = 31350;
	private final int GREMORY = 32008;
	private final int GRAIL = 32028;

	// ITEMS
	private final int SCRIPTURES = 8069;
	private final int WATER_BOTTLE = 8070;
	private final int HOLY_WATER_BOTTLE = 8071;

	// QUEST REWARD
	private final int EAS = 960;
	private final int EWS = 959;

	public _639_GuardiansOfTheHolyGrail()
	{
		super(true);
		addStartNpc(DOMINIC);
		addTalkId(GREMORY);
		addTalkId(GRAIL);
		addQuestItem(SCRIPTURES);
		for(int i = 22789; i <= 22800; i++)
			addKillId(i);
                addKillId(18909);
                addKillId(18910);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("falsepriest_dominic_q0639_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("falsepriest_dominic_q0639_09.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if(event.equalsIgnoreCase("falsepriest_dominic_q0639_08.htm"))
		{
			st.giveItems(ADENA_ID, st.takeAllItems(SCRIPTURES) * 1625, false);
		}
		else if(event.equalsIgnoreCase("falsepriest_gremory_q0639_05.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.giveItems(WATER_BOTTLE, 1, false);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("holy_grail_q0639_02.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(WATER_BOTTLE, -1);
			st.giveItems(HOLY_WATER_BOTTLE, 1);
			
		}
		else if(event.equalsIgnoreCase("falsepriest_gremory_q0639_09.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(HOLY_WATER_BOTTLE, -1);
		}
		else if(event.equalsIgnoreCase("falsepriest_gremory_q0639_11.htm"))
		{
			if(st.getQuestItemsCount(SCRIPTURES) >= 4000)
			{
				st.takeItems(SCRIPTURES, 4000);
				st.giveItems(EWS, 1, false);
			}
			else
				htmltext = "falsepriest_gremory_q0639_12.htm";
		}
		else if(event.equalsIgnoreCase("falsepriest_gremory_q0639_13.htm"))
		{
			if(st.getQuestItemsCount(SCRIPTURES) >= 400)
			{
				st.takeItems(SCRIPTURES, 400);
				st.giveItems(EAS, 1, false);
			}
			else
				htmltext = "falsepriest_gremory_q0639_14.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getInt("cond");
		if(npcId == DOMINIC)
		{
			if(id == CREATED)
			{
				if(st.getPlayer().getLevel() >= 73)
					htmltext = "falsepriest_dominic_q0639_01.htm";
				else
					htmltext = "falsepriest_dominic_q0639_02.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getQuestItemsCount(SCRIPTURES) >= 1)
				htmltext = "falsepriest_dominic_q0639_05.htm";
			else
				htmltext = "falsepriest_dominic_q0639_06.htm";
		}
		else if(npcId == GREMORY)
		{
			if(cond == 1)
				htmltext = "falsepriest_gremory_q0639_01.htm";
			else if(cond == 2)
				htmltext = "falsepriest_gremory_q0639_06.htm";
			else if(cond == 3)
				htmltext = "falsepriest_gremory_q0639_08.htm";
			else if(cond == 4 && st.getQuestItemsCount(SCRIPTURES) <= 0)
				htmltext = "falsepriest_gremory_q0639_12.htm";
			else if(cond == 4 && st.getQuestItemsCount(SCRIPTURES) > 0 && st.getQuestItemsCount(SCRIPTURES) < 400)
				htmltext = "falsepriest_gremory_q0639_14.htm";
			else if(cond == 4 && st.getQuestItemsCount(SCRIPTURES) >= 400)
				htmltext = "falsepriest_gremory_q0639_10.htm";
		}
		else if(npcId == GRAIL && cond == 2 && st.getQuestItemsCount(WATER_BOTTLE) > 0)
			htmltext = "holy_grail_q0639_01.htm";
		else if(npcId == GRAIL && cond == 3)
			htmltext = "holy_grail_q0639_02.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		st.rollAndGive(SCRIPTURES, (int)Config.RATE_QUESTS_DROP, DROP_CHANCE * npc.getTemplate().rateHp);
		return null;
	}
}