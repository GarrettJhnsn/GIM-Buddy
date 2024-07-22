package com.gimbuddy;

import com.gimbuddy.components.screens.Main;
import com.gimbuddy.providers.BankProvider;
import com.gimbuddy.providers.ServerProvider;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(name = "GIM Buddy")
public class GIMBuddy extends Plugin {

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private BankProvider bankProvider;

	@Inject
	private ServerProvider serverProvider;

	@Inject
	private Gson gson;

	private NavigationButton navButton;


	@Override
	protected void startUp() {
		final BufferedImage icon = ImageUtil.loadImageResource(GIMBuddy.class, "/icon.png");

		Main panel = new Main(client, itemManager, gson);

		navButton = NavigationButton.builder()
				.tooltip("GIM Buddy")
				.icon(icon)
				.priority(10)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
		panel.setBankProvider(bankProvider);
		eventBus.register(bankProvider);
	}

	@Override
	protected void shutDown() {
		clientToolbar.removeNavigation(navButton);
		eventBus.unregister(bankProvider);
	}
}
