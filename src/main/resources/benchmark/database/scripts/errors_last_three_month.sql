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
-- File    : errors_last_three_month.sql
-- Purpose : this file contains the needed SQL commands to 
--           retreive error jobs per week from DB for the last 3 months
-- Usage   : mysql < errors_last_three_month.sql
--


select arrivaldate,count(*)
       from (
         (select date_format(arrivaldate,"%Y/%m") as arrivaldate from works 
         where not isnull(arrivaldate)
            and arrivaldate > date_sub(now(), interval 1 quarter)
	       	and status="ERROR")
	     union all
         (select date_format(arrivaldate,"%Y/%m") as arrivaldate from works_history 
         where not isnull(arrivaldate)
            and arrivaldate > date_sub(now(), interval 1 quarter)
	       	and status="ERROR")
 	  ) as t 
 	  group by arrivaldate order by arrivaldate;
