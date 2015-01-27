-- XtremWeb-HEP 8.x Queries for Referential Integrity Detail of Bad References :


-- USERS WITH BAD REFERENCES

-- Users with bad Owner
select     'User with bad Owner' as "", 'Usergroup' as "", '#(Users)' as ""
union all
select     login, label, cast(number as char) from
(
select     users1.login, usergroups.label, count(users1.uid) as number
from       users as users1
left join  usergroups on usergroups.uid = users1.usergroupUID
where      (users1.ownerUID is not null) and
           (users1.ownerUID not in (select users.uid from users))
group by   users1.login, usergroups.label
order by   3 desc, 2 asc
) as users_with_bad_owner

-- Users with bad Usergroup
union all
select     '', '', ''
union all
select     'User with bad Usergroup', 'Owner', '#(Users)'
union all
select     user_name, owner_name, cast(number as char) from
(
select     users1.login as user_name, users2.login as owner_name, count(users2.uid) as number
from       users as users1
left join  users as users2 on users2.uid = users1.owneruid
where      (users1.usergroupUID is not null) and
           (users1.usergroupUID not in (select usergroups.uid from usergroups))
group by   users1.login, users2.login
order by   3 desc, 2 asc
) as users_with_bad_usergroup


-- HOSTS WITH BAD REFERENCES

-- Hosts with bad Owner
union all
select     '', '', ''
union all
select     'Host with bad Owner', 'Project', '#(Hosts)'
union all
select     host_name, host_project, cast(number as char) from
(
select     hosts.name as host_name, hosts.project as host_project, count(hosts.uid) as number
from       hosts
where      (hosts.ownerUID is not null) and
           (hosts.ownerUID not in (select users.uid from users))
group by   hosts.name, hosts.project
order by   3 desc, 2 asc
) as hosts_with_bad_owner

-- Hosts with bad Project
union all
select     '', '', ''
union all
select     'Host with bad Project', 'Owner', '#(Hosts)'
union all
select     host_name, login, cast(number as char) from
(
select     hosts.name as host_name, users.login, count(hosts.uid) as number
from       hosts
left join  users on users.uid = hosts.ownerUID
where      (hosts.project is not null) and
           (hosts.project <> '')       and
           (hosts.project not in (select usergroups.label from usergroups))
group by   hosts.name, users.login
order by   3 desc, 2 asc
) as hosts_with_bad_project


-- GROUPS WITH BAD REFERENCES

-- Groups with bad Owner
union all
select     '', '', ''
union all
select     'Group with bad Owner', 'Session', '#(Groups)'
union all
select     group_name, session_name, cast(number as char) from
(
select     groups.name as group_name, sessions.name as session_name, count(groups.uid) as number
from       groups
left join  sessions on sessions.uid = groups.sessionUID
where      (groups.ownerUID is not null) and
           (groups.ownerUID not in (select users.uid from users))
group by   groups.name, sessions.name
order by   3 desc, 2 asc
) as groups_with_bad_owner

-- Groups with bad Session
union all
select     '', '', ''
union all
select     'Group with bad Session', 'Owner', '#(Groups)'
union all
select     group_name, login, cast(number as char) from
(
select     groups.name as group_name, users.login, count(groups.uid) as number
from       groups
left join  users on users.uid = groups.ownerUID
where      (groups.sessionUID is not null) and
           (groups.sessionUID not in (select sessions.uid from sessions))
group by   groups.name, users.login
order by   3 desc, 2 asc
) as groups_with_bad_session
;


-- TASKS WITH BAD REFERENCES

-- Tasks with bad Host
select     'Owner' as "", '' as "", '#(Tasks with bad Host)' as ""
union all
select     login, '' , cast(number as char) from
(
select     users.login, count(tasks.uid) as number
from       users
right join tasks on tasks.ownerUID = users.uid
where      (tasks.hostUID is not null) and
           (tasks.hostUID not in (select hosts.uid from hosts))
group by   users.login
order by   2 desc, 1 asc
) as nb_tasks_with_bad_host

-- Tasks with bad Owner
union all
select     '', '' , ''
union all
select     'Host', '' , '#(Tasks with bad Owner)'
union all
select     host_name, '' , cast(number as char) from
(
select     hosts.name as host_name, count(tasks.uid) as number
from       hosts
right join tasks on tasks.hostUID = hosts.uid
where      (tasks.ownerUID is not null) and
           (tasks.ownerUID not in (select users.uid from users))
group by   hosts.name
order by   2 desc, 1 asc
) as nb_tasks_with_bad_owner

-- Tasks with bad Work
union all
select     '', '', ''
union all
select     'Owner', 'Host', '#(Tasks with bad Work)' as ""
union all
select     login, host_name, cast(number as char) from
(
select     users.login, hosts.name as host_name, count(tasks.uid) as number
from       tasks
left  join users on tasks.ownerUID = users.uid
left  join hosts on tasks.hostUID = hosts.uid
where      tasks.workUID not in (select works.uid from works)
group by   users.login, hosts.name
order by   3 desc, 1 asc, 2 asc
) as nb_tasks_with_bad_host
;


-- WORKS WITH BAD REFERENCES

-- Works with bad Application
select     'Owner' as "", 'Group' as "", 'Host' as "", 'Session' as "",
           '#(Works with bad Application)' as ""
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
where      (works.appUID is not null) and (works.appUID not in (select apps.uid from apps))
group by   users.login, groups.name, hosts.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_with_bad_application

-- Works with bad Group
union all
select     '', '', '', '', ''
union all
select     'Owner', 'Application', 'Host', 'Session', '#(Works with bad Group)'
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
where      (works.groupUID is not null) and (works.groupUID not in (select groups.uid from groups))
group by   users.login, apps.name, hosts.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_with_bad_group

-- Works with bad Expected host
union all
select     '', '', '', '', ''
union all
select     'Owner', 'Application', 'Group', 'Session', '#(Works with bad Expected host)'
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
where      (works.expectedhostUID is not null) and (works.expectedhostUID not in (select hosts.uid from hosts))
group by   users.login, apps.name, groups.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_with_bad_expected_host

-- Works with bad Session
union all
select     '', '', '', '', ''
union all
select     'Owner', 'Application', 'Group', 'Host', '#(Works with bad Session)'
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
where      (works.sessionUID is not null) and (works.sessionUID not in (select sessions.uid from sessions))
group by   users.login, apps.name, groups.name, hosts.name
order by   5 desc, 1 asc
) as nb_works_with_bad_session

-- Works with bad Owner
union all
select     '', '', '', '', ''
union all
select     'Application', 'Host', 'Group', 'Session', '#(Works with bad Owner)'
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
where      (works.ownerUID is not null) and (works.ownerUID not in (select users.uid from users))
group by   apps.name, groups.name, hosts.name, sessions.name
order by   5 desc, 1 asc
) as nb_works_with_bad_owner
;


-- OTHER TABLES WITH BAD REFERENCES TO USERS

-- Apps with bad Owner
select    count(apps.uid), apps.name as app_with_bad_owner
from      apps
where     (apps.ownerUID is not null) and (apps.ownerUID not in (select users.uid from users))
group by  apps.name
order by  1 desc, 2 asc;

-- Datas with bad Owner
select    count(datas.uid), datas.name as data_with_bad_owner
from      datas
where     (datas.ownerUID is not null) and (datas.ownerUID not in (select users.uid from users))
group by  datas.name
order by  1 desc, 2 asc;

-- Sessions with bad Owner
select    count(sessions.uid), sessions.name as session_with_bad_owner
from      sessions
where     (sessions.ownerUID is not null) and (sessions.ownerUID not in (select users.uid from users))
group by  sessions.name
order by  1 desc, 2 asc;

-- Usergroups with bad Owner
select    count(usergroups.uid), usergroups.label as usergroup_with_bad_owner
from      usergroups
where     (usergroups.ownerUID is not null) and (usergroups.ownerUID not in (select users.uid from users))
group by  usergroups.label
order by  1 desc, 2 asc;
