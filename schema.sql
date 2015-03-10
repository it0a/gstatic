DROP TABLE IF EXISTS contract;
CREATE TABLE contract (
    `id` BIGINT(20) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `date_start` DATETIME NOT NULL,
);

DROP TABLE IF EXISTS invoice;
CREATE TABLE invoice (
    `id` BIGINT(20) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `contract_id` BIGINT(20) NOT NULL,
    `date_invoiced` DATETIME NOT NULL,
    `number` VARCHAR(255) NOT NULL,
    KEY `fk_invoice_contract_id` (`contract_id`),
    CONSTRAINT `fk_invoice_contract_id` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`)
);

DROP TABLE IF EXISTS line_item;
CREATE TABLE line_item (
    `id` BIGINT(20) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `invoice_id` BIGINT(20) NOT NULL,
    `amount` DECIMAL(19,2) NOT NULL,
    KEY `fk_line_item_invoice_id` (`invoice_id`),
    CONSTRAINT `fk_line_item_invoice_id` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`)
);

