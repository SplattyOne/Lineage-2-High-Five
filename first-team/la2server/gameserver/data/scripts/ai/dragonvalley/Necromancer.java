package ai.dragonvalley;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.CtrlEvent;
import l2ft.gameserver.ai.Mystic;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.utils.NpcUtils;

public class Necromancer extends Mystic
{
	public Necromancer(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		if(Rnd.chance(30))
		{
			NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
			n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
		}
	}
}