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
SET GLOBAL sql_mode = '';
create temporary table appsjobs (
       thedate    datetime,
       appname    varchar(254),
       avgexec    int(10) default 0,
       completeds int(10) default 0,
       proxieds   int(10) default 0,
       errors     int(10) default 0,
       piloteds   int(10) default 0
);


insert into appsjobs (thedate,appname,completeds,avgexec,proxieds)
  select date(completeddate),
       apps.name,
       count(*) as completed,
       avg(UNIX_TIMESTAMP(completeddate)-UNIX_TIMESTAMP(laststartdate)) as avgexec,
       count(works.userproxy) as proxieds
       from apps,works,tasks,hosts
       where works.appuid=apps.uid
         and works.uid=tasks.workuid
         and tasks.hostuid=hosts.uid
 	 and not isnull(completeddate)
 	 and works.status="COMPLETED"
       group by apps.name, date(completeddate);

insert into appsjobs (thedate,appname,errors)
  select date(arrivaldate),
       apps.name,
       count(*) as errors
       from apps,works
       where works.appuid=apps.uid
 	 and not isnull(arrivaldate)
 	 and works.status="ERROR"
       group by apps.name, date(arrivaldate)
       order by apps.name, date(arrivaldate);

insert into appsjobs (thedate,appname,piloteds)
  select date(completeddate),
       apps.name,
       count(*) as piloteds
       from apps,works,tasks,hosts
       where works.appuid=apps.uid
         and works.uid=tasks.workuid
         and tasks.hostuid=hosts.uid
	 and not isnull(completeddate)
	 and works.status="COMPLETED"
	 and hosts.pilotjob="true"
       group by apps.name, date(completeddate);


select * from appsjobs
       group by appname, date(completeds)
       order by appname, date(completeds);
