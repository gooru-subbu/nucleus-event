package org.gooru.nucleus.handlers.events.app.components;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.gooru.nucleus.handlers.events.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.handlers.events.bootstrap.startup.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public final class KafkaRegistry implements Initializer, Finalizer {

    private static final String DEFAULT_KAFKA_SETTINGS = "defaultKafkaSettings";
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRegistry.class);
    private Producer<String, String> kafkaProducer;

    private String KAFKA_TOPIC = "prodContentLog";
    private String CONTENT_ENRICHER_TOPIC = null; // null in case this is not needed
    private boolean testWithoutKafkaServer = false;

    private volatile boolean initialized = false;

    @Override
    public void initializeComponent(Vertx vertx, JsonObject config) {
        // Skip if we are already initialized
        LOGGER.debug("Initialization called upon.");
        if (!initialized) {
            LOGGER.debug("May have to do initialization");
            // We need to do initialization, however, we are running it via
            // verticle instance which is going to run in
            // multiple threads hence we need to be safe for this operation
            synchronized (Holder.INSTANCE) {
                LOGGER.debug("Will initialize after double checking");
                if (!initialized) {
                    LOGGER.debug("Initializing KafkaRegistry now");
                    JsonObject kafkaConfig = config.getJsonObject(DEFAULT_KAFKA_SETTINGS);
                    this.kafkaProducer = initializeKafkaPublisher(kafkaConfig);
                    initialized = true;
                    LOGGER.debug("Initializing KafkaRegistry DONE");
                }
            }
        }
    }

    private Producer<String, String> initializeKafkaPublisher(JsonObject kafkaConfig) {
        LOGGER.debug("InitializeKafkaPublisher now...");

        final Properties properties = new Properties();

        for (Map.Entry<String, Object> entry : kafkaConfig) {
            switch (entry.getKey()) {
            case ProducerConfig.BOOTSTRAP_SERVERS_CONFIG:
                properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("BOOTSTRAP_SERVERS_CONFIG : " + entry.getValue());
                break;
            case ProducerConfig.RETRIES_CONFIG:
                properties.setProperty(ProducerConfig.RETRIES_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("RETRIES_CONFIG : " + entry.getValue());
                break;
            // case ProducerConfig.ACKS_CONFIG:
            // properties.put(ProducerConfig.ACKS_CONFIG,(String)
            // entry.getValue());
            // LOGGER.debug("ACKS_CONFIG : " + entry.getValue());
            // break;
            case ProducerConfig.BATCH_SIZE_CONFIG:
                properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("BATCH_SIZE_CONFIG : " + entry.getValue());
                break;
            case ProducerConfig.LINGER_MS_CONFIG:
                properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("LINGER_MS_CONFIG : " + entry.getValue());
                break;
            case ProducerConfig.BUFFER_MEMORY_CONFIG:
                properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("BUFFER_MEMORY_CONFIG : " + entry.getValue());
                break;
            case ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG:
                properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("KEY_SERIALIZER_CLASS_CONFIG : " + entry.getValue());
                break;
            case ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG:
                properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("VALUE_SERIALIZER_CLASS_CONFIG : " + entry.getValue());
                break;
            case "topic":
                this.KAFKA_TOPIC = entry.getValue().toString();
                LOGGER.debug("KAFKA_TOPIC : " + this.KAFKA_TOPIC);
                break;
            case "contentEnricherTopic" :
                this.CONTENT_ENRICHER_TOPIC = entry.getValue().toString();
                LOGGER.debug("CONTENT_ENRICHER_TOPIC : " + this.CONTENT_ENRICHER_TOPIC);
                break;
            case ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG:
                properties.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(entry.getValue()));
                LOGGER.debug("REQUEST_TIMEOUT_MS_CONFIG : " + entry.getValue());
                break;
            case "testEnvironmentWithoutKafkaServer":
                this.testWithoutKafkaServer = (boolean) entry.getValue();
                LOGGER.debug("KAFKA_TOPIC : " + this.KAFKA_TOPIC);
                break;
            }
        }

        if (this.testWithoutKafkaServer) {
            return null;
        }

        LOGGER.debug("InitializeKafkaPublisher properties created...");
        Producer<String, String> producer = new KafkaProducer<>(properties);

        LOGGER.debug("InitializeKafkaPublisher producer created successfully!");

        return producer;
    }

    public Producer<String, String> getKafkaProducer() {
        if (initialized) {
            return this.kafkaProducer;
        }
        return null;
    }

    @Override
    public void finalizeComponent() {
        if (this.kafkaProducer != null) {
            this.kafkaProducer.close();
            this.kafkaProducer = null;
        }
    }

    public boolean testWithoutKafkaServer() {
        return this.testWithoutKafkaServer;
    }

    public String getKafkaTopic() {
        return this.KAFKA_TOPIC;
    }
    
    public String getContentEnricherTopic() {
      return this.CONTENT_ENRICHER_TOPIC;
    }

    public static KafkaRegistry getInstance() {
        return Holder.INSTANCE;
    }

    private KafkaRegistry() {
        // TODO Auto-generated constructor stub
    }

    private static final class Holder {
        private static final KafkaRegistry INSTANCE = new KafkaRegistry();
    }

}
