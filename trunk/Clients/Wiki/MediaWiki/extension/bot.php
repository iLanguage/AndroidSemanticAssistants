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

  $wiki = new wikipediaapi();
  $user = htmlspecialchars($_GET["user"]);
  $pass = htmlspecialchars($_GET["pass"]);
  $page = htmlspecialchars($_GET["url"]);
  $engine = htmlspecialchars($_GET["engine"]);
  echo $wiki->login($user,$pass);
/**
  * This class is designed to provide a simplified interface to cURL which maintains cookies.
  * @author Cobi
  **/
class http {
	private $ch;
	private $uid;
	public $postfollowredirs;
	public $getfollowredirs;

	/**
	  * Our constructor function.  This just does basic cURL initialization.
	  * @return void
	  **/
	function __construct () {
		global $proxyhost, $proxyport, $proxytype;
		$this->ch = curl_init();
		$this->uid = dechex(rand(0,99999999));
		curl_setopt($this->ch,CURLOPT_COOKIEJAR,'/tmp/cluewikibot.cookies.'.$this->uid.'.dat');
		curl_setopt($this->ch,CURLOPT_COOKIEFILE,'/tmp/cluewikibot.cookies.'.$this->uid.'.dat');
		curl_setopt($this->ch,CURLOPT_MAXCONNECTS,100);
		curl_setopt($this->ch,CURLOPT_CLOSEPOLICY,CURLCLOSEPOLICY_LEAST_RECENTLY_USED);
		curl_setopt($this->ch,CURLOPT_USERAGENT,'ClueBot/1.1');
		if (isset($proxyhost) and isset($proxyport) and ($proxyport != null) and ($proxyhost != null)) {
			curl_setopt($this->ch,CURLOPT_PROXYTYPE,isset( $proxytype ) ? $proxytype : CURLPROXY_HTTP);
			curl_setopt($this->ch,CURLOPT_PROXY,$proxyhost);
			curl_setopt($this->ch,CURLOPT_PROXYPORT,$proxyport);
		}
		$this->postfollowredirs = 0;
		$this->getfollowredirs = 1;
	}

	/**
	  * Post to a URL.
	  * @param $url The URL to post to.
	  * @param $data The post-data to post, should be an array of key => value pairs.
	  * @return Data retrieved from the POST request.
	  **/
	function post ($url,$data) {
		$time = microtime(1);
		curl_setopt($this->ch,CURLOPT_URL,$url);
		curl_setopt($this->ch,CURLOPT_FOLLOWLOCATION,$this->postfollowredirs);
		curl_setopt($this->ch,CURLOPT_MAXREDIRS,10);
		curl_setopt($this->ch,CURLOPT_HEADER,0);
		curl_setopt($this->ch,CURLOPT_RETURNTRANSFER,1);
		curl_setopt($this->ch,CURLOPT_TIMEOUT,30);
		curl_setopt($this->ch,CURLOPT_CONNECTTIMEOUT,10);
		curl_setopt($this->ch,CURLOPT_POST,1);
		curl_setopt($this->ch,CURLOPT_POSTFIELDS, $data);
		curl_setopt($this->ch,CURLOPT_HTTPHEADER, array('Expect:'));
		$data = curl_exec($this->ch);		
global $logfd; if (!is_resource($logfd)) $logfd = fopen('php://stderr','w'); fwrite($logfd,'POST: '.$url.' ('.(microtime(1) - $time).' s) ('.strlen($data)." b)\n");
		return $data;
	}
 
	function read($page){
	    
	    curl_setopt($this->ch, CURLOPT_URL, $page);
	    curl_setopt($this->ch, CURLOPT_HEADER, 0);

	    // grab URL and pass it to the browser
	    return curl_exec($this->ch);
	}

	/**
	  * Get a URL.
	  * @param $url The URL to get.
	  * @return Data retrieved from the GET request.
	  **/
	function get ($url) {
		$time = microtime(1);
		curl_setopt($this->ch,CURLOPT_URL,$url);
		curl_setopt($this->ch,CURLOPT_FOLLOWLOCATION,$this->getfollowredirs);
		curl_setopt($this->ch,CURLOPT_MAXREDIRS,10);
		curl_setopt($this->ch,CURLOPT_HEADER,0);
		curl_setopt($this->ch,CURLOPT_RETURNTRANSFER,1);
		curl_setopt($this->ch,CURLOPT_TIMEOUT,30);
		curl_setopt($this->ch,CURLOPT_CONNECTTIMEOUT,10);
		curl_setopt($this->ch,CURLOPT_HTTPGET,1);
		$data = curl_exec($this->ch);
		global $logfd; if (!is_resource($logfd)) $logfd = fopen('php://stderr','w'); fwrite($logfd,'GET: '.$url.' ('.(microtime(1) - $time).' s) ('.strlen($data)." b)\n");
		return $data;
	}

	/**
	  * Our destructor.  Cleans up cURL and unlinks temporary files.
	  **/
	function __destruct () {
		curl_close($this->ch);
		@unlink('/tmp/cluewikibot.cookies.'.$this->uid.'.dat');
	}
}

 /**
         * This class is for interacting with Wikipedia's api.php API.
         **/
        class wikipediaapi {
                private $http;
                private $edittoken;
                private $tokencache;
                private $user, $pass;
		
                /**
                 * This is our constructor.
                 * @return void
                 **/
                function __construct () {
                        global $__wp__http;
                        if (!isset($__wp__http)) {
                                $__wp__http = new http;
                        }
                        $this->http = &$__wp__http;
                }

                /**
                 * This function takes a username and password and logs you into wikipedia.
                 * @param $user Username to login as.
                 * @param $pass Password that corrisponds to the username.
                 * @return void
                 **/
                function login ($user,$pass) {
			global $engine;
			$apiurl = $engine.'/api.php';
			global $page;
                        $this->user = $user;
                        $this->pass = $pass;
                        $x = unserialize($this->http->post($apiurl.'?action=login&format=php',array('lgname' => $user, 'lgpassword' => $pass)));
                        if($x['login']['result'] == 'Success')
                                return true;
                        if($x['login']['result'] == 'NeedToken') {                              
			$x = unserialize($this->http->post($apiurl.'?action=login&format=php',array('lgname' => $user, 'lgpassword' => $pass, 'lgtoken' => $x['login']['token'])));
                                if($x['login']['result'] == 'Success'){
                                        return $this->http->read($page);}
                        }
                        return false;
                }
        }
?> 