INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (1, 'Bohdan', 'Zinkevich', '111-11-11', 'bokhdan.zsnkevicasdasdasdh@ukr.n23w3we5eet');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (2, 'Bohdan', 'Zinkevich', '222-22-22', 'bohdan.zsnkevich@ukr.net2');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (3, 'Bohdan', 'Zinkevich', '333-33-33', 'bohdan.zsnkevich@ukr.net3');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (4, 'stanis', 'stanis', '444-44-44', 'its_an_omen@ukr.net');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (5, 'stanis1', 'stanis1', '555-55-55', 'stanis1.stanis1@ukr.net');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (6, 'agent', 'smith', '666-66-66', 'agent.smith@example.com');
INSERT INTO contact(contact_id, first_name, last_name, phone_number, email) VALUES (7, 'age3nt', 'sm3ith', '666-66-636', 'ag3ent.smith@example.com');

INSERT INTO person(person_id,  user_name, password, contact_id)
VALUES (1, 'Bohdan', '12121212', 1);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (2, 'Bohdan1', '12121212', 2);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (3, 'Bohdan2', '12121212', 3);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (4, 'stanis', '$2a$10$d5LXfYNl7n7DxjL8Ci42lOBiPiSd50400IjWU0AGuUYO8TeF/14de', 4);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (5, 'stanis1', '$2a$10$x8wKe1tpGVJTE4zkHRbDj.OXXblGefWRjjdWw82e5s.m3OjHXGgM6', 5);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (6, 'agent-smith', '$2a$10$wTuyKwJX2hUhhuy5Po/lVeKWppG0H3fH0x4UmX4ketu/PHzhghS12', 6);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (7, 'agent-lol', 'r245', 7);

INSERT INTO role (role_id, role_name, is_employee_role) VALUES (1, 'ROLE_ADMIN', TRUE);
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (2, 'ROLE_USER', FALSE, 'NORMAL');
INSERT INTO role (role_id, role_name, is_employee_role) VALUES (3, 'ROLE_UNCONFIRMED', FALSE);
INSERT INTO role (role_id, role_name, is_employee_role) VALUES (4, 'ROLE_MANAGER', TRUE);
INSERT INTO role (role_id, role_name, is_employee_role) VALUES (5, 'ROLE_CALL_CENTER', TRUE);
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (6, 'ROLE_VIP_USER', FALSE, 'VIP');
INSERT INTO role (role_id, role_name, is_employee_role) VALUES (7, 'ROLE_COURIER', TRUE );

INSERT INTO person_role (person_id, role_id) VALUES (1, 1);
INSERT INTO person_role (person_id, role_id) VALUES (2, 3);
INSERT INTO person_role (person_id, role_id) VALUES (4, 2);
INSERT INTO person_role (person_id, role_id) VALUES (5, 1);
INSERT INTO person_role (person_id, role_id) VALUES (5, 4);
INSERT INTO person_role (person_id, role_id) VALUES (6, 5);
INSERT INTO person_role (person_id, role_id) VALUES (7, 7);

INSERT INTO work_day (employee_id, week_day, start_time, end_time)
VALUES (6, trim(to_char(CURRENT_DATE, 'DAY')) :: logistic_company.week_day, '14:00', '22:00');

INSERT INTO advertisement_type (type_advertisement_id, advertisement_name) VALUES (1, 'Advertisement');
INSERT INTO advertisement_type (type_advertisement_id, advertisement_name) VALUES (2, 'Announcement');
INSERT INTO advertisement_type (type_advertisement_id, advertisement_name) VALUES (3, 'Other');
INSERT INTO address (address_id, address_name) VALUES (1, 'вул.Академіка Янгеля 5, Київ');
INSERT INTO address (address_id, address_name) VALUES (2, 'Будинок художника, Київ');
INSERT INTO office (office_id, name, address_id) VALUES (1, 'werwerwer', 1);
INSERT INTO office (office_id, name, address_id) VALUES (2, 'dddddddddd', 1);


INSERT INTO order_type (order_type_id, name, max_weight, max_width, max_height, max_length)
VALUES (1, 'Documents', 1, 35, 25, 2);
INSERT INTO order_type (order_type_id, name, max_weight, max_width, max_height, max_length)
VALUES (2, 'Package', 30, 150, 150, 150);
INSERT INTO order_type (order_type_id, name, max_weight, max_width, max_height, max_length)
VALUES (3, 'Cargo', 1000, 170, 170, 300);

INSERT INTO order_status (order_status_id, status_name) VALUES (1, 'DRAFT');
INSERT INTO order_status (order_status_id, status_name) VALUES (2, 'PROCESSING');
INSERT INTO order_status (order_status_id, status_name) VALUES (3, 'POSTPONED');
INSERT INTO order_status (order_status_id, status_name) VALUES (4, 'ASSOCIATED');
INSERT INTO order_status (order_status_id, status_name) VALUES (5, 'CONFIRMED');
INSERT INTO order_status (order_status_id, status_name) VALUES (6, 'DELIVERED');

INSERT INTO advertisement_type(type_advertisement_id, advertisement_name) VALUES (1, 'Advertisement');
INSERT INTO advertisement_type(type_advertisement_id, advertisement_name) VALUES (2, 'Announcement');
INSERT INTO advertisement_type(type_advertisement_id, advertisement_name) VALUES (3, 'Other');

INSERT INTO reset_password(person_id, reset_token) VALUES (4, '47a95c8c-a5d1-4984-9ce5-904ecd81e637');

INSERT INTO logistic_company."order"(order_id, estimated_delivery_time,  courier_id, receiver_contact_id, receiver_address_id, sender_contact_id, sender_address_id, office_id, order_status_id, order_type_id, weight, width, height, length)
VALUES (1, '04:05:06', 7, 1, 1,2,2, 1, 6,1, 12,12,12,12);
