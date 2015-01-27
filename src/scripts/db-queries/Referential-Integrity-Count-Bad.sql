-- XtremWeb-HEP 8.x Query for Referential Integrity Count of Bad References :

select concat('XtremWeb-HEP    Schema = ', schema(), '    ', now()) as "Table",
       '' as "Referred object", '' as "# not in live", '' as "# in history", '' as "# nowhere"

union all
select   'users',      'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select users1.uid,
         (users1.ownerUID in (select users_history.uid from users_history)) as in_history
  from   users as users1
  where  (users1.ownerUID is not null) and
         (users1.ownerUID not in (select users.uid from users))
) as     users_owner_not_live

union all
select   'users',      'usergroup',         cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select users.uid,
         (users.usergroupUID in (select usergroups_history.uid from usergroups_history)) as in_history
  from   users
  where  (users.usergroupUID is not null) and
         (users.usergroupUID not in (select usergroups.uid from usergroups))
) as     users_usergroup_not_live

union all select '', '', '', '', ''

union all
select   'usergroups', 'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select usergroups.uid,
         (usergroups.ownerUID in (select users_history.uid from users_history)) as in_history
  from   usergroups
  where  (usergroups.ownerUID is not null) and
         (usergroups.ownerUID not in (select users.uid from users))
) as usergroups_owner_not_live

union all select '', '', '', '', ''

union all
select   'hosts',      'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select hosts.uid,
         (hosts.ownerUID in (select users_history.uid from users_history)) as in_history
  from   hosts
  where  (hosts.ownerUID is not null) and
         (hosts.ownerUID not in (select users.uid from users))
) as hosts_owner_not_live

union all
select   'hosts',      'project',           cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select hosts.uid,
         (hosts.project in (select usergroups_history.label from usergroups_history)) as in_history
  from   hosts
  where  (hosts.project is not null) and
         (hosts.project <> '')       and
         (hosts.project not in (select usergroups.label from usergroups))
) as hosts_project_not_live

union all select '', '', '', '', ''

union all
select   'traces',     'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select traces.uid,
         (traces.ownerUID in (select users_history.uid from users_history)) as in_history
  from   traces
  where  (traces.ownerUID is not null) and
         (traces.ownerUID not in (select users.uid from users))
) as traces_owner_not_live

union all
select   'traces',     'host',              cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select traces.uid,
         (traces.hostUID in (select hosts_history.uid from hosts_history)) as in_history
  from   traces
  where  (traces.hostUID is not null) and
         (traces.hostUID not in (select hosts.uid from hosts))
) as traces_host_not_live

union all select '', '', '', '', ''

union all
select   'datas',      'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select datas.uid,
         (datas.ownerUID in (select users_history.uid from users_history)) as in_history
  from   datas
  where  (datas.ownerUID is not null) and
         (datas.ownerUID not in (select users.uid from users))
) as datas_owner_not_live

union all select '', '', '', '', ''

union all
select   'apps',       'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select apps.uid,
         (apps.ownerUID in (select users_history.uid from users_history)) as in_history
  from   apps
  where  (apps.ownerUID is not null) and
         (apps.ownerUID not in (select users.uid from users))
) as apps_owner_not_live

union all select '', '', '', '', ''

union all
select   'sessions',   'owner',            cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select sessions.uid,
         (sessions.ownerUID in (select users_history.uid from users_history)) as in_history
  from   sessions
  where  (sessions.ownerUID is not null) and
         (sessions.ownerUID not in (select users.uid from users))
) as sessions_owner_not_live

union all select '', '', '', '', ''

union all
select   'groups',     'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select groups.uid,
         (groups.ownerUID in (select users_history.uid from users_history)) as in_history
  from   groups
  where  (groups.ownerUID is not null) and
         (groups.ownerUID not in (select users.uid from users))
) as groups_owner_not_live

union all
select   'groups',     'session',           cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select groups.uid,
         (groups.sessionUID in (select sessions_history.uid from sessions_history)) as in_history
  from   groups
  where  (groups.sessionUID is not null) and
         (groups.sessionUID not in (select sessions.uid from sessions))
) as groups_session_not_live

union all select '', '', '', '', ''

union all
select   'works',      'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select works.uid,
         (works.ownerUID in (select users_history.uid from users_history)) as in_history
  from   works
  where  (works.ownerUID is not null) and
         (works.ownerUID not in (select users.uid from users))
) as works_owner_not_live

union all
select   'works',      'app',               cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select works.uid,
         (works.appUID in (select apps_history.uid from apps_history)) as in_history
  from   works
  where  (works.appUID is not null) and
         (works.appUID not in (select apps.uid from apps))
) as works_app_not_live

union all
select   'works',      'session',           cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select works.uid,
         (works.sessionUID in (select sessions_history.uid from sessions_history)) as in_history
  from   works
  where  (works.sessionUID is not null) and
         (works.sessionUID not in (select sessions.uid from sessions))
) as works_session_not_live

union all
select   'works',      'group',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select works.uid,
         (works.groupUID in (select groups_history.uid from groups_history)) as in_history
  from   works
  where  (works.groupUID is not null) and
         (works.groupUID not in (select groups.uid from groups))
) as works_group_not_live

union all
select   'works',      'expectedhost',      cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select works.uid,
         (works.expectedhostUID in (select hosts_history.uid from hosts_history)) as in_history
  from   works
  where  (works.expectedhostUID is not null) and
         (works.expectedhostUID not in (select hosts.uid from hosts))
) as works_expected_host_not_live

union all select '', '', '', '', ''

union all
select   'tasks',      'owner',             cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select tasks.uid,
         (tasks.ownerUID in (select users_history.uid from users_history)) as in_history
  from   tasks
  where  (tasks.ownerUID is not null) and
         (tasks.ownerUID not in (select users.uid from users))
) as tasks_owner_not_live

union all
select 'tasks',      'work',                cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select tasks.uid,
         (tasks.workUID in (select works_history.uid from works_history)) as in_history
  from   tasks
  where  (tasks.workUID is not null) and
         (tasks.workUID not in (select works.uid from works))
) as tasks_work_not_live

union all
select   'tasks',      'host',              cast(ifnull(count(uid),                   '') as char),
                                            cast(ifnull(sum(in_history),              '') as char),
                                            cast(ifnull(count(uid) - sum(in_history), '') as char)
from
( select tasks.uid,
         (tasks.hostUID in (select hosts_history.uid from hosts_history)) as in_history
  from   tasks
  where  (tasks.hostUID is not null) and
         (tasks.hostUID not in (select hosts.uid from hosts))
) as tasks_host_not_live
;
