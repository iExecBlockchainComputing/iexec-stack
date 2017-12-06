-- XtremWeb-HEP 8.x Query :  Works pending and running by group, owner and arrival date

select work_owner_groups.label      as work_owner_group,
       left(work_owners.login,16)   as work_owner,
       works.arrivalDate, apps.name as application,
       works.dirinURI, works.status,
       works.returnCode, tasks.trial,
       hosts.name                   as host_name,
       hosts.project                as host_project,
       left(host_owners.login,16)   as host_owner,
       host_owner_groups.label      as host_owner_group,
       tasks.uid, tasks.insertionDate, tasks.startDate,
       tasks.laststartDate, tasks.lastAlive,
       hex(tasks.accessRights)      as accessRights,
       tasks.hostUID, works.sgid
from       usergroups as work_owner_groups
right join users      as work_owners       on work_owner_groups.uid    = work_owners.usergroupUID
inner join works                           on work_owners.uid          = works.owneruid
inner join apps                            on works.appUID             = apps.uid
inner join tasks                           on works.uid                = tasks.workUID
left  join hosts                           on tasks.hostUID            = hosts.uid
left  join users      as host_owners       on hosts.ownerUID           = host_owners.uid
left  join usergroups as host_owner_groups on host_owners.usergroupUID = host_owner_groups.uid
where (works.status = 'PENDING') or (works.status = 'RUNNING')
order by work_owner_group, work_owner, works.arrivalDate;
