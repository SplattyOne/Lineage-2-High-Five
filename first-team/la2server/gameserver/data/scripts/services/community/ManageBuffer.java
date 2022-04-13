package services.community;

import l2ft.gameserver.Config;
import l2ft.gameserver.data.htm.HtmCache;
import l2ft.gameserver.handler.bbs.CommunityBoardManager;
import l2ft.gameserver.handler.bbs.ICommunityBoardHandler;
import l2ft.gameserver.dao.CommunityBufferDAO;
import l2ft.gameserver.model.ManageBbsBuffer;
import l2ft.gameserver.model.ManageBbsBuffer.SBufferScheme;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.Summon;
import l2ft.gameserver.model.Effect;
import l2ft.gameserver.model.Skill;
import l2ft.gameserver.model.base.TeamType;
import l2ft.gameserver.model.Zone.ZoneType;
import l2ft.gameserver.tables.SkillTable;
import l2ft.gameserver.scripts.Events;
import l2ft.gameserver.skills.EffectType;
import l2ft.gameserver.skills.effects.EffectTemplate;
import l2ft.gameserver.stats.Env;
import l2ft.gameserver.network.l2.s2c.ShowBoard;
import l2ft.gameserver.scripts.Functions;
import l2ft.gameserver.scripts.ScriptFile;
import l2ft.gameserver.database.DatabaseFactory;
import l2ft.gameserver.utils.BbsUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageBuffer  extends Functions implements ScriptFile, ICommunityBoardHandler {

    static final Logger _log = LoggerFactory.getLogger(ManageBuffer.class);
	private int time = Config.COMMUNITYBOARD_BUFF_TIME;
	private int time_dance_song = Config.COMMUNITYBOARD_BUFF_SONGDANCE_TIME;
	
    @Override
	public void onLoad(){
		if(Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: Buffer Community service loaded.");
			CommunityBufferDAO.getInstance().select();
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload(){
		if(Config.COMMUNITYBOARD_ENABLED)
		{
			ManageBbsBuffer.getSchemeList().clear();
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public void onShutdown(){}
	
	@Override
	public String[] getBypassCommands(){
		return new String[] {
				"_bbsbuff",
				"_bbsbaim",
				"_bbsbsingle",
				"_bbsbsave",
				"_bbsbrestore",
				"_bbsbdelete",
				"_bbsbregen",
				"_bbsbcansel",
				"_bbsblist"
				};
	}

	@Override
	public void onBypassCommand(Player player, String bypass){
		String html = "";

		if(!CheckCondition(player))
			return;

		if(bypass.startsWith("_bbsbuff"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			ShowHtml(mBypass.length == 1 ? "index" : mBypass[1], player);
		}
		if(bypass.startsWith("_bbsblist"))
		{
			if(!CheckCondition(player))
				return;
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			int pice = 0;
			if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
			{
				if(player.getLevel() < 20)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_NG_GR;
				else if (player.getLevel() >= 20 && player.getLevel() <40)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_D_GR;
				else if (player.getLevel() >= 40 && player.getLevel() <52)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_C_GR;
				else if (player.getLevel() >= 52 && player.getLevel() <61)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_B_GR;
				else if (player.getLevel() >= 61 && player.getLevel() <76)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_A_GR;
				else if (player.getLevel() >= 76 && player.getLevel() <80)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S_GR;
				else if (player.getLevel() >= 80 && player.getLevel() <84)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S80_GR;
				else
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S84_GR;
			}
			else
				pice = Config.COMMUNITYBOARD_BUFF_PICE * (mBypass[1].startsWith("mage") ? Config.COMMUNITI_LIST_MAGE_SUPPORT.size() : Config.COMMUNITI_LIST_FIGHTER_SUPPORT.size());

			if(player.getAdena() < pice)
			{
				if (player.isLangRus())
					player.sendMessage("Недостаточно сердств!");
				else 
					player.sendMessage("It is not enough money!");
				ShowHtml(mBypass[2], player);
				return;
			}

			GroupBuff(player, mBypass[1].startsWith("mage") ? Config.COMMUNITI_LIST_MAGE_SUPPORT : Config.COMMUNITI_LIST_FIGHTER_SUPPORT);
			player.reduceAdena(pice, true);
			ShowHtml(mBypass[2], player);
		}
		else if(bypass.startsWith("_bbsbsingle"))
		{
			if(!CheckCondition(player))
				return;
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");

			Summon pet = player.getPet();
			int id = Integer.parseInt(mBypass[1]);
			int lvl = Integer.parseInt(mBypass[2]);               
			int pice = 0;
			if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
			{
				if (player.getLevel() < 20)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_NG;
				else if (player.getLevel() >= 20 && player.getLevel() <40)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_D;
				else if (player.getLevel() >= 40 && player.getLevel() <52)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_C;
				else if (player.getLevel() >= 52 && player.getLevel() <61)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_B;
				else if (player.getLevel() >= 61 && player.getLevel() <76)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_A;
				else if (player.getLevel() >= 76 && player.getLevel() <80)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S;
				else if (player.getLevel() >= 80 && player.getLevel() <84)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S80;
				else
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S84;
			}
			else
				pice = Config.COMMUNITYBOARD_BUFF_PICE;

			String page = mBypass[3];
			
			if(player.getAdena() < pice)
			{
				if (player.isLangRus())
					player.sendMessage("Недостаточно сердств!");
				else 
					player.sendMessage("It is not enough money!");
				ShowHtml(page, player);
				return;
			}
			
			if(!Config.COMMUNITYBOARD_BUFF_ALLOW.contains(id))
			{
				if (player.isLangRus())
					player.sendMessage("Недопустимый эффект!");
				else
					player.sendMessage("Invalid effect!");
				ShowHtml(page, player);
				return;
			}
			
			Skill skill = SkillTable.getInstance().getInfo(id, lvl);
			setTime(skill);
			if(!player.getVarB("isPlayerBuff") && pet != null)
			{
				if(id == 1413 && pet.getEffectList().getEffectsBySkillId(1363) != null)
					pet.getEffectList().stopEffect(1363);
				for(EffectTemplate et : skill.getEffectTemplates())
				{	
					Env env = new Env(pet, pet, skill);
					Effect effect = et.getEffect(env);
					effect.setPeriod(time);
					pet.getEffectList().addEffect(effect);
					pet.updateStats();
					pet.sendChanges();
					pet.updateEffectIcons();
				}
			}
			else
			{
				if(id == 1413 && player.getEffectList().getEffectsBySkillId(1363) != null)
					player.getEffectList().stopEffect(1363);
				for(EffectTemplate et : skill.getEffectTemplates())
				{	
					Env env = new Env(player, player, skill);
					Effect effect = et.getEffect(env);
					effect.setPeriod(time);
					player.getEffectList().addEffect(effect);
					player.sendChanges();
					player.updateEffectIcons();
					player.updateEffectIconsImpl();
				}
			}
			player.reduceAdena(pice, true);
			ShowHtml(page, player);
		}
		else if(bypass.startsWith("_bbsbaim")){
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			
			player.setVar("isPlayerBuff", player.getVarB("isPlayerBuff") ? "0" : "1", -1);
			
			ShowHtml(mBypass[1], player);
		}
		else if(bypass.startsWith("_bbsbregen"))
		{
			if(!CheckCondition(player))
				return;
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			
			int pice = 0;
			if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
			{
				if (player.getLevel() < 20)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_NG;
				else if (player.getLevel() >= 20 && player.getLevel() <40)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_D;
				else if (player.getLevel() >= 40 && player.getLevel() <52)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_C;
				else if (player.getLevel() >= 52 && player.getLevel() <61)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_B;
				else if (player.getLevel() >= 61 && player.getLevel() <76)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_A;
				else if (player.getLevel() >= 76 && player.getLevel() <80)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S;
				else if (player.getLevel() >= 80 && player.getLevel() <84)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S80;
				else
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S84;
			}
			else
				pice = Config.COMMUNITYBOARD_BUFF_PICE;

			if(player.getAdena() < pice * 10)
			{
				if (player.isLangRus())
					player.sendMessage("Недостаточно сердств!");
				else 
					player.sendMessage("It is not enough money!");
				ShowHtml(mBypass[1], player);
				return;
			}
			
			if(!player.getVarB("isPlayerBuff") && player.getPet() != null)
			{
				player.getPet().setCurrentHpMp(player.getPet().getMaxHp(), player.getPet().getMaxMp());
				player.getPet().setCurrentCp(player.getPet().getMaxCp());
			}
			else
			{
				player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
				player.setCurrentCp(player.getMaxCp());
			}
			player.reduceAdena(pice * 10, true);
			ShowHtml(mBypass[1], player);
		}
		else if(bypass.startsWith("_bbsbcansel"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			if(!player.getVarB("isPlayerBuff") && player.getPet() != null)
			{
				if(player.getEffectList().getEffectsBySkillId(Skill.SKILL_RAID_CURSE) == null)
					player.getPet().getEffectList().stopAllEffects();
			}
			else
				if(player.getEffectList().getEffectsBySkillId(Skill.SKILL_RAID_CURSE) == null)
					player.getEffectList().stopAllEffects();
			ShowHtml(mBypass[1], player);
		}
		else if(bypass.startsWith("_bbsbsave"))
		{
			if(!CheckCondition(player))
				return;
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			try
			{
				String name = mBypass[2].substring(1);
					
				SBufferScheme scheme = new SBufferScheme();
				if(ManageBbsBuffer.getCountOnePlayer(player.getObjectId()) >= 3)
				{
					if(player.isLangRus())
						player.sendMessage("Превышено максимально допустимое количество схем!");
					else
						player.sendMessage("Exceeded the number of schemes!");
					ShowHtml(mBypass[1], player);
					return;
				}
				if(ManageBbsBuffer.existName(player.getObjectId(), name))
				{
					if(player.isLangRus())
						player.sendMessage("Схема с таким названием уже существует!");
					else
						player.sendMessage("Scheme with that name already exists!");
					ShowHtml(mBypass[1], player);
					return;
				}
				
				if(name.length() > 15)
					name = name.substring(0, 15);
					
				if(name.length() > 0)
				{
					scheme.obj_id = player.getObjectId();
					scheme.name = name;

					Effect skill[] = player.getEffectList().getAllFirstEffects();
					for(int i = 0; i < skill.length; i++)
					{
						if(Config.COMMUNITYBOARD_BUFF_ALLOW.contains(skill[i].getSkill().getId()))
							scheme.skills_id.add(skill[i].getSkill().getId());
					}
					CommunityBufferDAO.getInstance().insert(scheme, player);
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				player.sendMessage(player.isLangRus() ? "Вы не ввели имя для сохранения!" : "You did not enter a name to save!");
				return;
			}
			
			ShowHtml(mBypass[1], player);
		}
		else if(bypass.startsWith("_bbsbdelete")){
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			
			CommunityBufferDAO.getInstance().delete(ManageBbsBuffer.getScheme(Integer.parseInt(mBypass[1]), player.getObjectId()));
			
			ShowHtml(mBypass[3], player);
		}
		else if(bypass.startsWith("_bbsbrestore")){
			if(!CheckCondition(player))
				return;
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			
			int pice = 0;
			if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
			{
				if (player.getLevel() < 20)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_NG_GR;
				else if (player.getLevel() >= 20 && player.getLevel() <40)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_D_GR;
				else if (player.getLevel() >= 40 && player.getLevel() <52)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_C_GR;
				else if (player.getLevel() >= 52 && player.getLevel() <61)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_B_GR;
				else if (player.getLevel() >= 61 && player.getLevel() <76)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_A_GR;
				else if (player.getLevel() >= 76 && player.getLevel() <80)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S_GR;
				else if (player.getLevel() >= 80 && player.getLevel() <84)
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S80_GR;
				else
					pice = Config.COMMUNITYBOARD_BUFF_PICE_S84_GR;
			}
			else
				pice = Config.COMMUNITYBOARD_BUFF_SAVE_PICE;

			if(player.getAdena() < pice)
			{
				if (player.isLangRus())
					player.sendMessage("Недостаточно сердств!");
				else 
					player.sendMessage("It is not enough money!");
				ShowHtml(mBypass[3], player);
				return;
			}
			
			SBufferScheme scheme = ManageBbsBuffer.getScheme(Integer.parseInt(mBypass[1]), player.getObjectId());
			GroupBuff(player, scheme.skills_id);
			player.reduceAdena(pice, true);
			ShowHtml(mBypass[3], player);
		}
	}
	
	
	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5){}
	
	private void ShowHtml(String name, Player player)
	{

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/buffer/" + name + ".htm", player);
		if (player.isLangRus())
			html = html.replaceFirst("%aim%", player.getVarB("isPlayerBuff") ? "Персонаж" : "Питомец");
		else
			html = html.replaceFirst("%aim%", player.getVarB("isPlayerBuff") ? "Character" : "Pet");
			
		if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
		{
			if (player.getLevel() < 20)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_NG));
			else if (player.getLevel() >= 20 && player.getLevel() <40)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_D));
			else if (player.getLevel() >= 40 && player.getLevel() <52)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_C));
			else if (player.getLevel() >= 52 && player.getLevel() <61)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_B));
			else if (player.getLevel() >= 61 && player.getLevel() <76)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_A));
			else if (player.getLevel() >= 76 && player.getLevel() <80)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_S));
			else if (player.getLevel() >= 80 && player.getLevel() <84)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_S80));
			else
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_S84));
		}	
		else
			html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE));
 
		if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
		{
			if (player.getLevel() < 20)
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_NG_GR));
			else if (player.getLevel() >= 20 && player.getLevel() <40)
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_D_GR));
			else if (player.getLevel() >= 40 && player.getLevel() <52)
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_C_GR));
			else if (player.getLevel() >= 52 && player.getLevel() <61)
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_B_GR));
			else if (player.getLevel() >= 61 && player.getLevel() <76)
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_A_GR));
			else if (player.getLevel() >= 76 && player.getLevel() <80)
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_S_GR));
			else if (player.getLevel() >= 80 && player.getLevel() <84)
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_S80_GR));
			else
				html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_PICE_S84_GR));
		}
		else
			html = html.replace("%group_pice%", GetStringCount(Config.COMMUNITYBOARD_BUFF_SAVE_PICE));

		StringBuilder content = new StringBuilder("");
		content.append("<table width=120>");
		for(SBufferScheme sm : ManageBbsBuffer.getSchemePlayer(player.getObjectId()))
		{
			content.append("<tr>");
			content.append("<td>");
			content.append("<button value=\"" + sm.name + "\" action=\"bypass _bbsbrestore:" + sm.id + ":" + sm.name + ":" + name + ";\" width=105 height=20 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">");
			content.append("</td>");
			content.append("<td>");
			content.append("<button value=\"-\" action=\"bypass _bbsbdelete:" + sm.id + ":" + sm.name + ":" + name + ";\" width=20 height=20 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.Button_DF\">");
			content.append("</td>");
			content.append("</tr>");
		}
		content.append("</table>");
		
		html = html.replace("%list_sheme%",  content.toString());
		html = BbsUtil.htmlBuff(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private void GroupBuff(Player player, List<Integer> list){
	
		if(!CheckCondition(player))
			return;
		int time = Config.COMMUNITYBOARD_BUFF_TIME;
		Summon pet = player.getPet();
		Skill skill = null;
		
		for(int i : list)
		{
			int lvl = SkillTable.getInstance().getBaseLevel(i);
			
			if(!Config.COMMUNITYBOARD_BUFF_ALLOW.contains(i))
				continue;
			
			skill = SkillTable.getInstance().getInfo(i, lvl);
			if(!player.getVarB("isPlayerBuff") && pet != null)
			{
				if(i == 1413 && pet.getEffectList().getEffectsBySkillId(1363) != null)
					pet.getEffectList().stopEffect(1363);
				for(EffectTemplate et : skill.getEffectTemplates())
				{	
					Env env = new Env(pet, pet, skill);
					Effect effect = et.getEffect(env);
					if(skill.isMusic())
						effect.setPeriod(time_dance_song);
					else
						effect.setPeriod(time);
					pet.getEffectList().addEffect(effect);
					pet.updateEffectIcons();
				}
			}
			else
			{
				if(i == 1413 && player.getEffectList().getEffectsBySkillId(1363) != null)
					player.getEffectList().stopEffect(1363);
				for(EffectTemplate et : skill.getEffectTemplates())
				{	
					Env env = new Env(player, player, skill);
					Effect effect = et.getEffect(env);
					if(skill.isMusic())
						effect.setPeriod(time_dance_song);
					else
						effect.setPeriod(time);
					player.getEffectList().addEffect(effect);
					player.sendChanges();
					player.updateEffectIcons();
				}
			}
		}
		
	}
	
	private static boolean CheckCondition(Player player){
		if(player == null)
            return false;

		if(player.isDead())
            return false;

		if(Config.COMMUNITYBOARD_BUFFER_MAX_LVL_ALLOW && player.getLevel() > Config.COMMUNITYBOARD_BUFFER_MAX_LVL)
		{
			player.sendMessage(player.isLangRus() ? "Вам запрещено пользоваться этой функцией." : "You are not allowed to use this feature.");
			return false;
		}
		
		if(!Config.USE_BBS_BUFER_IS_CURSE_WEAPON && player.isCursedWeaponEquipped())
		{
			player.sendMessage(player.isLangRus() ? "Вам запрещено пользоваться этой функцией." : "You are not allowed to use this feature.");
			return false;
		}

        if (!Config.COMMUNITYBOARD_BUFFER_ENABLED) 
        {
        	if(player.isLangRus())
        		player.sendMessage("Функция баффа отключена.");
        	else
        		player.sendMessage("Buff off function.");
            return false;
        }

		if(!Config.USE_BBS_BUFER_IS_COMBAT && (player.isInDuel() || player.isInCombat() || player.isAttackingNow()))
		{
			if(player.isLangRus())
				player.sendMessage("Во время боя нельзя использовать данную функцию.");
			else
				player.sendMessage("During combat, you can not use this feature.");
			 return false;
		}

        if (player.isInOlympiadMode()) 
        {
        	if(player.isLangRus())
				player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
        	else
        		player.sendMessage("During the Olympics you can not use this feature.");
            return false;
        }

        if (player.getReflection().getId() != 0 && !Config.COMMUNITYBOARD_INSTANCE_ENABLED) 
        {
        	if(player.isLangRus())
        		player.sendMessage("Бафф доступен только в обычном мире.");
        	else
        		player.sendMessage("Buff is only available in the real world.");
            return false;
        }

        if (!Config.COMMUNITYBOARD_EVENTS_ENABLED) 
        {
            if(player.getTeam() != TeamType.NONE) 
            {
            	if (player.isLangRus())
            		player.sendMessage("Нельзя использовать бафф во время эвентов.");
            	else
            		player.sendMessage("You can not use the buff during Events.");
                return false;
            }
        }

		if(!Config.COMMUNITYBOARD_BUFFER_SIEGE_ENABLED && player.isInZone(ZoneType.SIEGE))
		{
        	if(player.isLangRus())
        		player.sendMessage("В зоне, находящейся в осаде, использовать телепорт запрещено.");
        	else
        		player.sendMessage("In the zone, located in the siege, use the teleport is prohibited.");
            return false;
		}

		if(!Config.COMMUNITYBOARD_BUFFER_NO_IS_IN_PEACE_ENABLED && !player.isInPeaceZone())
		{
			player.sendMessage(player.isLangRus() ? "Эта функция доступна только в мирной зоне!" : "This feature is only available in a peaceful area!");
			return false;}

		return true;
	}

	private void setTime(Skill skill)
	{
		if(skill.getId() == 1356 || skill.getId() == 4699 || skill.getId() == 4700 || 
			skill.getId() == 4702 || skill.getId() == 4703 || skill.getId() == 1363 || 
			skill.getId() == 1355 || skill.getId() == 1357)
			time = Config.COMMUNITYBOARD_BUFF_PETS_TIME;
		else if(skill.getId() >= 1499 && skill.getId() <= 1504)
			time = Config.COMMUNITYBOARD_BUFF_COMBO_TIME;
		else if(skill.isMusic())
			time = Config.COMMUNITYBOARD_BUFF_SONGDANCE_TIME;
		else
			time = Config.COMMUNITYBOARD_BUFF_TIME;
	}
}