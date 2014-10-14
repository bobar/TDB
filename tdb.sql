-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Mar 14 Octobre 2014 à 17:06
-- Version du serveur: 5.5.38-0ubuntu0.14.04.1
-- Version de PHP: 5.5.9-1ubuntu4.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `tdb`
--

-- --------------------------------------------------------

--
-- Structure de la table `accounts`
--

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trigramme` char(3) DEFAULT NULL,
  `name` text NOT NULL,
  `first_name` text NOT NULL,
  `nickname` text NOT NULL,
  `casert` tinytext NOT NULL,
  `status` int(11) DEFAULT NULL,
  `promo` int(11) DEFAULT NULL,
  `mail` text NOT NULL,
  `picture` text NOT NULL,
  `balance` int(11) NOT NULL DEFAULT '0',
  `turnover` int(11) NOT NULL DEFAULT '0',
  `total_clopes` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3670 ;

--
-- Contenu de la table `accounts`
--

INSERT INTO `accounts` (`id`, `trigramme`, `name`, `first_name`, `nickname`, `casert`, `status`, `promo`, `mail`, `picture`, `balance`, `turnover`, `total_clopes`) VALUES
(1, 'BOB', '', '', '', '', 2, NULL, '', '', 0, 0, 0);

-- --------------------------------------------------------

--
-- Structure de la table `admins`
--

CREATE TABLE IF NOT EXISTS `admins` (
  `id` int(11) NOT NULL DEFAULT '0',
  `permissions` int(11) DEFAULT NULL,
  `passwd` tinytext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Contenu de la table `admins`
--

INSERT INTO `admins` (`id`, `permissions`, `passwd`) VALUES
(1, 3, '9f9d51bc70ef21ca5c14f307980a29d8');

-- --------------------------------------------------------

--
-- Structure de la table `clopes`
--

CREATE TABLE IF NOT EXISTS `clopes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `marque` tinytext,
  `prix` int(11) DEFAULT NULL,
  `quantite` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `droits`
--

CREATE TABLE IF NOT EXISTS `droits` (
  `permissions` int(11) NOT NULL,
  `nom` varchar(20) NOT NULL,
  `log_eleve` tinyint(1) NOT NULL DEFAULT '0',
  `credit` tinyint(1) NOT NULL DEFAULT '0',
  `log_groupe` tinyint(1) NOT NULL DEFAULT '0',
  `transfert` tinyint(1) NOT NULL DEFAULT '0',
  `creer_tri` tinyint(1) NOT NULL DEFAULT '0',
  `modifier_tri` tinyint(1) NOT NULL DEFAULT '0',
  `supprimer_tri` tinyint(1) NOT NULL DEFAULT '0',
  `somme_tri` tinyint(1) NOT NULL DEFAULT '0',
  `voir_comptes` tinyint(1) NOT NULL DEFAULT '0',
  `debit_fichier` tinyint(1) NOT NULL DEFAULT '0',
  `export` tinyint(1) NOT NULL DEFAULT '0',
  `reinitialiser` tinyint(1) NOT NULL DEFAULT '0',
  `super_admin` tinyint(1) NOT NULL DEFAULT '0',
  `banque_binet` tinyint(1) NOT NULL DEFAULT '0',
  `gestion_clopes` tinyint(1) NOT NULL DEFAULT '0',
  `gestion_admin` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`permissions`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contenu de la table `droits`
--

INSERT INTO `droits` (`permissions`, `nom`, `log_eleve`, `credit`, `log_groupe`, `transfert`, `creer_tri`, `modifier_tri`, `supprimer_tri`, `somme_tri`, `voir_comptes`, `debit_fichier`, `export`, `reinitialiser`, `super_admin`, `banque_binet`, `gestion_clopes`, `gestion_admin`) VALUES
(0, 'PÃ©kin', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(1, 'Ami du BÃ´B', 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(2, 'Ex-BÃ´Barman', 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
(3, 'BÃ´Barman', 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);

-- --------------------------------------------------------

--
-- Structure de la table `transactions`
--

CREATE TABLE IF NOT EXISTS `transactions` (
  `id` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `comment` text,
  `admin` int(11) DEFAULT NULL,
  `id2` int(11) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  KEY `id_date` (`id`,`date`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `transactions_history`
--

CREATE TABLE IF NOT EXISTS `transactions_history` (
  `id` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `comment` text,
  `admin` int(11) DEFAULT NULL,
  `id2` int(11) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  KEY `id_date` (`id`,`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
