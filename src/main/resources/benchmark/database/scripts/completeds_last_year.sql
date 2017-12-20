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
-- Since : 5.8.0
--
-- File    : completeds_last_year.sql
-- Purpose : this file contains the needed SQL commands to 
--           retreive completed jobs per month  from DB for the last year
-- Usage   : mysql < completeds_last_year.sql
--


select completeddate,count(*)
       from (
         (select date_format(completeddate,"%Y/%m") as completeddate from works 
         where not isnull(completeddate)
            and completeddate > date_sub(now(), interval 1 year) 
	       	and status="COMPLETED")
	     union all
         (select date_format(completeddate,"%Y/%m") as completeddate from works_history 
         where not isnull(completeddate)
            and completeddate > date_sub(now(), interval 1 year) 
	       	and status="COMPLETED")
 	  ) as t 
 	  group by completeddate order by completeddate;
