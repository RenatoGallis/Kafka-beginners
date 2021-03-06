package com.github.renatogallis.kafka.tutorial1;


import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerDemo {
	
	public static void main(String[] args) {
		String bootstrapServers = "127.0.0.1:9092";
		// Para fazer o producer temos que seguir sempre tres passos
		 // 1 - Fazer as propriedades do producer
		    Properties properties = new Properties();
		   //Configurando as propriedades do topico utilizando o ProducerConfig do pacote clients do kafka    
		    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); //endere�o do broker kafka
		    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());//Informando que o tipo de serializador � String ou seja vou mandar texto para o kafka
		    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());//Informando que o tipo de serializador � String ou seja vou mandar texto para o kafka
		    
		 // 2 - Criar o producer
		    //Crio um produtor com chave e valor sendo String e passo as configura��es acima para o mesmo
		    KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		    
		//3 - criar o producer record
		    
		 //Nesse momento estou especificando o t�pico ja criado anteriormente e falando quero que envie essa mensagem
		    ProducerRecord<String, String> record = new ProducerRecord<String, String>("frist_topic", "verificando topico " );
		    
		 // 4 - Enviar a mensagem - Assincrono
	
		    //Aqui estou utilizando o producer que configurei la em cima com as propriedades do produtor
		    //e falo envia a mensagem para o topico X por isso uso como parametro o record pois ele tem essas informa��es
		    //de topico e mensagem 
		    producer.send(record);
		   
		    //Estou terminando o consumidor coneguir receber a mensagem
		    producer.flush();
		    
		    producer.close();
		    
	}

}
