package ai.hellbound;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.CtrlIntention;
import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.scripts.Functions;

public class TownGuard extends Fighter
{
	public TownGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
			Functions.npcSay(actor, "Invader!");
		super.onIntentionAttack(target);
	}
}