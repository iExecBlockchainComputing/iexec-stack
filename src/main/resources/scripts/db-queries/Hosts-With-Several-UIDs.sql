-- XtremWeb-HEP 7.x Query :
-- Count hosts having same characteristics  ('lastAlive' may be null or not null)
select   hosts.name, hosts.ipaddr, hosts.hwaddr, hosts.cputype, hosts.cpunb, hosts.cpumodel, hosts.os,
         sum(hosts.lastAlive is null)     as lastAlive_null,
         sum(hosts.lastAlive is not null) as lastAlive_not_null, count(hosts.uid) as nb_uids
from     hosts
group by hosts.name, hosts.ipaddr, hosts.hwaddr, hosts.cputype, hosts.cpunb, hosts.cpumodel, hosts.os
having   nb_uids > 1;


-- Sum of job numbers for all jobs having same characteristics  ('lastAlive' may be null or not null)
select   hosts.name as host_name, hosts.ipaddr as ip_addr,
         sum(hosts.lastAlive is null)     as lastAlive_null,
         sum(hosts.lastAlive is not null) as lastAlive_not_null, count(hosts.uid) as nb_uids,
         sum(hosts.nbjobs)      as sum_nb_jobs,      sum(hosts.pendingjobs) as sum_pending_jobs,
         sum(hosts.runningjobs) as sum_running_jobs, sum(hosts.errorjobs)   as sum_error_jobs,
         min( datediff(now(), hosts.lastAlive) ) as days_inactive,
         avg( datediff(now(), hosts.lastAlive) ) as avg_days_inactive
from     hosts
group by hosts.name, hosts.ipaddr, hosts.hwaddr, hosts.cputype, hosts.cpunb, hosts.cpumodel, hosts.os
having   nb_uids > 1
order by lastAlive_not_null, host_name;


-- Count hosts having same characteristics and same highest value of 'lastAlive'
select host_name, host_ipaddr, host_hwaddr, hosts_cputype, hosts_cpunb, hosts_cpumodel, hosts_os,
       max(last_alive) as max_last_alive, nb_uids
from   ( select   hosts.name as host_name, hosts.ipaddr as host_ipaddr, hosts.hwaddr as host_hwaddr,
         hosts.cputype as hosts_cputype, hosts.cpunb as hosts_cpunb, hosts.cpumodel as hosts_cpumodel,
         hosts.os as hosts_os, hosts.lastAlive as last_alive, count(hosts.uid) as nb_uids
         from     hosts
         where    hosts.lastAlive is not null
         group by hosts.name, hosts.ipaddr, hosts.hwaddr, hosts.cputype, hosts.cpunb, hosts.cpumodel,
                  hosts.os, hosts.lastAlive
         having   nb_uids > 1 ) as hosts_having_same_characteristics_and_last_alive
group by host_name, host_ipaddr, host_hwaddr, hosts_cputype, hosts_cpunb, hosts_cpumodel, hosts_os
order by nb_uids desc;
