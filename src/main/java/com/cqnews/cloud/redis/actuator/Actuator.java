package com.cqnews.cloud.redis.actuator;

import java.util.List;

public interface Actuator {

    byte[] exec(List<String> commands);

}
