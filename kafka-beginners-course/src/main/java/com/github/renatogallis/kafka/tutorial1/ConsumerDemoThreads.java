package com.github.renatogallis.kafka.tutorial1;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoThreads {
	
	public static void main(String[] args) {
		new ConsumerDemoThreads().run();//Acesso o mmetodo da classe pelo construtor que instanciei
		
	}
	
	//construtor da classe ConsumerDemoThread para inicializa-la
	private ConsumerDemoThreads(){}
	
	private void run() {
		
		Logger logger = LoggerFactory.getLogger(ConsumerDemoThreads.class.getName());//logar o que desejar da classe
		String bootstrapserver = "127.0.0.1:9092";
		String groupID = "my-seventh-application";
		String auto_offset_reset_config = "earliest";
		//variavel do topico:
		String topic = "frist_topic";
		//Trava para para o consumer
		 CountDownLatch trava = new CountDownLatch(1);
		
		//Criando o consumidor com a thread
		logger.info("Criando minha thread de consumer");
	Runnable myConsumerThread = new ConsumerThreads(trava,
														topic, 
														bootstrapserver, groupID, 
														auto_offset_reset_config);
		
		// come�a o fluxo da thread
		Thread myThread = new Thread(myConsumerThread);
		myThread.start();//come�a com a thread
		
		
		
		//Come�o o tratamento para fechar a aplica��o corretamente
		Runtime.getRuntime().addShutdownHook(new Thread( () ->  {
			logger.info("Desligando o consumer");
			((ConsumerThreads) myConsumerThread).shutdown();
			try {
				trava.await();
				}catch(InterruptedException e) {
					e.printStackTrace();
					logger.error("Aplica��o interrompida");
				}
			logger.info("Aplica��o terminada");
			
			
		}	
				));
		
		try {
		trava.await();
		}catch(InterruptedException e) {
			logger.error("Aplica��o interrompida" + e);
		}finally {
			//
			logger.info("for�ando o final do rol�");
		}
	}
	
	
	
	//Classe para disparar uma thread precisa implementar Runnable
	public class ConsumerThreads implements Runnable {
		
		
		private Logger logger = LoggerFactory.getLogger(ConsumerThreads.class.getName());//logar o que desejar da classe
		private CountDownLatch trava;//para fazer a contagem da trava para parar o consumo
		private KafkaConsumer<String, String> consumer;
		
		
		
		public ConsumerThreads(CountDownLatch trava, 
				String topic,
				String bootstrapserver,
				String groupID,
				String auto_offset_reset_config) {
			
			
			this.trava = trava;
			//Criar as propriedades do consumer
			Properties properties = new Properties();
			properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapserver);// servidor do broker kafka onde vai se conectar
			properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());//deserializando chave tem que ser o mesmo formato que o produtor
			properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());//deserializando valor tem que ser o mesmo formato que no produtor
			properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupID); //ID do grupo de consumidores que esse consumer faz parte (boa pratica todo consumer precisa pertencer a um consumer-group)
			properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, auto_offset_reset_config); //Define onde meu offset ir� ler as mensagens (mais cedo mais antigo pulando alguns offsets etc)
			// Aqui termina a configura��o do consumidor
			
			 consumer = new KafkaConsumer<String, String>(properties);//Criando o consumidor com o tipo de dado String e passando as propridades acima como configura��o
			 consumer.subscribe(Arrays.asList(topic));// o metodo Arrays.asList da a capacidade de consumir de varios t�picos distintos
		} 
		
		public void run() {
			//poll data temos que lan�ar uma exce��o
			try {
			
			while (true) {
				//Criando um consumer records para pegar a mensagem que esta sendo pesquisada de 100 mils em 100 mils
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				
				for(ConsumerRecord<String, String> record : records) {
					
					logger.info("Key:" + record.key() + "\n" 
							+ "Value:" + record.value() + "\n"
							+ "Partition:" + record.partition() + "\n"
							+ "Offset:" + record.offset());
				}	
			}
			}catch (WakeupException e) {
				logger.info("As mensagens foram recebidas!");
			}finally {
				//fechar o consumidor depois de ler a mensagens
				consumer.close();
				//fale para o main() que fechei o consumidor
				trava.countDown();
			}
		}
		
		public void shutdown(){
			//interromper o consumer.poll(); que � a pesquisa por mensagens
			consumer.wakeup();
			
		}
	}
}

