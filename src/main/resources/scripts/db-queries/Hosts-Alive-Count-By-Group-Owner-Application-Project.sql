-- XtremWeb-HEP 7.x Query :  Count of alive hosts by group, owner, application and project

select apps.name                                            as application,
       app_owners.login                                     as app_owner,
       usergroups.label                                     as owner_group,
       host_owners.login                                    as host_owner,
       hosts.project                                        as host_project,
       sum( timediff(now(), hosts.lastAlive) < '00:15:00' ) as "#(alive)"
from       apps
inner join users as app_owners  on apps.ownerUID           = app_owners.uid
left  join usergroups           on app_owners.usergroupUID = usergroups.uid
right join users as host_owners on usergroups.uid          = host_owners.usergroupUID
inner join hosts                on host_owners.uid         = hosts.ownerUID
where      not isNull(hosts.lastAlive)
group by   owner_group, app_owner, application, host_owner, host_project
order by   owner_group, app_owner, application, host_project;
