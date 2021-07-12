USE `note_app`;

CREATE TABLE IF NOT EXISTS `note_app`.`note`(
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    description VARCHAR(100) NOT NULL,
    createdAt DATETIME(4) DEFAULT CURRENT_TIMESTAMP(4),
    updatedAt DATETIME(4) DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE CURRENT_TIMESTAMP(4),
    PRIMARY KEY(id)
);

INSERT INTO `note_app`.`note`(name, description)
    VALUES ("First task", "Just a demo task");