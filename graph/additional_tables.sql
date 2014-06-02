SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE IF NOT EXISTS `simple_graph` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_person` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `to_person` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `weight` int(11) NOT NULL,
  `from_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `to_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE IF NOT EXISTS `ww2_article` (
  `id` int(10) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `text` mediumblob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;