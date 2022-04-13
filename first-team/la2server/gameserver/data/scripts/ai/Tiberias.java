package ai;

import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.scripts.Functions;

/**
 * AI рейдбосса Tiberias
 * любит поговорить после смерти
 * @author n0nam3
 */
public class Tiberias extends Fighter
{
	public Tiberias(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Functions.npcShoutCustomMessage(actor, "scripts.ai.Tiberias.kill");
		super.onEvtDead(killer);
	}
}