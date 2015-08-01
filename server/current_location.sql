--
-- Table structure for table `current_location`
--

CREATE TABLE IF NOT EXISTS `current_location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lat` double NOT NULL,
  `lon` double NOT NULL,
  `recorded_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=532 ;
