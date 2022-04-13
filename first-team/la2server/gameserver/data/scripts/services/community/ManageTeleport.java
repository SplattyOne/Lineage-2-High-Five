package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2ft.commons.dbutils.DbUtils;
import l2ft.gameserver.Config;
import l2ft.gameserver.data.htm.HtmCache;
import l2ft.gameserver.database.DatabaseFactory;
import l2ft.gameserver.handler.bbs.CommunityBoardManager;
import l2ft.gameserver.handler.bbs.ICommunityBoardHandler;
import l2ft.gameserver.model.Player;
import l2ft.gameserver.model.World;
import l2ft.gameserver.model.Zone;
import l2ft.gameserver.model.Zone.ZoneType;
import l2ft.gameserver.model.base.TeamType;
import l2ft.gameserver.model.entity.Reflection;
import l2ft.gameserver.network.l2.s2c.ShowBoard;
import l2ft.gameserver.scripts.Functions;
import l2ft.gameserver.scripts.ScriptFile;
import l2ft.gameserver.utils.BbsUtil;
import l2ft.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageTeleport  extends Functions implements ScriptFile, ICommunityBoardHandler
{
	static final Logger _log = LoggerFactory.getLogger(ManageTeleport.class);
	
    @Override
	public void onLoad()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: Teleport Community service loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
			CommunityBoardManager.getInstance().removeHandler(this);
	}

	@Override
	public void onShutdown(){}
	
	@Override
	public String[] getBypassCommands()
	{
		return new String[] {
				"_bbsteleport",
				"_bbsgotoxyz",
				"_bbstsave",
				"_bbstrestore",
				"_bbstdelete"
				};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		String html = "";

		if(!CheckCondition(player))
			return;
			
		if(bypass.startsWith("_bbsteleport"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			ShowHtml(mBypass.length == 1 ? "index" : mBypass[1], player);
		}
		else if(bypass.startsWith("_bbsgotoxyz"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			
			int cX = Integer.parseInt(mBypass[2]);
			int cY = Integer.parseInt(mBypass[3]);
			int cZ = Integer.parseInt(mBypass[4]);
			
			List<Zone> zones = new ArrayList<Zone>();
			World.getZones(zones, new Location(cX, cY, cZ), Reflection.createReflection(0));
			
			int pice = 0;
			if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
			{
				if (player.getLevel() < 20)
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_NG;
				else if (player.getLevel() >= 20 && player.getLevel() <40)
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_D;
				else if (player.getLevel() >= 40 && player.getLevel() <52)
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_C;
				else if (player.getLevel() >= 52 && player.getLevel() <61)
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_B;
				else if (player.getLevel() >= 61 && player.getLevel() <76)
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_A;
				else if (player.getLevel() >= 76 && player.getLevel() <80)
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_S;
				else if (player.getLevel() >= 80 && player.getLevel() <84)
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_S80;
				else
					pice = Config.COMMUNITYBOARD_TELEPORT_PICE_S84;
			}
			else
				pice = Config.COMMUNITYBOARD_TELE_PICE;
			String page = mBypass[1];
			
			if(player.getAdena() < pice)
			{
				if (player.isLangRus())
					player.sendMessage("Недостаточно сердств!");
				else 
					player.sendMessage("It is not enough money!");
				ShowHtml(page, player);
				return;
			}

			for(Zone zone : zones)
				if(zone.getType() == ZoneType.SIEGE)
				{
					if (player.isLangRus())
						player.sendMessage("Невозможно телепортироваться в местность, где активна осада!");
					else 
						player.sendMessage("Unable to teleport to the area where the siege is active!");
					ShowHtml(page, player);
					return;
				}
			player.teleToLocation(cX, cY, cZ, 0);			
				
			player.reduceAdena(pice, true);
			ShowHtml(page, player);
		}
		else if(bypass.startsWith("_bbstsave"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			int pice = Config.COMMUNITYBOARD_SAVE_TELE_PICE;
			try
			{
				String name = mBypass[2].substring(1);
				
				if(Config.COMMUNITYBOARD_SAVE_TELE_PREMIUM)
				{
					if(player.getNetConnection().getBonus() <= 1)
						{
						if (player.isLangRus())
							player.sendMessage("Доступно только для игроков с ПА!");
						else 
							player.sendMessage("Available only to players from PA!");
							ShowHtml(mBypass[1], player);
					return;
						}
				}
				if (player.isInZone("[baium_epic]") || player.isInZone("[antharas_epic]") || player.isInZone("[valakas_epic]") || player.isInZone("[sailren_epic]") || player.isInZone("[baylor_epic]") || player.isInZone("[queen_ant_epic]") || player.isInZone("[FourSepulchers1]") || player.isInZone("[FourSepulchers2]") || player.isInZone("[FourSepulchers3]") || player.isInZone("[FourSepulchers4]") || player.isInZone("[Frintezza]") || player.isInZone("[LastImperialTomb]") || player.isInZone("[vanhalter_epic]") || player.isInZone("[beleth_epic]") ||player.isInZone("[freya_normal_epic]") || player.isInZone("[tiat_room_epic]") || player.isInZone("[freya_landing_room_epic]"))
				{
					if (player.isLangRus())
						player.sendMessage("Здесь сохранить точку нельзя!");
					else 
						player.sendMessage("It can store the point!");
					ShowHtml(mBypass[1], player);
					return;
				}
				
				if(player.getAdena() < pice)
				{
					if (player.isLangRus())
						player.sendMessage("Недостаточно сердств!");
					else 
						player.sendMessage("It is not enough money!");
					ShowHtml(mBypass[1], player);
					return;
				}
				
				if(getTeleCount(player) >= Config.COMMUNITYBOARD_SAVE_TELE_COUNT)
				{
					if (player.isLangRus())
						player.sendMessage("Превышено максимално допустимое количество точек возвращения!");
					else
						player.sendMessage("Exceeded the maximum number of return points!");
					ShowHtml(mBypass[1], player);
					return;
				}
				if(!CheckTeleName(player, name))
				{
					if (player.isLangRus())
						player.sendMessage("Точка с таким названием уже существует!");
					else
						player.sendMessage("The point with this name already exists!");
					ShowHtml(mBypass[1], player);
					return;
				}
				
				if(name.length() > 15)
					name = name.substring(0, 15);
					
				if(name.length() > 0)
				{
					Connection con = null;
					PreparedStatement stmt = null;		
					try
					{
						con = DatabaseFactory.getInstance().getConnection();
						stmt = con.prepareStatement("INSERT INTO bbs_pointsave (charId,name,xPos,yPos,zPos) VALUES(?,?,?,?,?)");
						stmt.setInt(1, player.getObjectId());
						stmt.setString(2, name);
						stmt.setInt(3, player.getX());
						stmt.setInt(4, player.getY());
						stmt.setInt(5, player.getZ());
						stmt.execute();
					}
					catch(Exception e)
					{}
					finally
					{
						DbUtils.closeQuietly(con, stmt);
					}
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				if(player.isLangRus())
					player.sendMessage("Вы не ввели имя для сохранения!");
				else
					player.sendMessage("You did not enter a name to save!");
				return;
			}
			player.reduceAdena(pice, true);
			ShowHtml(mBypass[1], player);
		}
		else if(bypass.startsWith("_bbstdelete"))
		{
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			
			Connection con = null;
			PreparedStatement statement = null;

			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("DELETE FROM bbs_pointsave WHERE charId=? AND TpId=?;");
				statement.setInt(1, player.getObjectId());
				statement.setInt(2, Integer.parseInt(mBypass[2]));
				statement.execute();
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
			ShowHtml(mBypass[1], player);
		}
	}
	
	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5){}
	
	private static int getTeleCount(Player player)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		int count = 0;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT count(*) as cnt FROM bbs_pointsave WHERE `charId` = ?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			if(rset.next())
				count = rset.getInt("cnt");
		}
		catch(Exception e)
		{}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return count;
	}
	
	private static boolean CheckTeleName(Player player, String name)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT count(*) as cnt FROM bbs_pointsave WHERE `charId` = ? AND `name` = ?");
			statement.setInt(1, player.getObjectId());
			statement.setString(2, name);
			rset = statement.executeQuery();
			if(rset.next() && rset.getInt("cnt") == 0)
				return true;
		}
		catch(Exception e)
		{}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return false;
	}
	
	private void ShowHtml(String name, Player player)
	{

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/teleport/" + name + ".htm", player);
		
		if (Config.COMMUNITYBOARD_BOARD_ALT_ENABLED)
		{
			if (player.getLevel() < 20)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_NG));
			else if (player.getLevel() >= 20 && player.getLevel() <40)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_D));
			else if (player.getLevel() >= 40 && player.getLevel() <52)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_C));
			else if (player.getLevel() >= 52 && player.getLevel() <61)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_B));
			else if (player.getLevel() >= 61 && player.getLevel() <76)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_A));
			else if (player.getLevel() >= 76 && player.getLevel() <80)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_S));
			else if (player.getLevel() >= 80 && player.getLevel() <84)
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_S80));
			else
				html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELEPORT_PICE_S84));
		}
		else
			html = html.replace("%pice%", GetStringCount(Config.COMMUNITYBOARD_TELE_PICE));
		html = html.replace("%save_pice%", GetStringCount(Config.COMMUNITYBOARD_SAVE_TELE_PICE));
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM bbs_pointsave WHERE charId=?;");
            statement.setLong(1, player.getObjectId());
			rs = statement.executeQuery();
			StringBuilder content = new StringBuilder("");
			content.append("<table width=220>");
			while(rs.next())
			{
				content.append("<tr>");
                content.append("<td>");
                content.append("<button value=\"" + rs.getString("name") + "\" action=\"bypass _bbsgotoxyz:index:" + rs.getInt("xPos") + ":" + rs.getInt("yPos") + ":" + rs.getInt("zPos") + ";\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                content.append("</td>");
                content.append("<td>");
                content.append("<button value=\"Удалить\" action=\"bypass _bbstdelete:index:" + rs.getInt("TpId") + ";\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                content.append("</td>");
                content.append("</tr>");
			}
			content.append("</table>");
			
							if(Config.COMMUNITYBOARD_SAVE_TELE_PREMIUM)
				{
					if(player.getNetConnection().getBonus() <= 1)
						{
						html = html.replace("%list_teleport%",  "<table width=250><tr><td><center><font color=F2C202>Только для игроков с Премиум Аккаунтом</font></center></td></tr></table>");
						ShowBoard.separateAndSend(html, player);
						}
				}
				
			html = html.replace("%list_teleport%",  content.toString());
			ShowBoard.separateAndSend(BbsUtil.htmlBuff(html, player), player);
		}
		catch(Exception e)
		{}
		finally
		{
			DbUtils.closeQuietly(con, statement, rs);
		}
	}
	
	private static boolean CheckCondition(Player player)
	{
        if(!Config.COMMUNITYBOARD_TELEPORT_ENABLED)
		{
        	if (player.isLangRus())
        		player.sendMessage("Функция телепорта отключена.");
        	else
        		player.sendMessage("Teleport function is disabled.");
            return false;
        }

		if(player == null)
            return false;

		if(!Config.USE_BBS_TELEPORT_IS_COMBAT && (player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow()))
		{
			if (player.isLangRus())
				player.sendMessage("Во время боя нельзя использовать данную функцию.");
			else
				player.sendMessage("During combat, you can not use this feature.");
			return false;
		}

        if(player.isInOlympiadMode())
		{
        	if (player.isLangRus())
        		player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
        	else
        		player.sendMessage("During the Olympics you can not use this feature.");
            return false;
        }

        if(player.getReflection().getId() != 0 && !Config.COMMUNITYBOARD_INSTANCE_ENABLED)
		{
            player.sendMessage("Телепорт доступен только в обычном мире.");
            return false;
        }

        if (!Config.COMMUNITYBOARD_EVENTS_ENABLED)
		{
            if (player.getTeam() != TeamType.NONE)
			{
            	if (player.isLangRus())
            		player.sendMessage("Нельзя использовать телепорт во время эвентов.");
            	else
            		player.sendMessage("You can not use Teleport during Events.");
                return false;
            }
        }

		if(!Config.COMMUNITYBOARD_TELEPORT_SIEGE_ENABLED && player.isInZone(ZoneType.SIEGE))
		{
        	if (player.isLangRus())
        		player.sendMessage("В зоне, находящейся в осаде, использовать телепорт запрещено.");
        	else
        		player.sendMessage("In the zone, located in the siege, use the teleport is prohibited.");
            return false;
		}
        return true;
	}
}