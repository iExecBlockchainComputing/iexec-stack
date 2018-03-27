-- 
-- Version : 8.3.1
-- 
-- File    : xwupdatedb-8.3.1.sql
-- Purpose : since 8.3.1
--           table 'version' is renamed as 'versions'
--           table 'version' is renamed as 'versions'
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



ALTER TABLE  version         RENAME AS     versions;
ALTER TABLE  groups          MODIFY COLUMN sessionUID char(50);
ALTER TABLE  groups_history  MODIFY COLUMN sessionUID char(50);

-- 
-- End Of File
-- 
