BEGIN TRANSACTION;
DROP TABLE IF EXISTS `test_table`;
CREATE TABLE IF NOT EXISTS `test_table` (
	`id`	INTEGER NOT NULL,
	`f_date`	INTEGER,
	PRIMARY KEY(`id`)
);
DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
	`product_id`	INTEGER NOT NULL,
	`order_id`	INTEGER,
	`product_name`	TEXT NOT NULL,
	`product_unit_name`	TEXT,
	`product_counter_precision`	INT,
	PRIMARY KEY(`product_id`)
);
DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
	`payment_id`	INTEGER NOT NULL,
	`order_id`	INTEGER,
	`payment_date`	INTEGER NOT NULL,
	`payment_period_date`	INTEGER NOT NULL,
	`payment_object_id`	INTEGER NOT NULL,
	`payment_group_id`	INTEGER NOT NULL,
	`product_id`	INTEGER,
	`product_counter`	INTEGER,
	`payment_amount`	INTEGER NOT NULL,
	`commission_amount`	INTEGER NOT NULL,
	PRIMARY KEY(`payment_id`)
);
DROP TABLE IF EXISTS `payment_objects`;
CREATE TABLE IF NOT EXISTS `payment_objects` (
	`payment_object_id`	INTEGER NOT NULL,
	`order_id`	INTEGER,
	`payment_object_name`	TEXT NOT NULL,
	`payment_object_period`	TEXT,
	`payment_object_term`	TEXT,
	`payment_object_pay_delay`	INT,
	PRIMARY KEY(`payment_object_id`)
);
DROP TABLE IF EXISTS `payment_groups`;
CREATE TABLE IF NOT EXISTS `payment_groups` (
	`payment_group_id`	INTEGER NOT NULL,
	`order_id`	INTEGER,
	`payment_group_name`	TEXT NOT NULL,
	`payment_group_url`	TEXT,
	PRIMARY KEY(`payment_group_id`)
);
DROP INDEX IF EXISTS `ix_payments_period_date`;
CREATE INDEX IF NOT EXISTS `ix_payments_period_date` ON `payments` (
	`payment_period_date`
);
DROP INDEX IF EXISTS `ix_payments_location`;
CREATE INDEX IF NOT EXISTS `ix_payments_location` ON `payments` (
	`payment_object_id`
);
COMMIT;
