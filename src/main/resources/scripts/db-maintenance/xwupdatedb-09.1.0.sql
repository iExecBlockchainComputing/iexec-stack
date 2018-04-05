-- ===========================================================================
--  Copyrights     : CNRS
--  Authors        : Oleg Lodygensky
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
-- ===========================================================================

--
-- Version : 9.1.0
--
-- File    : xwupdatedb-9.1.0.sql
-- Purpose : this file contains the needed SQL commands to 
--           test if DB is 9.1.0 compliant
--




ALTER TABLE  hosts            ADD    COLUMN availablemem         int(10);
ALTER TABLE  hosts            MODIFY COLUMN cpuspeed             int(10);
ALTER TABLE  apps             MODIFY COLUMN minFreeMassStorage   bigint;
ALTER TABLE  works            MODIFY COLUMN diskSpace            bigint;
ALTER TABLE  works            MODIFY COLUMN minFreeMassStorage   bigint;

ALTER TABLE  hosts_history    ADD    COLUMN availablemem         int(10);
ALTER TABLE  hosts_history    MODIFY COLUMN cpuspeed             int(10);
ALTER TABLE  apps_history     MODIFY COLUMN minFreeMassStorage   bigint;
ALTER TABLE  works_history    MODIFY COLUMN diskSpace            bigint;
ALTER TABLE  works_history    MODIFY COLUMN minFreeMassStorage   bigint;


--
-- End Of File
--
