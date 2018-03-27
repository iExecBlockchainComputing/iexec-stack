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
-- Version : 7.2.0
--
-- File    : xwupdatedb-7.2.0.sql
-- Purpose : this file contains the needed SQL commands to 
--           update the XWHEP database from previous versions
--




-- linux x86 64 
ALTER TABLE  apps         ADD COLUMN linux_x86_64URI char(200);
ALTER TABLE  apps_history ADD COLUMN linux_x86_64URI char(200);
-- ld linux x86 64 
ALTER TABLE  apps         ADD COLUMN ldlinux_x86_64URI char(200);
ALTER TABLE  apps_history ADD COLUMN ldlinux_x86_64URI char(200);

-- service grid ID
ALTER TABLE  works         ADD COLUMN sgid char(200);
ALTER TABLE  works_history ADD COLUMN sgid char(200);

ALTER TABLE hosts          ADD COLUMN batchid char(200);
ALTER TABLE hosts          ADD COLUMN jobid   char(200);
ALTER TABLE hosts_history  ADD COLUMN batchid char(200);
ALTER TABLE hosts_history  ADD COLUMN jobid   char(200);


--
-- End Of File
--
