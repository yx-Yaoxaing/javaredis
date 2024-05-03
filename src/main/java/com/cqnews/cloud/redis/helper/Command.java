package com.cqnews.cloud.redis.helper;

public enum Command {

    // Strings
    SET("set","string set"),
    GET("get","string get"),

    MSET("mset", "string mset"),
    MGET("mget","stringm mget");

    private final String command;
    private final String desc;

    Command(String command,String desc) {
        this.command = command;
        this.desc = desc;
    }

    public String getCommand() {
        return command;
    }

    public String getDesc() {
        return desc;
    }

    public static Command parseCommand(String command){
        if (command == null || command == "") {
            throw new IllegalArgumentException("command dont not null or empty");
        }
        Command[] values = Command.values();
        for (Command value : values) {
            if (value.getCommand().toLowerCase().equals(command.toLowerCase())) {
                return value;
            }
        }
        return null;
    }


}
