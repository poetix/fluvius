release:
	mvn release:prepare
	mvn release:perform -Darguments="-Dmaven.deploy.skip=true"
