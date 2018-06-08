DROP USER IF EXISTS 'grafana';

CREATE USER 'grafana'@'%' IDENTIFIED BY 'grafanap';

GRANT SELECT(cpunb,cpuspeed,totalmem,isdeleted,lastalive,uid,name,os,cputype,nbjobs,pendingjobs,runningjobs,errorjobs) ON iexec.hosts TO 'grafana'@'%';
GRANT SELECT(cpunb,cpuspeed,totalmem,isdeleted,lastalive,uid,name,os,cputype,nbjobs,pendingjobs,runningjobs,errorjobs) ON iexec.hosts_history TO 'grafana'@'%';

GRANT SELECT ON iexec.tasks TO 'grafana'@'%';
GRANT SELECT ON iexec.tasks_history TO 'grafana'@'%';

GRANT SELECT ON iexec.marketorders TO 'grafana'@'%';
GRANT SELECT ON iexec.marketorders_history TO 'grafana'@'%';

GRANT SELECT ON iexec.works TO 'grafana'@'%';
GRANT SELECT ON iexec.works_history TO 'grafana'@'%';

GRANT SELECT ON iexec.apps TO 'grafana'@'%';
GRANT SELECT ON iexec.apps_history TO 'grafana'@'%';

FLUSH PRIVILEGES;
