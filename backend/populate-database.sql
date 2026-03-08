-- ============================================
-- Script para Llenar la Base de Datos Library
-- ============================================
-- Este script puebla la BD con datos realistas
-- incluyendo libros prestados a diferentes clientes

-- ============================================
-- LIMPIAR DATOS EXISTENTES (OPCIONAL)
-- ============================================
-- Descomentar si deseas limpiar datos previos
-- TRUNCATE TABLE borrowed_book CASCADE;
-- TRUNCATE TABLE rel_book__author CASCADE;
-- TRUNCATE TABLE book CASCADE;
-- TRUNCATE TABLE client CASCADE;
-- TRUNCATE TABLE author CASCADE;
-- TRUNCATE TABLE publisher CASCADE;

-- Reiniciar secuencias
-- ALTER SEQUENCE book_seq RESTART WITH 1;
-- ALTER SEQUENCE client_seq RESTART WITH 1;
-- ALTER SEQUENCE author_seq RESTART WITH 1;
-- ALTER SEQUENCE publisher_seq RESTART WITH 1;
-- ALTER SEQUENCE borrowed_book_seq RESTART WITH 1;

-- ============================================
-- 1. INSERTAR EDITORIALES (Publishers)
-- ============================================
INSERT INTO publisher (id, name) VALUES
(1, 'Penguin Random House'),
(2, 'HarperCollins'),
(3, 'Simon & Schuster'),
(4, 'Macmillan Publishers'),
(5, 'Hachette Book Group'),
(6, 'Scholastic'),
(7, 'Pearson Education'),
(8, 'Wiley'),
(9, 'O''Reilly Media'),
(10, 'MIT Press'),
(11, 'Addison-Wesley'),
(12, 'Springer'),
(13, 'Editorial Planeta'),
(14, 'Alfaguara'),
(15, 'Anagrama')
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 2. INSERTAR AUTORES (Authors)
-- ============================================
INSERT INTO author (id, first_name, last_name) VALUES
(1, 'Gabriel', 'García Márquez'),
(2, 'Isabel', 'Allende'),
(3, 'Jorge Luis', 'Borges'),
(4, 'Julio', 'Cortázar'),
(5, 'Mario', 'Vargas Llosa'),
(6, 'Miguel', 'de Cervantes'),
(7, 'Federico', 'García Lorca'),
(8, 'Pablo', 'Neruda'),
(9, 'Octavio', 'Paz'),
(10, 'Carlos', 'Fuentes'),
(11, 'Robert', 'Martin'),
(12, 'Martin', 'Fowler'),
(13, 'Erich', 'Gamma'),
(14, 'Kent', 'Beck'),
(15, 'Eric', 'Evans'),
(16, 'Joshua', 'Bloch'),
(17, 'Andrew', 'Hunt'),
(18, 'David', 'Thomas'),
(19, 'Brian', 'Goetz'),
(20, 'Grady', 'Booch')
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 3. INSERTAR CLIENTES (Clients)
-- ============================================
-- IMPORTANTE: Los Clientes NO son Usuarios del sistema
-- Son personas que toman libros prestados
INSERT INTO client (id, first_name, last_name, email, address, phone) VALUES
(1, 'Carlos', 'Fernández', 'carlos.fernandez@email.com', 'Calle Principal 123', '+54 11 4567-8901'),
(2, 'María', 'González', 'maria.gonzalez@email.com', 'Av. Libertador 456', '+54 11 4567-8902'),
(3, 'Juan', 'Rodríguez', 'juan.rodriguez@email.com', 'San Martín 789', '+54 11 4567-8903'),
(4, 'Ana', 'López', 'ana.lopez@email.com', 'Belgrano 321', '+54 11 4567-8904'),
(5, 'Pedro', 'Martínez', 'pedro.martinez@email.com', 'Rivadavia 654', '+54 11 4567-8905'),
(6, 'Laura', 'Sánchez', 'laura.sanchez@email.com', 'Mitre 987', '+54 11 4567-8906'),
(7, 'Diego', 'Ramírez', 'diego.ramirez@email.com', 'Córdoba 147', '+54 11 4567-8907'),
(8, 'Sofía', 'Torres', 'sofia.torres@email.com', 'Santa Fe 258', '+54 11 4567-8908'),
(9, 'Martín', 'Flores', 'martin.flores@email.com', 'Corrientes 369', '+54 11 4567-8909'),
(10, 'Valentina', 'Morales', 'valentina.morales@email.com', 'Entre Ríos 741', '+54 11 4567-8910'),
(11, 'Lucas', 'Benítez', 'lucas.benitez@email.com', 'Tucumán 852', '+54 11 4567-8911'),
(12, 'Camila', 'Castro', 'camila.castro@email.com', 'Jujuy 963', '+54 11 4567-8912'),
(13, 'Facundo', 'Romero', 'facundo.romero@email.com', 'Salta 159', '+54 11 4567-8913'),
(14, 'Agustina', 'Vega', 'agustina.vega@email.com', 'Mendoza 357', '+54 11 4567-8914'),
(15, 'Nicolás', 'Silva', 'nicolas.silva@email.com', 'La Plata 486', '+54 11 4567-8915')
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 4. INSERTAR LIBROS (Books)
-- ============================================
INSERT INTO book (id, isbn, name, publish_year, copies, publisher_id, cover_content_type) VALUES
-- Literatura en Español
(1, '9788437600001', 'Cien Años de Soledad', '1967', 5, 1, 'image/png'),
(2, '9788420400002', 'La Casa de los Espíritus', '1982', 4, 2, 'image/png'),
(3, '9788433900003', 'Ficciones', '1944', 3, 3, 'image/png'),
(4, '9788466300004', 'Rayuela', '1963', 4, 4, 'image/png'),
(5, '9788420400005', 'La Ciudad y los Perros', '1963', 3, 5, 'image/png'),
(6, '9788420600006', 'Don Quijote de la Mancha', '1605', 6, 6, 'image/png'),
(7, '9788437600007', 'Bodas de Sangre', '1933', 2, 13, 'image/png'),
(8, '9788433900008', 'Veinte Poemas de Amor', '1924', 4, 14, 'image/png'),
(9, '9788433900009', 'El Laberinto de la Soledad', '1950', 3, 15, 'image/png'),
(10, '9788466300010', 'La Muerte de Artemio Cruz', '1962', 2, 4, 'image/png'),

-- Literatura Técnica / Programación
(11, '9780132100011', 'Clean Code', '2008', 8, 11, 'image/png'),
(12, '9780133600012', 'Refactoring', '2018', 6, 11, 'image/png'),
(13, '9780201600013', 'Design Patterns', '1994', 5, 11, 'image/png'),
(14, '9780321100014', 'Test Driven Development', '2002', 4, 11, 'image/png'),
(15, '9780321300015', 'Domain-Driven Design', '2003', 5, 11, 'image/png'),
(16, '9780134600016', 'Effective Java', '2017', 7, 11, 'image/png'),
(17, '9780201600017', 'The Pragmatic Programmer', '2019', 6, 11, 'image/png'),
(18, '9780321300018', 'Java Concurrency in Practice', '2006', 4, 11, 'image/png'),
(19, '9780132100019', 'The Clean Coder', '2011', 5, 11, 'image/png'),
(20, '9780201600020', 'Object-Oriented Analysis', '2007', 3, 11, 'image/png'),

-- Libros adicionales
(21, '9788437600021', 'El Amor en los Tiempos del Cólera', '1985', 4, 1, 'image/png'),
(22, '9788466300022', 'Conversación en la Catedral', '1969', 3, 5, 'image/png'),
(23, '9780134600023', 'Clean Architecture', '2017', 6, 11, 'image/png'),
(24, '9780321300024', 'Patterns of Enterprise', '2002', 4, 11, 'image/png'),
(25, '9788433900025', 'El Aleph', '1949', 4, 3, 'image/png')
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 5. RELACIONES LIBRO-AUTOR (Many-to-Many)
-- ============================================
INSERT INTO rel_book__author (book_id, author_id) VALUES
-- Literatura en Español
(1, 1),   -- Cien Años de Soledad - García Márquez
(2, 2),   -- La Casa de los Espíritus - Isabel Allende
(3, 3),   -- Ficciones - Borges
(4, 4),   -- Rayuela - Cortázar
(5, 5),   -- La Ciudad y los Perros - Vargas Llosa
(6, 6),   -- Don Quijote - Cervantes
(7, 7),   -- Bodas de Sangre - García Lorca
(8, 8),   -- Veinte Poemas - Neruda
(9, 9),   -- El Laberinto - Octavio Paz
(10, 10), -- La Muerte de Artemio Cruz - Carlos Fuentes
(21, 1),  -- El Amor en los Tiempos - García Márquez
(22, 5),  -- Conversación en la Catedral - Vargas Llosa
(25, 3),  -- El Aleph - Borges

-- Libros Técnicos
(11, 11), -- Clean Code - Robert Martin
(12, 12), -- Refactoring - Martin Fowler
(13, 13), -- Design Patterns - Erich Gamma (Gang of Four)
(14, 14), -- TDD - Kent Beck
(15, 15), -- DDD - Eric Evans
(16, 16), -- Effective Java - Joshua Bloch
(17, 17), -- Pragmatic Programmer - Andrew Hunt
(17, 18), -- Pragmatic Programmer - David Thomas
(18, 19), -- Java Concurrency - Brian Goetz
(19, 11), -- Clean Coder - Robert Martin
(20, 20), -- OOA - Grady Booch
(23, 11), -- Clean Architecture - Robert Martin
(24, 12)  -- Patterns of Enterprise - Martin Fowler
ON CONFLICT (book_id, author_id) DO NOTHING;

-- ============================================
-- 6. LIBROS PRESTADOS (BorrowedBook)
-- ============================================
-- Aseguramos que varios clientes tienen libros prestados
-- con diferentes fechas de préstamo
INSERT INTO borrowed_book (id, borrow_date, book_id, client_id) VALUES
-- Préstamos recientes (última semana)
(1, '2026-03-01', 1, 1),   -- Carlos tiene 'Cien Años de Soledad'
(2, '2026-03-02', 11, 2),  -- María tiene 'Clean Code'
(3, '2026-03-03', 3, 3),   -- Juan tiene 'Ficciones'
(4, '2026-03-04', 16, 4),  -- Ana tiene 'Effective Java'
(5, '2026-03-05', 4, 5),   -- Pedro tiene 'Rayuela'

-- Préstamos de hace 2 semanas
(6, '2026-02-22', 12, 6),  -- Laura tiene 'Refactoring'
(7, '2026-02-23', 6, 7),   -- Diego tiene 'Don Quijote'
(8, '2026-02-24', 13, 8),  -- Sofía tiene 'Design Patterns'
(9, '2026-02-25', 2, 9),   -- Martín tiene 'La Casa de los Espíritus'

-- Préstamos de hace 3 semanas
(10, '2026-02-15', 17, 10), -- Valentina tiene 'Pragmatic Programmer'
(11, '2026-02-16', 8, 11),  -- Lucas tiene 'Veinte Poemas de Amor'
(12, '2026-02-17', 23, 12), -- Camila tiene 'Clean Architecture'

-- Préstamos de hace 1 mes
(13, '2026-02-08', 5, 13),  -- Facundo tiene 'La Ciudad y los Perros'
(14, '2026-02-10', 15, 14), -- Agustina tiene 'Domain-Driven Design'
(15, '2026-02-12', 25, 15)  -- Nicolás tiene 'El Aleph'
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- VERIFICACIÓN DE DATOS
-- ============================================
-- Estas consultas te permiten verificar los datos insertados

-- Ver todos los clientes con libros prestados
SELECT
    c.id,
    c.first_name || ' ' || c.last_name AS cliente,
    b.name AS libro,
    bb.borrow_date AS fecha_prestamo,
    CURRENT_DATE - bb.borrow_date AS dias_prestado
FROM borrowed_book bb
JOIN client c ON bb.client_id = c.id
JOIN book b ON bb.book_id = b.id
ORDER BY bb.borrow_date DESC;

-- Ver libros con sus autores
SELECT
    b.id,
    b.name AS libro,
    b.publish_year AS año,
    STRING_AGG(a.first_name || ' ' || a.last_name, ', ') AS autores,
    p.name AS editorial
FROM book b
LEFT JOIN rel_book__author rba ON b.id = rba.book_id
LEFT JOIN author a ON rba.author_id = a.id
LEFT JOIN publisher p ON b.publisher_id = p.id
GROUP BY b.id, b.name, b.publish_year, p.name
ORDER BY b.id;

-- Ver estadísticas
SELECT
    'Total Libros' AS concepto,
    COUNT(*) AS cantidad
FROM book
UNION ALL
SELECT
    'Total Clientes',
    COUNT(*)
FROM client
UNION ALL
SELECT
    'Libros Prestados',
    COUNT(*)
FROM borrowed_book
UNION ALL
SELECT
    'Libros Disponibles',
    COUNT(*) - (SELECT COUNT(*) FROM borrowed_book)
FROM book;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================
-- 1. Los CLIENTES (Client) son personas que toman libros prestados
--    NO son usuarios del sistema (User)
-- 2. Los USUARIOS del sistema son:
--    - admin (password: admin) - Rol: ADMIN
--    - user (password: user) - Rol: USER
-- 3. Cada BorrowedBook relaciona UN libro con UN cliente
-- 4. Las fechas se calculan desde hoy (2026-03-08) hacia atrás
-- 5. Puedes ajustar las fechas, cantidades y datos según necesites

-- ============================================
-- FIN DEL SCRIPT
-- ============================================
