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
--     You should have received a copy of the GNU General Public License
--     along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
-- 
-- 



--
-- Version : 7.4.1
--
-- File    : xwcheckdb-7.4.1.sql
-- Purpose : this file contains the needed SQL commands to 
--           test if DB is 7.4.1 compliant
--


-- 
-- Since XWHEP 7.4.1 :
--  * works.label is char(150)
--  * datas.name is char(200)

-- we always want to update
-- SELECT dummy FROM apps;
-- ALTER TABLE works MODIFY COLUMN label     char(150);

SELECT       column_type
       FROM  information_schema.columns
       WHERE column_name = "label"
        AND  table_name = "works" 
        AND  column_type="char(50)";
--
-- End Of File
--
