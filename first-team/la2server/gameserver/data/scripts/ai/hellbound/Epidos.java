package ai.hellbound;

import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.instancemanager.naia.NaiaCoreManager;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;

public class Epidos extends Fighter
{

	public Epidos(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NaiaCoreManager.removeSporesAndSpawnCube();
		super.onEvtDead(killer);
	}
}