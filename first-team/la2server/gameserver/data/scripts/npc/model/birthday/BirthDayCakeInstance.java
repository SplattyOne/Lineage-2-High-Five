package npc.model.birthday;

import java.util.concurrent.Future;

import l2ft.commons.threading.RunnableImpl;
import l2ft.gameserver.ThreadPoolManager;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.World;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.model.Skill;
import l2ft.gameserver.tables.SkillTable;
import l2ft.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 22:21/28.08.2011
 */
@SuppressWarnings("serial")
public class BirthDayCakeInstance extends NpcInstance
{
	private static final Skill SKILL = SkillTable.getInstance().getInfo(22035, 1);

	private class CastTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : World.getAroundPlayers(BirthDayCakeInstance.this, 500, 100))
			{
				if(player.getEffectList().getEffectsBySkill(SKILL) != null)
					continue;

				SKILL.getEffects(BirthDayCakeInstance.this, player, false, false);
			}
		}
	}

	private Future<?> _castTask;

	public BirthDayCakeInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setTargetable(false);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		_castTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CastTask(), 1000L, 1000L);
	}

	@Override
	public void onDespawn()
	{
		super.onDespawn();

		_castTask.cancel(false);
		_castTask = null;
	}
}
