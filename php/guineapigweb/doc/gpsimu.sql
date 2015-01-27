CREATE TABLE IF NOT EXISTS `gpsimu` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(50) NOT NULL,
  `status` enum('ready','running') NOT NULL DEFAULT 'ready',
  `xwlogin` char(250) NOT NULL,
  `configuration` text,
  `number` int(10) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`xwlogin`)
);
