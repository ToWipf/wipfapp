package org.wipf.elcd.model;

/**
 * @author wipf
 *
 */
public class MLogger {
	/**
	 * @param s
	 */
	public static void info(Object s) {
		System.out.println("INFO|" + MTime.dateTime() + "| " + s);
	}

	/**
	 * @param s
	 */
	public static void err(Object s) {
		System.err.println("WARN|" + MTime.dateTime() + "| " + s);
	}
}
