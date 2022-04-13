package ai.SkyshadowMeadow;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.DefaultAI;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.Skill;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.tables.SkillTable;

import org.apache.log4j.Logger;

/**
 * @author PaInKiLlEr
 *         - AI для Fire Feed (18933).
 *         - Удаляется через 10-60 секунд.
 *         - AI проверен и работает.
 */
public class FireFeed extends DefaultAI
{
	protected static Logger _log = Logger.getLogger(FireFeed.class.getName());
	private long _wait_timeout = System.currentTimeMillis() + Rnd.get(10, 30) * 1000;

	public FireFeed(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return true;

		if(_wait_timeout < System.currentTimeMillis())
			actor.decayMe();

		return true;
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		if(skill.getId() != 9075)
			return;

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		actor.doCast(SkillTable.getInstance().getInfo(6688, 1), caster, true);
	}
}