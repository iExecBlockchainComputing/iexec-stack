-- 
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



--
-- Version : 7.0.0
--
-- File    : xwupdatedb-7.0.0.sql
-- Purpose : this file contains the needed SQL commands to 
--           update the XWHEP database from previous versions
--




-- users.certificate column contains the X509 proxy itself
ALTER TABLE  users MODIFY COLUMN certificate text;
-- more char to store distinguished name
ALTER TABLE  users MODIFY COLUMN login char(250);

-- default 0
ALTER TABLE users MODIFY COLUMN nbJobs int(15) default 0;
-- pending jobs counter; updated on work submission
ALTER TABLE users ADD COLUMN pendingJobs int(15) default 0;
-- running jobs counter; updated on sucessfull worker request
ALTER TABLE users ADD COLUMN runningJobs int(15) default 0;
-- error jobs counter; updated on job error
ALTER TABLE users ADD COLUMN errorJobs int(15) default 0;
-- average execution time. updated on work completion
ALTER TABLE users ADD COLUMN usedCpuTime bigint default 0;

-- default 0
ALTER TABLE apps MODIFY COLUMN avgExecTime int(15) default 0;
ALTER TABLE apps MODIFY COLUMN nbJobs int(15) default 0;
-- linux intel itanium library
ALTER TABLE apps ADD COLUMN ldlinux_ia64URI char(200);
-- linux intel itanium binary
ALTER TABLE apps ADD COLUMN linux_ia64URI char(200);
-- pending jobs counter; updated on work submission
ALTER TABLE apps ADD COLUMN pendingJobs int(15) default 0;
-- running jobs counter; updated on sucessfull worker request
ALTER TABLE apps ADD COLUMN runningJobs int(15) default 0;
-- error jobs counter; updated on job error
ALTER TABLE apps ADD COLUMN errorJobs int(15) default 0;
-- application web page
ALTER TABLE apps ADD COLUMN webpage char(200);

-- default 0
ALTER TABLE hosts MODIFY COLUMN avgExecTime int(15) default 0;
ALTER TABLE hosts MODIFY COLUMN nbJobs int(15) default 0;
-- this is the amount of simultaneous jobs
ALTER TABLE hosts ADD COLUMN poolworksize int(2) default 0;
-- pending jobs counter; updated on work submission
ALTER TABLE hosts ADD COLUMN pendingJobs int(15) default 0;
-- running jobs counter; updated on sucessfull worker request
ALTER TABLE hosts ADD COLUMN runningJobs int(15) default 0;
-- error jobs counter; updated on job error
ALTER TABLE hosts ADD COLUMN errorJobs int(15) default 0;
-- cpu speed
ALTER TABLE hosts MODIFY COLUMN cpuspeed bigint default 0;
-- total swap
ALTER TABLE hosts MODIFY COLUMN totalswap bigint default 0;
-- total memory
ALTER TABLE hosts MODIFY COLUMN totalmem bigint default 0;
-- total space on tmp partition
ALTER TABLE hosts ADD COLUMN totaltmp bigint default 0;
-- free space on tmp partition
ALTER TABLE hosts ADD COLUMN freetmp bigint default 0;
-- this is the Service Grid Identifier; this is set by the DG 2 SG bridge, if any
ALTER TABLE hosts ADD COLUMN sgid char(200);

-- application web page
ALTER TABLE usergroups ADD COLUMN webpage char(200);

-- this is the Service Grid Identifier; this is set by the DG 2 SG bridge, if any
ALTER TABLE tasks MODIFY COLUMN duration bigint default 0;

-- we keep track of installations
alter table version add column installation datetime;


-- 
-- Since XWHEP 7.0.0 :
--  * table works_history contains deleted rows from works table
--  * table tasks_history contains deleted rows from tasks table
--
CREATE TABLE IF NOT EXISTS apps_history       LIKE apps;
CREATE TABLE IF NOT EXISTS datas_history      LIKE datas;
CREATE TABLE IF NOT EXISTS groups_history     LIKE groups;
CREATE TABLE IF NOT EXISTS hosts_history      LIKE hosts;
CREATE TABLE IF NOT EXISTS sessions_history   LIKE sessions;
CREATE TABLE IF NOT EXISTS tasks_history      LIKE tasks;
CREATE TABLE IF NOT EXISTS traces_history     LIKE traces;
CREATE TABLE IF NOT EXISTS usergroups_history LIKE usergroups;
CREATE TABLE IF NOT EXISTS users_history      LIKE users;
CREATE TABLE IF NOT EXISTS works_history      LIKE works;

--
-- if history tables already exists, let ensure they reflect production ones
--

-- users_history.certificate column contains the X509 proxy itself
ALTER TABLE  users_history MODIFY COLUMN certificate text;
-- more char to store distinguished name
ALTER TABLE  users_history MODIFY COLUMN login char(250);

-- default 0
ALTER TABLE users_history MODIFY COLUMN nbJobs int(15) default 0;
-- pending jobs counter; updated on work submission
ALTER TABLE users_history ADD COLUMN pendingJobs int(15) default 0;
-- running jobs counter; updated on sucessfull worker request
ALTER TABLE users_history ADD COLUMN runningJobs int(15) default 0;
-- error jobs counter; updated on job error
ALTER TABLE users_history ADD COLUMN errorJobs int(15) default 0;
-- average execution time. updated on work completion
ALTER TABLE users_history ADD COLUMN usedCpuTime bigint default 0;

-- default 0
ALTER TABLE apps_history MODIFY COLUMN avgExecTime int(15) default 0;
ALTER TABLE apps_history MODIFY COLUMN nbJobs int(15) default 0;
-- linux intel itanium library
ALTER TABLE apps_history ADD COLUMN ldlinux_ia64URI char(200);
-- linux intel itanium binary
ALTER TABLE apps_history ADD COLUMN linux_ia64URI char(200);
-- pending jobs counter; updated on work submission
ALTER TABLE apps_history ADD COLUMN pendingJobs int(15) default 0;
-- running jobs counter; updated on sucessfull worker request
ALTER TABLE apps_history ADD COLUMN runningJobs int(15) default 0;
-- error jobs counter; updated on job error
ALTER TABLE apps_history ADD COLUMN errorJobs int(15) default 0;
-- application web page
ALTER TABLE apps_history ADD COLUMN webpage char(200);

-- default 0
ALTER TABLE hosts_history MODIFY COLUMN avgExecTime int(15) default 0;
ALTER TABLE hosts_history MODIFY COLUMN nbJobs int(15) default 0;
-- this is the amount of simultaneous jobs
ALTER TABLE hosts_history ADD COLUMN poolworksize int(2) default 0;
-- pending jobs counter; updated on work submission
ALTER TABLE hosts_history ADD COLUMN pendingJobs int(15) default 0;
-- running jobs counter; updated on sucessfull worker request
ALTER TABLE hosts_history ADD COLUMN runningJobs int(15) default 0;
-- error jobs counter; updated on job error
ALTER TABLE hosts_history ADD COLUMN errorJobs int(15) default 0;
-- cpu speed
ALTER TABLE hosts_history MODIFY COLUMN cpuspeed bigint default 0;
-- total swap
ALTER TABLE hosts_history MODIFY COLUMN totalswap bigint default 0;
-- total memory
ALTER TABLE hosts_history MODIFY COLUMN totalmem bigint default 0;
-- total space on tmp partition
ALTER TABLE hosts_history ADD COLUMN totaltmp bigint default 0;
-- free space on tmp partition
ALTER TABLE hosts_history ADD COLUMN freetmp bigint default 0;
-- this is the Service Grid Identifier; this is set by the DG 2 SG bridge, if any
ALTER TABLE hosts_history ADD COLUMN sgid char(200);

-- application web page
ALTER TABLE usergroups_history ADD COLUMN webpage char(200);

-- this is the Service Grid Identifier; this is set by the DG 2 SG bridge, if any
ALTER TABLE tasks_history MODIFY COLUMN duration bigint default 0;



--
-- End Of File
--
