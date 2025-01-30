package com.ms.eurekaserver;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
        // Teste para verificar se o contexto do Spring Boot é carregado corretamente.
    }

    @Test
    void mainMethodTest() {
        // Espiona chamadas estáticas de SpringApplication
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            
            // Simula a execução sem realmente rodar a aplicação
            mockedSpringApplication.when(() -> SpringApplication.run(any(Class.class), any(String[].class))).thenReturn(null);

            // Chama o método main
            EurekaServerApplication.main(new String[]{});

            // Verifica se o método run foi chamado corretamente
            mockedSpringApplication.verify(() -> SpringApplication.run(EurekaServerApplication.class, new String[]{}));
        }
    }
}
