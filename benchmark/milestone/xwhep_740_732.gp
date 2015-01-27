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

set title "XWHEP 7.3.2 Vs 7.4.0\nClient timestamps, submittiing 10K jobs (2323 workers)\nSequential flow : time = fct(event number)"
set xlabel "Event number"
set ylabel 'Time (ms)'
set encoding iso_8859_1
set key outside
#set key left Left reverse 80,235
#set size 1.2,1.2
set key spacing 1.5
set key width 1.5
#set logscale y
#set yrange [0:300]
set xtics rotate

set terminal png
set output 'xwhep_740_732.png'

plot 'datas/submit_10kJ_343W_xwhep732.dat'  using 4 title '7.3.2' with linespoints, 'datas/submit_10kJ_2323W_xwhep740_insertdelayed_commoptimized_tblindexes_hpools_dbcache2.dat'  using 4 title '7.4.0' with linespoints

