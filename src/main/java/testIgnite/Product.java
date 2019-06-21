package testIgnite;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * Created by oogar on 4/22/19.
 */
public class Product {

  @QuerySqlField(index = true)
  private String uniq_id;

  @QuerySqlField
  private String sku;
  @QuerySqlField
  private String name_title;
  @QuerySqlField
  private String description;
  @QuerySqlField
  private String list_price;
  @QuerySqlField
  private String sale_price;
  @QuerySqlField
  private String category;
  @QuerySqlField
  private String category_tree;
  @QuerySqlField
  private String average_product_rating;
  @QuerySqlField
  private String product_url;
  @QuerySqlField
  private String product_image_urls;
  @QuerySqlField
  private String brand;
  @QuerySqlField
  private Integer total_number_reviews;
  @QuerySqlField
  private String reviews;

  public Product() {
    // No-op.
  }

  public String getUniq_id() {
    return uniq_id;
  }

  public void setUniq_id(String uniq_id) {
    this.uniq_id = uniq_id;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName_title() {
    return name_title;
  }

  public void setName_title(String name_title) {
    this.name_title = name_title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getList_price() {
    return list_price;
  }

  public void setList_price(String list_price) {
    this.list_price = list_price;
  }

  public String getSale_price() {
    return sale_price;
  }

  public void setSale_price(String sale_price) {
    this.sale_price = sale_price;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getCategory_tree() {
    return category_tree;
  }

  public void setCategory_tree(String category_tree) {
    this.category_tree = category_tree;
  }

  public String getAverage_product_rating() {
    return average_product_rating;
  }

  public void setAverage_product_rating(String average_product_rating) {
    this.average_product_rating = average_product_rating;
  }

  public String getProduct_url() {
    return product_url;
  }

  public void setProduct_url(String product_url) {
    this.product_url = product_url;
  }

  public String getProduct_image_urls() {
    return product_image_urls;
  }

  public void setProduct_image_urls(String product_image_urls) {
    this.product_image_urls = product_image_urls;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Integer getTotal_number_reviews() {
    return total_number_reviews;
  }

  public void setTotal_number_reviews(Integer total_number_reviews) {
    this.total_number_reviews = total_number_reviews;
  }

  public String getReviews() {
    return reviews;
  }

  public void setReviews(String reviews) {
    this.reviews = reviews;
  }

}