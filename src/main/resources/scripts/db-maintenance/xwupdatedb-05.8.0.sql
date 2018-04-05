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
-- Version : 5.8.0
--
-- File    : xwupdatedb-5.8.0.sql
-- Purpose : this file contains the needed SQL commands to 
--           update the XWHEP database from previous versions
--


--
-- In XWHEP 5.8.0, table accesses are uniformed and follow the same security access implementation 
--

--
-- Since XWHEP 5.8.0, all tables have an "owneruid" field
-- (datas, apps and hosts tables already had this field)
-- 
ALTER TABLE groups   CHANGE COLUMN clientUID ownerUID char(50);
ALTER TABLE sessions CHANGE COLUMN clientUID ownerUID char(50);
ALTER TABLE works    CHANGE COLUMN userUID   ownerUID char(50);

ALTER TABLE apps       ADD  COLUMN macos_x86_64URI    char(200);
ALTER TABLE apps       ADD  COLUMN ldmacos_x86_64URI  char(200);

ALTER TABLE tasks      ADD  COLUMN owneruid     char(50);
ALTER TABLE traces     ADD  COLUMN owneruid     char(50);
ALTER TABLE users      ADD  COLUMN owneruid     char(50);
ALTER TABLE usergroups ADD  COLUMN owneruid     char(50);

--
-- Since XWHEP 5.8.0, all tables have an accessrights field
-- (datas, apps and works tables already had this field)
-- 
ALTER TABLE groups     ADD    COLUMN accessRights int(4) DEFAULT 755;
ALTER TABLE hosts      ADD    COLUMN accessRights int(4) DEFAULT 755;
ALTER TABLE sessions   ADD    COLUMN accessRights int(4) DEFAULT 755;
ALTER TABLE tasks      ADD    COLUMN accessRights int(4) DEFAULT 755;
ALTER TABLE traces     ADD    COLUMN accessRights int(4) DEFAULT 755;
ALTER TABLE users      ADD    COLUMN accessRights int(4) DEFAULT 755;
ALTER TABLE usergroups ADD    COLUMN accessRights int(4) DEFAULT 755;


--
--
--
ALTER TABLE apps  MODIFY COLUMN minMemory   int(10) default 0;
ALTER TABLE apps  MODIFY COLUMN minCPUSpeed int(10) default 0;
ALTER TABLE hosts MODIFY COLUMN cpuspeed    int(10) default 0;
ALTER TABLE hosts MODIFY COLUMN totalmem    int(10) default 0;
ALTER TABLE hosts MODIFY COLUMN totalswap   int(10) default 0;
--
-- upgrade tables to conform to 5.8.0 new structure
--
SET @adminuid=(SELECT uid FROM users WHERE rights="SUPER_USER" AND isdeleted="false" LIMIT 1);

UPDATE traces     SET owneruid=@adminuid;
UPDATE usergroups SET owneruid=(SELECT uid FROM users WHERE usergroupuid=usergroups.uid AND (rights="ADVANCED_USER" OR rights="SUPER_USER") AND isdeleted="false" LIMIT 1);
UPDATE usergroups SET owneruid=@adminuid WHERE ISNULL(owneruid);


UPDATE users      SET owneruid=(SELECT owneruid FROM usergroups WHERE uid=users.usergroupuid AND isdeleted="false" LIMIT 1);
UPDATE users      SET owneruid=@adminuid WHERE ISNULL(owneruid);

UPDATE tasks      SET owneruid=(SELECT owneruid FROM works WHERE uid=works.uid AND isdeleted="false" LIMIT 1) WHERE isdeleted="false";
UPDATE tasks      SET owneruid=@adminuid WHERE ISNULL(owneruid);

UPDATE groups     SET accessrights=conv(755,16,10);
UPDATE hosts      SET accessrights=conv(755,16,10);
UPDATE sessions   SET accessrights=conv(755,16,10);
UPDATE traces     SET accessrights=conv(755,16,10);
UPDATE usergroups SET accessrights=conv(755,16,10);
UPDATE users      SET accessrights=conv(755,16,10);
UPDATE tasks      SET accessrights=(SELECT accessrights FROM works WHERE uid=works.uid AND isdeleted="false" LIMIT 1) WHERE isdeleted="false";
UPDATE tasks      SET accessrights=conv(755,16,10) WHERE ISNULL(accessrights);

UPDATE users      SET accessrights=(SELECT accessrights FROM usergroups WHERE uid=users.usergroupuid AND isdeleted="false" LIMIT 1);
UPDATE users      SET accessrights=conv(755,16,10) WHERE ISNULL(accessrights);

--
-- End Of File
--
