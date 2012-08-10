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

package info.semanticsoftware.semassist.client.wiki.utils;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.Template;

/**
 * A utility class to translate ReqWiki template markups
 * to HTML.
 * @author Bahar Sateli
 * */
public class Parser {

	/** Template markup for Actor entity. */
	private String actorMarkup ="";
	/** Template markup for Need entity. */
	private String needMarkup ="";
	/** Template markup for Assumption entity. */
	private String assumptionMarkup = "";
	/** Template markup for Dependency entity. */
	private String dependencyMarkup = "";
	/** Template markup for Feature entity. */
	private String featureMarkup = "";
	/** Template markup for Functional Requirements entity. */
	private String functionalReqMarkup = "";
	/** Template markup for Glossary entity. */
	private String glossaryMarkup = "";
	/** Template markup for Goal entity. */
	private String goalMarkup = "";
	/** Template markup for Non-Functional Requirements entity. */
	private String nonfunctionalMarkup = "";
	/** Template markup for Problem entity. */
	private String problemMarkup = "";
	/** Template markup for Product Position entity. */
	private String productPositionMarkup = "";
	/** Template markup for Responsibility entity. */
	private String responsibilityMarkup = "";
	/** Template markup for Stakeholder entity. */
	private String stakeHolderMarkup = "";
	/** Template markup for Test Case entity. */
	private String testCaseMarkup = "";
	/** Template markup for Use Case entity. */
	private String useCaseMarkup = "";
	/** MediaWiki language object. */
	private MediaWikiLanguage lang = null;
	/** MediaWiki parser object. */
	private MarkupParser markupParser = null;

	/**
	 * Class constructor.
	 * */
	public Parser(){
		createLanguage();
	}

	/**
	 * Adds template markups to the mediawiki language object.
	 */
	private void createLanguage(){
		lang = new MediaWikiLanguage();
		List<Template> temps = lang.getTemplates();

		Template actor = new Template();
		actor.setName("Actor");
		actorMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		actorMarkup += "! Description" + System.getProperty("line.separator");
		actorMarkup += "| {{{ActorDescription}}}" + System.getProperty("line.separator");
		actorMarkup += "|-" + System.getProperty("line.separator");
		actorMarkup += "! StakeHolder" + System.getProperty("line.separator");
		actorMarkup += "| {{{StakeHolder}}}" + System.getProperty("line.separator");
		actorMarkup += "|}" + System.getProperty("line.separator");
		actor.setTemplateMarkup(actorMarkup);
		temps.add(actor);

		Template assumption = new Template();
		assumption.setName("Assumption");
		assumptionMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		assumptionMarkup += "! Description" + System.getProperty("line.separator");
		assumptionMarkup += "| {{{AssumptionDescription}}}" + System.getProperty("line.separator");
		assumptionMarkup += "|}" + System.getProperty("line.separator");
		assumption.setTemplateMarkup(assumptionMarkup);
		temps.add(assumption);

		Template dependency = new Template();
		dependency.setName("Dependencies");
		dependencyMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		dependencyMarkup += "! Description" + System.getProperty("line.separator");
		dependencyMarkup += "| {{{DependencyDescription}}}" + System.getProperty("line.separator");
		dependencyMarkup += "|-" + System.getProperty("line.separator");
		dependencyMarkup += "! Assumptions" + System.getProperty("line.separator");
		dependencyMarkup += "| {{{Assumptions}}}" + System.getProperty("line.separator");
		dependencyMarkup += "|}" + System.getProperty("line.separator");
		dependency.setTemplateMarkup(dependencyMarkup);
		temps.add(dependency);

		Template feature = new Template();
		feature.setName("Features");
		featureMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		featureMarkup += "! Description" + System.getProperty("line.separator");
		featureMarkup += "| {{{FeatureDescription}}}" + System.getProperty("line.separator");
		featureMarkup += "|-" + System.getProperty("line.separator");
		featureMarkup += "! Planned Release" + System.getProperty("line.separator");
		featureMarkup += "| {{{Release}}}" + System.getProperty("line.separator");
		featureMarkup += "! Need" + System.getProperty("line.separator");
		featureMarkup += "| {{{Need}}}" + System.getProperty("line.separator");
		featureMarkup += "|}" + System.getProperty("line.separator");
		feature.setTemplateMarkup(featureMarkup);
		temps.add(feature);

		Template functionalReq = new Template();
		functionalReq.setName("FunctionalReq");
		functionalReqMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		functionalReqMarkup += "! Description" + System.getProperty("line.separator");
		functionalReqMarkup += "| {{{FunDescription}}}" + System.getProperty("line.separator");
		functionalReqMarkup += "|-" + System.getProperty("line.separator");
		functionalReqMarkup += "! Features" + System.getProperty("line.separator");
		functionalReqMarkup += "| {{{Features}}}" + System.getProperty("line.separator");
		functionalReqMarkup += "|}" + System.getProperty("line.separator");
		functionalReq.setTemplateMarkup(functionalReqMarkup);
		temps.add(functionalReq);

		Template glossary = new Template();
		glossary.setName("Glossary");
		glossaryMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		glossaryMarkup += "! Definition" + System.getProperty("line.separator");
		glossaryMarkup += "| {{{Definition}}}" + System.getProperty("line.separator");
		glossaryMarkup += "|}" + System.getProperty("line.separator");
		glossary.setTemplateMarkup(glossaryMarkup);
		temps.add(glossary);

		Template goal = new Template();
		goal.setName("Goal");
		goalMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		goalMarkup += "! Description" + System.getProperty("line.separator");
		goalMarkup += "| {{{GoalDesc}}}" + System.getProperty("line.separator");
		goalMarkup += "|-" + System.getProperty("line.separator");
		goalMarkup += "! Actor" + System.getProperty("line.separator");
		goalMarkup += "| {{{Actor}}}" + System.getProperty("line.separator");
		goalMarkup += "|}" + System.getProperty("line.separator");
		goal.setTemplateMarkup(goalMarkup);
		temps.add(goal);

		Template need = new Template();
		need.setName("Need");
		needMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		needMarkup += "! Description" + System.getProperty("line.separator");
		needMarkup += "| {{{NeedDescription}}}" + System.getProperty("line.separator");
		needMarkup += "|-" + System.getProperty("line.separator");
		needMarkup += "! Priority" + System.getProperty("line.separator");
		needMarkup += "| {{{Priority}}}" + System.getProperty("line.separator");
		needMarkup += "|}" + System.getProperty("line.separator");
		need.setTemplateMarkup(needMarkup);
		temps.add(need);

		Template nonfunc = new Template();
		nonfunc.setName("NonFunctionalReq");
		nonfunctionalMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		nonfunctionalMarkup += "! Type" + System.getProperty("line.separator");
		nonfunctionalMarkup += "| {{{NonFunType}}}" + System.getProperty("line.separator");
		nonfunctionalMarkup += "|-" + System.getProperty("line.separator");
		nonfunctionalMarkup += "! Description" + System.getProperty("line.separator");
		nonfunctionalMarkup += "| {{{NonFunDescription}}}" + System.getProperty("line.separator");
		nonfunctionalMarkup += "|-" + System.getProperty("line.separator");
		nonfunctionalMarkup += "! Features" + System.getProperty("line.separator");
		nonfunctionalMarkup += "| {{{Features}}}" + System.getProperty("line.separator");
		nonfunctionalMarkup += "|}" + System.getProperty("line.separator");
		nonfunc.setTemplateMarkup(nonfunctionalMarkup);
		temps.add(nonfunc);

		Template problem = new Template();
		problem.setName("Problem");
		problemMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		problemMarkup += "! Affects" + System.getProperty("line.separator");
		problemMarkup += "| {{{Affects}}}" + System.getProperty("line.separator");
		problemMarkup += "|-" + System.getProperty("line.separator");
		problemMarkup += "! The Impact of which is" + System.getProperty("line.separator");
		problemMarkup += "| {{{Impact}}}" + System.getProperty("line.separator");
		problemMarkup += "|-" + System.getProperty("line.separator");
		problemMarkup += "! A successful solution would be" + System.getProperty("line.separator");
		problemMarkup += "| {{{SuccessfulSolution}}}" + System.getProperty("line.separator");
		problemMarkup += "|}" + System.getProperty("line.separator");
		problem.setTemplateMarkup(problemMarkup);
		temps.add(problem);

		Template productPosition = new Template();
		productPosition.setName("ProductPosition");
		productPositionMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		productPositionMarkup += "! Who" + System.getProperty("line.separator");
		productPositionMarkup += "| {{{Who}}}" + System.getProperty("line.separator");
		productPositionMarkup += "|-" + System.getProperty("line.separator");
		productPositionMarkup += "! The ''Project Name''" + System.getProperty("line.separator");
		productPositionMarkup += "| {{{The}}}" + System.getProperty("line.separator");
		productPositionMarkup += "|-" + System.getProperty("line.separator");
		productPositionMarkup += "! That" + System.getProperty("line.separator");
		productPositionMarkup += "| {{{That}}}" + System.getProperty("line.separator");
		productPositionMarkup += "! Unlike" + System.getProperty("line.separator");
		productPositionMarkup += "| {{{unlike}}}" + System.getProperty("line.separator");
		productPositionMarkup += "! Our Product" + System.getProperty("line.separator");
		productPositionMarkup += "| {{{OurProduct}}}" + System.getProperty("line.separator");
		productPositionMarkup += "|}" + System.getProperty("line.separator");
		productPosition.setTemplateMarkup(productPositionMarkup);
		temps.add(productPosition);

		Template responsibility = new Template();
		responsibility.setName("Responsibility");
		responsibilityMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		responsibilityMarkup += "! Description" + System.getProperty("line.separator");
		responsibilityMarkup += "| {{{Description}}}" + System.getProperty("line.separator");
		responsibilityMarkup += "|-" + System.getProperty("line.separator");
		responsibilityMarkup += "! StakeHolder" + System.getProperty("line.separator");
		responsibilityMarkup += "| {{{StakeHolder}}}" + System.getProperty("line.separator");
		responsibilityMarkup += "|}" + System.getProperty("line.separator");
		responsibility.setTemplateMarkup(responsibilityMarkup);
		temps.add(responsibility);

		Template stakeHolder = new Template();
		stakeHolder.setName("StakeHolder");
		stakeHolderMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		stakeHolderMarkup += "! StakeHolder Description" + System.getProperty("line.separator");
		stakeHolderMarkup += "| {{{StakeHolderDescription}}}" + System.getProperty("line.separator");
		stakeHolderMarkup += "|}" + System.getProperty("line.separator");
		stakeHolder.setTemplateMarkup(stakeHolderMarkup);
		temps.add(stakeHolder);

		Template testCase = new Template();
		testCase.setName("TestCases");
		testCaseMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		testCaseMarkup += "! Requirement" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{Requirement}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|-" + System.getProperty("line.separator");
		testCaseMarkup += "! Type" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{Type}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|-" + System.getProperty("line.separator");
		testCaseMarkup += "! Settings" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{Settings}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|-" + System.getProperty("line.separator");
		testCaseMarkup += "! PreConditions" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{PreConditions}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|-" + System.getProperty("line.separator");
		testCaseMarkup += "! Description" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{Description}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|-" + System.getProperty("line.separator");
		testCaseMarkup += "! Results" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{Results}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|-" + System.getProperty("line.separator");
		testCaseMarkup += "! UseCases" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{UseCases}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|-" + System.getProperty("line.separator");
		testCaseMarkup += "! NonFun" + System.getProperty("line.separator");
		testCaseMarkup += "| {{{NonFun}}}" + System.getProperty("line.separator");
		testCaseMarkup += "|}" + System.getProperty("line.separator");
		testCase.setTemplateMarkup(testCaseMarkup);
		temps.add(testCase);

		Template useCase = new Template();
		useCase.setName("UseCase");
		useCaseMarkup = "{| class=\"wikitable\"" + System.getProperty("line.separator");
		useCaseMarkup += "! Description" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{Description}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Level" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{Level}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Primary Actor" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{PrimaryActor}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Stakeholders" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{StakeHolders}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Interests" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{Interests}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Pre-Conditions" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{PreConditions}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Success end condition" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{SuccessEndCondition}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Failure end condition" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{FailureEndCondition}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Minimal Guarantee" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{MinimalGuarantee}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Main Success Scenario" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{MainSuccessScenario}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Extensions" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{Extensions}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Special Requirements" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{SpecialRequirements}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|-" + System.getProperty("line.separator");
		useCaseMarkup += "! Features" + System.getProperty("line.separator");
		useCaseMarkup += "| {{{Features}}}" + System.getProperty("line.separator");
		useCaseMarkup += "|}" + System.getProperty("line.separator");
		useCase.setTemplateMarkup(useCaseMarkup);
		temps.add(useCase);

		markupParser = new MarkupParser();
		markupParser.setMarkupLanguage(lang);
	}

	/**
	 * Returns the mediawiki language objects
	 * that is aware of template markups.
	 * @return MediaWikiLanguage object
	 * */
	public MediaWikiLanguage getLanguage(){
		return lang;
	}
}
