INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (1, 'Bohdan', 'Zinkevich', '111-11-11', 'bohdan.zsnkevich@ukr.n23et');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (2, 'Bohdan', 'Zinkevich', '222-22-22', 'bohdan.zsnkevich@ukr.net2');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (3, 'Bohdan', 'Zinkevich', '333-33-33', 'bohdan.zsnkevich@ukr.net3');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (4, 'stanis', 'stanis', '444-44-44', 'stanis.stanis@ukr.net');

INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (5, 'stanis1', 'stanis1', '555-55-55', 'stanis1.stanis1@ukr.net');


INSERT INTO person(person_id,  user_name, password, contact_id)
VALUES (1, 'Bohdan', '12121212', 1);
INSERT INTO person(person_id,  user_name, password, contact_id)
VALUES (2, 'Bohdan1', '12121212', 2);
INSERT INTO person(person_id,  user_name, password, contact_id)
VALUES (3, 'Bohdan2', '12121212', 3);

INSERT INTO role(role_id, role_name, is_employee_role) VALUES (1, 'ROLE_ADMIN', TRUE);
INSERT INTO role(role_id, role_name, is_employee_role) VALUES (2, 'ROLE_USER', FALSE);
INSERT INTO role(role_id, role_name, is_employee_role) VALUES (3, 'ROLE_UNCONFIRMED', FALSE);
INSERT INTO role(role_id, role_name, is_employee_role) VALUES (4, 'ROLE_MANAGER', TRUE);
INSERT INTO role(role_id, role_name, is_employee_role) VALUES (5, 'ROLE_COURIER', TRUE);
INSERT INTO role(role_id, role_name, is_employee_role) VALUES (6, 'ROLE_VIP_USER', FALSE);

INSERT INTO person_role(person_id, role_id) VALUES (1,1);
INSERT INTO person_role(person_id, role_id) VALUES (2,3);

INSERT INTO person(person_id,  user_name, password, contact_id)
VALUES (4, 'stanis', '$2a$10$d5LXfYNl7n7DxjL8Ci42lOBiPiSd50400IjWU0AGuUYO8TeF/14de', 4);
INSERT INTO person(person_id,  user_name, password, contact_id)
VALUES (5, 'stanis1', '$2a$10$x8wKe1tpGVJTE4zkHRbDj.OXXblGefWRjjdWw82e5s.m3OjHXGgM6', 5);

INSERT INTO person_role(person_id, role_id) VALUES (4,2);
INSERT INTO person_role(person_id, role_id) VALUES (5,1);

INSERT INTO person_role(person_id, role_id) VALUES (5, 4);

INSERT INTO advertisement_type(type_advertisement_id, advertisement_name) VALUES (1, 'Advertisement');
INSERT INTO advertisement_type(type_advertisement_id, advertisement_name) VALUES (2, 'Announcement');
INSERT INTO advertisement_type(type_advertisement_id, advertisement_name) VALUES (3, 'Other');
INSERT INTO address(address_id, address_name) VALUES (1,'sdsdsdsdsdds');
INSERT INTO office(office_id ,name,address_id) VALUES (1, 'werwerwer', 1);
INSERT INTO office(office_id ,name,address_id) VALUES (2, 'dddddddddd', 1);
