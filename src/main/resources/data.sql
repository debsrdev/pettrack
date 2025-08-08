INSERT INTO users(id, username, email, password, role) VALUES
(1, 'Debora', 'debora@user.com', 'Debora123.', 'USER'),
(2, 'Roberto', 'roberto@user.com', 'Roberto123.', 'USER'),
(3, 'Jenni', 'jenni@user.com', 'Jenni123.', 'USER'),
(4, 'Carmen', 'carmen@vet.com', 'Carmen123.', 'VETERINARY'),
(5, 'Maribel', 'maribel@vet.com', 'Maribel123.', 'VETERINARY');

INSERT INTO pets(id, name, species, breed, birth_date, image, user_id) VALUES
(1, 'Luna', 'Perro', 'Golden Retriever', '2021-03-15', 'https://example.com/images/luna.jpg', 1),
(2, 'Milo', 'Gato', 'Siamés', '2022-06-20', 'https://example.com/images/milo.jpg', 1),
(3, 'Nala', 'Perro', 'Labrador', '2020-11-05', 'https://example.com/images/nala.jpg', 2),
(4, 'Simba', 'Gato', 'Maine Coon', '2021-09-12', 'https://example.com/images/simba.jpg', 2),
(5, 'Toby', 'Perro', 'Beagle', '2023-01-10', 'https://example.com/images/toby.jpg', 3),
(6, 'Coco', 'Conejo', 'Belier Holandés', '2022-03-08', 'https://example.com/images/coco.jpg', 3),
(7, 'Max', 'Perro', 'Pastor Alemán', '2020-05-20', 'https://example.com/images/max.jpg', 1),
(8, 'Lily', 'Gato', 'Abisinio', '2022-10-11', 'https://example.com/images/lily.jpg', 2),
(9, 'Daisy', 'Conejo', 'Cabeza de León', '2023-02-14', 'https://example.com/images/daisy.jpg', 3),
(10, 'Chispa', 'Perro', 'Chihuahua', '2021-08-30', 'https://example.com/images/chispa.jpg', 1),
(11, 'Kiwi', 'Agaporni', 'Agaporni Verde', '2022-09-01', 'https://example.com/images/kiwi.jpg', 2),
(12, 'Nube', 'Hámster', 'Hámster Ruso', '2023-03-10', 'https://example.com/images/nube.jpg', 3);

INSERT INTO medical_records(id, description, weight, date, type, pet_id, created_by_user_id) VALUES
(1, 'Vacunación antirrábica anual', 25.0, '2024-03-15', 'VACCINATION', 1, 4),
(2, 'Revisión general, sin incidencias', 4.5, '2024-05-10', 'REVISION', 2, 5),
(3, 'Desparasitación interna', 28.2, '2024-02-20', 'REVISION', 3, 4),
(4, 'Infección leve en el oído, se receta tratamiento', 6.7, '2024-04-08', 'REVISION', 4, 5),
(5, 'Primera vacuna cachorro', 10.0, '2024-01-12', 'VACCINATION', 5, 4),
(6, 'Revisión post-castración', 2.0, '2024-06-03', 'REVISION', 6, 5),
(7, 'Vacuna polivalente', 30.5, '2024-05-20', 'VACCINATION', 7, 4),
(8, 'Chequeo anual', 3.4, '2024-06-18', 'REVISION', 8, 5),
(9, 'Desparasitación externa', 1.8, '2024-07-05', 'REVISION', 9, 5),
(10, 'Vacuna leptospirosis', 2.5, '2024-06-10', 'VACCINATION', 10, 4),
(11, 'Corte de ala y revisión picos', 0.9, '2024-07-01', 'REVISION', 11, 5),
(12, 'Revisión inicial tras adopción', 0.3, '2024-07-22', 'REVISION', 12, 4);
