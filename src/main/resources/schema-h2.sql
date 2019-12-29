CREATE TABLE t_transaction_categories(
  category_id BIGINT NOT NULL,
  transaction_id BIGINT NOT NULL,
  date_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
