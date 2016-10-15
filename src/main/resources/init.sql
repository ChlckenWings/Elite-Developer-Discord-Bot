CREATE TABLE IF NOT EXISTS `role_power`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `server_id` VARCHAR(255) NOT NULL,
  `role_id` VARCHAR(255) NOT NULL,
  `power` INT NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE(`server_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `commands`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `server_id` VARCHAR(255) NOT NULL,
  `command_name` VARCHAR(255) NOT NULL,
  `power` INT NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE(`server_id`, `command_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;