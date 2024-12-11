create table CUSTOMER_DEMOGRAPHICS
(
    CD_DEMO_SK            INTEGER not null,
    CD_GENDER             VARCHAR,
    CD_MARITAL_STATUS     VARCHAR,
    CD_EDUCATION_STATUS   VARCHAR(20),
    CD_PURCHASE_ESTIMATE  INTEGER,
    CD_CREDIT_RATING      VARCHAR(10),
    CD_DEP_COUNT          INTEGER,
    CD_DEP_EMPLOYED_COUNT INTEGER,
    CD_DEP_COLLEGE_COUNT  INTEGER,
    constraint CUSTOMER_DEMOGRAPHICS_PK
        primary key (CD_DEMO_SK)
);
