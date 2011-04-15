/**
   Semantic Assistants - http://www.semanticsoftware.info/semantic-assistants

   This file is part of the Semantic Assistants architecture.

   Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info

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
/**
 * 
 * 
 * @author Chadi Cortbaoui - Concordia University Software Engineering
 * 
 * 	This Class adds to the SoapEnvelope the required Parameters by the WSDL, for invoking services.
 *  It Adds (from the list below) only "documents","literalDocs","connID","userCtx" ... The remaining parameters are added in other levels of the
 *  Domain Logic.
 *  
 *  Snapshot of the WSDL's invokeService message:
 *	<message name="invokeService">
 *		<part name="serviceName" type="xsd:string"/>
 *		<part name="documents" type="tns:uriList"/>
 *		<part name="literalDocs" type="ns1:stringArray"/>
 *		<part name="connID" type="xsd:long"/>
 *		<part name="gateParams" type="tns:gateRuntimeParameterArray"/>
 *		<part name="userCtx" type="tns:userContext"/>
 *	</message>
 */

package NLPAndroid.domainLogic;

import NLPAndroid.domainLogic.WSMatchingClasses.*;

public class serviceInvocationAddParams {

	private UriList documents;
	private stringArray literalDocs;
	private long connID;
	private UserContext userCtx;
	
	public serviceInvocationAddParams()
	{
		documents = new UriList();
		literalDocs = new stringArray();
		userCtx = new UserContext();
	}
	public serviceInvocationAddParams(UriList documents, stringArray literalDocs,
			long connID, UserContext userCtx) {
		this.documents = documents;
		this.literalDocs = literalDocs;
		this.connID = connID;
		this.userCtx = userCtx;
	}
	public UriList getDocuments() {
		return documents;
	}
	public void setDocuments(UriList documents) {
		this.documents = documents;
	}
	public stringArray getLiteralDocs() {
		return literalDocs;
	}
	public void setLiteralDocs(stringArray literalDocs) {
		this.literalDocs = literalDocs;
	}
	public long getConnID() {
		return connID;
	}
	public void setConnID(long connID) {
		this.connID = connID;
	}
	public UserContext getUserCtx() {
		return userCtx;
	}
	public void setUserCtx(UserContext userCtx) {
		this.userCtx = userCtx;
	}
	
	
}
