package handler.items;

import gnu.trove.set.hash.TIntHashSet;

import java.util.List;

import l2ft.gameserver.data.xml.holder.SkillAcquireHolder;
import l2ft.gameserver.handler.items.IItemHandler;
import l2ft.gameserver.handler.items.ItemHandler;
import l2ft.gameserver.model.Playable;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.Skill;
import l2ft.gameserver.model.SkillLearn;
import l2ft.gameserver.model.items.ItemInstance;
import l2ft.gameserver.network.l2.components.SystemMsg;
import l2ft.gameserver.network.l2.s2c.MagicSkillUse;
import l2ft.gameserver.network.l2.s2c.SkillList;
import l2ft.gameserver.network.l2.s2c.SystemMessage2;
import l2ft.gameserver.tables.SkillTable;
import handler.items.ScriptItemHandler;
import l2ft.gameserver.scripts.ScriptFile;

public class Spellbooks extends ScriptItemHandler implements ScriptFile
{
	private int[] _itemIds = null;

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	public Spellbooks()
	{
		TIntHashSet list = new TIntHashSet();
		List<SkillLearn> l = SkillAcquireHolder.getInstance().getAllNormalSkillTreeWithForgottenScrolls();
		for(SkillLearn learn : l)
			list.add(learn.getItemId());

		_itemIds = list.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;

		Player player = (Player) playable;

		if(item.getCount() < 1)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return false;
		}

		List<SkillLearn> list = SkillAcquireHolder.getInstance().getSkillLearnListByItemId(player, item.getItemId());
		if(list.isEmpty())
		{
			player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		// проверяем ли есть нужные скилы
		boolean alreadyHas = true;
		for(SkillLearn learn : list)
		{
			if(player.getSkillLevel(learn.getId()) != learn.getLevel())
			{
				alreadyHas = false;
				break;
			}
		}
		if(alreadyHas)
		{
			player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		// проверка по уровне
		boolean wrongLvl = false;
		for(SkillLearn learn : list)
		{
			if(player.getLevel() < learn.getMinLevel())
				wrongLvl = true;
		}

		if(wrongLvl)
		{
			player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		if(!player.consumeItem(item.getItemId(), 1L))
			return false;

		for(SkillLearn skillLearn :  list)
		{
			Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
			if(skill == null)
				continue;

			player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(),  skill.getLevel()));

			player.addSkill(skill, true);
		}

		player.updateStats();
		player.sendPacket(new SkillList(player));
		// Анимация изучения книги над головой чара (на самом деле, для каждой книги своя анимация, но они одинаковые)
		player.broadcastPacket(new MagicSkillUse(player, player, 2790, 1, 1, 0));
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}