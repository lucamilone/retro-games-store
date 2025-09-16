INSERT INTO ruolo (nome, attivo, creato_il)
VALUES ('amministratore', TRUE, CURRENT_TIMESTAMP),
       ('cliente', TRUE, CURRENT_TIMESTAMP);


INSERT INTO categoria (nome, attivo, creato_il)
VALUES ('console', TRUE, CURRENT_TIMESTAMP),
       ('videogiochi', TRUE, CURRENT_TIMESTAMP),
       ('accessori', TRUE, CURRENT_TIMESTAMP);


INSERT INTO tipo_metodo_pagamento (nome, attivo, creato_il)
VALUES ('carta', TRUE, CURRENT_TIMESTAMP),
       ('paypal', TRUE, CURRENT_TIMESTAMP),
       ('bonifico_bancario', TRUE, CURRENT_TIMESTAMP);


INSERT INTO piattaforma (anno_uscita_piattaforma, codice, nome, attivo, creato_il)
VALUES (1983, 'nes', 'Nintendo Entertainment System', TRUE, CURRENT_TIMESTAMP),
       (1990, 'snes', 'Super Nintendo Entertainment System', TRUE, CURRENT_TIMESTAMP),
       (2001, 'gamecube', 'Nintendo GameCube', TRUE, CURRENT_TIMESTAMP),
       (1989, 'gb', 'Game Boy', TRUE, CURRENT_TIMESTAMP),
       (1998, 'gbc', 'Game Boy Color', TRUE, CURRENT_TIMESTAMP),
       (2001, 'gba', 'Game Boy Advance', TRUE, CURRENT_TIMESTAMP),
       (2004, 'nds', 'Nintendo DS', TRUE, CURRENT_TIMESTAMP),
       (1988, 'smd', 'Sega Mega Drive', TRUE, CURRENT_TIMESTAMP),
       (1994, 'ss', 'Sega Saturn', TRUE, CURRENT_TIMESTAMP),
       (1998, 'sd', 'Sega Dreamcast', TRUE, CURRENT_TIMESTAMP),
       (1994, 'ps1', 'PlayStation', TRUE, CURRENT_TIMESTAMP),
       (2000, 'ps2', 'PlayStation 2', TRUE, CURRENT_TIMESTAMP),
       (2004, 'psp', 'PlayStation Portable', TRUE, CURRENT_TIMESTAMP),
       (2001, 'xbox', 'Xbox', TRUE, CURRENT_TIMESTAMP),
       (1977, 'atari', 'Atari 2600', TRUE, CURRENT_TIMESTAMP);