package ai;

import l2ft.commons.util.Rnd;
import l2ft.gameserver.ai.CtrlIntention;
import l2ft.gameserver.ai.Fighter;
import l2ft.gameserver.data.xml.holder.NpcHolder;
import l2ft.gameserver.model.Creature;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.scripts.Functions;
import l2ft.gameserver.utils.Location;

/**
 * AI монахов в Monastery of Silence<br>
 * - агрятся на чаров с оружием в руках
 * - перед тем как броситься в атаку кричат
 *
 * @author SYS
 * fix Kekess
 */
public class MoSMonk extends Fighter
{
	public MoSMonk(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(20))
			Functions.npcSayCustomMessage(getActor(), "scripts.ai.MoSMonk.onIntentionAttack");
		super.onIntentionAttack(target);
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		if(target.getActiveWeaponInstance() == null)
			return false;
		return super.checkAggression(target);
	}

	// for quest _457_LostandFound 
	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;
			
		if(Rnd.chance(1))
		{
			if(Rnd.chance(10))
				spawnGumiel(actor);
		}

		super.onEvtDead(killer);
	}

	protected void spawnGumiel(NpcInstance actor)
	{
		try
		{
			NpcInstance npc = NpcHolder.getInstance().getTemplate(32759).getNewInstance();
			Location pos = Location.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
			npc.setSpawnedLoc(pos);
			npc.setReflection(actor.getReflection());
			npc.spawnMe(npc.getSpawnedLoc());
			Functions.npcSayCustomMessage(getActor(), "scripts.ai.MoSMonk.spawnGumiel");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}