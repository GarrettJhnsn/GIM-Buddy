package com.gimbuddy.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("gimbuddy")
public interface GIMBuddyConfig extends Config {
	@ConfigItem(
			keyName = "groupName",
			name = "Group Name",
			description = "The name of the Group Ironman group"
	)
	default String groupName() {
		return "";
	}
}
