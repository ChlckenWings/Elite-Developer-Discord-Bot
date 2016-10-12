CREATE TABLE IF NOT EXISTS `channels`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `server_id` VARCHAR(255) NOT NULL,
  `channel_id` VARCHAR(255) NOT NULL,
  `channel_type` CHAR(1) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE(`server_id`, `channel_id`, `channel_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `roles`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `server_id` VARCHAR(255) NOT NULL,
  `role_id` VARCHAR(255) NOT NULL,
  `role_type` CHAR(1) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE(`server_id`, `role_id`, `role_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;