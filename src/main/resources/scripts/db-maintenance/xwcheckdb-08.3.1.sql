-- 
-- Version : 8.3.1
-- 
-- File    : xwcheckdb-8.3.1.sql
-- Purpose : this file contains the needed SQL commands to 
--           test if DB is 8.3.1 compliant
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
-- Since XWHEP 8.3.1 :
-- version table is renamed 'versions'
-- 
SELECT * FROM versions;

-- 
-- End Of File
-- 
