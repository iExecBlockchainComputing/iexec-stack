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

set title "Pipe (UDP) vs XW Client 15"
set xlabel '3 mount/umount successifs' 0,0
set ylabel 'Time (ms)' 0,0
set encoding iso_8859_1
set key outside
#set key left Left reverse 80,235
#set size 1.2,1.2
set key spacing 1.5
set key width 1.5
set logscale y

set terminal postscript	eps
set output 'pipeudpVSxwclient15.ps'

plot 'mounts_pipeudp0.dat'  using 2 title 'Time Pipe' with steps, 'mounts_pipeudp0.dat'  using 3 title 'Delta Pipe' with linespoints, 'mounts_pipeudp0.dat'  using 4 title 'Cumulated Pipe' with linespoints, 'mounts_xw_client15.dat'  using 2 title 'Time XW Client 15' with linespoints, 'mounts_xw_client15.dat'  using 3 title 'Delta XW Client 15' with linespoints, 'mounts_xw_client15.dat'  using 4 title 'Cumulated XW Client 15' with linespoints


set terminal jpeg
set output 'pipeudpVSxwclient15.jpeg'

#plot 'mounts_pipeudp0.dat'  using 2 title 'Time Pipe' with steps, 'mounts_pipeudp0.dat'  using 3 title 'Delta Pipe' with linespoints, 'mounts_xw_client15.dat'  using 2 title 'Time XW Client 15' with linespoints, 'mounts_xw_client15.dat'  using 3 title 'Delta XW Client 15' with linespoints
plot 'mounts_pipeudp0.dat'  using 2 title 'Time Pipe' with steps, 'mounts_pipeudp0.dat'  using 3 title 'Delta Pipe' with linespoints, 'mounts_pipeudp0.dat'  using 4 title 'Cumulated Pipe' with linespoints, 'mounts_xw_client15.dat'  using 2 title 'Time XW Client 15' with linespoints, 'mounts_xw_client15.dat'  using 3 title 'Delta XW Client 15' with linespoints, 'mounts_xw_client15.dat'  using 4 title 'Cumulated XW Client 15' with linespoints
