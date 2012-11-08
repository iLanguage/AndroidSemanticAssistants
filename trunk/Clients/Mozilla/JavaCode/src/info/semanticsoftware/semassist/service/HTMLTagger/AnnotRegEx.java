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

package info.semanticsoftware.semassist.service.HTMLTagger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotRegEx {
	
	public static void main(String args[]){
		//String text = "Find me in h<b key='value'><e>e<i>r</i>e</b> or he<b>o</b>re or her<b>o</b> or find me there. Wait a minute, this is not the correct fiind!";
		String text = "the<b id='1'>re</b> he<b id='2'>re<i>r</i>e</b> h<b id='3'>e</i>r</i>e</b> h<b id='4'>e</i>r</i>e</b> or he<b>o</b>re or her<b>o</b> or find me there.";
		String regex="[Ff]ind\\w*";
		
		String regex3="(\\</?(.)+?\\>+)";
		System.out.println(text);
		
		String cleanText = text.replaceAll(regex3, "");
		System.out.println(cleanText);
		
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(text);
		
		 while (matcher.find()) {
			  //System.out.println("Starting & ending index of " + matcher.group()+ ": " + "start=" + matcher.start() + " end = " + matcher.end());
		 }
		 
		 if (matcher.matches()) {
			  System.out.println("m1.matches() start = " + matcher.start() + " end = " + matcher.end());
		 }
		 
		 //System.out.println("=======");
		 
		 p = Pattern.compile(regex3);
		matcher = p.matcher(text);
			
			 while (matcher.find()) {
				 // System.out.println("Starting & ending index of " + matcher.group()+ ": " + "start=" + matcher.start() + " end = " + matcher.end());
			 }
			 
			 //System.out.println("=======");
		 
		//String regex4="here\\w.+[(\\</?(.)+?\\>+)]*?(h|e|r|e).+";
			 //HTML TAGS
			 String regex4="(\\<(/?[^\\>]+)\\>)";
			 String regex5 = "(\\s|-)[h]{1}([e]{1})?([r]{1})?" + regex4 + "";
			 p = Pattern.compile(regex5);
			matcher = p.matcher(text);
				String temp = "";
				 while (matcher.find()) {
					 System.out.println("------------------------------");
					 String match = matcher.group();
					  System.out.println("Found " +  match + ": " + "start=" + matcher.start() + " end = " + matcher.end());
					  //length of the HTML TAG
					  //System.out.println(matcher.group().length());
					  String rest = text.substring(matcher.end());
					  //System.out.println(rest);
					  temp = text;
					  String annotPart = match.replaceAll("-", "").substring(0, match.replaceAll("-", "").indexOf("<"));
					  if(isTheOne(annotPart, rest, "here")){
						  System.out.println("True match of offset " + matcher.start());
						  
						  temp += temp.substring(0, matcher.start()) + "<annotation style='background-color: red;'>" + annotPart  + "</annotation>" + temp.substring(matcher.start() + matcher.group().length());
						 temp = temp.substring(text.length());
						  System.out.println("Result: " + temp);
					  }
				 }
	}
	
	private static boolean isTheOne(String annotPart, String rest, String annotOriginal){
		annotPart = annotPart.trim();
		//System.out.println("FOUND PART: " + annotPart);
		String annotOriginalTemp = annotOriginal.substring(annotPart.length());
		//System.out.println("YET TO FIND: " + annotOriginalTemp);
		//System.out.println("IN " + rest);
		String regex="(\\<(/?[^\\>]+)\\>)";
		String strippedRest = rest.replaceAll(regex, "");
		//System.out.println("IN: " + strippedRest);
		if(annotOriginalTemp.subSequence(0, 1).equals(strippedRest.subSequence(0, 1))){
			//System.out.println("True positive so far");
			annotOriginalTemp = annotOriginalTemp.substring(1);
			strippedRest = strippedRest.substring(1);
			//System.out.println("Testing " + annotOriginalTemp);
			while(annotOriginalTemp.length() > 0){
				if(annotOriginalTemp.subSequence(0, 1).equals(strippedRest.subSequence(0, 1))){
					//System.out.println("yes");
					annotOriginalTemp = annotOriginalTemp.substring(1);
					strippedRest = strippedRest.substring(1);
				}else{
					return false;
				}
			}
			//for when what we found is attached to something else like "man" in " manner"
			if(!strippedRest.substring(0, 1).equals(" ")){
				//System.out.println("I've found the annotation but...");
				//System.out.println("we still have something left:*" + strippedRest);
				return false;
			}
			return true;
		}else{
			//System.out.println("False positive");
			return false;
		}
	}
	
	public static void multiNode(String HTML){
		
	}

}
