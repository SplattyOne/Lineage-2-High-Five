package ai.dragonvalley;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.CtrlEvent;
import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.ai.Mystic;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.utils.NpcUtils;

public class DragonKnight extends Fighter
{
	public DragonKnight(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		switch(getActor().getNpcId())
		{
			case 22844:
				if(Rnd.chance(50))
				{
					NpcInstance n = NpcUtils.spawnSingle(22845, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
			case 22845:
				if(Rnd.chance(50))
				{
					NpcInstance n = NpcUtils.spawnSingle(22846, getActor().getLoc());
					n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
				}
				break;
		}

	}
}