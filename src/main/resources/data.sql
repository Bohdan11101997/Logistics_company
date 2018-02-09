INSERT INTO "logistic_company"."person" (first_name, last_name, nick_name, password, registration_date, email, phone_number)
VALUES ('Bogdan', 'Zinkevich', 'Zibo', 121212, '1997-10-11', 'bogdan@gamil.com', '+380687072934');
INSERT INTO "logistic_company"."role" (role_name) VALUES ('ADMIN');
INSERT INTO "logistic_company"."user" ("user_id", "role_id") VALUES (2, 3);