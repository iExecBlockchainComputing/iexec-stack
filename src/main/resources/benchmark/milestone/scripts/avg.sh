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
# This calculates averages of events
#   $1 : file wildcard to get events from (optionnal)
#        examples : (*) worker
#                   (*) worker18
#   $2 : events to use (optionnal)
#        examples : (*) downloaded    : a single event
#                   (*) "got result"  : two events
#                   (*) got@result    : a single event ('@' replaces space)
#
#
#		avg of column :
#		  (*) 4 : time
#		  (*) 5 : delta time
#		  (*) 6 : total time

file=$1
event=$2
column=5
main_wildcard=""

#if [ "$file" == "" ]; then
#		echo "avg from which file ?"
#		exit
#fi

#if [ "$event" == "" ]; then
#		echo "avg of what event ?"
#		exit
#fi


#
# This calculates the average for a given file
# $1 is the file
# $2 is the event to search in file; spaces have been replaced by '@', we must reverse this
#
dopart()
{
	dp_file=$1
	dp_event=`echo $2 | sed "s/@/ /g"`
	dp_column=5
	dp_temp=`echo $event | sed "s/[[:space:]][[:space:]]*/_/g" `_in_`basename $file`
#	echo "dopart  event = $dp_event"
	rm -f $dp_temp
#	echo "grep $dp_event $dp_file"

	grep "$dp_event" $dp_file | grep -v TITLE | grep -v "#" | cut -f $dp_column > $dp_temp
	dp_total=0
	dp_items=0
	for dp_i in `cat $dp_temp`; do 
		dp_total=$((dp_i + dp_total))
		dp_items=$((dp_items + 1))
#		echo "dp_i = $dp_i total=$dp_total items = $dp_items"
	done

	if [ $dp_items -gt 0 ]; then
		titre=`grep TITLE $dp_file | cut -d ":" -f 2`
		dp_avg=`echo $dp_total/$dp_items | bc`
		echo "AVG($dp_file - \"$dp_event\" - $titre ) : $dp_avg"
	fi

	rm -f $dp_temp
}


#
# This calculates the average of a event from all files
# $1 is the file wildcard
# $2 is the event to search from files; spaces have been replaced by '@'
#
avgEvent()
{
	ae_nbfiles=0;
	ae_avg=0
	ae_totalfiles=0
	ae_event=$2
	ae_filewildcard=$1

	for ae_file in `ls $ae_filewildcard*.csv`; do

#		echo "avgEvent $ae_event"
#		dopart $ae_file $ae_event
#		continue

		ae_r=`dopart $ae_file $ae_event`
		if [ "$ae_r" == "" ]; then
			continue
		fi

		echo $ae_r
		ae_avg=`echo $ae_r | cut -d ':' -f 2`
		ae_nbfiles=$((ae_nbfiles + 1))
		ae_totalfiles=$((ae_totalfiles + ae_avg))
	done

	if [ $ae_nbfiles -gt 1 ]; then
		ae_avgfiles=`echo $ae_totalfiles/$ae_nbfiles | bc`
		ae_event=`echo $ae_event | sed "s/@/ /g"`
		echo "----------------"
		echo "AVG($ae_filewildcard/\"$ae_event\") : $ae_avgfiles"
		echo 
	fi
}



# ----------------- main -----------------




while [ $# -gt 0 ]; do
		case $1 in
				"-e" )
						shift
						main_events=$1
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

#
# events list is space separated; hence we replace spaces by '@' on events themselves
#

#events=`cat mounts_xw_dispatcher*.csv | sed "s/ /@/g" | grep -v '#' | grep -v DeltaTime | cut -f 2 | sort -u | sed "s/$/\"/g" | sed "s/^/\"/"`


[ "$main_wildcard" == "" ] && main_wildcard="*"

a_wildcard=$main_wildcard
[ "$a_wildcard" == "*" ] && a_wildcard=client
echo $a_wildcard | grep client > /dev/null 2>&1
TEST=$?
if [ $TEST == 0 ]; then
	[ "$main_events" == "" ] && main_events=`cat $main_wildcard*.csv | sed "s/ /@/g" | grep -v '#' | grep -v DeltaTime | cut -f 2 | sort -u`
	echo $main_events
	for event in $main_events; do
		avgEvent $main_wildcard $event
	done
fi

echo $main_wildcard | grep dispatcher > /dev/null 2>&1
if [ "$main_wildcard" == "" -o $? == 0 ]; then
	[ "$main_events" == "" ] &&	main_events=`cat $main_wildcard*.csv | sed "s/ /@/g" | grep -v '#' | grep -v DeltaTime | cut -f 2 | sort -u`
	for event in $main_events; do
		avgEvent $main_wildcard $event
	done
fi

echo $main_wildcard | grep worker > /dev/null 2>&1
if [ "$main_wildcard" == "" -o $? == 0 ]; then
	[ "$main_events" == "" ] &&	main_events=`cat $main_wildcard*.csv | sed "s/ /@/g" | grep -v '#' | grep -v DeltaTime | cut -f 2 | sort -u`
	for event in $main_events; do
		avgEvent $main_wildcard $event
	done
fi
