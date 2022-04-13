package ai;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.geodata.GeoEngine;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.utils.Location;

public class Elpy extends Fighter
{
	public Elpy(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker != null && Rnd.chance(50))
		{
			Location pos = Location.findPointToStay(actor, 150, 200); 
			if(GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
			{
				actor.setRunning();
				addTaskMove(pos, false);
			}
		}
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		return false;
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{

	}
}