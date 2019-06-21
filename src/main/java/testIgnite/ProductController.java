package testIgnite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.logger.log4j.Log4JLogger;
import org.apache.ignite.mxbean.CacheMetricsMXBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache.Entry;

@RestController
@RequestMapping("/product")
public class ProductController {

  private final Log4JLogger logger = new Log4JLogger();
  private static final String IGNITE_INSTANCE_NAME = "igniteInstance";

  @GetMapping("/{id}")
  public Product getProduct(@PathVariable String id) {
    IgniteCache<String, Product> cache = getCache();

    Product product = cache.get(id);
    CacheMetricsMXBean cacheMetricsMXBean = cache.localMxBean();
    logger.info("Avg get time for id " + id + " : " + cacheMetricsMXBean.getAverageGetTime());
    logger.info("Cache hits(get): " + cacheMetricsMXBean.getCacheHits());
    logger.info("Cache miss(get): " + cacheMetricsMXBean.getCacheMisses());
    logger.info("Cache size(get): " + cacheMetricsMXBean.getCacheSize());
    return product;
  }

  @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateProduct(@PathVariable String id, @RequestBody Price price) {
    IgniteCache<String, Product> cache = getCache();
    Product product = cache.get(id);
    if (product == null) {
      logger.error("Product " + id + " is not found in DB");
      return;
    }
    product.setList_price(price.getPrice());
    cache.put(product.getUniq_id(), product);

    CacheMetricsMXBean cacheMetricsMXBean = cache.localMxBean();
    logger.info("Avg put time for id " + id + " : " + cacheMetricsMXBean.getAveragePutTime());
    logger.info("Cache hits (put): " + cacheMetricsMXBean.getCacheHits());
    logger.info("Cache miss (put): " + cacheMetricsMXBean.getCacheMisses());
    logger.info("Cache size (put): " + cacheMetricsMXBean.getCacheSize());
  }

  @PostMapping("/loadCache")
  public void loadCache() {
    IgniteCache<String, Product> cache = getCache();
    CacheMetricsMXBean cacheMetricsMXBean = cache.localMxBean();

    cache.loadCache(null);

    logger.info("Cache size after loading: " + cacheMetricsMXBean.getCacheSize());
  }

  @PostMapping("/compute")
  public void compute() {
    final Ignite ignite = Ignition.ignite(IGNITE_INSTANCE_NAME);

    long startTime = System.currentTimeMillis();
    Map<String, Integer> finalResults = ignite.compute().call(() -> {
      IgniteCache<Long, Product> localCache = ignite.getOrCreateCache("product");
      localCache.loadCache(null);
      int lessThenZero = 0;
      int lessThenFifty = 0;
      int fiftyToHundred = 0;
      int hundredAndMore = 0;
      int invalidPrice = 0;
      Map<String, Integer> results = new HashMap<>();
      for (Entry<Long, Product> entry : localCache.localEntries()) {
        if (entry.getValue().getList_price() == null) {
          logger.error("Product " + entry.getValue().getUniq_id() + " has nullable price");
          continue;
        }
        double price = 0;
        try {
          price = new Double(entry.getValue().getList_price());
        } catch (NumberFormatException exc) {
          invalidPrice++;
        }
        if (price < 0) {
          lessThenZero++;
        }
        if (price < 50) {
          lessThenFifty++;
        } else if (price >= 50 && price < 100) {
          fiftyToHundred++;
        } else {
          hundredAndMore++;
        }
      }
      results.put("lessThenFifty", lessThenFifty);
      results.put("fiftyToHundred", fiftyToHundred);
      results.put("hundredAndMore", hundredAndMore);
      results.put("lessThenZero", lessThenZero);
      results.put("invalidPrice", invalidPrice);
      return results;
    });

    long elapsedTime = (System.currentTimeMillis() - startTime);

    logger.info("Computing results: " + finalResults.toString());
    logger.info("Computed in " + elapsedTime);
  }

  private IgniteCache<String, Product> getCache() {
    final Ignite ignite = Ignition.ignite(IGNITE_INSTANCE_NAME);
    return ignite.cache("product");
  }

}