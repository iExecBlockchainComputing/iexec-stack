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

set title "Sequential flow : time = fct(event number)"
set xlabel "Events"
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

set terminal postscript	eps
set output 'FILEOUTps'

plot 'FILEINdat'  using 2 title 'Time' with steps, 'FILEINdat'  using 3 title 'Delta' with linespoints, 'FILEINdat'  using 4 title 'Cumulated' with linespoints


set terminal png
set output 'FILEOUTpng'

plot 'FILEINdat'  using 2 title 'Time' with steps, 'FILEINdat'  using 3 title 'Delta' with linespoints, 'FILEINdat'  using 4 title 'Cumulated' with linespoints
