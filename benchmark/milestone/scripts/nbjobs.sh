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
# This extracts resultats
# Params : -h to get this help
#          -i <inputDir>  (datas)
#          -o <outputDir> (figures)
#
#




ROOTDIR=`dirname $0`/..
DATADIR=""
FIGDIR=""
RESULTFILE=""

MARGE=40
BARWIDTH0=15
BARWIDTH3=15

file=$1
event=$2
column=5


############################################################
# Help
############################################################
help()
{
		head -n 7 $0 | tail -6
		exit
}



###########################################
# cvsBoxes
# $1 part (client, dispatcher, worker)
# $2 data file
# $3 step
# $4 width
###########################################
cvsBoxes()
{
		WHAT=$1
		RESULTFILE=$2
		STEP=$3
		WIDTH=$4

		rm -f $RESULTFILE
		touch $RESULTFILE

		index=0
		for file in `ls $DATADIR/exp*$WHAT.out`; do
				data=`grep "got new work" $file | grep -v files | wc -l`
				ii=`expr $index \* $MARGE`
				ii=$(( $ii + $STEP ))
				echo "$ii $data $WIDTH #`basename $file`"
				echo "$ii $data $WIDTH" >> $RESULTFILE
				index=$(( $index + 1 ))
		done
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
				"-h" )
						help
						;;
		esac
		shift
done

[ "$DATADIR" = "" ] && help
[ "$FIGDIR"  = "" ] && help


###########################################
# CSV file 
###########################################

#RESULTFILE=$DATADIR/resultats.csv

#rm -f $RESULTFILE
#touch $RESULTFILE

#index=0
#echo "Exp;Client;Dispatcher;Worker " >> $RESULTFILE
#for file in `ls $DATADIR/*.csv`; do
#		words=`tail $file | grep -vE "^#" | tail -1 | wc -w`
#		words=$(( $words - 1 ))
#		data=`tail $file | grep -vE "^#" | tail -1 | cut -f $words`
#		[ "$data" == "" ] && data="null"
#		[ $index -eq 0 ] && echo -n `basename $file | cut -b 0-5`";" >> $RESULTFILE
#		echo -n $data";" >> $RESULTFILE
#		index=$(( $index + 1 ))
#		if [ $index -eq 3 ]; then
#				echo "" >> $RESULTFILE
#				index=0
#		fi
#done


###########################################
# gnuplot
###########################################

WHAT="worker"
WORKERFILE=$DATADIR/nbjobs.dat
STEP=0
WIDTH=$BARWIDTH0

cvsBoxes $WHAT $WORKERFILE $STEP $WIDTH

EXPS="exp00 exp01 exp02 exp03 exp04 exp12 exp13 exp14 exp22 exp23 exp24 exp32 exp33 exp34 exp42 exp43 exp44 exp52 exp53 exp54 exp62 exp63 exp64 exp72 exp73 exp74 exp82 exp83 exp84"
XTICS=""
index=0
#echo "m=$MARGE"
for exp in $EXPS; do 
		ii=`expr $index \* $MARGE`
#		echo "ii=$ii"
		index=$(( $index + 1 ))
		XTICS="$XTICS \"$exp\" $ii,"
done

XTICS=`echo $XTICS | sed "s/,$//g"`
#echo $XTICS

GPFILE=$DATADIR/nbjobs.gp
rm -f $GPFILE

cat > $GPFILE <<EOF
set title "SunRPC calls"
set xlabel "  " 0,0
set ylabel 'Calls' 0,0
set encoding iso_8859_1
#set key left Left reverse 80,235
#set size 1.2,1.2
set key spacing 1.5
set key width 1.5

set xtics rotate 90
set xtics ($XTICS)

set terminal postscript	eps
set output '$FIGDIR/nbjobs.ps'

plot '$WORKERFILE' with boxes title ''

set terminal png
set output '$FIGDIR/nbjobs.png

plot '$WORKERFILE' with boxes title ''

EOF
