package handler.bypass;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.handler.bypass.BypassHandler;
import l2ft.gameserver.handler.bypass.IBypassHandler;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.scripts.ScriptFile;
import l2ft.gameserver.utils.Location;

public class TeleToFantasyIsle implements ScriptFile, IBypassHandler
{
	public static final Location[] POINTS =
	{
			new Location(-60695, -56896, -2032),
			new Location(-59716, -55920, -2032),
			new Location(-58752, -56896, -2032),
			new Location(-59716, -57864, -2032)
	};

	@Override
	public String[] getBypasses()
	{
		return new String[] {"teleToFantasyIsle"};
	}

	@Override
	public void onBypassFeedback(NpcInstance npc, Player player, String command)
	{
		player.teleToLocation(Rnd.get(POINTS));
	}

	@Override
	public void onLoad()
	{
		BypassHandler.getInstance().registerBypass(this);
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
