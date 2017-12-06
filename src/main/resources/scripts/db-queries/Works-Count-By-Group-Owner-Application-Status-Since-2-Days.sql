-- XtremWeb-HEP 7.x Query :  Count of works by group, owner, application and status since 2 days

select work_owner_groups.label    as work_owner_group,
       left(work_owners.login,16) as work_owner,
       apps.name                  as application,
       left(app_owners.login,16)  as app_owner,
       app_owner_groups.label     as app_owner_group,
       works.status               as work_status,
       count(works.uid)           as "#(works)"
from       usergroups as work_owner_groups
right join users      as work_owners       on work_owner_groups.uid   = work_owners.usergroupUID
inner join works                           on work_owners.uid         = works.owneruid
inner join apps                            on works.appUID            = apps.uid
inner join users      as app_owners        on apps.ownerUID           = app_owners.uid
left  join usergroups as app_owner_groups  on app_owners.usergroupUID = app_owner_groups.uid
where hour(timediff(now(), works.arrivalDate)) < (24 * 2)
group by work_owner_group, work_owner, application, app_owner, app_owner_group, work_status
order by work_owner_group, work_owner, application, work_status;
