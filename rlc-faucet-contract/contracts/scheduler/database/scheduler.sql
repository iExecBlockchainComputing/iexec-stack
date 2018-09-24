-- MySQL dump 10.13  Distrib 5.7.22, for Linux (x86_64)
--
-- Host: localhost    Database: iexec
-- ------------------------------------------------------
-- Server version	5.7.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appTypes`
--

DROP TABLE IF EXISTS `appTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appTypes` (
  `appTypeId` tinyint(3) unsigned NOT NULL,
  `appTypeName` varchar(254) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `appTypeDescription` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`appTypeId`),
  UNIQUE KEY `appTypeName` (`appTypeName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='appTypes = Constants for "apps"."type"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appTypes`
--

LOCK TABLES `appTypes` WRITE;
/*!40000 ALTER TABLE `appTypes` DISABLE KEYS */;
INSERT INTO `appTypes` VALUES (0,'NONE','2018-05-24 12:39:46','Unknown application type.'),(1,'DEPLOYABLE','2018-05-24 12:39:46','Type for an application that must be deployed :  Its binary must be downloaded by volunteer resources.'),(2,'SHARED','2018-05-24 12:39:46','Type for an application that is shared by volunteer resources :  Its binary should not be downloaded by volunteer resources.'),(3,'VIRTUALBOX','2018-05-24 12:39:46','Type for a VirtualBox image of a virtual machine'),(4,'DOCKER','2018-05-24 12:39:46','Type for a Docker image of a container');
/*!40000 ALTER TABLE `appTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apps`
--

DROP TABLE IF EXISTS `apps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apps` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `name` varchar(254) NOT NULL COMMENT 'Secondary key. if your change length, don t forget to change AppInterface.APPNAMELENGTH',
  `appTypeId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Application type Id. See table AppTypes.',
  `type` varchar(254) NOT NULL DEFAULT 'NONE' COMMENT 'Application type :  "NONE" = Undefined; no job will run.  "DEPLOYABLE" = The worker must download the binary.  "SHARED" = Shared application : the worker will not download the binary.  "VIRTUALBOX" = Script for virtualbox shared application.  "VMWARE" = S',
  `packageTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'Optional, Id of a needed package',
  `neededpackages` varchar(254) DEFAULT NULL COMMENT 'Optional, needed packages on worker side  Since 8.0.0',
  `ownerUID` char(36) NOT NULL COMMENT 'Optionnal. user UID',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `envvars` varchar(254) DEFAULT NULL COMMENT 'Optional, env vars  Since 8.0.0',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `isService` char(5) DEFAULT 'false' COMMENT 'Optionnal. true if app is a service',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `avgExecTime` int(15) DEFAULT '0' COMMENT 'Average execution time. updated on work completion',
  `minMemory` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum memory needed in Kb',
  `minCPUSpeed` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum CPU speed need in MHz',
  `minFreeMassStorage` bigint(20) DEFAULT '0' COMMENT 'Min free amount of mass storage in Mb',
  `price` bigint(20) DEFAULT '0' COMMENT 'price since 13.1.0',
  `nbJobs` int(15) DEFAULT '0' COMMENT 'Completed jobs counter. updated on work completion',
  `pendingJobs` int(15) DEFAULT '0' COMMENT 'Pending jobs counter. updated on work submission',
  `runningJobs` int(15) DEFAULT '0' COMMENT 'Running jobs counter. updated on work request',
  `errorJobs` int(15) DEFAULT '0' COMMENT 'Error jobs counter. updated on job error',
  `webpage` varchar(254) DEFAULT NULL COMMENT 'Application web page',
  `defaultStdinURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the default STDIN, if any. this is an URI  If set, this is automatically provided to any job, except if job defines its own STDIN  Works.stdin may be set to NULLUID to force no STDIN even if this apps.stdin is defined',
  `baseDirinURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the base environments, if any. this is an URI  If set, this is automatically provided to any job  If set, this is installed **after** defaultdirin or works.dirin  To ensure those last do not override any file contained in basedirin',
  `defaultDirinURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the default environment, if any. this is an URI  If set, this is automatically provided to any job, except if job defines its own DIRIN  Works.dirin may be set to NULLUID to force no DIRIN even if this apps.dirin is defined  This is ins',
  `launchscriptshuri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, launch shell scripts  Since 8.0.0',
  `launchscriptcmduri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, launch MS command scripts  Since 8.0.0',
  `unloadscriptshuri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, unload shell scripts  Since 8.0.0',
  `unloadscriptcmduri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, unload MS command scripts  Since 8.0.0',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `linux_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ix86  binary, if any. this is an URI',
  `linux_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    amd64 binary, if any. this is an URI',
  `linux_arm64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm64 binary, if any. this is an URI',
  `linux_arm32URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm32 binary, if any. this is an URI',
  `linux_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    x86 64 binary, if any. this is an URI',
  `linux_ia64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    intel itanium binary, if any. this is an URI',
  `linux_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ppc   binary, if any. this is an URI',
  `macos_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os 10 ix86  binary, if any. this is an URI',
  `macos_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os 10 x86_64  binary, if any. this is an URI',
  `macos_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os 10 ppc   binary, if any. this is an URI',
  `win32_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    ix86  binary, if any. this is an URI',
  `win32_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    amd64 binary, if any. this is an URI',
  `win32_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32   x86_64 binary, if any. this is an URI',
  `javaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the java           binary, if any. this is an URI',
  `osf1_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     alpha binary, if any. this is an URI',
  `osf1_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     sparc binary, if any. this is an URI',
  `solaris_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  alpha binary, if any. this is an URI',
  `solaris_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  sparc binary, if any. this is an URI',
  `ldlinux_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ix86  library, if any. this is an URI',
  `ldlinux_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    amd64 library, if any. this is an URI',
  `ldlinux_arm64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm64 library, if any. this is an URI',
  `ldlinux_arm32URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm32 library, if any. this is an URI',
  `ldlinux_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    x86 64 library, if any. this is an URI',
  `ldlinux_ia64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    intel itanium library, if any. this is an URI',
  `ldlinux_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ppc   library, if any. this is an URI',
  `ldmacos_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os x ix86  library, if any. this is an URI',
  `ldmacos_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os x ix86  library, if any. this is an URI',
  `ldmacos_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os x ppc   library, if any. this is an URI',
  `ldwin32_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    ix86  library, if any. this is an URI',
  `ldwin32_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    amd64 library, if any. this is an URI',
  `ldwin32_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32   x86_64 library, if any. this is an URI',
  `ldosf1_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     alpha library, if any. this is an URI',
  `ldosf1_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     sparc library, if any. this is an URI',
  `ldsolaris_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  alpha library, if any. this is an URI',
  `ldsolaris_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  sparc library, if any. this is an URI',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `name` (`name`),
  KEY `fk_apps_appTypes` (`appTypeId`),
  KEY `fk_apps_packageTypes` (`packageTypeId`),
  KEY `fk_apps_users` (`ownerUID`),
  CONSTRAINT `fk_apps_appTypes` FOREIGN KEY (`appTypeId`) REFERENCES `appTypes` (`appTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_apps_packageTypes` FOREIGN KEY (`packageTypeId`) REFERENCES `packageTypes` (`packageTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_apps_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='applications, submitted by users inside works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apps`
--

LOCK TABLES `apps` WRITE;
/*!40000 ALTER TABLE `apps` DISABLE KEYS */;
/*!40000 ALTER TABLE `apps` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_before_insert before insert on apps
for each row
begin
  if   new.type is not null
  then set new.appTypeId =
           ( select appTypes.appTypeId
             from   appTypes
             where  appTypes.appTypeName = new.type );
  end if;

  if   new.neededpackages is not null
  then set new.packageTypeId =
           ( select packageTypes.packageTypeId
             from   packageTypes
             where  packageTypes.packageTypeName = new.neededpackages );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_after_insert after insert on apps
for each row
begin
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'IX86',   new.linux_ix86URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'AMD64',  new.linux_amd64URI  );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'X86_64', new.linux_x86_64URI );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'IA64',   new.linux_ia64URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'PPC',    new.linux_ppcURI    );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'MACOSX', 'IX86',   new.macos_ix86URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'MACOSX', 'X86_64', new.macos_x86_64URI );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'MACOSX', 'PPC',    new.macos_ppcURI    );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'WIN32',  'IX86',   new.win32_ix86URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'WIN32',  'AMD64',  new.win32_amd64URI  );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'WIN32',  'X86_64', new.win32_x86_64URI );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'JAVA',   'ALL',    new.javaURI         );

  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'IX86',   new.ldlinux_ix86URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'AMD64',  new.ldlinux_amd64URI  );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'X86_64', new.ldlinux_x86_64URI );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'IA64',   new.ldlinux_ia64URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'PPC',    new.ldlinux_ppcURI    );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'MACOSX', 'IX86',   new.ldmacos_ix86URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'MACOSX', 'X86_64', new.ldmacos_x86_64URI );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'MACOSX', 'PPC',    new.ldmacos_ppcURI    );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'WIN32',  'IX86',   new.ldwin32_ix86URI   );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'WIN32',  'AMD64',  new.ldwin32_amd64URI  );
  call  proc_insert_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'WIN32',  'X86_64', new.ldwin32_x86_64URI );
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_before_update before update on apps
for each row
begin

  if   (new.type is null) and (old.type is not null)
  then set new.appTypeId = null;
  end if;

  if   (  new.type is not null  )  and
       ( (old.type is     null) or (old.type <> new.type) )
  then set new.appTypeId =
           ( select appTypes.appTypeId
             from   appTypes
             where  appTypes.appTypeName = new.type );
  end if;

  if   (new.neededpackages is null) and (old.neededpackages is not null)
  then set new.packageTypeId = null;
  end if;

  if   (  new.neededpackages is not null  )  and
       ( (old.neededpackages is     null) or (old.neededpackages <> new.neededpackages) )
  then set new.packageTypeId =
           ( select packageTypes.packageTypeId
             from   packageTypes
             where  packageTypes.packageTypeName = new.neededpackages );
  end if;

  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'IX86',   old.linux_ix86URI,     new.linux_ix86URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'AMD64',  old.linux_amd64URI,    new.linux_amd64URI  );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'X86_64', old.linux_x86_64URI,   new.linux_x86_64URI );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'IA64',   old.linux_ia64URI,     new.linux_ia64URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'PPC',    old.linux_ppcURI,      new.linux_ppcURI    );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'MACOSX', 'IX86',   old.macos_ix86URI,     new.macos_ix86URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'MACOSX', 'X86_64', old.macos_x86_64URI,   new.macos_x86_64URI );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'MACOSX', 'PPC',    old.macos_ppcURI,      new.macos_ppcURI    );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'WIN32',  'IX86',   old.win32_ix86URI,     new.win32_ix86URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'WIN32',  'AMD64',  old.win32_amd64URI,    new.win32_amd64URI  );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'WIN32',  'X86_64', old.win32_x86_64URI,   new.win32_x86_64URI );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'JAVA',   'ALL',    old.javaURI,           new.javaURI         );

  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'IX86',   old.ldlinux_ix86URI,   new.ldlinux_ix86URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'AMD64',  old.ldlinux_amd64URI,  new.ldlinux_amd64URI  );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'X86_64', old.ldlinux_x86_64URI, new.ldlinux_x86_64URI );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'IA64',   old.ldlinux_ia64URI,   new.ldlinux_ia64URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'PPC',    old.ldlinux_ppcURI,    new.ldlinux_ppcURI    );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'MACOSX', 'IX86',   old.ldmacos_ix86URI,   new.ldmacos_ix86URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'MACOSX', 'X86_64', old.ldmacos_x86_64URI, new.ldmacos_x86_64URI );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'MACOSX', 'PPC',    old.ldmacos_ppcURI,    new.ldmacos_ppcURI    );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'WIN32',  'IX86',   old.ldwin32_ix86URI,   new.ldwin32_ix86URI   );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'WIN32',  'AMD64',  old.ldwin32_amd64URI,  new.ldwin32_amd64URI  );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'WIN32',  'X86_64', old.ldwin32_x86_64URI, new.ldwin32_x86_64URI );

end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_after_update after update on apps
for each row
begin
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'IX86',   old.linux_ix86URI,     new.linux_ix86URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'AMD64',  old.linux_amd64URI,    new.linux_amd64URI  );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'X86_64', old.linux_x86_64URI,   new.linux_x86_64URI );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'IA64',   old.linux_ia64URI,     new.linux_ia64URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'LINUX',  'PPC',    old.linux_ppcURI,      new.linux_ppcURI    );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'MACOSX', 'IX86',   old.macos_ix86URI,     new.macos_ix86URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'MACOSX', 'X86_64', old.macos_x86_64URI,   new.macos_x86_64URI );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'MACOSX', 'PPC',    old.macos_ppcURI,      new.macos_ppcURI    );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'WIN32',  'IX86',   old.win32_ix86URI,     new.win32_ix86URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'WIN32',  'AMD64',  old.win32_amd64URI,    new.win32_amd64URI  );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'WIN32',  'X86_64', old.win32_x86_64URI,   new.win32_x86_64URI );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'BINARY',  'JAVA',   'ALL',    old.javaURI,           new.javaURI         );

  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'IX86',   old.ldlinux_ix86URI,   new.ldlinux_ix86URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'AMD64',  old.ldlinux_amd64URI,  new.ldlinux_amd64URI  );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'X86_64', old.ldlinux_x86_64URI, new.ldlinux_x86_64URI );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'IA64',   old.ldlinux_ia64URI,   new.ldlinux_ia64URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'LINUX',  'PPC',    old.ldlinux_ppcURI,    new.ldlinux_ppcURI    );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'MACOSX', 'IX86',   old.ldmacos_ix86URI,   new.ldmacos_ix86URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'MACOSX', 'X86_64', old.ldmacos_x86_64URI, new.ldmacos_x86_64URI );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'MACOSX', 'PPC',    old.ldmacos_ppcURI,    new.ldmacos_ppcURI    );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'WIN32',  'IX86',   old.ldwin32_ix86URI,   new.ldwin32_ix86URI   );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'WIN32',  'AMD64',  old.ldwin32_amd64URI,  new.ldwin32_amd64URI  );
  call  proc_update_app_os_cpu_uri_in_executables ( new.uid, 'LIBRARY', 'WIN32',  'X86_64', old.ldwin32_x86_64URI, new.ldwin32_x86_64URI );
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_before_delete before delete on apps
for each row
begin
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'IX86',   old.linux_ix86URI,     null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'AMD64',  old.linux_amd64URI,    null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'X86_64', old.linux_x86_64URI,   null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'IA64',   old.linux_ia64URI,     null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'LINUX',  'PPC',    old.linux_ppcURI,      null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'MACOSX', 'IX86',   old.macos_ix86URI,     null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'MACOSX', 'X86_64', old.macos_x86_64URI,   null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'MACOSX', 'PPC',    old.macos_ppcURI,      null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'WIN32',  'IX86',   old.win32_ix86URI,     null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'WIN32',  'AMD64',  old.win32_amd64URI,    null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'WIN32',  'X86_64', old.win32_x86_64URI,   null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'BINARY',  'JAVA',   'ALL',    old.javaURI,           null );

  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'IX86',   old.ldlinux_ix86URI,   null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'AMD64',  old.ldlinux_amd64URI,  null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'X86_64', old.ldlinux_x86_64URI, null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'IA64',   old.ldlinux_ia64URI,   null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'LINUX',  'PPC',    old.ldlinux_ppcURI,    null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'MACOSX', 'IX86',   old.ldmacos_ix86URI,   null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'MACOSX', 'X86_64', old.ldmacos_x86_64URI, null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'MACOSX', 'PPC',    old.ldmacos_ppcURI,    null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'WIN32',  'IX86',   old.ldwin32_ix86URI,   null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'WIN32',  'AMD64',  old.ldwin32_amd64URI,  null );
  call  proc_delete_app_os_cpu_uri_in_executables ( old.uid, 'LIBRARY', 'WIN32',  'X86_64', old.ldwin32_x86_64URI, null );
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `apps_history`
--

DROP TABLE IF EXISTS `apps_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apps_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `name` varchar(254) NOT NULL COMMENT 'Secondary key. if your change length, don t forget to change AppInterface.APPNAMELENGTH',
  `appTypeId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Application type Id. See table AppTypes.',
  `type` varchar(254) NOT NULL DEFAULT 'NONE' COMMENT 'Application type :  "NONE" = Undefined; no job will run.  "DEPLOYABLE" = The worker must download the binary.  "SHARED" = Shared application : the worker will not download the binary.  "VIRTUALBOX" = Script for virtualbox shared application.  "VMWARE" = S',
  `packageTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'Optional, Id of a needed package',
  `neededpackages` varchar(254) DEFAULT NULL COMMENT 'Optional, needed packages on worker side  Since 8.0.0',
  `ownerUID` char(36) NOT NULL COMMENT 'Optionnal. user UID',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `envvars` varchar(254) DEFAULT NULL COMMENT 'Optional, env vars  Since 8.0.0',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `isService` char(5) DEFAULT 'false' COMMENT 'Optionnal. true if app is a service',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `avgExecTime` int(15) DEFAULT '0' COMMENT 'Average execution time. updated on work completion',
  `minMemory` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum memory needed in Kb',
  `minCPUSpeed` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum CPU speed need in MHz',
  `minFreeMassStorage` bigint(20) DEFAULT '0' COMMENT 'Min free amount of mass storage in Mb',
  `price` bigint(20) DEFAULT '0' COMMENT 'price since 13.1.0',
  `nbJobs` int(15) DEFAULT '0' COMMENT 'Completed jobs counter. updated on work completion',
  `pendingJobs` int(15) DEFAULT '0' COMMENT 'Pending jobs counter. updated on work submission',
  `runningJobs` int(15) DEFAULT '0' COMMENT 'Running jobs counter. updated on work request',
  `errorJobs` int(15) DEFAULT '0' COMMENT 'Error jobs counter. updated on job error',
  `webpage` varchar(254) DEFAULT NULL COMMENT 'Application web page',
  `defaultStdinURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the default STDIN, if any. this is an URI  If set, this is automatically provided to any job, except if job defines its own STDIN  Works.stdin may be set to NULLUID to force no STDIN even if this apps.stdin is defined',
  `baseDirinURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the base environments, if any. this is an URI  If set, this is automatically provided to any job  If set, this is installed **after** defaultdirin or works.dirin  To ensure those last do not override any file contained in basedirin',
  `defaultDirinURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the default environment, if any. this is an URI  If set, this is automatically provided to any job, except if job defines its own DIRIN  Works.dirin may be set to NULLUID to force no DIRIN even if this apps.dirin is defined  This is ins',
  `launchscriptshuri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, launch shell scripts  Since 8.0.0',
  `launchscriptcmduri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, launch MS command scripts  Since 8.0.0',
  `unloadscriptshuri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, unload shell scripts  Since 8.0.0',
  `unloadscriptcmduri` varchar(254) DEFAULT NULL COMMENT 'Optionnal, unload MS command scripts  Since 8.0.0',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `linux_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ix86  binary, if any. this is an URI',
  `linux_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    amd64 binary, if any. this is an URI',
  `linux_arm64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm64 binary, if any. this is an URI',
  `linux_arm32URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm32 binary, if any. this is an URI',
  `linux_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    x86 64 binary, if any. this is an URI',
  `linux_ia64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    intel itanium binary, if any. this is an URI',
  `linux_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ppc   binary, if any. this is an URI',
  `macos_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os 10 ix86  binary, if any. this is an URI',
  `macos_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os 10 x86_64  binary, if any. this is an URI',
  `macos_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os 10 ppc   binary, if any. this is an URI',
  `win32_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    ix86  binary, if any. this is an URI',
  `win32_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    amd64 binary, if any. this is an URI',
  `win32_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32   x86_64 binary, if any. this is an URI',
  `javaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the java           binary, if any. this is an URI',
  `osf1_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     alpha binary, if any. this is an URI',
  `osf1_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     sparc binary, if any. this is an URI',
  `solaris_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  alpha binary, if any. this is an URI',
  `solaris_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  sparc binary, if any. this is an URI',
  `ldlinux_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ix86  library, if any. this is an URI',
  `ldlinux_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    amd64 library, if any. this is an URI',
  `ldlinux_arm64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm64 library, if any. this is an URI',
  `ldlinux_arm32URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    arm32 library, if any. this is an URI',
  `ldlinux_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    x86 64 library, if any. this is an URI',
  `ldlinux_ia64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    intel itanium library, if any. this is an URI',
  `ldlinux_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the linux    ppc   library, if any. this is an URI',
  `ldmacos_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os x ix86  library, if any. this is an URI',
  `ldmacos_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os x ix86  library, if any. this is an URI',
  `ldmacos_ppcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the mac os x ppc   library, if any. this is an URI',
  `ldwin32_ix86URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    ix86  library, if any. this is an URI',
  `ldwin32_amd64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32    amd64 library, if any. this is an URI',
  `ldwin32_x86_64URI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the win32   x86_64 library, if any. this is an URI',
  `ldosf1_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     alpha library, if any. this is an URI',
  `ldosf1_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the osf1     sparc library, if any. this is an URI',
  `ldsolaris_alphaURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  alpha library, if any. this is an URI',
  `ldsolaris_sparcURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. this is the solaris  sparc library, if any. this is an URI',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `name` (`name`),
  KEY `appTypeId` (`appTypeId`),
  KEY `packageTypeId` (`packageTypeId`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='applications, submitted by users inside works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apps_history`
--

LOCK TABLES `apps_history` WRITE;
/*!40000 ALTER TABLE `apps_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `apps_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_history_before_insert before insert on apps_history
for each row
begin
  if   new.type is not null
  then set new.appTypeId =
           ( select appTypes.appTypeId
             from   appTypes
             where  appTypes.appTypeName = new.type );
  end if;

  if   new.neededpackages is not null
  then set new.packageTypeId =
           ( select packageTypes.packageTypeId
             from   packageTypes
             where  packageTypes.packageTypeName = new.neededpackages );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_history_after_insert after insert on apps_history
for each row
begin
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'IX86',   new.linux_ix86URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'AMD64',  new.linux_amd64URI  );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'X86_64', new.linux_x86_64URI );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'IA64',   new.linux_ia64URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'PPC',    new.linux_ppcURI    );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'MACOSX', 'IX86',   new.macos_ix86URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'MACOSX', 'X86_64', new.macos_x86_64URI );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'MACOSX', 'PPC',    new.macos_ppcURI    );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'WIN32',  'IX86',   new.win32_ix86URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'WIN32',  'AMD64',  new.win32_amd64URI  );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'WIN32',  'X86_64', new.win32_x86_64URI );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'JAVA',   'ALL',    new.javaURI         );

  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'IX86',   new.ldlinux_ix86URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'AMD64',  new.ldlinux_amd64URI  );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'X86_64', new.ldlinux_x86_64URI );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'IA64',   new.ldlinux_ia64URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'PPC',    new.ldlinux_ppcURI    );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'MACOSX', 'IX86',   new.ldmacos_ix86URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'MACOSX', 'X86_64', new.ldmacos_x86_64URI );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'MACOSX', 'PPC',    new.ldmacos_ppcURI    );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'WIN32',  'IX86',   new.ldwin32_ix86URI   );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'WIN32',  'AMD64',  new.ldwin32_amd64URI  );
  call  proc_insert_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'WIN32',  'X86_64', new.ldwin32_x86_64URI );
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_history_before_update before update on apps_history
for each row
begin

  if   (new.type is null) and (old.type is not null)
  then set new.appTypeId = null;
  end if;

  if   (  new.type is not null  )  and
       ( (old.type is     null) or (old.type <> new.type) )
  then set new.appTypeId =
           ( select appTypes.appTypeId
             from   appTypes
             where  appTypes.appTypeName = new.type );
  end if;

  if   (new.neededpackages is null) and (old.neededpackages is not null)
  then set new.packageTypeId = null;
  end if;

  if   (  new.neededpackages is not null  )  and
       ( (old.neededpackages is     null) or (old.neededpackages <> new.neededpackages) )
  then set new.packageTypeId =
           ( select packageTypes.packageTypeId
             from   packageTypes
             where  packageTypes.packageTypeName = new.neededpackages );
  end if;

  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'IX86',   old.linux_ix86URI,     new.linux_ix86URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'AMD64',  old.linux_amd64URI,    new.linux_amd64URI  );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'X86_64', old.linux_x86_64URI,   new.linux_x86_64URI );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'IA64',   old.linux_ia64URI,     new.linux_ia64URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'PPC',    old.linux_ppcURI,      new.linux_ppcURI    );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'MACOSX', 'IX86',   old.macos_ix86URI,     new.macos_ix86URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'MACOSX', 'X86_64', old.macos_x86_64URI,   new.macos_x86_64URI );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'MACOSX', 'PPC',    old.macos_ppcURI,      new.macos_ppcURI    );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'WIN32',  'IX86',   old.win32_ix86URI,     new.win32_ix86URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'WIN32',  'AMD64',  old.win32_amd64URI,    new.win32_amd64URI  );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'WIN32',  'X86_64', old.win32_x86_64URI,   new.win32_x86_64URI );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'JAVA',   'ALL',    old.javaURI,           new.javaURI         );

  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'IX86',   old.ldlinux_ix86URI,   new.ldlinux_ix86URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'AMD64',  old.ldlinux_amd64URI,  new.ldlinux_amd64URI  );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'X86_64', old.ldlinux_x86_64URI, new.ldlinux_x86_64URI );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'IA64',   old.ldlinux_ia64URI,   new.ldlinux_ia64URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'PPC',    old.ldlinux_ppcURI,    new.ldlinux_ppcURI    );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'MACOSX', 'IX86',   old.ldmacos_ix86URI,   new.ldmacos_ix86URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'MACOSX', 'X86_64', old.ldmacos_x86_64URI, new.ldmacos_x86_64URI );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'MACOSX', 'PPC',    old.ldmacos_ppcURI,    new.ldmacos_ppcURI    );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'WIN32',  'IX86',   old.ldwin32_ix86URI,   new.ldwin32_ix86URI   );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'WIN32',  'AMD64',  old.ldwin32_amd64URI,  new.ldwin32_amd64URI  );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'WIN32',  'X86_64', old.ldwin32_x86_64URI, new.ldwin32_x86_64URI );

end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_history_after_update after update on apps_history
for each row
begin
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'IX86',   old.linux_ix86URI,     new.linux_ix86URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'AMD64',  old.linux_amd64URI,    new.linux_amd64URI  );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'X86_64', old.linux_x86_64URI,   new.linux_x86_64URI );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'IA64',   old.linux_ia64URI,     new.linux_ia64URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'LINUX',  'PPC',    old.linux_ppcURI,      new.linux_ppcURI    );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'MACOSX', 'IX86',   old.macos_ix86URI,     new.macos_ix86URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'MACOSX', 'X86_64', old.macos_x86_64URI,   new.macos_x86_64URI );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'MACOSX', 'PPC',    old.macos_ppcURI,      new.macos_ppcURI    );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'WIN32',  'IX86',   old.win32_ix86URI,     new.win32_ix86URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'WIN32',  'AMD64',  old.win32_amd64URI,    new.win32_amd64URI  );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'WIN32',  'X86_64', old.win32_x86_64URI,   new.win32_x86_64URI );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'BINARY',  'JAVA',   'ALL',    old.javaURI,           new.javaURI         );

  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'IX86',   old.ldlinux_ix86URI,   new.ldlinux_ix86URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'AMD64',  old.ldlinux_amd64URI,  new.ldlinux_amd64URI  );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'X86_64', old.ldlinux_x86_64URI, new.ldlinux_x86_64URI );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'IA64',   old.ldlinux_ia64URI,   new.ldlinux_ia64URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'LINUX',  'PPC',    old.ldlinux_ppcURI,    new.ldlinux_ppcURI    );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'MACOSX', 'IX86',   old.ldmacos_ix86URI,   new.ldmacos_ix86URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'MACOSX', 'X86_64', old.ldmacos_x86_64URI, new.ldmacos_x86_64URI );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'MACOSX', 'PPC',    old.ldmacos_ppcURI,    new.ldmacos_ppcURI    );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'WIN32',  'IX86',   old.ldwin32_ix86URI,   new.ldwin32_ix86URI   );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'WIN32',  'AMD64',  old.ldwin32_amd64URI,  new.ldwin32_amd64URI  );
  call  proc_update_app_hist_os_cpu_uri_in_executables_hist ( new.uid, 'LIBRARY', 'WIN32',  'X86_64', old.ldwin32_x86_64URI, new.ldwin32_x86_64URI );
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_apps_history_before_delete before delete on apps_history
for each row
begin
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'IX86',   old.linux_ix86URI,     null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'AMD64',  old.linux_amd64URI,    null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'X86_64', old.linux_x86_64URI,   null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'IA64',   old.linux_ia64URI,     null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'LINUX',  'PPC',    old.linux_ppcURI,      null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'MACOSX', 'IX86',   old.macos_ix86URI,     null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'MACOSX', 'X86_64', old.macos_x86_64URI,   null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'MACOSX', 'PPC',    old.macos_ppcURI,      null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'WIN32',  'IX86',   old.win32_ix86URI,     null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'WIN32',  'AMD64',  old.win32_amd64URI,    null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'WIN32',  'X86_64', old.win32_x86_64URI,   null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'BINARY',  'JAVA',   'ALL',    old.javaURI,           null );

  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'IX86',   old.ldlinux_ix86URI,   null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'AMD64',  old.ldlinux_amd64URI,  null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'X86_64', old.ldlinux_x86_64URI, null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'IA64',   old.ldlinux_ia64URI,   null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'LINUX',  'PPC',    old.ldlinux_ppcURI,    null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'MACOSX', 'IX86',   old.ldmacos_ix86URI,   null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'MACOSX', 'X86_64', old.ldmacos_x86_64URI, null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'MACOSX', 'PPC',    old.ldmacos_ppcURI,    null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'WIN32',  'IX86',   old.ldwin32_ix86URI,   null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'WIN32',  'AMD64',  old.ldwin32_amd64URI,  null );
  call  proc_delete_app_hist_os_cpu_uri_in_executables_hist ( old.uid, 'LIBRARY', 'WIN32',  'X86_64', old.ldwin32_x86_64URI, null );
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `categoryId` bigint(20) NOT NULL COMMENT 'catID referenced by smart contracts',
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `accessRights` int(4) DEFAULT '1877' COMMENT 'Please note that a category is always public',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `name` varchar(254) NOT NULL DEFAULT 'NONE' COMMENT 'Category name is a free text',
  `maxWallClockTime` int(10) NOT NULL DEFAULT '300' COMMENT 'Max amount of seconds a job can be computed; default 5mn',
  `maxFreeMassStorage` bigint(20) NOT NULL DEFAULT '5368709120' COMMENT 'Max mass storage usage in bytes; default 5Gb',
  `maxFileSize` bigint(20) NOT NULL DEFAULT '104857600' COMMENT 'Max file length in bytes; default 100Mb',
  `maxMemory` bigint(20) NOT NULL DEFAULT '536870912' COMMENT 'Max RAM usage in bytes; default 512Mb',
  `maxCpuSpeed` float NOT NULL DEFAULT '0.5' COMMENT 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `categoryId` (`categoryId`),
  KEY `name` (`name`),
  KEY `idx_categoryId` (`categoryId`),
  KEY `ownerUID` (`ownerUID`),
  CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='categories = categories defining resources usage limits';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'7c0df1d3-53a4-481e-b2c5-e20294fa2579','98ec298c-406d-4379-9616-5652f2d05e79',1877,NULL,'2018-05-24 12:41:20','1',30,32212254720,524288000,1073741824,1),(5,'7f0f227f-2b4a-45b9-8feb-1433756f0048','98ec298c-406d-4379-9616-5652f2d05e79',1877,NULL,'2018-05-24 12:41:20','5',3600,32212254720,524288000,1073741824,1),(3,'8eaca495-4bf7-42e0-84c9-266bc7f31627','98ec298c-406d-4379-9616-5652f2d05e79',1877,NULL,'2018-05-24 12:41:20','3',900,32212254720,524288000,1073741824,1),(2,'b9d9a404-b6f0-4ae5-8cc9-f71f4c566597','98ec298c-406d-4379-9616-5652f2d05e79',1877,NULL,'2018-05-24 12:41:20','2',120,32212254720,524288000,1073741824,1),(4,'c472f2fb-0ac3-4bfe-93bc-34f92e129b8d','98ec298c-406d-4379-9616-5652f2d05e79',1877,NULL,'2018-05-24 12:41:20','4',1800,32212254720,524288000,1073741824,1);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories_history`
--

DROP TABLE IF EXISTS `categories_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories_history` (
  `categoryId` bigint(20) NOT NULL COMMENT 'catID referenced by smart contracts',
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `accessRights` int(4) DEFAULT '1877' COMMENT 'Please note that a category is always public',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `name` varchar(254) NOT NULL DEFAULT 'NONE' COMMENT 'Category name is a free text',
  `maxWallClockTime` int(10) NOT NULL DEFAULT '300' COMMENT 'Max amount of seconds a job can be computed; default 5mn',
  `maxFreeMassStorage` bigint(20) NOT NULL DEFAULT '5368709120' COMMENT 'Max mass storage usage in bytes; default 5Gb',
  `maxFileSize` bigint(20) NOT NULL DEFAULT '104857600' COMMENT 'Max file length in bytes; default 100Mb',
  `maxMemory` bigint(20) NOT NULL DEFAULT '536870912' COMMENT 'Max RAM usage in bytes; default 512Mb',
  `maxCpuSpeed` float NOT NULL DEFAULT '0.5' COMMENT 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `categoryId` (`categoryId`),
  KEY `name` (`name`),
  KEY `idx_categoryId` (`categoryId`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='categories = categories defining resources usage limits';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories_history`
--

LOCK TABLES `categories_history` WRITE;
/*!40000 ALTER TABLE `categories_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `categories_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cpuTypes`
--

DROP TABLE IF EXISTS `cpuTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cpuTypes` (
  `cpuTypeId` tinyint(3) unsigned NOT NULL,
  `cpuTypeName` char(7) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cpuTypeDescription` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`cpuTypeId`),
  UNIQUE KEY `cpuTypeName` (`cpuTypeName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='cpuTypes = Constants for *."cpuType"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cpuTypes`
--

LOCK TABLES `cpuTypes` WRITE;
/*!40000 ALTER TABLE `cpuTypes` DISABLE KEYS */;
INSERT INTO `cpuTypes` VALUES (0,'NONE','2018-05-24 12:39:46','Unknown'),(1,'ALL','2018-05-24 12:39:46','Architecture independant'),(2,'AMD64','2018-05-24 12:39:46','AMD - 64 bits'),(3,'ARM32','2018-05-24 12:39:46','Advanced RISC Machines Cortex A7'),(4,'IA64','2018-05-24 12:39:46','Intel Itanium - 64 bits'),(5,'IX86','2018-05-24 12:39:46','Intel x86 - 32 bits'),(6,'PPC','2018-05-24 12:39:46','Power PC'),(7,'X86_64','2018-05-24 12:39:46','Intel x86 - 64 bits'),(8,'ARM64','2018-05-24 12:39:46','Advanced RISC Machines v8');
/*!40000 ALTER TABLE `cpuTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataTypes`
--

DROP TABLE IF EXISTS `dataTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataTypes` (
  `dataTypeId` tinyint(3) unsigned NOT NULL,
  `dataTypeName` varchar(254) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dataTypeDescription` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`dataTypeId`),
  UNIQUE KEY `dataTypeName` (`dataTypeName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='dataTypes = Constants for "datas"."type"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataTypes`
--

LOCK TABLES `dataTypes` WRITE;
/*!40000 ALTER TABLE `dataTypes` DISABLE KEYS */;
INSERT INTO `dataTypes` VALUES (0,'NONE','2018-05-24 12:39:46','Unknown data type'),(1,'BINARY','2018-05-24 12:39:46','Binary data'),(2,'LIBRARY','2018-05-24 12:39:46','Library data'),(3,'JAVA','2018-05-24 12:39:46','Java byte code'),(4,'TEXT','2018-05-24 12:39:46','Text data'),(5,'ZIP','2018-05-24 12:39:46','Compressed data'),(6,'X509','2018-05-24 12:39:46','X509 certificate'),(7,'URIPASSTHROUGH','2018-05-24 12:39:46','Text file containing a list of uri, one per line'),(8,'UDPPACKET','2018-05-24 12:39:46','Data that should be send using udp protocol'),(9,'STREAM','2018-05-24 12:39:46','Data that should be send using tcp protocol'),(10,'ISO','2018-05-24 12:39:46','Disk image'),(11,'VMDK','2018-05-24 12:39:46','Virtual machine disk'),(12,'VDI','2018-05-24 12:39:46','Virtual disk image'),(13,'SH','2018-05-24 12:39:46','SH script'),(14,'BAT','2018-05-24 12:39:46','CMD script');
/*!40000 ALTER TABLE `dataTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datas`
--

DROP TABLE IF EXISTS `datas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datas` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `workUID` char(36) DEFAULT NULL COMMENT 'This is the reference work for data driven scheduling. Since 10.0.0',
  `package` varchar(254) DEFAULT NULL COMMENT 'Optional, needed packages on worker side. Since 10.0.0',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Status (deprecated)',
  `dataTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'DataType Id. See common/DataType.java',
  `type` varchar(254) DEFAULT NULL COMMENT 'Data type (deprecated)',
  `osId` tinyint(3) unsigned DEFAULT NULL COMMENT 'OS id',
  `os` char(7) DEFAULT NULL COMMENT 'Optionnal. mainly necessary if data is an app binary',
  `osVersion` varchar(36) DEFAULT NULL COMMENT 'Optionnal. mainly necessary if data is an app binary',
  `cpuTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'CPU type id',
  `cpu` char(7) DEFAULT NULL COMMENT 'Optionnal. mainly necessary if data is an app binary',
  `ownerUID` char(36) NOT NULL COMMENT 'May be {user, app, work} UID',
  `name` varchar(254) DEFAULT NULL COMMENT 'Symbolic file name (i.e. alias name)',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `uri` varchar(254) DEFAULT NULL COMMENT 'This is the URI of the content',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `links` int(4) DEFAULT NULL COMMENT 'How many times it is used. can be deleted if 0',
  `accessDate` datetime DEFAULT NULL COMMENT 'Last access date',
  `insertionDate` datetime DEFAULT NULL COMMENT 'Creation date',
  `shasum` varchar(254) DEFAULT NULL COMMENT 'Data sha sum',
  `size` bigint(20) DEFAULT NULL COMMENT 'Effective file size',
  `sendToClient` char(5) DEFAULT 'false' COMMENT 'Optionnal. used by replication',
  `replicated` char(5) DEFAULT 'false' COMMENT 'Optionnal. used by replication',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if row has been deleted ',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `fk_datas_statuses` (`statusId`),
  KEY `fk_datas_types` (`dataTypeId`),
  KEY `fk_datas_oses` (`osId`),
  KEY `fk_datas_cpuTypes` (`cpuTypeId`),
  KEY `fk_datas_users` (`ownerUID`),
  CONSTRAINT `fk_datas_cpuTypes` FOREIGN KEY (`cpuTypeId`) REFERENCES `cpuTypes` (`cpuTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_datas_oses` FOREIGN KEY (`osId`) REFERENCES `oses` (`osId`) ON DELETE CASCADE,
  CONSTRAINT `fk_datas_statuses` FOREIGN KEY (`statusId`) REFERENCES `statuses` (`statusId`) ON DELETE CASCADE,
  CONSTRAINT `fk_datas_types` FOREIGN KEY (`dataTypeId`) REFERENCES `dataTypes` (`dataTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_datas_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='datas = Files (binaries, input files, results)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datas`
--

LOCK TABLES `datas` WRITE;
/*!40000 ALTER TABLE `datas` DISABLE KEYS */;
/*!40000 ALTER TABLE `datas` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_datas_insert_status_type_os_cpu before insert on datas
for each row
begin
  if   new.status is not null
  then set new.statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
  end if;

  if   new.type is not null
  then set new.dataTypeId =
           ( select dataTypes.dataTypeId
             from   dataTypes
             where  dataTypes.dataTypeName = new.type );
  end if;

  if   new.os is not null
  then set new.osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
  end if;

  if   new.cpu is not null
  then set new.cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cpu );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_datas_update_status_type_os_cpu before update on datas
for each row
begin
  if   (new.status is null) and (old.status is not null)
  then set new.statusId = null;
  end if;

  if   (  new.status is not null  )  and
       ( (old.status is     null) or (old.status <> new.status) )
  then set new.statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
  end if;

  if   (new.type is null) and (old.type is not null)
  then set new.dataTypeId = null;
  end if;

  if   (  new.type is not null  )  and
       ( (old.type is     null) or (old.type <> new.type) )
  then set new.dataTypeId =
           ( select dataTypes.dataTypeId
             from   dataTypes
             where  dataTypes.dataTypeName = new.type );
  end if;

  if   (new.os is null) and (old.os is not null)
  then set new.osId = null;
  end if;

  if   (  new.os is not null  )  and
       ( (old.os is     null) or (old.os <> new.os) )
  then set new.osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
  end if;

  if   (new.cpu is null) and (old.cpu is not null)
  then set new.cpuTypeId = null;
  end if;

  if   (  new.cpu is not null  )  and
       ( (old.cpu is     null) or (old.cpu <> new.cpu) )
  then set new.cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cpu );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `datas_history`
--

DROP TABLE IF EXISTS `datas_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datas_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `workUID` char(36) DEFAULT NULL COMMENT 'This is the reference work for data driven scheduling. Since 10.0.0',
  `package` varchar(254) DEFAULT NULL COMMENT 'Optional, needed packages on worker side. Since 10.0.0',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Status (deprecated)',
  `dataTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'DataType Id. See common/DataType.java',
  `type` varchar(254) DEFAULT NULL COMMENT 'Data type (deprecated)',
  `osId` tinyint(3) unsigned DEFAULT NULL COMMENT 'OS id',
  `os` char(7) DEFAULT NULL COMMENT 'Optionnal. mainly necessary if data is an app binary',
  `osVersion` varchar(36) DEFAULT NULL COMMENT 'Optionnal. mainly necessary if data is an app binary',
  `cpuTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'CPU type id',
  `cpu` char(7) DEFAULT NULL COMMENT 'Optionnal. mainly necessary if data is an app binary',
  `ownerUID` char(36) NOT NULL COMMENT 'May be {user, app, work} UID',
  `name` varchar(254) DEFAULT NULL COMMENT 'Symbolic file name (i.e. alias name)',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `uri` varchar(254) DEFAULT NULL COMMENT 'This is the URI of the content',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `links` int(4) DEFAULT NULL COMMENT 'How many times it is used. can be deleted if 0',
  `accessDate` datetime DEFAULT NULL COMMENT 'Last access date',
  `insertionDate` datetime DEFAULT NULL COMMENT 'Creation date',
  `shasum` varchar(254) DEFAULT NULL COMMENT 'Data sha sum',
  `size` bigint(20) DEFAULT NULL COMMENT 'Effective file size',
  `sendToClient` char(5) DEFAULT 'false' COMMENT 'Optionnal. used by replication',
  `replicated` char(5) DEFAULT 'false' COMMENT 'Optionnal. used by replication',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if row has been deleted ',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `statusId` (`statusId`),
  KEY `dataTypeId` (`dataTypeId`),
  KEY `osId` (`osId`),
  KEY `cpuTypeId` (`cpuTypeId`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='datas = Files (binaries, input files, results)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datas_history`
--

LOCK TABLES `datas_history` WRITE;
/*!40000 ALTER TABLE `datas_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `datas_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_datas_history_insert_status_type_os_cpu before insert on datas_history
for each row
begin
  if   new.status is not null
  then
    set @statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
    if (select @statusId) is not null
    then set new.statusId = @statusId;
    end if;
  end if;

  if   new.type is not null
  then
    set @dataTypeId =
           ( select dataTypes.dataTypeId
             from   dataTypes
             where  dataTypes.dataTypeName = new.type );
    if (select @dataTypeId) is not null
    then set new.dataTypeId = @dataTypeId;
    end if;
  end if;

  if   new.os is not null
  then
    set @osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
    if (select @osId) is not null
    then set new.osId = @osId;
    end if;
  end if;

  if   new.cpu is not null
  then
    set @cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cpu );
    if (select @cpuTypeId) is not null
    then set new.cpuTypeId = @cpuTypeId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_datas_history_update_status_type_os_cpu before update on datas_history
for each row
begin
  if   (new.status is null) and (old.status is not null)
  then set new.statusId = null;
  end if;

  if   (  new.status is not null  )  and
       ( (old.status is     null) or (old.status <> new.status) )
  then
    set @statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
    if (select @statusId) is not null
    then set new.statusId = @statusId;
    end if;
  end if;

  if   (new.type is null) and (old.type is not null)
  then set new.dataTypeId = null;
  end if;

  if   (  new.type is not null  )  and
       ( (old.type is     null) or (old.type <> new.type) )
  then
    set @dataTypeId =
           ( select dataTypes.dataTypeId
             from   dataTypes
             where  dataTypes.dataTypeName = new.type );
    if (select @dataTypeId) is not null
    then set new.dataTypeId = @dataTypeId;
    end if;
  end if;

  if   (new.os is null) and (old.os is not null)
  then set new.osId = null;
  end if;

  if   (  new.os is not null  )  and
       ( (old.os is     null) or (old.os <> new.os) )
  then
    set @osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
    if (select @osId) is not null
    then set new.osId = @osId;
    end if;
  end if;

  if   (  new.cpu is not null  )  and
       ( (old.cpu is     null) or (old.cpu <> new.cpu) )
  then
    set @cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cpu );
    if (select @cpuTypeId) is not null
    then set new.cpuTypeId = @cpuTypeId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `executables`
--

DROP TABLE IF EXISTS `executables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `executables` (
  `executableId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `appUID` char(36) NOT NULL,
  `dataTypeId` tinyint(3) unsigned NOT NULL,
  `osId` tinyint(3) unsigned NOT NULL,
  `osVersion` varchar(36) DEFAULT NULL COMMENT 'May be NULL',
  `cpuTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'May be NULL for Java byte code',
  `dataUID` char(36) DEFAULT NULL COMMENT 'May be NULL if data is external',
  `dataURI` varchar(254) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`executableId`),
  UNIQUE KEY `unique_executables` (`appUID`,`dataTypeId`,`osId`,`osVersion`,`cpuTypeId`),
  KEY `fk_executables_dataTypes` (`dataTypeId`),
  KEY `fk_executables_oses` (`osId`),
  KEY `fk_executables_cpuTypes` (`cpuTypeId`),
  KEY `fk_executables_datas` (`dataUID`),
  CONSTRAINT `fk_executables_apps` FOREIGN KEY (`appUID`) REFERENCES `apps` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_executables_cpuTypes` FOREIGN KEY (`cpuTypeId`) REFERENCES `cpuTypes` (`cpuTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_executables_dataTypes` FOREIGN KEY (`dataTypeId`) REFERENCES `dataTypes` (`dataTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_executables_datas` FOREIGN KEY (`dataUID`) REFERENCES `datas` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_executables_oses` FOREIGN KEY (`osId`) REFERENCES `oses` (`osId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='executables = Applications files';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `executables`
--

LOCK TABLES `executables` WRITE;
/*!40000 ALTER TABLE `executables` DISABLE KEYS */;
/*!40000 ALTER TABLE `executables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `executables_history`
--

DROP TABLE IF EXISTS `executables_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `executables_history` (
  `executableId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `appUID` char(36) NOT NULL,
  `dataTypeId` tinyint(3) unsigned NOT NULL,
  `osId` tinyint(3) unsigned NOT NULL,
  `osVersion` varchar(36) DEFAULT NULL COMMENT 'May be NULL',
  `cpuTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'May be NULL for Java byte code',
  `dataUID` char(36) DEFAULT NULL COMMENT 'May be NULL if data is external',
  `dataURI` varchar(254) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`executableId`),
  UNIQUE KEY `unique_executables` (`appUID`,`dataTypeId`,`osId`,`osVersion`,`cpuTypeId`),
  KEY `appUID` (`appUID`),
  KEY `dataTypeId` (`dataTypeId`),
  KEY `osId` (`osId`),
  KEY `cpuTypeId` (`cpuTypeId`),
  KEY `dataUID` (`dataUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='executables = Applications files';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `executables_history`
--

LOCK TABLES `executables_history` WRITE;
/*!40000 ALTER TABLE `executables_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `executables_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `sessionUID` char(36) DEFAULT NULL COMMENT 'Session UID',
  `ownerUID` char(36) NOT NULL COMMENT 'Owner (user) UID',
  `name` varchar(254) NOT NULL COMMENT 'Group name',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `fk_groups_sessions` (`sessionUID`),
  KEY `fk_groups_users` (`ownerUID`),
  CONSTRAINT `fk_groups_sessions` FOREIGN KEY (`sessionUID`) REFERENCES `sessions` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_groups_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='work-groups = Persistent groups of works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups_history`
--

DROP TABLE IF EXISTS `groups_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `sessionUID` char(36) DEFAULT NULL COMMENT 'Session UID',
  `ownerUID` char(36) NOT NULL COMMENT 'Owner (user) UID',
  `name` varchar(254) NOT NULL COMMENT 'Group name',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `sessionUID` (`sessionUID`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='work-groups = Persistent groups of works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups_history`
--

LOCK TABLES `groups_history` WRITE;
/*!40000 ALTER TABLE `groups_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `groups_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hosts`
--

DROP TABLE IF EXISTS `hosts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hosts` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `osId` tinyint(3) unsigned DEFAULT NULL COMMENT 'Maybe unknown yet',
  `os` char(7) DEFAULT NULL COMMENT 'OS name',
  `osversion` varchar(36) DEFAULT NULL COMMENT 'OS version   Since XWHEP 6.0.0',
  `cpuTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'Maybe unknown yet',
  `cputype` char(7) DEFAULT NULL COMMENT 'CPU name',
  `usergroupUID` char(36) DEFAULT NULL COMMENT 'Optional, UID of the usergroup of the owner of jobs which will match.  Jobs from other groups will NOT match.',
  `project` varchar(254) DEFAULT NULL COMMENT 'Project this worker wants to limit its participation  In practice, this is a usergroup name  How it works : if a worker runs under an identity that belongs to a usergroup  This worker can execute any public or group job for the users of its group.  This w',
  `sharedapps` varchar(254) DEFAULT NULL COMMENT 'Optional, list of coma separated applications shared by the worker  Since 8.0.0 ',
  `sharedpackages` varchar(254) DEFAULT NULL COMMENT 'Optional, list of coma separated libraries shared by the worker  Since 8.0.0 ',
  `shareddatas` varchar(254) DEFAULT NULL COMMENT 'Optional, list of coma separated data shared by the worker  Since 8.0.0 (not used yet)',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `name` varchar(254) DEFAULT NULL COMMENT 'This host name',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `poolworksize` int(2) DEFAULT '0' COMMENT 'This is the amount of simultaneous jobs',
  `nbJobs` int(15) DEFAULT '0' COMMENT 'Completed jobs counter. updated on work completion',
  `pendingJobs` int(15) DEFAULT '0' COMMENT 'Pending jobs counter. updated on work submission',
  `runningJobs` int(15) DEFAULT '0' COMMENT 'Running jobs counter. updated on work request',
  `errorJobs` int(15) DEFAULT '0' COMMENT 'Error jobs counter. updated on job error',
  `timeOut` int(15) DEFAULT NULL COMMENT 'How many time to wait before this host is considered as lost  Default is provided by server, but this may also be defined from host config',
  `avgExecTime` int(15) DEFAULT '0' COMMENT 'Average execution time. updated on work completion',
  `lastAlive` datetime DEFAULT NULL COMMENT 'Last communication time (helps to determine whether this host is lost)',
  `nbconnections` int(10) DEFAULT NULL COMMENT 'How many time this host has been connected',
  `natedipaddr` varchar(50) DEFAULT NULL COMMENT 'This is the IP address as provided by worker itself.  This may be  a NATed IP  Since XWHEP 5.7.3 this length is 50 to comply to IP V6',
  `ipaddr` varchar(50) DEFAULT NULL COMMENT 'This is the IP address obtained at connexion time.  This is set by server at connexion time.  This may be different from NATed IP.  Since XWHEP 5.7.3 this length is 50 to comply to IP V6',
  `hwaddr` varchar(36) DEFAULT NULL COMMENT 'MAC address',
  `timezone` varchar(254) DEFAULT NULL COMMENT 'Time zone',
  `javaversion` varchar(254) DEFAULT NULL COMMENT 'Java version   Since XWHEP 6.0.0',
  `javadatamodel` int(4) DEFAULT NULL COMMENT 'Java data model (32 or 64 bits)  Since XWHEP 6.0.0',
  `cpunb` int(2) DEFAULT NULL COMMENT 'CPU counter',
  `cpumodel` varchar(50) DEFAULT NULL COMMENT 'CPU model',
  `cpuspeed` int(10) DEFAULT '0' COMMENT 'CPU speed in MHz',
  `totalmem` bigint(20) DEFAULT '0' COMMENT 'Total RAM in Kb',
  `availablemem` int(10) DEFAULT '0' COMMENT 'Available RAM in Kb, according to resource owner policy',
  `totalswap` bigint(20) DEFAULT '0' COMMENT 'Total SWAP in Mb',
  `totaltmp` bigint(20) DEFAULT '0' COMMENT 'Total space on tmp partition in Mb',
  `freetmp` bigint(20) DEFAULT '0' COMMENT 'Free space on tmp partition in Mb',
  `timeShift` int(15) DEFAULT NULL COMMENT 'Time diff from host to server',
  `avgping` int(20) DEFAULT NULL COMMENT 'Average ping to server',
  `nbping` int(10) DEFAULT NULL COMMENT 'Ping amount to server',
  `uploadbandwidth` float DEFAULT NULL COMMENT 'Upload bandwidth usage (in Mb/s)',
  `downloadbandwidth` float DEFAULT NULL COMMENT 'Download bandwidth usage (in Mb/s)',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java  Since 5.8.0',
  `cpuLoad` int(3) DEFAULT '50' COMMENT 'This defines the percentage of CPU usable by the worker  Since 8.0.0',
  `active` char(5) DEFAULT 'true' COMMENT 'This flag tells whether this host may be used   If a host has ever generated any error, this flag is automatically set to false  So that we don"t use faulty worker',
  `available` char(5) DEFAULT 'false' COMMENT 'This flag tells whether this host may run jobs accordingly to its local activation policy  This flag is provided by worker with alive signal',
  `incomingconnections` char(5) DEFAULT 'false' COMMENT 'This flag tells whether this host accepts to run jobs that listen for incoming connections  Since 8.0.0',
  `acceptbin` char(5) DEFAULT 'true' COMMENT 'This flag tells whether this host accept application binary   If false, this host accept to execute services written in java only',
  `version` varchar(254) NOT NULL COMMENT 'This is this host XWHEP software version',
  `traces` char(5) DEFAULT 'false' COMMENT 'This flag tells whether this host is collecting traces (CPU, memory, disk usage)',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `pilotjob` char(5) DEFAULT 'false' COMMENT 'XWHEP 5.7.3 : this is set to true if the worker is run on SG ressource (e.g. EGEE)  XWHEP 7.0.0 : this is deprecated; please use sgid instead ',
  `sgid` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.0.0 : this is the Service Grid Identifier; this is set by the DG 2 SG bridge, if any',
  `jobid` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.2.0 : this is a job URI; this is for SpeQuLoS (EDGI/JRA2).   If this is set, the worker will receive this job in priority,  If available, and according to the match making - CPU, OS...  This has a higher priority than batchid',
  `batchid` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.2.0 : this is a job group URI; this is for SpeQuLoS (EDGI/JRA2).  If this is set, the worker will receive a job from this group in priority,  If any, and according to the match making - CPU, OS...  This has a lower priority than jobid',
  `userproxy` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.0.0 this is not used',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `ethwalletaddr` varchar(254) DEFAULT NULL COMMENT 'worker eth wallet address; optional',
  `marketorderUID` char(36) DEFAULT NULL COMMENT 'Optional, UID of the market order',
  `contributionstatusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Contribution Status Id. See common/XWStatus.java',
  `contributionstatus` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Contribution Status. see common/XWStatus.java',
  `workerpooladdr` varchar(254) DEFAULT NULL COMMENT 'workerpool addr this host is registered to',
  PRIMARY KEY (`uid`),
  KEY `idx_ethwalletaddr` (`ethwalletaddr`),
  KEY `hosts_characteristics` (`name`,`ipaddr`,`hwaddr`,`cpuTypeId`,`cpunb`,`cpumodel`,`osId`,`osversion`),
  KEY `fk_hosts_oses` (`osId`),
  KEY `fk_hosts_cpuTypes` (`cpuTypeId`),
  KEY `fk_hosts_usergroups` (`usergroupUID`),
  KEY `fk_hosts_users` (`ownerUID`),
  CONSTRAINT `fk_hosts_cpuTypes` FOREIGN KEY (`cpuTypeId`) REFERENCES `cpuTypes` (`cpuTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_hosts_oses` FOREIGN KEY (`osId`) REFERENCES `oses` (`osId`) ON DELETE CASCADE,
  CONSTRAINT `fk_hosts_usergroups` FOREIGN KEY (`usergroupUID`) REFERENCES `usergroups` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_hosts_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='hosts = Computing resources where an XWHEP worker may run';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hosts`
--

LOCK TABLES `hosts` WRITE;
/*!40000 ALTER TABLE `hosts` DISABLE KEYS */;
/*!40000 ALTER TABLE `hosts` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_insert_os_cpu_project before insert on hosts
for each row
begin
  if   new.os is not null
  then
    if   new.os not in (select oses.osName from oses)
    then insert into oses
                 set osName        = new.os,
                     osDescription = concat('From host ', new.name, '  ', new.uid);
    end if;

    set new.osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
  end if;

  if   new.cputype is not null
  then
    if   new.cputype not in (select cpuTypes.cpuTypeName from cpuTypes)
    then insert into cpuTypes
                 set cpuTypeName        = new.cputype,
                     cpuTypeDescription = concat('From host ', new.name, '  ', new.uid);
    end if;

    set new.cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cputype );
  end if;

  if   new.project is not null
  then
    set new.usergroupUID =
           ( select usergroups.uid
             from   usergroups
             where  usergroups.label = new.project );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_insert_shared after insert on hosts
for each row
begin
  insert into sharedAppTypes ( hostUID, appTypeId )
  select new.uid, appTypes.appTypeId
  from   appTypes
  where  (new.sharedapps is not null) and (new.sharedapps <> '') and
         locate(appTypes.appTypeName, new.sharedapps);

  insert into sharedPackageTypes ( hostUID, packageTypeId )
  select new.uid, packageTypes.packageTypeId
  from   packageTypes
  where  (new.sharedpackages is not null) and (new.sharedpackages <> '') and
         locate(packageTypes.packageTypeName, new.sharedpackages);
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_update_os_cpu_project before update on hosts
for each row
begin
  if   (new.os is null) and (old.os is not null)
  then set new.osId = null;
  end if;

  if   (  new.os is not null  )  and
       ( (old.os is     null) or (old.os <> new.os) )
  then set new.osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
  end if;

  if   (new.cputype is null) and (old.cputype is not null)
  then set new.cpuTypeId = null;
  end if;

  if   (  new.cputype is not null  )  and
       ( (old.cputype is     null) or (old.cputype <> new.cputype) )
  then set new.cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cputype );
  end if;

  if   (new.project is null) and (old.project is not null)
  then set new.usergroupUID = null;
  end if;

  if   (  new.project is not null  )  and
       ( (old.project is     null) or (old.project <> new.project) )
  then set new.usergroupUID =
           ( select usergroups.uid
             from   usergroups
             where  usergroups.label = new.project );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_update_shared after update on hosts
for each row
begin
  if   (  old.sharedapps is not null  ) and
       ( (new.sharedapps is     null) or (new.sharedapps <> old.sharedapps) )
  then delete from  sharedAppTypes
              where sharedAppTypes.hostUID = old.uid;
  end if;

  if   (  new.sharedapps is not null  )  and
       ( (old.sharedapps is     null) or (old.sharedapps <> new.sharedapps) )
  then insert into  sharedAppTypes ( hostUID, appTypeId )
       select new.uid, appTypes.appTypeId
       from   appTypes
       where  (new.sharedapps is not null) and (new.sharedapps <> '') and
              locate(appTypes.appTypeName, new.sharedapps);
  end if;

  if   (  old.sharedpackages is not null  ) and
       ( (new.sharedpackages is     null) or (new.sharedpackages <> old.sharedpackages) )
  then delete from  sharedPackageTypes
              where sharedPackageTypes.hostUID = old.uid;
  end if;

  if   (  new.sharedpackages is not null  )  and
       ( (old.sharedpackages is     null) or (old.sharedpackages <> new.sharedpackages) )
  then insert into  sharedPackageTypes ( hostUID, packageTypeId )
       select new.uid, packageTypes.packageTypeId
       from   packageTypes
       where  (new.sharedpackages is not null) and (new.sharedpackages <> '') and
              locate(packageTypes.packageTypeName, new.sharedpackages);
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_delete_shared after delete on hosts
for each row
begin
  if   old.sharedapps is not null
  then delete from  sharedAppTypes
              where sharedAppTypes.hostUID = old.uid;
  end if;

  if   old.sharedpackages is not null
  then delete from  sharedPackageTypes
              where sharedPackageTypes.hostUID = old.uid;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hosts_history`
--

DROP TABLE IF EXISTS `hosts_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hosts_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `osId` tinyint(3) unsigned DEFAULT NULL COMMENT 'Maybe unknown yet',
  `os` char(7) DEFAULT NULL COMMENT 'OS name',
  `osversion` varchar(36) DEFAULT NULL COMMENT 'OS version   Since XWHEP 6.0.0',
  `cpuTypeId` tinyint(3) unsigned DEFAULT NULL COMMENT 'Maybe unknown yet',
  `cputype` char(7) DEFAULT NULL COMMENT 'CPU name',
  `usergroupUID` char(36) DEFAULT NULL COMMENT 'Optional, UID of the usergroup of the owner of jobs which will match.  Jobs from other groups will NOT match.',
  `project` varchar(254) DEFAULT NULL COMMENT 'Project this worker wants to limit its participation  In practice, this is a usergroup name  How it works : if a worker runs under an identity that belongs to a usergroup  This worker can execute any public or group job for the users of its group.  This w',
  `sharedapps` varchar(254) DEFAULT NULL COMMENT 'Optional, list of coma separated applications shared by the worker  Since 8.0.0 ',
  `sharedpackages` varchar(254) DEFAULT NULL COMMENT 'Optional, list of coma separated libraries shared by the worker  Since 8.0.0 ',
  `shareddatas` varchar(254) DEFAULT NULL COMMENT 'Optional, list of coma separated data shared by the worker  Since 8.0.0 (not used yet)',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `name` varchar(254) DEFAULT NULL COMMENT 'This host name',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `poolworksize` int(2) DEFAULT '0' COMMENT 'This is the amount of simultaneous jobs',
  `nbJobs` int(15) DEFAULT '0' COMMENT 'Completed jobs counter. updated on work completion',
  `pendingJobs` int(15) DEFAULT '0' COMMENT 'Pending jobs counter. updated on work submission',
  `runningJobs` int(15) DEFAULT '0' COMMENT 'Running jobs counter. updated on work request',
  `errorJobs` int(15) DEFAULT '0' COMMENT 'Error jobs counter. updated on job error',
  `timeOut` int(15) DEFAULT NULL COMMENT 'How many time to wait before this host is considered as lost  Default is provided by server, but this may also be defined from host config',
  `avgExecTime` int(15) DEFAULT '0' COMMENT 'Average execution time. updated on work completion',
  `lastAlive` datetime DEFAULT NULL COMMENT 'Last communication time (helps to determine whether this host is lost)',
  `nbconnections` int(10) DEFAULT NULL COMMENT 'How many time this host has been connected',
  `natedipaddr` varchar(50) DEFAULT NULL COMMENT 'This is the IP address as provided by worker itself.  This may be  a NATed IP  Since XWHEP 5.7.3 this length is 50 to comply to IP V6',
  `ipaddr` varchar(50) DEFAULT NULL COMMENT 'This is the IP address obtained at connexion time.  This is set by server at connexion time.  This may be different from NATed IP.  Since XWHEP 5.7.3 this length is 50 to comply to IP V6',
  `hwaddr` varchar(36) DEFAULT NULL COMMENT 'MAC address',
  `timezone` varchar(254) DEFAULT NULL COMMENT 'Time zone',
  `javaversion` varchar(254) DEFAULT NULL COMMENT 'Java version   Since XWHEP 6.0.0',
  `javadatamodel` int(4) DEFAULT NULL COMMENT 'Java data model (32 or 64 bits)  Since XWHEP 6.0.0',
  `cpunb` int(2) DEFAULT NULL COMMENT 'CPU counter',
  `cpumodel` varchar(50) DEFAULT NULL COMMENT 'CPU model',
  `cpuspeed` int(10) DEFAULT '0' COMMENT 'CPU speed in MHz',
  `totalmem` bigint(20) DEFAULT '0' COMMENT 'Total RAM in Kb',
  `availablemem` int(10) DEFAULT '0' COMMENT 'Available RAM in Kb, according to resource owner policy',
  `totalswap` bigint(20) DEFAULT '0' COMMENT 'Total SWAP in Mb',
  `totaltmp` bigint(20) DEFAULT '0' COMMENT 'Total space on tmp partition in Mb',
  `freetmp` bigint(20) DEFAULT '0' COMMENT 'Free space on tmp partition in Mb',
  `timeShift` int(15) DEFAULT NULL COMMENT 'Time diff from host to server',
  `avgping` int(20) DEFAULT NULL COMMENT 'Average ping to server',
  `nbping` int(10) DEFAULT NULL COMMENT 'Ping amount to server',
  `uploadbandwidth` float DEFAULT NULL COMMENT 'Upload bandwidth usage (in Mb/s)',
  `downloadbandwidth` float DEFAULT NULL COMMENT 'Download bandwidth usage (in Mb/s)',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java  Since 5.8.0',
  `cpuLoad` int(3) DEFAULT '50' COMMENT 'This defines the percentage of CPU usable by the worker  Since 8.0.0',
  `active` char(5) DEFAULT 'true' COMMENT 'This flag tells whether this host may be used   If a host has ever generated any error, this flag is automatically set to false  So that we don"t use faulty worker',
  `available` char(5) DEFAULT 'false' COMMENT 'This flag tells whether this host may run jobs accordingly to its local activation policy  This flag is provided by worker with alive signal',
  `incomingconnections` char(5) DEFAULT 'false' COMMENT 'This flag tells whether this host accepts to run jobs that listen for incoming connections  Since 8.0.0',
  `acceptbin` char(5) DEFAULT 'true' COMMENT 'This flag tells whether this host accept application binary   If false, this host accept to execute services written in java only',
  `version` varchar(254) NOT NULL COMMENT 'This is this host XWHEP software version',
  `traces` char(5) DEFAULT 'false' COMMENT 'This flag tells whether this host is collecting traces (CPU, memory, disk usage)',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `pilotjob` char(5) DEFAULT 'false' COMMENT 'XWHEP 5.7.3 : this is set to true if the worker is run on SG ressource (e.g. EGEE)  XWHEP 7.0.0 : this is deprecated; please use sgid instead ',
  `sgid` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.0.0 : this is the Service Grid Identifier; this is set by the DG 2 SG bridge, if any',
  `jobid` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.2.0 : this is a job URI; this is for SpeQuLoS (EDGI/JRA2).   If this is set, the worker will receive this job in priority,  If available, and according to the match making - CPU, OS...  This has a higher priority than batchid',
  `batchid` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.2.0 : this is a job group URI; this is for SpeQuLoS (EDGI/JRA2).  If this is set, the worker will receive a job from this group in priority,  If any, and according to the match making - CPU, OS...  This has a lower priority than jobid',
  `userproxy` varchar(254) DEFAULT NULL COMMENT 'XWHEP 7.0.0 this is not used',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `ethwalletaddr` varchar(254) DEFAULT NULL COMMENT 'worker eth wallet address; optional',
  `marketorderUID` char(36) DEFAULT NULL COMMENT 'Optional, UID of the market order',
  `contributionstatusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Contribution Status Id. See common/XWStatus.java',
  `contributionstatus` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Contribution Status. see common/XWStatus.java',
  `workerpooladdr` varchar(254) DEFAULT NULL COMMENT 'workerpool addr this host is registered to',
  PRIMARY KEY (`uid`),
  KEY `idx_ethwalletaddr` (`ethwalletaddr`),
  KEY `osId` (`osId`),
  KEY `cputypeId` (`cpuTypeId`),
  KEY `usergroupUID` (`usergroupUID`),
  KEY `ownerUID` (`ownerUID`),
  KEY `hosts_characteristics` (`name`,`ipaddr`,`hwaddr`,`cpuTypeId`,`cpunb`,`cpumodel`,`osId`,`osversion`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='hosts = Computing resources where an XWHEP worker may run';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hosts_history`
--

LOCK TABLES `hosts_history` WRITE;
/*!40000 ALTER TABLE `hosts_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `hosts_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_history_insert_os_cpu_project before insert on hosts_history
for each row
begin
  if   new.os is not null
  then
    set @osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
    if (select @osId) is not null
    then set new.osId = @osId;
    end if;
  end if;

  if   new.cputype is not null
  then
    set @cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cputype );
    if (select @cpuTypeId) is not null
    then set new.cpuTypeId = @cpuTypeId;
    end if;
  end if;

  if   new.project is not null
  then
    set @usergroupUID =
           ( select usergroups.uid
             from   usergroups
             where  usergroups.label = new.project );
    if (select @usergroupUID) is not null
    then set new.usergroupUID = @usergroupUID;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_history_insert_shared after insert on hosts_history
for each row
begin
  insert into sharedAppTypes_history ( hostUID, appTypeId )
  select new.uid, appTypes.appTypeId
  from   appTypes
  where  (new.sharedApps is not null) and (new.sharedApps <> '') and
         locate(appTypes.appTypeName, new.sharedApps);

  insert into sharedPackageTypes_history ( hostUID, packageTypeId )
  select new.uid, packageTypes.packageTypeId
  from   packageTypes
  where  (new.sharedApps is not null) and (new.sharedApps <> '') and
         locate(packageTypes.packageTypeName, new.sharedApps);
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_history_update_os_cpu_project before update on hosts_history
for each row
begin
  if   (new.os is null) and (old.os is not null)
  then set new.osId = null;
  end if;

  if   (  new.os is not null  )  and
       ( (old.os is     null) or (old.os <> new.os) )
  then
    set @osId =
           ( select oses.osId
             from   oses
             where  oses.osName = new.os );
    if (select @osId) is not null
    then set new.osId = @osId;
    end if;
  end if;

  if   (new.cputype is null) and (old.cputype is not null)
  then set new.cpuTypeId = null;
  end if;

  if   (  new.cputype is not null  )  and
       ( (old.cputype is     null) or (old.cputype <> new.cputype) )
  then
    set @cpuTypeId =
           ( select cpuTypes.cpuTypeId
             from   cpuTypes
             where  cpuTypes.cpuTypeName = new.cputype );
    if (select @cpuTypeId) is not null
    then set new.cpuTypeId = @cpuTypeId;
    end if;
  end if;

  if   (new.project is null) and (old.project is not null)
  then set new.usergroupUID = null;
  end if;

  if   (  new.project is not null  )  and
       ( (old.project is     null) or (old.project <> new.project) )
  then
    set @usergroupUID =
           ( select usergroups.uid
             from   usergroups
             where  usergroups.label = new.project );
    if (select @usergroupUID) is not null
    then set new.usergroupUID = @usergroupUID;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_history_update_shared after update on hosts_history
for each row
begin
  if   (  old.sharedapps is not null  ) and
       ( (new.sharedapps is     null) or (new.sharedapps <> old.sharedapps) )
  then delete from  sharedAppTypes_history
              where sharedAppTypes_history.hostUID = old.uid;
  end if;

  if   (  new.sharedapps is not null  )  and
       ( (old.sharedapps is     null) or (old.sharedapps <> new.sharedapps) )
  then insert into  sharedAppTypes_history ( hostUID, appTypeId )
       select new.uid, appTypes.appTypeId
       from   appTypes
       where  (new.sharedApps is not null) and (new.sharedApps <> '') and
              locate(appTypes.appTypeName, new.sharedApps);
  end if;

  if   (  old.sharedapps is not null  ) and
       ( (new.sharedapps is     null) or (new.sharedapps <> old.sharedapps) )
  then delete from  sharedPackageTypes_history
              where sharedPackageTypes_history.hostUID = old.uid;
  end if;

  if   (  new.sharedapps is not null  )  and
       ( (old.sharedapps is     null) or (old.sharedapps <> new.sharedapps) )
  then insert into  sharedPackageTypes_history ( hostUID, packageTypeId )
       select new.uid, packageTypes.packageTypeId
       from   packageTypes
       where  (new.sharedApps is not null) and (new.sharedApps <> '') and
              locate(packageTypes.packageTypeName, new.sharedApps);
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_hosts_history_delete_shared after delete on hosts_history
for each row
begin
  if   old.sharedapps is not null
  then delete from  sharedAppTypes_history
              where sharedAppTypes_history.hostUID = old.uid;
  end if;

  if   old.sharedapps is not null
  then delete from  sharedPackageTypes_history
              where sharedPackageTypes_history.hostUID = old.uid;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `marketorders`
--

DROP TABLE IF EXISTS `marketorders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marketorders` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `accessRights` int(4) DEFAULT '1877' COMMENT 'Please note that a category is always public',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `direction` char(25) NOT NULL DEFAULT 'UNSET' COMMENT 'Please see MarketOrderDirectionEnum',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Status. see common/XWStatus.java',
  `marketOrderIdx` bigint(20) DEFAULT NULL COMMENT 'market order index',
  `categoryId` bigint(20) NOT NULL COMMENT 'catID reference',
  `expectedWorkers` bigint(20) NOT NULL COMMENT 'how many workers to safely reach the trust',
  `nbWorkers` bigint(20) NOT NULL DEFAULT '0' COMMENT 'how many workers alredy booked',
  `trust` bigint(20) NOT NULL DEFAULT '70' COMMENT 'expected trust',
  `price` bigint(20) NOT NULL DEFAULT '0' COMMENT 'this is the cost or the price, depending on direction; this is named value in smart contract',
  `volume` bigint(20) DEFAULT '0' COMMENT 'how many such orders the scheduler can propose',
  `remaining` bigint(20) DEFAULT '0' COMMENT 'how many such orders left',
  `workerpooladdr` varchar(254) NOT NULL COMMENT 'workerpool smart contract address',
  `workerpoolowneraddr` varchar(254) NOT NULL COMMENT 'workerpool owner address',
  `arrivalDate` datetime DEFAULT NULL COMMENT 'insertion date',
  `startDate` datetime DEFAULT NULL COMMENT 'ready for computation date',
  `completedDate` datetime DEFAULT NULL COMMENT 'completion date',
  `contributingDate` datetime DEFAULT NULL COMMENT 'contributing date',
  `revealingDate` datetime DEFAULT NULL COMMENT 'revealing date',
  PRIMARY KEY (`uid`),
  KEY `idx_catgoryid` (`categoryId`),
  KEY `idx_workerpooladdr` (`workerpooladdr`),
  KEY `idx_workerpoolowneraddr` (`workerpoolowneraddr`),
  KEY `ownerUID` (`ownerUID`),
  CONSTRAINT `marketorders_ibfk_1` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='marketorders = marketorders to sell CPU power';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marketorders`
--

LOCK TABLES `marketorders` WRITE;
/*!40000 ALTER TABLE `marketorders` DISABLE KEYS */;
/*!40000 ALTER TABLE `marketorders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marketorders_history`
--

DROP TABLE IF EXISTS `marketorders_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marketorders_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `accessRights` int(4) DEFAULT '1877' COMMENT 'Please note that a category is always public',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `direction` char(25) NOT NULL DEFAULT 'UNSET' COMMENT 'Please see MarketOrderDirectionEnum',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Status. see common/XWStatus.java',
  `marketOrderIdx` bigint(20) DEFAULT NULL COMMENT 'market order index',
  `categoryId` bigint(20) NOT NULL COMMENT 'catID reference',
  `expectedWorkers` bigint(20) NOT NULL COMMENT 'how many workers to safely reach the trust',
  `nbWorkers` bigint(20) NOT NULL DEFAULT '0' COMMENT 'how many workers alredy booked',
  `trust` bigint(20) NOT NULL DEFAULT '70' COMMENT 'expected trust',
  `price` bigint(20) NOT NULL DEFAULT '0' COMMENT 'this is the cost or the price, depending on direction; this is named value in smart contract',
  `volume` bigint(20) DEFAULT '0' COMMENT 'how many such orders the scheduler can propose',
  `remaining` bigint(20) DEFAULT '0' COMMENT 'how many such orders left',
  `workerpooladdr` varchar(254) NOT NULL COMMENT 'workerpool smart contract address',
  `workerpoolowneraddr` varchar(254) NOT NULL COMMENT 'workerpool owner address',
  `arrivalDate` datetime DEFAULT NULL COMMENT 'insertion date',
  `startDate` datetime DEFAULT NULL COMMENT 'ready for computation date',
  `completedDate` datetime DEFAULT NULL COMMENT 'completion date',
  `contributingDate` datetime DEFAULT NULL COMMENT 'contributing date',
  `revealingDate` datetime DEFAULT NULL COMMENT 'revealing date',
  PRIMARY KEY (`uid`),
  KEY `idx_catgoryid` (`categoryId`),
  KEY `idx_workerpooladdr` (`workerpooladdr`),
  KEY `idx_workerpoolowneraddr` (`workerpoolowneraddr`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='marketorders = marketorders to sell CPU power';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marketorders_history`
--

LOCK TABLES `marketorders_history` WRITE;
/*!40000 ALTER TABLE `marketorders_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `marketorders_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memberships`
--

DROP TABLE IF EXISTS `memberships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `memberships` (
  `userUID` char(36) NOT NULL,
  `usergroupUID` char(36) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `fk_memberships_users` (`userUID`),
  KEY `fk_memberships_usergroups` (`usergroupUID`),
  CONSTRAINT `fk_memberships_usergroups` FOREIGN KEY (`usergroupUID`) REFERENCES `usergroups` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_memberships_users` FOREIGN KEY (`userUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='memberships = n-n relationship "users" - "usergoups"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memberships`
--

LOCK TABLES `memberships` WRITE;
/*!40000 ALTER TABLE `memberships` DISABLE KEYS */;
/*!40000 ALTER TABLE `memberships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memberships_history`
--

DROP TABLE IF EXISTS `memberships_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `memberships_history` (
  `userUID` char(36) NOT NULL,
  `usergroupUID` char(36) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `userUID` (`userUID`),
  KEY `usergroupUID` (`usergroupUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='memberships = n-n relationship "users" - "usergoups"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memberships_history`
--

LOCK TABLES `memberships_history` WRITE;
/*!40000 ALTER TABLE `memberships_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `memberships_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oses`
--

DROP TABLE IF EXISTS `oses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oses` (
  `osId` tinyint(3) unsigned NOT NULL,
  `osName` char(7) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `osDescription` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`osId`),
  UNIQUE KEY `osName` (`osName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='oses = Constants for *."os"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oses`
--

LOCK TABLES `oses` WRITE;
/*!40000 ALTER TABLE `oses` DISABLE KEYS */;
INSERT INTO `oses` VALUES (0,'NONE','2018-05-24 12:39:46','Unknown'),(1,'ANDROID','2018-05-24 12:39:46','Android'),(2,'JAVA','2018-05-24 12:39:46','Java Virtual Machine'),(3,'LINUX','2018-05-24 12:39:46','Linux'),(4,'MACOSX','2018-05-24 12:39:46','MacOS-X '),(5,'WIN32','2018-05-24 12:39:46','MS-Windows 32 bits'),(6,'WIN64','2018-05-24 12:39:46','MS-Windows 64 bits');
/*!40000 ALTER TABLE `oses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `packageTypes`
--

DROP TABLE IF EXISTS `packageTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `packageTypes` (
  `packageTypeId` tinyint(3) unsigned NOT NULL,
  `packageTypeName` varchar(254) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `packageTypeDescription` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`packageTypeId`),
  UNIQUE KEY `packageTypeName` (`packageTypeName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='appTypes = Constants for "apps"."neededpackages"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `packageTypes`
--

LOCK TABLES `packageTypes` WRITE;
/*!40000 ALTER TABLE `packageTypes` DISABLE KEYS */;
INSERT INTO `packageTypes` VALUES (0,'NONE','2018-05-24 12:39:46','Unknown package type'),(1,'GEANT4','2018-05-24 12:39:46','Package for High Energy Physics'),(2,'ROOT','2018-05-24 12:39:46','Package for High Energy Physics');
/*!40000 ALTER TABLE `packageTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `ownerUID` char(36) NOT NULL COMMENT 'Owner (user) UID',
  `name` varchar(254) NOT NULL COMMENT 'Session name',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `fk_sessions_users` (`ownerUID`),
  CONSTRAINT `fk_sessions_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='work-sessions = Sessions for transient grouping of works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions_history`
--

DROP TABLE IF EXISTS `sessions_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `ownerUID` char(36) NOT NULL COMMENT 'Owner (user) UID',
  `name` varchar(254) NOT NULL COMMENT 'Session name',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='work-sessions = Sessions for transient grouping of works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions_history`
--

LOCK TABLES `sessions_history` WRITE;
/*!40000 ALTER TABLE `sessions_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sharedAppTypes`
--

DROP TABLE IF EXISTS `sharedAppTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sharedAppTypes` (
  `hostUID` char(36) NOT NULL,
  `appTypeId` tinyint(3) unsigned NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`hostUID`,`appTypeId`),
  KEY `fk_sharedAppTypes_appTypes` (`appTypeId`),
  CONSTRAINT `fk_sharedAppTypes_appTypes` FOREIGN KEY (`appTypeId`) REFERENCES `appTypes` (`appTypeId`) ON DELETE CASCADE,
  CONSTRAINT `fk_sharedAppTypes_hosts` FOREIGN KEY (`hostUID`) REFERENCES `hosts` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='sharedAppTypes = n-n relationship "hosts" - "appTypes"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sharedAppTypes`
--

LOCK TABLES `sharedAppTypes` WRITE;
/*!40000 ALTER TABLE `sharedAppTypes` DISABLE KEYS */;
INSERT INTO `sharedAppTypes` VALUES ('2373f00b-ebd8-45ce-9a31-fc74f1766685',4,'2018-05-24 12:42:26');
/*!40000 ALTER TABLE `sharedAppTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sharedAppTypes_history`
--

DROP TABLE IF EXISTS `sharedAppTypes_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sharedAppTypes_history` (
  `hostUID` char(36) NOT NULL,
  `appTypeId` tinyint(3) unsigned NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`hostUID`,`appTypeId`),
  KEY `hostUID` (`hostUID`),
  KEY `appTypeId` (`appTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='sharedAppTypes = n-n relationship "hosts" - "appTypes"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sharedAppTypes_history`
--

LOCK TABLES `sharedAppTypes_history` WRITE;
/*!40000 ALTER TABLE `sharedAppTypes_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `sharedAppTypes_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sharedPackageTypes`
--

DROP TABLE IF EXISTS `sharedPackageTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sharedPackageTypes` (
  `hostUID` char(36) NOT NULL,
  `packageTypeId` tinyint(3) unsigned NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`hostUID`,`packageTypeId`),
  KEY `fk_sharedPackageTypes_packageTypes` (`packageTypeId`),
  CONSTRAINT `fk_sharedPackageTypes_hosts` FOREIGN KEY (`hostUID`) REFERENCES `hosts` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_sharedPackageTypes_packageTypes` FOREIGN KEY (`packageTypeId`) REFERENCES `packageTypes` (`packageTypeId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='sharedPackageTypes = n-n relationship "hosts"-"packageTypes"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sharedPackageTypes`
--

LOCK TABLES `sharedPackageTypes` WRITE;
/*!40000 ALTER TABLE `sharedPackageTypes` DISABLE KEYS */;
/*!40000 ALTER TABLE `sharedPackageTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sharedPackageTypes_history`
--

DROP TABLE IF EXISTS `sharedPackageTypes_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sharedPackageTypes_history` (
  `hostUID` char(36) NOT NULL,
  `packageTypeId` tinyint(3) unsigned NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`hostUID`,`packageTypeId`),
  KEY `hostUID` (`hostUID`),
  KEY `packageTypeId` (`packageTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='sharedPackageTypes = n-n relationship "hosts"-"packageTypes"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sharedPackageTypes_history`
--

LOCK TABLES `sharedPackageTypes_history` WRITE;
/*!40000 ALTER TABLE `sharedPackageTypes_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `sharedPackageTypes_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statuses`
--

DROP TABLE IF EXISTS `statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statuses` (
  `statusId` tinyint(3) unsigned NOT NULL,
  `statusName` varchar(36) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `statusObjects` varchar(254) NOT NULL,
  `statusComment` varchar(254) DEFAULT NULL,
  `statusDeprecated` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`statusId`),
  UNIQUE KEY `statusName` (`statusName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='statuses = Constants for *."status"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statuses`
--

LOCK TABLES `statuses` WRITE;
/*!40000 ALTER TABLE `statuses` DISABLE KEYS */;
INSERT INTO `statuses` VALUES (0,'NONE','2018-05-24 12:39:46','none',NULL,NULL),(1,'ANY','2018-05-24 12:39:46','any',NULL,NULL),(2,'WAITING','2018-05-24 12:39:46','works','The object is stored on server but not in the server queue yet',NULL),(3,'PENDING','2018-05-24 12:39:46','works, tasks','The object is stored and inserted in the server queue',NULL),(4,'RUNNING','2018-05-24 12:39:46','works, tasks','The object is being run by a worker',NULL),(5,'ERROR','2018-05-24 12:39:46','any','The object is erroneous',NULL),(6,'COMPLETED','2018-05-24 12:39:46','works, tasks','The job has been successfully computed',NULL),(7,'ABORTED','2018-05-24 12:39:46','works, tasks','NOT used anymore','Since XWHEP, aborted objects are set to PENDING'),(8,'LOST','2018-05-24 12:39:46','works, tasks','NOT used anymore','Since XWHEP, lost objects are set to PENDING'),(9,'DATAREQUEST','2018-05-24 12:39:46','datas, works, tasks','The server is unable to store the uploaded object. Waiting for another upload try',NULL),(10,'RESULTREQUEST','2018-05-24 12:39:46','works','The worker should retry to upload the results',NULL),(11,'AVAILABLE','2018-05-24 12:39:46','datas','The data is available and can be downloaded on demand',NULL),(12,'UNAVAILABLE','2018-05-24 12:39:46','datas','The data is not available and can not be downloaded on demand',NULL),(13,'REPLICATING','2018-05-24 12:39:46','works','The object is being replicated',NULL),(14,'FAILED','2018-05-24 12:39:46','works','The job does not fill its category requirements',NULL),(15,'CONTRIBUTING','2018-05-24 12:39:46','works','The job does not fill its category requirements',NULL),(16,'CONTRIBUTED','2018-05-24 12:39:46','works','The job does not fill its category requirements',NULL),(17,'REVEALING','2018-05-24 12:39:46','works','The job does not fill its category requirements',NULL);
/*!40000 ALTER TABLE `statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tasks` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `workUID` char(36) NOT NULL COMMENT 'This is the referenced work',
  `hostUID` char(36) DEFAULT NULL COMMENT 'Host UID',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) DEFAULT NULL COMMENT 'Status. see common/XWStatus.java',
  `ownerUID` char(36) DEFAULT NULL COMMENT 'Since 5.8.0',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `trial` int(11) DEFAULT NULL COMMENT 'Instanciation counter',
  `InsertionDate` datetime DEFAULT NULL COMMENT 'When the server put this task into queue',
  `StartDate` datetime DEFAULT NULL COMMENT 'First instanciation date',
  `LastStartDate` datetime DEFAULT NULL COMMENT 'Last instanciation date',
  `AliveCount` int(11) DEFAULT NULL COMMENT 'Is it necessary ?',
  `LastAlive` datetime DEFAULT NULL COMMENT 'Is it necessary ?',
  `removalDate` datetime DEFAULT NULL COMMENT 'When this task has been removed',
  `duration` bigint(20) DEFAULT '0' COMMENT 'Last instanciation duration',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if row has been deleted ',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `price` bigint(20) DEFAULT '0' COMMENT 'since 13.1.0',
  PRIMARY KEY (`uid`),
  KEY `fk_tasks_works` (`workUID`),
  KEY `fk_tasks_hosts` (`hostUID`),
  KEY `fk_tasks_statuses` (`statusId`),
  KEY `fk_tasks_users` (`ownerUID`),
  CONSTRAINT `fk_tasks_hosts` FOREIGN KEY (`hostUID`) REFERENCES `hosts` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_tasks_statuses` FOREIGN KEY (`statusId`) REFERENCES `statuses` (`statusId`) ON DELETE CASCADE,
  CONSTRAINT `fk_tasks_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_tasks_works` FOREIGN KEY (`workUID`) REFERENCES `works` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='tasks = Work sent to a host with the adequate app binary';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks`
--

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_tasks_insert_status before insert on tasks
for each row
begin
  if   new.status is not null
  then set new.statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_tasks_update_status before update on tasks
for each row
begin
  if   (new.status is null) and (old.status is not null)
  then set new.statusId = null;
  end if;

  if   (  new.status is not null  )  and
       ( (old.status is     null) or (old.status <> new.status) )
  then set new.statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `tasks_history`
--

DROP TABLE IF EXISTS `tasks_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tasks_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `workUID` char(36) NOT NULL COMMENT 'This is the referenced work',
  `hostUID` char(36) DEFAULT NULL COMMENT 'Host UID',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) DEFAULT NULL COMMENT 'Status. see common/XWStatus.java',
  `ownerUID` char(36) DEFAULT NULL COMMENT 'Since 5.8.0',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `trial` int(11) DEFAULT NULL COMMENT 'Instanciation counter',
  `InsertionDate` datetime DEFAULT NULL COMMENT 'When the server put this task into queue',
  `StartDate` datetime DEFAULT NULL COMMENT 'First instanciation date',
  `LastStartDate` datetime DEFAULT NULL COMMENT 'Last instanciation date',
  `AliveCount` int(11) DEFAULT NULL COMMENT 'Is it necessary ?',
  `LastAlive` datetime DEFAULT NULL COMMENT 'Is it necessary ?',
  `removalDate` datetime DEFAULT NULL COMMENT 'When this task has been removed',
  `duration` bigint(20) DEFAULT '0' COMMENT 'Last instanciation duration',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if row has been deleted ',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `price` bigint(20) DEFAULT '0' COMMENT 'since 13.1.0',
  PRIMARY KEY (`uid`),
  KEY `workUID` (`workUID`),
  KEY `hostUID` (`hostUID`),
  KEY `statusId` (`statusId`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='tasks = Work sent to a host with the adequate app binary';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks_history`
--

LOCK TABLES `tasks_history` WRITE;
/*!40000 ALTER TABLE `tasks_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `tasks_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_tasks_history_insert_status before insert on tasks_history
for each row
begin
  if   new.status is not null
  then
    set @statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
    if (select @statusId) is not null
    then set new.statusId = @statusId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_tasks_history_update_status before update on tasks_history
for each row
begin
  if   (new.status is null) and (old.status is not null)
  then set new.statusId = null;
  end if;

  if   (  new.status is not null  )  and
       ( (old.status is     null) or (old.status <> new.status) )
  then
    set @statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
    if (select @statusId) is not null
    then set new.statusId = @statusId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `traces`
--

DROP TABLE IF EXISTS `traces`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traces` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `hostUID` char(36) NOT NULL DEFAULT '' COMMENT 'Host UID',
  `ownerUID` char(36) NOT NULL COMMENT 'Since 5.8.0',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `login` varchar(254) NOT NULL DEFAULT '',
  `arrivalDate` datetime NOT NULL,
  `startDate` datetime NOT NULL,
  `endDate` datetime NOT NULL,
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `data` varchar(254) NOT NULL DEFAULT '' COMMENT 'Data URI',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  PRIMARY KEY (`uid`),
  KEY `fk_traces_hosts` (`hostUID`),
  KEY `fk_traces_users` (`ownerUID`),
  CONSTRAINT `fk_traces_hosts` FOREIGN KEY (`hostUID`) REFERENCES `hosts` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_traces_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='traces from workers (to trace CPU, RAM, Disk etc. activities';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traces`
--

LOCK TABLES `traces` WRITE;
/*!40000 ALTER TABLE `traces` DISABLE KEYS */;
/*!40000 ALTER TABLE `traces` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traces_history`
--

DROP TABLE IF EXISTS `traces_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traces_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `hostUID` char(36) NOT NULL DEFAULT '' COMMENT 'Host UID',
  `ownerUID` char(36) NOT NULL COMMENT 'Since 5.8.0',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `login` varchar(254) NOT NULL DEFAULT '',
  `arrivalDate` datetime NOT NULL,
  `startDate` datetime NOT NULL,
  `endDate` datetime NOT NULL,
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `data` varchar(254) NOT NULL DEFAULT '' COMMENT 'Data URI',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  PRIMARY KEY (`uid`),
  KEY `hostUID` (`hostUID`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='traces from workers (to trace CPU, RAM, Disk etc. activities';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traces_history`
--

LOCK TABLES `traces_history` WRITE;
/*!40000 ALTER TABLE `traces_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `traces_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userRights`
--

DROP TABLE IF EXISTS `userRights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userRights` (
  `userRightId` tinyint(3) unsigned NOT NULL,
  `userRightName` varchar(254) NOT NULL,
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `userRightDescription` varchar(254) DEFAULT NULL,
  PRIMARY KEY (`userRightId`),
  UNIQUE KEY `userRightName` (`userRightName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='userRights = Constants for "users"."rights"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userRights`
--

LOCK TABLES `userRights` WRITE;
/*!40000 ALTER TABLE `userRights` DISABLE KEYS */;
INSERT INTO `userRights` VALUES (0,'NONE','2018-05-24 12:39:46',NULL),(1,'INSERTJOB','2018-05-24 12:39:46',NULL),(2,'GETJOB','2018-05-24 12:39:46',NULL),(3,'INSERTDATA','2018-05-24 12:39:46',NULL),(4,'GETDATA','2018-05-24 12:39:46',NULL),(5,'GETGROUP','2018-05-24 12:39:46',NULL),(6,'GETSESSION','2018-05-24 12:39:46',NULL),(7,'GETHOST','2018-05-24 12:39:46',NULL),(8,'GETAPP','2018-05-24 12:39:46',NULL),(9,'GETUSER','2018-05-24 12:39:46',NULL),(10,'GETCATEGORY','2018-05-24 12:39:46',NULL),(11,'GETMARKETORDER','2018-05-24 12:39:46',NULL),(12,'UPDATEWORK','2018-05-24 12:39:46','worker can update work for the owner'),(13,'WORKER_USER','2018-05-24 12:39:46','worker cannot do everything'),(14,'VWORKER_USER','2018-05-24 12:39:46','vworker can take advantage of stickybit'),(15,'BROADCAST','2018-05-24 12:39:46','submit one job to all workers'),(16,'LISTJOB','2018-05-24 12:39:46',NULL),(17,'DELETEJOB','2018-05-24 12:39:46',NULL),(18,'LISTDATA','2018-05-24 12:39:46',NULL),(19,'DELETEDATA','2018-05-24 12:39:46',NULL),(20,'LISTGROUP','2018-05-24 12:39:46',NULL),(21,'INSERTGROUP','2018-05-24 12:39:46',NULL),(22,'DELETEGROUP','2018-05-24 12:39:46',NULL),(23,'LISTSESSION','2018-05-24 12:39:46',NULL),(24,'INSERTSESSION','2018-05-24 12:39:46',NULL),(25,'DELETESESSION','2018-05-24 12:39:46',NULL),(26,'LISTHOST','2018-05-24 12:39:46',NULL),(27,'LISTUSER','2018-05-24 12:39:46',NULL),(28,'LISTUSERGROUP','2018-05-24 12:39:46',NULL),(29,'GETUSERGROUP','2018-05-24 12:39:46',NULL),(30,'INSERTAPP','2018-05-24 12:39:46',NULL),(31,'DELETEAPP','2018-05-24 12:39:46',NULL),(32,'LISTAPP','2018-05-24 12:39:46',NULL),(33,'LISTCATEGORY','2018-05-24 12:39:46',NULL),(34,'LISTMARKETORDER','2018-05-24 12:39:46',NULL),(35,'STANDARD_USER','2018-05-24 12:39:46','non privileged user'),(36,'INSERTUSER','2018-05-24 12:39:46',NULL),(37,'DELETEUSER','2018-05-24 12:39:46',NULL),(38,'ADVANCED_USER','2018-05-24 12:39:46','privileged user (e.g. user group manager)'),(39,'MANDATED_USER','2018-05-24 12:39:46','can work in name of another user'),(40,'INSERTHOST','2018-05-24 12:39:46',NULL),(42,'INSERTCATEGORY','2018-05-24 12:39:46',NULL),(43,'INSERTMARKETORDER','2018-05-24 12:39:46',NULL),(44,'DELETEHOST','2018-05-24 12:39:46',NULL),(45,'INSERTUSERGROUP','2018-05-24 12:39:46',NULL),(46,'DELETEUSERGROUP','2018-05-24 12:39:46',NULL),(47,'DELETEMARKETORDER','2018-05-24 12:39:46',NULL),(48,'SUPER_USER','2018-05-24 12:39:46','can do all');
/*!40000 ALTER TABLE `userRights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usergroups`
--

DROP TABLE IF EXISTS `usergroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usergroups` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `label` varchar(254) NOT NULL COMMENT 'User group label',
  `ownerUID` char(36) NOT NULL COMMENT 'Since 5.8.0',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `webpage` varchar(254) DEFAULT NULL COMMENT 'Application web page',
  `project` char(5) DEFAULT 'true' COMMENT 'True if this can be a "project"  This is always true, except for worker and administrator user groups',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `fk_usergroups_users` (`ownerUID`),
  CONSTRAINT `fk_usergroups_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='usergroups = Groups of users';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usergroups`
--

LOCK TABLES `usergroups` WRITE;
/*!40000 ALTER TABLE `usergroups` DISABLE KEYS */;
/*!40000 ALTER TABLE `usergroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usergroups_history`
--

DROP TABLE IF EXISTS `usergroups_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usergroups_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `label` varchar(254) NOT NULL COMMENT 'User group label',
  `ownerUID` char(36) NOT NULL COMMENT 'Since 5.8.0',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `webpage` varchar(254) DEFAULT NULL COMMENT 'Application web page',
  `project` char(5) DEFAULT 'true' COMMENT 'True if this can be a "project"  This is always true, except for worker and administrator user groups',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='usergroups = Groups of users';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usergroups_history`
--

LOCK TABLES `usergroups_history` WRITE;
/*!40000 ALTER TABLE `usergroups_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `usergroups_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `login` varchar(254) NOT NULL COMMENT 'User login. if your change length, don t forget to change UserInterface.USERLOGINLENGTH',
  `userRightId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'User rights Id. See common/UserRights.java',
  `rights` varchar(254) NOT NULL DEFAULT 'NONE' COMMENT 'User rights (deprecated)',
  `usergroupUID` char(36) DEFAULT NULL COMMENT 'Optionnal. user group UID',
  `ownerUID` char(36) NOT NULL COMMENT 'Owner UID',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `nbJobs` int(15) DEFAULT '0' COMMENT 'Completed jobs counter. updated on work completion',
  `pendingJobs` int(15) DEFAULT '0' COMMENT 'Pending jobs counter. updated on work submission',
  `runningJobs` int(15) DEFAULT '0' COMMENT 'Running jobs counter. updated on work request',
  `errorJobs` int(15) DEFAULT '0' COMMENT 'Error jobs counter. updated on job error',
  `usedCpuTime` bigint(20) DEFAULT '0' COMMENT 'Average execution time. updated on work completion',
  `certificate` text COMMENT 'This is the X.509 proxy file content',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `password` varchar(254) NOT NULL DEFAULT '' COMMENT 'User password',
  `email` varchar(254) NOT NULL DEFAULT '' COMMENT 'User email',
  `fname` varchar(254) DEFAULT NULL COMMENT 'Optionnal, user first name',
  `lname` varchar(254) DEFAULT NULL COMMENT 'Optionnal, user last name',
  `country` varchar(254) DEFAULT NULL COMMENT 'User country',
  `challenging` char(5) DEFAULT 'false' COMMENT 'True if this user connect using private/public keys pair',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `login` (`login`),
  KEY `fk_users_userRights` (`userRightId`),
  KEY `fk_users_usergroups` (`usergroupUID`),
  KEY `fk_users_users` (`ownerUID`),
  CONSTRAINT `fk_users_userRights` FOREIGN KEY (`userRightId`) REFERENCES `userRights` (`userRightId`) ON DELETE CASCADE,
  CONSTRAINT `fk_users_usergroups` FOREIGN KEY (`usergroupUID`) REFERENCES `usergroups` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_users_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='users = Owners of objects.  Most users may submit works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('1bc0dec8-6f02-40e4-84da-b718be92863c','worker',13,'WORKER_USER',NULL,'98ec298c-406d-4379-9616-5652f2d05e79','2018-05-24 12:39:47',0,0,0,0,0,NULL,1792,'workerp','@DBADMINEMAIL@','@DBADMINFNAME@','@DBADMINLNAME@',NULL,'false','false',NULL),('98ec298c-406d-4379-9616-5652f2d05e79','admin',48,'SUPER_USER',NULL,'98ec298c-406d-4379-9616-5652f2d05e79','2018-05-24 12:39:47',0,0,0,0,0,NULL,1792,'adminp','@DBADMINEMAIL@','@DBADMINFNAME@','@DBADMINLNAME@',NULL,'false','false',NULL),('ef6c639a-07e3-4e21-bde3-e2fb61382dc9','vworker',14,'VWORKER_USER',NULL,'98ec298c-406d-4379-9616-5652f2d05e79','2018-05-24 12:39:47',0,0,0,0,0,NULL,1792,'vworkerp','@DBADMINEMAIL@','@DBADMINFNAME@','@DBADMINLNAME@',NULL,'false','false',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_users_insert_rights before insert on users
for each row
begin
  if   new.rights is not null
  then set new.userRightId =
           ( select userRights.userRightId
             from   userRights
             where  userRights.userRightName = new.rights );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_users_update_rights before update on users
for each row
begin
  if   (new.rights is null) and (old.rights is not null)
  then set new.userRightId = null;
  end if;

  if   (  new.rights is not null  )  and
       ( (old.rights is     null) or (old.rights <> new.rights) )
  then set new.userRightId =
           ( select userRights.userRightId
             from   userRights
             where  userRights.userRightName = new.rights );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `users_history`
--

DROP TABLE IF EXISTS `users_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `login` varchar(254) NOT NULL COMMENT 'User login. if your change length, don t forget to change UserInterface.USERLOGINLENGTH',
  `userRightId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'User rights Id. See common/UserRights.java',
  `rights` varchar(254) NOT NULL DEFAULT 'NONE' COMMENT 'User rights (deprecated)',
  `usergroupUID` char(36) DEFAULT NULL COMMENT 'Optionnal. user group UID',
  `ownerUID` char(36) NOT NULL COMMENT 'Owner UID',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `nbJobs` int(15) DEFAULT '0' COMMENT 'Completed jobs counter. updated on work completion',
  `pendingJobs` int(15) DEFAULT '0' COMMENT 'Pending jobs counter. updated on work submission',
  `runningJobs` int(15) DEFAULT '0' COMMENT 'Running jobs counter. updated on work request',
  `errorJobs` int(15) DEFAULT '0' COMMENT 'Error jobs counter. updated on job error',
  `usedCpuTime` bigint(20) DEFAULT '0' COMMENT 'Average execution time. updated on work completion',
  `certificate` text COMMENT 'This is the X.509 proxy file content',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'Since 5.8.0  This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `password` varchar(254) NOT NULL DEFAULT '' COMMENT 'User password',
  `email` varchar(254) NOT NULL DEFAULT '' COMMENT 'User email',
  `fname` varchar(254) DEFAULT NULL COMMENT 'Optionnal, user first name',
  `lname` varchar(254) DEFAULT NULL COMMENT 'Optionnal, user last name',
  `country` varchar(254) DEFAULT NULL COMMENT 'User country',
  `challenging` char(5) DEFAULT 'false' COMMENT 'True if this user connect using private/public keys pair',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if this row has been deleted',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `login` (`login`),
  KEY `userRightId` (`userRightId`),
  KEY `usergroupUID` (`usergroupUID`),
  KEY `ownerUID` (`ownerUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='users = Owners of objects.  Most users may submit works';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_history`
--

LOCK TABLES `users_history` WRITE;
/*!40000 ALTER TABLE `users_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `users_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_users_history_insert_rights before insert on users_history
for each row
begin
  if   new.rights is not null
  then
    set @userRightId =
           ( select userRights.userRightId
             from   userRights
             where  userRights.userRightName = new.rights );
    if (select @userRightId) is not null
    then set new.userRightId = @userRightId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_users_history_update_rights before update on users_history
for each row
begin
  if   (new.rights is null) and (old.rights is not null)
  then set new.userRightId = null;
  end if;

  if   (  new.rights is not null  )  and
       ( (old.rights is     null) or (old.rights <> new.rights) )
  then
    set @userRightId =
           ( select userRights.userRightId
             from   userRights
             where  userRights.userRightName = new.rights );
    if (select @userRightId) is not null
    then set new.userRightId = @userRightId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `version` (
  `version` char(20) DEFAULT NULL,
  `installation` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `version`
--

LOCK TABLES `version` WRITE;
/*!40000 ALTER TABLE `version` DISABLE KEYS */;
/*!40000 ALTER TABLE `version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `versions`
--

DROP TABLE IF EXISTS `versions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `versions` (
  `version` varchar(254) DEFAULT NULL,
  `installation` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='versions = Timestamps of XtremWeb-HEP versions';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `versions`
--

LOCK TABLES `versions` WRITE;
/*!40000 ALTER TABLE `versions` DISABLE KEYS */;
INSERT INTO `versions` VALUES ('13.1.0-SNAPSHOT','2018-05-24 14:39:46'),('13.1.0-SNAPSHOT','2018-05-24 14:39:48');
/*!40000 ALTER TABLE `versions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `view_apps`
--

DROP TABLE IF EXISTS `view_apps`;
/*!50001 DROP VIEW IF EXISTS `view_apps`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_apps` AS SELECT
 1 AS `uid`,
 1 AS `name`,
 1 AS `appTypeName`,
 1 AS `type`,
 1 AS `packageTypeName`,
 1 AS `neededpackages`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `mtime`,
 1 AS `envvars`,
 1 AS `isdeleted`,
 1 AS `isService`,
 1 AS `accessRights`,
 1 AS `avgExecTime`,
 1 AS `minMemory`,
 1 AS `minCPUSpeed`,
 1 AS `minFreeMassStorage`,
 1 AS `price`,
 1 AS `nbJobs`,
 1 AS `pendingJobs`,
 1 AS `runningJobs`,
 1 AS `errorJobs`,
 1 AS `webpage`,
 1 AS `defaultStdinURI`,
 1 AS `baseDirinURI`,
 1 AS `defaultDirinURI`,
 1 AS `launchscriptshuri`,
 1 AS `launchscriptcmduri`,
 1 AS `unloadscriptshuri`,
 1 AS `unloadscriptcmduri`,
 1 AS `errorMsg`,
 1 AS `linux_ix86URI`,
 1 AS `linux_amd64URI`,
 1 AS `linux_arm64URI`,
 1 AS `linux_arm32URI`,
 1 AS `linux_x86_64URI`,
 1 AS `linux_ia64URI`,
 1 AS `linux_ppcURI`,
 1 AS `macos_ix86URI`,
 1 AS `macos_x86_64URI`,
 1 AS `macos_ppcURI`,
 1 AS `win32_ix86URI`,
 1 AS `win32_amd64URI`,
 1 AS `win32_x86_64URI`,
 1 AS `javaURI`,
 1 AS `osf1_alphaURI`,
 1 AS `osf1_sparcURI`,
 1 AS `solaris_alphaURI`,
 1 AS `solaris_sparcURI`,
 1 AS `ldlinux_ix86URI`,
 1 AS `ldlinux_amd64URI`,
 1 AS `ldlinux_arm64URI`,
 1 AS `ldlinux_arm32URI`,
 1 AS `ldlinux_x86_64URI`,
 1 AS `ldlinux_ia64URI`,
 1 AS `ldlinux_ppcURI`,
 1 AS `ldmacos_ix86URI`,
 1 AS `ldmacos_x86_64URI`,
 1 AS `ldmacos_ppcURI`,
 1 AS `ldwin32_ix86URI`,
 1 AS `ldwin32_amd64URI`,
 1 AS `ldwin32_x86_64URI`,
 1 AS `ldosf1_alphaURI`,
 1 AS `ldosf1_sparcURI`,
 1 AS `ldsolaris_alphaURI`,
 1 AS `ldsolaris_sparcURI`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_apps_for_offering`
--

DROP TABLE IF EXISTS `view_apps_for_offering`;
/*!50001 DROP VIEW IF EXISTS `view_apps_for_offering`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_apps_for_offering` AS SELECT
 1 AS `uid`,
 1 AS `name`,
 1 AS `appTypeName`,
 1 AS `owner`,
 1 AS `minFreeMassStorage`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_apps_for_offering_with_file_sizes`
--

DROP TABLE IF EXISTS `view_apps_for_offering_with_file_sizes`;
/*!50001 DROP VIEW IF EXISTS `view_apps_for_offering_with_file_sizes`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_apps_for_offering_with_file_sizes` AS SELECT
 1 AS `uid`,
 1 AS `name`,
 1 AS `appTypeName`,
 1 AS `owner`,
 1 AS `appFilesTotalSize`,
 1 AS `minFreeMassStorage`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_datas`
--

DROP TABLE IF EXISTS `view_datas`;
/*!50001 DROP VIEW IF EXISTS `view_datas`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_datas` AS SELECT
 1 AS `uid`,
 1 AS `workUID`,
 1 AS `package`,
 1 AS `statusName`,
 1 AS `status`,
 1 AS `dataTypeName`,
 1 AS `type`,
 1 AS `osName`,
 1 AS `os`,
 1 AS `osVersion`,
 1 AS `cpuTypeName`,
 1 AS `cpu`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `uri`,
 1 AS `accessRights`,
 1 AS `links`,
 1 AS `accessDate`,
 1 AS `insertionDate`,
 1 AS `shasum`,
 1 AS `size`,
 1 AS `sendToClient`,
 1 AS `replicated`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_executables`
--

DROP TABLE IF EXISTS `view_executables`;
/*!50001 DROP VIEW IF EXISTS `view_executables`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_executables` AS SELECT
 1 AS `executableId`,
 1 AS `application`,
 1 AS `appType`,
 1 AS `dataType`,
 1 AS `osName`,
 1 AS `osVersion`,
 1 AS `cpuType`,
 1 AS `dataName`,
 1 AS `dataSize`,
 1 AS `statusName`,
 1 AS `owner`,
 1 AS `ownergroup`,
 1 AS `dataURI`,
 1 AS `mtime`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_groups`
--

DROP TABLE IF EXISTS `view_groups`;
/*!50001 DROP VIEW IF EXISTS `view_groups`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_groups` AS SELECT
 1 AS `uid`,
 1 AS `session`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `Waiting`,
 1 AS `Pending`,
 1 AS `Running`,
 1 AS `Replicating`,
 1 AS `Error`,
 1 AS `Completed`,
 1 AS `Aborted`,
 1 AS `Lost`,
 1 AS `DataRequest`,
 1 AS `ResultRequest`,
 1 AS `nb_works`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_hosts`
--

DROP TABLE IF EXISTS `view_hosts`;
/*!50001 DROP VIEW IF EXISTS `view_hosts`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_hosts` AS SELECT
 1 AS `uid`,
 1 AS `osId`,
 1 AS `os`,
 1 AS `osversion`,
 1 AS `cpuTypeName`,
 1 AS `cputype`,
 1 AS `projectName`,
 1 AS `project`,
 1 AS `sharedapps`,
 1 AS `sharedpackages`,
 1 AS `shareddatas`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `poolworksize`,
 1 AS `nbJobs`,
 1 AS `pendingJobs`,
 1 AS `runningJobs`,
 1 AS `errorJobs`,
 1 AS `timeOut`,
 1 AS `avgExecTime`,
 1 AS `lastAlive`,
 1 AS `nbconnections`,
 1 AS `natedipaddr`,
 1 AS `ipaddr`,
 1 AS `hwaddr`,
 1 AS `timezone`,
 1 AS `javaversion`,
 1 AS `javadatamodel`,
 1 AS `cpunb`,
 1 AS `cpumodel`,
 1 AS `cpuspeed`,
 1 AS `totalmem`,
 1 AS `availablemem`,
 1 AS `totalswap`,
 1 AS `totaltmp`,
 1 AS `freetmp`,
 1 AS `timeShift`,
 1 AS `avgping`,
 1 AS `nbping`,
 1 AS `uploadbandwidth`,
 1 AS `downloadbandwidth`,
 1 AS `accessRights`,
 1 AS `cpuLoad`,
 1 AS `active`,
 1 AS `available`,
 1 AS `incomingconnections`,
 1 AS `acceptbin`,
 1 AS `version`,
 1 AS `traces`,
 1 AS `isdeleted`,
 1 AS `pilotjob`,
 1 AS `sgid`,
 1 AS `jobid`,
 1 AS `batchid`,
 1 AS `userproxy`,
 1 AS `errorMsg`,
 1 AS `ethwalletaddr`,
 1 AS `marketorderUID`,
 1 AS `contributionstatusId`,
 1 AS `contributionstatus`,
 1 AS `workerpooladdr`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_hosts_matching_works_deployable`
--

DROP TABLE IF EXISTS `view_hosts_matching_works_deployable`;
/*!50001 DROP VIEW IF EXISTS `view_hosts_matching_works_deployable`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_hosts_matching_works_deployable` AS SELECT
 1 AS `workUID`,
 1 AS `workAccessRights`,
 1 AS `workArrivalDate`,
 1 AS `workOwner`,
 1 AS `workUsergroup`,
 1 AS `appName`,
 1 AS `appType`,
 1 AS `executableOsName`,
 1 AS `executableOsVersion`,
 1 AS `executableCpuType`,
 1 AS `hostUID`,
 1 AS `hostAccessRights`,
 1 AS `hostName`,
 1 AS `hostProject`,
 1 AS `hostOwner`,
 1 AS `hostOsName`,
 1 AS `hostCpuType`,
 1 AS `dataUID`,
 1 AS `dataName`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_hosts_matching_works_deployable_and_shared`
--

DROP TABLE IF EXISTS `view_hosts_matching_works_deployable_and_shared`;
/*!50001 DROP VIEW IF EXISTS `view_hosts_matching_works_deployable_and_shared`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_hosts_matching_works_deployable_and_shared` AS SELECT
 1 AS `workUID`,
 1 AS `workAccessRights`,
 1 AS `workArrivalDate`,
 1 AS `workOwner`,
 1 AS `workUsergroup`,
 1 AS `appName`,
 1 AS `appType`,
 1 AS `executableOsName`,
 1 AS `executableOsVersion`,
 1 AS `executableCpuType`,
 1 AS `hostUID`,
 1 AS `hostAccessRights`,
 1 AS `hostName`,
 1 AS `hostProject`,
 1 AS `hostOwner`,
 1 AS `hostOsName`,
 1 AS `hostCpuType`,
 1 AS `dataUID`,
 1 AS `dataName`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_hosts_matching_works_shared`
--

DROP TABLE IF EXISTS `view_hosts_matching_works_shared`;
/*!50001 DROP VIEW IF EXISTS `view_hosts_matching_works_shared`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_hosts_matching_works_shared` AS SELECT
 1 AS `workUID`,
 1 AS `workAccessRights`,
 1 AS `workArrivalDate`,
 1 AS `workOwner`,
 1 AS `workUsergroup`,
 1 AS `appName`,
 1 AS `appType`,
 1 AS `executableOsName`,
 1 AS `executableOsVersion`,
 1 AS `executableCpuType`,
 1 AS `hostUID`,
 1 AS `hostAccessRights`,
 1 AS `hostName`,
 1 AS `hostProject`,
 1 AS `hostOwner`,
 1 AS `hostOsName`,
 1 AS `hostCpuType`,
 1 AS `dataUID`,
 1 AS `dataName`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_sessions`
--

DROP TABLE IF EXISTS `view_sessions`;
/*!50001 DROP VIEW IF EXISTS `view_sessions`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_sessions` AS SELECT
 1 AS `uid`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `Waiting`,
 1 AS `Pending`,
 1 AS `Running`,
 1 AS `Replicating`,
 1 AS `Error`,
 1 AS `Completed`,
 1 AS `Aborted`,
 1 AS `Lost`,
 1 AS `DataRequest`,
 1 AS `ResultRequest`,
 1 AS `nb_works`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_sharedAppTypes`
--

DROP TABLE IF EXISTS `view_sharedAppTypes`;
/*!50001 DROP VIEW IF EXISTS `view_sharedAppTypes`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_sharedAppTypes` AS SELECT
 1 AS `appTypeName`,
 1 AS `hostname`,
 1 AS `ipaddr`,
 1 AS `hwaddr`,
 1 AS `cputype`,
 1 AS `cpunb`,
 1 AS `cpumodel`,
 1 AS `osName`,
 1 AS `osversion`,
 1 AS `mtime`,
 1 AS `appTypeDescription`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_sharedPackageTypes`
--

DROP TABLE IF EXISTS `view_sharedPackageTypes`;
/*!50001 DROP VIEW IF EXISTS `view_sharedPackageTypes`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_sharedPackageTypes` AS SELECT
 1 AS `packageTypeName`,
 1 AS `hostname`,
 1 AS `ipaddr`,
 1 AS `hwaddr`,
 1 AS `cputype`,
 1 AS `cpunb`,
 1 AS `cpumodel`,
 1 AS `osName`,
 1 AS `osversion`,
 1 AS `mtime`,
 1 AS `packageTypeDescription`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_tasks`
--

DROP TABLE IF EXISTS `view_tasks`;
/*!50001 DROP VIEW IF EXISTS `view_tasks`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_tasks` AS SELECT
 1 AS `uid`,
 1 AS `application`,
 1 AS `workUID`,
 1 AS `host`,
 1 AS `statusName`,
 1 AS `status`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `trial`,
 1 AS `InsertionDate`,
 1 AS `StartDate`,
 1 AS `LastStartDate`,
 1 AS `AliveCount`,
 1 AS `LastAlive`,
 1 AS `removalDate`,
 1 AS `duration`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `price`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_traces`
--

DROP TABLE IF EXISTS `view_traces`;
/*!50001 DROP VIEW IF EXISTS `view_traces`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_traces` AS SELECT
 1 AS `uid`,
 1 AS `host`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `mtime`,
 1 AS `login`,
 1 AS `arrivalDate`,
 1 AS `startDate`,
 1 AS `endDate`,
 1 AS `accessRights`,
 1 AS `data`,
 1 AS `isdeleted`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_usergroups`
--

DROP TABLE IF EXISTS `view_usergroups`;
/*!50001 DROP VIEW IF EXISTS `view_usergroups`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_usergroups` AS SELECT
 1 AS `uid`,
 1 AS `label`,
 1 AS `owner`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `webpage`,
 1 AS `project`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users`
--

DROP TABLE IF EXISTS `view_users`;
/*!50001 DROP VIEW IF EXISTS `view_users`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users` AS SELECT
 1 AS `uid`,
 1 AS `login`,
 1 AS `userRightName`,
 1 AS `rights`,
 1 AS `usergroup`,
 1 AS `owner`,
 1 AS `mtime`,
 1 AS `nbJobs`,
 1 AS `pendingJobs`,
 1 AS `runningJobs`,
 1 AS `errorJobs`,
 1 AS `usedCpuTime`,
 1 AS `certificate`,
 1 AS `accessRights`,
 1 AS `password`,
 1 AS `email`,
 1 AS `fname`,
 1 AS `lname`,
 1 AS `country`,
 1 AS `challenging`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_apps`
--

DROP TABLE IF EXISTS `view_users_apps`;
/*!50001 DROP VIEW IF EXISTS `view_users_apps`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_apps` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `name`,
 1 AS `appTypeName`,
 1 AS `type`,
 1 AS `packageTypeName`,
 1 AS `neededpackages`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `mtime`,
 1 AS `envvars`,
 1 AS `isdeleted`,
 1 AS `isService`,
 1 AS `accessRights`,
 1 AS `avgExecTime`,
 1 AS `minMemory`,
 1 AS `minCPUSpeed`,
 1 AS `minFreeMassStorage`,
 1 AS `price`,
 1 AS `nbJobs`,
 1 AS `pendingJobs`,
 1 AS `runningJobs`,
 1 AS `errorJobs`,
 1 AS `webpage`,
 1 AS `defaultStdinURI`,
 1 AS `baseDirinURI`,
 1 AS `defaultDirinURI`,
 1 AS `launchscriptshuri`,
 1 AS `launchscriptcmduri`,
 1 AS `unloadscriptshuri`,
 1 AS `unloadscriptcmduri`,
 1 AS `errorMsg`,
 1 AS `linux_ix86URI`,
 1 AS `linux_amd64URI`,
 1 AS `linux_arm64URI`,
 1 AS `linux_arm32URI`,
 1 AS `linux_x86_64URI`,
 1 AS `linux_ia64URI`,
 1 AS `linux_ppcURI`,
 1 AS `macos_ix86URI`,
 1 AS `macos_x86_64URI`,
 1 AS `macos_ppcURI`,
 1 AS `win32_ix86URI`,
 1 AS `win32_amd64URI`,
 1 AS `win32_x86_64URI`,
 1 AS `javaURI`,
 1 AS `osf1_alphaURI`,
 1 AS `osf1_sparcURI`,
 1 AS `solaris_alphaURI`,
 1 AS `solaris_sparcURI`,
 1 AS `ldlinux_ix86URI`,
 1 AS `ldlinux_amd64URI`,
 1 AS `ldlinux_arm64URI`,
 1 AS `ldlinux_arm32URI`,
 1 AS `ldlinux_x86_64URI`,
 1 AS `ldlinux_ia64URI`,
 1 AS `ldlinux_ppcURI`,
 1 AS `ldmacos_ix86URI`,
 1 AS `ldmacos_x86_64URI`,
 1 AS `ldmacos_ppcURI`,
 1 AS `ldwin32_ix86URI`,
 1 AS `ldwin32_amd64URI`,
 1 AS `ldwin32_x86_64URI`,
 1 AS `ldosf1_alphaURI`,
 1 AS `ldosf1_sparcURI`,
 1 AS `ldsolaris_alphaURI`,
 1 AS `ldsolaris_sparcURI`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_datas`
--

DROP TABLE IF EXISTS `view_users_datas`;
/*!50001 DROP VIEW IF EXISTS `view_users_datas`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_datas` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `workUID`,
 1 AS `package`,
 1 AS `statusName`,
 1 AS `status`,
 1 AS `dataTypeName`,
 1 AS `type`,
 1 AS `osName`,
 1 AS `os`,
 1 AS `osVersion`,
 1 AS `cpuTypeName`,
 1 AS `cpu`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `uri`,
 1 AS `accessRights`,
 1 AS `links`,
 1 AS `accessDate`,
 1 AS `insertionDate`,
 1 AS `shasum`,
 1 AS `size`,
 1 AS `sendToClient`,
 1 AS `replicated`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_groups`
--

DROP TABLE IF EXISTS `view_users_groups`;
/*!50001 DROP VIEW IF EXISTS `view_users_groups`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_groups` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `session`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_hosts`
--

DROP TABLE IF EXISTS `view_users_hosts`;
/*!50001 DROP VIEW IF EXISTS `view_users_hosts`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_hosts` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `osName`,
 1 AS `os`,
 1 AS `osversion`,
 1 AS `cpuTypeName`,
 1 AS `cputype`,
 1 AS `projectName`,
 1 AS `project`,
 1 AS `sharedapps`,
 1 AS `sharedpackages`,
 1 AS `shareddatas`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `poolworksize`,
 1 AS `nbJobs`,
 1 AS `pendingJobs`,
 1 AS `runningJobs`,
 1 AS `errorJobs`,
 1 AS `timeOut`,
 1 AS `avgExecTime`,
 1 AS `lastAlive`,
 1 AS `nbconnections`,
 1 AS `natedipaddr`,
 1 AS `ipaddr`,
 1 AS `hwaddr`,
 1 AS `timezone`,
 1 AS `javaversion`,
 1 AS `javadatamodel`,
 1 AS `cpunb`,
 1 AS `cpumodel`,
 1 AS `cpuspeed`,
 1 AS `totalmem`,
 1 AS `availablemem`,
 1 AS `totalswap`,
 1 AS `totaltmp`,
 1 AS `freetmp`,
 1 AS `timeShift`,
 1 AS `avgping`,
 1 AS `nbping`,
 1 AS `uploadbandwidth`,
 1 AS `downloadbandwidth`,
 1 AS `accessRights`,
 1 AS `cpuLoad`,
 1 AS `active`,
 1 AS `available`,
 1 AS `incomingconnections`,
 1 AS `acceptbin`,
 1 AS `version`,
 1 AS `traces`,
 1 AS `isdeleted`,
 1 AS `pilotjob`,
 1 AS `sgid`,
 1 AS `jobid`,
 1 AS `batchid`,
 1 AS `userproxy`,
 1 AS `errorMsg`,
 1 AS `ethwalletaddr`,
 1 AS `marketorderUID`,
 1 AS `contributionstatusId`,
 1 AS `contributionstatus`,
 1 AS `workerpooladdr`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_sessions`
--

DROP TABLE IF EXISTS `view_users_sessions`;
/*!50001 DROP VIEW IF EXISTS `view_users_sessions`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_sessions` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `name`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_tasks`
--

DROP TABLE IF EXISTS `view_users_tasks`;
/*!50001 DROP VIEW IF EXISTS `view_users_tasks`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_tasks` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `application`,
 1 AS `workUID`,
 1 AS `host`,
 1 AS `statusName`,
 1 AS `status`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `trial`,
 1 AS `InsertionDate`,
 1 AS `StartDate`,
 1 AS `LastStartDate`,
 1 AS `AliveCount`,
 1 AS `LastAlive`,
 1 AS `removalDate`,
 1 AS `duration`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `price`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_traces`
--

DROP TABLE IF EXISTS `view_users_traces`;
/*!50001 DROP VIEW IF EXISTS `view_users_traces`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_traces` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `host`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `mtime`,
 1 AS `login`,
 1 AS `arrivalDate`,
 1 AS `startDate`,
 1 AS `endDate`,
 1 AS `accessRights`,
 1 AS `data`,
 1 AS `isdeleted`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_usergroups`
--

DROP TABLE IF EXISTS `view_users_usergroups`;
/*!50001 DROP VIEW IF EXISTS `view_users_usergroups`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_usergroups` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `label`,
 1 AS `owner`,
 1 AS `mtime`,
 1 AS `accessRights`,
 1 AS `webpage`,
 1 AS `project`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_users`
--

DROP TABLE IF EXISTS `view_users_users`;
/*!50001 DROP VIEW IF EXISTS `view_users_users`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_users` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `login`,
 1 AS `userRightName`,
 1 AS `rights`,
 1 AS `usergroup`,
 1 AS `owner`,
 1 AS `mtime`,
 1 AS `nbJobs`,
 1 AS `pendingJobs`,
 1 AS `runningJobs`,
 1 AS `errorJobs`,
 1 AS `usedCpuTime`,
 1 AS `certificate`,
 1 AS `accessRights`,
 1 AS `password`,
 1 AS `email`,
 1 AS `fname`,
 1 AS `lname`,
 1 AS `country`,
 1 AS `challenging`,
 1 AS `isdeleted`,
 1 AS `errorMsg`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_users_works`
--

DROP TABLE IF EXISTS `view_users_works`;
/*!50001 DROP VIEW IF EXISTS `view_users_works`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_users_works` AS SELECT
 1 AS `userUID`,
 1 AS `userLogin`,
 1 AS `userRight`,
 1 AS `uid`,
 1 AS `application`,
 1 AS `categoryId`,
 1 AS `statusName`,
 1 AS `status`,
 1 AS `session`,
 1 AS `group`,
 1 AS `expectedhost`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `label`,
 1 AS `mtime`,
 1 AS `userproxy`,
 1 AS `accessRights`,
 1 AS `sgid`,
 1 AS `maxRetry`,
 1 AS `retry`,
 1 AS `minMemory`,
 1 AS `minCPUSpeed`,
 1 AS `minFreeMassStorage`,
 1 AS `maxWallClockTime`,
 1 AS `maxFreeMassStorage`,
 1 AS `maxFileSize`,
 1 AS `maxMemory`,
 1 AS `maxCpuSpeed`,
 1 AS `uploadbandwidth`,
 1 AS `downloadbandwidth`,
 1 AS `returnCode`,
 1 AS `server`,
 1 AS `cmdLine`,
 1 AS `listenport`,
 1 AS `smartsocketaddr`,
 1 AS `smartsocketclient`,
 1 AS `stdinURI`,
 1 AS `dirinURI`,
 1 AS `resultURI`,
 1 AS `arrivalDate`,
 1 AS `completedDate`,
 1 AS `resultDate`,
 1 AS `readydate`,
 1 AS `datareadydate`,
 1 AS `compstartdate`,
 1 AS `compenddate`,
 1 AS `error_msg`,
 1 AS `sendToClient`,
 1 AS `local`,
 1 AS `active`,
 1 AS `replicatedUID`,
 1 AS `replications`,
 1 AS `sizer`,
 1 AS `totalr`,
 1 AS `datadrivenURI`,
 1 AS `isService`,
 1 AS `isdeleted`,
 1 AS `envvars`,
 1 AS `errorMsg`,
 1 AS `requester`,
 1 AS `dataset`,
 1 AS `workerPool`,
 1 AS `emitcost`,
 1 AS `callback`,
 1 AS `beneficiary`,
 1 AS `marketorderUID`,
 1 AS `h2h2r`,
 1 AS `h2r`,
 1 AS `workOrderId`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_works`
--

DROP TABLE IF EXISTS `view_works`;
/*!50001 DROP VIEW IF EXISTS `view_works`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_works` AS SELECT
 1 AS `uid`,
 1 AS `application`,
 1 AS `categoryId`,
 1 AS `statusName`,
 1 AS `status`,
 1 AS `session`,
 1 AS `group`,
 1 AS `expectedhost`,
 1 AS `owner`,
 1 AS `usergroup`,
 1 AS `label`,
 1 AS `mtime`,
 1 AS `userproxy`,
 1 AS `accessRights`,
 1 AS `sgid`,
 1 AS `maxRetry`,
 1 AS `retry`,
 1 AS `minMemory`,
 1 AS `minCPUSpeed`,
 1 AS `minFreeMassStorage`,
 1 AS `maxWallClockTime`,
 1 AS `maxFreeMassStorage`,
 1 AS `maxFileSize`,
 1 AS `maxMemory`,
 1 AS `maxCpuSpeed`,
 1 AS `uploadbandwidth`,
 1 AS `downloadbandwidth`,
 1 AS `returnCode`,
 1 AS `server`,
 1 AS `cmdLine`,
 1 AS `listenport`,
 1 AS `smartsocketaddr`,
 1 AS `smartsocketclient`,
 1 AS `stdinURI`,
 1 AS `dirinURI`,
 1 AS `resultURI`,
 1 AS `arrivalDate`,
 1 AS `completedDate`,
 1 AS `resultDate`,
 1 AS `readydate`,
 1 AS `datareadydate`,
 1 AS `compstartdate`,
 1 AS `compenddate`,
 1 AS `error_msg`,
 1 AS `sendToClient`,
 1 AS `local`,
 1 AS `active`,
 1 AS `replicatedUID`,
 1 AS `replications`,
 1 AS `sizer`,
 1 AS `totalr`,
 1 AS `datadrivenURI`,
 1 AS `isService`,
 1 AS `isdeleted`,
 1 AS `envvars`,
 1 AS `errorMsg`,
 1 AS `requester`,
 1 AS `dataset`,
 1 AS `workerPool`,
 1 AS `emitcost`,
 1 AS `callback`,
 1 AS `beneficiary`,
 1 AS `marketorderUID`,
 1 AS `h2h2r`,
 1 AS `h2r`,
 1 AS `workOrderId`,
 1 AS `NULL`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_works_for_billing`
--

DROP TABLE IF EXISTS `view_works_for_billing`;
/*!50001 DROP VIEW IF EXISTS `view_works_for_billing`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_works_for_billing` AS SELECT
 1 AS `uid`,
 1 AS `statusName`,
 1 AS `owner`,
 1 AS `minFreeMassStorage`,
 1 AS `completedDate`,
 1 AS `compDuration`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary table structure for view `view_works_for_billing_with_file_sizes`
--

DROP TABLE IF EXISTS `view_works_for_billing_with_file_sizes`;
/*!50001 DROP VIEW IF EXISTS `view_works_for_billing_with_file_sizes`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `view_works_for_billing_with_file_sizes` AS SELECT
 1 AS `uid`,
 1 AS `statusName`,
 1 AS `owner`,
 1 AS `minFreeMassStorage`,
 1 AS `stdinSize`,
 1 AS `dirinSize`,
 1 AS `resultSize`,
 1 AS `completedDate`,
 1 AS `compDuration`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `works`
--

DROP TABLE IF EXISTS `works`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `appUID` char(36) NOT NULL COMMENT 'Application UID',
  `categoryId` bigint(20) DEFAULT '0' COMMENT 'categoryId. See common/CategoryInterface.java',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Status. see common/XWStatus.java',
  `sessionUID` char(36) DEFAULT NULL COMMENT 'Optionnal. session UID',
  `groupUID` char(36) DEFAULT NULL COMMENT 'Optionnal. group UID (we call it "groupUID" since "group" is a MySql reserved word)  This is not an usergroup but a group !',
  `expectedhostUID` char(36) DEFAULT NULL COMMENT 'Optionnal. expected host UID',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `label` varchar(254) DEFAULT NULL COMMENT 'Optionnal. user label',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `userproxy` varchar(254) DEFAULT NULL COMMENT 'This is the X.509 user proxy URI to identify the owner of this work. this is not a certificate',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `sgid` varchar(254) DEFAULT NULL COMMENT 'external id - xw 7.2.0: Service Grid Identifier set by the DG 2 SG bridge; xw 11.1.0 : blockchain transaction hash',
  `maxRetry` int(3) DEFAULT NULL COMMENT 'How many time should we try to compute',
  `retry` int(3) DEFAULT NULL COMMENT 'How many time have we tried to compute  Since 8.0.0',
  `minMemory` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum memory needed in Kb',
  `minCPUSpeed` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum CPU speed needed in MHz',
  `minFreeMassStorage` bigint(20) DEFAULT '0' COMMENT 'Min free amount of mass storage in Mb',
  `maxWallClockTime` int(10) DEFAULT '300' COMMENT 'Wallclocktime : how many seconds a job can be computed.  The job is stopped as the wall clocktime is reached.  If < 0, the job is not stopped.',
  `maxFreeMassStorage` bigint(20) NOT NULL DEFAULT '5368709120' COMMENT 'Max mass storage usage in bytes; default 5Gb',
  `maxFileSize` bigint(20) NOT NULL DEFAULT '104857600' COMMENT 'Max file length in bytes; default 100Mb',
  `maxMemory` bigint(20) NOT NULL DEFAULT '536870912' COMMENT 'Max RAM usage in bytes; default 512Mb',
  `maxCpuSpeed` float NOT NULL DEFAULT '0.5' COMMENT 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)',
  `uploadbandwidth` float DEFAULT '0' COMMENT 'Upload bandwidth usage (in Mb/s)',
  `downloadbandwidth` float DEFAULT '0' COMMENT 'Download bandwidth usage (in Mb/s)',
  `returnCode` int(3) DEFAULT NULL COMMENT 'Application return code',
  `server` varchar(254) DEFAULT NULL COMMENT 'For replication',
  `cmdLine` text COMMENT 'This work command line to provide to application',
  `listenport` varchar(254) DEFAULT NULL COMMENT 'The job may be a server that needs to listen to some ports  This is a comma separated integer list  Privileged ports (lower than 1024) are ignored  Default : null (no port)  Since 8.0.0',
  `smartsocketaddr` varchar(8190) DEFAULT NULL COMMENT 'The SmartSockets addresses to connect to the listened ports  This is set if listenport != null  This is a comma separated SmartSockets addresses list  Default : null  Since 8.0.0',
  `smartsocketclient` varchar(8190) DEFAULT NULL COMMENT 'This is the column index of a semicolon list containing tuple of   SmartSockets address and local port.  This helps a job running on XWHEP worker side to connect to a server  Like application running on XWHEP client side.  E.g. "Saddr1, port1; Saddr2, por',
  `stdinURI` varchar(254) DEFAULT NULL COMMENT 'Data URI. This is the STDIN. If not set, apps.stdin is used by default  NULLUID may be used to force no STDIN even if apps.stdin is defined  See common/UID.java#NULLUID',
  `dirinURI` varchar(254) DEFAULT NULL COMMENT 'Data URI. This is the DIRIN. If not set, apps.dirin is used by default  NULLURI may be used to force no DIRIN even if apps.dirin is defined  See common/UID.java#NULLUID  This is installed before apps.basedirin to ensure this does not override  Any of the ',
  `resultURI` varchar(254) DEFAULT NULL COMMENT 'Data URI',
  `arrivalDate` datetime DEFAULT NULL COMMENT 'When the server received this work',
  `completedDate` datetime DEFAULT NULL COMMENT 'When this work has been completed',
  `resultDate` datetime DEFAULT NULL COMMENT 'When this work result has been available',
  `readydate` datetime DEFAULT NULL COMMENT 'When this work has been downloaded by the worker  Since 8.0.0',
  `datareadydate` datetime DEFAULT NULL COMMENT 'When this work data have been downloaded by the worker  Since 8.0.0',
  `compstartdate` datetime DEFAULT NULL COMMENT 'When this work has been started on worker  Since 8.0.0',
  `compenddate` datetime DEFAULT NULL COMMENT 'When this work work has been ended on worker  Since 8.0.0',
  `error_msg` varchar(255) DEFAULT NULL COMMENT 'Error message',
  `sendToClient` char(5) DEFAULT 'false' COMMENT 'Used for replication',
  `local` char(5) DEFAULT 'true' COMMENT 'Used for replication',
  `active` char(5) DEFAULT 'true' COMMENT 'Used for replication',
  `replicatedUID` char(36) DEFAULT NULL COMMENT 'The UID of the original work, if this work is a replica',
  `replications` bigint(20) DEFAULT '0' COMMENT 'Optionnal. Amount of expected replications. No replication, if <= 0',
  `sizer` bigint(20) DEFAULT '0' COMMENT 'Optionnal. This is the size of the replica set',
  `totalr` bigint(20) DEFAULT '0' COMMENT 'Optionnal. Current amount of replicas',
  `datadrivenURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. The URI of the data the work drives',
  `isService` char(5) DEFAULT 'false' COMMENT 'Is it a service (see apps.isService)',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if row has been deleted ',
  `envvars` varchar(254) DEFAULT NULL COMMENT 'Opional,  Since 8.0.0',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `requester` varchar(50) DEFAULT NULL COMMENT 'requester is a public key of a blockchain wallet; since 13.1.0',
  `dataset` varchar(50) DEFAULT NULL COMMENT 'dataset is a blockchain smart contract address; since 13.1.0',
  `workerPool` varchar(50) DEFAULT NULL COMMENT 'worker pool is blockchain smart contract address; since 13.1.0',
  `emitcost` bigint(20) DEFAULT NULL COMMENT 'blockchain cost; since 13.1.0',
  `callback` varchar(50) DEFAULT NULL COMMENT 'since 13.1.0',
  `beneficiary` varchar(50) DEFAULT NULL COMMENT 'since 13.1.0',
  `marketorderUID` char(36) DEFAULT NULL COMMENT 'Optional, UID of the market order',
  `h2h2r` varchar(254) DEFAULT NULL COMMENT 'this is the contribution proposal h(h(r)), if this work belongs a market order',
  `h2r` varchar(254) DEFAULT NULL COMMENT 'this is the contribution proof h(r), if this work belongs a market order',
  `workOrderId` varchar(254) DEFAULT NULL COMMENT 'this is the blockchain work order id',
  PRIMARY KEY (`uid`),
  KEY `works_status_active` (`status`,`active`),
  KEY `idx_categoryId` (`categoryId`),
  KEY `idx_workorderId` (`workOrderId`),
  KEY `sgId` (`sgid`),
  KEY `completedDate` (`completedDate`),
  KEY `fk_works_apps` (`appUID`),
  KEY `fk_works_statuses` (`statusId`),
  KEY `fk_works_sessions` (`sessionUID`),
  KEY `fk_works_groups` (`groupUID`),
  KEY `fk_works_hosts` (`expectedhostUID`),
  KEY `fk_works_users` (`ownerUID`),
  CONSTRAINT `fk_works_apps` FOREIGN KEY (`appUID`) REFERENCES `apps` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_works_groups` FOREIGN KEY (`groupUID`) REFERENCES `groups` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_works_hosts` FOREIGN KEY (`expectedhostUID`) REFERENCES `hosts` (`uid`) ON DELETE SET NULL,
  CONSTRAINT `fk_works_sessions` FOREIGN KEY (`sessionUID`) REFERENCES `sessions` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_works_statuses` FOREIGN KEY (`statusId`) REFERENCES `statuses` (`statusId`) ON DELETE CASCADE,
  CONSTRAINT `fk_works_users` FOREIGN KEY (`ownerUID`) REFERENCES `users` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='works = Jobs submitted by a user with app and input data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works`
--

LOCK TABLES `works` WRITE;
/*!40000 ALTER TABLE `works` DISABLE KEYS */;
/*!40000 ALTER TABLE `works` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_works_insert_status before insert on works
for each row
begin
  if   new.status is not null
  then set new.statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_works_update_status before update on works
for each row
begin
  if   (new.status is null) and (old.status is not null)
  then set new.statusId = null;
  end if;

  if   (  new.status is not null  )  and
       ( (old.status is     null) or (old.status <> new.status) )
  then set new.statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `works_history`
--

DROP TABLE IF EXISTS `works_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `works_history` (
  `uid` char(36) NOT NULL COMMENT 'Primary key',
  `appUID` char(36) NOT NULL COMMENT 'Application UID',
  `categoryId` bigint(20) DEFAULT '0' COMMENT 'categoryId. See common/CategoryInterface.java',
  `statusId` tinyint(3) unsigned NOT NULL DEFAULT '255' COMMENT 'Status Id. See common/XWStatus.java',
  `status` varchar(36) NOT NULL DEFAULT 'NONE' COMMENT 'Status. see common/XWStatus.java',
  `sessionUID` char(36) DEFAULT NULL COMMENT 'Optionnal. session UID',
  `groupUID` char(36) DEFAULT NULL COMMENT 'Optionnal. group UID (we call it "groupUID" since "group" is a MySql reserved word)  This is not an usergroup but a group !',
  `expectedhostUID` char(36) DEFAULT NULL COMMENT 'Optionnal. expected host UID',
  `ownerUID` char(36) NOT NULL COMMENT 'User UID',
  `label` varchar(254) DEFAULT NULL COMMENT 'Optionnal. user label',
  `mtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp of last update',
  `userproxy` varchar(254) DEFAULT NULL COMMENT 'This is the X.509 user proxy URI to identify the owner of this work. this is not a certificate',
  `accessRights` int(4) DEFAULT '1792' COMMENT 'This defines access rights "a la" linux FS  See xtremweb.common.XWAccessRights.java',
  `sgid` varchar(254) DEFAULT NULL COMMENT 'external id - xw 7.2.0: Service Grid Identifier set by the DG 2 SG bridge; xw 11.1.0 : blockchain transaction hash',
  `maxRetry` int(3) DEFAULT NULL COMMENT 'How many time should we try to compute',
  `retry` int(3) DEFAULT NULL COMMENT 'How many time have we tried to compute  Since 8.0.0',
  `minMemory` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum memory needed in Kb',
  `minCPUSpeed` int(10) DEFAULT '0' COMMENT 'Optionnal. minimum CPU speed needed in MHz',
  `minFreeMassStorage` bigint(20) DEFAULT '0' COMMENT 'Min free amount of mass storage in Mb',
  `maxWallClockTime` int(10) DEFAULT '300' COMMENT 'Wallclocktime : how many seconds a job can be computed.  The job is stopped as the wall clocktime is reached.  If < 0, the job is not stopped.',
  `maxFreeMassStorage` bigint(20) NOT NULL DEFAULT '5368709120' COMMENT 'Max mass storage usage in bytes; default 5Gb',
  `maxFileSize` bigint(20) NOT NULL DEFAULT '104857600' COMMENT 'Max file length in bytes; default 100Mb',
  `maxMemory` bigint(20) NOT NULL DEFAULT '536870912' COMMENT 'Max RAM usage in bytes; default 512Mb',
  `maxCpuSpeed` float NOT NULL DEFAULT '0.5' COMMENT 'Max CPU usage in percentage; default 50% (https://docs.docker.com/engine/reference/run/#cpu-period-constraint)',
  `uploadbandwidth` float DEFAULT '0' COMMENT 'Upload bandwidth usage (in Mb/s)',
  `downloadbandwidth` float DEFAULT '0' COMMENT 'Download bandwidth usage (in Mb/s)',
  `returnCode` int(3) DEFAULT NULL COMMENT 'Application return code',
  `server` varchar(254) DEFAULT NULL COMMENT 'For replication',
  `cmdLine` text COMMENT 'This work command line to provide to application',
  `listenport` varchar(254) DEFAULT NULL COMMENT 'The job may be a server that needs to listen to some ports  This is a comma separated integer list  Privileged ports (lower than 1024) are ignored  Default : null (no port)  Since 8.0.0',
  `smartsocketaddr` varchar(8190) DEFAULT NULL COMMENT 'The SmartSockets addresses to connect to the listened ports  This is set if listenport != null  This is a comma separated SmartSockets addresses list  Default : null  Since 8.0.0',
  `smartsocketclient` varchar(8190) DEFAULT NULL COMMENT 'This is the column index of a semicolon list containing tuple of   SmartSockets address and local port.  This helps a job running on XWHEP worker side to connect to a server  Like application running on XWHEP client side.  E.g. "Saddr1, port1; Saddr2, por',
  `stdinURI` varchar(254) DEFAULT NULL COMMENT 'Data URI. This is the STDIN. If not set, apps.stdin is used by default  NULLUID may be used to force no STDIN even if apps.stdin is defined  See common/UID.java#NULLUID',
  `dirinURI` varchar(254) DEFAULT NULL COMMENT 'Data URI. This is the DIRIN. If not set, apps.dirin is used by default  NULLURI may be used to force no DIRIN even if apps.dirin is defined  See common/UID.java#NULLUID  This is installed before apps.basedirin to ensure this does not override  Any of the ',
  `resultURI` varchar(254) DEFAULT NULL COMMENT 'Data URI',
  `arrivalDate` datetime DEFAULT NULL COMMENT 'When the server received this work',
  `completedDate` datetime DEFAULT NULL COMMENT 'When this work has been completed',
  `resultDate` datetime DEFAULT NULL COMMENT 'When this work result has been available',
  `readydate` datetime DEFAULT NULL COMMENT 'When this work has been downloaded by the worker  Since 8.0.0',
  `datareadydate` datetime DEFAULT NULL COMMENT 'When this work data have been downloaded by the worker  Since 8.0.0',
  `compstartdate` datetime DEFAULT NULL COMMENT 'When this work has been started on worker  Since 8.0.0',
  `compenddate` datetime DEFAULT NULL COMMENT 'When this work work has been ended on worker  Since 8.0.0',
  `error_msg` varchar(255) DEFAULT NULL COMMENT 'Error message',
  `sendToClient` char(5) DEFAULT 'false' COMMENT 'Used for replication',
  `local` char(5) DEFAULT 'true' COMMENT 'Used for replication',
  `active` char(5) DEFAULT 'true' COMMENT 'Used for replication',
  `replicatedUID` char(36) DEFAULT NULL COMMENT 'The UID of the original work, if this work is a replica',
  `replications` bigint(20) DEFAULT '0' COMMENT 'Optionnal. Amount of expected replications. No replication, if <= 0',
  `sizer` bigint(20) DEFAULT '0' COMMENT 'Optionnal. This is the size of the replica set',
  `totalr` bigint(20) DEFAULT '0' COMMENT 'Optionnal. Current amount of replicas',
  `datadrivenURI` varchar(254) DEFAULT NULL COMMENT 'Optionnal. The URI of the data the work drives',
  `isService` char(5) DEFAULT 'false' COMMENT 'Is it a service (see apps.isService)',
  `isdeleted` char(5) DEFAULT 'false' COMMENT 'True if row has been deleted ',
  `envvars` varchar(254) DEFAULT NULL COMMENT 'Opional,  Since 8.0.0',
  `errorMsg` varchar(254) DEFAULT NULL COMMENT 'Error message',
  `requester` varchar(50) DEFAULT NULL COMMENT 'requester is a public key of a blockchain wallet; since 13.1.0',
  `dataset` varchar(50) DEFAULT NULL COMMENT 'dataset is a blockchain smart contract address; since 13.1.0',
  `workerPool` varchar(50) DEFAULT NULL COMMENT 'worker pool is blockchain smart contract address; since 13.1.0',
  `emitcost` bigint(20) DEFAULT NULL COMMENT 'blockchain cost; since 13.1.0',
  `callback` varchar(50) DEFAULT NULL COMMENT 'since 13.1.0',
  `beneficiary` varchar(50) DEFAULT NULL COMMENT 'since 13.1.0',
  `marketorderUID` char(36) DEFAULT NULL COMMENT 'Optional, UID of the market order',
  `h2h2r` varchar(254) DEFAULT NULL COMMENT 'this is the contribution proposal h(h(r)), if this work belongs a market order',
  `h2r` varchar(254) DEFAULT NULL COMMENT 'this is the contribution proof h(r), if this work belongs a market order',
  `workOrderId` varchar(254) DEFAULT NULL COMMENT 'this is the blockchain work order id',
  PRIMARY KEY (`uid`),
  KEY `works_status_active` (`status`,`active`),
  KEY `appUID` (`appUID`),
  KEY `statusId` (`statusId`),
  KEY `sessionUID` (`sessionUID`),
  KEY `groupUID` (`groupUID`),
  KEY `expectedhostUID` (`expectedhostUID`),
  KEY `ownerUID` (`ownerUID`),
  KEY `idx_categoryId` (`categoryId`),
  KEY `idx_workorderId` (`workOrderId`),
  KEY `sgId` (`sgid`),
  KEY `completedDate` (`completedDate`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='works = Jobs submitted by a user with app and input data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works_history`
--

LOCK TABLES `works_history` WRITE;
/*!40000 ALTER TABLE `works_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `works_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_works_history_insert_status before insert on works_history
for each row
begin
  if   new.status is not null
  then
    set @statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
    if (select @statusId) is not null
    then set new.statusId = @statusId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 trigger trig_works_history_update_status before update on works_history
for each row
begin
  if   (new.status is null) and (old.status is not null)
  then set new.statusId = null;
  end if;

  if   (  new.status is not null  )  and
       ( (old.status is     null) or (old.status <> new.status) )
  then
    set @statusId =
           ( select statuses.statusId
             from   statuses
             where  statuses.statusName = new.status );
    if (select @statusId) is not null
    then set new.statusId = @statusId;
    end if;
  end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping routines for database 'iexec'
--
/*!50003 DROP PROCEDURE IF EXISTS `proc_delete_app_hist_os_cpu_uri_in_executables_hist` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_delete_app_hist_os_cpu_uri_in_executables_hist`( in APP_UID       char(36),
  in DATA_TYPE     varchar(254),
  in DATA_OS       char(7),
  in DATA_CPU      char(7),
  in DATA_URI_OLD  varchar(254),
  in DATA_URI_NEW  varchar(254) )
begin
  if   (  DATA_URI_OLD is not null  ) and
       ( (DATA_URI_NEW is     null) or
         (DATA_URI_NEW <> DATA_URI_OLD) )
  then
    delete     executables_history
    from       executables_history
    inner join dataTypes on dataTypes.dataTypeId = executables_history.dataTypeId
    inner join oses      on oses.osId            = executables_history.osId
    inner join cpuTypes  on cpuTypes.cpuTypeId   = executables_history.cpuTypeId
    where      (executables_history.appUID       = APP_UID)   and
               (dataTypes.dataTypeName           = DATA_TYPE) and
               (oses.osName                      = DATA_OS)   and
               (cpuTypes.cpuTypeName             = DATA_CPU)  and
               (executables_history.dataURI      = DATA_URI_OLD);
  end if;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_delete_app_os_cpu_uri_in_executables` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_delete_app_os_cpu_uri_in_executables`( in APP_UID       char(36),
  in DATA_TYPE     varchar(254),
  in DATA_OS       char(7),
  in DATA_CPU      char(7),
  in DATA_URI_OLD  varchar(254),
  in DATA_URI_NEW  varchar(254) )
begin
  if   (  DATA_URI_OLD is not null  ) and
       ( (DATA_URI_NEW is     null) or
         (DATA_URI_NEW <> DATA_URI_OLD) )
  then
    delete     executables
    from       executables
    inner join dataTypes on dataTypes.dataTypeId = executables.dataTypeId
    inner join oses      on oses.osId            = executables.osId
    inner join cpuTypes  on cpuTypes.cpuTypeId   = executables.cpuTypeId
    where      (executables.appUID     = APP_UID)   and
               (dataTypes.dataTypeName = DATA_TYPE) and
               (oses.osName            = DATA_OS)   and
               (cpuTypes.cpuTypeName   = DATA_CPU)  and
               (executables.dataURI    = DATA_URI_OLD);
  end if;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_insert_app_hist_os_cpu_uri_in_executables_hist` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_insert_app_hist_os_cpu_uri_in_executables_hist`( in APP_UID   char(36),
  in DATA_TYPE varchar(254),
  in DATA_OS   char(7),
  in DATA_CPU  char(7),
  in DATA_URI  varchar(254) )
begin
  if  DATA_URI is not null
  then
    set  @DATA_UID = right(DATA_URI, 36);

    if   @DATA_UID not in (select datas.uid from datas)
    then set @DATA_UID = null;
    end if;

    insert into executables_history ( appUID, dataTypeId, osId, cpuTypeId, dataUID, dataURI )
    select APP_UID,
           dataTypes.dataTypeId,
           oses.osId,
           cpuTypes.cpuTypeId,
           @DATA_UID,
           DATA_URI
    from   dataTypes,
           oses,
           cpuTypes
    where  (dataTypes.dataTypeName = DATA_TYPE) and
           (oses.osName            = DATA_OS)   and
           (cpuTypes.cpuTypeName   = DATA_CPU);
  end if;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_insert_app_os_cpu_uri_in_executables` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_insert_app_os_cpu_uri_in_executables`( in APP_UID   char(36),
  in DATA_TYPE varchar(254),
  in DATA_OS   char(7),
  in DATA_CPU  char(7),
  in DATA_URI  varchar(254) )
begin
  if  DATA_URI is not null
  then
    set  @DATA_UID = right(DATA_URI, 36);

    if   @DATA_UID not in (select datas.uid from datas)
    then set @DATA_UID = null;
    end if;

    insert into executables ( appUID, dataTypeId, osId, cpuTypeId, dataUID, dataURI )
    select APP_UID,
           dataTypes.dataTypeId,
           oses.osId,
           cpuTypes.cpuTypeId,
           @DATA_UID,
           DATA_URI
    from   dataTypes,
           oses,
           cpuTypes
    where  (dataTypes.dataTypeName = DATA_TYPE) and
           (oses.osName            = DATA_OS)   and
           (cpuTypes.cpuTypeName   = DATA_CPU);
  end if;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_update_app_hist_os_cpu_uri_in_executables_hist` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_update_app_hist_os_cpu_uri_in_executables_hist`( in APP_UID       char(36),
  in DATA_TYPE     varchar(254),
  in DATA_OS       char(7),
  in DATA_CPU      char(7),
  in DATA_URI_OLD  varchar(254),
  in DATA_URI_NEW  varchar(254) )
begin
  if   (  DATA_URI_NEW is not null  ) and
       ( (DATA_URI_OLD is     null) or
         (DATA_URI_OLD <> DATA_URI_NEW) )
  then
    set  @DATA_UID = right(DATA_URI_NEW, 36);

    if   @DATA_UID not in (select datas.uid from datas)
    then set @DATA_UID = null;
    end if;

    insert into executables_history ( appUID, dataTypeId, osId, cpuTypeId, dataUID, dataURI )
    select APP_UID,
           dataTypes.dataTypeId,
           oses.osId,
           cpuTypes.cpuTypeId,
           @DATA_UID,
           DATA_URI_NEW
    from   dataTypes,
           oses,
           cpuTypes
    where  (dataTypes.dataTypeName = DATA_TYPE) and
           (oses.osName            = DATA_OS)   and
           (cpuTypes.cpuTypeName   = DATA_CPU);
  end if;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `proc_update_app_os_cpu_uri_in_executables` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `proc_update_app_os_cpu_uri_in_executables`( in APP_UID       char(36),
  in DATA_TYPE     varchar(254),
  in DATA_OS       char(7),
  in DATA_CPU      char(7),
  in DATA_URI_OLD  varchar(254),
  in DATA_URI_NEW  varchar(254) )
begin
  if   (  DATA_URI_NEW is not null  ) and
       ( (DATA_URI_OLD is     null) or
         (DATA_URI_OLD <> DATA_URI_NEW) )
  then
    set  @DATA_UID = right(DATA_URI_NEW, 36);

    if   @DATA_UID not in (select datas.uid from datas)
    then set @DATA_UID = null;
    end if;

    insert into executables ( appUID, dataTypeId, osId, cpuTypeId, dataUID, dataURI )
    select APP_UID,
           dataTypes.dataTypeId,
           oses.osId,
           cpuTypes.cpuTypeId,
           @DATA_UID,
           DATA_URI_NEW
    from   dataTypes,
           oses,
           cpuTypes
    where  (dataTypes.dataTypeName = DATA_TYPE) and
           (oses.osName            = DATA_OS)   and
           (cpuTypes.cpuTypeName   = DATA_CPU);
  end if;
end ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `view_apps`
--

/*!50001 DROP VIEW IF EXISTS `view_apps`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_apps` AS select `apps`.`uid` AS `uid`,`apps`.`name` AS `name`,`appTypes`.`appTypeName` AS `appTypeName`,`apps`.`type` AS `type`,`packageTypes`.`packageTypeName` AS `packageTypeName`,`apps`.`neededpackages` AS `neededpackages`,`users`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`apps`.`mtime` AS `mtime`,`apps`.`envvars` AS `envvars`,`apps`.`isdeleted` AS `isdeleted`,`apps`.`isService` AS `isService`,`apps`.`accessRights` AS `accessRights`,`apps`.`avgExecTime` AS `avgExecTime`,`apps`.`minMemory` AS `minMemory`,`apps`.`minCPUSpeed` AS `minCPUSpeed`,`apps`.`minFreeMassStorage` AS `minFreeMassStorage`,`apps`.`price` AS `price`,`apps`.`nbJobs` AS `nbJobs`,`apps`.`pendingJobs` AS `pendingJobs`,`apps`.`runningJobs` AS `runningJobs`,`apps`.`errorJobs` AS `errorJobs`,`apps`.`webpage` AS `webpage`,`apps`.`defaultStdinURI` AS `defaultStdinURI`,`apps`.`baseDirinURI` AS `baseDirinURI`,`apps`.`defaultDirinURI` AS `defaultDirinURI`,`apps`.`launchscriptshuri` AS `launchscriptshuri`,`apps`.`launchscriptcmduri` AS `launchscriptcmduri`,`apps`.`unloadscriptshuri` AS `unloadscriptshuri`,`apps`.`unloadscriptcmduri` AS `unloadscriptcmduri`,`apps`.`errorMsg` AS `errorMsg`,`apps`.`linux_ix86URI` AS `linux_ix86URI`,`apps`.`linux_amd64URI` AS `linux_amd64URI`,`apps`.`linux_arm64URI` AS `linux_arm64URI`,`apps`.`linux_arm32URI` AS `linux_arm32URI`,`apps`.`linux_x86_64URI` AS `linux_x86_64URI`,`apps`.`linux_ia64URI` AS `linux_ia64URI`,`apps`.`linux_ppcURI` AS `linux_ppcURI`,`apps`.`macos_ix86URI` AS `macos_ix86URI`,`apps`.`macos_x86_64URI` AS `macos_x86_64URI`,`apps`.`macos_ppcURI` AS `macos_ppcURI`,`apps`.`win32_ix86URI` AS `win32_ix86URI`,`apps`.`win32_amd64URI` AS `win32_amd64URI`,`apps`.`win32_x86_64URI` AS `win32_x86_64URI`,`apps`.`javaURI` AS `javaURI`,`apps`.`osf1_alphaURI` AS `osf1_alphaURI`,`apps`.`osf1_sparcURI` AS `osf1_sparcURI`,`apps`.`solaris_alphaURI` AS `solaris_alphaURI`,`apps`.`solaris_sparcURI` AS `solaris_sparcURI`,`apps`.`ldlinux_ix86URI` AS `ldlinux_ix86URI`,`apps`.`ldlinux_amd64URI` AS `ldlinux_amd64URI`,`apps`.`ldlinux_arm64URI` AS `ldlinux_arm64URI`,`apps`.`ldlinux_arm32URI` AS `ldlinux_arm32URI`,`apps`.`ldlinux_x86_64URI` AS `ldlinux_x86_64URI`,`apps`.`ldlinux_ia64URI` AS `ldlinux_ia64URI`,`apps`.`ldlinux_ppcURI` AS `ldlinux_ppcURI`,`apps`.`ldmacos_ix86URI` AS `ldmacos_ix86URI`,`apps`.`ldmacos_x86_64URI` AS `ldmacos_x86_64URI`,`apps`.`ldmacos_ppcURI` AS `ldmacos_ppcURI`,`apps`.`ldwin32_ix86URI` AS `ldwin32_ix86URI`,`apps`.`ldwin32_amd64URI` AS `ldwin32_amd64URI`,`apps`.`ldwin32_x86_64URI` AS `ldwin32_x86_64URI`,`apps`.`ldosf1_alphaURI` AS `ldosf1_alphaURI`,`apps`.`ldosf1_sparcURI` AS `ldosf1_sparcURI`,`apps`.`ldsolaris_alphaURI` AS `ldsolaris_alphaURI`,`apps`.`ldsolaris_sparcURI` AS `ldsolaris_sparcURI`,NULL AS `NULL` from ((((`apps` left join `appTypes` on((`apps`.`appTypeId` = `appTypes`.`appTypeId`))) left join `packageTypes` on((`apps`.`packageTypeId` = `packageTypes`.`packageTypeId`))) left join `users` on((`apps`.`ownerUID` = `users`.`uid`))) left join `usergroups` on((`users`.`usergroupUID` = `usergroups`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_apps_for_offering`
--

/*!50001 DROP VIEW IF EXISTS `view_apps_for_offering`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_apps_for_offering` AS select `apps`.`uid` AS `uid`,`apps`.`name` AS `name`,`appTypes`.`appTypeName` AS `appTypeName`,`users`.`login` AS `owner`,`apps`.`minFreeMassStorage` AS `minFreeMassStorage` from ((`apps` left join `appTypes` on((`apps`.`appTypeId` = `appTypes`.`appTypeId`))) left join `users` on((`apps`.`ownerUID` = `users`.`uid`))) group by `apps`.`uid`,`apps`.`name`,`appTypes`.`appTypeName`,`owner` order by `apps`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_apps_for_offering_with_file_sizes`
--

/*!50001 DROP VIEW IF EXISTS `view_apps_for_offering_with_file_sizes`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_apps_for_offering_with_file_sizes` AS select `apps`.`uid` AS `uid`,`apps`.`name` AS `name`,`appTypes`.`appTypeName` AS `appTypeName`,`users`.`login` AS `owner`,(((ifnull(`datas1`.`size`,0) + ifnull(`datas2`.`size`,0)) + ifnull(`datas3`.`size`,0)) + ifnull(max(`datas4`.`size`),0)) AS `appFilesTotalSize`,`apps`.`minFreeMassStorage` AS `minFreeMassStorage` from (((((((`apps` left join `appTypes` on((`apps`.`appTypeId` = `appTypes`.`appTypeId`))) left join `users` on((`apps`.`ownerUID` = `users`.`uid`))) left join `datas` `datas1` on((`datas1`.`uid` = right(`apps`.`defaultStdinURI`,36)))) left join `datas` `datas2` on((`datas2`.`uid` = right(`apps`.`baseDirinURI`,36)))) left join `datas` `datas3` on((`datas3`.`uid` = right(`apps`.`defaultDirinURI`,36)))) left join `executables` on((`apps`.`uid` = `executables`.`appUID`))) left join `datas` `datas4` on((`datas4`.`uid` = `executables`.`dataUID`))) group by `apps`.`uid`,`apps`.`name`,`appTypes`.`appTypeName`,`owner` order by `apps`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_datas`
--

/*!50001 DROP VIEW IF EXISTS `view_datas`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_datas` AS select `datas`.`uid` AS `uid`,`datas`.`workUID` AS `workUID`,`datas`.`package` AS `package`,`statuses`.`statusName` AS `statusName`,`datas`.`status` AS `status`,`dataTypes`.`dataTypeName` AS `dataTypeName`,`datas`.`type` AS `type`,`oses`.`osName` AS `osName`,`datas`.`os` AS `os`,`datas`.`osVersion` AS `osVersion`,`cpuTypes`.`cpuTypeName` AS `cpuTypeName`,`datas`.`cpu` AS `cpu`,`users`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`datas`.`name` AS `name`,`datas`.`mtime` AS `mtime`,`datas`.`uri` AS `uri`,`datas`.`accessRights` AS `accessRights`,`datas`.`links` AS `links`,`datas`.`accessDate` AS `accessDate`,`datas`.`insertionDate` AS `insertionDate`,`datas`.`shasum` AS `shasum`,`datas`.`size` AS `size`,`datas`.`sendToClient` AS `sendToClient`,`datas`.`replicated` AS `replicated`,`datas`.`isdeleted` AS `isdeleted`,`datas`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from ((((((`datas` left join `statuses` on((`datas`.`statusId` = `statuses`.`statusId`))) left join `dataTypes` on((`datas`.`dataTypeId` = `dataTypes`.`dataTypeId`))) left join `oses` on((`datas`.`osId` = `oses`.`osId`))) left join `cpuTypes` on((`datas`.`cpuTypeId` = `cpuTypes`.`cpuTypeId`))) left join `users` on((`datas`.`ownerUID` = `users`.`uid`))) left join `usergroups` on((`users`.`usergroupUID` = `usergroups`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_executables`
--

/*!50001 DROP VIEW IF EXISTS `view_executables`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_executables` AS select `executables`.`executableId` AS `executableId`,`apps`.`name` AS `application`,`appTypes`.`appTypeName` AS `appType`,`dataTypes`.`dataTypeName` AS `dataType`,`oses`.`osName` AS `osName`,`executables`.`osVersion` AS `osVersion`,`cpuTypes`.`cpuTypeName` AS `cpuType`,`datas`.`name` AS `dataName`,`datas`.`size` AS `dataSize`,`statuses`.`statusName` AS `statusName`,`users`.`login` AS `owner`,`usergroups`.`label` AS `ownergroup`,`executables`.`dataURI` AS `dataURI`,`executables`.`mtime` AS `mtime` from (((((((((`executables` left join `apps` on((`apps`.`uid` = `executables`.`appUID`))) left join `appTypes` on((`appTypes`.`appTypeId` = `apps`.`appTypeId`))) left join `dataTypes` on((`dataTypes`.`dataTypeId` = `executables`.`dataTypeId`))) left join `oses` on((`oses`.`osId` = `executables`.`osId`))) left join `cpuTypes` on((`cpuTypes`.`cpuTypeId` = `executables`.`cpuTypeId`))) left join `datas` on((`datas`.`uid` = `executables`.`dataUID`))) left join `statuses` on((`statuses`.`statusId` = `datas`.`statusId`))) left join `users` on((`users`.`uid` = `apps`.`ownerUID`))) left join `usergroups` on((`usergroups`.`uid` = `users`.`usergroupUID`))) order by `apps`.`name`,`appTypes`.`appTypeName`,`dataTypes`.`dataTypeName`,`oses`.`osName`,`executables`.`osVersion`,`cpuTypes`.`cpuTypeName`,`datas`.`name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_groups`
--

/*!50001 DROP VIEW IF EXISTS `view_groups`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_groups` AS select `groups`.`uid` AS `uid`,`sessions`.`name` AS `session`,`users`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`groups`.`name` AS `name`,`groups`.`mtime` AS `mtime`,`groups`.`accessRights` AS `accessRights`,`groups`.`isdeleted` AS `isdeleted`,`groups`.`errorMsg` AS `errorMsg`,sum((`works`.`status` = 'WAITING')) AS `Waiting`,sum((`works`.`status` = 'PENDING')) AS `Pending`,sum((`works`.`status` = 'RUNNING')) AS `Running`,sum((`works`.`status` = 'REPLICATING')) AS `Replicating`,sum((`works`.`status` = 'ERROR')) AS `Error`,sum((`works`.`status` = 'COMPLETED')) AS `Completed`,sum((`works`.`status` = 'ABORTED')) AS `Aborted`,sum((`works`.`status` = 'LOST')) AS `Lost`,sum((`works`.`status` = 'DATAREQUEST')) AS `DataRequest`,sum((`works`.`status` = 'RESULTREQUEST')) AS `ResultRequest`,count(`works`.`uid`) AS `nb_works` from ((((`groups` left join `users` on((`groups`.`ownerUID` = `users`.`uid`))) left join `sessions` on((`groups`.`sessionUID` = `sessions`.`uid`))) left join `usergroups` on((`users`.`usergroupUID` = `usergroups`.`uid`))) left join `works` on((`groups`.`uid` = `works`.`groupUID`))) group by `groups`.`uid`,`session`,`owner`,`usergroup`,`groups`.`name`,`groups`.`mtime`,`groups`.`accessRights`,`groups`.`isdeleted`,`groups`.`errorMsg` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_hosts`
--

/*!50001 DROP VIEW IF EXISTS `view_hosts`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_hosts` AS select `hosts`.`uid` AS `uid`,`hosts`.`osId` AS `osId`,`hosts`.`os` AS `os`,`hosts`.`osversion` AS `osversion`,`cpuTypes`.`cpuTypeName` AS `cpuTypeName`,`hosts`.`cputype` AS `cputype`,`usergroups1`.`label` AS `projectName`,`hosts`.`project` AS `project`,`hosts`.`sharedapps` AS `sharedapps`,`hosts`.`sharedpackages` AS `sharedpackages`,`hosts`.`shareddatas` AS `shareddatas`,`users`.`login` AS `owner`,`usergroups2`.`label` AS `usergroup`,`hosts`.`name` AS `name`,`hosts`.`mtime` AS `mtime`,`hosts`.`poolworksize` AS `poolworksize`,`hosts`.`nbJobs` AS `nbJobs`,`hosts`.`pendingJobs` AS `pendingJobs`,`hosts`.`runningJobs` AS `runningJobs`,`hosts`.`errorJobs` AS `errorJobs`,`hosts`.`timeOut` AS `timeOut`,`hosts`.`avgExecTime` AS `avgExecTime`,`hosts`.`lastAlive` AS `lastAlive`,`hosts`.`nbconnections` AS `nbconnections`,`hosts`.`natedipaddr` AS `natedipaddr`,`hosts`.`ipaddr` AS `ipaddr`,`hosts`.`hwaddr` AS `hwaddr`,`hosts`.`timezone` AS `timezone`,`hosts`.`javaversion` AS `javaversion`,`hosts`.`javadatamodel` AS `javadatamodel`,`hosts`.`cpunb` AS `cpunb`,`hosts`.`cpumodel` AS `cpumodel`,`hosts`.`cpuspeed` AS `cpuspeed`,`hosts`.`totalmem` AS `totalmem`,`hosts`.`availablemem` AS `availablemem`,`hosts`.`totalswap` AS `totalswap`,`hosts`.`totaltmp` AS `totaltmp`,`hosts`.`freetmp` AS `freetmp`,`hosts`.`timeShift` AS `timeShift`,`hosts`.`avgping` AS `avgping`,`hosts`.`nbping` AS `nbping`,`hosts`.`uploadbandwidth` AS `uploadbandwidth`,`hosts`.`downloadbandwidth` AS `downloadbandwidth`,`hosts`.`accessRights` AS `accessRights`,`hosts`.`cpuLoad` AS `cpuLoad`,`hosts`.`active` AS `active`,`hosts`.`available` AS `available`,`hosts`.`incomingconnections` AS `incomingconnections`,`hosts`.`acceptbin` AS `acceptbin`,`hosts`.`version` AS `version`,`hosts`.`traces` AS `traces`,`hosts`.`isdeleted` AS `isdeleted`,`hosts`.`pilotjob` AS `pilotjob`,`hosts`.`sgid` AS `sgid`,`hosts`.`jobid` AS `jobid`,`hosts`.`batchid` AS `batchid`,`hosts`.`userproxy` AS `userproxy`,`hosts`.`errorMsg` AS `errorMsg`,`hosts`.`ethwalletaddr` AS `ethwalletaddr`,`hosts`.`marketorderUID` AS `marketorderUID`,`hosts`.`contributionstatusId` AS `contributionstatusId`,`hosts`.`contributionstatus` AS `contributionstatus`,`hosts`.`workerpooladdr` AS `workerpooladdr`,NULL AS `NULL` from (((((`hosts` left join `oses` on((`hosts`.`osId` = `oses`.`osId`))) left join `cpuTypes` on((`hosts`.`cpuTypeId` = `cpuTypes`.`cpuTypeId`))) left join `usergroups` `usergroups1` on((`hosts`.`usergroupUID` = `usergroups1`.`uid`))) left join `users` on((`hosts`.`ownerUID` = `users`.`uid`))) left join `usergroups` `usergroups2` on((`users`.`usergroupUID` = `usergroups2`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_hosts_matching_works_deployable`
--

/*!50001 DROP VIEW IF EXISTS `view_hosts_matching_works_deployable`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_hosts_matching_works_deployable` AS select `works`.`uid` AS `workUID`,`works`.`accessRights` AS `workAccessRights`,`works`.`arrivalDate` AS `workArrivalDate`,`work_owners`.`login` AS `workOwner`,`usergroups`.`label` AS `workUsergroup`,`apps`.`name` AS `appName`,`appTypes`.`appTypeName` AS `appType`,`e_oses`.`osName` AS `executableOsName`,`executables`.`osVersion` AS `executableOsVersion`,`e_cpuTypes`.`cpuTypeName` AS `executableCpuType`,`hosts`.`uid` AS `hostUID`,`hosts`.`accessRights` AS `hostAccessRights`,`hosts`.`name` AS `hostName`,`hosts`.`project` AS `hostProject`,`host_owners`.`login` AS `hostOwner`,`h_oses`.`osName` AS `hostOsName`,`h_cpuTypes`.`cpuTypeName` AS `hostCpuType`,`datas`.`uid` AS `dataUID`,`datas`.`name` AS `dataName` from ((((((((((((((`works` join `statuses` on((`statuses`.`statusId` = `works`.`statusId`))) join `apps` on((`apps`.`uid` = `works`.`appUID`))) join `appTypes` on((`appTypes`.`appTypeId` = `apps`.`appTypeId`))) join `hosts` on(((`works`.`expectedhostUID` = `hosts`.`uid`) or isnull(`works`.`expectedhostUID`)))) join `executables` on((`executables`.`appUID` = `apps`.`uid`))) join `oses` `e_oses` on((`e_oses`.`osId` = `executables`.`osId`))) join `cpuTypes` `e_cpuTypes` on((`e_cpuTypes`.`cpuTypeId` = `executables`.`cpuTypeId`))) join `users` `work_owners` on((`work_owners`.`uid` = `works`.`ownerUID`))) join `users` `host_owners` on((`host_owners`.`uid` = `hosts`.`ownerUID`))) left join `usergroups` on((`usergroups`.`uid` = `work_owners`.`usergroupUID`))) left join `sharedPackageTypes` on(((`sharedPackageTypes`.`packageTypeId` = `apps`.`packageTypeId`) and (`sharedPackageTypes`.`hostUID` = `hosts`.`uid`)))) left join `oses` `h_oses` on((`h_oses`.`osId` = `hosts`.`osId`))) left join `cpuTypes` `h_cpuTypes` on((`h_cpuTypes`.`cpuTypeId` = `hosts`.`cpuTypeId`))) left join `datas` on((`datas`.`uid` = `executables`.`dataUID`))) where ((`statuses`.`statusName` = 'PENDING') and (`appTypes`.`appTypeName` = 'DEPLOYABLE') and ((`executables`.`osId` = `hosts`.`osId`) or (`e_oses`.`osName` = 'JAVA')) and ((`executables`.`osVersion` = `hosts`.`osversion`) or isnull(`executables`.`osVersion`)) and ((`executables`.`cpuTypeId` = `hosts`.`cpuTypeId`) or (`e_cpuTypes`.`cpuTypeName` = 'ALL')) and ((`hosts`.`ownerUID` = `works`.`ownerUID`) or (((`hosts`.`accessRights` & 5) = 5) and ((`works`.`accessRights` & 5) = 5)) or ((((`hosts`.`accessRights` >> 4) & 5) = 5) and (((`works`.`accessRights` >> 4) & 5) = 5) and ((`host_owners`.`usergroupUID` = `work_owners`.`usergroupUID`) or `host_owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `works`.`ownerUID`))))) and (isnull(`apps`.`packageTypeId`) or (`sharedPackageTypes`.`packageTypeId` is not null)) and (isnull(`hosts`.`project`) or (`hosts`.`project` = '') or (`hosts`.`project` = `usergroups`.`label`))) order by `works`.`arrivalDate` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_hosts_matching_works_deployable_and_shared`
--

/*!50001 DROP VIEW IF EXISTS `view_hosts_matching_works_deployable_and_shared`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_hosts_matching_works_deployable_and_shared` AS select `works`.`uid` AS `workUID`,`works`.`accessRights` AS `workAccessRights`,`works`.`arrivalDate` AS `workArrivalDate`,`work_owners`.`login` AS `workOwner`,`usergroups`.`label` AS `workUsergroup`,`apps`.`name` AS `appName`,`appTypes`.`appTypeName` AS `appType`,`e_oses`.`osName` AS `executableOsName`,`executables`.`osVersion` AS `executableOsVersion`,`e_cpuTypes`.`cpuTypeName` AS `executableCpuType`,`hosts`.`uid` AS `hostUID`,`hosts`.`accessRights` AS `hostAccessRights`,`hosts`.`name` AS `hostName`,`hosts`.`project` AS `hostProject`,`host_owners`.`login` AS `hostOwner`,`h_oses`.`osName` AS `hostOsName`,`h_cpuTypes`.`cpuTypeName` AS `hostCpuType`,`datas`.`uid` AS `dataUID`,`datas`.`name` AS `dataName` from (((((((((((((((`works` join `statuses` on((`statuses`.`statusId` = `works`.`statusId`))) join `apps` on((`apps`.`uid` = `works`.`appUID`))) join `appTypes` on((`appTypes`.`appTypeId` = `apps`.`appTypeId`))) join `hosts` on(((`works`.`expectedhostUID` = `hosts`.`uid`) or isnull(`works`.`expectedhostUID`)))) left join `sharedAppTypes` on((`sharedAppTypes`.`appTypeId` = `apps`.`appTypeId`))) left join `executables` on((`executables`.`appUID` = `apps`.`uid`))) left join `oses` `e_oses` on((`e_oses`.`osId` = `executables`.`osId`))) left join `cpuTypes` `e_cpuTypes` on((`e_cpuTypes`.`cpuTypeId` = `executables`.`cpuTypeId`))) join `users` `work_owners` on((`work_owners`.`uid` = `works`.`ownerUID`))) join `users` `host_owners` on((`host_owners`.`uid` = `hosts`.`ownerUID`))) left join `usergroups` on((`usergroups`.`uid` = `work_owners`.`usergroupUID`))) left join `sharedPackageTypes` on(((`sharedPackageTypes`.`packageTypeId` = `apps`.`packageTypeId`) and (`sharedPackageTypes`.`hostUID` = `hosts`.`uid`)))) left join `oses` `h_oses` on((`h_oses`.`osId` = `hosts`.`osId`))) left join `cpuTypes` `h_cpuTypes` on((`h_cpuTypes`.`cpuTypeId` = `hosts`.`cpuTypeId`))) left join `datas` on((`datas`.`uid` = `executables`.`dataUID`))) where ((`statuses`.`statusName` = 'PENDING') and ((`hosts`.`uid` = `sharedAppTypes`.`hostUID`) or ((`appTypes`.`appTypeName` = 'DEPLOYABLE') and ((`executables`.`osId` = `hosts`.`osId`) or (`e_oses`.`osName` = 'JAVA')) and ((`executables`.`osVersion` = `hosts`.`osversion`) or isnull(`executables`.`osVersion`)) and ((`executables`.`cpuTypeId` = `hosts`.`cpuTypeId`) or (`e_cpuTypes`.`cpuTypeName` = 'ALL')))) and ((`hosts`.`ownerUID` = `works`.`ownerUID`) or (((`hosts`.`accessRights` & 5) = 5) and ((`works`.`accessRights` & 5) = 5)) or ((((`hosts`.`accessRights` >> 4) & 5) = 5) and (((`works`.`accessRights` >> 4) & 5) = 5) and ((`host_owners`.`usergroupUID` = `work_owners`.`usergroupUID`) or `host_owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `works`.`ownerUID`))))) and (isnull(`apps`.`packageTypeId`) or (`sharedPackageTypes`.`packageTypeId` is not null)) and (isnull(`hosts`.`project`) or (`hosts`.`project` = '') or (`hosts`.`project` = `usergroups`.`label`))) order by `works`.`arrivalDate` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_hosts_matching_works_shared`
--

/*!50001 DROP VIEW IF EXISTS `view_hosts_matching_works_shared`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_hosts_matching_works_shared` AS select `works`.`uid` AS `workUID`,`works`.`accessRights` AS `workAccessRights`,`works`.`arrivalDate` AS `workArrivalDate`,`work_owners`.`login` AS `workOwner`,`usergroups`.`label` AS `workUsergroup`,`apps`.`name` AS `appName`,`appTypes`.`appTypeName` AS `appType`,NULL AS `executableOsName`,NULL AS `executableOsVersion`,NULL AS `executableCpuType`,`hosts`.`uid` AS `hostUID`,`hosts`.`accessRights` AS `hostAccessRights`,`hosts`.`name` AS `hostName`,`hosts`.`project` AS `hostProject`,`host_owners`.`login` AS `hostOwner`,`h_oses`.`osName` AS `hostOsName`,`h_cpuTypes`.`cpuTypeName` AS `hostCpuType`,NULL AS `dataUID`,NULL AS `dataName` from (((((((((((`works` join `statuses` on((`statuses`.`statusId` = `works`.`statusId`))) join `apps` on((`apps`.`uid` = `works`.`appUID`))) join `appTypes` on((`appTypes`.`appTypeId` = `apps`.`appTypeId`))) join `hosts` on(((`works`.`expectedhostUID` = `hosts`.`uid`) or isnull(`works`.`expectedhostUID`)))) join `sharedAppTypes` on((`sharedAppTypes`.`appTypeId` = `apps`.`appTypeId`))) join `users` `work_owners` on((`work_owners`.`uid` = `works`.`ownerUID`))) join `users` `host_owners` on((`host_owners`.`uid` = `hosts`.`ownerUID`))) left join `usergroups` on((`usergroups`.`uid` = `work_owners`.`usergroupUID`))) left join `sharedPackageTypes` on(((`sharedPackageTypes`.`packageTypeId` = `apps`.`packageTypeId`) and (`sharedPackageTypes`.`hostUID` = `hosts`.`uid`)))) left join `oses` `h_oses` on((`h_oses`.`osId` = `hosts`.`osId`))) left join `cpuTypes` `h_cpuTypes` on((`h_cpuTypes`.`cpuTypeId` = `hosts`.`cpuTypeId`))) where ((`statuses`.`statusName` = 'PENDING') and (`hosts`.`uid` = `sharedAppTypes`.`hostUID`) and ((`hosts`.`ownerUID` = `works`.`ownerUID`) or (((`hosts`.`accessRights` & 5) = 5) and ((`works`.`accessRights` & 5) = 5)) or ((((`hosts`.`accessRights` >> 4) & 5) = 5) and (((`works`.`accessRights` >> 4) & 5) = 5) and ((`host_owners`.`usergroupUID` = `work_owners`.`usergroupUID`) or `host_owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `works`.`ownerUID`))))) and (isnull(`apps`.`packageTypeId`) or (`sharedPackageTypes`.`packageTypeId` is not null)) and (isnull(`hosts`.`project`) or (`hosts`.`project` = '') or (`hosts`.`project` = `usergroups`.`label`))) order by `works`.`arrivalDate` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_sessions`
--

/*!50001 DROP VIEW IF EXISTS `view_sessions`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_sessions` AS select `sessions`.`uid` AS `uid`,`users`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`sessions`.`name` AS `name`,`sessions`.`mtime` AS `mtime`,`sessions`.`accessRights` AS `accessRights`,`sessions`.`isdeleted` AS `isdeleted`,`sessions`.`errorMsg` AS `errorMsg`,sum((`works`.`status` = 'WAITING')) AS `Waiting`,sum((`works`.`status` = 'PENDING')) AS `Pending`,sum((`works`.`status` = 'RUNNING')) AS `Running`,sum((`works`.`status` = 'REPLICATING')) AS `Replicating`,sum((`works`.`status` = 'ERROR')) AS `Error`,sum((`works`.`status` = 'COMPLETED')) AS `Completed`,sum((`works`.`status` = 'ABORTED')) AS `Aborted`,sum((`works`.`status` = 'LOST')) AS `Lost`,sum((`works`.`status` = 'DATAREQUEST')) AS `DataRequest`,sum((`works`.`status` = 'RESULTREQUEST')) AS `ResultRequest`,count(`works`.`uid`) AS `nb_works` from (((`sessions` left join `users` on((`sessions`.`ownerUID` = `users`.`uid`))) left join `usergroups` on((`users`.`usergroupUID` = `usergroups`.`uid`))) left join `works` on((`sessions`.`uid` = `works`.`sessionUID`))) group by `sessions`.`uid`,`owner`,`usergroup`,`sessions`.`name`,`sessions`.`mtime`,`sessions`.`accessRights`,`sessions`.`isdeleted`,`sessions`.`errorMsg` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_sharedAppTypes`
--

/*!50001 DROP VIEW IF EXISTS `view_sharedAppTypes`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_sharedAppTypes` AS select distinct `appTypes`.`appTypeName` AS `appTypeName`,`hosts`.`name` AS `hostname`,`hosts`.`ipaddr` AS `ipaddr`,`hosts`.`hwaddr` AS `hwaddr`,`cpuTypes`.`cpuTypeName` AS `cputype`,`hosts`.`cpunb` AS `cpunb`,`hosts`.`cpumodel` AS `cpumodel`,`oses`.`osName` AS `osName`,`hosts`.`osversion` AS `osversion`,`appTypes`.`mtime` AS `mtime`,`appTypes`.`appTypeDescription` AS `appTypeDescription` from ((((`sharedAppTypes` join `appTypes` on((`appTypes`.`appTypeId` = `sharedAppTypes`.`appTypeId`))) join `hosts` on((`hosts`.`uid` = `sharedAppTypes`.`hostUID`))) left join `cpuTypes` on((`cpuTypes`.`cpuTypeId` = `hosts`.`cpuTypeId`))) left join `oses` on((`oses`.`osId` = `hosts`.`osId`))) order by `appTypes`.`appTypeName`,`hosts`.`name`,`hosts`.`ipaddr`,`hosts`.`hwaddr`,`hosts`.`cputype`,`hosts`.`cpunb`,`hosts`.`cpumodel`,`hosts`.`os`,`hosts`.`osversion` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_sharedPackageTypes`
--

/*!50001 DROP VIEW IF EXISTS `view_sharedPackageTypes`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_sharedPackageTypes` AS select distinct `packageTypes`.`packageTypeName` AS `packageTypeName`,`hosts`.`name` AS `hostname`,`hosts`.`ipaddr` AS `ipaddr`,`hosts`.`hwaddr` AS `hwaddr`,`cpuTypes`.`cpuTypeName` AS `cputype`,`hosts`.`cpunb` AS `cpunb`,`hosts`.`cpumodel` AS `cpumodel`,`oses`.`osName` AS `osName`,`hosts`.`osversion` AS `osversion`,`packageTypes`.`mtime` AS `mtime`,`packageTypes`.`packageTypeDescription` AS `packageTypeDescription` from ((((`sharedPackageTypes` join `packageTypes` on((`packageTypes`.`packageTypeId` = `sharedPackageTypes`.`packageTypeId`))) join `hosts` on((`hosts`.`uid` = `sharedPackageTypes`.`hostUID`))) left join `cpuTypes` on((`cpuTypes`.`cpuTypeId` = `hosts`.`cpuTypeId`))) left join `oses` on((`oses`.`osId` = `hosts`.`osId`))) order by `packageTypes`.`packageTypeName`,`hosts`.`name`,`hosts`.`ipaddr`,`hosts`.`hwaddr`,`hosts`.`cputype`,`hosts`.`cpunb`,`hosts`.`cpumodel`,`hosts`.`os`,`hosts`.`osversion` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_tasks`
--

/*!50001 DROP VIEW IF EXISTS `view_tasks`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_tasks` AS select `tasks`.`uid` AS `uid`,`apps`.`name` AS `application`,`tasks`.`workUID` AS `workUID`,`hosts`.`name` AS `host`,`statuses`.`statusName` AS `statusName`,`tasks`.`status` AS `status`,`users`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`tasks`.`mtime` AS `mtime`,`tasks`.`accessRights` AS `accessRights`,`tasks`.`trial` AS `trial`,`tasks`.`InsertionDate` AS `InsertionDate`,`tasks`.`StartDate` AS `StartDate`,`tasks`.`LastStartDate` AS `LastStartDate`,`tasks`.`AliveCount` AS `AliveCount`,`tasks`.`LastAlive` AS `LastAlive`,`tasks`.`removalDate` AS `removalDate`,`tasks`.`duration` AS `duration`,`tasks`.`isdeleted` AS `isdeleted`,`tasks`.`errorMsg` AS `errorMsg`,`tasks`.`price` AS `price`,NULL AS `NULL` from ((((((`tasks` left join `works` on((`tasks`.`workUID` = `works`.`uid`))) left join `apps` on((`works`.`appUID` = `apps`.`uid`))) left join `hosts` on((`tasks`.`hostUID` = `hosts`.`uid`))) left join `statuses` on((`tasks`.`statusId` = `statuses`.`statusId`))) left join `users` on((`tasks`.`ownerUID` = `users`.`uid`))) left join `usergroups` on((`users`.`usergroupUID` = `usergroups`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_traces`
--

/*!50001 DROP VIEW IF EXISTS `view_traces`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_traces` AS select `traces`.`uid` AS `uid`,`hosts`.`name` AS `host`,`users`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`traces`.`mtime` AS `mtime`,`traces`.`login` AS `login`,`traces`.`arrivalDate` AS `arrivalDate`,`traces`.`startDate` AS `startDate`,`traces`.`endDate` AS `endDate`,`traces`.`accessRights` AS `accessRights`,`traces`.`data` AS `data`,`traces`.`isdeleted` AS `isdeleted`,NULL AS `NULL` from (((`traces` left join `hosts` on((`traces`.`hostUID` = `hosts`.`uid`))) left join `users` on((`traces`.`ownerUID` = `users`.`uid`))) left join `usergroups` on((`users`.`usergroupUID` = `usergroups`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_usergroups`
--

/*!50001 DROP VIEW IF EXISTS `view_usergroups`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_usergroups` AS select `usergroups`.`uid` AS `uid`,`usergroups`.`label` AS `label`,`users`.`login` AS `owner`,`usergroups`.`mtime` AS `mtime`,`usergroups`.`accessRights` AS `accessRights`,`usergroups`.`webpage` AS `webpage`,`usergroups`.`project` AS `project`,`usergroups`.`isdeleted` AS `isdeleted`,`usergroups`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from (`usergroups` left join `users` on((`usergroups`.`ownerUID` = `users`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users`
--

/*!50001 DROP VIEW IF EXISTS `view_users`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users` AS select `users1`.`uid` AS `uid`,`users1`.`login` AS `login`,`userRights`.`userRightName` AS `userRightName`,`users1`.`rights` AS `rights`,`usergroups`.`label` AS `usergroup`,`users2`.`login` AS `owner`,`users1`.`mtime` AS `mtime`,`users1`.`nbJobs` AS `nbJobs`,`users1`.`pendingJobs` AS `pendingJobs`,`users1`.`runningJobs` AS `runningJobs`,`users1`.`errorJobs` AS `errorJobs`,`users1`.`usedCpuTime` AS `usedCpuTime`,`users1`.`certificate` AS `certificate`,`users1`.`accessRights` AS `accessRights`,`users1`.`password` AS `password`,`users1`.`email` AS `email`,`users1`.`fname` AS `fname`,`users1`.`lname` AS `lname`,`users1`.`country` AS `country`,`users1`.`challenging` AS `challenging`,`users1`.`isdeleted` AS `isdeleted`,`users1`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from (((`users` `users1` left join `userRights` on((`users1`.`userRightId` = `userRights`.`userRightId`))) left join `usergroups` on((`users1`.`usergroupUID` = `usergroups`.`uid`))) left join `users` `users2` on((`users1`.`ownerUID` = `users2`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_apps`
--

/*!50001 DROP VIEW IF EXISTS `view_users_apps`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_apps` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`apps`.`uid` AS `uid`,`apps`.`name` AS `name`,`appTypes`.`appTypeName` AS `appTypeName`,`apps`.`type` AS `type`,`packageTypes`.`packageTypeName` AS `packageTypeName`,`apps`.`neededpackages` AS `neededpackages`,`owners`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`apps`.`mtime` AS `mtime`,`apps`.`envvars` AS `envvars`,`apps`.`isdeleted` AS `isdeleted`,`apps`.`isService` AS `isService`,`apps`.`accessRights` AS `accessRights`,`apps`.`avgExecTime` AS `avgExecTime`,`apps`.`minMemory` AS `minMemory`,`apps`.`minCPUSpeed` AS `minCPUSpeed`,`apps`.`minFreeMassStorage` AS `minFreeMassStorage`,`apps`.`price` AS `price`,`apps`.`nbJobs` AS `nbJobs`,`apps`.`pendingJobs` AS `pendingJobs`,`apps`.`runningJobs` AS `runningJobs`,`apps`.`errorJobs` AS `errorJobs`,`apps`.`webpage` AS `webpage`,`apps`.`defaultStdinURI` AS `defaultStdinURI`,`apps`.`baseDirinURI` AS `baseDirinURI`,`apps`.`defaultDirinURI` AS `defaultDirinURI`,`apps`.`launchscriptshuri` AS `launchscriptshuri`,`apps`.`launchscriptcmduri` AS `launchscriptcmduri`,`apps`.`unloadscriptshuri` AS `unloadscriptshuri`,`apps`.`unloadscriptcmduri` AS `unloadscriptcmduri`,`apps`.`errorMsg` AS `errorMsg`,`apps`.`linux_ix86URI` AS `linux_ix86URI`,`apps`.`linux_amd64URI` AS `linux_amd64URI`,`apps`.`linux_arm64URI` AS `linux_arm64URI`,`apps`.`linux_arm32URI` AS `linux_arm32URI`,`apps`.`linux_x86_64URI` AS `linux_x86_64URI`,`apps`.`linux_ia64URI` AS `linux_ia64URI`,`apps`.`linux_ppcURI` AS `linux_ppcURI`,`apps`.`macos_ix86URI` AS `macos_ix86URI`,`apps`.`macos_x86_64URI` AS `macos_x86_64URI`,`apps`.`macos_ppcURI` AS `macos_ppcURI`,`apps`.`win32_ix86URI` AS `win32_ix86URI`,`apps`.`win32_amd64URI` AS `win32_amd64URI`,`apps`.`win32_x86_64URI` AS `win32_x86_64URI`,`apps`.`javaURI` AS `javaURI`,`apps`.`osf1_alphaURI` AS `osf1_alphaURI`,`apps`.`osf1_sparcURI` AS `osf1_sparcURI`,`apps`.`solaris_alphaURI` AS `solaris_alphaURI`,`apps`.`solaris_sparcURI` AS `solaris_sparcURI`,`apps`.`ldlinux_ix86URI` AS `ldlinux_ix86URI`,`apps`.`ldlinux_amd64URI` AS `ldlinux_amd64URI`,`apps`.`ldlinux_arm64URI` AS `ldlinux_arm64URI`,`apps`.`ldlinux_arm32URI` AS `ldlinux_arm32URI`,`apps`.`ldlinux_x86_64URI` AS `ldlinux_x86_64URI`,`apps`.`ldlinux_ia64URI` AS `ldlinux_ia64URI`,`apps`.`ldlinux_ppcURI` AS `ldlinux_ppcURI`,`apps`.`ldmacos_ix86URI` AS `ldmacos_ix86URI`,`apps`.`ldmacos_x86_64URI` AS `ldmacos_x86_64URI`,`apps`.`ldmacos_ppcURI` AS `ldmacos_ppcURI`,`apps`.`ldwin32_ix86URI` AS `ldwin32_ix86URI`,`apps`.`ldwin32_amd64URI` AS `ldwin32_amd64URI`,`apps`.`ldwin32_x86_64URI` AS `ldwin32_x86_64URI`,`apps`.`ldosf1_alphaURI` AS `ldosf1_alphaURI`,`apps`.`ldosf1_sparcURI` AS `ldosf1_sparcURI`,`apps`.`ldsolaris_alphaURI` AS `ldsolaris_alphaURI`,`apps`.`ldsolaris_sparcURI` AS `ldsolaris_sparcURI`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join ((((`apps` left join `appTypes` on((`apps`.`appTypeId` = `appTypes`.`appTypeId`))) left join `packageTypes` on((`apps`.`packageTypeId` = `packageTypes`.`packageTypeId`))) left join `users` `owners` on((`apps`.`ownerUID` = `owners`.`uid`))) left join `usergroups` on((`owners`.`usergroupUID` = `usergroups`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`apps`.`ownerUID` = `users0`.`uid`) or ((`apps`.`accessRights` & 5) = 5) or ((((`apps`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `apps`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_datas`
--

/*!50001 DROP VIEW IF EXISTS `view_users_datas`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_datas` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`datas`.`uid` AS `uid`,`datas`.`workUID` AS `workUID`,`datas`.`package` AS `package`,`statuses`.`statusName` AS `statusName`,`datas`.`status` AS `status`,`dataTypes`.`dataTypeName` AS `dataTypeName`,`datas`.`type` AS `type`,`oses`.`osName` AS `osName`,`datas`.`os` AS `os`,`datas`.`osVersion` AS `osVersion`,`cpuTypes`.`cpuTypeName` AS `cpuTypeName`,`datas`.`cpu` AS `cpu`,`owners`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`datas`.`name` AS `name`,`datas`.`mtime` AS `mtime`,`datas`.`uri` AS `uri`,`datas`.`accessRights` AS `accessRights`,`datas`.`links` AS `links`,`datas`.`accessDate` AS `accessDate`,`datas`.`insertionDate` AS `insertionDate`,`datas`.`shasum` AS `shasum`,`datas`.`size` AS `size`,`datas`.`sendToClient` AS `sendToClient`,`datas`.`replicated` AS `replicated`,`datas`.`isdeleted` AS `isdeleted`,`datas`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join ((((((`datas` left join `statuses` on((`datas`.`statusId` = `statuses`.`statusId`))) left join `dataTypes` on((`datas`.`dataTypeId` = `dataTypes`.`dataTypeId`))) left join `oses` on((`datas`.`osId` = `oses`.`osId`))) left join `cpuTypes` on((`datas`.`cpuTypeId` = `cpuTypes`.`cpuTypeId`))) left join `users` `owners` on((`datas`.`ownerUID` = `owners`.`uid`))) left join `usergroups` on((`owners`.`usergroupUID` = `usergroups`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`datas`.`ownerUID` = `users0`.`uid`) or ((`datas`.`accessRights` & 5) = 5) or ((((`datas`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `datas`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_groups`
--

/*!50001 DROP VIEW IF EXISTS `view_users_groups`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_groups` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`groups`.`uid` AS `uid`,`sessions`.`name` AS `session`,`owners`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`groups`.`name` AS `name`,`groups`.`mtime` AS `mtime`,`groups`.`accessRights` AS `accessRights`,`groups`.`isdeleted` AS `isdeleted`,`groups`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join (((`groups` left join `sessions` on((`groups`.`sessionUID` = `sessions`.`uid`))) left join `users` `owners` on((`groups`.`ownerUID` = `owners`.`uid`))) left join `usergroups` on((`owners`.`usergroupUID` = `usergroups`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`groups`.`ownerUID` = `users0`.`uid`) or ((`groups`.`accessRights` & 5) = 5) or ((((`groups`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `groups`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_hosts`
--

/*!50001 DROP VIEW IF EXISTS `view_users_hosts`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_hosts` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`hosts`.`uid` AS `uid`,`oses`.`osName` AS `osName`,`hosts`.`os` AS `os`,`hosts`.`osversion` AS `osversion`,`cpuTypes`.`cpuTypeName` AS `cpuTypeName`,`hosts`.`cputype` AS `cputype`,`usergroups1`.`label` AS `projectName`,`hosts`.`project` AS `project`,`hosts`.`sharedapps` AS `sharedapps`,`hosts`.`sharedpackages` AS `sharedpackages`,`hosts`.`shareddatas` AS `shareddatas`,`owners`.`login` AS `owner`,`usergroups2`.`label` AS `usergroup`,`hosts`.`name` AS `name`,`hosts`.`mtime` AS `mtime`,`hosts`.`poolworksize` AS `poolworksize`,`hosts`.`nbJobs` AS `nbJobs`,`hosts`.`pendingJobs` AS `pendingJobs`,`hosts`.`runningJobs` AS `runningJobs`,`hosts`.`errorJobs` AS `errorJobs`,`hosts`.`timeOut` AS `timeOut`,`hosts`.`avgExecTime` AS `avgExecTime`,`hosts`.`lastAlive` AS `lastAlive`,`hosts`.`nbconnections` AS `nbconnections`,`hosts`.`natedipaddr` AS `natedipaddr`,`hosts`.`ipaddr` AS `ipaddr`,`hosts`.`hwaddr` AS `hwaddr`,`hosts`.`timezone` AS `timezone`,`hosts`.`javaversion` AS `javaversion`,`hosts`.`javadatamodel` AS `javadatamodel`,`hosts`.`cpunb` AS `cpunb`,`hosts`.`cpumodel` AS `cpumodel`,`hosts`.`cpuspeed` AS `cpuspeed`,`hosts`.`totalmem` AS `totalmem`,`hosts`.`availablemem` AS `availablemem`,`hosts`.`totalswap` AS `totalswap`,`hosts`.`totaltmp` AS `totaltmp`,`hosts`.`freetmp` AS `freetmp`,`hosts`.`timeShift` AS `timeShift`,`hosts`.`avgping` AS `avgping`,`hosts`.`nbping` AS `nbping`,`hosts`.`uploadbandwidth` AS `uploadbandwidth`,`hosts`.`downloadbandwidth` AS `downloadbandwidth`,`hosts`.`accessRights` AS `accessRights`,`hosts`.`cpuLoad` AS `cpuLoad`,`hosts`.`active` AS `active`,`hosts`.`available` AS `available`,`hosts`.`incomingconnections` AS `incomingconnections`,`hosts`.`acceptbin` AS `acceptbin`,`hosts`.`version` AS `version`,`hosts`.`traces` AS `traces`,`hosts`.`isdeleted` AS `isdeleted`,`hosts`.`pilotjob` AS `pilotjob`,`hosts`.`sgid` AS `sgid`,`hosts`.`jobid` AS `jobid`,`hosts`.`batchid` AS `batchid`,`hosts`.`userproxy` AS `userproxy`,`hosts`.`errorMsg` AS `errorMsg`,`hosts`.`ethwalletaddr` AS `ethwalletaddr`,`hosts`.`marketorderUID` AS `marketorderUID`,`hosts`.`contributionstatusId` AS `contributionstatusId`,`hosts`.`contributionstatus` AS `contributionstatus`,`hosts`.`workerpooladdr` AS `workerpooladdr`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join (((((`hosts` left join `oses` on((`hosts`.`osId` = `oses`.`osId`))) left join `cpuTypes` on((`hosts`.`cpuTypeId` = `cpuTypes`.`cpuTypeId`))) left join `usergroups` `usergroups1` on((`hosts`.`usergroupUID` = `usergroups1`.`uid`))) left join `users` `owners` on((`hosts`.`ownerUID` = `owners`.`uid`))) left join `usergroups` `usergroups2` on((`owners`.`usergroupUID` = `usergroups2`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`hosts`.`ownerUID` = `users0`.`uid`) or ((`hosts`.`accessRights` & 5) = 5) or ((((`hosts`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `hosts`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_sessions`
--

/*!50001 DROP VIEW IF EXISTS `view_users_sessions`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_sessions` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`sessions`.`uid` AS `uid`,`owners`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`sessions`.`name` AS `name`,`sessions`.`mtime` AS `mtime`,`sessions`.`accessRights` AS `accessRights`,`sessions`.`isdeleted` AS `isdeleted`,`sessions`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join ((`sessions` left join `users` `owners` on((`sessions`.`ownerUID` = `owners`.`uid`))) left join `usergroups` on((`owners`.`usergroupUID` = `usergroups`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`sessions`.`ownerUID` = `users0`.`uid`) or ((`sessions`.`accessRights` & 5) = 5) or ((((`sessions`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `sessions`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_tasks`
--

/*!50001 DROP VIEW IF EXISTS `view_users_tasks`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_tasks` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`tasks`.`uid` AS `uid`,`apps`.`name` AS `application`,`tasks`.`workUID` AS `workUID`,`hosts`.`name` AS `host`,`statuses`.`statusName` AS `statusName`,`tasks`.`status` AS `status`,`owners`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`tasks`.`mtime` AS `mtime`,`tasks`.`accessRights` AS `accessRights`,`tasks`.`trial` AS `trial`,`tasks`.`InsertionDate` AS `InsertionDate`,`tasks`.`StartDate` AS `StartDate`,`tasks`.`LastStartDate` AS `LastStartDate`,`tasks`.`AliveCount` AS `AliveCount`,`tasks`.`LastAlive` AS `LastAlive`,`tasks`.`removalDate` AS `removalDate`,`tasks`.`duration` AS `duration`,`tasks`.`isdeleted` AS `isdeleted`,`tasks`.`errorMsg` AS `errorMsg`,`tasks`.`price` AS `price`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join ((((((`tasks` left join `works` on((`tasks`.`workUID` = `works`.`uid`))) left join `apps` on((`works`.`appUID` = `apps`.`uid`))) left join `hosts` on((`tasks`.`hostUID` = `hosts`.`uid`))) left join `statuses` on((`tasks`.`statusId` = `statuses`.`statusId`))) left join `users` `owners` on((`tasks`.`ownerUID` = `owners`.`uid`))) left join `usergroups` on((`owners`.`usergroupUID` = `usergroups`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`tasks`.`ownerUID` = `users0`.`uid`) or ((`tasks`.`accessRights` & 5) = 5) or ((((`tasks`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `tasks`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_traces`
--

/*!50001 DROP VIEW IF EXISTS `view_users_traces`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_traces` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`traces`.`uid` AS `uid`,`hosts`.`name` AS `host`,`owners`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`traces`.`mtime` AS `mtime`,`traces`.`login` AS `login`,`traces`.`arrivalDate` AS `arrivalDate`,`traces`.`startDate` AS `startDate`,`traces`.`endDate` AS `endDate`,`traces`.`accessRights` AS `accessRights`,`traces`.`data` AS `data`,`traces`.`isdeleted` AS `isdeleted`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join (((`traces` left join `hosts` on((`traces`.`hostUID` = `hosts`.`uid`))) left join `users` `owners` on((`traces`.`ownerUID` = `owners`.`uid`))) left join `usergroups` on((`owners`.`usergroupUID` = `usergroups`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`traces`.`ownerUID` = `users0`.`uid`) or ((`traces`.`accessRights` & 5) = 5) or ((((`traces`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `traces`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_usergroups`
--

/*!50001 DROP VIEW IF EXISTS `view_users_usergroups`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_usergroups` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`usergroups`.`uid` AS `uid`,`usergroups`.`label` AS `label`,`owners`.`login` AS `owner`,`usergroups`.`mtime` AS `mtime`,`usergroups`.`accessRights` AS `accessRights`,`usergroups`.`webpage` AS `webpage`,`usergroups`.`project` AS `project`,`usergroups`.`isdeleted` AS `isdeleted`,`usergroups`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join (`usergroups` left join `users` `owners` on((`usergroups`.`ownerUID` = `owners`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`usergroups`.`ownerUID` = `users0`.`uid`) or ((`usergroups`.`accessRights` & 5) = 5) or ((((`usergroups`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `usergroups`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_users`
--

/*!50001 DROP VIEW IF EXISTS `view_users_users`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_users` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`users1`.`uid` AS `uid`,`users1`.`login` AS `login`,`userRights1`.`userRightName` AS `userRightName`,`users1`.`rights` AS `rights`,`usergroups`.`label` AS `usergroup`,`owners`.`login` AS `owner`,`users1`.`mtime` AS `mtime`,`users1`.`nbJobs` AS `nbJobs`,`users1`.`pendingJobs` AS `pendingJobs`,`users1`.`runningJobs` AS `runningJobs`,`users1`.`errorJobs` AS `errorJobs`,`users1`.`usedCpuTime` AS `usedCpuTime`,`users1`.`certificate` AS `certificate`,`users1`.`accessRights` AS `accessRights`,`users1`.`password` AS `password`,`users1`.`email` AS `email`,`users1`.`fname` AS `fname`,`users1`.`lname` AS `lname`,`users1`.`country` AS `country`,`users1`.`challenging` AS `challenging`,`users1`.`isdeleted` AS `isdeleted`,`users1`.`errorMsg` AS `errorMsg`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join (((`users` `users1` left join `userRights` `userRights1` on((`users1`.`userRightId` = `userRights1`.`userRightId`))) left join `usergroups` on((`users1`.`usergroupUID` = `usergroups`.`uid`))) left join `users` `owners` on((`users1`.`ownerUID` = `owners`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`users1`.`ownerUID` = `users0`.`uid`) or ((`users1`.`accessRights` & 5) = 5) or ((((`users1`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `users1`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_users_works`
--

/*!50001 DROP VIEW IF EXISTS `view_users_works`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_users_works` AS select `users0`.`uid` AS `userUID`,`users0`.`login` AS `userLogin`,`userRights0`.`userRightName` AS `userRight`,`works`.`uid` AS `uid`,`apps`.`name` AS `application`,`works`.`categoryId` AS `categoryId`,`statuses`.`statusName` AS `statusName`,`works`.`status` AS `status`,`sessions`.`name` AS `session`,`groups`.`name` AS `group`,`hosts`.`name` AS `expectedhost`,`owners`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`works`.`label` AS `label`,`works`.`mtime` AS `mtime`,`works`.`userproxy` AS `userproxy`,`works`.`accessRights` AS `accessRights`,`works`.`sgid` AS `sgid`,`works`.`maxRetry` AS `maxRetry`,`works`.`retry` AS `retry`,`works`.`minMemory` AS `minMemory`,`works`.`minCPUSpeed` AS `minCPUSpeed`,`works`.`minFreeMassStorage` AS `minFreeMassStorage`,`works`.`maxWallClockTime` AS `maxWallClockTime`,`works`.`maxFreeMassStorage` AS `maxFreeMassStorage`,`works`.`maxFileSize` AS `maxFileSize`,`works`.`maxMemory` AS `maxMemory`,`works`.`maxCpuSpeed` AS `maxCpuSpeed`,`works`.`uploadbandwidth` AS `uploadbandwidth`,`works`.`downloadbandwidth` AS `downloadbandwidth`,`works`.`returnCode` AS `returnCode`,`works`.`server` AS `server`,`works`.`cmdLine` AS `cmdLine`,`works`.`listenport` AS `listenport`,`works`.`smartsocketaddr` AS `smartsocketaddr`,`works`.`smartsocketclient` AS `smartsocketclient`,`works`.`stdinURI` AS `stdinURI`,`works`.`dirinURI` AS `dirinURI`,`works`.`resultURI` AS `resultURI`,`works`.`arrivalDate` AS `arrivalDate`,`works`.`completedDate` AS `completedDate`,`works`.`resultDate` AS `resultDate`,`works`.`readydate` AS `readydate`,`works`.`datareadydate` AS `datareadydate`,`works`.`compstartdate` AS `compstartdate`,`works`.`compenddate` AS `compenddate`,`works`.`error_msg` AS `error_msg`,`works`.`sendToClient` AS `sendToClient`,`works`.`local` AS `local`,`works`.`active` AS `active`,`works`.`replicatedUID` AS `replicatedUID`,`works`.`replications` AS `replications`,`works`.`sizer` AS `sizer`,`works`.`totalr` AS `totalr`,`works`.`datadrivenURI` AS `datadrivenURI`,`works`.`isService` AS `isService`,`works`.`isdeleted` AS `isdeleted`,`works`.`envvars` AS `envvars`,`works`.`errorMsg` AS `errorMsg`,`works`.`requester` AS `requester`,`works`.`dataset` AS `dataset`,`works`.`workerPool` AS `workerPool`,`works`.`emitcost` AS `emitcost`,`works`.`callback` AS `callback`,`works`.`beneficiary` AS `beneficiary`,`works`.`marketorderUID` AS `marketorderUID`,`works`.`h2h2r` AS `h2h2r`,`works`.`h2r` AS `h2r`,`works`.`workOrderId` AS `workOrderId`,NULL AS `NULL` from ((`users` `users0` join `userRights` `userRights0` on((`users0`.`userRightId` = `userRights0`.`userRightId`))) join (((((((`works` left join `apps` on((`works`.`appUID` = `apps`.`uid`))) left join `statuses` on((`works`.`statusId` = `statuses`.`statusId`))) left join `sessions` on((`works`.`sessionUID` = `sessions`.`uid`))) left join `groups` on((`works`.`groupUID` = `groups`.`uid`))) left join `hosts` on((`works`.`expectedhostUID` = `hosts`.`uid`))) left join `users` `owners` on((`works`.`ownerUID` = `owners`.`uid`))) left join `usergroups` on((`owners`.`usergroupUID` = `usergroups`.`uid`)))) where ((`userRights0`.`userRightName` = 'SUPER_USER') or (`works`.`ownerUID` = `users0`.`uid`) or ((`works`.`accessRights` & 5) = 5) or ((((`works`.`accessRights` >> 4) & 5) = 5) and ((`owners`.`usergroupUID` = `users0`.`usergroupUID`) or `owners`.`usergroupUID` in (select `memberships`.`usergroupUID` from `memberships` where (`memberships`.`userUID` = `works`.`ownerUID`))))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_works`
--

/*!50001 DROP VIEW IF EXISTS `view_works`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_works` AS select `works`.`uid` AS `uid`,`apps`.`name` AS `application`,`works`.`categoryId` AS `categoryId`,`statuses`.`statusName` AS `statusName`,`works`.`status` AS `status`,`sessions`.`name` AS `session`,`groups`.`name` AS `group`,`hosts`.`name` AS `expectedhost`,`users`.`login` AS `owner`,`usergroups`.`label` AS `usergroup`,`works`.`label` AS `label`,`works`.`mtime` AS `mtime`,`works`.`userproxy` AS `userproxy`,`works`.`accessRights` AS `accessRights`,`works`.`sgid` AS `sgid`,`works`.`maxRetry` AS `maxRetry`,`works`.`retry` AS `retry`,`works`.`minMemory` AS `minMemory`,`works`.`minCPUSpeed` AS `minCPUSpeed`,`works`.`minFreeMassStorage` AS `minFreeMassStorage`,`works`.`maxWallClockTime` AS `maxWallClockTime`,`works`.`maxFreeMassStorage` AS `maxFreeMassStorage`,`works`.`maxFileSize` AS `maxFileSize`,`works`.`maxMemory` AS `maxMemory`,`works`.`maxCpuSpeed` AS `maxCpuSpeed`,`works`.`uploadbandwidth` AS `uploadbandwidth`,`works`.`downloadbandwidth` AS `downloadbandwidth`,`works`.`returnCode` AS `returnCode`,`works`.`server` AS `server`,`works`.`cmdLine` AS `cmdLine`,`works`.`listenport` AS `listenport`,`works`.`smartsocketaddr` AS `smartsocketaddr`,`works`.`smartsocketclient` AS `smartsocketclient`,`works`.`stdinURI` AS `stdinURI`,`works`.`dirinURI` AS `dirinURI`,`works`.`resultURI` AS `resultURI`,`works`.`arrivalDate` AS `arrivalDate`,`works`.`completedDate` AS `completedDate`,`works`.`resultDate` AS `resultDate`,`works`.`readydate` AS `readydate`,`works`.`datareadydate` AS `datareadydate`,`works`.`compstartdate` AS `compstartdate`,`works`.`compenddate` AS `compenddate`,`works`.`error_msg` AS `error_msg`,`works`.`sendToClient` AS `sendToClient`,`works`.`local` AS `local`,`works`.`active` AS `active`,`works`.`replicatedUID` AS `replicatedUID`,`works`.`replications` AS `replications`,`works`.`sizer` AS `sizer`,`works`.`totalr` AS `totalr`,`works`.`datadrivenURI` AS `datadrivenURI`,`works`.`isService` AS `isService`,`works`.`isdeleted` AS `isdeleted`,`works`.`envvars` AS `envvars`,`works`.`errorMsg` AS `errorMsg`,`works`.`requester` AS `requester`,`works`.`dataset` AS `dataset`,`works`.`workerPool` AS `workerPool`,`works`.`emitcost` AS `emitcost`,`works`.`callback` AS `callback`,`works`.`beneficiary` AS `beneficiary`,`works`.`marketorderUID` AS `marketorderUID`,`works`.`h2h2r` AS `h2h2r`,`works`.`h2r` AS `h2r`,`works`.`workOrderId` AS `workOrderId`,NULL AS `NULL` from (((((((`works` left join `apps` on((`works`.`appUID` = `apps`.`uid`))) left join `statuses` on((`works`.`statusId` = `statuses`.`statusId`))) left join `sessions` on((`works`.`sessionUID` = `sessions`.`uid`))) left join `groups` on((`works`.`groupUID` = `groups`.`uid`))) left join `hosts` on((`works`.`expectedhostUID` = `hosts`.`uid`))) left join `users` on((`works`.`ownerUID` = `users`.`uid`))) left join `usergroups` on((`users`.`usergroupUID` = `usergroups`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_works_for_billing`
--

/*!50001 DROP VIEW IF EXISTS `view_works_for_billing`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_works_for_billing` AS select `works`.`uid` AS `uid`,`statuses`.`statusName` AS `statusName`,`users`.`login` AS `owner`,`works`.`minFreeMassStorage` AS `minFreeMassStorage`,`works`.`completedDate` AS `completedDate`,(unix_timestamp(`works`.`compenddate`) - unix_timestamp(`works`.`compstartdate`)) AS `compDuration` from ((`works` FORCE INDEX (`completedDate`) left join `statuses` on((`works`.`statusId` = `statuses`.`statusId`))) left join `users` on((`works`.`ownerUID` = `users`.`uid`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_works_for_billing_with_file_sizes`
--

/*!50001 DROP VIEW IF EXISTS `view_works_for_billing_with_file_sizes`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_works_for_billing_with_file_sizes` AS select `works`.`uid` AS `uid`,`statuses`.`statusName` AS `statusName`,`users`.`login` AS `owner`,`works`.`minFreeMassStorage` AS `minFreeMassStorage`,`datas1`.`size` AS `stdinSize`,`datas2`.`size` AS `dirinSize`,`datas3`.`size` AS `resultSize`,`works`.`completedDate` AS `completedDate`,(unix_timestamp(`works`.`compenddate`) - unix_timestamp(`works`.`compstartdate`)) AS `compDuration` from (((((`works` FORCE INDEX (`completedDate`) left join `statuses` on((`works`.`statusId` = `statuses`.`statusId`))) left join `users` on((`works`.`ownerUID` = `users`.`uid`))) left join `datas` `datas1` on((`datas1`.`uid` = right(`works`.`stdinURI`,36)))) left join `datas` `datas2` on((`datas2`.`uid` = right(`works`.`dirinURI`,36)))) left join `datas` `datas3` on((`datas3`.`uid` = right(`works`.`resultURI`,36)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-24 14:43:20
