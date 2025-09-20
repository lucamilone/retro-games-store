package com.betacom.retrogames;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.retrogames.cache.CacheInitializerTest;
import com.betacom.retrogames.cache.CacheManagerTest;
import com.betacom.retrogames.controller.CategoriaControllerTest;
import com.betacom.retrogames.controller.CredenzialeControllerTest;
import com.betacom.retrogames.controller.PiattaformaControllerTest;
import com.betacom.retrogames.controller.RuoloControllerTest;
import com.betacom.retrogames.controller.TipoMetodoPagamentoControllerTest;
import com.betacom.retrogames.service.CategoriaServiceTest;
import com.betacom.retrogames.service.CredenzialeServiceTest;
import com.betacom.retrogames.service.PiattaformaServiceTest;
import com.betacom.retrogames.service.RuoloServiceTest;
import com.betacom.retrogames.service.TipoMetodoPagamentoServiceTest;

@Suite
@SelectClasses({ CacheInitializerTest.class, CacheManagerTest.class, CategoriaControllerTest.class,
		CategoriaServiceTest.class, RuoloControllerTest.class, RuoloServiceTest.class, PiattaformaControllerTest.class,
		PiattaformaServiceTest.class, TipoMetodoPagamentoServiceTest.class, TipoMetodoPagamentoControllerTest.class,
		CredenzialeControllerTest.class, CredenzialeServiceTest.class })
@SpringBootTest
class RetroGamesStoreApplicationTests {
	@Test
	void contextLoads() {
	}
}
