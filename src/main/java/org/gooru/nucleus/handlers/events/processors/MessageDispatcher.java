package org.gooru.nucleus.handlers.events.processors;

import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.producer.BufferExhaustedException;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.SerializationException;
import org.gooru.nucleus.handlers.events.app.components.KafkaRegistry;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import org.gooru.nucleus.handlers.events.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageDispatcher {
    private static final MessageDispatcher INSTANCE = new MessageDispatcher();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDispatcher.class);

    private MessageDispatcher() {
    }

    public static MessageDispatcher getInstance() {
        return INSTANCE;
    }

  public void sendMessage2Kafka(String eventName, JsonObject eventBody) {
      //
      // Kafka message publish
      //
      if (KafkaRegistry.getInstance().testWithoutKafkaServer()) {
          return; // running without KafkaServer...
      }
      Producer<String, String> producer = KafkaRegistry.getInstance().getKafkaProducer();
      ProducerRecord<String, String> kafkaMsg;
      kafkaMsg = new ProducerRecord<>(KafkaRegistry.getInstance().getKafkaTopic(), eventName, eventBody.toString());
      
      try {
          if (producer != null) {
              producer.send(kafkaMsg, (metadata, exception) -> {
                  if (exception == null) {
                      LOGGER.info("Message Delivered Successfully: Offset : " + metadata.offset() + " : Topic : "
                          + metadata.topic() + " : Partition : " + metadata.partition() + " : Message : " + kafkaMsg);
                  } else {
                      LOGGER.error(
                          "Message Could not be delivered : " + kafkaMsg + ". Cause: " + exception.getMessage());
                  }
              });
              LOGGER.debug("Message Sent Successfully: " + kafkaMsg);
          } else {
              LOGGER.error("Not able to obtain producer instance");
          }

      } catch (InterruptException | BufferExhaustedException | SerializationException ie) {
          // - If the thread is interrupted while blocked
          LOGGER.error("SendMesage2Kafka: to Kafka server:", ie);
      }
  }
    
  public void sendMessage2KafkaWatson(String eventName, JsonObject eventBody) {
    //
    // Kafka message publish
    //
    if (KafkaRegistry.getInstance().testWithoutKafkaServer()) {
        return; // running without KafkaServer...
    }
    Producer<String, String> producer = KafkaRegistry.getInstance().getKafkaProducer();
    ProducerRecord<String, String> kafkaMsg;
    kafkaMsg = new ProducerRecord<>(EventResponseConstants.WATSON_TAG_TOPIC, eventName, eventBody.toString());  
    
    try {
        if (producer != null) {
            producer.send(kafkaMsg, (metadata, exception) -> {
                if (exception == null) {
                    LOGGER.info("Message Delivered Successfully: Offset : " + metadata.offset() + " : Topic : "
                        + metadata.topic() + " : Partition : " + metadata.partition() + " : Message : " + kafkaMsg);
                } else {
                    LOGGER.error(
                        "Message Could not be delivered : " + kafkaMsg + ". Cause: " + exception.getMessage());
                }
            });
            LOGGER.debug("Message Sent Successfully: " + kafkaMsg);
        } else {
            LOGGER.error("Not able to obtain producer instance");
        }

    } catch (InterruptException | BufferExhaustedException | SerializationException ie) {
        // - If the thread is interrupted while blocked
        LOGGER.error("SendMesage2Kafka: to Kafka server:", ie);
    }
  }
    
  public boolean isWatsonTaggable(JsonObject jsonPayload){
    String contentFormat = jsonPayload.getJsonObject(EventResponseConstants.PAYLOAD_OBJECT).getString(EventResponseConstants.CONTENT_FORMAT);
    //Check to see if the Sub event is either Create or Update
    if(!isSubEventTaggable(jsonPayload)){
      return false;
    }
    try{
      switch(contentFormat){
        case "course":
        case "resource":
        case "collection":
        //case "assessment":
        //case "question":
        //case "unit":
          return true;
        default:
          return false;
      }
    }catch(Exception e){
      LOGGER.error("There was an issue while checking content format: " + e.toString());
      return false;
    }
  }
  
  public boolean isSubEventTaggable(JsonObject jsonPayload){
    String subEventName = jsonPayload.getJsonObject(EventResponseConstants.PAYLOAD_OBJECT).getString(EventResponseConstants.SUB_EVENT_NAME);
    switch(subEventName){
      case MessageConstants.MSG_OP_EVT_COURSE_CREATE:
      case MessageConstants.MSG_OP_EVT_COURSE_UPDATE:
      case MessageConstants.MSG_OP_EVT_RESOURCE_CREATE:
      case MessageConstants.MSG_OP_EVT_RESOURCE_UPDATE:
      case MessageConstants.MSG_OP_EVT_COLLECTION_CREATE:
      case MessageConstants.MSG_OP_EVT_COLLECTION_UPDATE:
        LOGGER.debug("The sub event is Watson taggable...");
        return true;
      default: 
        return false;
    }
  }

}
