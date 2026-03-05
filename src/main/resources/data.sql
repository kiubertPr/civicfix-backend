-- Usuarios
INSERT INTO Users (userName, password, firstName, lastName, email, avatar, role, provider) values 
('admin', '$2a$10$2tvbaq09YoRdr.driBvX1.4oD6MCRlwGLoSZzXqEUFLiIev5HGdrC', 'Admin', 'Admin', 'admin@admin.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 0, 'LOCAL'),
('a', '$2a$10$vKCyYVu28x02dMi9Fl8QrOLjcfUcQn2kJ9LBGVVvalJQ3psbgSq1.', 'a', 'a', 'a@a.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('b', '$2a$10$qf6W//WtrSfJMSmTYLd4W.eSnxeEmfSY.92zGat/FSr.ZgpOfFJOu', 'b', 'b', 'b@b.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('c', '$2a$10$xL3KgTbugM9ZPHY6rIoCwOdmQhAY0nkHbqFGSFUu59gpYF7zaOSoS', 'c', 'c', 'c@c.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('d', '$2a$10$2NPeeWizaYxkizrJ7uP3FuNsYVqCYWgkn0bRgIMNxmrlBD90zX7XK', 'd', 'd', 'd@d.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('e', '$2a$10$gP8.R9x.wZaIYq6gIbG/cu7BANx016b.1Vpoa8tAT4qOUPPNgpe0u', 'e', 'e', 'e@e.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('f', '$2a$10$sVd0yJgxs9rjJ9o7U./jzO.UDv1hn6U6K5ZvdYSZ71cGM8NVV3ORm', 'f', 'f', 'f@f.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('g', '$2a$10$5Ms1H4i5jzo1UvLz1oqRi.qYP8wXIPatgVhaZ5FNLHPU/UMydlCfy', 'g', 'g', 'g@g.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('h', '$2a$10$Z6fulEyKBZPFVO0ToZ3Co.wXkZTdJxUdCWRaQbaIPjRANIRBDkaUe', 'h', 'h', 'h@h.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('i', '$2a$10$ehRq1//Rj0AR3raTSkszVOzFBdpUHop0M9/QlDu.dEVONq2oyhPBG', 'i', 'i', 'i@i.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('j', '$2a$10$iJqPhcMNDl.9iRO3vo4ws.jJ0Alr722g1UDuBceFRZf1WNMZDY0YW', 'j', 'j', 'j@j.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL'),
('k', '$2a$10$4BN1rwwQzj9cyqB12CnlEuxq8LeB1TDMOHTuTOXxp1JNZ4So5Bsmm', 'k', 'k', 'k@k.com', 'https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg', 1, 'LOCAL');

-- Posts iniciales
INSERT INTO Posts (title, content, location, latitude, longitude, date, userId, category) VALUES 
('Parque infantil en mal estado', 'El parque infantil en la calle Mayor presenta estructuras oxidadas y deterioradas. Es un riesgo para los niños. Se solicita reparación urgente.', 'Calle Mayor, 15', 43.3713, -8.3956, NOW(), 9, 'USER'),
('Parque descuidado en el centro', 'El parque central de la ciudad presenta áreas verdes descuidadas, bancos rotos y basura acumulada. Se solicita intervención municipal.', 'Calle El Sol, 23', 43.3685, -8.3962, NOW(), 10, 'USER'),
('Anuncio de eventos en parque renovado', 'Este fin de semana se inaugurará oficialmente el nuevo parque central. Habrá actividades culturales y recreativas para toda la familia.', 'Parque de Santa Margarita', 43.3576, -8.4088, NOW(), 1, 'ADMINISTRATION'),
('Facultad universitaria en mal estado', 'La Facultad de Informática muestra señales de abandono en su entorno: maleza, paredes sucias y mobiliario dañado.', 'Barrio San Martín', 43.3650, -8.4080, NOW(), 3, 'USER'),
('Edificio abandonado en el barrio', 'En la calle Libertad hay un edificio abandonado en estado de deterioro. Representa un riesgo de seguridad para el vecindario.', 'Calle Libertad, 45', 43.3700, -8.4070, NOW(), 4, 'USER'),
('Contenedores sucios en zona comunitaria', 'Los contenedores de basura en el centro comunitario están desbordados y sucios. Se solicita limpieza y mantenimiento.', 'Centro Comunitario, Ciudad', 43.3634, -8.4115, NOW(), 1, 'ADMINISTRATION'),
('Banco roto en vía pública', 'Se reporta un banco roto en la calle Independencia. Es peligroso para los peatones y da mala imagen al entorno.', 'Calle Independencia, 12', 43.3692, -8.4032, NOW(), 6, 'USER');

-- Imágenes asociadas
INSERT INTO posts_images (postsId, publicId, url) VALUES 
(1, 'parque-infantil', 'https://res.cloudinary.com/civicfix/image/upload/v1753735603/parque-infantil-Torrevieja-mal-estado_kmdxqf.jpg'),
(3, 'parque-infantil', 'https://res.cloudinary.com/civicfix/image/upload/v1753735603/parque-infantil-Torrevieja-mal-estado_kmdxqf.jpg'),
(2, 'parque-descuidado', 'https://res.cloudinary.com/civicfix/image/upload/v1753735602/parque-central-YrkAgn2YY_o4gayq.jpg'),
(3, 'inauguracion-parque', 'https://res.cloudinary.com/civicfix/image/upload/v1753735511/dsc_0127_2_rtqdmz.jpg'),
(4, 'facultad-abandono', 'https://res.cloudinary.com/civicfix/image/upload/v1753735602/Facultade_de_Inform%C3%A1tica.002_-_UDC_cjpjal.jpg'),
(5, 'edificio-abandonado', 'https://res.cloudinary.com/civicfix/image/upload/v1753735601/Elvira-Serrano-3-650x365_ghurml.jpg'),
(6, 'contenedores-sucios', 'https://res.cloudinary.com/civicfix/image/upload/v1753735600/contenedores-basura-sucios-torrelodones_ljyhpd.jpg'),
(1, 'contenedores-sucios', 'https://res.cloudinary.com/civicfix/image/upload/v1753735600/contenedores-basura-sucios-torrelodones_ljyhpd.jpg'),
(7, 'banco-roto', 'https://res.cloudinary.com/civicfix/image/upload/v1753735600/banca-rotta_t7zelv.jpg');

-- Nuevos Posts con fechas variadas
INSERT INTO Posts (title, content, location, latitude, longitude, date, userId, category) VALUES
('Bache peligroso en esquina', 'Gran bache en la esquina de Calle Rosalía. Representa un peligro para vehículos y peatones. Urge reparación.', 'Calle Rosalía, 10', 43.3622, -8.4089, DATEADD('DAY', -2, CURRENT_TIMESTAMP), 7, 'USER'),
('Árbol caído en la intersección', 'En la intersección de Calle Real y Av. del Mar hay un árbol caído obstruyendo el paso peatonal. Se requiere intervención.', 'Calle Real y Av. del Mar', 43.3661, -8.3998, DATEADD('DAY', -5, CURRENT_TIMESTAMP), 8, 'USER'),
('Acera rota en parque', 'Las losetas de la acera en el parque de San Pedro están levantadas y rotas. Podrían causar caídas.', 'Parque de San Pedro', 43.3591, -8.4182, DATEADD('DAY', -7, CURRENT_TIMESTAMP), 9, 'USER'),
('Jornada de limpieza comunitaria', 'El sábado próximo se organiza limpieza en la playa de Riazor. ¡Participa!', 'Playa de Riazor', 43.3719, -8.4127, DATEADD('DAY', -10, CURRENT_TIMESTAMP), 1, 'ADMINISTRATION'),
('Mejora de accesibilidad peatonal', 'Una señal de stop está caída en la Calle Barcelona. Se realizarán mejoras de accesibilidad en la zona peatonal.', 'Calle Barcelona, 22', 43.3730, -8.4050, DATEADD('DAY', -3, CURRENT_TIMESTAMP), 11, 'USER'),
('Obras en el centro cívico', 'Durante esta semana se desarrollarán obras de mejora en la zona peatonal del centro cívico. Se recomienda transitar con precaución.', 'Centro Cívico A Coruña', 43.3617, -8.4092, DATEADD('DAY', -4, CURRENT_TIMESTAMP), 1, 'ADMINISTRATION'),
('Tapa de alcantarilla rota', 'Hay una tapa de alcantarilla rota frente al número 33 de Calle Estrella. Representa un peligro.', 'Calle Estrella, 33', 43.3675, -8.4021, DATEADD('DAY', -9, CURRENT_TIMESTAMP), 4, 'USER'),
('Sombra solicitada para juegos', 'Los juegos del parque de la Calle Lago carecen de sombra. Propongo instalar toldos.', 'Calle Lago, Parque Infantil', 43.3659, -8.4003, DATEADD('DAY', -14, CURRENT_TIMESTAMP), 5, 'USER'),
('Fuga de agua persistente', 'Hay una fuga constante de agua en un callejón sin nombre detrás del mercado.', 'Callejón del Mercado', 43.3633, -8.3975, DATEADD('DAY', -6, CURRENT_TIMESTAMP), 6, 'USER'),
('Taller de compostaje urbano', 'Se dictará un taller gratuito de compostaje urbano el viernes en el centro cultural San Agustín. ¡Inscríbete!', 'Centro Cultural San Agustín', 43.3589, -8.4101, DATEADD('DAY', -12, CURRENT_TIMESTAMP), 1, 'ADMINISTRATION');

-- Imágenes asociadas a los 10 nuevos posts
INSERT INTO posts_images (postsId, publicId, url) VALUES 
(8, 'bache', 'https://res.cloudinary.com/civicfix/image/upload/v1753735600/bache_wmg0ay.jpg'),
(9, 'arbol-caido', 'https://res.cloudinary.com/civicfix/image/upload/v1753735599/676a9174a818b_pfar0z.jpg'),
(10, 'acera-rota', 'https://res.cloudinary.com/civicfix/image/upload/v1753735599/032D5CTGP2_1_iv9wyw.jpg'),
(11, 'limpieza-comunitaria', 'https://res.cloudinary.com/civicfix/image/upload/v1753735511/dsc_0127_2_rtqdmz.jpg'),
(8, 'limpieza-comunitaria', 'https://res.cloudinary.com/civicfix/image/upload/v1753735511/dsc_0127_2_rtqdmz.jpg'),
(12, 'peatonal-obras', 'https://res.cloudinary.com/civicfix/image/upload/v1753735511/fb0faa59-2c1a-4905-bd22-ff4f49403e49_16-9-discover-aspect-ratio_default_0_emeasd.jpg'),
(13, 'obras-centro-civico', 'https://res.cloudinary.com/civicfix/image/upload/v1753735511/27-10-2006_ObrasArenalPeatonal_15_ul4pud.jpg'),
(14, 'alcantarilla-rota', 'https://res.cloudinary.com/civicfix/image/upload/v1753735600/banca-rotta_t7zelv.jpg'),
(11, 'alcantarilla-rota', 'https://res.cloudinary.com/civicfix/image/upload/v1753735600/banca-rotta_t7zelv.jpg'),
(15, 'sombra-parque', 'https://res.cloudinary.com/civicfix/image/upload/v1753735603/parque-infantil-Torrevieja-mal-estado_kmdxqf.jpg'),
(11, 'sombra-parque', 'https://res.cloudinary.com/civicfix/image/upload/v1753735603/parque-infantil-Torrevieja-mal-estado_kmdxqf.jpg'),
(16, 'fuga-agua', 'https://res.cloudinary.com/civicfix/image/upload/v1753735602/parque-central-YrkAgn2YY_o4gayq.jpg'),
(17, 'taller-compostaje', 'https://res.cloudinary.com/civicfix/image/upload/v1753735602/Facultade_de_Inform%C3%A1tica.002_-_UDC_cjpjal.jpg');


-- Votos de los posts
INSERT INTO PostVotes (userId, postId, vote) VALUES
(1, 1, 1),
(2, 1, 1),
(3, 2, 1),
(4, 2, -1),
(5, 3, 1),
(6, 3, -1),
(7, 4, 1),
(8, 4, 1),
(9, 5, 1),
(10, 5, -1),
(11, 6, 1),
(10, 6, -1),
(3, 7, 1),
(11, 7, -1);

-- Encuestas
INSERT INTO Surveys (question, createdAt, endDateTime, type) VALUES
('¿Qué opinas sobre la calidad del servicio de transporte público?', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP), 0),
('¿Cuál es tu opinión sobre la seguridad en el barrio?', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', 2, CURRENT_TIMESTAMP), 0),
('¿Qué mejoras te gustaría ver en el parque central?', DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', 10, CURRENT_TIMESTAMP), 1),
('¿Cómo calificarías la limpieza de las calles de la ciudad?', DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 1, CURRENT_TIMESTAMP), 0),
('¿Qué tipo de eventos culturales te gustaría que se organizaran?', DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', 7, CURRENT_TIMESTAMP), 1);


-- Opciones de las encuestas
INSERT INTO survey_options (surveyId, optionId, optionText) VALUES
(1, 0, 'Excelente'),
(1, 1, 'Bueno'),
(1, 2, 'Regular'),
(1, 3, 'Malo'),
(2, 0, 'Muy seguro'),
(2, 1, 'Seguro'),
(2, 2, 'Inseguro'),
(2, 3, 'Muy inseguro'),
(3, 0, 'Más bancos'),
(3, 1, 'Más iluminación'),
(3, 2, 'Más actividades recreativas'),
(3, 3, 'Nada en particular'),
(4, 0, 'Muy limpia'),
(4, 1, 'Limpia'),
(4, 2, 'Suciedad moderada'),
(4, 3, 'Muy sucia'),
(5, 0, 'Conciertos de música local'),
(5, 1, 'Exposiciones de arte'),
(5, 2, 'Talleres comunitarios'),
(5, 3, 'Eventos deportivos');

-- Respuestas a las encuestas
INSERT INTO SurveyResponses (surveyId, userId) VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 4),
(3, 5),
(3, 6),
(4, 7),
(4, 8),
(5, 9),
(5, 10);

-- Opciones de las respuestas a las encuestas
INSERT INTO survey_response_options (surveyResponseId, optionId) VALUES
(1, 0),
(1, 1),
(2, 2),
(2, 3),
(3, 0),
(3, 1),
(4, 2),
(4, 3),
(5, 0),
(5, 1);

