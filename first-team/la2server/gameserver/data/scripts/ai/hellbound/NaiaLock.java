package ai.hellbound;

import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.data.xml.holder.NpcHolder;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.SimpleSpawner;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.scripts.Functions;
import l2ft.gameserver.utils.Location;

public class NaiaLock extends Fighter
{
	private static boolean _attacked = false;
	private static boolean _entranceactive = false;

	public NaiaLock(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		_entranceactive = true;
		Functions.npcShout(actor, "The lock has been removed from the Controller device");
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		NpcInstance actor = getActor();
		_entranceactive = false;
		Functions.npcShout(actor, "The lock has been put on the Controller device");
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if(!_attacked)
		{
			for(int i = 0; i < 4; i++)
				try
			{
					SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(18493));
					sp.setLoc(Location.findPointToStay(actor, 150, 250));
					sp.setReflection(actor.getReflection());
					sp.doSpawn(true);
					sp.stopRespawn();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			_attacked = true;
		}
	}

	public static boolean isEntranceActive()
	{
		return _entranceactive;
	}
}