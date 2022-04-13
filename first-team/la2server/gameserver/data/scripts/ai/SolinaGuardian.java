package ai;

import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.entity.Reflection;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */
public class SolinaGuardian extends Fighter
{

	public SolinaGuardian(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getInfo(6371, 1));
	}
}