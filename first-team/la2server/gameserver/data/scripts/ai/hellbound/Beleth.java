package ai.hellbound;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.CtrlEvent;
import l2ft.gameserver.ai.Mystic;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.Playable;
import l2ft.gameserver.model.World;
import l2ft.gameserver.model.instances.NpcInstance;
import bosses.BelethManager;

public class Beleth extends Mystic
{
	private long _lastFactionNotifyTime = 0;
	private static final int CLONE = 29119;

	public Beleth(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		BelethManager.setBelethDead();
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if(System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval)
		{
			_lastFactionNotifyTime = System.currentTimeMillis();
			
			for(NpcInstance npc : World.getAroundNpc(actor))
				if(npc.getNpcId() == CLONE)
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	public boolean canSeeInSilentMove(Playable target)
	{
		return true;
	}

	@Override
	public boolean canSeeInHide(Playable target)
	{
		return true;
	}

	@Override
	public void addTaskAttack(Creature target)
	{
		return;
	}

}