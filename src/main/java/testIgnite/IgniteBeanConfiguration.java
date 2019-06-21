package testIgnite;

import org.apache.commons.io.FileUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.store.cassandra.CassandraCacheStoreFactory;
import org.apache.ignite.cache.store.cassandra.datasource.DataSource;
import org.apache.ignite.cache.store.cassandra.persistence.KeyValuePersistenceSettings;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.logger.log4j.Log4JLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.policies.RoundRobinPolicy;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;


@Configuration
public class IgniteBeanConfiguration {

  @Bean
  public Ignite igniteInstance() throws IOException, IgniteCheckedException {

    CacheConfiguration<String, Product> configuration = new CacheConfiguration<>();
    configuration.setStatisticsEnabled(true);
    configuration.setIndexedTypes(String.class, Product.class);
    configuration.setWriteThrough(true);
    configuration.setReadThrough(true);
    configuration.setName("product");
    configuration.setCacheMode(CacheMode.PARTITIONED);
    configuration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));

    DataSource dataSource = new DataSource();
    dataSource.setContactPoints("127.0.0.1:9042");
    RoundRobinPolicy robinPolicy = new RoundRobinPolicy();
    dataSource.setLoadBalancingPolicy(robinPolicy);
    dataSource.setReadConsistency("ONE");
    dataSource.setWriteConsistency("ONE");
    String persistenceSettingsXml = FileUtils.readFileToString(new File("config/persistence_settings.xml"), "utf-8");
    KeyValuePersistenceSettings persistenceSettings = new KeyValuePersistenceSettings(persistenceSettingsXml);

    CassandraCacheStoreFactory cacheStoreFactory = new CassandraCacheStoreFactory();
    cacheStoreFactory.setDataSource(dataSource);
    cacheStoreFactory.setPersistenceSettings(persistenceSettings);
    configuration.setCacheStoreFactory(cacheStoreFactory);

    TcpDiscoverySpi spi = new TcpDiscoverySpi();
    TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
    ipFinder.setMulticastGroup("228.10.10.157");
    spi.setIpFinder(ipFinder);

    IgniteConfiguration cfg = igniteCfg();
    cfg.setCacheConfiguration(configuration);
    cfg.setDiscoverySpi(spi);
    cfg.setPeerClassLoadingEnabled(true);
    return Ignition.start(cfg);
  }

  @Bean
  public IgniteConfiguration igniteCfg() throws IgniteCheckedException {
    IgniteConfiguration cfg = new IgniteConfiguration();
    cfg.setIgniteInstanceName("igniteInstance");
    cfg.setPeerClassLoadingEnabled(true);
    URL xml = U.resolveIgniteUrl("config/custom-log4j.xml");
    IgniteLogger log = new Log4JLogger(xml);
    cfg.setGridLogger(log);
    return cfg;
  }
}
