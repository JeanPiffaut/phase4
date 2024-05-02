/*
 * Copyright (C) 2023-2024 Gregor Scholtysik (www.soptim.de)
 * gregor[dot]scholtysik[at]soptim[dot]de
 *
 * Copyright (C) 2023-2024 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.phase4.euctp;

import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;

import com.helger.commons.ws.TrustManagerTrustAll;
import com.helger.http.tls.ETLSVersion;
import com.helger.http.tls.TLSConfigurationMode;
import com.helger.httpclient.HttpClientSettings;
import com.helger.phase4.CAS4;
import com.helger.phase4.CAS4Version;

/**
 * Special {@link HttpClientSettings} with better defaults for euctp.
 *
 * @author Ulrik Stehling
 */
public class Phase4EuCtpHttpClientSettings extends HttpClientSettings
{
	public static final Timeout DEFAULT_EUCTP_CONNECTION_REQUEST_TIMEOUT = Timeout.ofSeconds(1);
	public static final Timeout DEFAULT_EUCTP_CONNECT_TIMEOUT = Timeout.ofSeconds(5);
	public static final Timeout DEFAULT_EUCTP_RESPONSE_TIMEOUT = Timeout.ofSeconds(100);

	public Phase4EuCtpHttpClientSettings(KeyStore keyStore, char[] keyPassword) throws GeneralSecurityException
	{
		// BDEW recommends at least TLS v1.2 [TR02102-2]
		final SSLContext aSSLContext = SSLContexts.custom()
				.setProtocol(ETLSVersion.TLS_12.getID())
				.loadKeyMaterial(keyStore, keyPassword) // needed for client certificate
				.build();

		setSSLContext(aSSLContext);

		// Cipher Suite follow https://www.enisa.europa.eu/publications/algorithms-key-sizes-and-parameters-report
		final TLSConfigurationMode aTLSConfigMode = new TLSConfigurationMode(new ETLSVersion[]{ETLSVersion.TLS_12,
				ETLSVersion.TLS_13},
				new String[]{
						// TLS
						// 1.3
						"TLS_AES_128_GCM_SHA256",
						"TLS_AES_256_GCM_SHA384",
						// TLS
						// 1.2
						"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
						"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
						"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
						"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"});
		setTLSConfigurationMode(aTLSConfigMode);

		setConnectionRequestTimeout(DEFAULT_EUCTP_CONNECTION_REQUEST_TIMEOUT);
		setConnectTimeout(DEFAULT_EUCTP_CONNECT_TIMEOUT);
		setResponseTimeout(DEFAULT_EUCTP_RESPONSE_TIMEOUT);

		// Set an explicit user agent
		setUserAgent(CAS4.LIB_NAME + "/" + CAS4Version.BUILD_VERSION + " " + CAS4.LIB_URL);
	}
}
