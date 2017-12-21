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
-- File    : proxieds.sql
-- Purpose : this file contains the needed SQL commands to 
--           retreive jobs submitted with an X509 proxy certificate
-- Usage   : mysql < whathappened.sql
--


select arrivaldate,count(*)
       from (
         (select date_format(arrivaldate,"%Y/%m/%d %H:%i") as arrivaldate from works 
         where not isnull(arrivaldate)
	  		and not isnull(userproxy))
	     union all
         (select date_format(arrivaldate,"%Y/%m/%d %H:%i") as arrivaldate from works_history
         where not isnull(arrivaldate)
	  		and not isnull(userproxy))
 	  ) as t 
 	  group by arrivaldate order by arrivaldate;
