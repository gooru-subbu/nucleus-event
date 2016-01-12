package org.gooru.nucleus.handlers.events.processors.repositories;

import io.vertx.core.json.JsonObject;

/**
 * Created by subbu on 07-Jan-2016.
 */
public interface UserRepo {  
  JsonObject getUser(String userID);
  JsonObject getDeletedUser(String userID);
}