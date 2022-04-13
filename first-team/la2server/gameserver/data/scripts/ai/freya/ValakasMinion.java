package ai.freya;

import l2ft.gameserver.ai.CtrlEvent;
import l2ft.gameserver.ai.Mystic;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.instances.NpcInstance;
import bosses.ValakasManager;

public class ValakasMinion extends Mystic
{
	public ValakasMinion(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		for(Player p : ValakasManager.getZone().getInsidePlayers())
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
	}
}