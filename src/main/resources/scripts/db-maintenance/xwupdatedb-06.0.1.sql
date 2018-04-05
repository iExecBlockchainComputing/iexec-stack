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
-- Version : 6.0.1
--
-- File    : xwupdatedb-6.0.1.sql
-- Purpose : this file contains the needed SQL commands to 
--           update the XWHEP database from previous versions
--


--
-- Since XWHEP 6.0.0, "hosts" table contains three new fields
-- 
ALTER TABLE hosts    ADD  COLUMN osversion   char(50);
ALTER TABLE hosts    ADD  COLUMN javaversion char(50);
ALTER TABLE hosts    ADD  COLUMN javadatamodel int(4);

--
-- Since XWHEP 6.0.0, "apps" table contains two new fields
-- 
ALTER TABLE apps    ADD  COLUMN win32_x86_64URI char(200);
ALTER TABLE apps    ADD  COLUMN ldwin32_x86_64URI char(200);


--
-- End Of File
--
