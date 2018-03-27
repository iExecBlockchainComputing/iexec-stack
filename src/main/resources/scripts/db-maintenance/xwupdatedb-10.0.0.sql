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


ALTER TABLE  datas            ADD  COLUMN workUID        char(36);
ALTER TABLE  datas            ADD  COLUMN package        varchar(254);
ALTER TABLE  works            ADD  COLUMN replicatedUID  char(36);
ALTER TABLE  works            ADD  COLUMN replications   int(3);
ALTER TABLE  works            ADD  COLUMN sizer          int(3);
ALTER TABLE  works            ADD  COLUMN totalr         int(3);
ALTER TABLE  works            ADD  COLUMN datadrivenURI  varchar(254);
ALTER TABLE  works            DROP COLUMN replicated;

ALTER TABLE  datas_history    ADD  COLUMN workUID        char(36);
ALTER TABLE  datas_history    ADD  COLUMN package        varchar(254);
ALTER TABLE  works_history    ADD  COLUMN replicatedUID  char(36);
ALTER TABLE  works_history    ADD  COLUMN replications   int(3);
ALTER TABLE  works_history    ADD  COLUMN sizer          int(3);
ALTER TABLE  works_history    ADD  COLUMN totalr         int(3);
ALTER TABLE  works_history    ADD  COLUMN datadrivenURI  varchar(254);
ALTER TABLE  works_history    DROP COLUMN replicated;


--
-- End Of File
--
