package npc.model;

import l2ft.gameserver.cache.Msg;
import l2ft.gameserver.instancemanager.naia.NaiaTowerManager;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.instances.NpcInstance;
import l2ft.gameserver.network.l2.s2c.MagicSkillUse;
import l2ft.gameserver.network.l2.s2c.SystemMessage;
import l2ft.gameserver.templates.npc.NpcTemplate;
import ai.hellbound.NaiaLock;

/**
 * @author pchayka
 */
public class NaiaControllerInstance extends NpcInstance
{
	public NaiaControllerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("tryenter"))
		{
			if(NaiaLock.isEntranceActive())
			{
				//instance
				if(!player.isInParty())
				{
					player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
					return;
				}
				if(!player.getParty().isLeader(player))
				{
					player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
					return;
				}
				for(Player member : player.getParty().getPartyMembers())
				{
					if(member.getLevel() < 80)
					{
						player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
						return;
					}
					if(!member.isInRange(this, 500))
					{
						player.sendPacket(new SystemMessage(SystemMessage.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member));
						return;
					}
				}
				NaiaTowerManager.startNaiaTower(player);

				broadcastPacket(new MagicSkillUse(this, this, 5527, 1, 0, 0));
				doDie(null);
			}
			else
			{
				broadcastPacket(new MagicSkillUse(this, this, 5527, 1, 0, 0));
				doDie(null);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}