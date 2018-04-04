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


create table if not exists  envs  (
  envId              int unsigned   not null  auto_increment     comment 'EnvID referenced by smart contracts',
  uid                char(36)       not null  primary key        comment 'Primary key',
  ownerUID           char(36)       not null                     comment 'User UID',
  accessRights       int(4)                   default 0x755      comment 'Please note that an envelope is always public',
  errorMsg           varchar(254)                                comment 'Error message',
  mtime              timestamp                                   comment 'Timestamp of last update',
  name               varchar(254)   not null  default 'NONE'     comment 'Envelope name is a free text',
  maxWallClockTime   bigint         not null  default 300        comment 'Max amount of seconds a job can be computed; default 5mn',
  maxFreeMassStorage bigint         not null  default 5368709120 comment 'Max mass storage usage in bytes; default 5Gb',
  maxFileSize        bigint         not null  default 104857600  comment 'Max file length in bytes; default 100Mb',
  maxMemory          bigint         not null  default 536870912  comment 'Max RAM usage in bytes; default 512Mb',
  maxCpuSpeed        float          not null  default 0.5        comment 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)',


  index  name     (name),
  index  envId    (envId),
  index  ownerUID (ownerUID),
  foreign key (owneruid) references users(uid)
  )
engine  = InnoDB,
comment = 'envs = envelopes defining resources usage limit';

create table if not exists  envs_history  like  envs;

insert into envs (uid, owneruid, accessrights, errormsg, name, envid)
        values ("cb2b401c-374a-11e8-a703-4f504e4b684f", (select uid from users where rights="SUPER_USER" limit 1), "1877", '', 'DefaultEnv', 0);


ALTER TABLE  works ADD    COLUMN envId              int unsigned   not null  default 1  comment 'envId. See common/Envs.java';
ALTER TABLE  works ADD    COLUMN maxFreeMassStorage bigint         not null  default 5368709120   comment 'Max mass storage usage in bytes; default 5Gb';
ALTER TABLE  works ADD    COLUMN maxFileSize        bigint         not null  default 104857600    comment 'Max file length in bytes; default 100Mb';
ALTER TABLE  works ADD    COLUMN maxMemory          bigint         not null  default 536870912    comment 'Max RAM usage in bytes; default 512Mb';
ALTER TABLE  works ADD    COLUMN maxCpuSpeed        float          not null  default 0.5          comment 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)';

ALTER TABLE  works MODIFY COLUMN minMemory           bigint                      default 0        comment 'Optionnal. minimum memory needed in Kb';
ALTER TABLE  works MODIFY COLUMN minCPUSpeed         int                         default 0        comment 'Optionnal. minimum CPU speed needed in MHz';
ALTER TABLE  works MODIFY COLUMN maxWallClockTime   bigint         not null  default 900          comment 'Max amount of seconds a job can be computed; default 5mn';

ALTER TABLE  works DROP   COLUMN wallclocktime;
ALTER TABLE  works DROP   COLUMN diskSpace;

update table works set envid='1', maxWallClocktime='300', maxFreeMassStorage='5368709120', maxFileSize='104857600', maxMemory='536870912', maxCpuSpeed='0.5';
--
-- End Of File
--
