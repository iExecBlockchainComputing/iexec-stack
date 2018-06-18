-- XtremWeb-HEP 7.x Query :  Count of alive and inactive hosts by group, owner and project

select usergroups.label                                      as host_owner_group,
       users.login                                           as host_owner,
       hosts.project                                         as host_project,
       hosts.cputype                                         as host_cputype,
       sum( timediff(now(), hosts.lastAlive) <  '00:15:00' ) as "#(alive_hosts)",
       sum( timediff(now(), hosts.lastAlive) >= '00:15:00' ) as "#(sleeping_hosts)",
       count(hosts.lastAlive)                                as "#(total_hosts)",
       min( datediff(now(), hosts.lastAlive) )               as days_inactive,
       min( timediff(now(), hosts.lastAlive) )               as time_inactive
from       usergroups
right join users on usergroups.uid = users.usergroupUID
inner join hosts on users.uid      = hosts.ownerUID
where      not isNull(hosts.lastAlive)
group by   host_owner_group, host_owner, host_project, host_cputype
order by   host_owner_group, host_owner, host_project, host_cputype;
