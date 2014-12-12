/*
* Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants
* 
* Copyright (C) 2014 Semantic Software Lab, http://www.semanticsoftware.info
* Rene Witte
* Bahar Sateli
* 
* This file is part of the Semantic Assistants architecture, and is 
* free software, licensed under the GNU Lesser General Public License 
* as published by the Free Software Foundation, either version 3 of 
* the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package info.semanticsoftware.semassist.android.encryption;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class CustomSSLSocketFactory extends SSLSocketFactory{
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public CustomSSLSocketFactory(KeyStore truststore)  throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException{
		super(truststore);
		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		try {
			sslContext.init(null, new TrustManager[] { tm }, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}
}
