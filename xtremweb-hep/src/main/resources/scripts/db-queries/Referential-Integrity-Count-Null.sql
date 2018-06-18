-- XtremWeb-HEP 8.x Query for Referential Integrity Count of Nulls :

select concat('XtremWeb-HEP    Schema = ', schema(), '    ', now()) as "Table",
       '' as "Referred object", '' as "Null allowed", '' as "# Null"
union all
select 'users',      'owner',        '0', 
       cast(count(*) as char) from users      where users.ownerUID        is null
union all
select 'users',      'usergroup',    'Yes',
       cast(count(*) as char) from users      where users.usergroupUID    is null
union all
select '', '', '', ''
union all
select 'usergroups', 'owner',        '0',
       cast(count(*) as char) from usergroups where usergroups.ownerUID   is null
union all
select '', '', '', ''
union all
select 'hosts',      'owner',         '0',
       cast(count(*) as char) from hosts      where hosts.ownerUID        is null
union all
select '', '', '', ''
union all
select 'traces',     'owner',        '0',
       cast(count(*) as char) from traces     where traces.ownerUID       is null
union all
select 'traces',     'host',         '0',
       cast(count(*) as char) from traces     where traces.hostUID        is null
union all
select '', '', '', ''
union all
select 'datas',      'owner',        '0',
       cast(count(*) as char) from datas      where datas.ownerUID        is null
union all
select '', '', '', ''
union all
select 'apps',       'owner',        '0',
       cast(count(*) as char) from apps       where apps.ownerUID         is null
union all
select '', '', '', ''
union all
select 'sessions',   'owner',        '0',
       cast(count(*) as char) from sessions   where sessions.ownerUID     is null
union all
select '', '', '', ''
union all
select 'groups',     'owner',        '0',
       cast(count(*) as char) from groups     where groups.ownerUID       is null
union all
select 'groups',     'session',      'Yes',
       cast(count(*) as char) from groups     where groups.sessionUID     is null
union all
select '', '', '', ''
union all
select 'works',      'owner',        '0',
       cast(count(*) as char) from works      where works.ownerUID        is null
union all
select 'works',      'app',          '0',
       cast(count(*) as char) from works      where works.appUID          is null
union all
select 'works',      'session',      'Yes',
       cast(count(*) as char) from works      where works.sessionUID      is null
union all
select 'works',      'group',        'Yes',
       cast(count(*) as char) from works      where works.groupUID        is null
union all
select 'works',      'expectedhost', 'Yes',
       cast(count(*) as char) from works      where works.expectedhostUID is null
union all
select '', '', '', ''
union all
select 'tasks',      'owner',        '0',
       cast(count(*) as char) from tasks      where tasks.ownerUID        is null
union all
select 'tasks',      'work',         '0',
       cast(count(*) as char) from tasks      where tasks.workUID         is null
union all
select 'tasks',      'host',         'Yes',
       cast(count(*) as char) from tasks      where tasks.hostUID         is null;
