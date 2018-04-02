--  Copyrights     : CNRS
--  Author         : Oleg Lodygensky
--  Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
--  Web            : http://www.xtremweb-hep.org
-- 
--       This file is part of XtremWeb-HEP.
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--     http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- 

--
-- Since : 5.8.0
--
-- File    : whathappened.sql
-- Purpose : this file contains the needed SQL commands to 
--           retreive pilot jobs from DB
-- Usage   : mysql < whathappened.sql
--


-- select "create a temporary table for completed jobs"

create temporary table completedjobs (
       thedate datetime,
       completeds int(10) default 0
);

-- select "fill temporary table completedjobs";

insert into completedjobs (thedate,completeds)
       select date(completeddate),count(*) 
         from works 
	 where not isnull(completeddate)
	   and date(completeddate)>"2009-07-14" 
	   and status="COMPLETED" 
	 group by date(completeddate);

--  since 5.9.0 deleted rows go to works_history

insert into completedjobs (thedate,completeds)
       select date(completeddate),count(*) 
         from works_history 
	 where not isnull(completeddate)
	   and date(completeddate)>"2009-07-14" 
	   and status="COMPLETED" 
	 group by date(completeddate);

-- select "create a temporary table for error jobs"

create temporary table errorjobs (
       thedate datetime,
       errors int(10) default 0
);

-- select "fill temporary table errorjobs";

insert into errorjobs (thedate,errors)
       select date(arrivaldate),count(*) 
         from works 
	 where not isnull(arrivaldate)
	   and date(arrivaldate)>"2009-07-14" 
	   and status="ERROR" 
	 group by date(arrivaldate);

-- since 5.9.0 deleted rows go to works_history

insert into errorjobs (thedate,errors)
       select date(arrivaldate),count(*) 
         from works_history 
	 where not isnull(arrivaldate)
	   and date(arrivaldate)>"2009-07-14" 
	   and status="ERROR" 
	 group by date(arrivaldate);

-- select "create temporary table for proxy jobs (jobs inserted with a valid X509 proxy)";

create temporary table proxyjobs (
       thedate datetime,
       proxieds int(10) default 0
);

-- select "fill temporary table pilotedjobs";

insert into proxyjobs (thedate,proxieds)
       select date(arrivaldate),count(*) 
         from works 
	 where not isnull(arrivaldate)
	   and date(arrivaldate)>"2009-07-14" 
	   and not isnull(userproxy)
	 group by date(arrivaldate);

-- since 5.9.0 deleted rows go to works_history

insert into proxyjobs (thedate,proxieds)
       select date(arrivaldate),count(*) 
         from works_history 
	 where not isnull(arrivaldate)
	   and date(arrivaldate)>"2009-07-14" 
	   and not isnull(userproxy)
	 group by date(arrivaldate);

-- select "create temporary table for piloted jobs (jobs run on EGEE ressources)";

create temporary table pilotedjobs (
       thedate datetime,
       piloteds int(10) default 0
);

-- select "fill temporary table pilotedjobs";

insert into pilotedjobs (thedate,piloteds)
       select date(completeddate),count(*) 
       from works , tasks, hosts 
       where tasks.workuid=works.uid 
       	 and works.status="COMPLETED" 
	 and not isnull(works.completeddate)
	 and date(works.completeddate)>"2009-07-14" 
	 and tasks.hostuid=hosts.uid 
	 and hosts.pilotjob="true" 
       group by date(completeddate);


-- select "create temporary table for pilot jobs (EGEE ressources)";

create temporary table pilotjobs (
       thedate datetime,
       pilotjobs int(10) default 0
);

-- select "fill temporary table pilotedjobs";

insert into pilotjobs (thedate,pilotjobs)
       select date(lastalive),count(*) 
       from hosts 
       where not isnull(lastalive)
       and   pilotjob="true"
       and   date(lastalive)>"2009-07-14" 
       group by date(lastalive);

-- select date(thedate),piloteds from pilotedjobs group by date(thedate) order by date(thedate);


-- select "create temporary table stats";

create temporary table stats (
       thedate datetime,
       completeds int(10) default 0,
       errors int(10) default 0,
       proxieds int(10) default 0,
       piloteds int(10) default 0,
       pilotjobs int(10) default 0
);

-- select "inserta all possible dates"

insert into stats (thedate,completeds)
       select date(thedate),completeds
       from completedjobs;
insert into stats (thedate,errors)
       select date(completedjobs.thedate),errors
       from completedjobs
	      join errorjobs
	      on date(errorjobs.thedate)=date(completedjobs.thedate);
insert into stats (thedate,proxieds)
       select date(completedjobs.thedate),proxieds
       from completedjobs
	      join proxyjobs
	      on date(proxyjobs.thedate)=date(completedjobs.thedate);
insert into stats (thedate,piloteds)
       select date(completedjobs.thedate),piloteds
       from completedjobs
	      join pilotedjobs
	      on date(pilotedjobs.thedate)=date(completedjobs.thedate);
insert into stats (thedate,pilotjobs)
       select date(completedjobs.thedate),pilotjobs
       from completedjobs
	      join pilotjobs
	      on date(pilotjobs.thedate)=date(completedjobs.thedate);


select date(thedate),
       max(completeds) as completed,
       max(errors) as errors,
       max(proxieds) as proxieds,
       max(piloteds) as piloteds,
       max(pilotjobs) as pilotjobs
       from stats 
       group by thedate
       order by thedate;
