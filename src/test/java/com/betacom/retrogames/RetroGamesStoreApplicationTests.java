package com.betacom.retrogames;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SelectClasses({ CacheInitializerTest.class, CacheManagerTest.class, RuoloControllerTest.class,
		RuoloServiceTest.class, PiattaformaControllerTest.class, PiattaformaServiceTest.class,
		CategoriaControllerTest.class, CategoriaServiceTest.class, TipoMetodoPagamentoServiceTest.class, 
		TipoMetodoPagamentoControllerTest.class, CredenzialeControllerTest.class, CredenzialeServiceTest.class})
@SpringBootTest
class RetroGamesStoreApplicationTests {
	@Test
	void contextLoads() {
	}
}
