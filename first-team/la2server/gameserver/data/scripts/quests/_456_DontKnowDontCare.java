package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2ft.commons.util.Rnd;
import l2ft.gameserver.model.GameObjectsStorage;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.model.quest.Quest;
import l2ft.gameserver.model.quest.QuestState;
import l2ft.gameserver.scripts.ScriptFile;

/**
 *         ВНИМАНИЕ! Данный квест можно выполнять не только группой, но и командным каналом, все персонажи в командном 
 *         канале имеют шанс получить квестовые предметы. После убийства боссов будут появляться специальные НПЦ - мертвые тела боссов, 
 *		   для получения квестовых предметов необходимо будет "поговорить" с этим НПЦ.
 */
public class _456_DontKnowDontCare extends Quest implements ScriptFile
{
	private static final int[] SeparatedSoul = {32864, 32865, 32866, 32867, 32868, 32869, 32870};
	private static final int DrakeLordsEssence = 17251;
	private static final int BehemothLeadersEssence = 17252;
	private static final int DragonBeastsEssence = 17253;
	private static final int DrakeLord = 25725;
	private static final int BehemothLeader = 25726;
	private static final int DragonBeast = 25727;

	private static final int DrakeLordCorpse = 32884;
	private static final int BehemothLeaderCorpse = 32885;
	private static final int DragonBeastCorpse = 32886;

	//Reward set
	private static final int[] weapons = {15558, 15559, 15560, 15561, 15562, 15563, 15564, 15565, 15566, 15567, 15568, 15569, 15570, 15571};
	private static final int[] armors = {15743, 15744, 15745, 15746, 15747, 15748, 15749, 15750, 15751, 15752, 15753, 15754, 15755, 15756, 15757, 15759, 15758};
	private static final int[] accessory = {15763, 15764, 15765};
	private static final int[] scrolls = {6577, 6578, 959};
	private static final int[] reward_attr_crystal = {4342, 4343, 4344, 4345, 4346, 4347};
	private static final int gemstone_s = 2134;
	
	private static final int CHANCE = 5;


	public _456_DontKnowDontCare()
	{
		super(PARTY_ALL);
		addStartNpc(SeparatedSoul);
		addTalkId(DrakeLordCorpse, BehemothLeaderCorpse, DragonBeastCorpse);
		addQuestItem(DrakeLordsEssence, BehemothLeadersEssence, DragonBeastsEssence);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sepsoul_q456_05.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("take_essense"))
		{
			if(st.getCond() == 1)
			{
				switch(npc.getNpcId())
				{
					case DrakeLordCorpse:
						if(st.getQuestItemsCount(DrakeLordsEssence) < 1)
							st.giveItems(DrakeLordsEssence, 1);
						break;
					case BehemothLeaderCorpse:
						if(st.getQuestItemsCount(BehemothLeadersEssence) < 1)
							st.giveItems(BehemothLeadersEssence, 1);
						break;
					case DragonBeastCorpse:
						if(st.getQuestItemsCount(DragonBeastsEssence) < 1)
							st.giveItems(DragonBeastsEssence, 1);
						break;
					default:
						break;
				}
				if(st.getQuestItemsCount(DrakeLordsEssence) > 0 && st.getQuestItemsCount(BehemothLeadersEssence) > 0 && st.getQuestItemsCount(DragonBeastsEssence) > 0)
					st.setCond(2);
			}
			return null;
		}
		
		else if(event.equalsIgnoreCase("drake"))
		{
			if((st.getQuestItemsCount(DrakeLordsEssence) < 1) && (st.getInt("cond") == 1))
			{
				if (st.getInt("KB456_DRAKE") != 1)
				{
					if(Rnd.chance(CHANCE))
					{	
						st.giveItems(DrakeLordsEssence, 1, false);
						htmltext = "32864-13.htm";
					}	else htmltext = "32864-14.htm";
					if((st.getQuestItemsCount(DrakeLordsEssence) >= 1) && (st.getQuestItemsCount(BehemothLeadersEssence) >= 1) && (st.getQuestItemsCount(DragonBeastsEssence) >= 1))
						{
							st.set("cond", "2");
							st.playSound(SOUND_MIDDLE);
						}
					st.set("KB456_DRAKE", 1);
				}
			}		
			else htmltext = "32864-10.htm";	
		}
		
		else if(event.equalsIgnoreCase("behemoth"))
		{
			if((st.getQuestItemsCount(BehemothLeadersEssence) < 1) && (st.getInt("cond") == 1))
			{
				if (st.getInt("KB456_BEHEMOTH") != 1)
				{
					if(Rnd.chance(CHANCE))
					{	
						st.giveItems(BehemothLeadersEssence, 1, false);
						htmltext = "32864-13.htm";
					}	else htmltext = "32864-14.htm";
					if((st.getQuestItemsCount(DrakeLordsEssence) >= 1) && (st.getQuestItemsCount(BehemothLeadersEssence) >= 1) && (st.getQuestItemsCount(DragonBeastsEssence) >= 1))
						{
							st.set("cond", "2");
							st.playSound(SOUND_MIDDLE);
						}
					st.set("KB456_BEHEMOTH", 1);
				}
			}		
			else htmltext = "32864-10.htm";	
		}
		
		else if(event.equalsIgnoreCase("dragon"))
		{
			if((st.getQuestItemsCount(DragonBeastsEssence) < 1) && (st.getInt("cond") == 1))
			{
				if (st.getInt("KB456_DRAGON") != 1)
				{
					if(Rnd.chance(CHANCE))
					{	
						st.giveItems(DragonBeastsEssence, 1, false);
						htmltext = "32864-13.htm";
					}	else htmltext = "32864-14.htm";
					if((st.getQuestItemsCount(DrakeLordsEssence) >= 1) && (st.getQuestItemsCount(BehemothLeadersEssence) >= 1) && (st.getQuestItemsCount(DragonBeastsEssence) >= 1))
						{
							st.set("cond", "2");
							st.playSound(SOUND_MIDDLE);
						}
					st.set("KB456_DRAGON", 1);
				}
			}		
			else htmltext = "32864-10.htm";	
		}
		
		else if(event.equalsIgnoreCase("sepsoul_q456_08.htm"))
		{
			st.takeAllItems(DrakeLordsEssence);
			st.takeAllItems(BehemothLeadersEssence);
			st.takeAllItems(DragonBeastsEssence);

			if(Rnd.chance(30))
				st.giveItems(weapons[Rnd.get(weapons.length)], 1);
			else if(Rnd.chance(50))
				st.giveItems(armors[Rnd.get(armors.length)], 1);
			else
				st.giveItems(accessory[Rnd.get(accessory.length)], 1);

			if(Rnd.chance(30))
				st.giveItems(scrolls[Rnd.get(scrolls.length)], 1);
			if(Rnd.chance(70))
				st.giveItems(reward_attr_crystal[Rnd.get(reward_attr_crystal.length)], 1);
			st.giveItems(gemstone_s, 3);

			st.setState(COMPLETED);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(SeparatedSoul, npc.getNpcId()))
		{
			switch(st.getState())
			{
				case CREATED:
					if(st.isNowAvailable())
					{
						if(st.getPlayer().getLevel() >= 80)
							htmltext = "sepsoul_q456_01.htm";
						else
						{
							htmltext = "sepsoul_q456_00.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
						htmltext = "sepsoul_q456_00a.htm";
					break;
				case STARTED:
					if(cond == 1)
						htmltext = "sepsoul_q456_06.htm";
					else if(cond == 2)
						htmltext = "sepsoul_q456_07.htm";
					break;
			}
		}
		
		else if (npcId == DrakeLordCorpse)
		{	
			if (st.getInt("cond") == 1)
			htmltext = "drake.htm";
			else 
			htmltext = "drake_no.htm";
		}

		else if (npcId == BehemothLeaderCorpse)
		{	
			if (st.getInt("cond") == 1)
			htmltext = "behemoth.htm";
			else 
			htmltext = "behemoth_no.htm";
		}

		else if (npcId == DragonBeastCorpse)
		{	
			if (st.getInt("cond") == 1)
			htmltext = "dragon.htm";
			else 
			htmltext = "dragon_no.htm";
		}

		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(st.getInt("cond") == 1)
		{
			if(npcId == DrakeLord)
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrakeLordCorpse);
				if(isQuest == null)
				{
					st.addSpawn(DrakeLordCorpse, 120000);
					st.playSound(SOUND_MIDDLE);
					st.startQuestTimer("DRAKE_LEADER_Fail", 120000);
					st.set("KB456_DRAKE", 0);
				}
			}		
			else if(npcId == BehemothLeader)
			{			
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(BehemothLeaderCorpse);
				if(isQuest == null)
				{
					st.addSpawn(BehemothLeaderCorpse, 120000);
					st.playSound(SOUND_MIDDLE);
					st.startQuestTimer("BEHEMOTH_LEADER_Fail", 120000);
					st.set("KB456_BEHEMOTH", 0);
				}
			}
			else if(npcId == DragonBeast)
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(DragonBeastCorpse);
				if(isQuest == null)
				{
					st.addSpawn(DragonBeastCorpse, 120000);
					st.playSound(SOUND_MIDDLE);
					st.startQuestTimer("DRAGON_BEAST_Fail", 120000);
					st.set("KB456_DRAGON", 0);
				}
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}