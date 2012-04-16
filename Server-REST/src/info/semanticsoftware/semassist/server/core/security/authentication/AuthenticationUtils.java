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

package info.semanticsoftware.semassist.server.core.security.authentication;

import info.semanticsoftware.semassist.server.core.security.encryption.EncryptionUtils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationUtils {
	
	private static AuthenticationUtils instance = null;
	private Connection connection = null;
	
	protected AuthenticationUtils(){
		// defeat instantiation
	}
	
	public static AuthenticationUtils getInstance(){
		if ( instance == null) {
			instance = new AuthenticationUtils();
		} 
		
		return instance;
	}
	
	public void loadDBIntoMemory(){
		if(connection == null){
			try {
				Class.forName("org.h2.Driver");
				// Setup the connection with the DB
				connection = DriverManager.getConnection("jdbc:h2:~/H2DB/UsersDB", "dbadmin", "dbpass");
				connection.setAutoCommit(false);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean authenticateUser(String userName, String password){
		boolean isValidUser = false;
		loadDBIntoMemory();
		
		PreparedStatement prepStatement;
		try {
			prepStatement = connection.prepareStatement("SELECT password FROM USERS WHERE USERNAME = ?");
			prepStatement.setString(1, userName);
			ResultSet resultSet = prepStatement.executeQuery();
			
			if (resultSet.next()) {
				String retrievedPassword = resultSet.getString("password");
				if(retrievedPassword.equals(password)){
					isValidUser = true;
				}else{
					isValidUser = false;
				}
				return isValidUser;
			}else{
			    System.out.println("Authentication failed. No such credentials.");
			    return isValidUser = false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isValidUser;
	}
	
	public String getModulusString(String userName){
		loadDBIntoMemory();
		String pubKeyString = "null";
		try {
			PreparedStatement prepStatement = connection.prepareStatement("SELECT mod FROM USERS WHERE USERNAME = ?");
			prepStatement.setString(1, userName);
			System.out.println("Querying DB: " + prepStatement.toString());
			ResultSet result = prepStatement.executeQuery();
			while (result.next()) {
	            pubKeyString = result.getString("mod");
	        }
			return pubKeyString;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pubKeyString;
	}
	
	public String getPrivateKeyString(String userName){
		loadDBIntoMemory();
		String priKeyString = "null";
		try {
			PreparedStatement prepStatement = connection.prepareStatement("SELECT priKey FROM USERS WHERE USERNAME = ?");
			prepStatement.setString(1, userName);
			System.out.println("Querying DB: " + prepStatement.toString());
			ResultSet result = prepStatement.executeQuery();
			while (result.next()) {
	            priKeyString = result.getString("priKey");
	        }
			return priKeyString;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return priKeyString;
	}
	
	public PrivateKey getPrivateKey(String userName){
		PrivateKey priKey = null;
		String mod = getModulusString(userName);
		String priStr = getPrivateKeyString(userName);
		
		BigInteger modulus = new BigInteger(mod);
		BigInteger pri = new BigInteger(priStr);
		
		RSAPrivateKeySpec newSpec = new RSAPrivateKeySpec(modulus, pri);
		KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("RSA");
			priKey = fact.generatePrivate(newSpec);
			return priKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return priKey;		
	}
	
	public void addUser(String userName, String password, String devKey){
		loadDBIntoMemory();
		PreparedStatement prepStatement;
		try {
			//Generate key-pair for the user
			EncryptionUtils encUtil = EncryptionUtils.getInstance();
			KeyPair pair = encUtil.generateKeyPair();
			
			PublicKey pubkey = encUtil.getPublicKey(pair);
			BigInteger mod = encUtil.getModulus(pubkey);
			BigInteger pubEx = encUtil.getPubEx(pubkey);
			
			PrivateKey priKey = encUtil.getPrivateKey(pair);
			BigInteger priEx = encUtil.getPriEx(priKey);
			
			prepStatement = connection.prepareStatement("INSERT INTO USERS (USERNAME, PASSWORD, MOD, PRIKEY, PUBKEY) VALUES (?, ?, ?, ?, ?)");
			prepStatement.setString(1, userName);
			prepStatement.setString(2, password);
			//prepStatement.setBigDecimal(3, new BigDecimal(mod));
			prepStatement.setString(3, mod.toString());
			//prepStatement.setBigDecimal(4, new BigDecimal(priEx));
			prepStatement.setString(4, priEx.toString());
			//prepStatement.setBigDecimal(5, new BigDecimal(pubEx));
			prepStatement.setString(5, pubEx.toString());
			
			/*if(devKey != null){
				prepStatement.setBigDecimal(6, new BigDecimal(new BigInteger(devKey)));
			}*/
			
			try{ 
				prepStatement.executeUpdate();
				connection.commit();
				System.out.println("New user added!");
			} catch (SQLException e){
				e.printStackTrace();
				if (connection != null){
					System.out.println("Exception occured in database. Rolling back all the changes made in this transaction and releasing the lock.");
					connection.rollback();
				}
			} 

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
			System.out.println("Database is locked by another process");
		}
	}
	
	public static void main(String args[]){
		AuthenticationUtils obj = AuthenticationUtils.getInstance();
		obj.addUser(args[0], args[1], null);
	}
}
