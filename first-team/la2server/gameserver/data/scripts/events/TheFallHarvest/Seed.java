package events.TheFallHarvest;

import l2ft.commons.threading.RunnableImpl;
import l2ft.gameserver.ThreadPoolManager;
import l2ft.gameserver.data.xml.holder.NpcHolder;
import l2ft.gameserver.handler.items.IItemHandler;
import l2ft.gameserver.handler.items.ItemHandler;
import l2ft.gameserver.model.Playable;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.SimpleSpawner;
import l2ft.gameserver.model.Zone.ZoneType;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.model.items.ItemInstance;
import l2ft.gameserver.templates.npc.NpcTemplate;
import l2ft.gameserver.utils.Location;
import l2ft.gameserver.scripts.ScriptFile;
import handler.items.ScriptItemHandler;
import npc.model.SquashInstance;

public class Seed extends ScriptItemHandler implements ScriptFile
{
	public class DeSpawnScheduleTimerTask extends RunnableImpl
	{
		SimpleSpawner spawnedPlant = null;

		public DeSpawnScheduleTimerTask(SimpleSpawner spawn)
		{
			spawnedPlant = spawn;
		}

		@Override
		public void runImpl() throws Exception
		{
			spawnedPlant.deleteAll();
		}
	}

	private static int[] _itemIds = { 6389, // small seed
		6390 // large seed
	};

	private static int[] _npcIds = { 12774, // Young Pumpkin
		12777 // Large Young Pumpkin
	};

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		Player activeChar = (Player) playable;
		if(activeChar.isInZone(ZoneType.RESIDENCE))
		{
			return false;
		}
		if(activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("Нельзя взращивать тыкву на стадионе.");
			return false;
		}
		if(!activeChar.getReflection().isDefault())
		{
			activeChar.sendMessage("Нельзя взращивать тыкву в инстансе.");
			return false;
		}

		NpcTemplate template = null;

		int itemId = item.getItemId();
		for(int i = 0; i < _itemIds.length; i++)
			if(_itemIds[i] == itemId)
			{
				template = NpcHolder.getInstance().getTemplate(_npcIds[i]);
				break;
			}

		if(template == null)
			return false;

		if (!activeChar.getInventory().destroyItem(item, 1L))
			return false;

		SimpleSpawner spawn = new SimpleSpawner(template);
		spawn.setLoc(Location.findPointToStay(activeChar, 30, 70));
		NpcInstance npc = spawn.doSpawn(true);
		npc.setAI(new SquashAI(npc));
		((SquashInstance) npc).setSpawner(activeChar);

		ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(spawn), 180000);

		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}