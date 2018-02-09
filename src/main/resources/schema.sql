DROP TABLE IF EXISTS "logistic_company"."employee_role";
DROP TABLE IF EXISTS "logistic_company"."user";
DROP TABLE IF EXISTS "logistic_company"."employee";
DROP TABLE IF EXISTS "logistic_company"."person";
DROP TABLE IF EXISTS "logistic_company"."role";
DROP TABLE IF EXISTS "logistic_company"."registration_data";
DROP SEQUENCE IF EXISTS "logistic_company"."main_seq_id";
DROP SCHEMA IF EXISTS "logistic_company";

CREATE SCHEMA "logistic_company";

CREATE SEQUENCE "logistic_company"."main_seq_id"
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 10;

SELECT setval('"logistic_company"."main_seq_id"', 1, TRUE);

SET SEARCH_PATH TO logistic_company;

CREATE TABLE "logistic_company"."person" (
  "person_id"         INT4 DEFAULT nextval('main_seq_id' :: REGCLASS) NOT NULL,
  "first_name"        VARCHAR(45) COLLATE "default"                   NOT NULL,
  "last_name"         VARCHAR(45) COLLATE "default"                   NOT NULL,
  "nick_name"         VARCHAR(45) COLLATE "default",
  "password"          VARCHAR(200) COLLATE "default"                  NOT NULL,
  "registration_date" DATE,
  "email"             VARCHAR(45) COLLATE "default"                   NOT NULL,
  "phone_number"      VARCHAR(45) COLLATE "default"
)
WITH (OIDS = FALSE
);


CREATE TABLE "logistic_company"."user" (
  "user_id"       INT4,
  "role_id"       INT4,
  "permission_id" INT4
)
WITH (OIDS = FALSE
);

CREATE TABLE "logistic_company"."employee" (
  "employee_id"  INT4,
  "salary"       NUMERIC,
  "manager_id"   INT4,
  "work_days_id" INT4
)
WITH (OIDS = FALSE
);

CREATE TABLE "logistic_company"."role" (
  "role_id"   INT4 DEFAULT nextval('main_seq_id' :: REGCLASS) NOT NULL,
  "role_name" VARCHAR(45) COLLATE "default"                   NOT NULL
);

CREATE TABLE "logistic_company"."employee_role"
(
  "employee_id" INT4,
  "role_id"     INT4
);

CREATE TABLE "logistic_company"."registration_data"
(
  "registration_data_id" INT4 DEFAULT nextval('main_seq_id') NOT NULL,
  "first_name"           VARCHAR(45)                         NOT NULL,
  "last_name"            VARCHAR(45)                         NOT NULL,
  "nick_name"            VARCHAR(45),
  "password"             VARCHAR(200)                        NOT NULL,
  "email"                VARCHAR(45)                         NOT NULL,
  "phone_number"         VARCHAR(45)
);


ALTER  TABLE  "logistic_company"."person" ADD  PRIMARY KEY ("person_id");
ALTER  TABLE  "logistic_comp any"."employee" ADD PRIMARY KEY ("employee_id");
ALTER  TABLE "logistic_company"."user" ADD PRIMARY KEY ("user_id");
ALTER TABLE "logistic_company"."role" ADD PRIMARY KEY ("role_id");

ALTER TABLE "logistic_company"."user" ADD FOREIGN KEY("user_id") REFERENCES  "logistic_company"."person"("person_id");
ALTER  TABLE "logistic_company"."employee" ADD  FOREIGN KEY ("employee_id") REFERENCES  "logistic_company"."person"("person_id");
ALTER TABLE "logistic_company"."user" ADD  FOREIGN KEY ("role_id ") REFERENCES "logistic_company"."role"("role_id");
ALTER  TABLE  "logistic_company"."employee_role" ADD  FOREIGN KEY  ("role_id") REFERENCES "logistic_company"."role"("role_id");
ALTER  TABLE  "logistic_company"."employee_role" ADD  FOREIGN KEY ("employee_id") REFERENCES  "logistic_company"."employee"("employee_id");

ALTER TABLE "logistic_company"."person"
  ADD PRIMARY KEY ("person_id");
ALTER TABLE "logistic_company"."employee"
  ADD PRIMARY KEY ("employee_id");
ALTER TABLE "logistic_company"."user"
  ADD PRIMARY KEY ("user_id");
ALTER TABLE "logistic_company"."role"
  ADD PRIMARY KEY ("role_id");
ALTER TABLE "logistic_company"."registration_data"
  ADD PRIMARY KEY ("registration_data_id");

ALTER TABLE "logistic_company"."user"
  ADD FOREIGN KEY ("user_id") REFERENCES "logistic_company"."person" ("person_id");
ALTER TABLE "logistic_company"."employee"
  ADD FOREIGN KEY ("employee_id") REFERENCES "logistic_company"."person" ("person_id");
ALTER TABLE "logistic_company"."user"
  ADD FOREIGN KEY ("role_id") REFERENCES "logistic_company"."role" ("role_id");
ALTER TABLE "logistic_company"."employee_role"
  ADD FOREIGN KEY ("role_id") REFERENCES "logistic_company"."role" ("role_id");
ALTER TABLE "logistic_company"."employee_role"
  ADD FOREIGN KEY ("employee_id") REFERENCES "logistic_company"."employee" ("employee_id");
