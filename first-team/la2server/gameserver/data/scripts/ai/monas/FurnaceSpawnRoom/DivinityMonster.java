package ai.monas.FurnaceSpawnRoom;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.DefaultAI;
import l2ft.gameserver.data.xml.holder.EventHolder;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.entity.events.EventType;
import l2ft.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import l2ft.gameserver.model.instances.NpcInstance;

/**
 * @author PaInKiLlEr
 *         - AI для монстров 22798, 22799, 22800.
 *         - АИ для спавна жаровень комнате.
 *         - Есть шанс 5% что заспавнят при смерти 4 жаровни вряд.
 *         - AI проверен и работает.
 */
public class DivinityMonster extends DefaultAI
{
	public DivinityMonster(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		int event_id = actor.getAISpawnParam();
		MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);

		if(Rnd.chance(5) && !furnace.isInProgress())
			furnace.spawnAction(MonasteryFurnaceEvent.FURNACE_ROOM, true);

		super.onEvtDead(killer);
	}
}