INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (1, 'Bohdan', 'Zinkevich', '984565656', 'bohdan.zsnkevich@ukr.net');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (2, 'Stanislav', 'Popov', '984565657', 'stanis1.stanis1@ukr.net');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (3, 'Ned', 'Stark', '984565658', 'Ned.Stark@ukr.net');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (4, 'Jaime', 'Lannister', '984565659', 'Jaime.Lannister@ukr.net');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (5, 'Tyrion', 'Lannister', '984565660', 'Tyrion.Lannister@ukr.net');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (6, 'Tywin', 'Lannister', '984565661', 'Tywin.Lannister@ukr.net');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (7, 'Khal', 'Drogo', '984565662', 'Khal.Drogo@example.com');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (8, 'Daenerys', 'Targaryen', '984565663', 'Daenerys.Targaryen@example.com');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (9, 'courier', 'rapidman', '984565664', 'courier.rapidman@example.com');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (10, 'Sansa', 'Stark', '984565665', 'Sansa.Stark@example.com');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (11, 'callcenter', 'callcenter', '984565666', 'callcenter.callcenter@example.com');
INSERT INTO contact (contact_id, first_name, last_name, phone_number, email)
VALUES (12, 'manager', 'manager', '888461636', 'manager.manager@example.com');


INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (1, 'Bohdan', '12121212', 1);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (2, 'Bohdan1', '$2a$10$FaTZAFUbG7UmfDCkAjepf.CY5bHap7wjSsKU02AT6Q7MxB6pgSy1u', 2);
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
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (8, 'courier', '$2a$10$xAqw2EDobxgMKPp3.yBt5OJAuWa/eQxajk2W1ALNuW5MiFxWs4YZC', 8);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (11, 'callcenter', '$2a$10$GgaiWAHTSsCZYdUJt.9Wo.Pr9z/3.r5mIbmzGV2gESwXm9NRKmpd6', 11);
INSERT INTO person (person_id, user_name, password, contact_id)
VALUES (12, 'manager', '$2a$10$PKTqjODixGFeY4SG/6uUMOdmPS2neZBJiqOkoe2xgzYz7wPHlO/c.', 12);

INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (1, 'ROLE_ADMIN', TRUE, 'VIP');
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (2, 'ROLE_USER', FALSE, 'NORMAL');
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (3, 'ROLE_UNCONFIRMED', FALSE, 'NORMAL');
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (4, 'ROLE_MANAGER', TRUE, 'VIP');
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (5, 'ROLE_CALL_CENTER', TRUE, 'VIP');
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (6, 'ROLE_VIP_USER', FALSE, 'VIP');
INSERT INTO role (role_id, role_name, is_employee_role, priority) VALUES (7, 'ROLE_COURIER', TRUE, 'VIP');

INSERT INTO person_role (person_id, role_id) VALUES (1, 1);
INSERT INTO person_role (person_id, role_id) VALUES (2, 3);
INSERT INTO person_role (person_id, role_id) VALUES (4, 1);
INSERT INTO person_role (person_id, role_id) VALUES (2, 2);
INSERT INTO person_role (person_id, role_id) VALUES (5, 1);
INSERT INTO person_role (person_id, role_id) VALUES (5, 2);
INSERT INTO person_role (person_id, role_id) VALUES (5, 5);
INSERT INTO person_role (person_id, role_id) VALUES (5, 7);
INSERT INTO person_role (person_id, role_id) VALUES (6, 5);
INSERT INTO person_role (person_id, role_id) VALUES (7, 7);
INSERT INTO person_role (person_id, role_id) VALUES (8, 7);
INSERT INTO person_role (person_id, role_id) VALUES (11, 5);
INSERT INTO person_role (person_id, role_id) VALUES (12, 4);

INSERT INTO work_day (employee_id, week_day, start_time, end_time)
VALUES (1, trim(to_char(CURRENT_DATE, 'DAY')) :: WEEK_DAY, '12:00', '22:00');
INSERT INTO work_day (employee_id, week_day, start_time, end_time)
VALUES (2, trim(to_char(CURRENT_DATE, 'DAY')) :: WEEK_DAY, '07:00', '15:00');
INSERT INTO work_day (employee_id, week_day, start_time, end_time)
VALUES (3, trim(to_char(CURRENT_DATE, 'DAY')) :: WEEK_DAY, '15:00', '21:00');
INSERT INTO work_day (employee_id, week_day, start_time, end_time)
VALUES (4, trim(to_char(CURRENT_DATE, 'DAY')) :: WEEK_DAY, '16:00', '02:00');
INSERT INTO work_day (employee_id, week_day, start_time, end_time)
VALUES (5, trim(to_char(CURRENT_DATE, 'DAY')) :: WEEK_DAY, '06:00', '23:00');
INSERT INTO work_day (employee_id, week_day, start_time, end_time)
VALUES (6, trim(to_char(CURRENT_DATE, 'DAY')) :: WEEK_DAY, '00:00', '23:59');

INSERT INTO address (address_id, address_name) VALUES (1, 'Zodchykh St, 34А, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (2, 'Lesya Kurbasa Ave, 18, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (3, 'Semashka St, 9, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (4, 'Mykoly Krasnova St, 29, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (5, 'Borysa Zhytkova St, 7Б, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (6, 'Bakynska St, 35, 5, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (7, 'Vvedenska St, 25-21, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (8, 'Druzhby Narodiv Blvd, 23, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (9, 'Metrolohichna St, 4, Kyiv');
INSERT INTO address (address_id, address_name) VALUES (10, 'Romena Rollana Blvd, 5-3, Kyiv');

INSERT INTO office (office_id, name, address_id) VALUES (1, 'Storage №1', 1);
INSERT INTO office (office_id, name, address_id) VALUES (2, 'Storage №2', 2);
INSERT INTO office (office_id, name, address_id) VALUES (3, 'Storage №2', 3);

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
INSERT INTO order_status (order_status_id, status_name) VALUES (6, 'DELIVERING');
INSERT INTO order_status (order_status_id, status_name) VALUES (7, 'DELIVERED');

INSERT INTO advertisement_type (type_advertisement_id, advertisement_name) VALUES (1, 'Advertisement');
INSERT INTO advertisement_type (type_advertisement_id, advertisement_name) VALUES (2, 'Announcement');
INSERT INTO advertisement_type (type_advertisement_id, advertisement_name) VALUES (3, 'Other');


INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (1, 'Nulla Facilisi Foundation',
        'dolor. Nulla semper tellus id nunc interdum feugiat. Sed nec metus facilisis lorem tristique aliquet. Phasellus fermentum convallis ligula. Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget, venenatis a, magna. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam laoreet, libero et tristique pellentesque, tellus sem mollis dui,',
        '2019-02-23', '2020-05-02', 3),
  (2, 'Mi Fringilla Institute', 'lorem, sit amet ultricies sem magna nec quam. Curabitur vel lectus. Cum', '2019-03-29',
   '2027-01-13', 1), (3, 'Libero Et Inc.',
                      'vehicula aliquet libero. Integer in magna. Phasellus dolor elit, pellentesque a, facilisis non, bibendum sed, est. Nunc laoreet lectus quis massa. Mauris vestibulum, neque sed dictum eleifend, nunc risus varius orci, in consequat enim diam vel arcu. Curabitur ut odio vel est tempor bibendum. Donec felis orci, adipiscing non, luctus sit amet, faucibus ut, nulla.',
                      '2018-04-13', '2034-07-30', 1), (4, 'Ultrices Vivamus Rhoncus LLC',
                                                       'turpis egestas. Fusce aliquet magna a neque. Nullam ut nisi a odio semper cursus. Integer mollis. Integer tincidunt aliquam arcu. Aliquam ultrices iaculis odio. Nam interdum enim non nisi. Aenean eget metus. In nec orci. Donec nibh. Quisque nonummy ipsum non arcu. Vivamus sit amet risus. Donec egestas. Aliquam nec enim. Nunc ut erat. Sed nunc est, mollis non, cursus non, egestas a, dui. Cras pellentesque. Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec',
                                                       '2018-12-24', '2026-09-18', 2), (5, 'Interdum Libero Dui PC',
                                                                                        'sapien, cursus in, hendrerit consectetuer, cursus et, magna. Praesent interdum ligula eu enim. Etiam imperdiet dictum magna. Ut tincidunt orci quis lectus. Nullam suscipit, est ac facilisis facilisis, magna tellus faucibus leo, in lobortis tellus justo sit amet nulla. Donec non justo. Proin non massa non ante bibendum ullamcorper. Duis cursus, diam at pretium aliquet, metus urna convallis',
                                                                                        '2019-04-16', '2028-10-20', 3),
  (6, 'Hymenaeos Ltd', 'lectus pede, ultrices a, auctor non, feugiat nec, diam. Duis', '2019-01-13', '2023-01-16', 2),
  (7, 'Scelerisque Neque Institute',
   'arcu iaculis enim, sit amet ornare lectus justo eu arcu. Morbi sit amet massa. Quisque porttitor eros nec tellus. Nunc lectus pede, ultrices a, auctor non, feugiat nec, diam. Duis mi enim, condimentum eget, volutpat ornare, facilisis eget, ipsum. Donec sollicitudin adipiscing ligula. Aenean gravida nunc sed pede. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel arcu eu odio tristique pharetra. Quisque ac libero nec ligula consectetuer rhoncus. Nullam velit dui, semper et, lacinia vitae, sodales at, velit. Pellentesque ultricies dignissim lacus. Aliquam rutrum lorem ac risus. Morbi metus. Vivamus',
   '2019-07-19', '2029-12-29', 1), (8, 'Suspendisse Sagittis Nullam Corporation',
                                    'suscipit, est ac facilisis facilisis, magna tellus faucibus leo, in lobortis tellus justo sit amet nulla. Donec non justo. Proin non massa non ante bibendum ullamcorper. Duis cursus, diam at pretium aliquet, metus urna convallis erat, eget tincidunt dui augue eu tellus. Phasellus elit pede, malesuada vel, venenatis vel, faucibus id, libero. Donec consectetuer mauris',
                                    '2019-07-22', '2035-02-02', 3), (9, 'Aenean Euismod Mauris Limited',
                                                                     'eros turpis non enim. Mauris quis turpis vitae purus gravida sagittis. Duis gravida. Praesent eu nulla at sem molestie sodales. Mauris blandit enim consequat purus. Maecenas libero est, congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac ipsum. Phasellus vitae mauris sit amet lorem semper auctor. Mauris vel turpis. Aliquam adipiscing lobortis risus. In',
                                                                     '2019-03-03', '2021-04-20', 1),
  (10, 'Mauris Id Sapien LLP',
   'ornare lectus justo eu arcu. Morbi sit amet massa. Quisque porttitor eros nec tellus. Nunc lectus pede, ultrices a, auctor non, feugiat nec, diam. Duis mi enim, condimentum eget, volutpat ornare, facilisis eget, ipsum. Donec sollicitudin adipiscing ligula. Aenean gravida nunc sed pede. Cum sociis natoque penatibus et magnis dis parturient',
   '2018-09-13', '2037-01-12', 3);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (11, 'Nulla Magna Corporation',
        'vulputate, lacus. Cras interdum. Nunc sollicitudin commodo ipsum. Suspendisse non leo. Vivamus nibh dolor, nonummy ac, feugiat non, lobortis quis, pede. Suspendisse dui. Fusce diam nunc, ullamcorper eu, euismod ac, fermentum vel, mauris.',
        '2019-08-03', '2024-08-03', 3), (12, 'Dolor Dapibus Gravida Inc.',
                                         'mus. Proin vel arcu eu odio tristique pharetra. Quisque ac libero nec ligula consectetuer rhoncus. Nullam velit dui, semper et, lacinia vitae, sodales at, velit. Pellentesque ultricies dignissim lacus. Aliquam rutrum lorem ac risus. Morbi metus. Vivamus euismod urna. Nullam lobortis quam a felis ullamcorper viverra. Maecenas iaculis aliquet diam. Sed diam lorem, auctor quis, tristique ac, eleifend vitae, erat. Vivamus nisi. Mauris nulla. Integer urna. Vivamus molestie dapibus ligula. Aliquam erat volutpat. Nulla dignissim. Maecenas ornare egestas ligula. Nullam feugiat placerat velit. Quisque varius. Nam porttitor scelerisque',
                                         '2019-03-19', '2039-02-07', 2), (13, 'Pretium Industries',
                                                                          'est, congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac',
                                                                          '2019-05-09', '2033-01-09', 1),
  (14, 'Mollis Vitae Corp.',
   'at arcu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec tincidunt. Donec vitae erat vel pede blandit congue.',
   '2018-10-21', '2036-04-23', 1), (15, 'Lectus Ante Dictum PC',
                                    'netus et malesuada fames ac turpis egestas. Aliquam fringilla cursus purus. Nullam scelerisque neque sed sem egestas blandit. Nam nulla magna, malesuada vel, convallis in, cursus et, eros. Proin ultrices. Duis volutpat nunc sit amet metus. Aliquam erat volutpat. Nulla facilisis. Suspendisse commodo tincidunt nibh. Phasellus nulla. Integer vulputate, risus a ultricies adipiscing, enim mi tempor lorem, eget mollis lectus pede et risus. Quisque libero',
                                    '2018-07-29', '2023-05-04', 1), (16, 'Mus LLP',
                                                                     'Donec non justo. Proin non massa non ante bibendum ullamcorper. Duis cursus, diam at pretium aliquet, metus urna convallis erat, eget tincidunt dui augue eu tellus. Phasellus elit pede, malesuada vel, venenatis vel, faucibus id, libero. Donec consectetuer mauris id sapien. Cras dolor dolor,',
                                                                     '2018-09-27', '2043-03-19', 3),
  (17, 'Suspendisse Tristique Corporation',
   'amet, faucibus ut, nulla. Cras eu tellus eu augue porttitor interdum. Sed auctor odio a purus. Duis elementum, dui quis accumsan convallis, ante lectus convallis est, vitae sodales nisi magna sed dui. Fusce aliquam, enim nec tempus scelerisque, lorem ipsum sodales purus,',
   '2018-07-23', '2037-08-10', 2), (18, 'Lacus Aliquam Incorporated',
                                    'condimentum. Donec at arcu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec tincidunt. Donec vitae erat vel pede blandit congue. In scelerisque scelerisque dui. Suspendisse ac metus vitae velit egestas lacinia. Sed congue, elit sed consequat auctor, nunc nulla vulputate',
                                    '2019-05-22', '2036-01-06', 1), (19, 'Ullamcorper Velit In Ltd',
                                                                     'sed, hendrerit a, arcu. Sed et libero. Proin mi. Aliquam gravida mauris ut mi. Duis risus odio, auctor vitae, aliquet nec, imperdiet nec, leo. Morbi neque tellus, imperdiet non, vestibulum nec, euismod in, dolor. Fusce feugiat. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam auctor, velit eget',
                                                                     '2018-08-23', '2043-06-10', 2),
  (20, 'Nisl Quisque Corp.',
   'justo nec ante. Maecenas mi felis, adipiscing fringilla, porttitor vulputate, posuere vulputate, lacus. Cras interdum. Nunc sollicitudin commodo ipsum. Suspendisse non leo. Vivamus nibh dolor, nonummy',
   '2019-05-08', '2042-09-20', 3);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (21, 'Semper Erat In Institute',
        'Donec est mauris, rhoncus id, mollis nec, cursus a, enim. Suspendisse aliquet, sem ut cursus luctus, ipsum leo elementum sem, vitae aliquam eros turpis non enim. Mauris quis turpis vitae purus gravida sagittis. Duis gravida. Praesent eu nulla at sem molestie sodales. Mauris blandit enim consequat purus. Maecenas libero est, congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac ipsum. Phasellus vitae mauris sit amet lorem semper auctor. Mauris vel',
        '2018-03-15', '2045-10-18', 3), (22, 'Metus Urna Convallis PC',
                                         'at, libero. Morbi accumsan laoreet ipsum. Curabitur consequat, lectus sit amet luctus vulputate, nisi sem semper erat, in consectetuer ipsum nunc id enim. Curabitur massa. Vestibulum accumsan',
                                         '2018-12-27', '2045-10-20', 1), (23, 'Ut Quam Vel LLC',
                                                                          'sed libero. Proin sed turpis nec mauris blandit mattis. Cras eget nisi dictum augue malesuada malesuada. Integer id magna et ipsum cursus vestibulum. Mauris magna. Duis dignissim tempor arcu. Vestibulum ut eros non enim commodo hendrerit. Donec porttitor tellus non magna.',
                                                                          '2019-05-26', '2029-04-20', 2),
  (24, 'Sagittis Duis Inc.',
   'nibh lacinia orci, consectetuer euismod est arcu ac orci. Ut semper pretium neque. Morbi quis urna. Nunc quis arcu vel quam dignissim pharetra. Nam ac nulla. In tincidunt congue turpis. In condimentum. Donec at arcu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec tincidunt. Donec vitae erat vel pede blandit congue. In scelerisque scelerisque dui. Suspendisse ac metus vitae velit egestas lacinia. Sed congue, elit sed consequat auctor, nunc nulla vulputate dui, nec tempus mauris erat eget ipsum. Suspendisse sagittis. Nullam vitae diam. Proin dolor. Nulla',
   '2019-01-31', '2021-09-20', 2), (25, 'Elit Sed Consequat Limited',
                                    'a, magna. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam laoreet, libero et tristique pellentesque, tellus sem mollis dui, in sodales elit erat vitae risus. Duis a mi fringilla mi lacinia mattis. Integer eu lacus. Quisque imperdiet, erat nonummy ultricies ornare, elit elit fermentum risus, at fringilla purus mauris a nunc. In at pede. Cras vulputate velit eu sem. Pellentesque ut ipsum ac mi eleifend egestas. Sed pharetra, felis eget varius',
                                    '2019-01-16', '2032-03-18', 1), (26, 'Dapibus Foundation',
                                                                     'nonummy ipsum non arcu. Vivamus sit amet risus. Donec egestas. Aliquam nec enim. Nunc ut erat. Sed nunc est, mollis non, cursus non, egestas a, dui. Cras pellentesque. Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec feugiat metus sit amet ante. Vivamus non lorem vitae odio sagittis semper. Nam tempor',
                                                                     '2018-09-04', '2044-09-11', 2),
  (27, 'Ultricies Company',
   'risus. Donec egestas. Duis ac arcu. Nunc mauris. Morbi non sapien molestie orci tincidunt adipiscing. Mauris molestie pharetra nibh. Aliquam ornare, libero at auctor ullamcorper, nisl arcu iaculis enim, sit amet ornare lectus justo eu arcu. Morbi sit amet massa. Quisque porttitor eros nec tellus. Nunc lectus pede, ultrices a, auctor non, feugiat nec, diam. Duis mi enim, condimentum eget, volutpat ornare, facilisis eget, ipsum. Donec sollicitudin adipiscing ligula. Aenean gravida nunc sed pede. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel arcu eu odio',
   '2018-04-10', '2024-03-20', 3), (28, 'A Foundation',
                                    'metus sit amet ante. Vivamus non lorem vitae odio sagittis semper. Nam tempor diam dictum sapien. Aenean massa. Integer vitae nibh. Donec est mauris, rhoncus id, mollis nec, cursus a, enim. Suspendisse aliquet, sem ut cursus luctus, ipsum leo elementum sem, vitae aliquam eros turpis non enim. Mauris quis turpis vitae purus gravida sagittis. Duis gravida. Praesent eu nulla at sem molestie sodales. Mauris',
                                    '2018-07-03', '2041-07-19', 1), (29, 'Vel Turpis Aliquam Incorporated',
                                                                     'pulvinar arcu et pede. Nunc sed orci lobortis augue scelerisque mollis. Phasellus libero mauris, aliquam eu, accumsan sed, facilisis vitae, orci.',
                                                                     '2019-02-21', '2045-07-03', 2),
  (30, 'Dapibus Rutrum Incorporated',
   'pharetra nibh. Aliquam ornare, libero at auctor ullamcorper, nisl arcu iaculis enim,', '2018-06-27', '2041-09-11',
   1);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (31, 'Nisi Limited',
        'feugiat metus sit amet ante. Vivamus non lorem vitae odio sagittis semper. Nam tempor diam dictum sapien. Aenean massa. Integer vitae nibh. Donec est mauris, rhoncus id, mollis nec, cursus a, enim. Suspendisse',
        '2018-05-31', '2034-12-30', 2), (32, 'Sem Ut Ltd',
                                         'Quisque porttitor eros nec tellus. Nunc lectus pede, ultrices a, auctor non, feugiat nec, diam. Duis mi enim,',
                                         '2019-05-26', '2043-11-13', 3), (33, 'Dui Industries',
                                                                          'ligula. Aliquam erat volutpat. Nulla dignissim. Maecenas ornare egestas ligula. Nullam feugiat placerat velit. Quisque varius. Nam porttitor scelerisque neque. Nullam nisl. Maecenas malesuada fringilla est.',
                                                                          '2018-08-23', '2043-03-13', 2),
  (34, 'Velit Eget Laoreet Associates',
   'molestie. Sed id risus quis diam luctus lobortis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Mauris',
   '2018-12-05', '2036-12-23', 1), (35, 'Lorem Ut Ltd',
                                    'nec tempus mauris erat eget ipsum. Suspendisse sagittis. Nullam vitae diam. Proin dolor. Nulla semper tellus id nunc interdum feugiat. Sed nec metus facilisis lorem tristique aliquet. Phasellus fermentum convallis ligula. Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget, venenatis a, magna. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam laoreet, libero et tristique pellentesque, tellus sem mollis dui, in sodales elit erat vitae risus. Duis a mi fringilla mi lacinia mattis. Integer eu lacus. Quisque imperdiet, erat nonummy ultricies',
                                    '2019-05-01', '2026-12-12', 1), (36, 'Ac Nulla In Institute',
                                                                     'erat neque non quam. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam fringilla cursus purus. Nullam scelerisque neque sed sem egestas blandit. Nam nulla magna, malesuada vel, convallis in, cursus et, eros. Proin ultrices. Duis volutpat nunc sit amet metus. Aliquam erat volutpat. Nulla facilisis. Suspendisse commodo tincidunt nibh. Phasellus nulla. Integer vulputate, risus a ultricies adipiscing,',
                                                                     '2019-07-08', '2021-01-18', 1),
  (37, 'Adipiscing Lobortis Risus Company',
   'venenatis lacus. Etiam bibendum fermentum metus. Aenean sed pede nec ante blandit viverra. Donec tempus, lorem fringilla ornare',
   '2018-09-02', '2041-03-15', 1), (38, 'Donec Consulting',
                                    'mauris. Morbi non sapien molestie orci tincidunt adipiscing. Mauris molestie pharetra nibh. Aliquam ornare, libero at auctor ullamcorper, nisl arcu iaculis enim, sit amet ornare lectus justo eu arcu. Morbi sit amet massa. Quisque porttitor eros nec tellus. Nunc lectus pede, ultrices a, auctor non, feugiat nec, diam. Duis mi enim, condimentum eget, volutpat ornare, facilisis eget, ipsum. Donec sollicitudin adipiscing ligula. Aenean gravida nunc sed pede. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel arcu eu odio tristique',
                                    '2019-01-29', '2039-08-17', 3), (39, 'Eros Nec Tellus LLP',
                                                                     'pretium neque. Morbi quis urna. Nunc quis arcu vel quam dignissim pharetra. Nam ac nulla. In tincidunt congue turpis. In condimentum. Donec at arcu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec tincidunt. Donec vitae erat vel pede blandit',
                                                                     '2018-03-19', '2032-01-01', 2),
  (40, 'Neque Pellentesque Corp.',
   'vel, faucibus id, libero. Donec consectetuer mauris id sapien. Cras dolor dolor, tempus non, lacinia at, iaculis quis, pede. Praesent eu dui. Cum sociis natoque penatibus',
   '2018-09-11', '2033-02-04', 3);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (41, 'Hendrerit A Institute',
        'malesuada vel, venenatis vel, faucibus id, libero. Donec consectetuer mauris id sapien. Cras dolor dolor, tempus non, lacinia at, iaculis quis, pede. Praesent eu dui. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aenean eget magna. Suspendisse tristique neque venenatis lacus. Etiam bibendum fermentum metus. Aenean sed pede nec ante blandit viverra. Donec tempus, lorem fringilla ornare',
        '2019-01-24', '2044-11-04', 2), (42, 'Luctus Et Ultrices Associates',
                                         'purus, in molestie tortor nibh sit amet orci. Ut sagittis lobortis mauris. Suspendisse aliquet molestie tellus. Aenean egestas hendrerit neque. In ornare sagittis felis. Donec tempor, est ac mattis semper, dui lectus rutrum urna, nec luctus felis purus ac tellus. Suspendisse sed dolor. Fusce mi lorem, vehicula et, rutrum eu, ultrices sit amet, risus. Donec nibh enim, gravida sit amet, dapibus id, blandit at, nisi. Cum sociis natoque penatibus et',
                                         '2019-01-26', '2037-09-19', 1), (43, 'Nec Orci Donec Company',
                                                                          'malesuada ut, sem. Nulla interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac ipsum. Phasellus vitae mauris sit amet lorem semper auctor. Mauris vel turpis. Aliquam adipiscing lobortis risus. In mi pede, nonummy ut, molestie in, tempus eu, ligula. Aenean euismod mauris eu elit. Nulla facilisi. Sed neque. Sed eget lacus. Mauris non dui nec urna suscipit nonummy. Fusce fermentum fermentum arcu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus ornare. Fusce mollis. Duis sit amet diam eu dolor',
                                                                          '2019-08-08', '2022-10-04', 1),
  (44, 'Dolor Quisque LLP',
   'feugiat nec, diam. Duis mi enim, condimentum eget, volutpat ornare, facilisis eget, ipsum. Donec sollicitudin adipiscing ligula. Aenean gravida nunc sed pede. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel arcu eu odio tristique pharetra. Quisque ac libero nec ligula consectetuer rhoncus. Nullam velit dui, semper et, lacinia vitae, sodales at, velit. Pellentesque ultricies dignissim lacus. Aliquam rutrum lorem ac risus. Morbi metus. Vivamus euismod urna. Nullam lobortis quam',
   '2018-08-19', '2022-09-19', 3), (45, 'Sit Amet Dapibus Institute',
                                    'vitae sodales nisi magna sed dui. Fusce aliquam, enim nec tempus scelerisque, lorem ipsum sodales purus, in molestie tortor nibh sit amet orci. Ut sagittis lobortis mauris. Suspendisse aliquet molestie tellus. Aenean egestas hendrerit neque. In ornare sagittis felis. Donec tempor, est ac mattis semper, dui lectus rutrum urna, nec luctus felis purus ac tellus. Suspendisse sed dolor. Fusce mi lorem, vehicula et, rutrum eu, ultrices sit amet, risus. Donec nibh enim, gravida sit amet, dapibus id, blandit at, nisi. Cum sociis natoque penatibus',
                                    '2018-04-22', '2027-12-02', 1), (46, 'Lacinia At Iaculis Ltd',
                                                                     'sollicitudin commodo ipsum. Suspendisse non leo. Vivamus nibh dolor, nonummy ac, feugiat non, lobortis quis, pede. Suspendisse dui. Fusce diam nunc, ullamcorper eu, euismod ac, fermentum vel, mauris. Integer sem elit, pharetra ut, pharetra sed, hendrerit a, arcu. Sed et libero. Proin mi. Aliquam gravida mauris ut mi. Duis risus odio, auctor vitae, aliquet nec, imperdiet nec, leo. Morbi neque tellus, imperdiet non, vestibulum nec, euismod in, dolor.',
                                                                     '2018-07-30', '2046-02-28', 2),
  (47, 'Congue A Aliquet Ltd', 'ultrices, mauris ipsum porta elit, a feugiat tellus lorem eu metus.', '2018-10-05',
   '2031-10-15', 1), (48, 'Proin Mi PC',
                      'sit amet risus. Donec egestas. Aliquam nec enim. Nunc ut erat. Sed nunc est, mollis non, cursus non, egestas a, dui. Cras pellentesque. Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec feugiat metus sit amet ante. Vivamus non lorem vitae odio sagittis semper. Nam tempor diam dictum sapien. Aenean massa. Integer vitae nibh. Donec est mauris, rhoncus id, mollis nec, cursus a, enim. Suspendisse',
                      '2019-05-11', '2035-11-23', 3), (49, 'Lorem Eu Foundation',
                                                       'ligula. Nullam feugiat placerat velit. Quisque varius. Nam porttitor scelerisque neque. Nullam nisl. Maecenas malesuada fringilla est. Mauris eu turpis.',
                                                       '2018-04-28', '2031-02-28', 3), (50, 'Donec Foundation',
                                                                                        'Mauris ut quam vel sapien imperdiet ornare. In faucibus. Morbi vehicula. Pellentesque tincidunt tempus risus. Donec egestas. Duis ac arcu. Nunc mauris. Morbi non sapien molestie orci tincidunt adipiscing. Mauris molestie pharetra nibh. Aliquam ornare, libero at auctor ullamcorper, nisl arcu iaculis enim,',
                                                                                        '2018-07-30', '2044-04-28', 3);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (51, 'Eget Ltd',
        'blandit congue. In scelerisque scelerisque dui. Suspendisse ac metus vitae velit egestas lacinia. Sed congue, elit sed consequat auctor, nunc nulla vulputate dui, nec tempus mauris erat eget ipsum. Suspendisse sagittis. Nullam vitae diam. Proin dolor. Nulla semper tellus id nunc interdum feugiat. Sed nec metus facilisis lorem tristique aliquet. Phasellus fermentum convallis ligula. Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget, venenatis a, magna. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam laoreet, libero et tristique pellentesque,',
        '2019-01-29', '2044-10-22', 2), (52, 'In Scelerisque Scelerisque Inc.',
                                         'augue porttitor interdum. Sed auctor odio a purus. Duis elementum, dui quis accumsan convallis, ante lectus convallis est, vitae sodales nisi magna sed dui. Fusce aliquam, enim nec tempus scelerisque,',
                                         '2019-04-02', '2026-12-05', 1), (53, 'Dignissim Associates',
                                                                          'non enim commodo hendrerit. Donec porttitor tellus non magna. Nam ligula elit, pretium et, rutrum non, hendrerit id, ante. Nunc mauris sapien, cursus in, hendrerit consectetuer, cursus et, magna. Praesent interdum ligula eu enim. Etiam imperdiet dictum magna. Ut tincidunt orci quis lectus. Nullam suscipit, est ac facilisis facilisis, magna tellus faucibus leo, in lobortis tellus justo sit amet nulla. Donec non justo. Proin non massa non ante bibendum ullamcorper. Duis cursus,',
                                                                          '2018-04-29', '2041-04-20', 3),
  (54, 'Ut Pellentesque LLC',
   'ac risus. Morbi metus. Vivamus euismod urna. Nullam lobortis quam a felis ullamcorper viverra. Maecenas iaculis aliquet diam. Sed diam lorem, auctor quis, tristique ac, eleifend',
   '2018-07-07', '2040-06-27', 2), (55, 'Quam Quis Corp.',
                                    'interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac ipsum. Phasellus vitae mauris sit amet lorem semper auctor. Mauris vel turpis. Aliquam adipiscing lobortis risus. In mi pede,',
                                    '2018-10-17', '2029-03-05', 3), (56, 'Commodo Hendrerit Corp.',
                                                                     'Suspendisse aliquet, sem ut cursus luctus, ipsum leo elementum sem, vitae aliquam eros turpis non enim. Mauris quis turpis vitae purus gravida sagittis. Duis gravida. Praesent eu nulla at sem molestie sodales. Mauris blandit enim consequat purus. Maecenas libero est, congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac ipsum. Phasellus vitae mauris sit amet lorem semper auctor. Mauris vel turpis. Aliquam',
                                                                     '2018-05-17', '2022-08-07', 1),
  (57, 'Ridiculus Foundation',
   'id, blandit at, nisi. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel nisl. Quisque fringilla euismod enim. Etiam gravida molestie arcu. Sed eu nibh vulputate mauris sagittis placerat. Cras dictum ultricies ligula. Nullam enim. Sed nulla ante, iaculis nec, eleifend non, dapibus rutrum, justo.',
   '2018-11-28', '2042-12-30', 2), (58, 'Aenean Corp.',
                                    'suscipit nonummy. Fusce fermentum fermentum arcu. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus ornare. Fusce mollis. Duis sit amet diam eu dolor egestas rhoncus. Proin nisl sem,',
                                    '2019-06-07', '2025-01-12', 2), (59, 'Elit Pharetra Incorporated',
                                                                     'congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac ipsum. Phasellus vitae mauris sit amet lorem semper auctor. Mauris vel turpis. Aliquam adipiscing lobortis risus. In mi pede, nonummy ut, molestie in, tempus eu, ligula. Aenean euismod mauris eu elit. Nulla facilisi.',
                                                                     '2018-12-30', '2024-07-16', 1), (60, 'Euismod Ltd',
                                                                                                      'et magnis dis parturient montes, nascetur ridiculus mus. Proin vel nisl. Quisque fringilla euismod enim. Etiam gravida molestie arcu. Sed eu nibh vulputate mauris sagittis placerat. Cras dictum ultricies ligula. Nullam enim. Sed nulla ante, iaculis nec, eleifend non, dapibus rutrum, justo. Praesent luctus. Curabitur egestas nunc sed libero. Proin sed turpis nec mauris blandit mattis. Cras eget nisi dictum augue malesuada malesuada. Integer id magna et ipsum cursus vestibulum. Mauris magna. Duis dignissim tempor arcu. Vestibulum ut eros non enim commodo hendrerit. Donec porttitor tellus non magna. Nam',
                                                                                                      '2018-06-08',
                                                                                                      '2038-01-26', 2);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (61, 'Quis Industries',
        'aliquet. Phasellus fermentum convallis ligula. Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget, venenatis a, magna. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam laoreet, libero et tristique pellentesque, tellus sem mollis dui, in sodales elit erat vitae risus. Duis a mi fringilla mi lacinia mattis. Integer eu lacus. Quisque imperdiet, erat nonummy ultricies ornare, elit elit fermentum',
        '2019-03-10', '2023-05-22', 2), (62, 'Semper Egestas Urna LLP',
                                         'varius et, euismod et, commodo at, libero. Morbi accumsan laoreet ipsum. Curabitur consequat, lectus sit amet luctus vulputate, nisi sem semper erat, in consectetuer ipsum nunc id enim. Curabitur massa. Vestibulum accumsan neque et nunc. Quisque ornare tortor at risus.',
                                         '2019-02-03', '2046-04-01', 2), (63, 'Ac Turpis Egestas Corp.',
                                                                          'arcu eu odio tristique pharetra. Quisque ac libero nec ligula consectetuer rhoncus. Nullam velit dui, semper et, lacinia vitae, sodales at, velit. Pellentesque ultricies dignissim lacus. Aliquam rutrum lorem ac risus. Morbi metus. Vivamus euismod urna. Nullam lobortis quam a felis ullamcorper',
                                                                          '2019-02-20', '2032-04-18', 3),
  (64, 'Imperdiet Company',
   'massa. Vestibulum accumsan neque et nunc. Quisque ornare tortor at risus. Nunc ac sem ut dolor dapibus gravida. Aliquam tincidunt, nunc ac mattis ornare, lectus ante dictum mi, ac mattis velit justo',
   '2019-07-10', '2022-09-10', 1), (65, 'Duis Sit PC',
                                    'dui lectus rutrum urna, nec luctus felis purus ac tellus. Suspendisse sed dolor. Fusce mi lorem, vehicula et, rutrum eu, ultrices sit amet, risus. Donec nibh enim, gravida sit amet, dapibus id,',
                                    '2018-06-15', '2044-07-16', 3), (66, 'Fringilla Mi Institute',
                                                                     'semper, dui lectus rutrum urna, nec luctus felis purus ac tellus. Suspendisse sed dolor. Fusce mi lorem, vehicula et, rutrum eu, ultrices sit amet, risus. Donec nibh enim, gravida sit amet, dapibus id, blandit at, nisi. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel nisl. Quisque fringilla euismod enim. Etiam gravida molestie arcu. Sed eu nibh vulputate mauris sagittis placerat. Cras dictum ultricies ligula. Nullam enim. Sed nulla ante, iaculis nec, eleifend',
                                                                     '2018-06-30', '2025-06-02', 1),
  (67, 'Mattis Institute',
   'orci tincidunt adipiscing. Mauris molestie pharetra nibh. Aliquam ornare, libero at auctor ullamcorper, nisl arcu iaculis enim, sit',
   '2018-09-20', '2028-05-22', 1), (68, 'Id Company',
                                    'a tortor. Nunc commodo auctor velit. Aliquam nisl. Nulla eu neque pellentesque massa lobortis ultrices. Vivamus rhoncus. Donec est. Nunc ullamcorper, velit in aliquet lobortis, nisi nibh lacinia orci, consectetuer euismod est arcu ac orci. Ut semper pretium neque. Morbi quis urna. Nunc quis arcu vel quam dignissim pharetra. Nam ac nulla. In tincidunt congue turpis. In',
                                    '2019-05-12', '2040-12-17', 2), (69, 'Eros Non Enim Ltd',
                                                                     'est ac mattis semper, dui lectus rutrum urna, nec luctus felis purus ac tellus. Suspendisse sed dolor. Fusce mi lorem, vehicula et, rutrum eu, ultrices sit amet, risus. Donec nibh enim, gravida sit',
                                                                     '2018-12-26', '2030-11-04', 1),
  (70, 'Et Malesuada Limited',
   'Sed diam lorem, auctor quis, tristique ac, eleifend vitae, erat. Vivamus nisi. Mauris nulla. Integer urna. Vivamus molestie dapibus ligula. Aliquam erat volutpat. Nulla dignissim. Maecenas ornare egestas ligula. Nullam feugiat placerat velit. Quisque varius. Nam porttitor scelerisque neque. Nullam nisl. Maecenas malesuada fringilla est. Mauris eu turpis. Nulla aliquet. Proin velit. Sed malesuada augue ut lacus. Nulla tincidunt, neque vitae semper egestas, urna justo faucibus lectus, a sollicitudin orci sem eget massa. Suspendisse eleifend. Cras sed leo. Cras vehicula aliquet libero. Integer in magna. Phasellus dolor elit, pellentesque a, facilisis non, bibendum sed,',
   '2018-08-31', '2043-06-22', 2);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (71, 'Dolor Corporation',
        'scelerisque scelerisque dui. Suspendisse ac metus vitae velit egestas lacinia. Sed congue, elit sed consequat auctor, nunc nulla vulputate dui, nec tempus mauris erat eget ipsum. Suspendisse sagittis. Nullam vitae diam. Proin dolor. Nulla semper tellus id nunc interdum feugiat. Sed nec metus facilisis lorem tristique aliquet. Phasellus fermentum convallis ligula. Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget, venenatis a, magna. Lorem ipsum dolor sit',
        '2019-01-17', '2045-05-13', 1), (72, 'Euismod Industries',
                                         'Aliquam erat volutpat. Nulla dignissim. Maecenas ornare egestas ligula. Nullam feugiat placerat velit. Quisque varius. Nam porttitor scelerisque neque. Nullam nisl. Maecenas malesuada fringilla est. Mauris eu turpis. Nulla aliquet. Proin velit. Sed malesuada augue ut lacus. Nulla tincidunt, neque vitae semper egestas, urna justo faucibus lectus, a sollicitudin orci sem eget massa. Suspendisse eleifend. Cras sed leo. Cras vehicula aliquet libero. Integer',
                                         '2019-07-27', '2039-03-12', 1), (73, 'Aliquam Limited',
                                                                          'porttitor eros nec tellus. Nunc lectus pede, ultrices a, auctor non, feugiat nec, diam. Duis mi enim, condimentum eget, volutpat',
                                                                          '2019-07-02', '2038-08-02', 2),
  (74, 'Risus Duis A Associates',
   'magna. Phasellus dolor elit, pellentesque a, facilisis non, bibendum sed, est. Nunc laoreet lectus quis massa. Mauris vestibulum, neque sed dictum eleifend, nunc risus varius orci, in consequat enim diam vel arcu. Curabitur ut odio vel est tempor bibendum. Donec felis orci, adipiscing non, luctus sit amet, faucibus ut, nulla. Cras eu tellus eu augue porttitor interdum. Sed auctor odio a purus. Duis elementum, dui quis accumsan convallis, ante lectus convallis est, vitae sodales nisi magna sed dui. Fusce aliquam, enim nec tempus scelerisque, lorem ipsum sodales purus, in molestie tortor nibh sit amet orci. Ut sagittis lobortis mauris. Suspendisse',
   '2018-10-21', '2046-10-18', 1), (75, 'In Aliquet PC',
                                    'Aenean eget metus. In nec orci. Donec nibh. Quisque nonummy ipsum non arcu. Vivamus sit amet risus. Donec egestas. Aliquam nec enim. Nunc ut erat. Sed nunc est, mollis non, cursus non, egestas a, dui. Cras pellentesque. Sed dictum. Proin eget odio. Aliquam vulputate ullamcorper magna. Sed eu eros. Nam consequat dolor vitae dolor. Donec fringilla. Donec feugiat metus sit amet ante. Vivamus non lorem vitae odio sagittis semper. Nam tempor diam dictum sapien. Aenean massa. Integer vitae nibh. Donec est mauris, rhoncus id, mollis nec, cursus a, enim. Suspendisse aliquet, sem ut cursus luctus, ipsum leo elementum sem, vitae',
                                    '2018-05-21', '2039-02-28', 3), (76, 'Sit Corporation',
                                                                     'semper rutrum. Fusce dolor quam, elementum at, egestas a, scelerisque sed, sapien. Nunc pulvinar arcu et pede. Nunc sed orci lobortis augue scelerisque mollis. Phasellus libero mauris, aliquam eu, accumsan sed, facilisis',
                                                                     '2018-08-29', '2038-12-26', 1),
  (77, 'Dictum Augue LLP',
   'suscipit, est ac facilisis facilisis, magna tellus faucibus leo, in lobortis tellus justo sit amet nulla. Donec non justo. Proin non massa non ante bibendum ullamcorper. Duis cursus, diam at pretium aliquet, metus urna convallis erat, eget tincidunt dui augue eu tellus. Phasellus elit pede, malesuada vel, venenatis vel, faucibus id, libero. Donec consectetuer mauris id sapien. Cras dolor dolor, tempus non, lacinia at, iaculis quis, pede.',
   '2019-07-05', '2044-03-30', 2), (78, 'Curabitur Associates',
                                    'nec, diam. Duis mi enim, condimentum eget, volutpat ornare, facilisis eget, ipsum. Donec sollicitudin adipiscing ligula. Aenean gravida nunc sed pede. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel arcu eu odio tristique pharetra. Quisque ac libero nec ligula consectetuer rhoncus. Nullam velit',
                                    '2018-05-12', '2045-01-26', 1), (79, 'Eu Limited',
                                                                     'Quisque ornare tortor at risus. Nunc ac sem ut dolor dapibus gravida. Aliquam tincidunt, nunc ac mattis ornare, lectus ante dictum mi, ac mattis velit justo nec ante. Maecenas mi felis, adipiscing fringilla, porttitor vulputate, posuere vulputate, lacus. Cras interdum. Nunc',
                                                                     '2018-06-19', '2035-06-10', 1),
  (80, 'Aliquam Iaculis Ltd',
   'aliquam eros turpis non enim. Mauris quis turpis vitae purus gravida sagittis. Duis gravida. Praesent eu nulla at sem molestie sodales. Mauris blandit enim consequat purus. Maecenas libero est, congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla interdum. Curabitur dictum.',
   '2018-08-22', '2031-04-17', 2);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (81, 'Justo Nec Limited',
        'sodales elit erat vitae risus. Duis a mi fringilla mi lacinia mattis. Integer eu lacus. Quisque imperdiet, erat nonummy ultricies ornare, elit elit fermentum risus, at fringilla purus mauris a nunc. In at pede. Cras vulputate velit eu sem. Pellentesque',
        '2018-09-20', '2042-03-18', 3), (82, 'Est LLP',
                                         'lacus. Mauris non dui nec urna suscipit nonummy. Fusce fermentum fermentum arcu. Vestibulum ante ipsum primis in faucibus orci',
                                         '2018-08-11', '2038-06-01', 1), (83, 'Vitae Posuere PC',
                                                                          'molestie dapibus ligula. Aliquam erat volutpat. Nulla dignissim. Maecenas ornare egestas ligula. Nullam feugiat placerat velit. Quisque varius. Nam porttitor scelerisque neque. Nullam nisl. Maecenas malesuada fringilla est. Mauris eu turpis. Nulla aliquet. Proin velit. Sed malesuada augue ut lacus. Nulla tincidunt, neque vitae semper egestas, urna justo faucibus lectus, a sollicitudin orci sem eget massa. Suspendisse eleifend. Cras sed leo. Cras vehicula aliquet libero. Integer in magna. Phasellus dolor elit, pellentesque a, facilisis non, bibendum sed, est. Nunc laoreet lectus quis massa. Mauris vestibulum, neque sed dictum eleifend, nunc risus varius orci, in consequat enim',
                                                                          '2018-08-17', '2033-02-23', 3),
  (84, 'Metus Aenean Sed Corporation',
   'ornare placerat, orci lacus vestibulum lorem, sit amet ultricies sem magna nec quam. Curabitur vel lectus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec dignissim magna a tortor. Nunc commodo auctor velit. Aliquam nisl. Nulla eu neque pellentesque massa lobortis ultrices. Vivamus rhoncus. Donec est. Nunc ullamcorper, velit in aliquet lobortis, nisi nibh lacinia orci, consectetuer euismod est arcu ac orci. Ut semper pretium neque. Morbi quis urna. Nunc quis arcu vel quam dignissim pharetra. Nam ac nulla. In tincidunt congue turpis. In condimentum. Donec at arcu. Vestibulum ante ipsum',
   '2018-04-24', '2029-12-15', 3), (85, 'Feugiat Placerat Velit Institute',
                                    'blandit at, nisi. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Proin vel nisl. Quisque fringilla euismod enim. Etiam gravida molestie arcu. Sed eu nibh vulputate mauris sagittis placerat. Cras dictum ultricies ligula. Nullam enim. Sed nulla ante, iaculis nec, eleifend non, dapibus rutrum, justo. Praesent luctus. Curabitur egestas nunc sed libero. Proin sed turpis nec mauris blandit mattis. Cras eget nisi dictum augue malesuada malesuada. Integer',
                                    '2018-12-15', '2036-05-20', 1), (86, 'Interdum Ligula Eu Institute',
                                                                     'Sed nec metus facilisis lorem tristique aliquet. Phasellus fermentum convallis ligula. Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget, venenatis a, magna. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.',
                                                                     '2019-03-05', '2032-11-14', 2),
  (87, 'Semper Tellus Incorporated',
   'lectus ante dictum mi, ac mattis velit justo nec ante. Maecenas mi felis, adipiscing fringilla, porttitor vulputate, posuere vulputate, lacus. Cras interdum. Nunc sollicitudin commodo ipsum. Suspendisse non leo. Vivamus nibh dolor, nonummy ac, feugiat non, lobortis quis, pede.',
   '2018-11-30', '2024-03-15', 1), (88, 'Eu Inc.',
                                    'Proin ultrices. Duis volutpat nunc sit amet metus. Aliquam erat volutpat. Nulla facilisis. Suspendisse commodo tincidunt nibh. Phasellus nulla. Integer vulputate, risus a ultricies adipiscing, enim mi tempor lorem, eget mollis lectus pede et risus. Quisque libero lacus, varius et, euismod et, commodo at, libero. Morbi accumsan laoreet ipsum. Curabitur consequat, lectus sit amet luctus vulputate, nisi sem semper erat, in consectetuer ipsum nunc id enim. Curabitur',
                                    '2018-08-21', '2031-08-05', 1), (89, 'Ut Industries',
                                                                     'a nunc. In at pede. Cras vulputate velit eu sem. Pellentesque ut ipsum ac mi eleifend egestas. Sed pharetra, felis eget varius ultrices, mauris ipsum porta elit, a feugiat tellus lorem eu metus. In lorem. Donec elementum, lorem ut aliquam iaculis, lacus pede sagittis augue, eu tempor erat neque non quam. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam fringilla cursus purus. Nullam scelerisque neque sed sem egestas blandit. Nam nulla magna, malesuada vel, convallis in, cursus et, eros. Proin ultrices. Duis volutpat nunc sit amet metus. Aliquam erat volutpat. Nulla facilisis. Suspendisse commodo',
                                                                     '2018-05-31', '2031-03-06', 2),
  (90, 'Quis Massa LLP',
   'amet, consectetuer adipiscing elit. Etiam laoreet, libero et tristique pellentesque, tellus sem mollis dui, in sodales elit erat vitae risus. Duis a mi fringilla mi lacinia mattis. Integer eu lacus. Quisque imperdiet, erat nonummy ultricies ornare, elit elit fermentum risus, at fringilla purus mauris a nunc. In at pede. Cras vulputate velit eu sem. Pellentesque ut ipsum ac mi eleifend egestas. Sed pharetra, felis eget varius ultrices, mauris ipsum porta elit, a feugiat tellus lorem',
   '2019-01-16', '2040-06-27', 1);
INSERT INTO "advertisement" (advertisement_id, caption, description, show_first_date, show_end_date, type_advertisement_id)
VALUES (91, 'Aliquam Fringilla Ltd',
        'quam a felis ullamcorper viverra. Maecenas iaculis aliquet diam. Sed diam lorem, auctor quis, tristique ac, eleifend vitae, erat. Vivamus nisi. Mauris nulla. Integer urna. Vivamus molestie dapibus ligula. Aliquam erat volutpat. Nulla dignissim. Maecenas ornare egestas ligula. Nullam feugiat placerat velit. Quisque varius. Nam porttitor scelerisque neque. Nullam nisl. Maecenas malesuada fringilla est. Mauris eu turpis. Nulla aliquet. Proin velit. Sed malesuada augue ut lacus. Nulla tincidunt, neque vitae semper egestas, urna justo faucibus lectus, a',
        '2019-06-14', '2029-09-14', 1), (92, 'Neque Corp.',
                                         'nec enim. Nunc ut erat. Sed nunc est, mollis non, cursus non, egestas a, dui. Cras pellentesque. Sed dictum. Proin eget odio. Aliquam vulputate',
                                         '2018-09-16', '2033-04-29', 1), (93, 'Mi Tempor Incorporated',
                                                                          'posuere cubilia Curae; Donec tincidunt. Donec vitae erat vel pede blandit congue. In scelerisque scelerisque dui. Suspendisse ac metus vitae velit egestas lacinia. Sed congue, elit sed consequat auctor, nunc nulla vulputate dui, nec tempus mauris erat eget ipsum. Suspendisse sagittis. Nullam vitae diam. Proin dolor. Nulla semper tellus id nunc interdum feugiat. Sed nec metus facilisis lorem tristique aliquet. Phasellus fermentum convallis ligula. Donec luctus aliquet odio. Etiam ligula tortor, dictum eu, placerat eget,',
                                                                          '2019-04-11', '2029-06-10', 2),
  (94, 'Euismod Mauris Consulting',
   'dui. Fusce aliquam, enim nec tempus scelerisque, lorem ipsum sodales purus, in molestie tortor nibh sit amet orci. Ut sagittis lobortis mauris. Suspendisse aliquet molestie tellus. Aenean egestas hendrerit neque. In ornare sagittis felis. Donec tempor, est ac mattis semper, dui lectus',
   '2018-10-03', '2033-07-30', 1), (95, 'Lorem Lorem Luctus PC',
                                    'elit erat vitae risus. Duis a mi fringilla mi lacinia mattis. Integer eu lacus. Quisque imperdiet, erat nonummy ultricies ornare, elit elit fermentum risus, at fringilla purus mauris a nunc. In at pede. Cras vulputate velit eu sem. Pellentesque ut ipsum ac mi eleifend egestas. Sed pharetra, felis eget varius ultrices, mauris ipsum porta elit, a feugiat tellus',
                                    '2019-06-25', '2029-05-01', 2), (96, 'Nulla Eget Metus Company',
                                                                     'sem ut cursus luctus, ipsum leo elementum sem, vitae aliquam eros turpis non enim. Mauris quis turpis vitae purus gravida sagittis. Duis gravida. Praesent eu nulla at sem molestie sodales. Mauris blandit enim consequat purus. Maecenas libero est, congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla interdum. Curabitur dictum. Phasellus in felis. Nulla tempor augue ac ipsum. Phasellus vitae mauris sit amet lorem semper auctor. Mauris vel turpis. Aliquam adipiscing lobortis risus. In mi pede, nonummy ut, molestie in, tempus eu, ligula.',
                                                                     '2018-12-23', '2034-03-28', 3),
  (97, 'Congue In Associates',
   'fringilla cursus purus. Nullam scelerisque neque sed sem egestas blandit. Nam nulla magna, malesuada vel, convallis in, cursus et, eros. Proin ultrices. Duis volutpat nunc sit amet metus. Aliquam erat volutpat. Nulla facilisis. Suspendisse commodo tincidunt nibh. Phasellus nulla. Integer vulputate, risus a ultricies adipiscing, enim mi tempor lorem, eget mollis lectus',
   '2018-08-20', '2039-05-03', 3), (98, 'Est Mauris Company',
                                    'quis diam. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Fusce aliquet magna a neque. Nullam ut nisi a odio semper cursus. Integer mollis. Integer tincidunt aliquam arcu. Aliquam ultrices iaculis odio. Nam interdum enim non nisi. Aenean eget metus. In nec orci. Donec nibh. Quisque nonummy ipsum non arcu. Vivamus sit amet risus. Donec egestas. Aliquam nec enim. Nunc ut erat. Sed nunc est, mollis non, cursus non, egestas a, dui. Cras pellentesque. Sed dictum. Proin',
                                    '2018-12-26', '2038-09-25', 1), (99, 'Viverra Donec Associates',
                                                                     'porttitor tellus non magna. Nam ligula elit, pretium et, rutrum non, hendrerit id, ante. Nunc mauris sapien, cursus in, hendrerit consectetuer, cursus et, magna. Praesent interdum ligula eu enim. Etiam imperdiet dictum magna. Ut tincidunt orci quis lectus. Nullam suscipit, est ac facilisis facilisis, magna tellus faucibus leo, in lobortis tellus justo sit amet nulla. Donec non justo. Proin non massa non ante bibendum ullamcorper. Duis cursus, diam at pretium aliquet, metus urna convallis erat, eget tincidunt dui augue eu tellus. Phasellus elit pede,',
                                                                     '2018-04-17', '2024-01-05', 3), (100, 'Tortor PC',
                                                                                                      'nulla at sem molestie sodales. Mauris blandit enim consequat purus. Maecenas libero est, congue a, aliquet vel, vulputate eu, odio. Phasellus at augue id ante dictum cursus. Nunc mauris elit, dictum eu, eleifend nec, malesuada ut, sem. Nulla',
                                                                                                      '2019-06-20',
                                                                                                      '2036-11-21', 2);


INSERT INTO reset_password (person_id, reset_token) VALUES (4, '47a95c8c-a5d1-4984-9ce5-904ecd81e637');

INSERT INTO logistic_company."order" (order_id, estimated_delivery_time, courier_id, receiver_contact_id, receiver_address_id, sender_contact_id, sender_address_id, office_id, order_status_id, order_type_id, weight, width, height, length)
VALUES (1, '04:05:06', 7, 5, 1, 2, 2, 1, 6, 1, 12, 12, 12, 12);
INSERT INTO logistic_company."order" (order_id, estimated_delivery_time, courier_id, receiver_contact_id, receiver_address_id, sender_contact_id, sender_address_id, office_id, order_status_id, order_type_id, weight, width, height, length)
VALUES (3, '14:05:06', 7, 5, 1, 2, 2, 1, 6, 1, 12, 12, 12, 12);
INSERT INTO logistic_company."order" (order_id, estimated_delivery_time, courier_id, receiver_contact_id, receiver_address_id, sender_contact_id, sender_address_id, office_id, order_status_id, order_type_id, weight, width, height, length)
VALUES (2, '04:05:06', 7, 2, 1, 5, 2, 1, 6, 2, 112, 132, 1322, 12);

INSERT INTO logistic_company."courier_data" (person_id, courier_status, courier_last_location, courier_travel_mode)
VALUES (5, trim('FREE') :: logistic_company.COURIER_STATUS, '', 'DRIVING');
INSERT INTO logistic_company."courier_data" (person_id, courier_status, courier_last_location, courier_travel_mode)
VALUES (7, trim('FREE') :: logistic_company.COURIER_STATUS, '', 'WALKING');
INSERT INTO logistic_company."courier_data" (person_id, courier_status, courier_last_location, courier_travel_mode)
VALUES (8, trim('FREE') :: logistic_company.COURIER_STATUS, '', 'WALKING');