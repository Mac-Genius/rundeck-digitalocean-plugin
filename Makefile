ifndef VERBOSE
.SILENT:
endif

VERSION = 0.0.1

default: mvn

test:
	mvn clean test

mvn:
	mvn clean package