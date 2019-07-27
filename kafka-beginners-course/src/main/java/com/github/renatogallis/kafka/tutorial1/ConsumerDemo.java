package com.github.renatogallis.kafka.tutorial1;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemo {
	
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(ConsumerDemo.class.getName());//logar o que desejar da classe
		String bootstrapserver = "127.0.0.1:9092";
		String groupID = "my-fourth-application";
		String auto_offset_reset_config = "earliest";
		
		
		//variavel do topico:
		String topic = "frist_topic";
		
		//Criar as propriedades do consumer
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapserver);// servidor do broker kafka onde vai se conectar
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());//deserializando chave tem que ser o mesmo formato que o produtor
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());//deserializando valor tem que ser o mesmo formato que no produtor
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupID); //ID do grupo de consumidores que esse consumer faz parte (boa pratica todo consumer precisa pertencer a um consumer-group)
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, auto_offset_reset_config); //Define onde meu offset irá ler as mensagens (mais cedo mais antigo pulando alguns offsets etc)
		// Aqui termina a configuração do consumidor
		
		//Criar o consumidor
		
		KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);//Criando o consumidor com o tipo de dado String e passando as propridades acima como configuração
		consumer.subscribe(Arrays.asList(topic));// o metodo Arrays.asList da a capacidade de consumir de varios tópicos distintos
		
		
		//poll data
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
	}

}
