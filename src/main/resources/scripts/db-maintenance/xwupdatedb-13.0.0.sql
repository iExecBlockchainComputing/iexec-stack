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


create table if not exists  categories  (
  categoryId         int unsigned   not null  auto_increment     comment 'EnvID referenced by smart contracts',
  uid                char(36)       not null  primary key        comment 'Primary key',
  ownerUID           char(36)       not null                     comment 'User UID',
  accessRights       int(4)                   default 0x755      comment 'Please note that an category is always public',
  errorMsg           varchar(254)                                comment 'Error message',
  mtime              timestamp                                   comment 'Timestamp of last update',
  name               varchar(254)   not null  default 'NONE'     comment 'Envelope name is a free text',
  maxWallClockTime   int(10)        not null  default 300        comment 'Max amount of seconds a job can be computed; default 5mn',
  maxFreeMassStorage bigint         not null  default 5368709120 comment 'Max mass storage usage in bytes; default 5Gb',
  maxFileSize        bigint         not null  default 104857600  comment 'Max file length in bytes; default 100Mb',
  maxMemory          bigint         not null  default 536870912  comment 'Max RAM usage in bytes; default 512Mb',
  maxCpuSpeed        float          not null  default 0.5        comment 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)',


  index  name     (name),
  index  envId    (envId),
  index  ownerUID (ownerUID)
  )
engine  = InnoDB,
comment = 'envs = categories defining resources usage limit';

create table if not exists  categories_history  like  categories;


ALTER TABLE  works ADD    COLUMN categoryId          int unsigned   not null  default 1            comment 'envId. See common/Envs.java';
ALTER TABLE  works ADD    COLUMN marketOrderId       int unsigned             default 0            comment 'blockchain market order id',
ALTER TABLE  works ADD    COLUMN maxFreeMassStorage  bigint         not null  default 5368709120   comment 'Max mass storage usage in bytes; default 5Gb';
ALTER TABLE  works ADD    COLUMN maxFileSize         bigint         not null  default 104857600    comment 'Max file length in bytes; default 100Mb';
ALTER TABLE  works ADD    COLUMN maxMemory           bigint         not null  default 536870912    comment 'Max RAM usage in bytes; default 512Mb';
ALTER TABLE  works ADD    COLUMN maxCpuSpeed         float          not null  default 0.5          comment 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)';
ALTER TABLE  works ADD    COLUMN uploadbandwidth     float default 0.0      comment 'Upload bandwidth usage (in Mb/s)';
ALTER TABLE  works ADD    COLUMN downloadbandwidth   float default 0.0      comment 'Download bandwidth usage (in Mb/s)';

ALTER TABLE  works DROP   COLUMN wallclocktime;
ALTER TABLE  works DROP   COLUMN diskSpace;

ALTER TABLE datas         CHANGE COLUMN md5 shasum varchar(254) comment 'Shasum for datas';
ALTER TABLE datas_history CHANGE COLUMN md5 shasum varchar(254) comment 'Shasum for datas';

insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (14, 'FAILED', 'works', 'The job does not fill its category requirements', null);

--
-- End Of File
--
