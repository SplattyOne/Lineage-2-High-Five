package events.TvT;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import l2ft.gameserver.utils.GArray;
import l2ft.gameserver.utils.Location;
import l2ft.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configs implements Comparable<Object>
{
	private static final Logger _log = LoggerFactory.getLogger(Configs.class);
	public long START_TIME = 0;
	public int TIME_TO_END_BATTLE = 120;
	public int TIME_MAGE_SUPPORT = 10;
	public int MIN_PARTICIPANTS = 2;
	public int MAX_PARTICIPANTS = 10;
	public int TEAM_COUNTS = 0;
	public String ST_REWARD_ITEM_ID = "";
	public String ST_REWARD_COUNT = "";
	public GArray<Integer> LIST_MAGE_MAG_SUPPORT = new GArray<Integer>();
	public GArray<Integer> LIST_MAGE_FAITER_SUPPORT = new GArray<Integer>();
	public GArray<String> TEAM_NAME = new GArray<String>();
	public boolean STOP_ALL_EFFECTS = true;
	public boolean ALLOW_TAKE_ITEM = false;
	public boolean ALLOW_KILL_BONUS = false;
	public boolean ALLOW_HERO_WEAPONS = true;
	public int KILL_BONUS_ID = 0;
	public int KILL_BONUS_COUNT = 0;
	public int TAKE_ITEM_ID = 0;
	public int TAKE_COUNT = 0;
	public int RESURRECTION_TIME = 0;
	public int NUMBER_OF_ROUNDS = 0;
	public int MIN_LEVEL = 1;
	public int MAX_LEVEL = 85;
	public int TIME_TO_START_BATTLE = 10;
	public HashMap<Integer, Boolean> DOORS = new HashMap<Integer, Boolean>();
	public int REWARD_FOR_KILL = 1;
	public int PAUSE_TIME = 5;
	public String RESTRICT_ITEMS = "";
	public String CAN_USE_SKILL_ID = "";
	private ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();

	public int compareTo(Object obj)
	{
		Configs tmp = (Configs)obj;
		return START_TIME < tmp.START_TIME ? -1 : START_TIME > tmp.START_TIME ? 1 : 0;
	}

	public int[] getRestictId()
	{
		return getIntArray(RESTRICT_ITEMS);
	}

	public int[] getRewardId()
	{
		return getIntArray(ST_REWARD_ITEM_ID);
	}

	public int[] getCanUseSkillId()
	{
		return getIntArray(CAN_USE_SKILL_ID);
	}

	public int[] getRewardCount()
	{
		return getIntArray(ST_REWARD_COUNT);
	}

    private int[] getIntArray(String name)
	{
         return Util.parseCommaSeparatedIntegerArray(name);
    }
}