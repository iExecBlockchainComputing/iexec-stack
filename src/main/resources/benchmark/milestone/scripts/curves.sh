#!/bin/sh
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
# This creates a graph to compare
#    - dispatcher on different runs
#    - worker on different runs
#    - client on different runs
#
# Params : -h to get this help
#          -i <inputDir>  (datas)
#          -o <outputDir> (figures)
#          -w <wildcard>


ROOTDIR=`dirname $0`/..
DATADIR=""
FIGDIR=""
main_wildcard=""




############################################################
# Help
############################################################
help()
{
		head -n 12 $0 | tail -11
		exit
}


###################################################
# This creates a gnuplot script
#
# $1 title
# $2 xlabel
# $3 ylabel
# $4 filename
# $5 plot command
############################################################
gnuplotscript ()
{
#		echo "\$1 = $1"
#		echo "\$4 = $4"
#		echo "\$5 = $5"

		filegp="$4.gp"

		cat > $filegp <<EOF
set title "$1"
set xlabel '$2' 0,0
set ylabel '$3' 0,0
set encoding iso_8859_1
set key outside
#set key left Left reverse 80,235
#set size 1.2,1.2
set key spacing 1.5
set key width 1.5
#set logscale y

set terminal postscript eps
set output '$FIGDIR/$4.ps'

$5

set terminal png
set output '$FIGDIR/$4.png'

$5

EOF

}


############################################################
# $1 part name : must contain client, or dispatcher, or worker
# $2 static part name : must be client, or dispatcher, or worker
############################################################
dopart()
{
		thispart=$1
		staticpart=$2
		filesdone=""

		allplots=""
		for i in `ls -r $DATADIR/*$thispart*.dat`; do

				filesdone=$i" "$filesdone

				title0=`basename $i | cut -d '.' -f 1`

				echo $title0 | grep downloads > /dev/null 2>&1
				downloads0=$?

				echo $title0 | grep deltaTime > /dev/null 2>&1
				delta0=$?

				if [ "$allplots" == "" ]; then
						allplots="plot '$i'  using 2 title '$title0' with steps" ;
				else
						allplots="$allplots, '$i'  using 2 title '$title0' with steps" ;
				fi

				for j in `ls -r $DATADIR/*$staticpart*.dat`; do

						found=0
						for k in $filesdone; do
								if [ "$j" == "$k" ]; then
										found=1
								fi
						done

						if [ $found == 1 ]; then
#								echo "deja fait"
								continue
						fi

#						if [ "$i" == "$j" ]; then
#								echo "continuing ($i) ($j)"
#								continue
#						fi


						title1=`basename $j | cut -d '.' -f 1`

						# compare downloads to downloads only
						echo $title1 | grep downloads > /dev/null 2>&1
						downloads1=$?
						if [ $downloads0 == 0 ]; then
								if [ $downloads1 == 1 ]; then
										continue
								fi
						else
								if [ $downloads1 == 0 ]; then
										continue
								fi
						fi

						# compare deltatime to deltatime only
						echo $title1 | grep deltaTime > /dev/null 2>&1
						delta1=$?
						if [ $delta0 == 0 ]; then
								if [ $delta1 == 1 ]; then
										continue
								fi
						else
								if [ $delta1 == 0 ]; then
										continue
								fi
						fi

						vsplots="plot '$i'  using 2 title '$title0' with steps, '$j'  using 2 title '$title1' with steps, '$i'  using 4 title '$title0 cumulated' with steps, '$j'  using 4 title '$title1 cumulated' with steps"

						filename=$title0"VS"$title1
						gpfilename=$filename".gp"
						psfilename=$filename".ps"
						pngfilename=$filename".png"
						if [ ! -f $psfilename -o  ! -f $pngfilename -o $force == 1 ]; then
								echo $filename
								gnuplotscript "$title0 VS $title1" "Calls" "Time (ms)" $filename "$vsplots"
#						echo "gpfilename = $gpfilename"
								gnuplot $gpfilename
								rm -f  $gpfilename
						fi

#						vsplots="plot [0:60] '$i' using 3 title '$title0 delta time' with steps lw 10, '$j'  using 3 title '$title1 delta time' with steps lw 10"
						vsplots="plot [0:100] '$j'  using 3 title '$title1 delta time' with steps lw 10, '$i' using 3 title '$title0 delta time' with steps"

						filename=$title0"VS"$title1"_deltaTime"
						gpfilename=$filename".gp"
						psfilename=$filename".ps"
						pngfilename=$filename".png"
						if [ ! -f $psfilename -o  ! -f $pngfilename ]; then
								echo $filename
								gnuplotscript "$title0 VS $title1 : delta time" "Calls" "Time (ms)" $filename "$vsplots"
#						echo "gpfilename = $gpfilename"
								gnuplot $gpfilename
								rm -f  $gpfilename
						fi
				done

		done

		thisfilename=$thispart"s"
		gnuplotscript "$thisfilename" "Calls" "Time (ms)" $thisfilename "$allplots"
		gnuplot $thisfilename".gp"

}



############################################################
# Main
############################################################


while [ $# -gt 0 ]; do
		case $1 in
				"-i" )
						shift
						DATADIR=$1
						;;
				"-o" )
						shift
						FIGDIR=$1
						;;
				"-w" )
						shift
						main_wildcard=$1
						;;
				"-h" )
						help
						;;
		esac
		shift
done

[ "$DATADIR" = "" ] && help
[ "$FIGDIR"  = "" ] && help


force=0
[ "$main_wildcard" == "" ] && main_wildcard="*"

a_wildcard=$main_wildcard
[ "$a_wildcard" == "*" ] && a_wildcard=client
echo $a_wildcard | grep client > /dev/null 2>&1
TEST=$?
if [ $TEST == 0 ]; then
		echo "a_wildcard=$a_wildcard"
		dopart $a_wildcard client
fi

a_wildcard=$main_wildcard
[ "$a_wildcard" == "*" ] && a_wildcard=dispatcher
echo $a_wildcard | grep dispatcher > /dev/null 2>&1
TEST=$?
if [ $TEST == 0 ]; then
		echo "a_wildcard=$a_wildcard"
		dopart $a_wildcard dispatcher
fi

a_wildcard=$main_wildcard
[ "$a_wildcard" == "*" ] && a_wildcard=worker
echo $a_wildcard | grep worker > /dev/null 2>&1
TEST=$?
if [ $TEST == 0 ]; then
		echo "a_wildcard=$a_wildcard"
		dopart $a_wildcard worker
fi
