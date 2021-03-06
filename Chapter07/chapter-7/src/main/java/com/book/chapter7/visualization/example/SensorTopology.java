package com.book.chapter7.visualization.example;

import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;


public class SensorTopology {
	public static void main(String args[]) throws InterruptedException {
		Config config = new Config();
		config.setNumWorkers(3);
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		
		String zkConnString = "localhost:2181";
		String topicName = "sensor-data";
		
		BrokerHosts hosts = new ZkHosts(zkConnString);
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName , "/" + topicName, UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		topologyBuilder.setSpout("spout", kafkaSpout, 1);
        topologyBuilder.setBolt("es-bolt", new ElasticSearchBolt(), 1).shuffleGrouping("spout");
        
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("storm-es-example", config, topologyBuilder.createTopology());
	}
}