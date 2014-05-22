<?php
/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2012, 2013 Semantic Software Lab, http://www.semanticsoftware.info
Rene Witte
Bahar Sateli

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

# Not a valid entry point, skip unless MEDIAWIKI is defined
if ( !defined( 'MEDIAWIKI' ) ) {
	echo <<<EOT
To install the Semantic Assistants extension, put the following line in LocalSettings.php:
require_once( "\$IP/extensions/SemanticAssistants/SemAssist.php" );
EOT;
	exit( 1 );
}

$dir = dirname( __FILE__ ) . '/';

# Extension version
$wgSemAssistVersion = "1.0";

/** Array of namespaces that can be added to a collection */
$wgSemAssistArticleNamespaces = array(
	NS_MAIN,
	NS_TALK,
	NS_USER,
	NS_USER_TALK,
	NS_PROJECT,
	NS_PROJECT_TALK,
	NS_MEDIAWIKI,
	NS_MEDIAWIKI_TALK,
	100,
	101,
	102,
	103,
	104,
	105,
	106,
	107,
	108,
	109,
	110,
	111,
);

$wgExtensionCredits['semantic'][] = array(
	'path' => __FILE__,
	'name' => 'Semantic Assistants',
	'version' => $wgSemAssistVersion,
	'author' => array( 'Bahar Sateli' ),
	'description' => 'Offers NLP services by connecting the Wiki to the Semantic Assistants framework.',
	'url' => 'http://www.semanticsoftware.info/semantic-assistants-project',
);

# Set up hook
$wgHooks['MonoBookTemplateToolboxEnd'][] = 'wfToolboxLink';
function wfToolboxLink(&$monobook) {
// Load system messages wfLoadExtensionMessages('ToolboxLink');
// Print a bulleted link
#echo sprintf("<li> <a href=\"%s\">%s</a></li>",'http://oompa:8080/Wiki-NLP/SemAssistServlet?action=proxy',wfMsg('Semantic Assistants')); 
print("<li> <a href=\"http://loompa.cs.concordia.ca:8080/SA-WikiConnector/SemAssistServlet?action=proxy\">Semantic Assistants</a></li>");
return true;
}
?>
