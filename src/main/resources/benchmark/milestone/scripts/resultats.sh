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

MARGE=60
BARWIDTH0=30
BARWIDTH3=30

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
# prepareDataFiles
# $1 part (client, dispatcher, worker)
# $2 data file
# $3 step
# $4 width
###########################################
prepareDataFiles()
{
		TIER=$1
		ACTION=$2
		RESULTFILE=$3
		STEP=$4
		WIDTH=$5

		echo "prepareDataFiles $TIER `basename $RESULTFILE` $STEP $WIDTH"

		rm -f $RESULTFILE
		touch $RESULTFILE

		index=0
#		ls -l $DATADIR"/exp"*$TIER"_"$ACTION".dat"

		for file in `ls $DATADIR"/exp"*$TIER"_"$ACTION".dat"`; do
				words=`tail $file | grep -vE "^#" | tail -1 | wc -w`
				data=`tail $file | grep -vE "^#" | tail -1 | cut -f $words`
				[ "$data" == "" ] && data="null"
				ii=`expr $index \* $MARGE`
				ii=$(( $ii + $STEP ))
#				echo "$ii $data $WIDTH"
				echo "$ii $data $WIDTH" >> $RESULTFILE
				index=$(( $index + 1 ))
		done

}


###########################################
# prepareDataFiles
# $1 part (client, dispatcher, worker)
###########################################
prepareGnuplotFiles()
{
		TIER=$1
		ACTIONS="submit exec status result remove"
		for action in $ACTIONS; do 
				DATAFILE="$DATADIR/resultats_"$TIER"_"$action".dat"
				STEP=0
				WIDTH=$BARWIDTH0

				prepareDataFiles $TIER $action $DATAFILE $STEP $WIDTH

				if [ ! -s $DATAFILE ]; then 
						echo "$DATAFILE is empty"
						rm -f $DATAFILE
						continue
				fi

				DATAFILEB="$DATADIR/resultats_"$TIER"_"$action"-unebarre.dat"
				STEP=0
				WIDTH=$BARWIDTH3

				prepareDataFiles $TIER $action $DATAFILEB $STEP $WIDTH

				if [ ! -s $DATAFILEB ]; then 
						echo "$DATAFILEB is empty"
						rm -f $DATAFILE
						rm -f $DATAFILEB
						continue
				fi


				EXPS="exp01 exp02 exp03 exp12 exp13 exp22 exp23 exp32 exp33 exp34 exp35 exp36 exp37 exp38 exp40 exp41"
				#EXPS="exp50 exp51 exp52 exp53 exp54 exp55 exp56 exp57"

				XTICS=""
				index=0
#echo "m=$MARGE"
				for exp in $EXPS; do 
						ii=`expr $index \* $MARGE`
#		echo "ii=$ii"
						XTICS="$XTICS \"$exp\" $ii,"
						index=$(( $index + 1 ))
#		XTICS="$XTICS \"$exp\" $ii,"
				done
				
				XTICS=`echo $XTICS | sed "s/,$//g"`
#echo $XTICS

				XMAX=`expr $MARGE \* $index`
				
				GPFILE=$DATADIR"/resultats_"$TIER"_"$action".gp"
				PSFILE=$FIGDIR"/resultats_"$TIER"_"$action".ps"
				PSFILEB=$FIGDIR"/resultats_"$TIER"_"$action"-unebarre.ps"
				PNGFILE=$FIGDIR"/resultats_"$TIER"_"$action".png"
				PNGFILEB=$FIGDIR"/resultats_"$TIER"_"$action"-unebarre.png"
				rm -f $GPFILE

				cat > $GPFILE <<EOF
set title "Resultats/exp : $TIER $action"
set xlabel "  " 0,0
set ylabel 'Time (ms)' 0,0
set encoding iso_8859_1
set key outside
#set key left Left reverse 80,235
#set size 1.2,1.2
set key spacing 1.5
set key width 1.5
#set logscale y 20

#set yrange [8000:30000]
set xrange [0:$XMAX]

#set ytics (10000,12000,14000,16000,100000)
set xtics rotate 90
set xtics ($XTICS)
#set x2tics ("exp0X" 0, "exp1X" 5, "exp2X" 8, "exp3X" 11, "exp4X" 14, "exp5X" 17, "exp6X" 20, "exp7X" 23, "exp8X" 26)

#set grid noxtics x2tics

set terminal postscript	eps
set output '$PSFILE'

plot '$DATAFILE' using 2 with linespoints pt 9 title '$TIER'

set output '$PSFILEB'

plot '$DATAFILEB' using 2 with linespoints pt 9 title '$TIER'

set terminal png
set output '$PNGFILE'

plot '$DATAFILE' with steps title '$TIER'

set output '$PNGFILEB'

plot '$DATAFILEB' with steps title '$TIER'

EOF

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

TIER="client"
prepareGnuplotFiles $TIER

TIER="dispatcher"
prepareGnuplotFiles $TIER

TIER="worker"
prepareGnuplotFiles $TIER

