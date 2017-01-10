default: build

build:
	mvn -DskipTests=true clean package

tree:
	mvn dependency:tree

jarcheck:
	mvn versions:display-dependency-updates

plugincheck:
	mvn versions:display-plugin-updates

versioncheck:
	mvn versions:display-dependency-updates versions:display-plugin-updates

