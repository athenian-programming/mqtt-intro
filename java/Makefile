default: build

build:
	mvn -DskipTests=true clean package

clean:
	mvn clean

tree:
	mvn dependency:tree

jarcheck:
	mvn versions:display-dependency-updates

plugincheck:
	mvn versions:display-plugin-updates

versioncheck: jarcheck plugincheck
