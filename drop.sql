
    set client_min_messages = WARNING;

    alter table if exists account 
       drop constraint if exists FKthdrqbmhk5dp00po3yey8uikk;

    alter table if exists carrello 
       drop constraint if exists FKqe4cig9fabcp7d4sq1th1opq8;

    alter table if exists carrello_riga 
       drop constraint if exists FK1kdw3wtgsdr5o2x1xbg7puej0;

    alter table if exists carrello_riga 
       drop constraint if exists FK9l12co6tdh4f86puoe17spyky;

    alter table if exists credenziale 
       drop constraint if exists FKsd0fonwnaypsmwkqag7m1dhvr;

    alter table if exists metodo_pagamento 
       drop constraint if exists FKyvwfci8m8kma8wxt9hmqyf85;

    alter table if exists metodo_pagamento 
       drop constraint if exists FKg7p8w0dvjjgrbe9m0xs0ghdgk;

    alter table if exists ordine 
       drop constraint if exists FKpy50oev35mxm2shcvuye9likx;

    alter table if exists ordine_riga 
       drop constraint if exists FKpplo0tcsgv8ypjgblpakdw7ej;

    alter table if exists ordine_riga 
       drop constraint if exists FKoqpbw7rwrnsgxrqeu90oo08ue;

    alter table if exists pagamento 
       drop constraint if exists FKj33asgypvarkf02119piw9o9b;

    alter table if exists pagamento 
       drop constraint if exists FK7vk3ivb6us3276tk9exydpq31;

    alter table if exists prodotto 
       drop constraint if exists FKp54y50a2i7pdiipduc60tttrw;

    alter table if exists prodotto_piattaforma 
       drop constraint if exists FKso9otr8br843nbpjk8202faay;

    alter table if exists prodotto_piattaforma 
       drop constraint if exists FKke1e38s15ou25kw28dcjl3m3t;

    drop table if exists account cascade;

    drop table if exists carrello cascade;

    drop table if exists carrello_riga cascade;

    drop table if exists categoria cascade;

    drop table if exists credenziale cascade;

    drop table if exists messaggi_sistema cascade;

    drop table if exists metodo_pagamento cascade;

    drop table if exists ordine cascade;

    drop table if exists ordine_riga cascade;

    drop table if exists pagamento cascade;

    drop table if exists piattaforma cascade;

    drop table if exists prodotto cascade;

    drop table if exists prodotto_piattaforma cascade;

    drop table if exists ruolo cascade;

    drop table if exists tipo_metodo_pagamento cascade;
