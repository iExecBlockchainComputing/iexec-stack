--  Copyrights     : CNRS
--  Author         : Oleg Lodygensky
--  Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
--  Web            : http://www.xtremweb-hep.org
-- 
--       This file is part of XtremWeb-HEP.
-- 
--     XtremWeb-HEP is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
-- 
--     XtremWeb-HEP is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU General Public License for more details.
-- 
--     You should have received a copy of the GNU General Public License
--     along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
-- 
--
--
-- Since : 7.4.0
--
-- File    : timings.sql
-- Purpose : this file contains the needed SQL commands to 
--           retreive timings (pending delay, running delay etc.)
-- Usage   : mysql < timings.sql
--

select completeddate,
       laststartdate,
       startdate,
       insertiondate,
       arrivaldate,
       run,
       restart,
       pending,
       waiting
       from (
         (select date_format(completeddate,"%Y/%m/%d %H:%i") as completeddate,
		       laststartdate,
		       startdate,
		       insertiondate,
		       arrivaldate,
		       unix_timestamp(completeddate)-unix_timestamp(laststartdate) as run,
		       unix_timestamp(laststartdate)-unix_timestamp(startdate) as restart,
		       unix_timestamp(startdate)-unix_timestamp(insertiondate) as pending,
		       unix_timestamp(insertiondate)-unix_timestamp(arrivaldate) as waiting
            from works, tasks
            where works.uid=tasks.workuid
	       	  and works.status="COMPLETED" 
 	 		  and not isnull(completeddate))
	     union all
         (select date_format(completeddate,"%Y/%m/%d %H:%i") as completeddate,
		       laststartdate,
		       startdate,
		       insertiondate,
		       arrivaldate,
		       unix_timestamp(completeddate)-unix_timestamp(laststartdate) as run,
		       unix_timestamp(laststartdate)-unix_timestamp(startdate) as restart,
		       unix_timestamp(startdate)-unix_timestamp(insertiondate) as pending,
		       unix_timestamp(insertiondate)-unix_timestamp(arrivaldate) as waiting
            from works_history, tasks_history
            where works_history.uid=tasks_history.workuid
	       	  and works_history.status="COMPLETED" 
 	 		  and not isnull(completeddate))
 	  ) as t 
 	  group by completeddate order by completeddate;
