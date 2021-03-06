package com.github.renatogallis.kafka.tutorial1;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemoKeys {

	public static void main(String[] args) {

		final Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class);//Com isso eu crio um log para essa classe
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

		for(int i =0; i<5; i++) {

			String topic = "frist_topic";
			String value = "Ola mundo" + Integer.toString(i);
			String key = "key" + Integer.toString(i);
			
			//Nesse momento estou especificando o t�pico ja criado anteriormente e falando quero que envie essa mensagem
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,key,value );
			
			logger.info("Key:" + key);

			// 4 - Enviar a mensagem - Assincrono
			//Nesse cara que voce vai colocar o controle de callback do kafka clients
			producer.send(record,new Callback() {

				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
					// Isso vai executar toda vez que uma mensagem for enviada com sucesso ou se h� um erro de envio

					if(e == null) {
						//Nao deu erro:
						logger.info("Recebendo informa��es. \n"+ 
								"Topico:" + recordMetadata.topic() + "Parti��es:" + recordMetadata.partition() + "\n"+
								"Offset:" + recordMetadata.offset() +"\n"+
								"TimeStamp:" + recordMetadata.timestamp());
					}else {
						//Exibindo a exce��o do kafka clients:
						logger.error("Erro no envio:", e);
					}
				}
			});
		} //fim do for � no final da exce��o



		//Estou terminando o consumidor coneguir receber a mensagem
		producer.flush();

		producer.close();

	}

}
