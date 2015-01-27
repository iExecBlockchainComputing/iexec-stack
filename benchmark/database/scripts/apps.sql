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

select date(completeddate),
       apps.name,
       count(*) as completed,
       format(avg(UNIX_TIMESTAMP(completeddate)-UNIX_TIMESTAMP(laststartdate)),0) as avgexec,
       count(works.userproxy) as proxieds
       from apps,works,tasks,hosts
       where works.appuid=apps.uid
         and works.uid=tasks.workuid
         and tasks.hostuid=hosts.uid
 	 and date(completeddate)>"2009-07-14" 
 	 and not isnull(completeddate)
 	 and works.status="COMPLETED"
       group by apps.name, date(completeddate)
       order by apps.name, date(completeddate);

select date(arrivaldate),
       apps.name,
       count(*) as errors
       from apps,works
       where works.appuid=apps.uid
 	 and date(arrivaldate)>"2009-07-14" 
 	 and not isnull(arrivaldate)
 	 and works.status="ERROR"
       group by apps.name, date(arrivaldate)
       order by apps.name, date(arrivaldate);

select date(completeddate),
       apps.name,
       count(*) as piloteds
       from apps,works,tasks,hosts
       where works.appuid=apps.uid
         and works.uid=tasks.workuid
         and tasks.hostuid=hosts.uid
	 and date(completeddate)>"2009-07-14" 
	 and not isnull(completeddate)
	 and works.status="COMPLETED"
	 and hosts.pilotjob="true"
       group by apps.name, date(completeddate)
       order by apps.name, date(completeddate);
