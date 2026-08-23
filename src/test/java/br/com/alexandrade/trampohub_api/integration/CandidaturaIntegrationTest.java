package br.com.alexandrade.trampohub_api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;
import br.com.alexandrade.trampohub_api.event.CandidaturaEvent;

@SpringBootTest
public class CandidaturaIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Test
//    public void testEnviar10000MensagensEVerFilaCresce() throws InterruptedException {
//        System.out.println("\n═══════════════════════════════════════════════");
//        System.out.println("📤 ENVIANDO 10.000 MENSAGENS PARA RABBITMQ");
//        System.out.println("═══════════════════════════════════════════════");
//        System.out.println("🔗 Abra: http://localhost:15672");
//        System.out.println("⏳ Você terá 10 segundos para abrir o dashboard\n");
//
//
//        long inicio = System.currentTimeMillis();
//
//        // Envia 10.000 mensagens!
//        for (int i = 1; i <= 10000; i++) {
//            CandidaturaRequest request = new CandidaturaRequest(
//                    "6a898e11ccfabd353e9df8a3",
//                    "Teste candidatura " + i,
//                    null
//            );
//
//            CandidaturaEvent evento = new CandidaturaEvent(
//                    request,
//                    "usuario-teste-" + i,
//                    "usuario" + i + "@teste.com",
//                    "Candidato " + i
//            );
//
//            rabbitTemplate.convertAndSend(
//                    "candidatura-exchange",
//                    "candidatura.criada",
//                    evento
//            );
//
//            if (i % 1000 == 0) {
//                System.out.println("✅ " + i + " mensagens enviadas...");
//            }
//        }
//
//        long fim = System.currentTimeMillis();
//        long tempo = fim - inicio;
//
//        System.out.println("\n═══════════════════════════════════════════════");
//        System.out.println("✅ 10.000 MENSAGENS ENVIADAS!");
//        System.out.println("═══════════════════════════════════════════════");
//        System.out.println("⏱️  Tempo: " + tempo + "ms");
//        System.out.println("📊 Taxa: " + (10000000 / tempo) + " msg/segundo");
//        System.out.println("\n📊 Ready (esperando): ~10.000 mensagens na fila!");
//        System.out.println("⚙️  Com delay de 5s cada = ~13 HORAS para processar!");
//        System.out.println("\n🎯 Você vai ver um PICO GIGANTE no gráfico!");
//        System.out.println("\n⏳ Aguardando 120 segundos para você ver a orquestração...\n");
//
//        Thread.sleep(120000);  // ← 2 minutos para observar
//
//        System.out.println("✅ Teste finalizado!");
//    }
}