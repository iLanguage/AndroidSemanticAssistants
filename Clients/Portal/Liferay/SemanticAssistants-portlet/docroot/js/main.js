/*
Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

This file is part of the Semantic Assistants architecture.

Copyright (C) 2013, 2014 Semantic Software Lab, http://www.semanticsoftware.info

The Semantic Assistants architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.
*/

jQuery(document).ready(function() {
	$(document).delegate(".sa-heading", "click", toggleService);
	
	// hiding needs to be called after all Ajax calls to the SA server is completed
	$(document).ajaxComplete(function() {
		jQuery(".sa-content").hide();
	});
});

function toggleService(){
	// find which element was clicked
	var serviceInfo = $(this).next(".sa-content");
	// slide it open
	serviceInfo.slideToggle(300);
	// slide up all others so we only have one open service at a time
	$(".sa-content").not(serviceInfo).slideUp();
}
