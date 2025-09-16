package com.betacom.retrogames;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SelectClasses({ CacheManagerTest.class, CacheInitializerTest.class, TipoMetodoPagamentoServiceTest.class,
		TipoMetodoPagamentoControllerTest.class })
@SpringBootTest
class RetroGamesStoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
