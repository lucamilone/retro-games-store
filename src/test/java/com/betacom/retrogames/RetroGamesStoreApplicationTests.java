package com.betacom.retrogames;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.retrogames.cache.CacheInitializerTest;
import com.betacom.retrogames.cache.CacheManagerTest;
import com.betacom.retrogames.controller.CarrelloControllerTest;
import com.betacom.retrogames.controller.CarrelloRigaControllerTest;
import com.betacom.retrogames.controller.CategoriaControllerTest;
import com.betacom.retrogames.controller.CredenzialeControllerTest;
import com.betacom.retrogames.controller.PiattaformaControllerTest;
import com.betacom.retrogames.controller.ProdottoControllerTest;
import com.betacom.retrogames.controller.RuoloControllerTest;
import com.betacom.retrogames.controller.TipoMetodoPagamentoControllerTest;
import com.betacom.retrogames.service.CarrelloRigaServiceTest;
import com.betacom.retrogames.service.CarrelloServiceTest;
import com.betacom.retrogames.service.CategoriaServiceTest;
import com.betacom.retrogames.service.CredenzialeServiceTest;
import com.betacom.retrogames.service.PiattaformaServiceTest;
import com.betacom.retrogames.service.ProdottoServiceTest;
import com.betacom.retrogames.service.RuoloServiceTest;
import com.betacom.retrogames.service.TipoMetodoPagamentoServiceTest;

@Suite
@SelectClasses({ CacheInitializerTest.class, CacheManagerTest.class, CarrelloControllerTest.class,
		CarrelloServiceTest.class, CarrelloRigaControllerTest.class, CarrelloRigaServiceTest.class,
		CategoriaControllerTest.class, CategoriaServiceTest.class, CredenzialeControllerTest.class,
		CredenzialeServiceTest.class, PiattaformaControllerTest.class, PiattaformaServiceTest.class,
		ProdottoControllerTest.class, ProdottoServiceTest.class, RuoloControllerTest.class, RuoloServiceTest.class,
		TipoMetodoPagamentoControllerTest.class, TipoMetodoPagamentoServiceTest.class })
@SpringBootTest
class RetroGamesStoreApplicationTests {
	@Test
	void contextLoads() {
	}
}
