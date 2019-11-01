package org.wipf.elcd.model;

import java.sql.ResultSet;
import java.sql.Statement;

import org.wipf.elcd.model.struct.Telegram;
import org.wipf.elcd.model.struct.TicTacToe;

/**
 * @author wipf
 *
 */
public class MTicTacToe {

	/**
	 * 
	 */
	public static void initDB() {
		try {
			Statement stmt = MsqlLite.getDB();
			stmt.executeUpdate(
					"CREATE TABLE IF NOT EXISTS tictactoe (chatid INTEGER UNIQUE, feld TEXT, msgdate INTEGER, type TEXT);");

		} catch (Exception e) {
			MLogger.warn("initDB tictactoe " + e);
		}
	}

	public static String input(Telegram t) {
		String res = game(t);
		if (res == null || res.length() == 0) {
			return "Fehler im Spiel. Neustarten mit 'ttt neu'";
		}
		return res;
	}

	/**
	 * @param sTelegramSetTo
	 * @return
	 */
	private static String game(Telegram t) {
		TicTacToe ttt = loadTicTacToe(t.getChatID());
		;
		String sAction = t.getMessageWord(1);
		if (sAction == null) {
			return "Anleitung mit TicTacToe help";
		}

		switch (sAction) {
		case "setzen":
		case "setze":
		case "set":
		case "se":
		case "s":
			String sHelpAuswertung;
			// spiel vorhanden
			if (ttt == null) {
				return "Es wurde noch kein Spiel gestartet"; // TODO autocreate new game ?
			}
			// auswertung
			sHelpAuswertung = helpAuswertung(ttt);
			if (sHelpAuswertung != null) {
				return sHelpAuswertung;
			}
			ttt.setByTelegram(t);
			// setze feld
			if (!ttt.setByNummer(t.getMessageInt(2), 'X')) {
				return "Feld konnte nicht gesetzt werden";
			} else {
				saveTicTacToe(ttt); // save game
			}
			// auswertung
			sHelpAuswertung = helpAuswertung(ttt);
			if (sHelpAuswertung != null) {
				return sHelpAuswertung;
			}
			// set cpu
			if (!ttt.cpuSetzen('O')) {
				return "CPU konnte nicht MsqlLite.setzen";
			} else {
				saveTicTacToe(ttt); // save game
			}
			// auswertung
			sHelpAuswertung = helpAuswertung(ttt);
			if (sHelpAuswertung != null) {
				return sHelpAuswertung;
			}
			// Spielfeld ausgeben
			return ttt.tttToNiceString();
		case "new":
		case "neu":
		case "ne":
		case "n":
			ttt = new TicTacToe("FFFFFFFFF");
			ttt.setByTelegram(t);
			saveTicTacToe(ttt);
			return "Setzen mit 'ttt se NR'\n\n" + ttt.tttToNiceString();
		case "show":
		case "sh":
			return ttt.tttToNiceString();
		case "raw":
		case "ra":
		case "r":
			return ttt.tttToString();
		default:
			return "Anleitung:\n\nttt neu: Neues Spiel\nttt setze NR: Setzen\nttt show: Zeige feld";
		}
	}

	/**
	 * @param ttt
	 * @return
	 */
	private static String helpAuswertung(TicTacToe ttt) {
		Character win = ttt.auswertung();
		if (win != null) {
			if (win == 'U') {
				return "Unentschieden\n\n" + ttt.tttToNiceString() + "\n\n neues Spiel mit 'ttt neu'";
			} else if (win == 'X') {
				return "Du hast gewonnen\n\n" + ttt.tttToNiceString() + "\n\n neues Spiel mit 'ttt neu'";
			} else if (win == 'O') {
				return "Du hast verloren\n\n" + ttt.tttToNiceString() + "\n\n neues Spiel mit 'ttt neu'";
			}
		}
		return null;
	}

	/**
	 * @param ttt
	 * @return
	 */
	private static Boolean saveTicTacToe(TicTacToe ttt) {
		try {
			Statement stmt = MsqlLite.getDB();
			stmt.execute(
					"INSERT OR REPLACE INTO tictactoe (chatid, feld, msgdate, type) VALUES " + "('" + ttt.getChatID()
							+ "','" + ttt.getFieldString() + "','" + ttt.getDate() + "','" + ttt.getType() + "')");
			return true;
		} catch (Exception e) {
			MLogger.warn("setTicTacToe " + e);
			return false;
		}
	}

	/**
	 * @param sChatid
	 * @return
	 */
	private static TicTacToe loadTicTacToe(Integer nChatid) {
		try {
			Statement stmt = MsqlLite.getDB();
			ResultSet rs = stmt.executeQuery("SELECT * FROM tictactoe WHERE chatid = '" + nChatid + "';");
			TicTacToe ttt = new TicTacToe(rs.getString("feld"));
			// ttt.setChatID(rs.getInt("chatid")); weitere felder sind nicht nötig -> werden
			// neu befüllt
			rs.close();
			return ttt;
		} catch (Exception e) {
			// Kann vorkommen wenn kein spiel aktiv ist
			// MLogger.warn("getTicTacToe " + e);
		}
		return null;
	}
}