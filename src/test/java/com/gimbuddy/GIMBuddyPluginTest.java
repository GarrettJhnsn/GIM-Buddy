package com.gimbuddy;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

@SuppressWarnings("unchecked")
public class GIMBuddyPluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(GIMBuddy.class);  // No warning here
		RuneLite.main(args);
	}
}
