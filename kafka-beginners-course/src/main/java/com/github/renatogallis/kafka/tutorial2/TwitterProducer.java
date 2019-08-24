package com.github.renatogallis.kafka.tutorial2;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterProducer {

	public TwitterProducer() {}
	//criando rastreio de loger na classe
		Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());
		String consumerKey="pkT8cOGsT9mxrmP3EUPNSEBl5";
		String consumerSecret="TZOrtH1aw16E6lCo5JkqBWIKfUo1EXxZ1vYExsZ8Vv7keavRDa";
		String token="792793562064748544-e3SGSMqyuXCtZKGcsNMvji2AaCA4Ekv";
		String secret="yTNktI3NXlM27ICIig9ZCjdioM4FQ9o59yLycqG0pOvTt";
		
		
	public static void main(String[] args) {
	
	new TwitterProducer().run();
		
	}
	
	public void run() {
	   
		logger.info("Inicio");
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
		//criar um twitter client
		Client client = createTwitterClient(msgQueue);
		// Attempts to establish a connection.
		client.connect();
		//criar um kafka producer
		//loop para mandar mensagens para o kafka
		while (!client.isDone()) {
			  String msg = null; 
			  try {
			  msg = msgQueue.poll(5,TimeUnit.SECONDS);
			  }catch(InterruptedException e) {
				  e.printStackTrace();
				  client.stop();
			  }
			  if(msg!= null) {
				  logger.info(msg);	  
			  }
			  
			}
		logger.info("End of application");
	}
	
	public Client createTwitterClient(BlockingQueue<String> msgQueue) {
		
		 
		
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		//List<Long> followings = Lists.newArrayList(1234L, 566788L);
		List<String> terms = Lists.newArrayList("Brasil");
		//hosebirdEndpoint.followings(followings);
		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
		
		ClientBuilder builder = new ClientBuilder()
				  .name("Hosebird-Client-01")                              // optional: mainly for the logs
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(hosebirdEndpoint)
				  .processor(new StringDelimitedProcessor(msgQueue));
				//  .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

				Client hosebirdClient = builder.build();
				return hosebirdClient;
				
	}
}
