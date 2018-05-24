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

SET FOREIGN_KEY_CHECKS=0;

insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (15, 'CONTRIBUTED',   'works',               'The job does not fill its category requirements',                                   null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (16, 'REVEALING',     'works',               'The job does not fill its category requirements',                                   null);

ALTER TABLE  hosts ADD    COLUMN ethwalletaddr        varchar(254)                                comment 'worker eth wallet address; optional';
ALTER TABLE  hosts ADD    COLUMN marketorderUID       char(36)                                    comment 'Optional, UID of the market order';
ALTER TABLE  hosts ADD    COLUMN hascontributed       char(5)                    default 'false'  comment 'This flag tells whether this host ahs already contributed to its current market order';
ALTER TABLE  hosts ADD    COLUMN workerpooladdr       varchar(254)                                comment 'workerpool addr this host is registered to';
ALTER TABLE  hosts ADD    COLUMN contributionstatusId tinyint unsigned  not null  default 255     comment 'Contribution Status Id. See common/XWStatus.java',
ALTER TABLE  hosts ADD    COLUMN contributionstatus   varchar(36)       not null  default 'NONE'  comment 'Contribution Status. see common/XWStatus.java',

ALTER TABLE  apps  ADD    COLUMN price             bigint          default 0  comment 'price since 13.1.0',

ALTER TABLE  works ADD    COLUMN marketorderIdx    bigint                     comment 'Optional, ID of the market order to sell CPU';
ALTER TABLE  works ADD    COLUMN requester         varchar(50)                comment 'requester is a public key of a blockchain wallet; since 13.1.0',
ALTER TABLE  works ADD    COLUMN dataset           varchar(50)                comment 'dataset is a blockchain smart contract address; since 13.1.0',
ALTER TABLE  works ADD    COLUMN workerPool        varchar(50)                comment 'worker pool is blockchain smart contract address; since 13.1.0',
ALTER TABLE  works ADD    COLUMN emitcost          bigint                     comment 'blockchain cost; since 13.1.0'
ALTER TABLE  works ADD    COLUMN callback          varchar(50)                comment 'since 13.1.0',
ALTER TABLE  works ADD    COLUMN beneficiary       varchar(50)                comment 'since 13.1.0',
ALTER TABLE  works ADD    COLUMN marketorderUID    char(36)                   comment 'Optional, UID of the market order';
ALTER TABLE  works ADD    COLUMN h2h2r             varchar(254)               comment 'this is the contribution proposal h(h(r)), if this work belongs a market order';
ALTER TABLE  works ADD    COLUMN h2r               varchar(254)               comment 'this is the contribution proof h(r), if this work belongs a market order';
ALTER TABLE  works ADD    COLUMN workOrderId       varchar(254)               comment 'this is the blockchain work order id';



ALTER TABLE  works CHANGE COLUMN replications  replications bigint  default 0        comment 'Optionnal. Amount of expected replications. No replication, if <= 0';
ALTER TABLE  works CHANGE COLUMN sizer         sizer        bigint  default 0        comment 'Optionnal. This is the size of the replica set';
ALTER TABLE  works CHANGE COLUMN totalr        totalr       bigint  default 0        comment 'Optionnal. Current amount of replicas';

ALTER TABLE  tasks ADD    COLUMN price         bigint               default 0        comment 'since 13.1.0';

-- ---------------------------------------------------------------------------
-- Table "marketorders" :
-- This table contains "marketorders" that can be created wy the scheduler
-- Since 13.1.0
-- ---------------------------------------------------------------------------
create table if not exists  marketorders (
  uid                  char(36)       not null  primary key        comment 'Primary key',
  ownerUID             char(36)       not null                     comment 'User UID',
  accessRights         int(4)                   default 0x755      comment 'Please note that a category is always public',
  errorMsg             varchar(254)                                comment 'Error message',
  mtime                timestamp                                   comment 'Timestamp of last update',
  direction            char(25)       not null  default 'UNSET'    comment 'Please see MarketOrderDirectionEnum',
  marketOrderIdx       bigint                                      comment 'market order index',
  categoryId           bigint         not null                     comment 'catID reference',
  expectedWorkers      bigint         not null                     comment 'how many workers to safely reach the trust',
  nbWorkers            bigint         not null  default 0          comment 'how many workers alredy booked',
  trust                bigint         not null  default 70         comment 'expected trust',
  price                bigint         not null  default 0          comment 'this is the cost or the price, depending on direction; this is named value in smart contract',
  volume               bigint                   default 0          comment 'how many such orders the scheduler can propose; this is calculated by the scheduler',
  remaining            bigint                   default 0          comment 'how many such orders left; this is calculated by the scheduler',
  workerpooladdr       varchar(254)   not null                     comment 'workerpool smart contract address',
  workerpoolowneraddr  varchar(254)   not null                     comment 'workerpool owner address',
  statusId             tinyint unsigned  not null  default 255     comment 'Status Id. See common/XWStatus.java',
  status               varchar(36)       not null  default 'NONE'  comment 'Status. see common/XWStatus.java',
  arrivalDate          datetime                                    comment 'insertion date',
  startDate            datetime                                    comment 'ready for computation date',
  completedDate        datetime                                    comment 'completion date',
  contributingDate     datetime                                    comment 'contributing date',
  revealingDate        datetime                                    comment 'revealing date',

  index  idx_catgoryid         (categoryid),
  index  idx_workerpooladdr    (workerpooladdr),
  index  ownerUID (ownerUID),
  foreign key (owneruid) references users(uid)
  )
engine  = InnoDB,
comment = 'marketorders = marketorders to sell CPU power';

show warnings;

create table if not exists  marketorders_history  like  marketorders;

show warnings;


insert into versions (version, installation) values ('13.1.0', now());

drop table userRights;
create table if not exists  userRights  (
  userRightId           tinyint unsigned  not null  primary key,
  userRightName         varchar(254)      not null  unique,
  mtime                 timestamp,
  userRightDescription  varchar(254)
  )
engine  = InnoDB,
comment = 'userRights = Constants for "users"."rights"';

-- ---------------------------------------------------------------------------
-- Data for table "userRights"
-- ---------------------------------------------------------------------------
insert into userRights (userRightId, userRightName, userRightDescription) values ( 0, 'NONE',              null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 1, 'INSERTJOB',         null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 2, 'GETJOB',            null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 3, 'INSERTDATA',        null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 4, 'GETDATA',           null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 5, 'GETGROUP',          null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 6, 'GETSESSION',        null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 7, 'GETHOST',           null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 8, 'GETAPP',            null);
insert into userRights (userRightId, userRightName, userRightDescription) values ( 9, 'GETUSER',           null);
insert into userRights (userRightId, userRightName, userRightDescription) values (10, 'GETCATEGORY',       null);
insert into userRights (userRightId, userRightName, userRightDescription) values (11, 'GETMARKETORDER',    null);
insert into userRights (userRightId, userRightName, userRightDescription) values (12, 'UPDATEWORK',        'worker can update work for the owner');
insert into userRights (userRightId, userRightName, userRightDescription) values (13, 'WORKER_USER',       'worker cannot do everything');
insert into userRights (userRightId, userRightName, userRightDescription) values (14, 'VWORKER_USER',      'vworker can take advantage of stickybit');
insert into userRights (userRightId, userRightName, userRightDescription) values (15, 'BROADCAST',         'submit one job to all workers');
insert into userRights (userRightId, userRightName, userRightDescription) values (16, 'LISTJOB',           null);
insert into userRights (userRightId, userRightName, userRightDescription) values (17, 'DELETEJOB',         null);
insert into userRights (userRightId, userRightName, userRightDescription) values (18, 'LISTDATA',          null);
insert into userRights (userRightId, userRightName, userRightDescription) values (19, 'DELETEDATA',        null);
insert into userRights (userRightId, userRightName, userRightDescription) values (20, 'LISTGROUP',         null);
insert into userRights (userRightId, userRightName, userRightDescription) values (21, 'INSERTGROUP',       null);
insert into userRights (userRightId, userRightName, userRightDescription) values (22, 'DELETEGROUP',       null);
insert into userRights (userRightId, userRightName, userRightDescription) values (23, 'LISTSESSION',       null);
insert into userRights (userRightId, userRightName, userRightDescription) values (24, 'INSERTSESSION',     null);
insert into userRights (userRightId, userRightName, userRightDescription) values (25, 'DELETESESSION',     null);
insert into userRights (userRightId, userRightName, userRightDescription) values (26, 'LISTHOST',          null);
insert into userRights (userRightId, userRightName, userRightDescription) values (27, 'LISTUSER',          null);
insert into userRights (userRightId, userRightName, userRightDescription) values (28, 'LISTUSERGROUP',     null);
insert into userRights (userRightId, userRightName, userRightDescription) values (29, 'GETUSERGROUP',      null);
insert into userRights (userRightId, userRightName, userRightDescription) values (30, 'INSERTAPP',         null);
insert into userRights (userRightId, userRightName, userRightDescription) values (31, 'DELETEAPP',         null);
insert into userRights (userRightId, userRightName, userRightDescription) values (32, 'LISTAPP',           null);
insert into userRights (userRightId, userRightName, userRightDescription) values (33, 'LISTCATEGORY',      null);
insert into userRights (userRightId, userRightName, userRightDescription) values (34, 'LISTMARKETORDER',   null);
insert into userRights (userRightId, userRightName, userRightDescription) values (35, 'STANDARD_USER',     'non privileged user');
insert into userRights (userRightId, userRightName, userRightDescription) values (36, 'INSERTUSER',        null);
insert into userRights (userRightId, userRightName, userRightDescription) values (37, 'DELETEUSER',        null);
insert into userRights (userRightId, userRightName, userRightDescription) values (38, 'ADVANCED_USER',     'privileged user (e.g. user group manager)');
insert into userRights (userRightId, userRightName, userRightDescription) values (39, 'MANDATED_USER',     'can work in name of another user');
insert into userRights (userRightId, userRightName, userRightDescription) values (40, 'INSERTHOST',        null);
insert into userRights (userRightId, userRightName, userRightDescription) values (42, 'INSERTCATEGORY',    null);
insert into userRights (userRightId, userRightName, userRightDescription) values (43, 'INSERTMARKETORDER', null);
insert into userRights (userRightId, userRightName, userRightDescription) values (44, 'DELETEHOST',        null);
insert into userRights (userRightId, userRightName, userRightDescription) values (45, 'INSERTUSERGROUP',   null);
insert into userRights (userRightId, userRightName, userRightDescription) values (46, 'DELETEUSERGROUP',   null);
insert into userRights (userRightId, userRightName, userRightDescription) values (47, 'DELETEMARKETORDER',   null);
insert into userRights (userRightId, userRightName, userRightDescription) values (48, 'SUPER_USER',        'can do all');


UPDATE users SET userRightId=(select userRightId from userRights where userRightName=users.rights);

delete from statuses;

insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 0, 'NONE',          'none',                null,                                                                                null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 1, 'ANY',           'any',                 null,                                                                                null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 2, 'WAITING',       'works',               'The object is stored on server but not in the server queue yet',                    null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 3, 'PENDING',       'works, tasks',        'The object is stored and inserted in the server queue',                             null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 4, 'RUNNING',       'works, tasks',        'The object is being run by a worker',                                               null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 5, 'ERROR',         'any',                 'The object is erroneous',                                                           null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 6, 'COMPLETED',     'works, tasks',        'The job has been successfully computed',                                            null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 7, 'ABORTED',       'works, tasks',        'NOT used anymore', 'Since XWHEP, aborted objects are set to PENDING');
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 8, 'LOST',          'works, tasks',        'NOT used anymore', 'Since XWHEP, lost objects are set to PENDING');
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values ( 9, 'DATAREQUEST',   'datas, works, tasks', 'The server is unable to store the uploaded object. Waiting for another upload try', null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (10, 'RESULTREQUEST', 'works',               'The worker should retry to upload the results',                                     null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (11, 'AVAILABLE',     'datas',               'The data is available and can be downloaded on demand',                             null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (12, 'UNAVAILABLE',   'datas',               'The data is not available and can not be downloaded on demand',                     null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (13, 'REPLICATING',   'works',               'The object is being replicated',                                                    null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (14, 'FAILED',        'works',               'The job does not fill its category requirements',                                   null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (15, 'CONTRIBUTING',   'works',               'The job does not fill its category requirements',                                   null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (16, 'CONTRIBUTED',   'works',               'The job does not fill its category requirements',                                   null);
insert into statuses (statusId, statusName, statusObjects, statusComment, statusDeprecated) values (17, 'REVEALING',     'works',               'The job does not fill its category requirements',                                   null);

UPDATE hosts        SET contributionstatusId=(select statusId from statuses where statusName=hosts.contributionstatus);
UPDATE datas        SET statusId=(select statusId from statuses where statusName=datas.status);
UPDATE marketorders SET statusId=(select statusId from statuses where statusName=marketorders.status);
UPDATE works        SET statusId=(select statusId from statuses where statusName=works.status);
UPDATE tasks        SET statusId=(select statusId from statuses where statusName=tasks.status);


SET FOREIGN_KEY_CHECKS=1;

--
-- End Of File
--
