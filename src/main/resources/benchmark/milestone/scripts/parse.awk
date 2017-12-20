#
# Copyrights     : CNRS
# Author         : Oleg Lodygensky
# Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
# Web            : http://www.xtremweb-hep.org
#
#      This file is part of XtremWeb-HEP.
#
#    XtremWeb-HEP is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    XtremWeb-HEP is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
#
#
# This parses bench files to get gnuplot files
#
BEGIN {
  FS = ";";
	heure = 0;
	prevHeure = 0;
	delta = 0;

	if (of == "") {
		print "Output format must be provided by \"-v of=<output format>\"";
		print "Output format may be \"gnuplot\"";
		print "                     \"csv\"";
		exit;
	}
	outputFormat = of;
	totalHeures = 0;
	firstTime = -1;
	currentTime = 0;
}


#
# blank line
#
/^$/ {
	prevHeure = 0;
	heure = 0;
	delta = 0;
	currentTime = 0;
}


{
  if (NF == 0) {
#    print "#"$0;
		next;
	}
  if (NF == 1) {
#    print "#"$1;
	}
	else {

		if (heure == 0) {
			if (outputFormat == "csv")
				print "Parts,Comments,Time,CurrentTime,DeltaTime,Total";
			else if (outputFormat == "gnuplot")
				print "Time\tTime\tDeltaTime\tTotal";

			if (firstTime == -1)
				firstTime = $3;

			heure = $3;
		}

		currentTime = $3 - heure;
		delta = currentTime - prevHeure;
		prevHeure = currentTime;
		totalHeure += delta;

		if (outputFormat == "csv") {
			print $1","$2","$3","currentTime","delta","totalHeure;
		}
		else if (outputFormat == "gnuplot") {
			print $3"\t"currentTime"\t"delta"\t"totalHeure;
		}
		else {
			print "#Unknown format ("outputFormat") : please provide one with \"-v of=<output format>\"";
			exit;
		}
	}
}
