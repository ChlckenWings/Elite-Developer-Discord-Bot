package com.collectivedev.bot.persist;

import com.google.common.collect.Table;

import java.util.Map;

public interface AbstractDatabase {

    Table<String, String, Character> getAllChannelIDs();

    Table<String, String, Character> getAllRoleIDs();

    Map<String, Character> getChannelsForServer(String server);

    Map<String, Character> getRolesForServer(String server);

    char getChannelType(String server, String channel);

    char getRoleType(String server, String role);
}