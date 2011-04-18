tell application "GrowlHelperApp"

	--       Send a Notification...
	notify with name ¬
		"BuildFinished" title ¬
		"Build Finished" description ¬
		"Project:\t{$project}\rFile:\t\t{$file}" application name "FBuild"
		
end tell