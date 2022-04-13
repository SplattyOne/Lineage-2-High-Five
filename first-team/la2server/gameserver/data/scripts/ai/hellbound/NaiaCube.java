package ai.hellbound;

import l2ft.commons.threading.RunnableImpl;
import l2ft.gameserver.ThreadPoolManager;
import l2ft.gameserver.ai.DefaultAI;
import l2ft.gameserver.instancemanager.naia.NaiaCoreManager;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.utils.ReflectionUtils;

public class NaiaCube extends DefaultAI
{

	public NaiaCube(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		ThreadPoolManager.getInstance().schedule(new Despawn(getActor()), 120 * 1000L);
	}

	private class Despawn extends RunnableImpl
	{
		NpcInstance _npc;

		private Despawn(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			_npc.deleteMe();
			NaiaCoreManager.setZoneActive(false);
			ReflectionUtils.getDoor(20240001).openMe(); // Beleth Door
			ReflectionUtils.getDoor(18250025).openMe(); // Epidos Door
		}
	}
}