tell application "GrowlHelperApp"
	-- Make a list of all the notification types 
	-- that this script will ever send:
	set the allNotificationsList to ¬
		{"BuildFinished"}
	
	-- Make a list of the notifications 
	-- that will be enabled by default.      
	-- Those not enabled by default can be enabled later 
	-- in the 'Applications' tab of the growl prefpane.
	set the enabledNotificationsList to ¬
		{"BuildFinished"}
	
	-- Register our script with growl.
	-- You can optionally (as here) set a default icon 
	-- for this script's notifications.
	register as application ¬
		"FBuild" all notifications allNotificationsList ¬
		default notifications enabledNotificationsList ¬
		icon of application "Script Editor"

end tell