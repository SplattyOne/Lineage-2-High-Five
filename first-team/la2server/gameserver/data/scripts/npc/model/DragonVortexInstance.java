package npc.model;

import l2ft.commons.threading.RunnableImpl;
import l2ft.commons.util.Rnd;
import l2ft.gameserver.Config;
import l2ft.gameserver.ThreadPoolManager;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.templates.npc.NpcTemplate;
import l2ft.gameserver.utils.ItemFunctions;
import l2ft.gameserver.utils.Location;
import l2ft.gameserver.utils.NpcUtils;

public final class DragonVortexInstance extends NpcInstance
{
	private final int[] bosses = { 25718, 25719, 25720, 25721, 25722, 25723, 25724 };
	private NpcInstance boss;
	private int bossCount = 0;

	public DragonVortexInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("request_boss"))
		{
			if(boss != null && Config.MAX_VORTEX_BOSS_COUNT != 0 && bossCount >= Config.MAX_VORTEX_BOSS_COUNT)
			{
				showChatWindow(player, "default/32871-3.htm");
				return;
			}

			if(ItemFunctions.getItemCount(player, 17248) > 0)
			{
				ItemFunctions.removeItem(player, 17248, 1, true);
				boss = NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], Location.coordsRandomize(getLoc(), 300, 600), getReflection());
				ThreadPoolManager.getInstance().schedule(new checkSpawnBoss(player, boss), Config.TIME_DESPAWN_VORTEX_BOSS * 60000);
				bossCount += 1;
				showChatWindow(player, "default/32871-1.htm");
			}
			else
				showChatWindow(player, "default/32871-2.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	private class checkSpawnBoss extends RunnableImpl
	{
		private NpcInstance _boss;
		private Player _player;

		public checkSpawnBoss(Player player, NpcInstance boss)
		{
			_player = player;
			_boss = boss;
		}

		@Override
		public void runImpl()
		{
			if(_boss != null)
			{
				if(_player != null)
					showChatWindow(_player, "default/32871-4.htm");
				if(bossCount > 0)
					bossCount -= 1;
				_boss.deleteMe();
			}
		}
	}
}