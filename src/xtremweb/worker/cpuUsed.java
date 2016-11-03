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

// NO COMMENT
// Should replace PS.java

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

public class cpuUsed {

	public static void main(final String[] args) {
		Runtime machine;
		machine = java.lang.Runtime.getRuntime();
		final String name = "a.out";
		String pname;
		String ligne;
		Process ls;
		BufferedReader input;
		int index, i;
		int pid;
		long user;
		long jiffies;
		long us = 0, usOld = 0;
		long somme = 0, sommeOld = 0;

		while (true) {
			try {
				// on recupere la liste des process
				ls = machine.exec("ls -A1 /proc");
				ls.waitFor();
				input = new BufferedReader(new InputStreamReader(ls.getInputStream()));

				while (input.ready()) {
					ligne = input.readLine();
					try {
						// on ne garde que ce qui est numerique (i.e. pid)
						pid = Integer.valueOf(ligne).intValue();
						final BufferedReader pidstat = new BufferedReader(new FileReader("/proc/" + pid + "/stat"));
						ligne = pidstat.readLine();
						pidstat.close();

						pname = ligne.substring(ligne.indexOf('(') + 1, ligne.indexOf(')'));
						if (pname.equals(name)) {
							index = 0;
							System.out.println(pname);
							for (i = 0; i < 13; i++) {
								index = ligne.indexOf(' ', index + 1);
							}
							// on recupere les jiffies user et system du process

							us = Integer.valueOf(ligne.substring(index, ligne.indexOf('+', index + 1)).trim())
									.intValue();
						}
					} catch (final Exception e) {
					} // ce n'est pas numerique
				}

				// lecture de la premiere ligne de /proc/stat
				// "cpu long:user+long:nice long:sys long:idle"
				final BufferedReader stat = new BufferedReader(new FileReader("/proc/stat"));
				ligne = stat.readLine();
				stat.close();
				ligne = ligne.trim();
				somme = 0;
				for (i = 0; i < 5; i++) {
					try {
						index = ligne.indexOf(' ');
						if (index != -1) {

							jiffies = Long.valueOf(ligne.substring(0, index).trim()).longValue();
						} else {
							jiffies = Long.valueOf(ligne.trim()).longValue();
						}
						somme = somme + jiffies; // somme=user+nice+sys+idle
					} catch (final Exception e) {
					}
					try {
						ligne = ligne.substring(ligne.indexOf(' ')).trim();
					} catch (final Exception e) {
					}
				}

				/*
				 * calcul du nombre de jiffies dans l'intervalle pour chaque
				 * process
				 */
				user = us - usOld;

				System.out.println("%CPU user:" + ((user * 100) / (somme - sommeOld)));

				// intervalle en ms.
				java.lang.Thread.sleep(1000);

				// mise a jour des valeurs de depart
				usOld = us;
				sommeOld = somme;

			} catch (final Exception e) {
				System.out.println("erreur: " + e.getMessage());
			}
		}
	}
}
