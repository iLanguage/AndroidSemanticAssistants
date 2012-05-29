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
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationUtils {

	/** Singleton class object */
	private static AuthenticationUtils instance = null;
	/** Database connection object */
	private Connection connection = null;

	/** Protected class constructor to defeat instantiation. */
	protected AuthenticationUtils(){
		// defeat instantiation
	}

	/** Returns the singleton object.
	 * @return singleton object
	 */
	public static AuthenticationUtils getInstance(){
		if ( instance == null) {
			instance = new AuthenticationUtils();
		} 
		return instance;
	}

	/** Loads the H2 database object into memory. */
	public void loadDBIntoMemory(){
		if(connection == null){
			try {
				Class.forName("org.h2.Driver");
				// Setup the connection with the DB
				//TODO move the credentials to the servlet's properties
				connection = DriverManager.getConnection("jdbc:h2:~/H2DB/UsersDB", "dbadmin", "dbpass");
				connection.setAutoCommit(false);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/** Authenticates the provided user credentials against the database.
	 * @param userName username
	 * @param password password
	 * @return true if the credentials are valid, false otherwise */
	public boolean authenticateUser(final String userName, final String password){
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

	/** Returns the modulus part of the specified username's public/private keys.
	 * @param userName username
	 * @return string representation of the modulus
	 */
	public String getModulusString(final String userName){
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

	/** Returns the private exponent of the specified username's private key.
	 * @param userName username
	 * @return string representation of the private exponent
	 */
	public String getPrivateKeyString(final String userName){
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

	/** Returns the specified username's private key object.
	 * @param userName username
	 * @return user's private key object
	 */
	public PrivateKey getPrivateKey(final String userName){
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
	
	public PublicKey getPublicKey(final String userName){
		PublicKey pubKey = null;
		String mod = getModulusString(userName);
		
		BigInteger modulus = new BigInteger(mod);
		BigInteger pub = new BigInteger("65537");
		
		RSAPublicKeySpec newSpec = new RSAPublicKeySpec(modulus, pub);
		KeyFactory fact;
		try {
			fact = KeyFactory.getInstance("RSA");
			pubKey = fact.generatePublic(newSpec);
			return pubKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return pubKey;
	}

	/** Adds a new user to the USERS database.
	 * @param userName username
	 * @param password password
	 * @param devKey user's developer key (optional) */
	public void addUser(final String userName, final String password, final String devKey){
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
			prepStatement.setString(3, mod.toString());
			prepStatement.setString(4, priEx.toString());
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

	/** Convenience method to add users to the database via commandline
	 * @param args commandline arguments
	 */
	public static void main(String args[]){
		AuthenticationUtils obj = AuthenticationUtils.getInstance();
		obj.addUser(args[0], args[1], null);
	}
}
