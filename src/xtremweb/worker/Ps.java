/*
 * Copyrights     : CNRS
 * Author         : Oleg Lodygensky
 * Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
 * Web            : http://www.xtremweb-hep.org
 *
 *      This file is part of XtremWeb-HEP.
 *
 *    XtremWeb-HEP is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    XtremWeb-HEP is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package xtremweb.worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Get Activity, that is the CPU state %of CPU time used by user
 */

public class Ps {
	private static int[] pid = new int[1000];
	private static int[] us = new int[1000];
	private static int[] sy = new int[1000];
	private static int id;
	private static long somme;
	private static long userTot;
	private static long niceTot;
	private static long systemTot;
	private static long idleTot;

	private static void stat() {
		Runtime machine;
		machine = java.lang.Runtime.getRuntime();
		String ligne;
		Process ls;
		BufferedReader input;
		long jiffies;
		int index, i;
		try {
			// on recupere la liste des process
			ls = machine.exec("ls -A1 /proc");
			ls.waitFor();
			input = new BufferedReader(new InputStreamReader(ls.getInputStream()));

			id = 0;
			while (input.ready()) {
				ligne = input.readLine();
				pid[id] = Integer.valueOf(ligne).intValue();
				try (final BufferedReader pidstat = new BufferedReader(new FileReader("/proc/" + pid[id] + "/stat"))){
					// on ne garde que ce qui est numerique (i.e. pid)
					ligne = pidstat.readLine();
					pidstat.close();
					index = 0;
					for (i = 0; i < 13; i++) {
						index = ligne.indexOf(' ', index + 1);
					}
					// on recupere les jiffies user et system du process
					us[id] = Integer.parseInt(ligne.substring(index, ligne.indexOf(' ', index + 1)).trim());
					index = ligne.indexOf(' ', index + 1);
					sy[id] = Integer.parseInt(ligne.substring(index, ligne.indexOf(' ', index + 1)).trim());
					id++;

				} catch (final Exception e) {
				} // ce n'est pas numerique
			}

			// lecture de la premiere ligne de /proc/stat
			// "cpu long:user long:nice long:sys long:idle"
			final BufferedReader stat = new BufferedReader(new FileReader("/proc/stat"));
			ligne = stat.readLine();
			stat.close();
			ligne = ligne.trim();
			somme = 0;
			userTot = 0;
			niceTot = 0;
			systemTot = 0;
			idleTot = 0;
			for (i = 0; i < 5; i++) {
				try {
					index = ligne.indexOf(' ');
					if (index != -1) {
						jiffies = Long.valueOf(ligne.substring(0, index).trim()).longValue();
					} else {
						jiffies = Long.valueOf(ligne.trim()).longValue();
					}
					somme = somme + jiffies; // somme=user+nice+sys+idle
					if (i == 1) {
						userTot = jiffies;
					}
					if (i == 2) {
						niceTot = jiffies;
					}
					if (i == 3) {
						systemTot = jiffies;
					}
					if (i == 4) {
						idleTot = jiffies;
					}
				} catch (final Exception e) {
				}
				try {
					ligne = ligne.substring(ligne.indexOf(' ')).trim();
				} catch (final Exception e) {
				}
			}

		} catch (final Exception e) {
			System.out.println("erreur: " + e.getMessage());
		}
	}

	public static int ps(final int delai) {
		int idOld, user, system;
		long userTotOld;
		long niceTotOld;
		long systemTotOld;
		long idleTotOld;
		long sommeOld;
		int[] pidOld = new int[1000];
		int[] usOld = new int[1000];
		int[] syOld = new int[1000];

		sommeOld = 0;
		userTotOld = 0;
		niceTotOld = 0;
		systemTotOld = 0;
		idleTotOld = 0;
		idOld = 0;

		stat();

		// mise a jour des valeurs de depart
		idOld = id;
		pidOld = pid;
		usOld = us;
		syOld = sy;
		sommeOld = somme;
		userTotOld = userTot;
		niceTotOld = niceTot;
		systemTotOld = systemTot;
		idleTotOld = idleTot;

		try {
			java.lang.Thread.sleep(delai);
		} catch (final Exception e) {
		}

		stat();

		// calcul du nombre de jiffies dans l'intervalle pour chaque process
		user = 0;
		system = 0;
		for (int i = 0; i < id; i++) {
			for (int j = 0; j < idOld; j++) {
				if (pid[i] == pidOld[j]) {
					user = (user + us[i]) - usOld[j];
					system = (system + sy[i]) - syOld[j];
				}
			}
		}

		return ((int) (((userTot - userTotOld) * 100) / (somme - sommeOld)));

	}
}
