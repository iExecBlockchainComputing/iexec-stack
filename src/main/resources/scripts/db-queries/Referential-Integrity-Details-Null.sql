-- XtremWeb-HEP 8.x Queries for Referential Integrity Detail of Null References :


-- USERS WITHOUT REFERENCES

-- Users without Owner
select     'User without Owner' as "", 'Usergroup' as "", '#(Users)' as ""
union all
select     login, label, cast(number as char) from
(
select     users1.login, usergroups.label, count(users1.uid) as number
from       users as users1
left join  usergroups on usergroups.uid = users1.usergroupUID
where      users1.ownerUID is null
group by   users1.login, usergroups.label
order by   3 desc, 2 asc
) as users_without_owner

-- Users without Usergroup
union all
select     '', '', ''
union all
select     'User without Usergroup', 'Owner', '#(Users)'
union all
select     user_name, owner_name, cast(number as char) from
(
select     users1.login as user_name, users2.login as owner_name, count(users2.uid) as number
from       users as users1
left join  users as users2 on users2.uid = users1.owneruid
where      users1.usergroupUID is null
group by   users1.login, users2.login
order by   3 desc, 2 asc
) as users_without_usergroup


-- GROUPS WITHOUT REFERENCES

-- Groups without Owner
union all
select     '', '', ''
union all
select     'Group without Owner', 'Session', '#(Groups)'
union all
select     group_name, session_name, cast(number as char) from
(
select     groups.name as group_name, sessions.name as session_name, count(groups.uid) as number
from       groups
left join  sessions on sessions.uid = groups.sessionUID
where      groups.ownerUID is null
group by   groups.name, sessions.name
order by   3 desc, 2 asc
) as groups_without_owner

-- Groups without Session
union all
select     '', '', ''
union all
select     'Group without Session', 'Owner', '#(Groups)'
union all
select     group_name, login, cast(number as char) from
(
select     groups.name as group_name, users.login, count(groups.uid) as number
from       groups
left join  users on users.uid = groups.ownerUID
where      groups.sessionUID is null
group by   groups.name, users.login
order by   3 desc, 2 asc
) as groups_without_session
;


-- TASKS WITHOUT REFERENCES

-- Tasks without Host
select     'Owner' as "", '' as "", '#(Tasks without Host)' as ""
union all
select     login, '', cast(number as char) from
(
select     users.login, count(tasks.uid) as number
from       users
right join tasks on tasks.ownerUID = users.uid
where      tasks.hostUID is null
group by   users.login
order by   2 desc, 1 asc
) as nb_tasks_without_host

-- Tasks without Owner
union all
select     '    ', '', ''
union all
select     'Host', '', '#(Tasks without Owner)'
union all
select     host_name, '', cast(number as char) from
(
select     hosts.name as host_name, count(tasks.uid) as number
from       hosts
right join tasks on tasks.hostUID = hosts.uid
where      tasks.ownerUID is null
group by   hosts.name
order by   2 desc, 1 asc
) as nb_tasks_without_owner

-- Tasks without Work
union all
select     '    ', '', ''
union all
select     'Owner', 'Host', '#(Tasks without Work)'
union all
select     login, host_name, cast(number as char) from
(
select     users.login, hosts.name as host_name, count(tasks.uid) as number
from       tasks
left  join users on tasks.ownerUID = users.uid
left  join hosts on tasks.hostUID = hosts.uid
where      tasks.workUID is null
group by   users.login, hosts.name
order by   3 desc, 1 asc, 2 asc
) as nb_tasks_without_work
;


-- WORKS WITHOUT REFERENCES

-- Works without Application
select     'Owner' as "", 'Group' as "", 'Host' as "", 'Session' as "",
           '#(Works without Application)' as ""
union all
select     login, group_name, host_name, session_name, cast(number as char) from
(
select     users.login, groups.name as group_name, hosts.name as host_name,
           sessions.name as session_name, count(works.uid) as number
from       works
left  join users    on users.uid    = works.ownerUID
left  join groups   on groups.uid   = works.groupUID
left  join hosts    on hosts.uid    = works.expectedhostUID
left  join sessions on sessions.uid = works.sessionUID
where      works.appUID is null
group by   users.login, groups.name, hosts.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_without_application

-- Works without Group
union all
select     '     ', '           ', '    ', '       ', ''
union all
select     'Owner', 'Application', 'Host', 'Session', '#(Works without Group)'
union all
select     login, app_name, host_name, session_name, cast(number as char) from
(
select     users.login, apps.name as app_name, hosts.name as host_name,
           sessions.name as session_name, count(works.uid) as number
from       works
left  join users    on users.uid    = works.ownerUID
left  join apps     on apps.uid     = works.appUID
left  join hosts    on hosts.uid    = works.expectedhostUID
left  join sessions on sessions.uid = works.sessionUID
where      works.groupUID is null
group by   users.login, apps.name, hosts.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_without_group

-- Works without Expected host
union all
select     '     ', '           ', '     ', '       ', ''
union all
select     'Owner', 'Application', 'Group', 'Session', '#(Works without Expected host)'
union all
select     login, app_name, group_name, session_name, cast(number as char) from
(
select     users.login, apps.name as app_name, groups.name as group_name,
           sessions.name as session_name, count(works.uid) as number
from       works
left  join users    on users.uid    = works.ownerUID
left  join apps     on apps.uid     = works.appUID
left  join groups   on groups.uid   = works.groupUID
left  join sessions on sessions.uid = works.sessionUID
where      works.expectedhostUID is null
group by   users.login, apps.name, groups.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_without_expected_host

-- Works without Session
union all
select     '     ', '           ', '    ', '     ', ''
union all
select     'Owner', 'Application', 'Group', 'Host', '#(Works without Session)'
union all
select     login, app_name, group_name, host_name, cast(number as char) from
(
select     users.login, apps.name as app_name, groups.name as group_name,
           hosts.name as host_name, count(works.uid) as number
from       works
left  join users  on users.uid  = works.ownerUID
left  join apps   on apps.uid   = works.appUID
left  join groups on groups.uid = works.groupUID
left  join hosts  on hosts.uid  = works.expectedhostUID
where      works.sessionUID is null
group by   users.login, apps.name, groups.name, hosts.name
order by   5 desc, 1 asc
) as nb_works_without_session

-- Works without Owner
union all
select     '           ', '    ', '     ', '       ', ''
union all
select     'Application', 'Host', 'Group', 'Session', '#(Works without Owner)'
union all
select     app_name, group_name, host_name, session_name, cast(number as char) from
(
select     apps.name as app_name, groups.name as group_name, hosts.name as host_name,
           sessions.name as session_name, count(works.uid) as number
from       works
left  join apps     on apps.uid     = works.appUID
left  join groups   on groups.uid   = works.groupUID
left  join hosts    on hosts.uid    = works.expectedhostUID
left  join sessions on sessions.uid = works.sessionUID
where      works.ownerUID is null
group by   apps.name, groups.name, hosts.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_without_owner
;


-- OTHER TABLES WITHOUT REFERENCES TO USERS

-- Apps without Owner
select    count(apps.uid), apps.name as app_without_owner
from      apps
where     apps.ownerUID is null
group by  apps.name
order by  1 desc, 2 asc;

-- Datas without Owner
select    count(datas.uid), datas.name as data_without_owner
from      datas
where     datas.ownerUID is null
group by  datas.name
order by  1 desc, 2 asc;

-- Hosts without Owner
select    count(hosts.uid), hosts.name as host_without_owner
from      hosts
where     hosts.ownerUID is null
group by  hosts.name
order by  1 desc, 2 asc;

-- Sessions without Owner
select    count(sessions.uid), sessions.name as session_without_owner
from      sessions
where     sessions.ownerUID is null
group by  sessions.name
order by  1 desc, 2 asc;

-- Usergroups without Owner
select    count(usergroups.uid), usergroups.label as usergroup_without_owner
from      usergroups
where     usergroups.ownerUID is null
group by  usergroups.label
order by  1 desc, 2 asc;
