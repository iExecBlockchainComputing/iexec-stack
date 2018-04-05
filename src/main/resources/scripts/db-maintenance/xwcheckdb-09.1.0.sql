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
-- File    : xwcheckdb-9.1.0.sql
-- Purpose : this file contains the needed SQL commands to 
--           test if DB is 9.1.0 compliant
--


-- 
-- Since XWHEP 9.1.0 :
--  * some data types have changed
-- 

-- 
-- let always return false to force update, just in case
-- since there is no new nor removed column, but column types modification only, I don't have any other idea
-- ;)
-- 
SELECT availablemem FROM hosts;

--
-- End Of File
--
