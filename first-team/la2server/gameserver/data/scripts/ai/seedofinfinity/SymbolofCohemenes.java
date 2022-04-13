package ai.seedofinfinity;

import l2ft.gameserver.ai.DefaultAI;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;

public class SymbolofCohemenes extends DefaultAI
{

	public SymbolofCohemenes(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}