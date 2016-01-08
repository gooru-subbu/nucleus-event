package org.gooru.nucleus.handlers.events.constants;

public class MessageConstants {
  
  public static final String MSG_EVENT_NAME = "event.name";
  public static final String MSG_EVENT_BODY = "event.body";
    
  // Operation names: Also need to be updated in corresponding handlers
  //Content related events
  public static final String MSG_OP_EVT_RES_CREATE = "event.resource.create";
  public static final String MSG_OP_EVT_RES_UPDATE = "event.resource.update";
  public static final String MSG_OP_EVT_RES_DELETE = "event.resource.delete";
  public static final String MSG_OP_EVT_RES_COPY = "event.resource.copy";
  
  public static final String MSG_OP_EVT_QUESTION_CREATE = "event.question.create";
  public static final String MSG_OP_EVT_QUESTION_UPDATE = "event.question.update";
  public static final String MSG_OP_EVT_QUESTION_DELETE = "event.question.delete";
  public static final String MSG_OP_EVT_QUESTION_COPY = "event.question.copy";

  public static final String MSG_OP_EVT_COLLECTION_CREATE = "event.collection.create";
  public static final String MSG_OP_EVT_COLLECTION_UPDATE = "event.collection.update";
  public static final String MSG_OP_EVT_COLLECTION_DELETE = "event.collection.delete";
  public static final String MSG_OP_EVT_COLLECTION_COPY = "event.collection.copy";
  
  public static final String MSG_OP_EVT_ASSESSMENT_CREATE = "event.assessment.create";
  public static final String MSG_OP_EVT_ASSESSMENT_UPDATE = "event.assessment.update";
  public static final String MSG_OP_EVT_ASSESSMENT_DELETE = "event.assessment.delete";
  public static final String MSG_OP_EVT_ASSESSMENT_COPY = "event.assessment.copy";

  public static final String MSG_OP_EVT_USER_CREATE = "event.user.create";
  public static final String MSG_OP_EVT_USER_UPDATE = "event.user.update";
  public static final String MSG_OP_EVT_USER_DELETE = "event.user.delete";
  
  //Activity related events
  
}
