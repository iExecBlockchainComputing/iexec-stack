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
-- Version : 7.4.1
--
-- File    : xwupdatedb-7.4.1.sql
-- Purpose : this file contains the needed SQL commands to 
--           update the XWHEP database from previous versions
--


-- 
ALTER TABLE works MODIFY COLUMN label     char(150);
ALTER TABLE datas MODIFY COLUMN name      char(200);

ALTER TABLE works_history MODIFY COLUMN label     char(150);
ALTER TABLE datas_history MODIFY COLUMN name      char(200);

--
-- End Of File
--
