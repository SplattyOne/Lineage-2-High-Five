package ai.freya;

import l2ft.gameserver.ai.CtrlEvent;
import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.tables.SkillTable;
import bosses.AntharasManager;

public class AntharasMinion extends Fighter
{
	public AntharasMinion(NpcInstance actor)
	{
		super(actor);
		actor.startDebuffImmunity();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		for(Player p : AntharasManager.getZone().getInsidePlayers())
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		getActor().doCast(SkillTable.getInstance().getInfo(5097, 1), getActor(), true);
		super.onEvtDead(killer);
	}

	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		return;
	}
}