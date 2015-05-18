/**
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */
package org.mule.module.mongo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.module.mongo.api.MongoClient;
import org.mule.module.mongo.api.MongoClientAdaptor;
import org.mule.module.mongo.api.MongoClientImpl;
import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;

@ConnectionManagement(friendlyName="ConnectionManagement", configElementName="config")
public class ConnectionManagementStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManagementStrategy.class);

    /**
     * The host of the Mongo server, it can also be a list of comma separated hosts for replicas
     */
    @Configurable
    @Default("localhost")
    private String host;

    /**
     * The port of the Mongo server
     */
    @Configurable
    @Optional
    @Default("27017")
    private int port;

    /**
     * The number of connections allowed per host (the pool size, per host)
     */
    @Configurable
    @Optional
    public Integer connectionsPerHost;

    /**
     * Multiplier for connectionsPerHost for # of threads that can block
     */
    @Configurable
    @Optional
    public Integer threadsAllowedToBlockForConnectionMultiplier;

    /**
     * The max wait time for a blocking thread for a connection from the pool in ms.
     */
    @Configurable
    @Optional
    public Integer maxWaitTime;

    /**
     * The connection timeout in milliseconds; this is for establishing the socket connections
     * (open). 0 is default and infinite.
     */
    @Configurable
    @Optional
    @Default("30000")
    private Integer connectTimeout;

    /**
     * The socket timeout. 0 is default and infinite.
     */
    @Configurable
    @Optional
    private Integer socketTimeout;

    /**
     * This controls whether the system retries automatically on connection errors.
     */
    @Configurable
    @Optional
    private Boolean autoConnectRetry;

    private String database;

    private Mongo mongo;

    private MongoClient client;

    public MongoClient getClient() {
		return client;
	}
    
    /**
     * Method invoked when a {@link MongoSession} needs to be created.
     * 
     * @param username the username to use for authentication. NOTE: Please use a dummy user if you
     *            have disabled Mongo authentication
     * @param password the password to use for authentication. If the password is null or whitespaces only the connector
     *                 won't use authentication.
     * @param database Name of the database
     * @return the newly created {@link MongoSession}
     * @throws org.mule.api.ConnectionException
     */
    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey final String username,
                        @Optional @Password final String password,
                        @ConnectionKey final String database) throws ConnectionException
    {
        try
        {
            mongo = new com.mongodb.MongoClient(getMongoClientURI(username, password, database));
            this.client = new MongoClientImpl(getDatabase(mongo, username, password, database));
            
            DB db = mongo.getDB(database);
            db.getStats();
        }
        catch (final UnknownHostException ex)
        {
            LOGGER.info(ex.getMessage(), ex); 
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN_HOST, ex.getLocalizedMessage(), ex.getMessage(), ex.getCause());
        }
        catch (final MongoException.Network e)
        {
            LOGGER.info(e.getMessage(), e);
            throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, e.getLocalizedMessage(), e.getMessage(), e.getCause());
        }
        catch (final IllegalArgumentException e)
        {
            LOGGER.info(e.getMessage(), e);
            throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, e.getLocalizedMessage(), e.getMessage(), e.getCause());
        }
    }

    private MongoClientOptions.Builder getMongoOptions(String database) {
        final MongoClientOptions.Builder options = MongoClientOptions.builder();

        if (connectionsPerHost != null)
        {
            options.connectionsPerHost(connectionsPerHost);
        }
        if (threadsAllowedToBlockForConnectionMultiplier != null)
        {
            options.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);
        }
        if (maxWaitTime != null)
        {
            options.maxWaitTime(maxWaitTime);
        }
        if (connectTimeout != null)
        {
            options.connectTimeout(connectTimeout);
        }
        if (socketTimeout != null)
        {
            options.socketTimeout(socketTimeout);
        }
        if (autoConnectRetry != null)
        {
            options.autoConnectRetry(autoConnectRetry);
        }
        if (database != null)
        {
            this.database = database;
        }
        return options;
    }

    /**
     * Method invoked when the {@link MongoSession} is to be destroyed.
     * 
     * @throws IOException in case something goes wrong when disconnecting.
     */
    @Disconnect
    public void disconnect() 
    {
        if (client != null)
        {
            try
            {
                client.close();
            }
            catch (final Exception e)
            {
                LOGGER.warn("Failed to properly close client: " + client, e);
            }
            finally
            {
                client = null;
            }
        }
        
        if (mongo != null)
        {
            try
            {
                mongo.close();
            }
            catch (final Exception e)
            {
                LOGGER.warn("Failed to properly close mongo: " + mongo, e);
            }
            finally
            {
                mongo = null;
            }
        }
    }

    @ValidateConnection
    public boolean isConnected()
    {
        return this.client != null && this.mongo != null && mongo.getConnector().isOpen();
    }


    private MongoClientURI getMongoClientURI(final String username, final String password, final String database) {
        List<String> hostsWithPort = new LinkedList<String>();
        for (String hostname : host.split(",\\s?")) {
            hostsWithPort.add(hostname + ":" + port);
        }
        return new MongoClientURI(MongoURI.MONGODB_PREFIX +
//                        username + ":" + password + "@" +
                        StringUtils.join(hostsWithPort, ",") +
                        "/" + database
                , getMongoOptions(database));
    }

    @ConnectionIdentifier
    public String connectionId()
    {
        return mongo == null ? "n/a" : mongo.toString();
    }

    private DB getDatabase(final Mongo mongo,
                           final String username,
                           final String password,
                           final String database) throws ConnectionException
    {
        final DB db = mongo.getDB(database);
        if (StringUtils.isNotBlank(password))
        {
            Validate.notNull(username, "Username must not be null if password is set");
            if (!db.isAuthenticated() && !db.authenticate(username, password.toCharArray()))
            {
                throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, null,
                        "Couldn't connect with the given credentials");
            }
        }
        return db;
    }

    protected MongoClient adaptClient(final MongoClient client)
    {
        return MongoClientAdaptor.adapt(client);
    }

    
    public Mongo getMongo() {
		return mongo;
	}

    public String getHost()
    {
        return host;
    }

    public void setHost(final String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(final int port)
    {
        this.port = port;
    }

    public String getDatabase()
    {
        return database;
    }

    public void setDatabase(final String database)
    {
        this.database = database;
    }

    public Integer getConnectionsPerHost()
    {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(final Integer connectionsPerHost)
    {
        this.connectionsPerHost = connectionsPerHost;
    }

    public Integer getThreadsAllowedToBlockForConnectionMultiplier()
    {
        return threadsAllowedToBlockForConnectionMultiplier;
    }

    public void setThreadsAllowedToBlockForConnectionMultiplier(final Integer threadsAllowedToBlockForConnectionMultiplier)
    {
        this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }

    public Integer getMaxWaitTime()
    {
        return maxWaitTime;
    }

    public void setMaxWaitTime(final Integer maxWaitTime)
    {
        this.maxWaitTime = maxWaitTime;
    }

    public Integer getConnectTimeout()
    {
        return connectTimeout;
    }

    public void setConnectTimeout(final Integer connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout()
    {
        return socketTimeout;
    }

    public void setSocketTimeout(final Integer socketTimeout)
    {
        this.socketTimeout = socketTimeout;
    }

    public Boolean getAutoConnectRetry()
    {
        return autoConnectRetry;
    }

    public void setAutoConnectRetry(final Boolean autoConnectRetry)
    {
        this.autoConnectRetry = autoConnectRetry;
    }

//
//	
//	/**
//	 * Mongo Host
//	 */
//	@Configurable
//	@Placement(group = "Connection")
//	private String host;
//
//	/**
//	 * Mongo Port
//	 */
//	@Configurable
//	@Placement(group = "Connection")
//	private String port;

//
//	/**
//	 * Connect
//	 *
//	 * @param username
//	 *            A username
//	 * @param password
//	 *            A password
//	 * @throws ConnectionException
//	 */
//	@Connect
//	@TestConnectivity
//	public void connect(@ConnectionKey String username,
//			@Password String password) throws ConnectionException {
//
//		ServiceCxfClient cxfClient = new ServiceCxfClient(responsibilityName,
//				responsibilityApplName, securityGroupName, nlsLanguage, orgId);
//		client = OracleEBSFinancialAdapter.adapt(cxfClient);
//		setBasePath(setUrl(getBasePath(), this.host, this.port));
//		cxfClient.setBasePath(getBasePath());
//		setBasePathPlSql(setUrl(getBasePathPlSql(), this.host, this.port));
//		cxfClient.setBasePathPlSql(getBasePathPlSql());
//		cxfClient.setDqmSearchServiceUrl(getDqmSearchServiceUrl());
//		cxfClient.setEmailServiceUrl(getEmailServiceUrl());
//		cxfClient.setLocationServiceUrl(getLocationServiceUrl());
//		cxfClient.setOrgContactServiceUrl(getOrgContactServiceUrl());
//		cxfClient.setOrganizationServiceUrl(getOrganizationServiceUrl());
//		cxfClient.setOrgCustomerServiceUrl(getOrgCustomerServiceUrl());
//		cxfClient.setPartySiteServiceUrl(getPartySiteServiceUrl());
//		cxfClient.setPersonServiceUrl(getPersonServiceUrl());
//		cxfClient.setPersonCustomerServiceUrl(getPersonCustomerServiceUrl());
//		cxfClient.setPhoneServiceUrl(getPhoneServiceUrl());
//		cxfClient.setRelationshipServiceUrl(getRelationshipServiceUrl());
//		cxfClient.setWebServiceUrl(getWebServiceUrl());
//		cxfClient.setSecServiceUrl(getBasePathPlSql() + "/fnd_web_sec/?wsdl");
//		cxfClient.setCredentials(username, password);
//		testConnectivity(username, password);
//	}
//
//	/**
//	 * Are we connected
//	 */
//	@ConnectionIdentifier
//	public String connectionId() {
//		return "001";
//	}
//
//	/**
//	 * Disconnect
//	 */
//	@Disconnect
//	public void disconnect() {
//		client = null;
//	}
//
//	/**
//	 * Retrieves endpoint
//	 *
//	 */
//	public String getBasePath() {
//		return basePath;
//	}
//
//	/**
//	 * Retrieves endpoint for PL/SQL
//	 *
//	 */
//	public String getBasePathPlSql() {
//		return basePathPlSql;
//	}
//
//    /**
//     * Returns the list of custom PL/SQL names for web services defined.
//     *
//     * @return the values.
//     */
//    public List<String> getCustomPlSqlNameList() {
//        return customPlSqlNameList;
//    }
//
//    /**
//     * Sets the custom PL/SQL names for web services list to be used for metadata extraction.
//     *
//     * @param customPlSqlNameList the name list of custom PL/SQL.
//     */
//    public void setCustomPlSqlNameList(List<String> customPlSqlNameList) {
//        this.customPlSqlNameList = customPlSqlNameList;
//    }
//
//	/**
//	 * @return the client
//	 */
//	public OracleEBSFinancialServices getClient() {
//		return client;
//	}
//
//	public String getDqmSearchServiceUrl() {
//		return dqmSearchServiceUrl;
//	}
//
//	public String getEmailServiceUrl() {
//		return emailServiceUrl;
//	}
//
//	/**
//	 * Retrieves host
//	 *
//	 */
//	public String getHost() {
//		return this.host;
//	}
//
//
//	public String getLocationServiceUrl() {
//		return locationServiceUrl;
//	}
//
//	public String getNlsLanguage() {
//		return nlsLanguage;
//	}
//
//	public String getOrganizationServiceUrl() {
//		return organizationServiceUrl;
//	}
//
//	public String getOrgContactServiceUrl() {
//		return orgContactServiceUrl;
//	}
//
//	public String getOrgCustomerServiceUrl() {
//		return orgCustomerServiceUrl;
//	}
//
//	public String getOrgId() {
//		return orgId;
//	}
//
//	public String getPartySiteServiceUrl() {
//		return partySiteServiceUrl;
//	}
//
//	public String getPersonCustomerServiceUrl() {
//		return personCustomerServiceUrl;
//	}
//
//	public String getPersonServiceUrl() {
//		return personServiceUrl;
//	}
//
//	public String getPhoneServiceUrl() {
//		return phoneServiceUrl;
//	}
//
//	/**
//	 * Retrieves port
//	 *
//	 */
//	public String getPort() {
//		return this.port;
//	}
//
//	public String getRelationshipServiceUrl() {
//		return relationshipServiceUrl;
//	}
//
//	public String getResponsibilityApplName() {
//		return responsibilityApplName;
//	}
//
//	public String getResponsibilityName() {
//		return responsibilityName;
//	}
//
//	public String getSecurityGroupName() {
//		return securityGroupName;
//	}
//
//	private SOAHeader getSOAHeader() {
//		if (soaheader == null) {
//			soaheader = new SOAHeader();
//			soaheader.setNLSLanguage(getNlsLanguage());
//			soaheader.setRespApplication(getResponsibilityApplName());
//			soaheader.setResponsibility(getResponsibilityName());
//			soaheader.setSecurityGroup(getSecurityGroupName());
//			soaheader.setOrgId(getOrgId());
//		}
//		return soaheader;
//	}
//
//	public String getWebServiceUrl() {
//		return webServiceUrl;
//	}
//
//	/**
//	 * Are we connected
//	 */
//	@ValidateConnection
//	public boolean isConnected() {
//		return client != null;
//	}
//
//	/**
//	 * Sets endpoint
//	 *
//	 * @param value
//	 *            Value to set
//	 */
//	public void setBasePath(String value) {
//		this.basePath = value;
//	}
//
//	/**
//	 * Sets endpoint for PL/SQL
//	 *
//	 * @param basePathPlSql
//	 *            Value to set
//	 */
//	public void setBasePathPlSql(String basePathPlSql) {
//		this.basePathPlSql = basePathPlSql;
//	}
//
//	/**
//	 * @param client the client to set
//	 */
//	public void setClient(OracleEBSFinancialServices client) {
//		this.client = client;
//	}
//
//	public void setDqmSearchServiceUrl(String dqmSearchServiceUrl) {
//		this.dqmSearchServiceUrl = dqmSearchServiceUrl;
//	}
//
//	public void setEmailServiceUrl(String emailServiceUrl) {
//		this.emailServiceUrl = emailServiceUrl;
//	}
//
//	/**
//	 * Sets host
//	 *
//	 * @param value
//	 *            Value to set
//	 */
//	public void setHost(String value) {
//		this.host = value;
//	}
//
//	public void setLocationServiceUrl(String locationServiceUrl) {
//		this.locationServiceUrl = locationServiceUrl;
//	}
//
//	public void setNlsLanguage(String nlsLanguage) {
//		this.nlsLanguage = nlsLanguage;
//	}
//
//	public void setOrganizationServiceUrl(String organizationServiceUrl) {
//		this.organizationServiceUrl = organizationServiceUrl;
//	}
//
//	public void setOrgContactServiceUrl(String orgContactServiceUrl) {
//		this.orgContactServiceUrl = orgContactServiceUrl;
//	}
//
//	public void setOrgCustomerServiceUrl(String orgCustomerServiceUrl) {
//		this.orgCustomerServiceUrl = orgCustomerServiceUrl;
//	}
//
//	public void setOrgId(String orgId) {
//		this.orgId = orgId;
//	}
//
//	public void setPartySiteServiceUrl(String partySiteServiceUrl) {
//		this.partySiteServiceUrl = partySiteServiceUrl;
//	}
//
//	public void setPersonCustomerServiceUrl(String personCustomerServiceUrl) {
//		this.personCustomerServiceUrl = personCustomerServiceUrl;
//	}
//
//	public void setPersonServiceUrl(String personServiceUrl) {
//		this.personServiceUrl = personServiceUrl;
//	}
//
//	public void setPhoneServiceUrl(String phoneServiceUrl) {
//		this.phoneServiceUrl = phoneServiceUrl;
//	}
//
//	/**
//	 * Sets port
//	 *
//	 * @param value
//	 *            Value to set
//	 */
//	public void setPort(String value) {
//		this.port = value;
//	}
//
//	public void setRelationshipServiceUrl(String relationshipServiceUrl) {
//		this.relationshipServiceUrl = relationshipServiceUrl;
//	}
//
//	public void setResponsibilityApplName(String responsibilityApplName) {
//		this.responsibilityApplName = responsibilityApplName;
//	}
//
//	public void setResponsibilityName(String responsibilityName) {
//		this.responsibilityName = responsibilityName;
//	}
//
//	public void setSecurityGroupName(String securityGroupName) {
//		this.securityGroupName = securityGroupName;
//	}
//
//	/**
//	 * Sets the host and port to the base path
//	 * @param path String base path
//	 * @param host String host
//	 * @param port String port
//	 * @return complete url
//	 */
//	private String setUrl(String path, String host, String port) {
//		String url = path;
//
//		if (host.startsWith("http://") || host.startsWith("http:\\")) {
//			url = url.replace("<host>", host.substring(7));
//		} else {
//			url = url.replace("<host>", host);
//		}
//
//		url = url.replace("<port>", port);
//
//		return url;
//	}
//
//	public void setWebServiceUrl(String webServiceUrl) {
//		this.webServiceUrl = webServiceUrl;
//	}
//
//	/**
//	 * Checks if credentials are valid
//	 *
//	 * @param username
//	 *            A username
//	 * @param password
//	 *            A password
//	 * @throws ConnectionException
//	 */
//	private void testConnectivity(String username, String password) throws ConnectionException {
//		InputParameters ip = new InputParameters();
//		ip.setPUSER(username);
//		ip.setPPWD(password);
//		OutputParameters op = client.validateLogin(getSOAHeader(),ip);
//		String result = op.getVALIDATELOGIN();
//		if(result.equals("N")){
//			throw new ConnectionException(
//					ConnectionExceptionCode.INCORRECT_CREDENTIALS, result, "Invalid credentials");
//		}
//	}
}
