package ai.dragonvalley;

import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.utils.NpcUtils;

public class DrakeBosses extends Fighter
{
	public DrakeBosses(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		switch(getActor().getNpcId())
		{
			case 25725:
				NpcUtils.spawnSingle(32884, getActor().getLoc(), 300000);
				break;
			case 25726:
				NpcUtils.spawnSingle(32885, getActor().getLoc(), 300000);
				break;
			case 25727:
				NpcUtils.spawnSingle(32886, getActor().getLoc(), 300000);
				break;
		}
		super.onEvtDead(killer);
		getActor().endDecayTask();
	}
}