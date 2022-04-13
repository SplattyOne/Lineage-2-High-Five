package ai.residences.clanhall;

import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.network.l2.components.NpcString;
import l2ft.gameserver.scripts.Functions;

/**
 * @author VISTALL
 * @date 15:17/03.06.2011
 */
public class RainbowEnragedYeti extends Fighter
{
	public RainbowEnragedYeti(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();

		Functions.npcShout(getActor(), NpcString.OOOH_WHO_POURED_NECTAR_ON_MY_HEAD_WHILE_I_WAS_SLEEPING);
	}
}
