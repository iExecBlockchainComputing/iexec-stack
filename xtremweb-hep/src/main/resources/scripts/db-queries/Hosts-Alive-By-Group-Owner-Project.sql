-- XtremWeb-HEP 7.x Query :  Hosts alive by group, owner and project

select usergroups.label as worker_owner_group,
       users.login      as worker_owner,
       hosts.project    as host_project,
       hosts.cputype    as host_cputype,
       hosts.name       as host_name
from       usergroups
right join users on usergroups.uid = users.usergroupUID
inner join hosts on users.uid      = hosts.ownerUID
where      ( not isNull(hosts.lastAlive) ) and
           ( timediff(now(), hosts.lastAlive) < '00:15:00' )
order by   worker_owner_group, worker_owner, host_project;
