# DiscordMCPurchases
A simple and straightforward Spigot plugin for Minecraft 1.8.8 that allows Discord users to purchase products on the Discord store, and receive Minecraft rewards for their purchases.

Before we start, you will get errors in your console until you configure your MySQL. Please configure all that before complaining that your console is full of errors.

This plugin includes
* MySQL integration (it only works with MySQL)
* Complete Minecraft and Discord account linking
* Cached results (for faster responses and less slowing down your MySQL database)
* Custom placeholders
* Pretty much everything customizable
* Discord role rewards
    * You can customize which roles to add and remove from a Discord user on your guild
* Minecraft rewards
    * You can customize the commands ran via console upon a successful purchase
* Manual purchase checking
* Automatic purchase checking (customizable time in the config)
* Customized rewards per product
* Ability to claim multiple rewards at once

The plugin also has the functionality to provide rewards upon verification. If somebody purchases a product, but their account isn't verified yet, the plugin will recognize this and keep the rewards in a pending queue. So upon verification, the rewards will be given to the player.

If you are going to use this plugin, please ensure the SKUs for your "game" are marked as IAP or In App Purchases. This is essential for the plugin to function. You will also need to invite the application as a bot to your Discord guild with the **Send Messages**, **Read Messages**, **Read Message History**, **Manage Messages**, and **Manage Roles** permissions. Read and send is necessary to see if you've posted your verification code. **Manage Messages** is necessary since the plugin will delete all messages received in the verification channel. And lastly, **Manage Roles** is only necessary if you are going to have role rewards for any of your products.

Compared to Tebex/BuyCraft, this plugin offers a ton of advantages. Think of it this way. Discord's developer license is a $25 one-time fee. Tebex can get up to $25 a month. On Discord, you can customize exactly how you'd like everything to look, plus no transaction limit, super advanced analytics, team accounts, etc. Plus, this plugin also supports variables. The plugin even supports using Discord gift codes. In layman's terms, you will get literally everything you get with Tebex, **EXCEPT** for subscriptions. Discord does not offer that type of platform. Other than that, it's basically Tebex for Discord.



### Commands
* verify
   * This provides the player with a code to type in the verification channel
* unlink
   * This unlinks your Discord from your Minecraft account
* confirm
   * This manually asks Discord if the user running the command has purchased a product
* dmcp
   * This command gives basic information regarding the plugin
* dmcp reload
   * This will completely reload/refresh the config file and relog into the Discord bot
   

