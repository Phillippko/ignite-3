create table CUSTOMER
(
    C_CUSTOMER_SK          INTEGER       not null,
    C_CUSTOMER_ID          VARCHAR(16) not null,
    C_CURRENT_CDEMO_SK     INTEGER,
    C_CURRENT_HDEMO_SK     INTEGER,
    C_CURRENT_ADDR_SK      INTEGER,
    C_FIRST_SHIPTO_DATE_SK INTEGER,
    C_FIRST_SALES_DATE_SK  INTEGER,
    C_SALUTATION           VARCHAR(10),
    C_FIRST_NAME           VARCHAR(20),
    C_LAST_NAME            VARCHAR(30),
    C_PREFERRED_CUST_FLAG  VARCHAR,
    C_BIRTH_DAY            INTEGER,
    C_BIRTH_MONTH          INTEGER,
    C_BIRTH_YEAR           INTEGER,
    C_BIRTH_COUNTRY        VARCHAR(20),
    C_LOGIN                VARCHAR(13),
    C_EMAIL_ADDRESS        VARCHAR(50),
    C_LAST_REVIEW_DATE_SK  INTEGER,
    constraint CUSTOMER_PK
        primary key (C_CUSTOMER_SK)
);
